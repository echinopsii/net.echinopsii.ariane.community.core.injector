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
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RemoteGearWorker implements AppMsgWorker {
    private static final Logger log = LoggerFactory.getLogger(RemoteGearWorker.class);

    public final static String OPERATION_ADD_GEAR_IN_CACHE = "ADD_GEAR_IN_CACHE";
    public final static String OPERATION_RM_GEAR_FROM_CACHE = "RM_GEAR_IN_CACHE";
    public final static String OPERATION_GET_GEARS_FROM_CACHE = "GET_GEARS_FROM_CACHE";


    @Override
    public Map<String, Object> apply(Map<String, Object> message) {
        log.debug("Remote Injector Gear Worker on  : {" + message.toString() +  " }...");
        Map<String, Object> reply = new HashMap<String, Object>();

        Object oOperation = message.get(RemoteWorkerCommon.OPERATION_FDN);
        String operation = null;

        Object oCacheID = null;
        String cacheID = null;

        if (oOperation==null)
            operation = RemoteWorkerCommon.OPERATION_NOT_DEFINED;
        else
            operation = oOperation.toString();

        switch (operation) {
            case OPERATION_ADD_GEAR_IN_CACHE:
                oCacheID = message.get(RemoteWorkerCommon.CACHE_ID);
                if (oCacheID!=null) {
                    InjectorGearsRegistry gearsRegistry = InjectorMessagingBootstrap.getInjectorRegistryFactory().getGearsRegistry(oCacheID.toString());
                    if ( gearsRegistry != null) {
                        if (gearsRegistry.isStarted()) {

                        } else {

                        }
                    } else {

                    }
                } else {

                }
                break;
            case OPERATION_RM_GEAR_FROM_CACHE:
                oCacheID = message.get(RemoteWorkerCommon.CACHE_ID);
                if (oCacheID!=null) {
                    InjectorGearsRegistry gearsRegistry = InjectorMessagingBootstrap.getInjectorRegistryFactory().getGearsRegistry(oCacheID.toString());
                    if ( gearsRegistry != null) {
                        if (gearsRegistry.isStarted()) {

                        } else {

                        }
                    } else {

                    }
                } else {

                }
                break;
            case OPERATION_GET_GEARS_FROM_CACHE:
                oCacheID = message.get(RemoteWorkerCommon.CACHE_ID);
                if (oCacheID!=null) {
                    InjectorGearsRegistry gearsRegistry = InjectorMessagingBootstrap.getInjectorRegistryFactory().getGearsRegistry(oCacheID.toString());
                    if ( gearsRegistry != null) {
                        if (gearsRegistry.isStarted()) {

                        } else {

                        }
                    } else {

                    }
                } else {

                }
                break;
            case RemoteWorkerCommon.OPERATION_NOT_DEFINED:
                break;
            default:
                break;
        }

        reply.put(MomMsgTranslator.MSG_BODY, "DONE");
        return reply;
    }
}