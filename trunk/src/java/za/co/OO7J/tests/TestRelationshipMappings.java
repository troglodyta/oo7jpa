/* TestRelationshipMappings.java
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
package za.co.OO7J.tests;

import java.util.Iterator;
import java.util.List;

import za.co.OO7J.AtomicPart;
import za.co.OO7J.BaseAssembly;
import za.co.OO7J.Module;
import za.co.OO7J.utils.NewPersistence;

public class TestRelationshipMappings {

	public static void testDesignObjectProperties() {
                NewPersistence.startSessionWithTransaction();

		Module module = NewPersistence.findOneModule(1);

		System.out.println("module: " + module.getDesignId() + " build date: "
				+ module.getBuildDate() + " type: " + module.getType());
		BaseAssembly baseAssembly = NewPersistence.findOneBaseAssembly(709);

		System.out.println("baseAssembly: " + baseAssembly.getDesignId()
				+ " build date: " + baseAssembly.getBuildDate() + " type: "
				+ baseAssembly.getType());

		System.out.println("components priv size: "
				+ baseAssembly.getComponentsPrivate().size()
				+ " components shared size: "
				+ baseAssembly.getComponentsShared().size());

		AtomicPart atomicPart = NewPersistence.findAtomicPart(1);

		System.out.println("atomicPart: " + atomicPart.getDesignId()
				+ " build date: " + atomicPart.getBuildDate() + " type: "
				+ atomicPart.getType());

		List all = NewPersistence.findAllBaseAssemblies();
		int count = 0;
		for (Iterator iter = all.iterator(); iter.hasNext();) {
			baseAssembly = (BaseAssembly) iter.next();
			System.out.println("id: " + baseAssembly.getDesignId()
					+ " components priv size: "
					+ baseAssembly.getComponentsPrivate().size()
					+ " components shared size: "
					+ baseAssembly.getComponentsShared().size());
			if ((baseAssembly.getComponentsPrivate().size() + baseAssembly
					.getComponentsShared().size()) > 1) {
				count++;
				System.out.println(" count: " + count);
			}

		}

                NewPersistence.closeSession();

	}

	public static void main(String args[]) {
            NewPersistence.openPersistence();
            testDesignObjectProperties();
	}

}
