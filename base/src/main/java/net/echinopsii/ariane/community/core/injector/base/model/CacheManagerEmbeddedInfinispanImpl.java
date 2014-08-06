/**
 * Tibco rv injector directory bundle
 * provide injector from Tibco RV components to MappingDS (RVRD, RVD ...)
 *
 * Copyright (C) 2014  Mathilde Ffrench
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

package net.echinopsii.ariane.community.core.injector.base.model;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.*;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class CacheManagerEmbeddedInfinispanImpl implements CacheManager {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerEmbeddedInfinispanImpl.class);

    private EmbeddedCacheManager manager = null;
    private static CacheManagerEmbeddedInfinispanImpl INSTANCE = null;

    /**
     * Factory method for this singleton.
     *
     * @return instantiated infinispan embedded cache manager
     */
    public synchronized static CacheManagerEmbeddedInfinispanImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CacheManagerEmbeddedInfinispanImpl();
        }
        return INSTANCE;
    }

    @Override
    public CacheManager start(File confFile) {
        try {
            manager = new DefaultCacheManager(new FileInputStream(confFile));
        } catch (Exception e) {
            log.error("Error while initializing Infinispan Embedded Cache Manager !");
            e.printStackTrace();
        }

        return this;
    }

    @Override
    public CacheManager stop() {
        if (manager!=null) {
            manager.stop();
            manager = null;
        }
        return this;
    }

    @Override
    public boolean isStarted() {
        return (manager!=null);
    }

    @Override
    public Cache getCache(String id) {
        if (manager!=null)
            return manager.getCache(id);
        else
            return null;
    }

    @Override
    public Properties getCacheConfiguration(Cache cache) {
        Properties ret = null;
        if (cache!=null && manager!=null) {
            ret = new Properties();
            Configuration conf = cache.getCacheConfiguration();

            ret.put("Cache name", cache.getName());
            ret.put("Cache size", cache.size());
            ret.put("Cache status", cache.getStatus().toString());

            ret.put("Cluster enabled", manager.getCacheManagerConfiguration().isClustered());
            if (manager.getCacheManagerConfiguration().isClustered())
                ret.put("Cluster cache mode",conf.clustering().cacheModeString());

            EvictionConfiguration evictionConfiguration = conf.eviction();
            ret.put("Eviction max entries",evictionConfiguration.maxEntries());
            ret.put("Eviction strategy enabled",evictionConfiguration.strategy().isEnabled());
            ret.put("Eviction strategy name",evictionConfiguration.strategy().name());

            ExpirationConfiguration expirationConfiguration = conf.expiration();
            ret.put("Expiration lifespan",expirationConfiguration.lifespan());
            ret.put("Expiration max idle", expirationConfiguration.maxIdle());
            ret.put("Expiration reaper enabled", expirationConfiguration.reaperEnabled());
            ret.put("Expiration wake up interval", expirationConfiguration.wakeUpInterval());

            PersistenceConfiguration persistenceConfiguration = conf.persistence();
            ret.put("Persistence fetch state",persistenceConfiguration.fetchPersistentState());
            ret.put("Persistence passivation",persistenceConfiguration.passivation());
            ret.put("Persistence preload",persistenceConfiguration.preload());
            ret.put("Persistence use async store", persistenceConfiguration.usingAsyncStore());
            for (StoreConfiguration storeConfiguration : persistenceConfiguration.stores()) {
                ret.put("Persistent store async enabled",storeConfiguration.async().enabled());
                ret.put("Persistent store fetch state",storeConfiguration.fetchPersistentState());
                ret.put("Persistent store ignore modification", storeConfiguration.ignoreModifications());
                ret.put("Persistent store preload", storeConfiguration.preload());
                if (storeConfiguration.properties()!=null)
                    for (Object key : storeConfiguration.properties().keySet())
                        ret.put("Persistent store property " + key.toString(), storeConfiguration.properties().get(key));
                ret.put("Persistent store purge on startup", storeConfiguration.purgeOnStartup());
            }

            ret.put("Versioning enabled", conf.versioning().enabled());
        }
        return ret;
    }
}