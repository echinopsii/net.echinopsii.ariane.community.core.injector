/**
 * Injector Main
 * Registrator
 * Copyright (C) 2013 Mathilde Ffrench
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.spectral.cc.core.injector.main.runtime;

import com.spectral.cc.core.injector.commons.consumer.InjectorPluginFacesMBeanRegistryConsumer;
import com.spectral.cc.core.injector.commons.consumer.InjectorRootsTreeRegistryServiceConsumer;
import com.spectral.cc.core.injector.commons.model.InjectorEntity;
import com.spectral.cc.core.portal.commons.consumer.MainMenuRegistryConsumer;
import com.spectral.cc.core.portal.commons.model.MainMenuEntity;
import com.spectral.cc.core.portal.commons.model.MenuEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Registrator implements Runnable {

    private static final String INJECTOR_REGISTRATOR_TASK_NAME = "Injector Registrator Task";
    private static final Logger log = LoggerFactory.getLogger(Registrator.class);

    private static String MAIN_MENU_INJECTOR_CONTEXT;
    private static int MAIN_MENU_DIR_RANK = 2;

    @Override
    public void run() {
        //TODO : check a better way to start war after OSGI layer
        while((InjectorPluginFacesMBeanRegistryConsumer.getInstance().getInjectorPluginFacesMBeanRegistry()==null) ||
                      (InjectorPluginFacesMBeanRegistryConsumer.getInstance().getInjectorPluginFacesMBeanRegistry().getRegisteredServletContext()==null))
            try {
                log.info("Injector plugin faces managed bean registry is missing or is still not initialized to load {}. Sleep some times...", INJECTOR_REGISTRATOR_TASK_NAME);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        MAIN_MENU_INJECTOR_CONTEXT = InjectorPluginFacesMBeanRegistryConsumer.getInstance().getInjectorPluginFacesMBeanRegistry().getRegisteredServletContext().getContextPath()+"/";

        //TODO : check a better way to start war after OSGI layer
        while(MainMenuRegistryConsumer.getInstance().getMainMenuEntityRegistry()==null)
            try {
                log.info("Portal main menu registry is missing to load {}. Sleep some times...", INJECTOR_REGISTRATOR_TASK_NAME);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        try {
            MainMenuEntity mainMenuEntity = new MainMenuEntity("injectorsMItem", "Injectors", MAIN_MENU_INJECTOR_CONTEXT + "views/main.jsf", MenuEntityType.TYPE_MENU_ITEM, MAIN_MENU_DIR_RANK, "icon-filter icon-large");
            OsgiActivator.injectorMainMenuEntityList.add(mainMenuEntity);
            MainMenuRegistryConsumer.getInstance().getMainMenuEntityRegistry().registerMainMenuEntity(mainMenuEntity);
            log.debug("{} has registered its main menu items", new Object[]{INJECTOR_REGISTRATOR_TASK_NAME});
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //TODO : check a better way to start war after OSGI layer
        while(InjectorRootsTreeRegistryServiceConsumer.getInstance().getInjectorRootsTreeRegistry()==null)
            try {
                log.info("Injector roots tree registry is missing to load {}. Sleep some times...", INJECTOR_REGISTRATOR_TASK_NAME);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        try {
            InjectorEntity directoryRootInjectorEntity = new InjectorEntity().setId("dirDir").setValue("Directories").setType(MenuEntityType.TYPE_MENU_SUBMENU);
            OsgiActivator.injectorTreeEntityList.add(directoryRootInjectorEntity);
            InjectorRootsTreeRegistryServiceConsumer.getInstance().getInjectorRootsTreeRegistry().registerRootInjectorEntity(directoryRootInjectorEntity);

            InjectorEntity commonsInjectorEntity = new InjectorEntity().setId("commonsdbDir").setValue("Common").
                                                            setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                            setParentInjector(directoryRootInjectorEntity);
            directoryRootInjectorEntity.addChildInjector(commonsInjectorEntity);

            commonsInjectorEntity.
                    addChildInjector(new InjectorEntity().setId("organisationDirInjID").setValue("Organisation").setParentInjector(commonsInjectorEntity).setIcon("icon-building").
                                                                                                                                                                                         setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_INJECTOR_CONTEXT + "views/main.jsf").
                                                                                                                                                                                                                                                                                                        setDescription("Inject data from your local organisation DB to CC organisation directory")).
                    addChildInjector(new InjectorEntity().setId("networkDirInjID").setValue("Network").setParentInjector(commonsInjectorEntity).setIcon("icon-road").
                                                            setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_INJECTOR_CONTEXT + "views/main.jsf").
                                                            setDescription("Inject data from your network CMDB to CC network directory")).
                    addChildInjector(new InjectorEntity().setId("systemDirInjID").setValue("System").setParentInjector(commonsInjectorEntity).setIcon("icon-cogs").
                                                            setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_INJECTOR_CONTEXT + "views/main.jsf").
                                                            setDescription("Inject data from your system CMDB to CC system directory"));//.
            log.debug("{} has registered its commons injector items", new Object[]{INJECTOR_REGISTRATOR_TASK_NAME});

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}