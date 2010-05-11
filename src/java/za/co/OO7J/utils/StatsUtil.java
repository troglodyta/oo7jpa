/* StatsUtil.java
 *
 * Copyright (C) 2007 Pieter van Zyl
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Used to create a file with the benchmark timings that can be used to create
 * graphs and result tables.
 * 
 * Current format of file:
 * 
 * mechanism:Hibernate ,OO7_CONFIGURATION:small3 ,useIndexes:true
 * ,many_transactions:true ,one_transaction:false ,operation: Trav1
 * ,numberOfObjects: 9860 ,cold: 56.826 ,avgHotTime: 0.02275 
 * 
 * mechanism:Hibernate
 * ,OO7_CONFIGURATION:small3 ,useIndexes:true ,many_transactions:true
 * ,one_transaction:false ,operation: Trav2a ,numberOfObjects: 493 ,cold: 66.054
 * ,avgHotTime: 1.43025 
 * 
 * mechanism:Hibernate ,OO7_CONFIGURATION:small3
 * ,useIndexes:true ,many_transactions:true ,one_transaction:false ,operation:
 * Trav2b ,numberOfObjects: 9860 ,cold: 88.796 ,avgHotTime: 17.829250000000002
 * 
 * 
 * 
 * @author pvz 9 Nov 2007
 * 
 */
public class StatsUtil {

	private String[] results = null;
	
	private String resultFileName = "hibernate_stats.txt";
	private String fileDir = "C:\\";

	public StatsUtil() {
		// TODO Auto-generated constructor stub
	}

	public StatsUtil(String[] results) {
		// TODO Auto-generated constructor stub
	}
	
	public StatsUtil(String fileName) {
		resultFileName = fileName;
	}

        //PJWD: added easier constructor
        public StatsUtil(String dirName, String fileName) {
                fileDir = dirName;
		resultFileName = fileName;
	}

	public void writeToStatsFile(String results) {

		try {
			File outputFile = new File(fileDir+resultFileName);

			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(outputFile, true)));

			out.println(results);
			System.out.println("Wrote to stats file!!!!!!!!!!");
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

}
