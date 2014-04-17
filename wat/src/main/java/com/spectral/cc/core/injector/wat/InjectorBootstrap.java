/**
 * Injector Commons JSF bundle
 * Injector Component Bootstrap
 * Copyright (C) 2013 Mathilde Ffrench
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

package com.spectral.cc.core.injector.wat;

import com.spectral.cc.core.portal.base.plugin.FaceletsResourceResolverService;
import com.spectral.cc.core.portal.base.model.MainMenuEntity;
import com.spectral.cc.core.portal.base.model.MenuEntityType;
import com.spectral.cc.core.portal.base.model.TreeMenuEntity;
import com.spectral.cc.core.portal.base.plugin.FacesMBeanRegistry;
import com.spectral.cc.core.portal.base.plugin.MainMenuEntityRegistry;
import com.spectral.cc.core.portal.base.plugin.TreeMenuRootsRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;

@Component
@Provides(properties= {@StaticServiceProperty(name="targetCCcomponent", type="java.lang.String", value="Portal")})
@Instantiate
public class InjectorBootstrap implements FaceletsResourceResolverService {
    private static final Logger log = LoggerFactory.getLogger(InjectorBootstrap.class);
    private static final String INJECTOR_COMPONENT = "CC Injector Component";

    protected static ArrayList<MainMenuEntity> injectorMainMenuEntityList = new ArrayList<MainMenuEntity>() ;
    protected static ArrayList<TreeMenuEntity> injectorTreeEntityList     = new ArrayList<TreeMenuEntity>() ;

    private static String MAIN_MENU_INJECTOR_CONTEXT;
    private static final int    MAIN_MENU_DIR_RANK = 3;
    private static final String basePath = "/META-INF";
    private static final String FACES_CONFIG_FILE_PATH= basePath + "/faces-config.xml";

    @Requires(from="CCPortalFacesMBeanRegistry")
    private FacesMBeanRegistry portalPluginFacesMBeanRegistry = null;

    @Requires(from="InjectorTreeMenuRootsRegistryImpl")
    private TreeMenuRootsRegistry treeMenuRootsRegistry = null;

    @Requires
    private MainMenuEntityRegistry mainMenuEntityRegistry = null;

    @Bind
    public void bindMainMenuEntityRegistry(MainMenuEntityRegistry r) {
        log.debug("Bound to main menu item registry...");
        mainMenuEntityRegistry = r;
    }

    @Unbind
    public void unbindMainMenuEntityRegistry() {
        log.debug("Unbound from main menu item registry...");
        mainMenuEntityRegistry = null;
    }

    @Bind
    public void bindPortalPluginFacesMBeanRegistry(FacesMBeanRegistry r) {
        log.debug("Bound to portal plugin faces managed bean registry...");
        portalPluginFacesMBeanRegistry = r;
    }

    @Unbind
    public void unbindPortalPluginFacesMBeanRegistry() {
        log.debug("Unbound from portal plugin faces managed bean registry...");
        portalPluginFacesMBeanRegistry = null;
    }

    @Bind
    public void bindTreeMenuRootsRegistry(TreeMenuRootsRegistry r) {
        log.debug("Bound to directory tree menu roots registry...");
        treeMenuRootsRegistry = r;
    }

    @Unbind
    public void unbindTreeMenuRootsRegistry() {
        log.debug("Unbound from directory tree menu roots registry...");
        treeMenuRootsRegistry = null;
    }

    @Validate
    public void validate() throws Exception {
        portalPluginFacesMBeanRegistry.registerPluginFacesMBeanConfig(InjectorBootstrap.class.getResource(FACES_CONFIG_FILE_PATH));
        MAIN_MENU_INJECTOR_CONTEXT = portalPluginFacesMBeanRegistry.getRegisteredServletContext().getContextPath()+"/";

        try {
            MainMenuEntity mainMenuEntity = new MainMenuEntity("injectorsMItem", "Injectors", MAIN_MENU_INJECTOR_CONTEXT + "views/injectors/main.jsf", MenuEntityType.TYPE_MENU_ITEM, MAIN_MENU_DIR_RANK, "icon-filter icon-large");
            mainMenuEntity.getDisplayRoles().add("ccntwadmin");
            mainMenuEntity.getDisplayRoles().add("ccsysadmin");
            mainMenuEntity.getDisplayRoles().add("ccorgadmin");
            mainMenuEntity.getDisplayRoles().add("ccntwreviewer");
            mainMenuEntity.getDisplayRoles().add("ccsysreviewer");
            mainMenuEntity.getDisplayRoles().add("ccorgreviewer");
            mainMenuEntity.getDisplayPermissions().add("ccInjDirComNtw:display");
            mainMenuEntity.getDisplayPermissions().add("ccInjDirComSys:display");
            mainMenuEntity.getDisplayPermissions().add("ccInjDirComOrg:display");

            injectorMainMenuEntityList.add(mainMenuEntity);
            mainMenuEntityRegistry.registerMainMenuEntity(mainMenuEntity);
            treeMenuRootsRegistry.setLinkedMainMenuEntity(mainMenuEntity);
            log.debug("{} has registered its main menu items", new Object[]{INJECTOR_COMPONENT});
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            TreeMenuEntity directoryRootInjectorMenuEntity = new TreeMenuEntity().setId("dirTree").setValue("Directories").setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                                  addDisplayRole("ccntwadmin").addDisplayRole("ccsysadmin").addDisplayRole("ccorgadmin").
                                                                                  addDisplayRole("ccntwreviewer").addDisplayRole("ccsysreviewer").addDisplayRole("ccorgreviewer").
                                                                                  addDisplayPermission("ccInjDirComNtw:display").addDisplayPermission("ccInjDirComSys:display").
                                                                                  addDisplayPermission("ccInjDirComOrg:display");
            injectorTreeEntityList.add(directoryRootInjectorMenuEntity);
            treeMenuRootsRegistry.registerTreeMenuRootEntity(directoryRootInjectorMenuEntity);


            TreeMenuEntity commonsInjectorMenuEntity = new TreeMenuEntity().setId("commonsdbTree").setValue("Common").setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                            setParentTreeMenuEntity(directoryRootInjectorMenuEntity).
                                                                            addDisplayRole("ccntwadmin").addDisplayRole("ccsysadmin").addDisplayRole("ccorgadmin").
                                                                            addDisplayRole("ccntwreviewer").addDisplayRole("ccsysreviewer").addDisplayRole("ccorgreviewer").
                                                                            addDisplayPermission("ccInjDirComNtw:display").addDisplayPermission("ccInjDirComSys:display").
                                                                            addDisplayPermission("ccInjDirComOrg:display");
            directoryRootInjectorMenuEntity.addChildTreeMenuEntity(commonsInjectorMenuEntity);


            commonsInjectorMenuEntity.addChildTreeMenuEntity(new TreeMenuEntity().setId("organisationDirTreeID").setValue("Organisation").setParentTreeMenuEntity(commonsInjectorMenuEntity).
                                                                                  setIcon("icon-building").setDescription("Inject data from your local organisation DB to CC organisation directory").
                                                                                  setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_INJECTOR_CONTEXT + "views/injectors/main.jsf").
                                                                                  addDisplayRole("ccorgadmin").addDisplayRole("ccorgreviewer").addDisplayPermission("ccInjDirComOrg:display")).
                                      addChildTreeMenuEntity(new TreeMenuEntity().setId("networkDirTreeID").setValue("Network").setParentTreeMenuEntity(commonsInjectorMenuEntity).
                                                                                  setIcon("icon-road").setDescription("Inject data from your network CMDB to CC network directory").
                                                                                  setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_INJECTOR_CONTEXT + "views/injectors/main.jsf").
                                                                                  addDisplayRole("ccntwadmin").addDisplayRole("ccntwreviewer").addDisplayPermission("ccInjDirComNtw:display")).
                                      addChildTreeMenuEntity(new TreeMenuEntity().setId("systemDirTreeID").setValue("System").setParentTreeMenuEntity(commonsInjectorMenuEntity).
                                                                                  setIcon("icon-cogs").setDescription("Inject data from your system CMDB to CC system directory").
                                                                                  setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_INJECTOR_CONTEXT + "views/injectors/main.jsf").
                                                                                  addDisplayRole("ccsysadmin").addDisplayRole("ccsysreviewer").addDisplayPermission("ccInjDirComSys:display"));//.

            log.debug("{} has registered its commons injector items", new Object[]{INJECTOR_COMPONENT});

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        log.info("{} is started", new Object[]{INJECTOR_COMPONENT});
    }

    @Invalidate
    public void invalidate() throws Exception {
        if (mainMenuEntityRegistry!=null) {
            for(MainMenuEntity entity : injectorMainMenuEntityList) {
                mainMenuEntityRegistry.unregisterMainMenuEntity(entity);
            }
        }
        injectorMainMenuEntityList.clear();

        if (treeMenuRootsRegistry!=null) {
            for(TreeMenuEntity entity : injectorTreeEntityList) {
                treeMenuRootsRegistry.unregisterTreeMenuRootEntity(entity);
            }
        }
        injectorTreeEntityList.clear();

        log.info("{} is stopped", new Object[]{INJECTOR_COMPONENT});
    }

    @Override
    public URL resolveURL(String path) {
        log.debug("Resolve {} from directory commons-jsf...", new Object[]{path});
        return InjectorBootstrap.class.getResource(basePath + path);
    }
}