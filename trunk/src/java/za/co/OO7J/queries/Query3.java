/* Query3.java
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

import java.util.Iterator;
import java.util.List;

import za.co.OO7J.AtomicPart;
import za.co.OO7J.utils.GeneralParameters;
import za.co.OO7J.utils.NewPersistence;
import za.co.OO7J.utils.SettingsUtil;

/**
 * @author pvz 30-Apr-2006
 * 
 *         Comments are taken from the Paper: The OO7 Benchmark by M .J. Carey,
 *         D. J. DeWitt and J. F. Naughton
 * 
 *         Query #3: Choose a range for dates that will contain the last 10% of
 *         the dates found in the database's atomic parts. Retrieve the atomic
 *         parts that will satisfy this range predicate
 * 
 */
public class Query3 implements QueryInterface {

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see za.co.OO7J.queries.QueryInterface#query()
	 */
	public int query() {

		/*
		 * choose a range of dates with the appropriate selectivity
		 */
		int dateRange = (SettingsUtil.Query3Percent * (GeneralParameters.maxAtomicDate - GeneralParameters.minAtomicDate)) / 100;
		int lowerDate = GeneralParameters.maxAtomicDate - dateRange;

		if (SettingsUtil.debugMode) {
			System.out.println("Running Query3 with dateRange = " + dateRange
					+ " and lowerDate = " + lowerDate);
		}

		AtomicPart part;
		int count = 0;

		List atomicPartsResults = null;

		atomicPartsResults = NewPersistence.findAtomicPartsInBuildDateRange(
				lowerDate, GeneralParameters.maxAtomicDate);

		for (Iterator iter = atomicPartsResults.iterator(); iter.hasNext();) {
			part = (AtomicPart) iter.next();

			if (SettingsUtil.debugMode) {
				System.out.println("In Query3, partId = " + part.getDesignId()
						+ "and  buildDate = " + part.getBuildDate());
			}

			/*
			 * process qualifying parts by calling the null procedure
			 */
			if (part.getBuildDate() >= lowerDate) {
				part.doNothing();
				count++;
			} else // shouldn't happen
			{
				System.out.println("error:In Query3, partId = "
						+ part.getDesignId() + "and  buildDate = "
						+ part.getBuildDate());
			}
		}
		return count;

	}
}
