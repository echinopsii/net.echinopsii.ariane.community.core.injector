/**
 * Injector Messaging Module
 * Remote Component Injector Messaging worker
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

import net.echinopsii.ariane.community.core.injector.base.registry.InjectorComponentsRegistry;
import net.echinopsii.ariane.community.core.injector.messaging.InjectorMessagingBootstrap;
import net.echinopsii.ariane.community.core.injector.messaging.worker.json.RemoteComponentJSON;
import net.echinopsii.ariane.community.core.injector.messaging.worker.model.RemoteComponent;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import net.echinopsii.ariane.community.messaging.api.MomServiceFactory;
import net.echinopsii.ariane.community.messaging.common.MomAkkaAbsAppHPMsgSrvWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RemoteComponentWorker extends MomAkkaAbsAppHPMsgSrvWorker {
    private static final Logger log = LoggerFactory.getLogger(RemoteComponentWorker.class);

    public final static String OPERATION_PUSH_COMPONENT_IN_CACHE = "PUSH_COMPONENT_IN_CACHE";
    public final static String OPERATION_DEL_COMPONENT_FROM_CACHE = "DEL_COMPONENT_FROM_CACHE";
    public final static String OPERATION_PULL_COMPONENT_FROM_CACHE = "PULL_COMPONENT_FROM_CACHE";
    public final static String OPERATION_COUNT_COMPONENTS_FROM_CACHE = "COUNT_COMPONENTS_CACHE";

    public final static String REMOTE_COMPONENT = "REMOTE_COMPONENT";

    public RemoteComponentWorker(MomServiceFactory serviceFactory) {
        super(serviceFactory);
    }

    @Override
    public Map<String, Object> apply(Map<String, Object> message) {
        log.debug("Remote Injector Component Worker on  : {" + message.toString() +  " }...");
        Map<String, Object> reply = super.apply(message);
        if (reply!=null) return reply;
        else reply = new HashMap<>();
        Object oOperation = message.get(MomMsgTranslator.OPERATION_FDN);
        String operation = null;

        Object oCacheID = null;
        String cacheID = null;

        Object oComponentJSON = null;

        String ret;

        if (oOperation==null)
            operation = MomMsgTranslator.OPERATION_NOT_DEFINED;
        else
            operation = oOperation.toString();

        switch (operation) {
            case OPERATION_PUSH_COMPONENT_IN_CACHE:
                oCacheID = message.get(RemoteWorkerCommon.CACHE_ID);
                if (oCacheID!=null) {
                    InjectorComponentsRegistry componentsRegistry =  InjectorMessagingBootstrap.getInjectorRegistryFactory().getComponentsRegistry(oCacheID.toString());
                    if (componentsRegistry!=null) {
                        if (componentsRegistry.isStarted()) {
                            oComponentJSON = message.get(REMOTE_COMPONENT);
                            if (oComponentJSON != null) {
                                try {
                                    RemoteComponent remoteComponent = RemoteComponentJSON.JSON2RemoteComponent(oComponentJSON.toString());
                                    RemoteComponent registeredComponent = (RemoteComponent)componentsRegistry.getEntityFromCache(remoteComponent.getComponentId());
                                    if (registeredComponent!=null) {
                                        if (remoteComponent.getComponentName()!=null) registeredComponent.setComponentName(remoteComponent.getComponentName());
                                        if (remoteComponent.getComponentType()!=null) registeredComponent.setComponentType(remoteComponent.getComponentType());
                                        registeredComponent.setRefreshing(remoteComponent.isRefreshing());
                                        if (remoteComponent.getJsonLastRefresh()!=null) registeredComponent.setJsonLastRefresh(remoteComponent.getJsonLastRefresh());
                                        if (remoteComponent.getLastRefreshDuration()!=null) registeredComponent.setLastRefreshDuration(remoteComponent.getLastRefreshDuration());
                                        if (remoteComponent.getNextAction()!=0) registeredComponent.setNextAction(remoteComponent.getNextAction());
                                        if (remoteComponent.getComponentAdminQueue()!=null) registeredComponent.setComponentAdminQueue(remoteComponent.getComponentAdminQueue());
                                        registeredComponent.setComponentBlob(new String((byte[]) message.get(MomMsgTranslator.MSG_BODY), StandardCharsets.UTF_8));
                                        componentsRegistry.putEntityToCache(registeredComponent);
                                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                        reply.put(MomMsgTranslator.MSG_BODY, "Remote Component " + remoteComponent.getComponentName() + " successfully updated on registry " + oCacheID.toString());
                                    } else {
                                        remoteComponent.setComponentBlob(new String((byte[]) message.get(MomMsgTranslator.MSG_BODY), StandardCharsets.UTF_8));
                                        componentsRegistry.putEntityToCache(remoteComponent);
                                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                        reply.put(MomMsgTranslator.MSG_BODY, "Remote Component " + remoteComponent.getComponentName() + " successfully pushed to registry " + oCacheID.toString());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                    reply.put(MomMsgTranslator.MSG_ERR, "Remote Component serialization problem... Have a look to Ariane server logs ! ");
                                    reply.put(MomMsgTranslator.MSG_BODY, e.getMessage());
                                }
                            } else {
                                reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                reply.put(MomMsgTranslator.MSG_ERR, "Component to push is not defined ! ");
                            }
                        } else {
                            reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                            reply.put(MomMsgTranslator.MSG_ERR, "Registry from cache ID " + oCacheID.toString() + " is not started ! ");
                        }
                    } else {
                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                        reply.put(MomMsgTranslator.MSG_ERR, "Unable to retrieve registry from cache ID " + oCacheID.toString() + " ! ");
                    }
                } else {
                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                    reply.put(MomMsgTranslator.MSG_ERR, "Cache ID is not defined ! ");
                }
                break;
            case OPERATION_DEL_COMPONENT_FROM_CACHE:
                oCacheID = message.get(RemoteWorkerCommon.CACHE_ID);
                if (oCacheID!=null) {
                    InjectorComponentsRegistry componentsRegistry =  InjectorMessagingBootstrap.getInjectorRegistryFactory().getComponentsRegistry(oCacheID.toString());
                    if (componentsRegistry!=null) {
                        if (componentsRegistry.isStarted()) {
                            oComponentJSON = message.get(REMOTE_COMPONENT);
                            if (oComponentJSON != null) {
                                try {
                                    RemoteComponent remoteComponent = RemoteComponentJSON.JSON2RemoteComponent(oComponentJSON.toString());
                                    RemoteComponent registeredComponent = (RemoteComponent)componentsRegistry.getEntityFromCache(remoteComponent.getComponentId());
                                    if (registeredComponent!=null) {
                                        componentsRegistry.removeEntityFromCache(registeredComponent);
                                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                        reply.put(MomMsgTranslator.MSG_BODY, "Remote Component " + remoteComponent.getComponentName() + " successfully deleted from registry " + oCacheID.toString());
                                    } else {
                                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                                        reply.put(MomMsgTranslator.MSG_ERR, "Remote Component "+ remoteComponent.getComponentId() +" doesn't exists on cache " + oCacheID.toString() + "  ! ");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                    reply.put(MomMsgTranslator.MSG_ERR, "Remote Component serialization problem... Have a look to message body and Ariane server logs ! ");
                                    reply.put(MomMsgTranslator.MSG_BODY, e.getMessage());
                                }
                            } else {
                                reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                reply.put(MomMsgTranslator.MSG_ERR, "Component to delete is not defined ! ");
                            }
                        } else {
                            reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                            reply.put(MomMsgTranslator.MSG_ERR, "Registry from cache ID " + oCacheID.toString() + " is not started ! ");
                        }
                    } else {
                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                        reply.put(MomMsgTranslator.MSG_ERR, "Unable to retrieve registry from cache ID " + oCacheID.toString() + " ! ");
                    }
                } else {
                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                    reply.put(MomMsgTranslator.MSG_ERR, "Cache ID is not defined ! ");
                }
                break;
            case OPERATION_PULL_COMPONENT_FROM_CACHE:
                oCacheID = message.get(RemoteWorkerCommon.CACHE_ID);
                if (oCacheID!=null) {
                    InjectorComponentsRegistry componentsRegistry =  InjectorMessagingBootstrap.getInjectorRegistryFactory().getComponentsRegistry(oCacheID.toString());
                    if (componentsRegistry!=null) {
                        if (componentsRegistry.isStarted()) {
                            oComponentJSON = message.get(REMOTE_COMPONENT);
                            if (oComponentJSON != null) {
                                try {
                                    RemoteComponent remoteComponent = RemoteComponentJSON.JSON2RemoteComponent(oComponentJSON.toString());
                                    RemoteComponent registeredComponent = (RemoteComponent)componentsRegistry.getEntityFromCache(remoteComponent.getComponentId());
                                    if (registeredComponent!=null) {
                                        ret = RemoteComponentJSON.remoteComponent2JSON(registeredComponent);
                                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                                        reply.put(MomMsgTranslator.MSG_PROPERTIES, ret);
                                        reply.put(MomMsgTranslator.MSG_BODY, registeredComponent.getComponentBlob());
                                    } else {
                                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                                        reply.put(MomMsgTranslator.MSG_ERR, "Remote Component "+ remoteComponent.getComponentId() +" doesn't exists on cache " + oCacheID.toString() + "  ! ");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                    reply.put(MomMsgTranslator.MSG_ERR, "Remote Component serialization problem... Have a look to message body and Ariane server logs ! ");
                                    reply.put(MomMsgTranslator.MSG_BODY, e.getMessage());
                                }
                            } else {
                                reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                                reply.put(MomMsgTranslator.MSG_ERR, "Component to delete is not defined ! ");
                            }
                        } else {
                            reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SERVER_ERR);
                            reply.put(MomMsgTranslator.MSG_ERR, "Registry from cache ID " + oCacheID.toString() + " is not started ! ");
                        }
                    } else {
                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                        reply.put(MomMsgTranslator.MSG_ERR, "Unable to retrieve registry from cache ID " + oCacheID.toString() + " ! ");
                    }
                } else {
                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                    reply.put(MomMsgTranslator.MSG_ERR, "Cache ID is not defined ! ");
                }
                break;
            case OPERATION_COUNT_COMPONENTS_FROM_CACHE:
                oCacheID = message.get(RemoteWorkerCommon.CACHE_ID);
                if (oCacheID!=null) {
                    InjectorComponentsRegistry componentsRegistry =  InjectorMessagingBootstrap.getInjectorRegistryFactory().getComponentsRegistry(oCacheID.toString());
                    if (componentsRegistry!=null) {
                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_SUCCESS);
                        reply.put(MomMsgTranslator.MSG_BODY, String.valueOf(componentsRegistry.size()));

                    } else {
                        reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_NOT_FOUND);
                        reply.put(MomMsgTranslator.MSG_ERR, "Unable to retrieve registry from cache ID " + oCacheID.toString() + " ! ");
                    }
                } else {
                    reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                    reply.put(MomMsgTranslator.MSG_ERR, "Cache ID is not defined ! ");
                }
                break;
            case MomMsgTranslator.OPERATION_NOT_DEFINED:
                reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                reply.put(MomMsgTranslator.MSG_ERR, "Operation is not defined ! ");
                break;
            default:
                reply.put(MomMsgTranslator.MSG_RC, MomMsgTranslator.MSG_RET_BAD_REQ);
                reply.put(MomMsgTranslator.MSG_ERR, "Unknown operation (" + operation + ") ! ");
                break;
        }


        return reply;
    }
}