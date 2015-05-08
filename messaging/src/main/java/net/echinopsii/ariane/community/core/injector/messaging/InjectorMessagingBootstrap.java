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

import net.echinopsii.ariane.community.core.injector.messaging.service.RemoteComponentService;
import net.echinopsii.ariane.community.core.injector.messaging.service.RemoteGearService;
import net.echinopsii.ariane.community.core.injector.messaging.service.RemoteTreeService;
import net.echinopsii.ariane.community.core.portal.base.plugin.TreeMenuRootsRegistry;
import net.echinopsii.ariane.community.core.messaging.api.MomClient;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;

@Component(managedservice="net.echinopsii.ariane.community.core.InjectorMessagingManagedService")
@Instantiate
public class InjectorMessagingBootstrap {
    private static final Logger log = LoggerFactory.getLogger(InjectorMessagingBootstrap.class);
    private static final String INJECTOR_COMPONENT = "Ariane Injector Messaging Component";

    public static final String PROPS_FIELD_TREE_QUEUE = "remote.injector.tree.queue";
    public static final String PROPS_FIELD_GEAR_QUEUE = "remote.injector.gear.queue";
    public static final String PROPS_FIELD_COMP_QUEUE = "remote.injector.comp.queue";

    private static Dictionary conf = null;
    private boolean isStarted = false;

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
    }

    public static TreeMenuRootsRegistry getTreeMenuRootsRegistry() {
        return treeMenuRootsRegistrySgt;
    }

    @Validate
    public void validate() throws Exception {
        if (!isStarted) {
            while (conf==null)
                Thread.sleep(100);

            isStarted=true;

            remoteComponentService.start(conf);
            remoteGearService.start(conf);
            remoteTreeService.start(conf);

            log.info("{} is started", new Object[]{INJECTOR_COMPONENT});
        }
    }

    @Invalidate
    public void invalidate() throws Exception {
        remoteComponentService.stop();
        remoteGearService.stop();
        remoteTreeService.stop();
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

        if (ret && !properties.get(MomClient.MOM_CLI).equals("net.echinopsii.ariane.community.core.messaging.rabbitmq.Client")) {
            ret = false;
            log.error("MoM client implementation not supported yet : {}", new Object[]{properties.get(MomClient.MOM_CLI)});
        } else if (ret) {
            if (properties.get(MomClient.RBQ_VERSION_KEY)==null || properties.get(MomClient.RBQ_VERSION_KEY).equals("")) {
                ret = false;
                log.error(MomClient.RBQ_VERSION_KEY + " is not defined.");
            }
            if (properties.get(MomClient.RBQ_PRODUCT_KEY)==null || properties.get(MomClient.RBQ_PRODUCT_KEY).equals("")) {
                ret = false;
                log.error(MomClient.RBQ_PRODUCT_KEY + " is not defined.");
            }
            if (properties.get(MomClient.RBQ_INFORMATION_KEY)==null || properties.get(MomClient.RBQ_INFORMATION_KEY).equals("")) {
                ret = false;
                log.error(MomClient.RBQ_INFORMATION_KEY + " is not defined.");
            }
            if (properties.get(MomClient.RBQ_COPYRIGHT_KEY)==null || properties.get(MomClient.RBQ_COPYRIGHT_KEY).equals("")) {
                ret = false;
                log.error(MomClient.RBQ_COPYRIGHT_KEY + " is not defined.");
            }
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
}