/**
 * Injector Commons Services bundle
 * Injector Menu Roots Tree Registry iPojo impl test
 * Copyright (C) 2014 Mathilde Ffrench
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.spectral.cc.core.injector.commons.registry;

import com.spectral.cc.core.injector.commons.registry.iPojo.InjectorTreeMenuRootsRegistryImpl;
import com.spectral.cc.core.portal.commons.model.MenuEntityType;
import com.spectral.cc.core.portal.commons.model.TreeMenuEntity;
import com.spectral.cc.core.portal.commons.registry.TreeMenuRootsRegistry;
import junit.framework.TestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class InjectorMenuRootsTreeRegistryTest extends TestCase {

    private static TreeMenuRootsRegistry injectorMenuRootsTreeRegistry;
    private static TreeMenuEntity directoryRootTreeMenuEntity;
    private static TreeMenuEntity commonsTreeMenuEntity;

    @BeforeClass
    public static void testSetup() throws Exception {
        injectorMenuRootsTreeRegistry = new InjectorTreeMenuRootsRegistryImpl();
        directoryRootTreeMenuEntity = new TreeMenuEntity().setId("dirDir").setValue("Directories").setType(MenuEntityType.TYPE_MENU_SUBMENU);
        commonsTreeMenuEntity = new TreeMenuEntity().setId("commonsdbDir").setValue("Common").
                                                                           setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                           setParentTreeMenuEntity(directoryRootTreeMenuEntity);
        directoryRootTreeMenuEntity.addChildTreeMenuEntity(commonsTreeMenuEntity);

        commonsTreeMenuEntity.addChildTreeMenuEntity(new TreeMenuEntity().setId("organisationDirTreeID").setValue("Organisation").
                                                                                                                                         setParentTreeMenuEntity(commonsTreeMenuEntity).setIcon("icon-building").
                                                                                                                                                                                                                        setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress("views/main.jsf").
                                                                                                                                                                                                                                                                                                          setDescription("Inject data from your local organisation DB to CC organisation directory")).
                                  addChildTreeMenuEntity(new TreeMenuEntity().setId("networkDirTreeID").setValue("Network").setParentTreeMenuEntity(commonsTreeMenuEntity).setIcon("icon-road").
                                                                                                                                                                                                       setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress("views/main.jsf").
                                                                                                                                                                                                                                                                                         setDescription("Inject data from your network CMDB to CC network directory")).
                                  addChildTreeMenuEntity(new TreeMenuEntity().setId("systemDirInjID").setValue("System").setParentTreeMenuEntity(commonsTreeMenuEntity).setIcon("icon-cogs").
                                                                                                                                                                                                    setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress("views/main.jsf").
                                                                                                                                                                                                                                                                                      setDescription("Inject data from your system CMDB to CC system directory"));
    }

    @AfterClass
    public static void testCleanup() {
        ((InjectorTreeMenuRootsRegistryImpl)injectorMenuRootsTreeRegistry).invalidate();
    }

    @Test
    public void testBasicInjectorsRegistration() throws Exception {
        if (injectorMenuRootsTreeRegistry==null)
            testSetup();
        injectorMenuRootsTreeRegistry.registerTreeMenuRootEntity(directoryRootTreeMenuEntity);
        assertTrue(injectorMenuRootsTreeRegistry.getTreeMenuRootsEntities().contains(directoryRootTreeMenuEntity));
        assertFalse(injectorMenuRootsTreeRegistry.getTreeMenuRootsEntities().contains(commonsTreeMenuEntity));
        assertTrue(injectorMenuRootsTreeRegistry.getTreeMenuEntityFromID("commonsdbDir").equals(commonsTreeMenuEntity));
        assertNotNull(injectorMenuRootsTreeRegistry.getTreeMenuEntityFromValue("System"));
    }

    @Test
    public void testBasicInjectorsUnregistration() throws Exception {
        if (injectorMenuRootsTreeRegistry==null)
            testSetup();
        injectorMenuRootsTreeRegistry.unregisterTreeMenuRootEntity(directoryRootTreeMenuEntity);
        assertFalse(injectorMenuRootsTreeRegistry.getTreeMenuRootsEntities().contains(directoryRootTreeMenuEntity));
        assertNull(injectorMenuRootsTreeRegistry.getTreeMenuEntityFromID("commonsdbDir"));
        assertNull(injectorMenuRootsTreeRegistry.getTreeMenuEntityFromValue("System"));
    }
}