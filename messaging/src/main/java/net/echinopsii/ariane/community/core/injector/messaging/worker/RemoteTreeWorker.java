/**
 * Injector Messaging Module
 * Remote Tree Injector Messaging worker
 * Copyright (C) 21/04/15 echinopsii
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

package net.echinopsii.ariane.community.core.injector.messaging.worker;

import net.echinopsii.ariane.community.core.injector.messaging.InjectorMessagingBootstrap;
import net.echinopsii.ariane.community.core.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.core.messaging.api.MomMsgTranslator;
import net.echinopsii.ariane.community.core.portal.base.json.TreeMenuEntityJSON;
import net.echinopsii.ariane.community.core.portal.base.model.MainMenuEntity;
import net.echinopsii.ariane.community.core.portal.base.model.TreeMenuEntity;
import net.echinopsii.ariane.community.core.portal.base.plugin.TreeMenuRootsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class RemoteTreeWorker implements AppMsgWorker, TreeMenuRootsRegistry {
    private static final Logger log = LoggerFactory.getLogger(RemoteTreeWorker.class);

    public final static String OPERATION_FDN = "OPERATION";
    public final static String OPERATION_VAL_REGISTER = "REGISTER";
    public final static String OPERATION_VAL_UNREGISTER = "UNREGISTER";
    public final static String OPERATION_VAL_GETTREEMRES = "GET_TREE_MENU_ENTITIES";
    public final static String OPERATION_VAL_GETTREEMREV = "GET_TREE_MENU_ENTITY_V";
    public final static String OPERATION_VAL_GETTREEMREI = "GET_TREE_MENU_ENTITY_I";
    public final static String OPERATION_VAL_GETTREEMREC = "GET_TREE_MENU_ENTITY_C";
    public final static String OPERATION_VAL_GETLKME = "GET_LINKED_MENU_ENTITY";
    public final static String OPERATION_VAL_SETLKME = "SET_LINKED_MENU_ENTITY";

    public final static String TREE_MENU_ENTITY = "TREE_MENU_ENTITY";



    @Override
    public Map<String, Object> apply(Map<String, Object> message) {
        log.debug("Injector Remote Tree Worker on  : {" + message.get(MomMsgTranslator.MSG_APPLICATION_ID) + " }...");

        Map<String, Object> reply = null;
        String entity;
        String operation = (String)message.get(OPERATION_FDN);
        switch (operation) {
            case OPERATION_VAL_REGISTER:
                entity = (String) message.get(TREE_MENU_ENTITY);
                try {
                    this.registerTreeMenuRootEntity(TreeMenuEntityJSON.JSON2TreeMenuEntity(entity));
                    reply = new HashMap<>();
                    reply.put(MomMsgTranslator.MSG_BODY, "Tree Menu Entity Registered successfully...");
                } catch (IOException e) {
                    e.printStackTrace();
                    reply = new HashMap<String, Object>();
                    reply.put(MomMsgTranslator.MSG_BODY, "Invalid Tree Menu Entity : " + entity);
                }
                break;
            case OPERATION_VAL_UNREGISTER:
                entity = (String) message.get(TREE_MENU_ENTITY);
                try {
                    this.unregisterTreeMenuRootEntity(TreeMenuEntityJSON.JSON2TreeMenuEntity(entity));
                    reply = new HashMap<>();
                    reply.put(MomMsgTranslator.MSG_BODY, "Tree Menu Entity Unregistered successfully...");
                } catch (IOException e) {
                    e.printStackTrace();
                    reply = new HashMap<String, Object>();
                    reply.put(MomMsgTranslator.MSG_BODY, "Invalid Tree Menu Entity : " + entity);
                }
                break;
            case OPERATION_VAL_GETTREEMRES:
                String ret;
                try {
                    ret = TreeMenuEntityJSON.manyTreeMenuEntity2JSON(this.getTreeMenuRootsEntities());
                    reply = new HashMap<>();
                    reply.put(MomMsgTranslator.MSG_BODY, ret);
                } catch (IOException e) {
                    e.printStackTrace();
                    reply = new HashMap<String, Object>();
                    reply.put(MomMsgTranslator.MSG_BODY, "Error while getting Tree Menu Entities ... ");
                }
                break;
            case OPERATION_VAL_GETTREEMREV:

                break;
            case OPERATION_VAL_GETTREEMREI:
                break;
            case OPERATION_VAL_GETTREEMREC:
                break;
            case OPERATION_VAL_GETLKME:
                break;
            case OPERATION_VAL_SETLKME:
                break;
            default:
                reply = new HashMap<String, Object>();
                reply.put(MomMsgTranslator.MSG_BODY, "Unknow operation : " + operation);
        }
        return reply;
    }

    @Override
    public TreeMenuEntity registerTreeMenuRootEntity(TreeMenuEntity treeMenuEntity) {
        InjectorMessagingBootstrap.getTreeMenuRootsRegistry().registerTreeMenuRootEntity(treeMenuEntity);
        return treeMenuEntity;
    }

    @Override
    public TreeMenuEntity unregisterTreeMenuRootEntity(TreeMenuEntity treeMenuEntity) {
        InjectorMessagingBootstrap.getTreeMenuRootsRegistry().unregisterTreeMenuRootEntity(treeMenuEntity);
        return null;
    }

    @Override
    public TreeSet<TreeMenuEntity> getTreeMenuRootsEntities() {
        return InjectorMessagingBootstrap.getTreeMenuRootsRegistry().getTreeMenuRootsEntities();
    }

    @Override
    public TreeMenuEntity getTreeMenuEntityFromValue(String value) {
        return InjectorMessagingBootstrap.getTreeMenuRootsRegistry().getTreeMenuEntityFromValue(value);
    }

    @Override
    public TreeMenuEntity getTreeMenuEntityFromID(String id) {
        return InjectorMessagingBootstrap.getTreeMenuRootsRegistry().getTreeMenuEntityFromID(id);
    }

    @Override
    public TreeMenuEntity getTreeMenuEntityFromContextAddress(String contextAddress) {
        return InjectorMessagingBootstrap.getTreeMenuRootsRegistry().getTreeMenuEntityFromContextAddress(contextAddress);
    }

    @Override
    public MainMenuEntity getLinkedMainMenuEntity() {
        return InjectorMessagingBootstrap.getTreeMenuRootsRegistry().getLinkedMainMenuEntity();
    }

    @Override
    public void setLinkedMainMenuEntity(MainMenuEntity mainMenuEntity) {
        InjectorMessagingBootstrap.getTreeMenuRootsRegistry().setLinkedMainMenuEntity(mainMenuEntity);
    }
}