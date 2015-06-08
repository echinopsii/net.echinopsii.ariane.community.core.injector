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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RemoteComponentWorker implements AppMsgWorker {
    private static final Logger log = LoggerFactory.getLogger(RemoteComponentWorker.class);

    public final static String OPERATION_PUSH_COMPONENT_IN_CACHE = "PUSH_GEAR_IN_CACHE";
    public final static String OPERATION_DEL_COMPONENT_FROM_CACHE = "DEL_GEAR_FROM_CACHE";

    public final static String REMOTE_COMPONENT = "REMOTE_COMPONENT";

    @Override
    public Map<String, Object> apply(Map<String, Object> message) {
        log.debug("Remote Injector Component Worker on  : {" + message.toString() +  " }...");
        Map<String, Object> reply = new HashMap<String, Object>();
        Object oOperation = message.get(RemoteWorkerCommon.OPERATION_FDN);
        String operation = null;

        Object oCacheID = null;
        String cacheID = null;

        Object oComponentJSON = null;

        if (oOperation==null)
            operation = RemoteWorkerCommon.OPERATION_NOT_DEFINED;
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
                                    RemoteComponent remoteComponent = RemoteComponentJSON.JSON2RemoteComoonent(oComponentJSON.toString());
                                    RemoteComponent registeredComponent = (RemoteComponent)componentsRegistry.getEntityFromCache(remoteComponent.getComponentId());
                                    if (registeredComponent!=null) {
                                        registeredComponent.setComponentName(remoteComponent.getComponentName());
                                        registeredComponent.setComponentType(remoteComponent.getComponentType());
                                        registeredComponent.setRefreshing(remoteComponent.isRefreshing());
                                        registeredComponent.setLastRefresh(remoteComponent.getLastRefresh());
                                        registeredComponent.setNextAction(remoteComponent.getNextAction());
                                        registeredComponent.setComponentAdminQueue(remoteComponent.getComponentAdminQueue());
                                        reply.put(RemoteWorkerCommon.REPLY_RC, 0);
                                        reply.put(MomMsgTranslator.MSG_BODY, "Remote Component " + remoteComponent.getComponentName() + " successfully updated on registry " + oCacheID.toString());
                                    } else {
                                        componentsRegistry.putEntityToCache(remoteComponent);
                                        reply.put(RemoteWorkerCommon.REPLY_RC, 0);
                                        reply.put(MomMsgTranslator.MSG_BODY, "Remote Component " + remoteComponent.getComponentName() + " successfully pushed to registry " + oCacheID.toString());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                                    reply.put(MomMsgTranslator.MSG_BODY, "Remote Component serialization problem... Have a look to Ariane server logs ! ");
                                }
                            } else {
                                reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                                reply.put(MomMsgTranslator.MSG_BODY, "Component to push is not defined ! ");
                            }
                        } else {
                            reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                            reply.put(MomMsgTranslator.MSG_BODY, "Registry from cache ID " + oCacheID.toString() + " is not started ! ");
                        }
                    } else {
                        reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                        reply.put(MomMsgTranslator.MSG_BODY, "Unable to retrieve registry from cache ID " + oCacheID.toString() + " ! ");
                    }
                } else {
                    reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                    reply.put(MomMsgTranslator.MSG_BODY, "Cache ID is not defined ! ");
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
                                    RemoteComponent remoteComponent = RemoteComponentJSON.JSON2RemoteComoonent(oComponentJSON.toString());
                                    RemoteComponent registeredComponent = (RemoteComponent)componentsRegistry.getEntityFromCache(remoteComponent.getComponentId());
                                    if (registeredComponent!=null) {
                                        componentsRegistry.removeEntityFromCache(registeredComponent);
                                        reply.put(RemoteWorkerCommon.REPLY_RC, 0);
                                        reply.put(MomMsgTranslator.MSG_BODY, "Remote Component " + remoteComponent.getComponentName() + " successfully deleted from registry " + oCacheID.toString());
                                    } else {
                                        reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                                        reply.put(MomMsgTranslator.MSG_BODY, "Remote Component "+ remoteComponent.getComponentId() +" doesn't exists on cache " + oCacheID.toString() + "  ! ");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                                    reply.put(MomMsgTranslator.MSG_BODY, "Remote Component serialization problem... Have a look to Ariane server logs ! ");
                                }
                            } else {
                                reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                                reply.put(MomMsgTranslator.MSG_BODY, "Component to delete is not defined ! ");
                            }
                        } else {
                            reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                            reply.put(MomMsgTranslator.MSG_BODY, "Registry from cache ID " + oCacheID.toString() + " is not started ! ");
                        }
                    } else {
                        reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                        reply.put(MomMsgTranslator.MSG_BODY, "Unable to retrieve registry from cache ID " + oCacheID.toString() + " ! ");
                    }
                } else {
                    reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                    reply.put(MomMsgTranslator.MSG_BODY, "Cache ID is not defined ! ");
                }
                break;
            case RemoteWorkerCommon.OPERATION_NOT_DEFINED:
                reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                reply.put(MomMsgTranslator.MSG_BODY, "Operation is not defined ! ");
                break;
            default:
                reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                reply.put(MomMsgTranslator.MSG_BODY, "Unknown operation ! ");
                break;
        }


        return reply;
    }
}