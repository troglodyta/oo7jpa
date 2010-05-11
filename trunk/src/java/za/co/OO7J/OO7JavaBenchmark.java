package za.co.OO7J;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import za.co.OO7J.modifications.Delete;
import za.co.OO7J.modifications.Insert;
import za.co.OO7J.queries.Query1;
import za.co.OO7J.queries.Query2;
import za.co.OO7J.queries.Query3;
import za.co.OO7J.queries.Query4;
import za.co.OO7J.queries.Query5;
import za.co.OO7J.queries.Query7;
import za.co.OO7J.queries.Query8;
import za.co.OO7J.queries.Query8Join;
import za.co.OO7J.traversal.Traversal;
import za.co.OO7J.utils.NewPersistence;
import za.co.OO7J.utils.RandomUtil;
import za.co.OO7J.utils.SettingsUtil;
import za.co.OO7J.utils.StatsUtil;

/**
 *         Created from Bench.C and GenDB.C
 */
public class OO7JavaBenchmark {

    public static ArrayList inMemoryModuleList = new ArrayList();
    public static String mechanismName = null;
    public static String resultFileName = null;
    public static String resultPath = null;

    public static void main(String[] args) {
        System.out.println("Running main...");
        //TODO: PJWD: provide better method of running multiple operations
        //mechanism means => currently set but never used
        //useIndexes => set but never used

        //Faking the arguments...
        //PJWD: set the first argument to the benchmarkop:
        // - "generateDB" to generate a db
        // - "inMemoryRun" to generate db inmem and execute (one or multiple) benchmarkops
        //    as defined in the settings class!
        // - Another (SINGLE!) benchmarkop to execute multiple operations on db
        //String[] args_test = {"Query1", "Hibernate_Lazy_Index_On", "test.txt"};
        String[] args_test = {};

        resultFileName = "seppe_output.txt";
        resultPath = "C:\\Users\\Admin\\Desktop\\oo7j_stats\\";

        String benchmarkOp = null;

        System.out.println("Loading settingsutil...");
        SettingsUtil settingsUtil = SettingsUtil.getInstance();

        System.out.println("Checking arguments...");
        if (args_test.length >= 1) {
            System.out.println("Operation: " + args_test[0]);
            benchmarkOp = args_test[0];
            System.out.println("Mechanism: " + args_test[1]);
            mechanismName = args_test[1];
            resultFileName = args_test[2];
        }

        if (benchmarkOp != null) {
            useRuntimeSettings(benchmarkOp);
        } else {
            usePropertyFileSettings();
        }

    }

    public static void useRuntimeSettings(String benchmarkOp) {
        System.out.println("==> useRuntimeSettings");
        if (benchmarkOp.equals("generateDB")) {
            /*
             * this is doing the work of GenDB.C (original OO7)
             */
            System.out.println("==> benchmarkOp from arguments = createDB");
            createDatabase();
        } else if (benchmarkOp.equals("inMemoryRun")) {
            createDatabase();
            System.out.println("==> inMemoryRun getting benchmarkOp from settings class");
            for (int i = 0; i < SettingsUtil.benchmarkOp.length; i++) {
                System.out.println("\n\n======= NOW STARTING OP: " + SettingsUtil.benchmarkOp[i]);
                runOperationOnExistingDB(SettingsUtil.benchmarkOp[i]);
            }
        } else {
            System.out.println("==> getting benchmarkOp from arguments");
            runOperationOnExistingDB(benchmarkOp);
        }

    }

    public static void usePropertyFileSettings() {
        System.out.println("==> usePropertyFileSettings");
        if (SettingsUtil.generateDB) {
            /*
             * this is doing the work of GenDB.C (original OO7)
             */
            System.out.println("==> Only creating DB");
            createDatabase();
        } else if (SettingsUtil.inMemoryRun) {
            System.out.println("==> Creating DB and runOperationOnExistingDB");
            createDatabase();
            for (int i = 0; i < SettingsUtil.benchmarkOp.length; i++) {
                System.out.println("\n\n======= NOW STARTING OP: " + SettingsUtil.benchmarkOp[i]);
                runOperationOnExistingDB(SettingsUtil.benchmarkOp[i]);
            }
        } else {
            System.out.println("==> Assuming existing DB and runOperationOnExistingDB");
            for (int i = 0; i < SettingsUtil.benchmarkOp.length; i++) {
                System.out.println("\n\n======= NOW STARTING OP: " + SettingsUtil.benchmarkOp[i]);
                runOperationOnExistingDB(SettingsUtil.benchmarkOp[i]);
            }
        }

    }

    public static void runOperationOnExistingDB(String benchmarkOp) {
        List times = new ArrayList();

        NewPersistence.openPersistence();

        // initialize parameters for benchmark.
        Module moduleH;
        SettingsUtil.nextAtomicId = SettingsUtil.TotalAtomicParts + 1;
        SettingsUtil.nextCompositeId = SettingsUtil.TotalCompParts + 1;

        /*
         * Compute structural info needed by the update operations, since these
         * operations need to know which id's should be used next.
         */
        int baseCnt = SettingsUtil.NumAssmPerAssm;
        int complexCnt = 1;

        for (int i = 1; i < SettingsUtil.NumAssmLevels - 1; i++) {
            baseCnt = baseCnt * SettingsUtil.NumAssmPerAssm;
            complexCnt += complexCnt * SettingsUtil.NumAssmPerAssm;
        }// end for

        SettingsUtil.nextBaseAssemblyId = SettingsUtil.TotalModules * baseCnt
                + 1;
        SettingsUtil.nextComplexAssemblyId = SettingsUtil.TotalModules
                * complexCnt + 1;
        SettingsUtil.nextAtomicId = SettingsUtil.TotalAtomicParts + 1;
        SettingsUtil.nextCompositeId = SettingsUtil.TotalCompParts + 1;

        int opIndex = 2;
        int repeatCount = 1;// default
        if (SettingsUtil.repeatCount > 1) {
            repeatCount = SettingsUtil.repeatCount;
        }

        int manyXACTS = 0;

        int prevModuleId = -1;
        int moduleId;
        Module modules = null;

        /*
         * start time, get system time in milliseconds
         */
        double startTime = System.currentTimeMillis();
        double startIterationTime = 0;
        double endIterationTime = 0;

        double coldTime = 0;

        /*
         * pvz my additions.Same as "count" variable
         */
        int numberOfObjects = 0;

        /*
         * Actually run the darn thing.
         */
        for (int iter = 0; iter < repeatCount; iter++) {
            // ////////////////////////////////////////////////////////////////
            //
            // Run an OO7 Benchmark Operation
            //
            // ////////////////////////////////////////////////////////////////
            System.out.println("RUNNING OO7 BENCHMARK OPERATION: "
                    + benchmarkOp + " iteration = " + iter);

            startIterationTime = System.currentTimeMillis();

            /*
             * pick a random module
             */
            moduleId = (RandomUtil.nextInt() % SettingsUtil.TotalModules) + 1;
            System.out.println("runOperationOnExistingDB()-> module id to use for this operation: "
                    + moduleId);

            /*
             * Start a new transaction if either this is the first iteration of
             * a multioperation transaction or we we are running each operate as
             * a separate transaction
             */

            //PJWD: added if -> makes for better code
            if ((iter == 0) || (SettingsUtil.manyTransactions)) {
                NewPersistence.startSessionWithTransaction();
            }

            if (prevModuleId != moduleId) {

                if (SettingsUtil.inMemoryRun) {
                    if (SettingsUtil.debugMode) {
                        System.out.println("OO7JavaBenchmark()-> runOperationOnExistingDB()-> inMemoryRun");
                    }
                    /*
                     * PVZ: THIS IS NEW java arrays start from index 0 so to get
                     * the first entry
                     */
                    modules = (Module) inMemoryModuleList.get(moduleId - 1);

                } else {
                    if (SettingsUtil.debugMode) {
                        System.out.println("OO7JavaBenchmark()-> runOperationOnExistingDB()-> Using persistence mechanism");
                    }

                    modules = (Module) NewPersistence.findOneModule(moduleId);

                }

                prevModuleId = moduleId;
            }

            moduleH = modules;

            if (moduleH == null) {
                System.out.println("ERROR: Unable to access Module with id: "
                        + moduleId);
                System.exit(0);
                break;
            }

            /*
             * Perform the requested operation on the chosen module
             */
            int count = 0;

            /*
             * default is not to do work
             */
            int RealWork = 0;

            if (benchmarkOp.equals("Trav1")) {

                Traversal traverse = new Traversal();

                count = traverse.traverse(moduleH, benchmarkOp);
                System.out.println("Traversal 1 DFS visited the following number of atomic parts: "
                        + count);

            } else if (benchmarkOp.equals("Trav1WW")) {
                Traversal traverse = new Traversal();

                RealWork = 1;
                benchmarkOp = "Trav1"; // so traverse methods work
                // correctly
                count = traverse.traverse(moduleH, benchmarkOp);
                benchmarkOp = "Trav1WW"; // for next (hot) iteration
                System.out.println("Traversal 1 WW DFS visited the following number of atomic parts:"
                        + count);
            } else if (benchmarkOp.equals("Trav2a")) {

                Traversal traverse = new Traversal();
                count = traverse.traverse(moduleH, benchmarkOp);
                System.out.println("Traversal 2A swapped the following number of pairs of (X,Y) coordinates."
                        + count);
            } else if (benchmarkOp.equals("Trav2b")) {

                Traversal traverse = new Traversal();
                count = traverse.traverse(moduleH, benchmarkOp);
                System.out.println("Traversal 2B swapped the following number of pairs of (X,Y) coordinates:"
                        + count);
            } else if (benchmarkOp.equals("Trav2c")) {
                Traversal traverse = new Traversal();

                count = traverse.traverse(moduleH, benchmarkOp);
                System.out.println("Traversal 2C swapped the following number of pairs of (X,Y) coordinates:"
                        + count);
            } else if (benchmarkOp.equals("Trav3a")) {
                Traversal traverse = new Traversal();

                count = traverse.traverse(moduleH, benchmarkOp);
                System.out.println("Traversal 3A toggled the following number of dates:"
                        + count);
            } else if (benchmarkOp.equals("Trav3b")) {

                Traversal traverse = new Traversal();

                count = traverse.traverse(moduleH, benchmarkOp);
                System.out.println("Traversal 3B toggled the following number of dates:"
                        + count);
            } else if (benchmarkOp.equals("Trav3c")) {

                Traversal traverse = new Traversal();
                count = traverse.traverse(moduleH, benchmarkOp);
                System.out.println("Traversal 3C toggled the following number of dates:"
                        + count);
            } else if (benchmarkOp.equals("Trav4")) {
                Traversal traverse = new Traversal();
                count = traverse.traverse(moduleH, benchmarkOp);
                System.out.println("Traversal 4: %d instances of the character found"
                        + count);
            } else if (benchmarkOp.equals("Trav5do")) {
                Traversal traverse = new Traversal();
                count = traverse.traverse(moduleH, benchmarkOp);
                System.out.println("Traversal 5(DO): %d string replacements performed"
                        + count);
            } else if (benchmarkOp.equals("Trav5undo")) {

                Traversal traverse = new Traversal();
                count = traverse.traverse(moduleH, benchmarkOp);
                System.out.println("Traversal 5(UNDO): %d string replacements performed"
                        + count);
            } else if (benchmarkOp.equals("Trav6")) {
                Traversal traverse = new Traversal();

                count = traverse.traverse(moduleH, benchmarkOp);
                System.out.println("Traversal 6: visited %d atomic part roots:"
                        + count);
            } else if (benchmarkOp.equals("Trav7")) {

                // count = traverse7();
                System.out.println("Traversal 7: found %d assemblies using rand om atomic part:"
                        + count);
            } else if (benchmarkOp.equals("Trav8")) {

                count = moduleH.scanManual();
                System.out.println("Traversal 8: found %d instances of character in manual:"
                        + count);
            } else if (benchmarkOp.equals("Trav9")) {

                count = moduleH.firstLast();
                System.out.println("Traversal 9: match was %d:" + count);
            } else if (benchmarkOp.equals("Trav10")) {

                // run traversal #1 on every module.
                count = 0;
                benchmarkOp = "Trav1"; // so object methods don't complain
                {// start block

                    Set moduleSet = (Set) new HashSet(NewPersistence.findAllModules());
                    Traversal traverse = new Traversal();
                    for (Iterator iterator = moduleSet.iterator(); iterator.hasNext();) {
                        Module currentModule = (Module) iterator.next();

                        if (currentModule == null) {
                            System.out.println("ERROR: t10 Unable to access Module with id: "
                                    + currentModule.getDesignId());
                            System.exit(0);
                        }// end if
                        count += traverse.traverse(currentModule, benchmarkOp);
                    }// end for
                    // mods.release();
                    moduleSet = null;
                }// end block
                System.out.println("Traversal 10 visited " + count
                        + " atomic parts in " + SettingsUtil.TotalModules
                        + " modules:");
                benchmarkOp = "Trav10"; // for next time around
            } else if (benchmarkOp.equals("Query1")) {
                Query1 query1 = new Query1();

                count = query1.query();

                System.out.println("Query one retrieved the following number of atomic parts:"
                        + count);
            } else if (benchmarkOp.equals("Query2")) {

                Query2 query2 = new Query2();

                count = query2.query();
                System.out.println("Query two retrieved the following number of qualifying atomic parts:"
                        + count);
            } else if (benchmarkOp.equals("Query3")) {

                Query3 query3 = new Query3();
                count = query3.query();
                System.out.println("Query three retrieved the following number of qualifying atomic parts:"
                        + count);
            } else if (benchmarkOp.equals("Query4")) {

                Query4 query4 = new Query4();
                count = query4.query();
                System.out.println("Query four retrieved the following number of (document, base assembly) pairs:"
                        + count);
            } else if (benchmarkOp.equals("Query5")) {

                Query5 query5 = new Query5();
                count = query5.query();
                System.out.println("Query five retrieved the following number of out-of-date base assemblies:"
                        + count);
            } else if (benchmarkOp.equals("Query6")) {
                // Query6 query6 = new Query6();
                // count = query6.query();

                System.out.println("Query six retrieved the following number of out-of-date assemblies:"
                        + count);
            } else if (benchmarkOp.equals("Query7")) {
                Query7 query7 = new Query7();
                count = query7.query();

                System.out.println("Query seven iterated through the following number of atomic parts:"
                        + count);
            } else if (benchmarkOp.equals("Query8")) {

                Query8 query8 = new Query8();
                count = query8.query();

                System.out.println("Query eight found the following number of atomic part/document matches:"
                        + count);
            } else if (benchmarkOp.equals("Query8Join")) {

                Query8Join query8 = new Query8Join();
                count = query8.query();

                System.out.println("Query eight using Join found the following number of atomic part/document matches:"
                        + count);
            } else if (benchmarkOp.equals("Insert")) {
                Insert insert1 = new Insert();

                insert1.insert();
                System.out.println("Inserted " + SettingsUtil.NumNewCompParts
                        + " composite parts (a total of "
                        + SettingsUtil.NumNewCompParts
                        * SettingsUtil.NumAtomicPerComp + " atomic parts.)");
                count = SettingsUtil.NumNewCompParts;
            } else if (benchmarkOp.equals("Delete")) {
                Delete delete1 = new Delete();

                delete1.delete();
                System.out.println("Deleted " + SettingsUtil.NumNewCompParts
                        + " composite parts (a total of "
                        + SettingsUtil.NumNewCompParts
                        * SettingsUtil.NumAtomicPerComp + " atomic parts.)");
                count = SettingsUtil.NumNewCompParts;

            } else if (benchmarkOp.equals("Reorg1")) {

                // count = reorg1();
                System.out.println("Reorg1 replaced %d atomic parts: " + count);
            } else if (benchmarkOp.equals("Reorg2")) {

                // count = reorg2();
                System.out.println("Reorg2 replaced %d atomic parts: " + count);
            } else if (benchmarkOp.equals("WarmUpdate")) {
                Traversal traverse = new Traversal();

                // first do the t1 traversal to warm the cache
                count = traverse.traverse(moduleH, "Trav1");
                // then call T2 to do the update
                count = traverse.traverse(moduleH, "Trav2a");
                System.out.println("Warm update swapped the following number of pairs of (X,Y) coordinates:"
                        + count);
            } else {
                // Persistence.closeSession();
                NewPersistence.commitTransaction();
                System.out.println("Sorry, that operation isn't available yet:");
                System.exit(0);
            }

            /*
             * Commit the current transaction if we are running the last
             * iteration or running a multi-transaction test
             */
            if ((iter == repeatCount - 1) || (SettingsUtil.manyTransactions)) {

                System.out.println("Commiting transaction");
                if (SettingsUtil.manyTransactions) {
                    System.out.println("Using many transactions");
                }
                NewPersistence.commitTransaction();

            }

            endIterationTime = System.currentTimeMillis() - startIterationTime;

            System.out.println("operation= " + benchmarkOp + " iteration= "
                    + iter + " and elapsedTime(in seconds)= "
                    + endIterationTime / 1000);
            times.add(new Double(endIterationTime / 1000));

            if (iter == 1) {
                /*
                 * Warm time not used
                 */
                // startWarmTime = startWallTime;
            }

            if (iter == 0) {
                /*
                 * get the cold time
                 */
                coldTime = endIterationTime;
                System.out.println("Cold time in seconds: " + coldTime / 1000);
            }

            if (iter == (repeatCount - 1)) {
                /*
                 * compute average hot time for 2nd through n-1 th iterations
                 */
                double avgHotTime = calculateAvg(times);
                System.out.println("Average hot time for 2nd through n-1 iterations: operation= "
                        + benchmarkOp
                        + " average hot elapsedTime(in seconds)= "
                        + (avgHotTime));

            }// end if
            if (iter == 0) {
                numberOfObjects = count;
            }
        }// end for

        double endTime = System.currentTimeMillis() - startTime;

        System.out.println("Total stats/time taken to run operation: "
                + endTime / 1000);

        String results = "";
        //results += "mechanism (NOT USED):" + mechanismName;
        results += "\tOO7_CONFIGURATION:" + SettingsUtil.OO7_DB_CONFIGURATION;
        //results += "\tuseIndexes (NOT USED):" + SettingsUtil.useIndexes;
        //results += "\tannotationConfiguration:" + SettingsUtil.annotationConfiguration;
        //results += "\tmany_transactions:" + SettingsUtil.manyTransactions;
        //results += "\tone_transaction:" + !SettingsUtil.manyTransactions;
        results += "\toperation: " + benchmarkOp;
        results += "\tnumberOfObjects: " + numberOfObjects;
        results += "\tcold: " + coldTime / 1000;
        results += "\tavgHotTime: " + calculateAvg(times);
        System.out.println(results);

        StatsUtil statsUtil = new StatsUtil(resultPath, resultFileName);
        statsUtil.writeToStatsFile(results);

        // pvz: modules release:
        modules = null;

        // totalTime.Stop();
        // totalSrvTime.Stop();
        // System.out.println(stdout, "Total stats (client,server):");
        // totalTime.PrintStatsHeader(stdout);
        // totalTime.PrintStats(stdout, "TotalCli");
        // totalSrvTime.PrintStats(stdout, "TotalSrv");

        NewPersistence.closeSession();
        NewPersistence.closeSessionFactory();

    }

    public static double calculateAvg(List times) {
        Object[] timesArray = times.toArray();
        double totalTime = 0;
        // start at 1 and not 0. The first time is not included
        for (int i = 1; i < timesArray.length; i++) {
            totalTime += ((Double) timesArray[i]).doubleValue();
        }

        return totalTime / (timesArray.length - 1);
    }

    public static void createDatabase() {
        // read in settings

        SettingsUtil settingsUtil = SettingsUtil.getInstance();

        /* PJWD: not used, use linkingmap instead!
         * if (settingsUtil.getPrivateCompositePartIDs() == null) {
         *    settingsUtil.setPrivateCompositePartIDs(new Hashtable());
         * }

         * if (settingsUtil.getSharedCompositePartIDs() == null) {
         *    settingsUtil.setSharedCompositePartIDs(new Hashtable());
         * }
         */

        // get starting composite partID.
        int startCompId = 1;
        int endCompId = SettingsUtil.numberOfCompositeObjects;
        int totalModules = SettingsUtil.TotalModules;

        System.out.println("Generating composite parts from " + startCompId
                + " to " + endCompId);

        // start time, get system time in milliseconds
        double startTime = System.currentTimeMillis();

        // generate the database

        int id;

        // First generate the desired number of modules
        if (startCompId == 1) {
            // first run, generate modules
            System.out.println("First run, generating modules, next id: " + SettingsUtil.nextModuleId);
            System.out.println("Total modules requested:" + totalModules);
            while (SettingsUtil.nextModuleId <= totalModules) {
                Module module = null;

                id = SettingsUtil.nextModuleId++;
                System.out.println("About to generate module with id: " + id);
                // start session and transaction
                // Hibernate: session-per-request:

                NewPersistence.startSessionWithTransaction();

                module = new Module(id);

                NewPersistence.commitTransaction();

                // pvz: I have added this to create an in memory database
                if (SettingsUtil.inMemoryRun) {
                    inMemoryModuleList.add(module);
                }
            }// end while

        }// end if startCompId == 1

        System.out.println("Made modules, now on to composite parts");

        // now create a batch of composite parts.
        SettingsUtil.nextCompositeId = startCompId;

        int numAtomicPerComp = SettingsUtil.NumAtomicPerComp;

        SettingsUtil.nextAtomicId = (startCompId - 1) * numAtomicPerComp + 1;

        //*NewPersistence.startSessionWithTransaction();
        long currentTime = System.currentTimeMillis();
        while (SettingsUtil.nextCompositeId <= endCompId) {
            CompositePart compositePart = null;
            NewPersistence.startSessionWithTransaction();
            id = SettingsUtil.nextCompositeId++;
            compositePart = new CompositePart(id);
            NewPersistence.commitTransaction();

            if ((SettingsUtil.nextCompositeId % 50) == 0) {
                System.out.println("Made composite part with id: "
                        + SettingsUtil.nextCompositeId);
                long timeTaken = (System.currentTimeMillis() - currentTime);
                currentTime = System.currentTimeMillis();
                long heapSize = Runtime.getRuntime().totalMemory();
                long heapMaxSize = Runtime.getRuntime().maxMemory();
                long heapFreeSize = Runtime.getRuntime().freeMemory();
                System.out.println("--- " + heapFreeSize + " free ("
                        + heapSize + " / " + heapMaxSize + ") @" + timeTaken + " ---");
            }

        }// end while
        //*NewPersistence.commitTransaction();

        System.out.println("Made composite parts from id: " + startCompId
                + " to id: " + endCompId);

        /*
         * Print out some useful information about what happened
         */
        System.out.println("=== DONE CREATING DATABASE, TOTALS:");
        System.out.println("Number of atomic parts: "
                + (SettingsUtil.nextAtomicId - 1));
        System.out.println("Number of composite parts: "
                + (SettingsUtil.nextCompositeId - 1));
        System.out.println("Number of complex assemblies: "
                + (SettingsUtil.nextComplexAssemblyId - 1));
        System.out.println("Number of base assemblies: "
                + (SettingsUtil.nextBaseAssemblyId - 1));
        System.out.println("Number of modules: "
                + (SettingsUtil.nextModuleId - 1));

        double endTime = System.currentTimeMillis() - startTime;

        System.out.println("Total time taken to create Database: " + endTime
                / 1000);
        System.out.println("Total Number of getPrivateCompositePartIDs().size(): "
                + settingsUtil.getLinkingMap().getPrivateCompositePartIDs().size());

        System.out.println("Total Number of getSharedCompositePartIDs: "
                + settingsUtil.getLinkingMap().getSharedCompositePartIDs().size());

        String results = "";
        results += "mechanism (NOT USED):" + mechanismName;
        results += "\tOO7_CONFIGURATION:" + SettingsUtil.OO7_DB_CONFIGURATION;
        results += "\tcreateTime: " + endTime / 1000;
        System.out.println(results);

        StatsUtil statsUtil = new StatsUtil(resultPath, resultFileName);
        statsUtil.writeToStatsFile(results);


        NewPersistence.closeSession();
        NewPersistence.closeSessionFactory();

    }
}
