/* Query8.java
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
import za.co.OO7J.Document;
import za.co.OO7J.utils.NewPersistence;
import za.co.OO7J.utils.SettingsUtil;
import za.co.OO7J.utils.Support;

/**
 * @author pvz 30-Apr-2006
 *
 * Comments are taken from the Paper: The OO7 Benchmark by M .J. Carey, D. J.
 * DeWitt and J. F. Naughton
 *
 * Query #8: Find all the pairs of documents and atomic parts where the document
 * id in the atomic part matches the id of the document. Also, return a count of
 * the number of such pairs encountered
 *
 */
public class Query8Join implements QueryInterface {

    /*
     * (non-Javadoc)
     *
     * @see za.co.OO7J.queries.QueryInterface#query()
     *
     * pvz: This method has changed from the original in that it is not using
     * The document Index's
     */
    public int query() {

        AtomicPart part;
        int count = 0;
        int start = 0;
        int chunkSize = 2000;
        // TODO: Return all atomic parts
        List atomicPartsAndAssociatedDocs = null;

        atomicPartsAndAssociatedDocs = NewPersistence.findAtomicPartsAndAssociatedDocsPaged(start, chunkSize);
        if (SettingsUtil.debugMode) {
            // new debug by pvz:
            System.out.println("Size of result set: "
                    + atomicPartsAndAssociatedDocs.size());
        }

        while (atomicPartsAndAssociatedDocs.size() > 0) {
            if (SettingsUtil.debugMode) {
                System.out.println("Query8Join at chunk: "+start);
            }
            NewPersistence.getSession().clear();
            //NewPersistence.getSession().getEntityManagerFactory().getCache().evictAll();
            Document document = null;

            for (Iterator iter = atomicPartsAndAssociatedDocs.iterator(); iter.hasNext();) {
                Object[] tuple = (Object[]) iter.next();
                document = (Document) tuple[0];
                part = (AtomicPart) tuple[1];

                if (document != null) {
                    // found a matching pair of tuples
                    if (SettingsUtil.debugMode) {
                        System.out.println("In Query8, atomic part id:"
                                + part.getDesignId() + " matches doc id"
                                + document.getDocId());
                    }
                    Support.joinDoNothing(part.getDesignId(), document.getDocId());
                    count++;
                }
            }// end for
            start+=chunkSize;
            atomicPartsAndAssociatedDocs = NewPersistence.findAtomicPartsAndAssociatedDocsPaged(start, chunkSize);
        }//end while
        return count;
    }
}
