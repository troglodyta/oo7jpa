/* Delete.java
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
package za.co.OO7J.modifications;


import za.co.OO7J.AtomicPart;
import za.co.OO7J.BaseAssembly;
import za.co.OO7J.CompositePart;
import za.co.OO7J.Document;
import za.co.OO7J.utils.NewPersistence;
import za.co.OO7J.utils.SettingsUtil;

/**
 * @author pvz 30-Apr-2006
 * 
 * 
 * Comments are taken from the Paper: The OO7 Benchmark by M .J. Carey, D. J.
 * DeWitt and J. F. Naughton
 * 
 * Delete - Delete the five newly created composite parts( and all of their
 * associated atomic parts and document objects)
 * 
 * NOTE: All hibernate methods: find, update does not close the session in this Delete operation.
 * 
 */
public class Delete {

    public void delete() {
        try {

            // now delete the desired number of new composite parts
            for (int i = 0; i < SettingsUtil.NumNewCompParts; i++) {
                int compId = SettingsUtil.nextCompositeId++;

                if (SettingsUtil.debugMode) {
                    System.out.println("In Delete, deleting composite part with id: "
                            + compId);
                }

                // LinkVstr<CompositePart> comps =
                // PClassObject<CompositePart>::Object().select(NULL, FALSE,
                // PAttribute("CompositePart::id") == compId);

                // PVZ TODO: select composite parts with this id
                //PJWD: select ONE PART ONLY!!!
                CompositePart compPart = NewPersistence.findCompositePart(compId);

                /*
                 * TODO:NOTE PVZ: note: got an error:
                 * LazyInitializationException: could not initialize proxy - the
                 * owning Session was closed Fixed this by not closing the
                 * session IN findCompositePart BUT closing it here in Delete!
                 *
                 * Got this error when I called: doc.setPart(null);
                 *
                 * ch 19.1.4: Initializing collections and proxies
                 *
                 * These same happed for Query5
                 */

                if (compPart != null) {
                    // JGM 93/11/8 Added code to fix bidirectional links
                    // relationships for
                    // part we are about to delete.

                    //CompositePart compPart = (CompositePart) compositeParts.get(0);

                    //PJWD: initialize not used in JPA
                    //SET FETCHMODE JOIN TO EAGERLY JOIN
                    //OR TRY TO WORKAROUND WITH SIZE FOR NOW
                    //Hibernate.initialize(compPart.getUsedInPrivate());
                    //Hibernate.initialize(compPart.getUsedInShared());
                    int dummy1 = compPart.getUsedInPrivate().size();
                    int dummy2 = compPart.getUsedInShared().size();

                    // Remove link from document object
                    Document doc = compPart.getDocumentation();

                    doc.setPart(null);
                    NewPersistence.delete(doc);

                    // Remove link from atomicPart
                    //PJWD: remove circle
                    compPart.setRootPart(null);
                    int numElements = (compPart.getParts()).size();
                    for (int j = 0; j < numElements; j++) {
                        AtomicPart atomicPart = (AtomicPart) compPart.getParts().get(j);

                        atomicPart.setPartOf(null);
                        if (SettingsUtil.debugMode) {
                            System.out.println("In Delete, setting atomic part's partOf null with atmic part id: "
                                    + atomicPart.getDesignId());
                        }

                        //Persistence.saveOrUpdateDelete(atomicPart);
                        NewPersistence.delete(atomicPart);
                    }// end for j

                    // Remove link from set of ComponentsPriv links
                    // TODO:NOTE pvz:added this if NULL check. Not in the
                    // original code!!!!!!
//					Session session = null;

                    BaseAssembly baseAssembly = null;
                    if (compPart.getUsedInPrivate() != null) {
                        numElements = (compPart.getUsedInPrivate()).size();
                        Object[] usedInPriv = compPart.getUsedInPrivate().toArray();

                        for (int j = 0; j < numElements; j++) {
                            baseAssembly = (BaseAssembly) usedInPriv[j];
                            //some hibernate magic:
//							session = HibernateUtil.getCurrentSession();
//							session.lock(baseAssembly, LockMode.NONE);
                            baseAssembly.getComponentsPrivate().remove(compPart);
//							session.merge(baseAssembly);
                            NewPersistence.update(baseAssembly);


                        }
                    }

                    // Remove link from set of ComponentsShar links
                    // Remove link from set of ComponentsShar links
                    // TODO:NOTE pvz:added this if NULL check. Not in the
                    // original code!!!!!!
                    if (compPart.getUsedInShared() != null) {
                        numElements = (compPart.getUsedInShared()).size();

                        Object[] usedInShar = compPart.getUsedInShared().toArray();

                        baseAssembly = null;

                        for (int j = 0; j < numElements; j++) {
                            baseAssembly = (BaseAssembly) usedInShar[j];

//							session = HibernateUtil.getCurrentSession();
//							session.lock(baseAssembly, LockMode.NONE);

                            baseAssembly.getComponentsShared().remove(compPart);
//							session.merge(baseAssembly);
                            NewPersistence.update(baseAssembly);
                        }// end for
                    }

                    // Delete this instance of CompositePart
//					session = HibernateUtil.getCurrentSession();
//					session.lock(compPart, LockMode.NONE);
                    NewPersistence.delete(compPart);
                    if (SettingsUtil.inMemoryRun) {
                        compPart = null;
                    }
                } else {
                    System.out.println("ERROR: Unable to locate composite part with id: "
                            + compId);
                }// end if-else

            }// end for
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Persistence.closeSessionWithTransaction();
        }

    }// end delete method
}// end class

