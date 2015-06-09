/**
 * Injector Messaging Module
 * Remote Gear Injector Messaging worker
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

import net.echinopsii.ariane.community.core.injector.base.registry.InjectorGearsRegistry;
import net.echinopsii.ariane.community.core.injector.messaging.InjectorMessagingBootstrap;
import net.echinopsii.ariane.community.core.injector.messaging.worker.json.RemoteGearJSON;
import net.echinopsii.ariane.community.core.injector.messaging.worker.model.RemoteGear;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class RemoteGearWorker implements AppMsgWorker {
    private static final Logger log = LoggerFactory.getLogger(RemoteGearWorker.class);

    public final static String OPERATION_PUSH_GEAR_IN_CACHE = "PUSH_GEAR_IN_CACHE";
    public final static String OPERATION_DEL_GEAR_FROM_CACHE = "DEL_GEAR_FROM_CACHE";

    public final static String REMOTE_GEAR = "REMOTE_GEAR";


    @Override
    public Map<String, Object> apply(Map<String, Object> message) {
        log.debug("Remote Injector Gear Worker on  : {" + message.toString() +  " }...");
        Map<String, Object> reply = new HashMap<String, Object>();

        Object oOperation = message.get(RemoteWorkerCommon.OPERATION_FDN);
        String operation = null;

        Object oCacheID = null;
        String cacheID = null;

        Object oGearJSON = null;


        if (oOperation==null)
            operation = RemoteWorkerCommon.OPERATION_NOT_DEFINED;
        else
            operation = oOperation.toString();

        switch (operation) {
            case OPERATION_PUSH_GEAR_IN_CACHE:
                oCacheID = message.get(RemoteWorkerCommon.CACHE_ID);
                if (oCacheID!=null) {
                    InjectorGearsRegistry gearsRegistry = InjectorMessagingBootstrap.getInjectorRegistryFactory().getGearsRegistry(oCacheID.toString());
                    if ( gearsRegistry != null) {
                        if (gearsRegistry.isStarted()) {
                            oGearJSON = message.get(REMOTE_GEAR);
                            if (oGearJSON!=null) {
                                try {
                                    RemoteGear remoteGear = RemoteGearJSON.JSON2RemoteGear(oGearJSON.toString());
                                    RemoteGear registeredGear = (RemoteGear)gearsRegistry.getEntityFromCache(remoteGear.getGearId());
                                    if (registeredGear!=null) {
                                        if (remoteGear.getGearName()!=null) registeredGear.setGearName(remoteGear.getGearName());
                                        if (remoteGear.getGearDescription()!=null) registeredGear.setGearDescription(remoteGear.getGearDescription());
                                        if (remoteGear.getGearAdminQueue()!=null) registeredGear.setGearAdminQueue(remoteGear.getGearAdminQueue());
                                        if (remoteGear.getSleepingPeriod()<=0) registeredGear.setSleepingPeriod(remoteGear.getSleepingPeriod());
                                        registeredGear.setRunning(remoteGear.isRunning());
                                        reply.put(RemoteWorkerCommon.REPLY_RC, 0);
                                        reply.put(MomMsgTranslator.MSG_BODY, "Remote Gear " + remoteGear.getGearName() + " successfully updated on registry " + oCacheID.toString());
                                    } else {
                                        gearsRegistry.putEntityToCache(remoteGear);
                                        reply.put(RemoteWorkerCommon.REPLY_RC, 0);
                                        reply.put(MomMsgTranslator.MSG_BODY, "Remote Gear " + remoteGear.getGearName() + " successfully pushed to registry " + oCacheID.toString());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                                    reply.put(RemoteWorkerCommon.REPLY_MSG, "Remote Gear serialization problem... Have a look to this message body and Ariane server logs ! ");
                                    reply.put(MomMsgTranslator.MSG_BODY, e.getMessage());
                                }
                            } else {
                                reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                                reply.put(RemoteWorkerCommon.REPLY_MSG, "Gear to push is not defined ! ");
                            }
                        } else {
                            reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                            reply.put(RemoteWorkerCommon.REPLY_MSG, "Registry from cache ID " + oCacheID.toString() + " is not started ! ");
                        }
                    } else {
                        reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                        reply.put(RemoteWorkerCommon.REPLY_MSG, "Unable to retrieve registry from cache ID " + oCacheID.toString() + " ! ");
                    }
                } else {
                    reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                    reply.put(RemoteWorkerCommon.REPLY_MSG, "Cache ID is not defined ! ");
                }
                break;
            case OPERATION_DEL_GEAR_FROM_CACHE:
                oCacheID = message.get(RemoteWorkerCommon.CACHE_ID);
                if (oCacheID!=null) {
                    InjectorGearsRegistry gearsRegistry = InjectorMessagingBootstrap.getInjectorRegistryFactory().getGearsRegistry(oCacheID.toString());
                    if ( gearsRegistry != null) {
                        if (gearsRegistry.isStarted()) {
                            oGearJSON = message.get(REMOTE_GEAR);
                            if (oGearJSON!=null) {
                                try {
                                    RemoteGear remoteGear = RemoteGearJSON.JSON2RemoteGear(oGearJSON.toString());
                                    if (gearsRegistry.getEntityFromCache(remoteGear.getGearId())!=null) {
                                        gearsRegistry.removeEntityFromCache(remoteGear);
                                        reply.put(RemoteWorkerCommon.REPLY_RC, 0);
                                        reply.put(MomMsgTranslator.MSG_BODY, "Remote Gear "+ remoteGear.getGearName() +" successfully deleted from registry " + oCacheID.toString());
                                    }
                                    else {
                                        reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                                        reply.put(RemoteWorkerCommon.REPLY_MSG, "Remote Gear "+ remoteGear.getGearName() +" doesn't exists on cache " + oCacheID.toString() + "  ! ");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                                    reply.put(RemoteWorkerCommon.REPLY_MSG, "Remote Gear serialization problem... Have a look to this message body and Ariane server logs ! ");
                                    reply.put(MomMsgTranslator.MSG_BODY, e.getMessage());
                                }
                            } else {
                                reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                                reply.put(RemoteWorkerCommon.REPLY_MSG, "Gear to delete is not defined ! ");
                            }
                        } else {
                            reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                            reply.put(RemoteWorkerCommon.REPLY_MSG, "Registry from cache ID " + oCacheID.toString() + " is not started ! ");
                        }
                    } else {
                        reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                        reply.put(RemoteWorkerCommon.REPLY_MSG, "Unable to retrieve registry from cache ID " + oCacheID.toString() + " ! ");
                    }
                } else {
                    reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                    reply.put(RemoteWorkerCommon.REPLY_MSG, "Cache ID is not defined ! ");
                }
                break;
            case RemoteWorkerCommon.OPERATION_NOT_DEFINED:
                reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                reply.put(RemoteWorkerCommon.REPLY_MSG, "Operation is not defined ! ");
                break;
            default:
                reply.put(RemoteWorkerCommon.REPLY_RC, 1);
                reply.put(RemoteWorkerCommon.REPLY_MSG, "Unknown operation "+operation+" ! ");
                break;
        }
        return reply;
    }
}