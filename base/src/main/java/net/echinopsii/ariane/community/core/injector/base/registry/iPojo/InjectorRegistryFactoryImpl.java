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

import net.echinopsii.ariane.community.core.injector.base.model.CacheManagerEmbeddedInfinispanImpl;
import net.echinopsii.ariane.community.core.injector.base.registry.InjectorComponentsRegistry;
import net.echinopsii.ariane.community.core.injector.base.registry.InjectorGearsRegistry;
import net.echinopsii.ariane.community.core.injector.base.registry.InjectorRegistryFactory;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Dictionary;
import java.util.HashMap;

@Component(managedservice="net.echinopsii.ariane.community.core.InjectorRegistryFactory")
@Provides
@Instantiate
public class InjectorRegistryFactoryImpl implements InjectorRegistryFactory {

    private static final Logger log = LoggerFactory.getLogger(InjectorRegistryFactoryImpl.class);

    private static final String INJECTOR_REGISTRY_FACTORY_SERVICE_NAME = "Ariane Injector Registry Factory";

    private HashMap<String, InjectorGearsRegistry> injectorGearsRegistryHashMap = new HashMap<>();
    private HashMap<String, InjectorComponentsRegistry> injectorComponentsRegistryHashMap = new HashMap<>();

    private String gearsRegistryRemoteCacheDirPath = null;
    private String componentsRegistryRemoteCacheDirPath = null;

    private boolean isValidConfiguration(Dictionary properties) {
        boolean ret = true;
        if (properties!=null) {
            Object pathGearsRegistryRemoteCache = properties.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_REMOTE_CACHE_DIR_PATH_KEY);
            if (pathGearsRegistryRemoteCache != null && pathGearsRegistryRemoteCache instanceof String) {
                File gearsRegistryRemoteCache = new File((String) pathGearsRegistryRemoteCache);
                if (!gearsRegistryRemoteCache.exists() || !gearsRegistryRemoteCache.isDirectory()) {
                    ret = false;
                    log.error("Gears registry remote cache dir path ({}) is not correct ! ", new Object[]{(String) pathGearsRegistryRemoteCache});
                } else this.gearsRegistryRemoteCacheDirPath = (String) pathGearsRegistryRemoteCache;
            } else if (!CacheManagerEmbeddedInfinispanImpl.isValidProperties(properties)) {
                ret = false;
                log.error("{} configuration parameters is not defined correctly !", new Object[]{InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_REMOTE_CACHE_DIR_PATH_KEY});
            }

            Object pathComponentsRegistryRemoteCache = properties.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_REMOTE_CACHE_DIR_PATH_KEY);
            if (pathComponentsRegistryRemoteCache != null && pathComponentsRegistryRemoteCache instanceof String) {
                File componentsRegistryRemoteCache = new File((String)pathComponentsRegistryRemoteCache);
                if (!componentsRegistryRemoteCache.exists() || !componentsRegistryRemoteCache.isDirectory()) {
                    ret = false;
                    log.error("Components registry remote cache dir path ({}) is not correct ! ", new Object[]{(String) pathGearsRegistryRemoteCache});
                } else this.componentsRegistryRemoteCacheDirPath = (String) pathComponentsRegistryRemoteCache;
            } else if (!CacheManagerEmbeddedInfinispanImpl.isValidProperties(properties)) {
                ret = false;
                log.error("{} configuration parameters is not defined correctly !", new Object[]{InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_REMOTE_CACHE_DIR_PATH_KEY});
            }
        }
        return ret ;
    }

    @Validate
    public void validate() {
        log.info("{} is started", new Object[]{INJECTOR_REGISTRY_FACTORY_SERVICE_NAME});
    }

    @Invalidate
    public void invalidate() throws InterruptedException {
        log.info("{} is stopped", new Object[]{INJECTOR_REGISTRY_FACTORY_SERVICE_NAME});
    }

    @Updated
    public void updated(Dictionary properties) {
        log.debug("{} is being updated by {}", new Object[]{INJECTOR_REGISTRY_FACTORY_SERVICE_NAME, Thread.currentThread().toString()});
        log.debug("properties : {}", properties);
        if (!isValidConfiguration(properties))
            log.error("Invalid configuration for service " + INJECTOR_REGISTRY_FACTORY_SERVICE_NAME);
    }

    private boolean isValidProperties(Dictionary properties) {
        boolean ret = false;
        if (properties!=null) {
            Object registryType = properties.get(InjectorRegistryFactory.INJECTOR_REGISTRY_TYPE);
            if (registryType!=null && registryType instanceof String) {
                if (registryType.equals(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_TYPE)) {
                    ret = InjectorComponentsRegistryImpl.isValid(properties);
                    if (ret) {
                        Object registryName = properties.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_NAME);
                        if (registryName==null) {
                            log.error("Components Registry name is not defined correctly");
                            ret = false;
                        }
                        Object cacheID = properties.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_ID);
                        if (cacheID==null) {
                            log.error("Components Cache ID is not defined correctly");
                            ret = false;
                        }
                        Object cacheName = properties.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_NAME);
                        if (cacheName==null) {
                            log.error("Components Cache Name is not defined correctly");
                            ret = false;
                        }
                    }
                } else if (registryType.equals(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_TYPE)) {
                    ret = InjectorGearsRegistryImpl.isValid(properties);
                    if (ret) {
                        Object registryName = properties.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_NAME);
                        if (registryName==null) {
                            log.error("Gears Registry name is not defined correctly");
                            ret = false;
                        }
                        Object cacheID = properties.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_ID);
                        if (cacheID==null) {
                            log.error("Gears Cache ID is not defined correctly");
                            ret = false;
                        }
                        Object cacheName = properties.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_NAME);
                        if (cacheName==null) {
                            log.error("Gears Cache Name is not defined correctly");
                            ret = false;
                        }
                    }
                } else {
                    log.error("Unsupported registry type {}.", new Object[]{registryType});
                    ret = false;
                }
            }
        }
        return ret;
    }

    @Override
    public InjectorGearsRegistry makeGearsRegistry(Dictionary properties) {
        InjectorGearsRegistry ret = null;
        properties.put(InjectorRegistryFactory.INJECTOR_REGISTRY_TYPE, InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_TYPE);
        properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_LOCATION, gearsRegistryRemoteCacheDirPath);
        if (isValidProperties(properties)) {
            if (injectorGearsRegistryHashMap.containsKey((String) properties.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_ID)))
                ret = injectorGearsRegistryHashMap.get((String) properties.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_ID));
            else {
                String registryName = (String) properties.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_NAME);
                String cacheID = (String) properties.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_ID);
                String cacheName = (String) properties.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_NAME);
                ret = new InjectorGearsRegistryImpl().setRegistryName(registryName).setRegistryCacheID(cacheID).
                        setRegistryCacheName(cacheName).setRegistryConfiguration(properties);
                injectorGearsRegistryHashMap.put((String) properties.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_ID), ret);
            }
        }
        return ret;
    }

    @Override
    public InjectorComponentsRegistry makeComponentsRegistry(Dictionary properties) {
        InjectorComponentsRegistry ret = null;
        properties.put(InjectorRegistryFactory.INJECTOR_REGISTRY_TYPE, InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_TYPE);
        properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_LOCATION, componentsRegistryRemoteCacheDirPath);
        if (isValidProperties(properties)) {
            if (injectorComponentsRegistryHashMap.containsKey((String) properties.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_ID)))
                ret = injectorComponentsRegistryHashMap.get((String) properties.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_ID));
            else {
                String registryName = (String) properties.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_NAME);
                String cacheID = (String) properties.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_ID);
                String cacheName = (String) properties.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_NAME);
                ret = new InjectorComponentsRegistryImpl().setRegistryName(registryName).setRegistryCacheID(cacheID).
                        setRegistryCacheName(cacheName).setRegistryConfiguration(properties);
                injectorComponentsRegistryHashMap.put((String) properties.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_ID), ret);
            }
        }
        return ret;
    }
}