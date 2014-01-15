/**
 * Injector Commons Services bundle
 * Injector Registry iPojo Impl
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
package com.spectral.cc.core.injector.commons.registry.iPojo;

import com.spectral.cc.core.injector.commons.model.InjectorEntity;
import com.spectral.cc.core.injector.commons.registry.InjectorRootsTreeRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeSet;

@Component
@Provides
@Instantiate
public class InjectorRootsTreeRegistryImpl implements InjectorRootsTreeRegistry {

    private static final String ROOT_INJECTOR_REGISTRY_SERVICE_NAME = "Injector Roots Tree Registry Service";
    private static final Logger log = LoggerFactory.getLogger(InjectorRootsTreeRegistryImpl.class);

    private TreeSet<InjectorEntity> registry = new TreeSet<InjectorEntity>();

    @Validate
    public void validate() throws Exception {
        log.info("{} is started.", new Object[]{ROOT_INJECTOR_REGISTRY_SERVICE_NAME});
    }

    @Invalidate
    public void invalidate(){
        log.info("Stopping {}...", new Object[]{ROOT_INJECTOR_REGISTRY_SERVICE_NAME});
        registry.clear();
        log.info("{} is stopped.", new Object[]{ROOT_INJECTOR_REGISTRY_SERVICE_NAME});
    }

    @Override
    public InjectorEntity registerRootInjectorEntity(InjectorEntity injectorEntity) throws Exception {
        registry.add(injectorEntity);
        return injectorEntity;
    }

    @Override
    public InjectorEntity unregisterRootInjectorEntity(InjectorEntity injectorEntity) throws Exception {
        registry.remove(injectorEntity);
        return injectorEntity;
    }

    @Override
    public TreeSet<InjectorEntity> getRootInjectorEntities() {
        return registry;
    }

    @Override
    public InjectorEntity getInjectorEntityFromValue(String value) {
        InjectorEntity ret = null;
        for (InjectorEntity entity : registry) {
            ret = entity.findInjectorEntityFromValue(value);
            if (ret!=null)
                break;
        }
        return ret;
    }

    @Override
    public InjectorEntity getInjectorEntityFromID(String id) {
        InjectorEntity ret = null;
        for (InjectorEntity entity : registry) {
            ret = entity.findInjectorEntityFromID(id);
            if (ret!=null)
                break;
        }
        return ret;
    }
}