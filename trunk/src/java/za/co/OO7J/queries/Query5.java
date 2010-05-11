/* Query5.java
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

import java.util.List;
import java.util.Set;

import za.co.OO7J.BaseAssembly;
import za.co.OO7J.CompositePart;
import za.co.OO7J.Module;
import za.co.OO7J.utils.NewPersistence;
import za.co.OO7J.utils.SettingsUtil;

/**
 * @author pvz 30-Apr-2006
 * 
 *         Comments are taken from the Paper: The OO7 Benchmark by M .J. Carey,
 *         D. J. DeWitt and J. F. Naughton
 * 
 *         Query #5: Find all base assemblies that use a composite part with a
 *         build date later than the build date of the base assembly. Also,
 *         report the number of qualifying base assemblies found.
 * 
 */
public class Query5 implements QueryInterface {

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.OO7J.queries.QueryInterface#query()
	 */
	public int query() {
		/*
		 * scan all modules, scanning all of the base assemblies in each one,
		 * scanning all of the composite parts that they use, looking for base
		 * assemblies that use more recently dated composite parts
		 */

		int count = 0;
		List modules = null;

		/*
		 * TODO:NOTE PVZ: note: got an error: LazyInitializationException: could
		 * not initialize proxy - the owning Session was closed Fixed this by
		 * not closing the session IN findAllModules BUT closing it here in
		 * Query 5!
		 * 
		 * Got this error when I called: baseAssembly.getDesignId()
		 * 
		 * ch 19.1.4: Initializing collections and proxies
		 */

		modules = (List) NewPersistence.findAllModules();

		if (modules.isEmpty()) {
			System.out.println("No Module found");
			return 0;
		}

		int size = modules.size();

		for (int i = 0; i < size; i++) {
			Module module = (Module) modules.get(i);

			/*
			 * for each module iterate over its base assemblies this assumes
			 * that we maintain a collection of base asemblies in each module
			 */

			Set baseAssemblies = module.getAssemblies(); // set of Assembly
			// objects
			int bsize = baseAssemblies.size();

			Object[] baseAssembliesArray = baseAssemblies.toArray();

			for (int j = 0; j < bsize; j++) {
				BaseAssembly baseAssembly = (BaseAssembly) baseAssembliesArray[j];

				if (SettingsUtil.debugMode) {
					System.out
							.println("In Query5, processing base assembly with id:"
									+ baseAssembly.getDesignId()
									+ "and buildDate = "
									+ baseAssembly.getBuildDate());
				}// end if debug mode

				Set compositePartsSet = baseAssembly.getComponentsPrivate();
				if (compositePartsSet == null) {
					System.out
							.println("Possible error as base assembly has no private components!");
					continue;
				}
				int csize = compositePartsSet.size();
				Object[] compositeParts = compositePartsSet.toArray();

				for (int k = 0; k < csize; k++) {
					CompositePart compositePart = (CompositePart) compositeParts[k];

					if (SettingsUtil.debugMode) {
						System.out.println("[Checking composite part with id :"
								+ compositePart.getDesignId()
								+ "and  buildDate = "
								+ compositePart.getBuildDate());
					}// end if debug
					if (compositePart.getBuildDate() > baseAssembly
							.getBuildDate()) {
						baseAssembly.doNothing();
						count++;
					}// end if
				}// end for k
			}// end for j
		}// end for i

		return count;
	}
}
