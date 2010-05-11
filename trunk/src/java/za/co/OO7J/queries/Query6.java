/* Query6.java
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

import za.co.OO7J.Assembly;

/**
 * @author pvz 30-Apr-2006
 * 
 * Comments are taken from the Paper: The OO7 Benchmark by M .J. Carey, D. J.
 * DeWitt and J. F. Naughton
 * 
 * Query #6: find all assemblies (base or complex) B that "use" (directly or
 * transitively ) a composite part with a more recent build date than B's build
 * date.
 * 
 */
public class Query6 implements QueryInterface {

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.OO7J.queries.QueryInterface#query()
	 */
	public int query() {
		// // scan modules, visiting all of the assemblies in each one, checking
		// // to see if they use (either directly or indirectly) a more recently
		// // dated composite part; the visits are done by recursively
		// traversing
		// // the assemblies, checking them on the way back up (to avoid lots of
		// // repeated work)
		//
		// // defaults to full scan
		// LinkVstr<Module> modules = PClassObject<Module>::Object().select(
		// NULL, FALSE, NULL_PREDICATE);
		// o_4b size = modules.size();
		// o_4b count = 0;
		//
		// for ( o_word i = 0; i < size; i++) {
		// o_4b rslt = checkMaxCompDate(modules[i]->designRoot, count);
		// }
		// modules.release();
		// return count;
		return 0;
	}

	private int checkMaxCompDate(Assembly assembly, int count) {

		// // check to see if this assembly uses a more recently dated composite
		// // part, and process it if so - handling complex and base assemblies
		// // differently since the former have subassemblies while the latter
		// // directly use composite parts
		//
		// o_4b maxSubDate = -1;
		//
		// if (assembly->assmType == Complex)
		// {
		// // complex assembly case - check its subassemblies recursively
		// if (debugMode) {
		// printf("In Query6: complex assembly %d, buildDate = %d\n",
		// assembly->id, assembly->buildDate);
		// }
		//
		// ComplexAssembly* complexAssembly = (ComplexAssembly*)assembly;
		// LinkVstr<Assembly>& assemblies = complexAssembly->subAssemblies;
		// o_4b asize = assemblies.size();
		//
		// for (o_word i = 0; i < asize; i++) {
		// o_4b rsltDate;
		//
		// rsltDate = checkMaxCompDate(assemblies[i], count);
		// if (rsltDate > maxSubDate)
		// maxSubDate = rsltDate;
		// }
		// }
		// else
		// {
		// // base assembly case - check its composite part build dates
		// if (debugMode) {
		// printf("In Query6: base assembly %d, buildDate = %d\n",
		// assembly->id, assembly->buildDate);
		// printf(" composite part dates = { ");
		// }
		//
		// BaseAssembly* baseAssembly = (BaseAssembly*)assembly;
		// LinkVstr<CompositePart>& compositeParts =
		// baseAssembly->ComponentsPriv;
		// o_4b csize = compositeParts.size();
		//
		// for ( o_word i = 0; i < csize; i++) {
		// CompositePart* compositePart = compositeParts[i];
		//
		// if (debugMode) { printf("%d ",compositePart->buildDate); }
		// if (compositePart->buildDate > maxSubDate)
		// maxSubDate = compositePart->buildDate;
		// }
		// if (debugMode) { printf("}\n"); }
		// }
		//
		// // see if the maximum build date of composite parts used by this
		// assembly
		// // is greater than the assembly's build date, and process the
		// assembly if
		// // this is found to be the case; also, return the maximum build date
		// for
		// // this subassembly
		//
		// if (maxSubDate > assembly->buildDate) {
		// assembly->DoNothing();
		// count++;
		// }
		// return maxSubDate;

		return 0;
	}

}
