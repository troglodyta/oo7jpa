/* LinkingMap.java
 *
 * Copyright (C) 2009 Pieter van Zyl
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

import java.util.Hashtable;

import za.co.OO7J.BAidList;


public class LinkingMap {

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
	 * 
	 */
	// Private_cp: This table stores the ids of compositeParts that are randomly
	// generated
	// the compositePart objects are only added later in the CompositePart class
	private Hashtable<Long, BAidList> privateCompositePartIDs = new Hashtable<Long, BAidList>();

	// Shared_cp:This table stores the ids of compositeParts that are randomly
	// generated
	// the compositePart objects are only added later in the CompositePart class

	private Hashtable<Long, BAidList> sharedCompositePartIDs = new Hashtable<Long, BAidList>();

	/**
	 * pvz 9 Nov 2007
	 * 
	 * @return the privateCompositePartIDs
	 */
	public Hashtable<Long, BAidList> getPrivateCompositePartIDs() {
		return privateCompositePartIDs;
	}

	/**
	 * pvz 9 Nov 2007
	 * 
	 * @param privateCompositePartIDs
	 *            the privateCompositePartIDs to set
	 */
	public void setPrivateCompositePartIDs(
			Hashtable<Long, BAidList> privateCompositePartIDs) {
		this.privateCompositePartIDs = privateCompositePartIDs;
	}

	/**
	 * pvz 9 Nov 2007
	 * 
	 * @return the sharedCompositePartIDs
	 */
	public Hashtable<Long, BAidList> getSharedCompositePartIDs() {
		return sharedCompositePartIDs;
	}

	/**
	 * pvz 9 Nov 2007
	 * 
	 * @param sharedCompositePartIDs
	 *            the sharedCompositePartIDs to set
	 */
	public void setSharedCompositePartIDs(
			Hashtable<Long, BAidList> sharedCompositePartIDs) {
		this.sharedCompositePartIDs = sharedCompositePartIDs;
	}

}
