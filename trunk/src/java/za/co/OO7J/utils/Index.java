/* Index.java
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

/**
 * @author pvz 18-Apr-2006
 * 
 */
public class Index {

	private static Index index = null;

	/*
	 * // create all the indices needed PClassObject<AtomicPart>::Object().createindex("AtomicPart::buildDate",
	 * O_IT_BTREE); PClassObject<AtomicPart>::Object().createindex("AtomicPart::id",
	 * O_IT_HASH); PClassObject<CompositePart>::Object().createindex("CompositePart::id",
	 * O_IT_HASH); PClassObject<Document>::Object().createindex("Document::title",
	 * O_IT_HASH); PClassObject<Module>::Object().createindex("Module::id",
	 * O_IT_HASH); PClassObject<BaseAssembly>::Object().createindex("Assembly::id",
	 * O_IT_HASH);
	 */

	public Index() {

	}

	/**
	 * SINGLETON pattern
	 * 
	 * @return
	 * 18-Apr-2006
	 */
	public static Index getInstance() {
		if (index == null) {
			index = new Index();

			return index;
		} else {
			return index;
		}

	}
}
