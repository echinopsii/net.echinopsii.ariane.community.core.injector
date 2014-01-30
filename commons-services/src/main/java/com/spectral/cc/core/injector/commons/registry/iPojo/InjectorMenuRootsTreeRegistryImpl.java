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

import com.spectral.cc.core.injector.commons.model.InjectorMenuEntity;
import com.spectral.cc.core.injector.commons.registry.InjectorMenuRootsTreeRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeSet;

@Component
@Provides
@Instantiate
public class InjectorMenuRootsTreeRegistryImpl implements InjectorMenuRootsTreeRegistry {

    private static final String ROOT_INJECTOR_REGISTRY_SERVICE_NAME = "Injector Roots Tree Registry Service";
    private static final Logger log = LoggerFactory.getLogger(InjectorMenuRootsTreeRegistryImpl.class);

    private TreeSet<InjectorMenuEntity> registry = new TreeSet<InjectorMenuEntity>();

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
    public InjectorMenuEntity registerRootInjectorEntity(InjectorMenuEntity injectorMenuEntity) throws Exception {
        registry.add(injectorMenuEntity);
        return injectorMenuEntity;
    }

    @Override
    public InjectorMenuEntity unregisterRootInjectorEntity(InjectorMenuEntity injectorMenuEntity) throws Exception {
        registry.remove(injectorMenuEntity);
        return injectorMenuEntity;
    }

    @Override
    public TreeSet<InjectorMenuEntity> getRootInjectorEntities() {
        return registry;
    }

    @Override
    public InjectorMenuEntity getInjectorEntityFromValue(String value) {
        InjectorMenuEntity ret = null;
        for (InjectorMenuEntity entity : registry) {
            ret = entity.findInjectorEntityFromValue(value);
            if (ret!=null)
                break;
        }
        return ret;
    }

    @Override
    public InjectorMenuEntity getInjectorEntityFromID(String id) {
        InjectorMenuEntity ret = null;
        for (InjectorMenuEntity entity : registry) {
            ret = entity.findInjectorEntityFromID(id);
            if (ret!=null)
                break;
        }
        return ret;
    }
}