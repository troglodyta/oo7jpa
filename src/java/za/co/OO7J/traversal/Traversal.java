/* Traversal.java
 *
 * Copyright (C) 2006 Pieter van Zyl
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */
package za.co.OO7J.traversal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import za.co.OO7J.Assembly;
import za.co.OO7J.AtomicPart;
import za.co.OO7J.BaseAssembly;
import za.co.OO7J.ComplexAssembly;
import za.co.OO7J.CompositePart;
import za.co.OO7J.Connection;
import za.co.OO7J.Module;
import za.co.OO7J.utils.NewPersistence;
import za.co.OO7J.utils.SettingsUtil;

/**
 * @author pvz 18-Apr-2006
 * 
 * Original comment from OO7 creators: "Traverse - DFS traverse module. Upon
 * reaching a base assembly, visit all referenced composite parts. At each
 * composite part, take an action that depends on which traversal variant has
 * been requested."
 * 
 * dfs= depth first search
 * 
 * based on Traverse.C
 * 
 */
public class Traversal {

    // traversal types:
    private static String Trav1 = "Trav1";
    private static String Trav1WW = "Trav1WW";
    private static String Trav2a = "Trav2a";
    private static String Trav2b = "Trav2b";
    private static String Trav2c = "Trav2c";
    private static String Trav3a = "Trav3a";
    private static String Trav3b = "Trav3b";
    private static String Trav3c = "Trav3c";
    private static String Trav4 = "Trav4";
    private static String Trav6 = "Trav6";
    private static String Trav5do = "Trav5do";
    private static String Trav5undo = "Trav5undo";
    // pvz: CHECKPOINTS FOR DEBUGGING
    public static int num_of_assemblies = 0;
    public static int num_of_sub_assemblies = 0;
    public static int num_of_parts = 0;
    public static int num_of_atomic_parts = 0;
    public static int num_of_atomic_parts_x = 0;

    public int traverse(Module module, String typeOfSearch) {

        if (SettingsUtil.debugMode) {
            System.out.println("Traversing Module with id = "
                    + module.getDesignId() + "op = " + typeOfSearch);
        }

        if (SettingsUtil.debugMode) {
            System.out.println("Traversal.traverse() Module. CHECKPOINT. Num of  assemblies:  "
                    + module.getAssemblies().size());
        }

        int count = traverse(module.getDesignRoot(), typeOfSearch);
        return count;
    }

    public int traverse(ComplexAssembly complexAssembly, String typeOfSearch) {

        if (SettingsUtil.debugMode) {
            System.out.println("Traversing ComplexAssembly with id = "
                    + complexAssembly.getDesignId() + "op = " + typeOfSearch);
        }
        int count = 0;
        // traverse each of the assembly's subassemblies
        // complexAssembly.getSubAssemblies().iterator();
        if (complexAssembly.getSubAssemblies() != null) {
            if (SettingsUtil.debugMode) {
                num_of_sub_assemblies += complexAssembly.getSubAssemblies().size();
                System.out.println("Traversal.traverse() ComplexAssembly. CHECKPOINT. Num of sub assemblies:  "
                        + num_of_sub_assemblies);
            }

            for (Iterator iter = complexAssembly.getSubAssemblies().iterator(); iter.hasNext();) {
                Assembly assembly = (Assembly) iter.next();
                if (assembly instanceof ComplexAssembly) {
                    ComplexAssembly currentComplexAssembly = (ComplexAssembly) assembly;
                    count += traverse(currentComplexAssembly, typeOfSearch);
                } else {
                    // note: I had a class cast exception here using ont 2 many
                    // uni, with join table ch 7.3.1 in Hibernate docs.
                    BaseAssembly currentBaseAssembly = (BaseAssembly) assembly;
                    count += traverse(currentBaseAssembly, typeOfSearch);
                }// end if

            }// end for
        }// end if complexAssembly.getSubAssemblies() != null
        return count;
    }

    public int traverse(BaseAssembly baseAssembly, String typeOfSearch) {

        if (SettingsUtil.debugMode) {
            System.out.println("Traversing BaseAssembly with id = "
                    + baseAssembly.getDesignId() + "op = " + typeOfSearch);
        }
        int count = 0;
        // establish iterator of private composite parts
        // TODO:NOTE: Ozone uses the shared components! why?
        if (baseAssembly.getComponentsPrivate() != null) {
            for (Iterator iter = baseAssembly.getComponentsPrivate().iterator(); iter.hasNext();) {
                CompositePart compositePart = (CompositePart) iter.next();
                count += traverse(compositePart, typeOfSearch);
            }// end for
        }// end ifcomplexAssembly.getComponentsPrivate()
        return count;
    }

    /**
     *
     * OZONE, does not have the extra if's
     *
     * traversal + dfs method in OZONE
     *
     * @param compositePart
     * @param typeOfSearch
     * @return 18-Apr-2006
     */
    public int traverse(CompositePart compositePart, String typeOfSearch) {
        if (SettingsUtil.debugMode) {
            System.out.println("Traversing CompositePart with id = "
                    + compositePart.getDesignId() + "op = " + typeOfSearch);
        }

        if (SettingsUtil.debugMode) {
            num_of_parts += compositePart.getParts().size();
            System.out.println("Traversal.traverse() CompositePart. CHECKPOINT. Num of  parts:  "
                    + num_of_parts);
        }

        if (typeOfSearch.equals(Trav1) || typeOfSearch.equals(Trav1WW)
                || typeOfSearch.equals(Trav2a) || typeOfSearch.equals(Trav2b)
                || typeOfSearch.equals(Trav2c) || typeOfSearch.equals(Trav3a)
                || typeOfSearch.equals(Trav3b) || typeOfSearch.equals(Trav3c)) {

            // TODO:NOTE at this point in the original version they set some
            // read
            // and
            // write
            // locks on the database depending on the type of traversal
            // DO I NEED THIS?

            // do a DFS(DEPTH FIRST SEARCH) of the composite part's atomic part
            // graph

            // OZONE, does not have the extra if's
            Set visitedIDs = new HashSet();

            {
//				Session session = null;
//				session = HibernateUtil.getCurrentSession();
//				session.lock(compositePart, LockMode.NONE);
            }

            return traverse(compositePart.getRootPart(), typeOfSearch,
                    visitedIDs);

        } else if (typeOfSearch.equals(Traversal.Trav4)) {
            // search document text for a certain character
            return compositePart.getDocumentation().searchText("I");
        } else if (typeOfSearch.equals(Traversal.Trav5do)) {
            // conditionally change initial part of document text
            // TODO:NOTE pvz: not sure if I need to save this change. Where do
            // thy do it in the Versant version?
            int success = compositePart.getDocumentation().replaceText("I am",
                    "This is");
            NewPersistence.saveOrUpdate(compositePart.getDocumentation());
            return success;
        } else if (typeOfSearch.equals(Traversal.Trav5undo)) {
            // conditionally change back initial part of document text
            int success = compositePart.getDocumentation().replaceText(
                    "This is", "I am");
            NewPersistence.saveOrUpdate(compositePart.getDocumentation());
            return success;
        } else if (typeOfSearch.equals(Traversal.Trav6)) {
            // visit the root part only (it knows how to handle this)
            Set visitedIDs = new HashSet();

            return traverse(compositePart.getRootPart(), typeOfSearch,
                    visitedIDs);
        } else {
            // composite parts don't respond to other traversals
            System.out.println("*** CompositePart::PANIC -- illegal traversal!!!");
            return 0;
        }

    }

    /**
     *
     * This is a depth first search through the atomicParts (dfs methhod in
     * Ozone) OZONE, does not have the extra if's
     *
     * dfs method in OZONE
     *
     * @param atomicPart
     * @param typeOfSearch
     * @param visitedIDs
     * @return 18-Apr-2006
     */
    public int traverse(AtomicPart atomicPart, String typeOfSearch,
            Set visitedIDs) {
        if (SettingsUtil.debugMode) {

            System.out.println("Traversing AtomicPart with id = "
                    + atomicPart.getDesignId() + "op = " + typeOfSearch);
        }

        int count = 0; // was 1 in Version 1. Why???

        if (Traversal.Trav1.equals(typeOfSearch)) {

            // just examine the part
            count += 1;
            // DoNothing();
        } else if (Traversal.Trav2a.equals(typeOfSearch)) {

            // swap X and Y if first part
            if (visitedIDs.isEmpty()) {
                atomicPart.swapXY();
                NewPersistence.update(atomicPart);
                count += 1;
            }
        } else if (Traversal.Trav2b.equals(typeOfSearch)) {

            // swap X and Y
            atomicPart.swapXY();
            NewPersistence.saveOrUpdate(atomicPart);
            count += 1;
        } else if (Traversal.Trav2c.equals(typeOfSearch)) {

            // swap X and Y repeatedly
            for (int i = 0; i < SettingsUtil.UpdateRepeatCnt; i++) {
                atomicPart.swapXY();
                // TODO:NOTE pvz: do I need to save this change back to the db
                // each
                // time? if so move this line into/or out the for loop
                NewPersistence.saveOrUpdate(atomicPart);
                count += 1;
            }// end for

        } else if (Traversal.Trav3a.equals(typeOfSearch)) {

            // toggle date if first part
            if (visitedIDs.isEmpty()) {
                atomicPart.toggleDate();
                NewPersistence.saveOrUpdate(atomicPart);
                count += 1;
            }
        } else if (Traversal.Trav3b.equals(typeOfSearch)) {
            // toggle date
            atomicPart.toggleDate();
            NewPersistence.saveOrUpdate(atomicPart);
            count += 1;
        } else if (Traversal.Trav3c.equals(typeOfSearch)) {

            // toggle date repeatedly
            for (int i = 0; i < SettingsUtil.UpdateRepeatCnt; i++) {
                atomicPart.toggleDate();
                // TODO:NOTE pvz: do I need to save this change back to the db
                // each
                // time? if so move this line into/or out the for loop
                NewPersistence.saveOrUpdate(atomicPart);
                count += 1;
            }
        } else if (Traversal.Trav6.equals(typeOfSearch)) {
            // examine only the root part
            count += 1;
            // DoNothing();
            return count;

        } else {
            // atomic parts don't respond to other traversals
            System.out.println("*** AtomicPart()->PANIC -- illegal traversal!!!");

        }
        // now, record the fact that we've visited this part
        visitedIDs.add(new Integer(atomicPart.getDesignId()));

        // finally, continue with a DFS of the atomic parts graph
        // establish iterator over connected parts
        if (atomicPart.getToConnections() != null) {
            for (Iterator iter = atomicPart.getToConnections().iterator(); iter.hasNext();) {
                Connection connection = (Connection) iter.next();
                AtomicPart nextAtomicPart = connection.getTo();
                if (SettingsUtil.debugMode) {
                    System.out.println("next atomic part to visit: "
                            + nextAtomicPart.getDesignId());
                }

                if (!visitedIDs.contains(new Integer(nextAtomicPart.getDesignId()))) {
                    count += traverse(nextAtomicPart, typeOfSearch, visitedIDs);
                }
            }// end for

        }// end if

        return count;
    }// end traverse atomic part
}
