/**
 * Injector base
 * Injector gear registry iPojo impl
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

package net.echinopsii.ariane.community.core.injector.base.registry.iPojo;

import net.echinopsii.ariane.community.core.injector.base.mapreduce.PrefixKeyCollator;
import net.echinopsii.ariane.community.core.injector.base.mapreduce.PrefixKeyMapper;
import net.echinopsii.ariane.community.core.injector.base.mapreduce.PrefixKeyReduce;
import net.echinopsii.ariane.community.core.injector.base.model.AbstractCacheGear;
import net.echinopsii.ariane.community.core.injector.base.model.CacheManager;
import net.echinopsii.ariane.community.core.injector.base.model.CacheManagerEmbeddedInfinispanImpl;
import net.echinopsii.ariane.community.core.injector.base.registry.InjectorGearsRegistry;
import net.echinopsii.ariane.community.core.injector.base.registry.InjectorRegistryFactory;
import org.apache.felix.ipojo.annotations.*;
import org.infinispan.distexec.mapreduce.MapReduceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Dictionary;
import java.util.List;

@Component(managedservice="net.echinopsii.ariane.community.core.InjectorGearsRegistry")
@Provides
@Instantiate
public class InjectorGearsRegistryImpl extends AbstractCacheGear implements InjectorGearsRegistry {
    private static final Logger log = LoggerFactory.getLogger(InjectorGearsRegistryImpl.class);

    private static final String INJECTOR_GEARS_SHARED_REGISTRY_NAME = "Ariane Injector Shared Gears Registry";
    private static final String INJECTOR_GEARS_REGISTRY_CACHE_ID    = "ariane.community.core.injector.shared.gears.cache";
    private static final String INJECTOR_GEARS_REGISTRY_CACHE_NAME  = "Ariane Injector Shared Gears Cache";

    private String registryName = INJECTOR_GEARS_SHARED_REGISTRY_NAME;
    private Dictionary config = null;
    private File infConfFile  = null;
    private CacheManager cacheManager = null;
    private boolean started = false;

    public static boolean isValid(Dictionary properties) {
        boolean ret = true;
        if (properties!=null) {
            Object path = properties.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_CONFIGURATION_PATH_KEY);
            if (path != null && path instanceof String) {
                File testConfFile = new File((String)path);
                if (!testConfFile.exists() || !testConfFile.isFile()) {
                    ret = false;
                    log.error("infinispan configuration file path ({}) is not correct ! ", new Object[]{(String)path});
                }
            } else if (!CacheManagerEmbeddedInfinispanImpl.isValidProperties(properties)) {
                ret = false;
                log.error("Configuration parameters are not defined correctly !");
            }
        }
        return ret;
    }

    public InjectorGearsRegistry setRegistryName(String registryName) {
        this.registryName = registryName;
        return this;
    }

    public InjectorGearsRegistry setRegistryCacheID(String cacheID) {
        super.setCacheID(cacheID);
        return this;
    }

    public InjectorGearsRegistry setRegistryCacheName(String cacheName) {
        super.setCacheName(cacheName);
        return this;
    }

    public InjectorGearsRegistry setRegistryConfiguration(Dictionary properties) {
        config = properties;
        if (properties.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_CONFIGURATION_PATH_KEY)!=null)
            infConfFile = new File((String)properties.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_CONFIGURATION_PATH_KEY));
        return this;
    }

    public InjectorGearsRegistryImpl() {
        super();
    }

    public void startRegistry() {
        cacheManager = new CacheManagerEmbeddedInfinispanImpl();
        if (infConfFile!=null) cacheManager.start(infConfFile);
        else cacheManager.start(config);
        super.setCacheManager(cacheManager);
        super.start();
        //TODO: investigate infinispan purgeOnStartup instability
        for (String key : this.keySetFromPrefix(""))
            super.removeEntityFromCache(super.getEntityFromCache(key));
        started = true;
        log.info("{} is started", new Object[]{registryName});
    }

    @Validate
    public void validate() throws InterruptedException {
        setRegistryCacheID(INJECTOR_GEARS_REGISTRY_CACHE_ID);
        setRegistryCacheName(INJECTOR_GEARS_REGISTRY_CACHE_NAME);
        setRegistryName(INJECTOR_GEARS_SHARED_REGISTRY_NAME);
        startRegistry();
    }

    public void stopRegistry() {
        if (started) {
            super.stop();
            cacheManager.stop();
            started = false;
            log.info("{} is stopped", new Object[]{registryName});
        } else {
            log.error("{} can't be stopped as Infinispan cache manager has not been started !", new Object[]{registryName});
        }
    }

    public boolean isStarted() {
        return started;
    }

    @Invalidate
    public void invalidate() throws InterruptedException {
         stopRegistry();
    }

    @Updated
    public void updated(Dictionary properties) {
        log.debug("{} is being updated by {}", new Object[]{registryName, Thread.currentThread().toString()});
        log.debug("properties : {}", properties);
        if (!isValid(properties)) log.error("Invalid configuration for service " + registryName);
        else this.setRegistryConfiguration(properties);
    }

    @Override
    public List<String> keySetFromPrefix(String prefix) {
        MapReduceTask<String, Object,
                      String, String> mapReduceTask =
                new MapReduceTask<String, Object,
                                  String, String>(super.getCache());

        List<String> ret = mapReduceTask
                           .mappedWith(new PrefixKeyMapper(prefix))
                           .reducedWith(new PrefixKeyReduce())
                           .execute(new PrefixKeyCollator());

        return ret;
    }
}