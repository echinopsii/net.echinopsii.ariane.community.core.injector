/**
 * Injector base
 * Injector registry factory iPojo impl
 * Copyright (C) 2015 Mathilde Ffrench
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

package net.echinopsii.ariane.community.core.injector.base.registry.iPojo;

import net.echinopsii.ariane.community.core.injector.base.registry.InjectorComponentsRegistry;
import net.echinopsii.ariane.community.core.injector.base.registry.InjectorGearsRegistry;
import net.echinopsii.ariane.community.core.injector.base.registry.InjectorRegistryFactory;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;

@Component
@Provides
@Instantiate
public class InjectorRegistryFactoryImpl implements InjectorRegistryFactory {

    private static final Logger log = LoggerFactory.getLogger(InjectorRegistryFactoryImpl.class);

    private static final String INJECTOR_REGISTRY_FACTORY_SERVICE_NAME = "Ariane Injector Registry Factory";


    @Validate
    public void validate() {
        log.info("{} is started", new Object[]{INJECTOR_REGISTRY_FACTORY_SERVICE_NAME});
    }

    @Invalidate
    public void invalidate() throws InterruptedException {
        log.info("{} is stopped", new Object[]{INJECTOR_REGISTRY_FACTORY_SERVICE_NAME});
    }


    @Override
    public boolean isValidProperties(Dictionary properties) {
        boolean ret = false;
        if (properties!=null) {
            Object registryType = properties.get(InjectorRegistryFactory.INJECTOR_REGISTRY_TYPE);
            if (registryType!=null && registryType instanceof String) {
                if (registryType.equals(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_TYPE)) ret = InjectorComponentsRegistryImpl.isValid(properties);
                else if (registryType.equals(InjectorRegistryFactory.INJECTORY_GEARS_REGISTRY_TYPE)) ret = InjectorGearsRegistryImpl.isValid(properties);
                else {
                    log.error("Unsupported registry type {}.", new Object[]{registryType});
                    ret = false;
                }
                if (ret) {
                    Object registryName = properties.get(InjectorRegistryFactory.INJECTOR_REGISTRY_NAME);
                    if (registryName==null || !(registryName instanceof String)) {
                        log.error("Registry name is not defined correctly");
                        ret = false;
                    }
                    Object cacheID = properties.get(InjectorRegistryFactory.INJECTOR_REGISTRY_CACHE_ID);
                    if (cacheID==null || !(cacheID instanceof String)) {
                        log.error("Cache ID is not defined correctly");
                        ret = false;
                    }
                    Object cacheName = properties.get(InjectorRegistryFactory.INJECTOR_REGISTRY_CACHE_NAME);
                    if (cacheName==null || !(cacheName instanceof  String)) {
                        log.error("Cache Name is not defined correctly");
                        ret = false;
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public InjectorGearsRegistry makeGearsRegistry(Dictionary properties) {
        InjectorGearsRegistry ret = null;
        properties.put(InjectorRegistryFactory.INJECTOR_REGISTRY_TYPE, InjectorRegistryFactory.INJECTORY_GEARS_REGISTRY_TYPE);
        if (isValidProperties(properties)) {
            String registryName = (String) properties.get(InjectorRegistryFactory.INJECTOR_REGISTRY_NAME);
            String cacheID = (String) properties.get(InjectorRegistryFactory.INJECTOR_REGISTRY_CACHE_ID);
            String cacheName = (String) properties.get(InjectorRegistryFactory.INJECTOR_REGISTRY_CACHE_NAME);
            ret = new InjectorGearsRegistryImpl().setRegistryName(registryName).setRegistryCacheID(cacheID).setRegistryCacheName(cacheName);
        }
        return ret;
    }

    @Override
    public InjectorComponentsRegistry makeComponentsRegistry(Dictionary properties) {
        InjectorComponentsRegistry ret = null;
        properties.put(InjectorRegistryFactory.INJECTOR_REGISTRY_TYPE, InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_TYPE);
        if (isValidProperties(properties)) {
            String registryName = (String) properties.get(InjectorRegistryFactory.INJECTOR_REGISTRY_NAME);
            String cacheID = (String) properties.get(InjectorRegistryFactory.INJECTOR_REGISTRY_CACHE_ID);
            String cacheName = (String) properties.get(InjectorRegistryFactory.INJECTOR_REGISTRY_CACHE_NAME);
            ret = new InjectorComponentsRegistryImpl().setRegistryName(registryName).setRegistryCacheID(cacheID).setRegistryCacheName(cacheName);
        }
        return ret;
    }
}