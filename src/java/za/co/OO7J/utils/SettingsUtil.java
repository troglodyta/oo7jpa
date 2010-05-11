/* SettingsUtil.java
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
package za.co.OO7J.utils;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

public class SettingsUtil {

	private static String PROPERTY_FILE_LOCATION = "oo7.properties";

	private static String DB_NAME_STR = "dbname";

	private static String OO7_DB_CONFIGURATION_STR = "oo7_db_configuration";

	private static String SMALL3_STR = "small3";
	private static String MEDIUM3_STR = "medium3";
	private static String LARGE3_STR = "large3";

	private static SettingsUtil settingsUtil = null;

	private Properties oo7Properties = null;

	public static String NumAssmPerAssmStr = "NumAssmPerAssm";

	public static String NumCompPerAssmStr = "NumCompPerAssm";

	public static String NumCompPerModuleStr = "NumCompPerModule";

	public static String NumAssmLevelsStr = "NumAssmLevels";

	public static String TotalModulesStr = "TotalModules";

	public static String NumAtomicPerCompStr = "NumAtomicPerComp";

	public static String NumConnPerAtomicStr = "NumConnPerAtomic";

	public static String DocumentSizeStr = "DocumentSize";

	public static String ManualSizeStr = "ManualSize";

	public static String CacheSizeStr = "CacheSize";

	public static String DebugModeStr = "debugMode";

	// PVZ: my addition:
	public static String InMemoryRunStr = "inMemoryRun";

	public static String UseIndexesStr = "useIndexes";

	public static String repeatCountStr = "repeatCount";

	public static String benchmarkOpStr = "benchmarkOp";

	public static String numberOfCompositeObjectsStr = "numberOfCompositeObjects";

	public static String generateDBStr = "generateDB";

	public static String manyTransactionsStr = "manyTransactions";

	public static String annotationConfigurationStr = "annotationConfiguration";

	/*
	 * VARIABLES NEEDED FOR TRAVERSALS AND QUERIES:
	 */
	public static String UpdateRepeatCntStr = "UpdateRepeatCnt";

	public static String Query1RepeatCntStr = "Query1RepeatCnt";

	public static String Query2PercentStr = "Query2Percent";

	public static String Query3PercentStr = "Query3Percent";

	public static String Query4RepeatCntStr = "Query4RepeatCnt";

	public static String NumNewCompPartsStr = "NumNewCompParts";

	// the int's:

	public static int NumAssmPerAssm = -1;

	public static int NumCompPerAssm = -1;

	public static int NumCompPerModule = -1;

	public static int NumAssmLevels = -1;

	public static int TotalModules = -1;

	public static int NumAtomicPerComp = -1;

	public static int NumConnPerAtomic = -1;

	public static int DocumentSize = -1;

	public static int ManualSize = -1;

	public static int CacheSize = -1;

	public static boolean debugMode = false;

	public static int UpdateRepeatCnt = -1;

	public static int Query1RepeatCnt = -1;

	public static int Query2Percent = -1;

	public static int Query3Percent = -1;

	public static int Query4RepeatCnt = -1;

	public static int NumNewCompParts = -1;

	public static int TotalCompParts = -1;

	public static int TotalAtomicParts = -1;

	// run parameters:
	public static int repeatCount = -1;

	public static String[] benchmarkOp = {"Trav1"};// default operation

	public static boolean inMemoryRun = false;
	public static boolean useIndexes = false;

	public static int numberOfCompositeObjects = -1;

	public static boolean generateDB = false;

	/*
	 * For each iteration we use a new transaction
	 */
	public static boolean manyTransactions = false;

	public static boolean annotationConfiguration = true;

	public static String DB_NAME = "oo7_versant";
	public static boolean SMALL3 = false;
	public static boolean MEDIUM3 = false;
	public static boolean LARGE3 = false;
	public static String OO7_DB_CONFIGURATION = "small3";

	/*
	 * This is not in the model but is needed to set the relationships between
	 * BaseAssemblies and CompositeParts: The private and shared in both
	 * directions
	 * 
	 * Original Comment from oo7: taken from GenDB.C:
	 * 
	 * data structures to keep track of which composite parts each base
	 * assemblies uses. This is done so that we can generate the base assemblies
	 * first and then the composite parts. Why bother? In particular, if you did
	 * the composite parts first and the base assemblies, there would be no need
	 * for these data structures. This second approach worked fine for the small
	 * and medium databases but not for the large as the program seeked all over
	 * the disk.
	 * 
	 * OZONE chose to create the composite parts first.
	 */
	// Private_cp: This table stores the ids of compositeParts that are randomly
	// generated
	// the compositePart objects are only added later in the CompositePart class
	private Hashtable privateCompositePartIDs = null;

	// Shared_cp:This table stores the ids of compositeParts that are randomly
	// generated
	// the compositePart objects are only added later in the CompositePart class

	private Hashtable sharedCompositePartIDs = null;

	private LinkingMap linkingMap = new LinkingMap();
	/*
	 * this code is also n
	 */
	public static int nextComplexAssemblyId = 1;

	public static int nextBaseAssemblyId = 1;

	public static int nextAtomicId = 1;

	public static int nextCompositeId = 1;

	public static int nextModuleId = 1;

	// static boolean debugMode = true;

	public SettingsUtil() {
		loadSettings();
	}

	public static SettingsUtil getInstance() {
		if (settingsUtil == null) {
			settingsUtil = new SettingsUtil();
			setValues();
			return settingsUtil;
		} else {
			return settingsUtil;
		}

	}

	private void loadSettings() {

		Properties oo7PropertiesTmp = new Properties();
		try {
                        System.out.println(PROPERTY_FILE_LOCATION);
                        System.out.println(SettingsUtil.class
					.getResourceAsStream(PROPERTY_FILE_LOCATION));
			oo7PropertiesTmp.load(SettingsUtil.class
					.getResourceAsStream(PROPERTY_FILE_LOCATION));

			System.out.println(oo7PropertiesTmp.toString());
			setOo7Properties(oo7PropertiesTmp);

		} catch (IOException io) {
			io.printStackTrace();
		}

	}

	private void loadSettings(String propertyFileLocation) {

		Properties oo7PropertiesTmp = new Properties();
		try {

			oo7PropertiesTmp.load(SettingsUtil.class
					.getResourceAsStream(propertyFileLocation));

			System.out.println(oo7PropertiesTmp.toString());
			setOo7Properties(oo7PropertiesTmp);

		} catch (IOException io) {
			io.printStackTrace();
		}

	}

	private static void setValues() {

		if (settingsUtil == null) {
			System.out.println("settingsUtil is null!!!!");
		}
		if (settingsUtil.getOo7Properties() == null) {
			System.out.println("properties is null!!!!");
		}

		String value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.NumAssmPerAssmStr);
		NumAssmPerAssm = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.NumCompPerModuleStr);
		NumCompPerModule = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.NumCompPerAssmStr);
		NumCompPerAssm = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.NumAssmLevelsStr);
		NumAssmLevels = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.TotalModulesStr);
		TotalModules = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.NumAtomicPerCompStr);
		NumAtomicPerComp = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.NumConnPerAtomicStr);
		NumConnPerAtomic = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.DocumentSizeStr);
		DocumentSize = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.ManualSizeStr);
		ManualSize = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.CacheSizeStr);
		CacheSize = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.DebugModeStr);
		debugMode = new Boolean(value).booleanValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.UpdateRepeatCntStr);
		UpdateRepeatCnt = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.Query1RepeatCntStr);
		Query1RepeatCnt = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.Query2PercentStr);
		Query2Percent = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.Query3PercentStr);
		Query3Percent = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.Query4RepeatCntStr);
		Query4RepeatCnt = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.NumNewCompPartsStr);
		NumNewCompParts = new Integer(value).intValue();

		TotalCompParts = NumCompPerModule * TotalModules;

		TotalAtomicParts = TotalCompParts * NumAtomicPerComp;
		// RUN APP PARAMETERS: START
		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.InMemoryRunStr);
		inMemoryRun = new Boolean(value).booleanValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.UseIndexesStr);
		useIndexes = new Boolean(value).booleanValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.generateDBStr);
		generateDB = new Boolean(value).booleanValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.repeatCountStr);
		repeatCount = new Integer(value).intValue();

		benchmarkOp = settingsUtil.getOo7Properties().getProperty(
				SettingsUtil.benchmarkOpStr).split(",");

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.numberOfCompositeObjectsStr);
		numberOfCompositeObjects = new Integer(value).intValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.manyTransactionsStr);
		manyTransactions = new Boolean(value).booleanValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.DB_NAME_STR);

		DB_NAME = value;

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.SMALL3_STR);

		SMALL3 = new Boolean(value).booleanValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.MEDIUM3_STR);

		MEDIUM3 = new Boolean(value).booleanValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.LARGE3_STR);

		LARGE3 = new Boolean(value).booleanValue();

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.OO7_DB_CONFIGURATION_STR);

		OO7_DB_CONFIGURATION = value;

		value = (String) settingsUtil.getOo7Properties().get(
				SettingsUtil.annotationConfigurationStr);
		annotationConfiguration = new Boolean(value).booleanValue();

		// RUN APP PARAMETERS: END

	}

	public Properties getOo7Properties() {
		return oo7Properties;
	}

	public void setOo7Properties(Properties oo7Properties) {
		this.oo7Properties = oo7Properties;
	}
/*
	public Hashtable getPrivateCompositePartIDs() {
            return linkingMap.getPrivateCompositePartIDs();
            //return privateCompositePartIDs;
	}

	public void setPrivateCompositePartIDs(Hashtable privateCompositePartIDs) {
		this.privateCompositePartIDs = privateCompositePartIDs;
	}

	public Hashtable getSharedCompositePartIDs() {
            return linkingMap.getSharedCompositePartIDs();
            //return sharedCompositePartIDs;
	}

	public void setSharedCompositePartIDs(Hashtable sharedCompositePartIDs) {
		this.sharedCompositePartIDs = sharedCompositePartIDs;
	}
*/
	/**
	 * pvz 23 Nov 2007
	 * 
	 * @return the linkingMap
	 */
	public LinkingMap getLinkingMap() {
		return linkingMap;
	}

	/**
	 * pvz 23 Nov 2007
	 * 
	 * @param linkingMap
	 *            the linkingMap to set
	 */
	public void setLinkingMap(LinkingMap linkingMap) {
		this.linkingMap = linkingMap;
	}

}
