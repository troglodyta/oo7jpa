/* Insert.java
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

import java.util.HashSet;

import za.co.OO7J.BaseAssembly;
import za.co.OO7J.CompositePart;
import za.co.OO7J.utils.NewPersistence;
import za.co.OO7J.utils.RandomUtil;
import za.co.OO7J.utils.SettingsUtil;

/**
 * 
 * Comments are taken from the Paper: The OO7 Benchmark by M .J. Carey, D. J.
 * DeWitt and J. F. Naughton
 * 
 * Insert - Create 5 new composite part, which includes creating a number of new
 * atomic parts(100 in the small configuration, 1000 in the large, and 5 new
 * document objects) and insert them into the database by installing references
 * to these composite parts into 10 randomly chosen base assembly objects
 * 
 * 
 * @author pvz 18-Apr-2006
 * 
 */
public class Insert {

    public void insert() {
        try {
            // now create the desired number of new composite parts, adding each
            // one as a (private) composite part that a randomly selected base
            // assembly now wishes to use

            int compositePartId;
            int assmemblyId;
            CompositePart compositePart;// compH
            BaseAssembly baseAssembly;// assmH

            if (SettingsUtil.debugMode) {
                System.out.println("In Insert, nextCompositeId = "
                        + SettingsUtil.nextCompositeId);
                System.out.println("In Insert, nextAtomicId = "
                        + SettingsUtil.nextAtomicId);
                System.out.println("In Insert, nextBaseAssemblyId ="
                        + SettingsUtil.nextBaseAssemblyId);
            }

            for (int i = 0; i < SettingsUtil.NumNewCompParts; i++) {

                // add a new composite part to the design library
                if (SettingsUtil.debugMode) {
                    System.out.println("In Insert, making composite part with id: "
                            + SettingsUtil.nextCompositeId);
                }// end if

                compositePartId = SettingsUtil.nextCompositeId++;
                compositePart = new CompositePart(compositePartId);

                // randomly select a base assembly that should use it and figure
                // out which module it resides in TODO:NOTE WHERE is this being
                // used?????!!!!!
                int baseAssembliesPerModule = (SettingsUtil.nextBaseAssemblyId - 1)
                        / SettingsUtil.TotalModules;

                assmemblyId = (RandomUtil.nextInt() % (SettingsUtil.nextBaseAssemblyId - 1)) + 1;

                // now locate the appropriate base assembly object within its
                // module

                baseAssembly = (BaseAssembly) NewPersistence.findOneBaseAssembly(assmemblyId);

                if (baseAssembly == null) {
                    System.out.println("ERROR: Can't find base assembly with id: "
                            + assmemblyId);
                    break;
                }// end if

                // finally, add the newly created composite part as a privately
                // used
                // member of the randomly selected base assembly

                // first add this assembly to the list of assemblies in which
                // this composite part is used as a private member
                if (compositePart.getUsedInPrivate() == null) {
                    compositePart.setUsedInPrivate(new HashSet());
                }// end if pvz

                compositePart.getUsedInPrivate().add(baseAssembly);

                // JGM 93/11/8 don't need to mark dirty, since compH is a new
                // object

                // then add the composite part compH to the list of private
                // parts
                // used
                // in this assembly
                if (baseAssembly.getComponentsPrivate() == null) {
                    baseAssembly.setComponentsPrivate(new HashSet());
                }// end if pvz
                baseAssembly.getComponentsPrivate().add(compositePart);


                NewPersistence.saveOrUpdate(baseAssembly);
                NewPersistence.saveOrUpdate(compositePart);

                if (SettingsUtil.debugMode) {
                    System.out.println("just made it be used by base assembly with id:"
                            + assmemblyId);
                }

            }// end for

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
