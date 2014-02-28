/**
 * Injector Commons JSF bundle
 * Root Injector Registry consumer singleton
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

package com.spectral.cc.core.injector.wat.consumer;

import com.spectral.cc.core.portal.base.registry.TreeMenuRootsRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * iPojo singleton which consume the injector roots tree registry service<br/>
 * Instantiated during injector commons-jsf bundle startup. FactoryMethod : getInstance
 */
@Component(publicFactory = false, factoryMethod = "getInstance")
@Instantiate
public class InjectorTreeMenuRootsRegistryServiceConsumer {
    private static final Logger log = LoggerFactory.getLogger(InjectorTreeMenuRootsRegistryServiceConsumer.class);
    private static InjectorTreeMenuRootsRegistryServiceConsumer INSTANCE;

    @Requires(from="InjectorTreeMenuRootsRegistryImpl")
    private TreeMenuRootsRegistry treeMenuRootsRegistry = null;

    @Bind
    public void bindTreeMenuRootsRegistry(TreeMenuRootsRegistry r) {
        log.debug("Bound to injector tree menu roots registry...");
        treeMenuRootsRegistry = r;
    }

    @Unbind
    public void unbindTreeMenuRootsRegistry() {
        log.debug("Unbound from injector tree menu roots registry...");
        treeMenuRootsRegistry = null;
    }


    /**
     * Get binded injector roots tree registry service consumer
     *
     * @return injector roots tree registry service consumer binded on this consumer. If null no registry has been binded ...
     */
    public TreeMenuRootsRegistry getTreeMenuRootsRegistry() {
        return treeMenuRootsRegistry;
    }

    /**
     * Factory method for this singleton
     *
     * @return instantiated injector roots tree registry service consumer
     */
    public static InjectorTreeMenuRootsRegistryServiceConsumer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InjectorTreeMenuRootsRegistryServiceConsumer();
        }
        return INSTANCE;
    }
}