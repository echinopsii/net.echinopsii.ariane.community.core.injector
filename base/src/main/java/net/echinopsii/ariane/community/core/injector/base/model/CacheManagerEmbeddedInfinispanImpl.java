/**
 * Injector base
 * Injector model cache manager embedded infinispan impl
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

package net.echinopsii.ariane.community.core.injector.base.model;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.*;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Dictionary;
import java.util.Properties;

public class CacheManagerEmbeddedInfinispanImpl implements CacheManager {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerEmbeddedInfinispanImpl.class);

    private EmbeddedCacheManager manager = null;
    private static CacheManagerEmbeddedInfinispanImpl INSTANCE = null;

    public static final String INJECTOR_CACHE_MGR_NAME = "ariane.community.injector.cache.mgr.name";
    public static final String INJECTOR_CACHE_NAME = "ariane.community.injector.cache.name";
    public static final String INJECTOR_CACHE_EVICTION_STRATEGY = "ariane.community.injector.cache.eviction.strategy";
    public static final String INJECTOR_CACHE_EVICTION_MAX_ENTRIES = "ariane.community.injector.cache.eviction.max.entries";
    public static final String INJECTOR_CACHE_PERSISTENCE_PASSIVATION = "ariane.community.injector.cache.persistence.passivation";
    public static final String INJECTOR_CACHE_PERSISTENCE_SF_FETCH = "ariane.community.injector.cache.persistence.sf.fetch";
    public static final String INJECTOR_CACHE_PERSISTENCE_SF_IGNORE_DIFF = "ariane.community.injector.cache.persistence.sf.ignore.diff";
    public static final String INJECTOR_CACHE_PERSISTENCE_SF_PURGE_STARTUP = "ariane.community.injector.cache.persistence.sf.purge.startup";
    public static final String INJECTOR_CACHE_PERSISTENCE_SF_LOCATION = "ariane.community.injector.cache.persistence.sf.location";
    public static final String INJECTOR_CACHE_PERSISTENCE_ASYNC = "ariane.community.injector.cache.persistence.async";

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

    public static boolean isValidProperties(Dictionary properties) {
        boolean ret = true;
        if (properties.get(INJECTOR_CACHE_MGR_NAME)==null && properties.get(INJECTOR_CACHE_MGR_NAME) instanceof String) ret = false;
        if (ret && properties.get(INJECTOR_CACHE_NAME)==null) ret = false;

        if (ret && (
                    (properties.get(INJECTOR_CACHE_EVICTION_STRATEGY)==null && properties.get(INJECTOR_CACHE_EVICTION_MAX_ENTRIES)!=null) ||
                    (properties.get(INJECTOR_CACHE_EVICTION_STRATEGY)!=null && properties.get(INJECTOR_CACHE_EVICTION_MAX_ENTRIES)==null)
            )) ret = false;
        else if (ret && properties.get(INJECTOR_CACHE_EVICTION_STRATEGY)!=null && properties.get(INJECTOR_CACHE_EVICTION_MAX_ENTRIES)!=null) {
            if (!(  properties.get(INJECTOR_CACHE_EVICTION_STRATEGY).equals("LIRS") ||
                    properties.get(INJECTOR_CACHE_EVICTION_STRATEGY).equals("LRU") ||
                    properties.get(INJECTOR_CACHE_EVICTION_STRATEGY).equals("NONE") ||
                    properties.get(INJECTOR_CACHE_EVICTION_STRATEGY).equals("UNORDERED")))
                ret = false;
        }

        if (ret && properties.get(INJECTOR_CACHE_PERSISTENCE_PASSIVATION)==null) ret = false;
        else if (ret && !(
                            properties.get(INJECTOR_CACHE_PERSISTENCE_PASSIVATION) instanceof String &&
                            (
                                    ((String)properties.get(INJECTOR_CACHE_PERSISTENCE_PASSIVATION)).toLowerCase().equals("true") ||
                                    ((String)properties.get(INJECTOR_CACHE_PERSISTENCE_PASSIVATION)).toLowerCase().equals("false")
                            )
                        )
                ) ret = false;

        if (ret && properties.get(INJECTOR_CACHE_PERSISTENCE_SF_FETCH)==null) ret = false;
        else if (ret && !(
                            properties.get(INJECTOR_CACHE_PERSISTENCE_SF_FETCH) instanceof String &&
                            (
                                    ((String)properties.get(INJECTOR_CACHE_PERSISTENCE_SF_FETCH)).toLowerCase().equals("true") ||
                                    ((String)properties.get(INJECTOR_CACHE_PERSISTENCE_SF_FETCH)).toLowerCase().equals("false")
                            )
                        )
                ) ret = false;

        if (ret && properties.get(INJECTOR_CACHE_PERSISTENCE_SF_IGNORE_DIFF)==null) ret = false;
        else if (ret && !(
                            properties.get(INJECTOR_CACHE_PERSISTENCE_SF_IGNORE_DIFF) instanceof String &&
                            (
                                    ((String)properties.get(INJECTOR_CACHE_PERSISTENCE_SF_IGNORE_DIFF)).toLowerCase().equals("true") ||
                                    ((String)properties.get(INJECTOR_CACHE_PERSISTENCE_SF_IGNORE_DIFF)).toLowerCase().equals("false")
                            )
                        )
                ) ret = false;

        if (ret && properties.get(INJECTOR_CACHE_PERSISTENCE_SF_PURGE_STARTUP)==null) ret = false;
        else if (ret && !(
                        properties.get(INJECTOR_CACHE_PERSISTENCE_SF_PURGE_STARTUP) instanceof String &&
                        (
                                ((String)properties.get(INJECTOR_CACHE_PERSISTENCE_SF_PURGE_STARTUP)).toLowerCase().equals("true") ||
                                ((String)properties.get(INJECTOR_CACHE_PERSISTENCE_SF_PURGE_STARTUP)).toLowerCase().equals("false")
                        )
                    )
                ) ret = false;

        if (ret && properties.get(INJECTOR_CACHE_PERSISTENCE_SF_LOCATION)==null) ret = false;

        return ret;
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
    public CacheManager start(Dictionary properties) {
        GlobalConfigurationBuilder globalConfigurationBuilder = new GlobalConfigurationBuilder();
        globalConfigurationBuilder.globalJmxStatistics().enable().cacheManagerName((String) properties.get(INJECTOR_CACHE_MGR_NAME));
        ClassLoader cl = this.getClass().getClassLoader();
        globalConfigurationBuilder.classLoader(cl);
        GlobalConfiguration globalConfiguration = globalConfigurationBuilder.build();

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        if (properties.get(INJECTOR_CACHE_EVICTION_STRATEGY) != null) {
            EvictionStrategy strategy = null;
            if (properties.get(INJECTOR_CACHE_EVICTION_STRATEGY).equals("LIRS")) strategy = EvictionStrategy.LIRS;
            if (properties.get(INJECTOR_CACHE_EVICTION_STRATEGY).equals("LRU")) strategy = EvictionStrategy.LRU;
            if (properties.get(INJECTOR_CACHE_EVICTION_STRATEGY).equals("NONE")) strategy = EvictionStrategy.NONE;
            if (properties.get(INJECTOR_CACHE_EVICTION_STRATEGY).equals("UNORDERED"))
                strategy = EvictionStrategy.UNORDERED;
            configurationBuilder.eviction().strategy(strategy).maxEntries(new Integer((String) properties.get(INJECTOR_CACHE_EVICTION_MAX_ENTRIES)));
        }

        PersistenceConfigurationBuilder persistenceConfigurationBuilder = configurationBuilder.persistence();
        if (((String) properties.get(INJECTOR_CACHE_PERSISTENCE_PASSIVATION)).toLowerCase().equals("true"))
            persistenceConfigurationBuilder.passivation(true);
        else
            persistenceConfigurationBuilder.passivation(false);

        SingleFileStoreConfigurationBuilder singleFileStoreConfigurationBuilder = persistenceConfigurationBuilder.addSingleFileStore();
        if (((String) properties.get(INJECTOR_CACHE_PERSISTENCE_SF_FETCH)).toLowerCase().equals("true"))
            singleFileStoreConfigurationBuilder.fetchPersistentState(true);
        else
            singleFileStoreConfigurationBuilder.fetchPersistentState(false);

        if (((String) properties.get(INJECTOR_CACHE_PERSISTENCE_SF_IGNORE_DIFF)).toLowerCase().equals("true"))
            singleFileStoreConfigurationBuilder.ignoreModifications(true);
        else
            singleFileStoreConfigurationBuilder.ignoreModifications(false);

        if (((String) properties.get(INJECTOR_CACHE_PERSISTENCE_SF_PURGE_STARTUP)).toLowerCase().equals("true"))
            singleFileStoreConfigurationBuilder.purgeOnStartup(true);
        else
            singleFileStoreConfigurationBuilder.purgeOnStartup(false);

        singleFileStoreConfigurationBuilder.location((String) properties.get(INJECTOR_CACHE_PERSISTENCE_SF_LOCATION));
        if (properties.get(INJECTOR_CACHE_PERSISTENCE_ASYNC)!=null && ((String) properties.get(INJECTOR_CACHE_PERSISTENCE_ASYNC)).toLowerCase().equals("true"))
            singleFileStoreConfigurationBuilder.async().enable();

        Configuration configuration = configurationBuilder.build();

        try {
            manager = new DefaultCacheManager(globalConfiguration, configuration);
        } catch (Throwable t) {
            t.printStackTrace();
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