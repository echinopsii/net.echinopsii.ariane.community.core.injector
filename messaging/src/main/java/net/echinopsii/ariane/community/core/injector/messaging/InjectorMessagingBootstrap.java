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

package net.echinopsii.ariane.community.core.injector.messaging;

import net.echinopsii.ariane.community.core.injector.base.registry.InjectorRegistryFactory;
import net.echinopsii.ariane.community.core.injector.messaging.service.RemoteCacheFactoryService;
import net.echinopsii.ariane.community.core.injector.messaging.service.RemoteComponentService;
import net.echinopsii.ariane.community.core.injector.messaging.service.RemoteGearService;
import net.echinopsii.ariane.community.core.injector.messaging.service.RemoteTreeService;
import net.echinopsii.ariane.community.core.portal.base.plugin.TreeMenuRootsRegistry;
import net.echinopsii.ariane.community.messaging.api.MomClient;
import net.echinopsii.ariane.community.messaging.common.MomClientFactory;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Dictionary;

@Component(managedservice="net.echinopsii.ariane.community.core.InjectorMessagingManagedService")
@Instantiate
public class InjectorMessagingBootstrap {
    private static final Logger log = LoggerFactory.getLogger(InjectorMessagingBootstrap.class);
    private static final String INJECTOR_COMPONENT = "Ariane Injector Messaging Component";

    public static final String PROPS_FIELD_TREE_QUEUE = "remote.injector.tree.queue";
    public static final String PROPS_FIELD_GEAR_QUEUE = "remote.injector.gear.queue";
    public static final String PROPS_FIELD_COMP_QUEUE = "remote.injector.comp.queue";
    public static final String PROPS_FIELD_RCFT_QUEUE = "remote.injector.rcft.queue";

    private static Dictionary conf = null;
    private boolean isStarted = false;

    public static MomClient sharedMoMConnection = null;

    private RemoteCacheFactoryService remoteCacheFactoryService = new RemoteCacheFactoryService();
    private RemoteComponentService remoteComponentService = new RemoteComponentService();
    private RemoteGearService remoteGearService = new RemoteGearService();
    private RemoteTreeService remoteTreeService = new RemoteTreeService();


    @Requires(from="InjectorTreeMenuRootsRegistryImpl")
    private TreeMenuRootsRegistry treeMenuRootsRegistry = null;
    private static TreeMenuRootsRegistry treeMenuRootsRegistrySgt = null;

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
        treeMenuRootsRegistrySgt = null;
    }

    public static TreeMenuRootsRegistry getTreeMenuRootsRegistry() {
        return treeMenuRootsRegistrySgt;
    }

    @Requires
    private InjectorRegistryFactory injectorRegistryFactory = null;
    private static InjectorRegistryFactory injectorRegistryFactorySgt = null;

    @Bind
    public void bindInjectorRegistryFactory(InjectorRegistryFactory r) {
        log.debug("Bound to injector registry factory..." + r.toString());
        injectorRegistryFactory = r;
        injectorRegistryFactorySgt = r;
    }

    @Unbind
    public void unbindInjectorRegistryFactory() {
        log.debug("Unboumd from injector registry factory...");
        injectorRegistryFactory = null;
        injectorRegistryFactorySgt = null;
    }

    public static InjectorRegistryFactory getInjectorRegistryFactory() { return injectorRegistryFactorySgt; }


    @Validate
    public void validate() throws Exception {
        if (!isStarted) {
            while (conf==null)
                Thread.sleep(100);

            try {
                sharedMoMConnection = MomClientFactory.make((String) conf.get(MomClient.MOM_CLI));
            } catch (Exception e) {
                log.error("Error while loading MoM client : " + e.getMessage());
                log.error("Provided MoM client : " + conf.get(MomClient.MOM_CLI));
            }

            try {
                if (sharedMoMConnection!=null)
                    sharedMoMConnection.init(conf);
            } catch (Exception e) {
                System.err.println("Error while initializing MoM client : " + e.getMessage());
                System.err.println("Provided MoM host : " + conf.get(MomClient.MOM_HOST));
                System.err.println("Provided MoM port : " + conf.get(MomClient.MOM_PORT));
                sharedMoMConnection = null;
            }

            if (sharedMoMConnection!=null) {
                isStarted = true;
                remoteCacheFactoryService.start(conf);
                remoteTreeService.start(conf);
                remoteComponentService.start(conf);
                remoteGearService.start(conf);
                log.info("{} is started", new Object[]{INJECTOR_COMPONENT});
            }
        }
    }

    @Invalidate
    public void invalidate() throws Exception {
        remoteComponentService.stop();
        remoteGearService.stop();
        remoteTreeService.stop();
        remoteCacheFactoryService.stop();
        if (InjectorMessagingBootstrap.sharedMoMConnection.isConnected())
            InjectorMessagingBootstrap.sharedMoMConnection.close();
        isStarted = false;
        log.info("{} is stopped", new Object[]{INJECTOR_COMPONENT});
    }

    private static boolean isValid(Dictionary properties) {
        boolean ret = true;

        if (properties.get(MomClient.MOM_CLI)==null || properties.get(MomClient.MOM_CLI).equals("")) {
            log.error(MomClient.MOM_CLI + " is not defined.");
            ret = false;
        }
        if (properties.get(MomClient.MOM_HOST)==null || properties.get(MomClient.MOM_HOST).equals("")) {
            ret = false;
            log.error(MomClient.MOM_HOST + " is not defined.");
        }
        if (properties.get(MomClient.MOM_PORT)==null || properties.get(MomClient.MOM_PORT).equals("")) {
            ret = false;
            log.error(MomClient.MOM_PORT + " is not defined.");
        }
        if (properties.get(MomClient.MOM_USER)==null || properties.get(MomClient.MOM_USER).equals("")) {
            ret = false;
            log.error(MomClient.MOM_USER + " is not defined.");
        }
        if (properties.get(MomClient.MOM_PSWD)==null || properties.get(MomClient.MOM_PSWD).equals("")) {
            ret = false;
            log.error(MomClient.MOM_PSWD + " is not defined.");
        }

        String hostname = null;
        String connectionName;
        try {
            hostname = java.net.InetAddress.getLocalHost().getHostName();
            connectionName = "Ariane Injector Service @ " + hostname;
        } catch (UnknownHostException e) {
            log.warn("Problem while getting hostname : " + e.getCause());
            connectionName = "Ariane Injector Service";
        }
        if (properties.get(MomClient.ARIANE_PGURL_KEY)==null) properties.put(MomClient.ARIANE_PGURL_KEY, "http://"+hostname+":6969/ariane");
        if (properties.get(MomClient.ARIANE_OSI_KEY)==null) properties.put(MomClient.ARIANE_OSI_KEY, hostname);
        if (properties.get(MomClient.ARIANE_APP_KEY)==null) properties.put(MomClient.ARIANE_APP_KEY, "Ariane");
        if (properties.get(MomClient.ARIANE_OTM_KEY)==null) properties.put(MomClient.ARIANE_OTM_KEY, MomClient.ARIANE_OTM_NOT_DEFINED);
        if (properties.get(MomClient.ARIANE_CMP_KEY)==null) properties.put(MomClient.ARIANE_CMP_KEY, "echinopsii");

        String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        properties.put(MomClient.ARIANE_PID_KEY, pid);

        if (ret && !(properties.get(MomClient.MOM_CLI).equals("net.echinopsii.ariane.community.messaging.rabbitmq.Client") ||
                properties.get(MomClient.MOM_CLI).equals("net.echinopsii.ariane.community.messaging.nats.Client"))) {
            ret = false;
            log.error("MoM client implementation not supported yet : {}", new Object[]{properties.get(MomClient.MOM_CLI)});
        } else if (ret && properties.get(MomClient.MOM_CLI).equals("net.echinopsii.ariane.community.messaging.rabbitmq.Client")) {
            if (properties.get(MomClient.RBQ_VERSION_KEY)==null || properties.get(MomClient.RBQ_VERSION_KEY).equals("")) {
                String version = InjectorMessagingBootstrap.class.getPackage().getImplementationVersion();
                if (version!=null) properties.put(MomClient.RBQ_VERSION_KEY, version);
                else {
                    ret = false;
                    log.error(MomClient.RBQ_VERSION_KEY + " is not defined.");
                }
            }
            if (properties.get(MomClient.RBQ_PRODUCT_KEY)==null || properties.get(MomClient.RBQ_PRODUCT_KEY).equals("")) properties.put(MomClient.RBQ_PRODUCT_KEY, "Ariane");
            if (properties.get(MomClient.RBQ_INFORMATION_KEY)==null || properties.get(MomClient.RBQ_INFORMATION_KEY).equals(""))  properties.put(MomClient.RBQ_INFORMATION_KEY, connectionName);
            if (properties.get(MomClient.RBQ_COPYRIGHT_KEY)==null || properties.get(MomClient.RBQ_COPYRIGHT_KEY).equals("")) properties.put(MomClient.RBQ_COPYRIGHT_KEY, "AGPLv3 / Free2Biz");
        } else if (ret && properties.get(MomClient.MOM_CLI).equals("net.echinopsii.ariane.community.messaging.nats.Client")) {
            if (properties.get(MomClient.NATS_CONNECTION_NAME) == null || properties.get(MomClient.NATS_CONNECTION_NAME).equals("")) properties.put(MomClient.NATS_CONNECTION_NAME, connectionName);
        }

        return ret;
    }

    @Updated
    public static void updated(Dictionary properties) {
        if (conf == null && properties.size()>1 && isValid(properties)) {
            log.debug("{} is being updated by {}", new Object[]{INJECTOR_COMPONENT, Thread.currentThread().toString()});
            conf = properties;
        }
    }

    public static Dictionary getConf() {
        return conf;
    }

    public static void setConf(Dictionary conf) {
        InjectorMessagingBootstrap.conf = conf;
    }
}