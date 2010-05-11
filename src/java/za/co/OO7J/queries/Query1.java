/* Query1.java
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

import za.co.OO7J.AtomicPart;
import za.co.OO7J.utils.NewPersistence;
import za.co.OO7J.utils.RandomUtil;
import za.co.OO7J.utils.SettingsUtil;

/**
 * @author pvz 18-Apr-2006
 * 
 *         Comments are taken from the Paper: The OO7 Benchmark by M .J. Carey,
 *         D. J. DeWitt and J. F. Naughton
 * 
 *         Query #1 - Generate 10 random atomic part id's. For each part id
 *         generated lookup the atomic part with that id. Return the number of
 *         atomic parts processed
 * 
 *         OZONE implements only this method. They call it: matchQuery()
 * 
 */
public class Query1 implements QueryInterface {

	public int query() {

		int partId;

		/*
		 * now randomly select parts via partId index and process them
		 */
		for (int i = 0; i < SettingsUtil.Query1RepeatCnt; i++) {
			AtomicPart atomicPart = null;

			/*
			 * generate part id and lookup part
			 */
			partId = (RandomUtil.nextInt() % SettingsUtil.TotalAtomicParts) + 1;

			/*
			 * select the AtomicPart with this id
			 */
			atomicPart = (AtomicPart) NewPersistence.findAtomicPart(partId);

			if (atomicPart == null) {
				System.out.println("ERROR: Unable to find atomic part with id:"
						+ partId);

			}
			if (SettingsUtil.debugMode) {
				System.out.println("In Query1, partId = " + partId);
			}
			// process part by calling the null procedure
			// atomicPart->DoNothing();
		}
		return SettingsUtil.Query1RepeatCnt;

	}

}
