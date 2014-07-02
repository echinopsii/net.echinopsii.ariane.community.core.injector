/**
 * Injector wat
 * Injector plugin faces managed bean registry consumer singleton
 * Copyright (C) 2014 Mathilde Ffrench
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

package net.echinopsii.ariane.community.core.injector.wat.consumer;

import net.echinopsii.ariane.community.core.portal.base.plugin.FacesMBeanRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * iPojo singleton which consume the plugin faces mbean registry implemented by PortalPluginFacesMBearRegistryImpl.<br/>
 * Instantiated during directory wat bundle startup. FactoryMethod : getInstance
 */
@Component(publicFactory = false, factoryMethod = "getInstance")
@Instantiate
public class InjectorFacesMBeanRegistryConsumer {
    private static final Logger log = LoggerFactory.getLogger(InjectorFacesMBeanRegistryConsumer.class);
    private static InjectorFacesMBeanRegistryConsumer INSTANCE;

    @Requires(from="ArianePortalFacesMBeanRegistry")
    private FacesMBeanRegistry portalFacesMBeanRegistry = null;

    @Bind
    public void bindPortalFacesMBeanRegistry(FacesMBeanRegistry r) {
        log.debug("Bound to ariane portal faces managed bean registry...");
        portalFacesMBeanRegistry = r;
    }

    @Unbind
    public void unbindPortalFacesMBeanRegistry() {
        log.debug("Unbound from ariane portal faces managed bean registry...");
        portalFacesMBeanRegistry = null;
    }

    /**
     * Get portal plugin faces managed bean registry
     *
     * @return the binded portal plugin faces managed bean registry. null if unbinded.
     */
    public FacesMBeanRegistry getPortalPluginFacesMBeanRegistry() {
        return portalFacesMBeanRegistry;
    }

    /**
     * Factory method for this singleton...
     *
     * @return instantiated directory plugin faces mbean registry consumer
     */
    public static InjectorFacesMBeanRegistryConsumer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InjectorFacesMBeanRegistryConsumer();
        }
        return INSTANCE;
    }
}