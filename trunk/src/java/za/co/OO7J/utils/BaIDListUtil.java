/* BaIDListUtil.java
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

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import za.co.OO7J.BAidList;

/**
 *  This utility class is used to printout the contents of ba id lists
 * 
 * @author pvz
 * 23 Nov 2007
 *
 */
public class BaIDListUtil {

	private static Set<Long> set = new TreeSet<Long>();
	
	public static void printList(BAidList bAidList) {
		
		for (Iterator iter = bAidList.getBaIdList().iterator(); iter.hasNext();) {
			Long id = (Long) iter.next();
			System.out.println(" ba id: "+id);
			set.add(id);
			
		}
		
	}
	/**
	 * pvz
	 * 14 Nov 2007
	 * @return the set
	 */
	public static Set<Long> getSet() {
		return set;
	}
	/**
	 * pvz
	 * 14 Nov 2007
	 * @param set the set to set
	 */
	public static void setSet(Set<Long> set) {
		BaIDListUtil.set = set;
	}
}
