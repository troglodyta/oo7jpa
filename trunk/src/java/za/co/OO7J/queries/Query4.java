/* Query4.java
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
package za.co.OO7J.queries;

import java.util.Set;

import za.co.OO7J.BaseAssembly;
import za.co.OO7J.Document;
import za.co.OO7J.utils.GeneralParameters;
import za.co.OO7J.utils.NewPersistence;
import za.co.OO7J.utils.RandomUtil;
import za.co.OO7J.utils.SettingsUtil;

/**
 * @author pvz 30-Apr-2006
 * 
 *         Comments are taken from the Paper: The OO7 Benchmark by M .J. Carey,
 *         D. J. DeWitt and J. F. Naughton
 * 
 *         Query #4: Generate 100 random document titles. For each title
 *         generated, find all the base assemblies that use the composite part
 *         corresponding to the document. Also, count the total number of base
 *         assemblies that qualify. -->randomly choose "Query4RepeatCnt"
 *         document objects by lookup on their title field. An index can be used
 *         for the actual lookup.
 * 
 */
public class Query4 implements QueryInterface {

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see za.co.OO7J.queries.QueryInterface#query()
	 */
	public int query() {

		/*
		 * now randomly select a document via its title index and trace a path
		 * to the base assemblies that use its associated composite part (and
		 * then repeat this process the desired number of times)
		 * 
		 * "set random seed so "hot" runs are truly hot srandom(1);" not used in
		 * our Java Version
		 */

		int count = 0;
		String title = "";

		for (int i = 0; i < SettingsUtil.Query4RepeatCnt; i++) {
			// generate random document title and lookup document
			int compPartId = (RandomUtil.nextPositiveInt() % SettingsUtil.TotalCompParts) + 1;
			title = GeneralParameters.documentText + " " + compPartId;
			if (SettingsUtil.debugMode) {
				System.out.println("Composite Part id =" + compPartId
						+ " with title: " + title);
			}

			Document document;

                        //PJWD: fixed, only returns one match
			document = NewPersistence.findDocumentWithTitle(title);

			if (document == null) {
				System.out.println("ERROR: Unable to find document called: "
						+ title);
				return count;
			}

			if (SettingsUtil.debugMode) {
				System.out.println("In Query4, document title = " + title);
			}

			/*
			 * now walk back up the path(s) to the associated base assemblies
			 * (based on private uses of the composite part, at least for now)
			 */
			Set bases;// set of BaseAssembly objects

			if ((document.getPart() != null)
					&& (document.getPart().getUsedInPrivate() != null)) {
				bases = document.getPart().getUsedInPrivate();
				int size = bases.size();
				Object[] basesArray = bases.toArray();

				/*
				 * establish baseassembly iterator
				 */
				for (int j = 0; j < size; j++) {
					BaseAssembly baseAssembly = (BaseAssembly) basesArray[j];
					baseAssembly.doNothing();
					count++;
				}// end for
			} else {
				System.out.println("In Query4, document with id: "
						+ document.getDocId()
						+ " has no parts or used in private parts!!!");
			}

		}// end for
		return count;
	}
}
