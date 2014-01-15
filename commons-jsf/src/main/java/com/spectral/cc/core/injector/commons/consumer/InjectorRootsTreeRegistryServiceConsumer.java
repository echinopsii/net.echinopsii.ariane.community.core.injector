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

package com.spectral.cc.core.injector.commons.consumer;

import com.spectral.cc.core.injector.commons.registry.InjectorRootsTreeRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(publicFactory = false, factoryMethod = "getInstance")
@Instantiate
public class InjectorRootsTreeRegistryServiceConsumer {
    private static final Logger log = LoggerFactory.getLogger(InjectorRootsTreeRegistryServiceConsumer.class);
    private static InjectorRootsTreeRegistryServiceConsumer INSTANCE;

    @Requires
    private InjectorRootsTreeRegistry injectorRootsTreeRegistry = null;

    @Bind
    public void bindInjectorRootsTreeRegistry(InjectorRootsTreeRegistry r) {
        log.info("Consumer bound to injector roots tree registry...");
        injectorRootsTreeRegistry = r;
    }

    @Unbind
    public void unbindInjectorRootsTreeRegistry() {
        log.info("Consumer unbound from injector roots tree registry...");
        injectorRootsTreeRegistry = null;
    }

    public InjectorRootsTreeRegistry getInjectorRootsTreeRegistry() {
        return injectorRootsTreeRegistry;
    }

    public static InjectorRootsTreeRegistryServiceConsumer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InjectorRootsTreeRegistryServiceConsumer();
        }
        return INSTANCE;
    }
}