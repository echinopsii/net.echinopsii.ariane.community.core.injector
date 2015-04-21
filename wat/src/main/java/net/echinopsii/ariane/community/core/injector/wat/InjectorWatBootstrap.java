/**
 * Injector wat
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

package net.echinopsii.ariane.community.core.injector.wat;

import net.echinopsii.ariane.community.core.portal.base.plugin.FaceletsResourceResolverService;
import net.echinopsii.ariane.community.core.portal.base.model.MainMenuEntity;
import net.echinopsii.ariane.community.core.portal.base.model.MenuEntityType;
import net.echinopsii.ariane.community.core.portal.base.model.TreeMenuEntity;
import net.echinopsii.ariane.community.core.portal.base.plugin.FacesMBeanRegistry;
import net.echinopsii.ariane.community.core.portal.base.plugin.MainMenuEntityRegistry;
import net.echinopsii.ariane.community.core.portal.base.plugin.TreeMenuRootsRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;

@Component
@Provides(properties= {@StaticServiceProperty(name="targetArianeComponent", type="java.lang.String", value="Portal")})
@Instantiate
public class InjectorWatBootstrap implements FaceletsResourceResolverService {
    private static final Logger log = LoggerFactory.getLogger(InjectorWatBootstrap.class);
    private static final String INJECTOR_COMPONENT = "Ariane WAT Injector Component";

    protected static ArrayList<MainMenuEntity> injectorMainMenuEntityList = new ArrayList<MainMenuEntity>() ;
    protected static ArrayList<TreeMenuEntity> injectorTreeEntityList     = new ArrayList<TreeMenuEntity>() ;

    private static String MAIN_MENU_INJECTOR_CONTEXT;
    private static final int    MAIN_MENU_DIR_RANK = 3;
    private static final String basePath = "/META-INF";
    private static final String FACES_CONFIG_FILE_PATH= basePath + "/faces-config.xml";

    @Requires(from="ArianePortalFacesMBeanRegistry")
    private FacesMBeanRegistry portalPluginFacesMBeanRegistry = null;
    private static FacesMBeanRegistry portalPluginFacesMBeanRegistrySgt = null;

    @Requires(from="InjectorTreeMenuRootsRegistryImpl")
    private TreeMenuRootsRegistry treeMenuRootsRegistry = null;
    private static TreeMenuRootsRegistry treeMenuRootsRegistrySgt = null;

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

    @Bind(from="ArianePortalFacesMBeanRegistry")
    public void bindPortalPluginFacesMBeanRegistry(FacesMBeanRegistry r) {
        log.debug("Bound to portal plugin faces managed bean registry...");
        portalPluginFacesMBeanRegistry = r;
        portalPluginFacesMBeanRegistrySgt = r;
    }

    @Unbind
    public void unbindPortalPluginFacesMBeanRegistry() {
        log.debug("Unbound from portal plugin faces managed bean registry...");
        portalPluginFacesMBeanRegistry = null;
    }

    @Bind(from="InjectorTreeMenuRootsRegistryImpl")
    public void bindTreeMenuRootsRegistry(TreeMenuRootsRegistry r) {
        log.debug("Bound to injector tree menu roots registry..." + r.toString());
        treeMenuRootsRegistry = r;
        treeMenuRootsRegistrySgt = r;
    }

    @Unbind
    public void unbindTreeMenuRootsRegistry() {
        log.debug("Unbound from injector tree menu roots registry...");
        treeMenuRootsRegistry = null;
    }

    public static FacesMBeanRegistry getPortalPluginFacesMBeanRegistry() {
        return portalPluginFacesMBeanRegistrySgt;
    }

    public static TreeMenuRootsRegistry getTreeMenuRootsRegistry() {
        return treeMenuRootsRegistrySgt;
    }

    @Validate
    public void validate() throws Exception {
        portalPluginFacesMBeanRegistry.registerPluginFacesMBeanConfig(InjectorWatBootstrap.class.getResource(FACES_CONFIG_FILE_PATH));
        MAIN_MENU_INJECTOR_CONTEXT = portalPluginFacesMBeanRegistry.getRegisteredServletContext().getContextPath()+"/";

        try {
            MainMenuEntity mainMenuEntity = new MainMenuEntity("injectorsMItem", "Injectors", MAIN_MENU_INJECTOR_CONTEXT + "views/injectors/main.jsf", MenuEntityType.TYPE_MENU_ITEM, MAIN_MENU_DIR_RANK, "icon-inject-ariane icon-large");
            mainMenuEntity.getDisplayRoles().add("ntwadmin");
            mainMenuEntity.getDisplayRoles().add("sysadmin");
            mainMenuEntity.getDisplayRoles().add("orgadmin");
            mainMenuEntity.getDisplayRoles().add("ntwreviewer");
            mainMenuEntity.getDisplayRoles().add("sysreviewer");
            mainMenuEntity.getDisplayRoles().add("orgreviewer");
            mainMenuEntity.getDisplayPermissions().add("injDirComNtw:display");
            mainMenuEntity.getDisplayPermissions().add("injDirComSys:display");
            mainMenuEntity.getDisplayPermissions().add("injDirComOrg:display");

            injectorMainMenuEntityList.add(mainMenuEntity);
            mainMenuEntityRegistry.registerMainLeftMenuEntity(mainMenuEntity);
            treeMenuRootsRegistry.setLinkedMainMenuEntity(mainMenuEntity);
            log.debug("{} has registered its main menu items", new Object[]{INJECTOR_COMPONENT});
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        /*
        try {
            TreeMenuEntity directoryRootInjectorMenuEntity = new TreeMenuEntity().setId("dirTree").setValue("Directories").setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                                  addDisplayRole("ntwadmin").addDisplayRole("sysadmin").addDisplayRole("orgadmin").
                                                                                  addDisplayRole("ntwreviewer").addDisplayRole("sysreviewer").addDisplayRole("orgreviewer").
                                                                                  addDisplayPermission("injDirComNtw:display").addDisplayPermission("injDirComSys:display").
                                                                                  addDisplayPermission("injDirComOrg:display");
            injectorTreeEntityList.add(directoryRootInjectorMenuEntity);
            treeMenuRootsRegistry.registerTreeMenuRootEntity(directoryRootInjectorMenuEntity);


            TreeMenuEntity commonsInjectorMenuEntity = new TreeMenuEntity().setId("commonsdbTree").setValue("Common").setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                            setParentTreeMenuEntity(directoryRootInjectorMenuEntity).
                                                                            addDisplayRole("ntwadmin").addDisplayRole("sysadmin").addDisplayRole("orgadmin").
                                                                            addDisplayRole("ntwreviewer").addDisplayRole("sysreviewer").addDisplayRole("orgreviewer").
                                                                            addDisplayPermission("injDirComNtw:display").addDisplayPermission("injDirComSys:display").
                                                                            addDisplayPermission("injDirComOrg:display");
            directoryRootInjectorMenuEntity.addChildTreeMenuEntity(commonsInjectorMenuEntity);


            commonsInjectorMenuEntity.addChildTreeMenuEntity(new TreeMenuEntity().setId("organisationDirTreeID").setValue("Organisation").setParentTreeMenuEntity(commonsInjectorMenuEntity).
                                                                                  setIcon("icon-building").setDescription("Inject data from your local organisation DB to Ariane organisation directory").
                                                                                  setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_INJECTOR_CONTEXT + "views/injectors/main.jsf").
                                                                                  addDisplayRole("orgadmin").addDisplayRole("orgreviewer").addDisplayPermission("injDirComOrg:display")).
                                      addChildTreeMenuEntity(new TreeMenuEntity().setId("networkDirTreeID").setValue("Network").setParentTreeMenuEntity(commonsInjectorMenuEntity).
                                                                                  setIcon("icon-road").setDescription("Inject data from your network CMDB to Ariane network directory").
                                                                                  setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_INJECTOR_CONTEXT + "views/injectors/main.jsf").
                                                                                  addDisplayRole("ntwadmin").addDisplayRole("ntwreviewer").addDisplayPermission("injDirComNtw:display")).
                                      addChildTreeMenuEntity(new TreeMenuEntity().setId("systemDirTreeID").setValue("System").setParentTreeMenuEntity(commonsInjectorMenuEntity).
                                                                                  setIcon("icon-cogs").setDescription("Inject data from your system CMDB to Ariane system directory").
                                                                                  setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_INJECTOR_CONTEXT + "views/injectors/main.jsf").
                                                                                  addDisplayRole("sysadmin").addDisplayRole("sysreviewer").addDisplayPermission("injDirComSys:display"));//.

            log.debug("{} has registered its commons injector items", new Object[]{INJECTOR_COMPONENT});

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        */
        log.info("{} is started", new Object[]{INJECTOR_COMPONENT});
    }

    @Invalidate
    public void invalidate() throws Exception {
        if (mainMenuEntityRegistry!=null) {
            for(MainMenuEntity entity : injectorMainMenuEntityList) {
                mainMenuEntityRegistry.unregisterMainLeftMenuEntity(entity);
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
        return InjectorWatBootstrap.class.getResource(basePath + path);
    }
}