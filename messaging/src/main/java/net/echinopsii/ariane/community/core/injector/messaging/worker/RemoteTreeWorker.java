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
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import net.echinopsii.ariane.community.core.portal.base.json.TreeMenuEntityJSON;
import net.echinopsii.ariane.community.core.portal.base.model.MainMenuEntity;
import net.echinopsii.ariane.community.core.portal.base.model.TreeMenuEntity;
import net.echinopsii.ariane.community.core.portal.base.plugin.TreeMenuRootsRegistry;
import net.echinopsii.ariane.community.messaging.api.MomServiceFactory;
import net.echinopsii.ariane.community.messaging.common.MomAkkaAbsAppHPMsgSrvWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class RemoteTreeWorker extends MomAkkaAbsAppHPMsgSrvWorker implements TreeMenuRootsRegistry {
    private static final Logger log = LoggerFactory.getLogger(RemoteTreeWorker.class);

    public final static String OPERATION_VAL_REGISTER = "REGISTER";
    public final static String OPERATION_VAL_SET_PARENT_ENTITY = "SET_PARENT";
    public final static String OPERATION_VAL_UNREGISTER = "UNREGISTER";
    public final static String OPERATION_VAL_UPDATE_ENTITY = "UPDATE";
    public final static String OPERATION_VAL_GETTREEMRES = "GET_TREE_MENU_ENTITIES";
    public final static String OPERATION_VAL_GETTREEMREV = "GET_TREE_MENU_ENTITY_V";
    public final static String OPERATION_VAL_GETTREEMREI = "GET_TREE_MENU_ENTITY_I";
    public final static String OPERATION_VAL_GETTREEMREC = "GET_TREE_MENU_ENTITY_C";

    public final static String TREE_MENU_ENTITY = "TREE_MENU_ENTITY";
    public final static String TREE_MENU_ENTITY_VALUE = "TREE_MENU_ENTITY_VALUE";
    public final static String TREE_MENU_ENTITY_ID = "TREE_MENU_ENTITY_ID";
    public final static String TREE_MENU_ENTITY_PARENT_ID = "TREE_MENU_ENTITY_PARENT_ID";
    public final static String TREE_MENU_ENTITY_CA = "TREE_MENU_ENTITY_CONTEXT_ADDRESS";

    public RemoteTreeWorker (MomServiceFactory serviceFactory) {
        super(serviceFactory);
    }

    @Override
    public Map<String, Object> apply(Map<String, Object> message) {
        log.debug("Injector Remote Tree Worker on  : { " + message.toString() + " }...");
        Map<String, Object> reply = super.apply(message);
        if (reply!=null) return reply;
        else reply = new HashMap<>();

        Object oOperation = message.get(MomMsgTranslator.OPERATION_FDN);
        String operation = null;

        String param1 = null;
        String param2 = null;
        Object oParam = null;

        String ret;

        if (oOperation==null)
            operation = MomMsgTranslator.OPERATION_NOT_DEFINED;
        else
            operation = oOperation.toString();

        switch (operation) {
            case OPERATION_VAL_REGISTER:
                oParam = message.get(TREE_MENU_ENTITY);
                if (oParam != null) {
                    param1 = oParam.toString();
                    try {
                        TreeMenuEntity entity = TreeMenuEntityJSON.JSON2TreeMenuEntity(param1);
                        if (this.getTreeMenuEntityFromID(entity.getId()) == null) {
                            this.registerTreeMenuRootEntity(entity);
                            reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            reply.put(MomMsgTranslator.MSG_BODY, "Tree Menu Entity Registered successfully...");
                        } else {
                            reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            reply.put(MomMsgTranslator.MSG_ERR, "This entity " + entity.getId() + " is already registered !");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                        reply.put(MomMsgTranslator.MSG_ERR, "Tree Menu Entity serialization problem... Have a look to this message body and Ariane server logs !");
                        reply.put(MomMsgTranslator.MSG_BODY, e.getMessage());
                    }
                } else {
                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                    reply.put(MomMsgTranslator.MSG_ERR, "Invalid request : no tree menu entity submitted...");
                }
                break;

            case OPERATION_VAL_SET_PARENT_ENTITY:
                oParam = message.get(TREE_MENU_ENTITY_ID);
                if (oParam != null) {
                    param1 = oParam.toString();
                    oParam = message.get(TREE_MENU_ENTITY_PARENT_ID);
                    if (oParam != null) {
                        param2 = oParam.toString();
                        TreeMenuEntity entity = this.getTreeMenuEntityFromID(param1);
                        if (entity != null) {
                            TreeMenuEntity parentEntity = this.getTreeMenuEntityFromID(param2);
                            if (parentEntity != null) {
                                if (entity.getParentTreeMenuEntity() != null)
                                    entity.getParentTreeMenuEntity().removeChildTreeMenuEntity(entity);
                                else this.unregisterTreeMenuRootEntity(entity);
                                entity.setParentTreeMenuEntity(parentEntity);
                                parentEntity.addChildTreeMenuEntity(entity);
                                reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                reply.put(MomMsgTranslator.MSG_BODY, "Set parent " + param2 + " successfully to tree menu entity " + param1);
                            } else {
                                reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                                reply.put(MomMsgTranslator.MSG_ERR, "Parent tree menu entity " + param1 + " not found !");
                            }
                        } else {
                            reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                            reply.put(MomMsgTranslator.MSG_ERR, "Tree menu entity " + param1 + " not found !");
                        }
                    } else {
                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                        reply.put(MomMsgTranslator.MSG_ERR, "Invalid request : no parent tree menu entity id submitted...");
                    }
                } else {
                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                    reply.put(MomMsgTranslator.MSG_ERR, "Invalid request : no tree menu entity id submitted...");
                }
                break;
            case OPERATION_VAL_UPDATE_ENTITY:
                oParam = message.get(TREE_MENU_ENTITY);
                if (oParam != null) {
                    param1 = oParam.toString();
                    try {
                        TreeMenuEntity entity = TreeMenuEntityJSON.JSON2TreeMenuEntity(param1);
                        TreeMenuEntity entityToUpdate = this.getTreeMenuEntityFromID(entity.getId());
                        if (entityToUpdate == null) {
                            reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                            reply.put(MomMsgTranslator.MSG_ERR, "This entity " + entity.getId() + " is already registered !");
                        } else {
                            if (entity.getType()!=0) entityToUpdate.setType(entity.getType());
                            if (entity.getValue()!=null) entityToUpdate.setValue(entity.getValue());
                            if (entity.getContextAddress()!=null) entityToUpdate.setContextAddress(entity.getContextAddress());
                            if (entity.getDescription()!=null) entityToUpdate.setDescription(entity.getDescription());
                            if (entity.getIcon()!=null) entityToUpdate.setIcon(entity.getIcon());
                            if (entity.getDisplayRoles()!=null) {
                                entityToUpdate.getDisplayRoles().clear();
                                entityToUpdate.getDisplayRoles().addAll(entity.getDisplayRoles());
                            }
                            if (entity.getDisplayPermissions()!=null) {
                                entityToUpdate.getDisplayPermissions().clear();
                                entityToUpdate.getDisplayPermissions().addAll(entity.getDisplayPermissions());
                            }
                            if (entity.getOtherActionsRoles()!=null) {
                                entityToUpdate.getOtherActionsRoles().clear();
                                entityToUpdate.getOtherActionsRoles().putAll(entity.getOtherActionsRoles());
                            }
                            if (entity.getOtherActionsPerms()!=null) {
                                entityToUpdate.getOtherActionsPerms().clear();
                                entityToUpdate.getOtherActionsPerms().putAll(entity.getOtherActionsPerms());
                            }
                            if (entity.getRemoteInjectorTreeEntityComponentsCacheId()!=null)
                                entityToUpdate.setRemoteInjectorTreeEntityComponentsCacheId(entity.getRemoteInjectorTreeEntityComponentsCacheId());
                            if (entity.getRemoteInjectorTreeEntityGearsCacheId()!=null)
                                entityToUpdate.setRemoteInjectorTreeEntityGearsCacheId(entity.getRemoteInjectorTreeEntityGearsCacheId());
                            reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            reply.put(MomMsgTranslator.MSG_BODY, "Tree Menu Entity Registered successfully...");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                        reply.put(MomMsgTranslator.MSG_ERR, "Tree Menu Entity serialization problem... Have a look to this message body and Ariane server logs !");
                        reply.put(MomMsgTranslator.MSG_BODY, e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                        reply.put(MomMsgTranslator.MSG_ERR, "Unexpected exception... Have a look to this message body and Ariane server logs !");
                        reply.put(MomMsgTranslator.MSG_BODY, e.getMessage());
                    }
                } else {
                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                    reply.put(MomMsgTranslator.MSG_ERR, "Invalid request : no tree menu entity submitted...");
                }
                break;

            case OPERATION_VAL_UNREGISTER:
                oParam = message.get(TREE_MENU_ENTITY_ID);
                if (oParam != null) {
                    param1 = oParam.toString();
                    TreeMenuEntity entity = this.getTreeMenuEntityFromID(param1);
                    if (entity != null) {
                        if (entity.getParentTreeMenuEntity() != null) {
                            entity.getParentTreeMenuEntity().removeChildTreeMenuEntity(entity);
                            entity.setParentTreeMenuEntity(null);
                        } else this.unregisterTreeMenuRootEntity(entity);
                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                        reply.put(MomMsgTranslator.MSG_BODY, "Tree Menu Entity Unregistered successfully...");
                    } else {
                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                        reply.put(MomMsgTranslator.MSG_ERR, "Tree menu entity " + param1 + " not found !");
                    }
                } else {
                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                    reply.put(MomMsgTranslator.MSG_ERR, "Invalid request : no tree menu entity id submitted...");
                }

                break;

            case OPERATION_VAL_GETTREEMRES:
                try {
                    ret = TreeMenuEntityJSON.manyTreeMenuEntity2JSON(this.getTreeMenuRootsEntities());
                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                    reply.put(MomMsgTranslator.MSG_BODY, ret);
                } catch (IOException e) {
                    e.printStackTrace();
                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                    reply.put(MomMsgTranslator.MSG_ERR, "Unexpected exception... Have a look to this message body and Ariane server logs !");
                    reply.put(MomMsgTranslator.MSG_BODY, e.getMessage());
                }
                break;

            case OPERATION_VAL_GETTREEMREV:
                oParam = message.get(TREE_MENU_ENTITY_VALUE);
                try {
                    if (oParam != null) {
                        param1 = oParam.toString();
                        TreeMenuEntity entity = this.getTreeMenuEntityFromValue(param1);
                        if (entity!=null) {
                            ret = TreeMenuEntityJSON.treeMenuEntity2JSON(entity);
                            reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            reply.put(MomMsgTranslator.MSG_BODY, ret);
                        } else {
                            reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                            reply.put(MomMsgTranslator.MSG_ERR, "TreeMenuEntity not found ( value: " + param1 + " ) !");
                        }
                    } else {
                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                        reply.put(MomMsgTranslator.MSG_ERR, "Invalid request : no tree menu entity value submitted...");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                    reply.put(MomMsgTranslator.MSG_ERR, "Unexpected exception... Have a look to this message body and Ariane server logs !");
                    reply.put(MomMsgTranslator.MSG_BODY, e.getMessage());
                }
                break;

            case OPERATION_VAL_GETTREEMREI:
                oParam = message.get(TREE_MENU_ENTITY_ID);
                if (oParam != null) {
                    param1 = oParam.toString();
                    try {
                        TreeMenuEntity entity = this.getTreeMenuEntityFromID(param1);
                        if (entity!=null) {
                            ret = TreeMenuEntityJSON.treeMenuEntity2JSON(entity);
                            reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                            reply.put(MomMsgTranslator.MSG_BODY, ret);
                        } else {
                            reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                            reply.put(MomMsgTranslator.MSG_ERR, "TreeMenuEntity not found ( id: " + param1 +" ) !");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                        reply.put(MomMsgTranslator.MSG_ERR, "Unexpected exception... Have a look to this message body and Ariane server logs !");
                        reply.put(MomMsgTranslator.MSG_BODY, e.getMessage());
                    }
                } else {
                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                    reply.put(MomMsgTranslator.MSG_ERR, "Invalid request : no tree menu entity id submitted...");
                }

                break;
            case OPERATION_VAL_GETTREEMREC:
                oParam = message.get(TREE_MENU_ENTITY_CA);
                if (oParam != null) {
                    param1 = oParam.toString();
                    if (!param1.equals("")) {
                        try {
                            TreeMenuEntity entity = this.getTreeMenuEntityFromContextAddress(param1);
                            if (entity!=null) {
                                ret = TreeMenuEntityJSON.treeMenuEntity2JSON(entity);
                                reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                reply.put(MomMsgTranslator.MSG_BODY, ret);
                            } else {
                                reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                                reply.put(MomMsgTranslator.MSG_ERR, "TreeMenuEntity not found ( context address: " + param1 +" ) !");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                            reply.put(MomMsgTranslator.MSG_ERR, "Unexpected exception... Have a look to this message body and Ariane server logs !");
                            reply.put(MomMsgTranslator.MSG_BODY, e.getMessage());
                        }
                    } else {
                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                        reply.put(MomMsgTranslator.MSG_ERR, "Invalid request : no tree menu entity context address submitted...");
                    }
                } else {
                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                    reply.put(MomMsgTranslator.MSG_ERR, "Invalid request : no tree menu entity context address submitted...");
                }
                break;
            case MomMsgTranslator.OPERATION_NOT_DEFINED:
                reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                reply.put(MomMsgTranslator.MSG_ERR, "Operation not defined ! ");
                break;
            default:
                reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                reply.put(MomMsgTranslator.MSG_ERR, "Unknow operation (" + operation + ") !");
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