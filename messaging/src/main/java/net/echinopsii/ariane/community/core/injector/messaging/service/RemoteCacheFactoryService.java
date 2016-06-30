/**
 *
 *
 * Copyright (C) 2015 mffrench
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
import net.echinopsii.ariane.community.core.injector.messaging.worker.RemoteCacheFactoryWorker;
import net.echinopsii.ariane.community.messaging.api.MomClient;
import net.echinopsii.ariane.community.messaging.common.MomClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;

public class RemoteCacheFactoryService {

    private static final Logger log = LoggerFactory.getLogger(RemoteCacheFactoryService.class);

    private MomClient client = null;
    private static String ricfQueue = "ARIANE_INJECTOR_REMOTE_CACHEFACTORY_Q";

    public void start(Dictionary properties) {
        try {
            client = MomClientFactory.make((String) properties.get(MomClient.MOM_CLI));
        } catch (Exception e) {
            log.error("Error while loading MoM client : " + e.getMessage());
            log.error("Provided MoM client : " + properties.get(MomClient.MOM_CLI));
            return;
        }

        try {
            String hostname =  java.net.InetAddress.getLocalHost().getHostName();
            if (properties.get(MomClient.ARIANE_PGURL_KEY)==null) properties.put(MomClient.ARIANE_PGURL_KEY, "http://"+hostname+":6969/ariane");
            if (properties.get(MomClient.ARIANE_OSI_KEY)==null) properties.put(MomClient.ARIANE_OSI_KEY, hostname);
            if (properties.get(MomClient.ARIANE_APP_KEY)==null) properties.put(MomClient.ARIANE_APP_KEY, "Ariane");
            if (properties.get(MomClient.ARIANE_OTM_KEY)==null) properties.put(MomClient.ARIANE_OTM_KEY, MomClient.ARIANE_OTM_NOT_DEFINED);
            if (properties.get(MomClient.ARIANE_CMP_KEY)==null) properties.put(MomClient.ARIANE_CMP_KEY, "echinopsii");

            String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            properties.put(MomClient.ARIANE_PID_KEY, pid);
            client.init(properties);
        } catch (Exception e) {
            log.error("Error while initializing MoM client : " + e.getMessage());
            log.error("Provided MoM host : " + properties.get(MomClient.MOM_HOST));
            log.error("Provided MoM port : " + properties.get(MomClient.MOM_PORT));
            client = null;
            return;
        }

        if (properties.get(InjectorMessagingBootstrap.PROPS_FIELD_TREE_QUEUE)!=null)
            ricfQueue = (String) properties.get(InjectorMessagingBootstrap.PROPS_FIELD_TREE_QUEUE);

        client.getServiceFactory().requestService(ricfQueue, new RemoteCacheFactoryWorker());
        log.info("Ariane Injector Remote Cache Factory Messaging Service is waiting message on  " + ricfQueue + "...");
    }

    public void stop() throws Exception {
        log.info("Stop Injector Remote Cache Factory Messaging Service ...");
        if (client!=null)
            client.close();
    }
}
