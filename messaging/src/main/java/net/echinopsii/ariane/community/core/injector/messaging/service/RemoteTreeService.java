/**
 * Injector Messaging Module
 * Remote Injector Tree Messaging Service
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

package net.echinopsii.ariane.community.core.injector.messaging.service;

import net.echinopsii.ariane.community.core.injector.messaging.InjectorMessagingBootstrap;
import net.echinopsii.ariane.community.core.injector.messaging.worker.RemoteTreeWorker;
import net.echinopsii.ariane.community.messaging.api.MomClient;
import net.echinopsii.ariane.community.messaging.common.MomClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;

public class RemoteTreeService {
    private static final Logger log = LoggerFactory.getLogger(RemoteTreeService.class);

    private static String    ritQueue = "ARIANE_INJECTOR_REMOTE_TREE_Q";

    public void start(Dictionary properties) {
        if (properties.get(InjectorMessagingBootstrap.PROPS_FIELD_TREE_QUEUE)!=null)
            ritQueue = (String) properties.get(InjectorMessagingBootstrap.PROPS_FIELD_TREE_QUEUE);

        InjectorMessagingBootstrap.sharedMoMConnection.getServiceFactory().msgGroupRequestService(
                ritQueue, new RemoteTreeWorker(InjectorMessagingBootstrap.sharedMoMConnection.getServiceFactory())
        );
        log.info("Ariane Injector Remote Tree Messaging Service is waiting message on  " + ritQueue + "...");
    }

    public void stop() throws Exception {
        log.info("Stop Injector Remote Tree Messaging service ...");
    }
}
