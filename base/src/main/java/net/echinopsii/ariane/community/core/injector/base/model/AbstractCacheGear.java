/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 7/31/14 echinopsii
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractCacheGear implements Cache<Gear, Long> {

    private static final Logger log = LoggerFactory.getLogger(AbstractCacheGear.class);

    private String cacheID ;
    private String cacheName;
    private org.infinispan.Cache cache;
    private CacheManager cacheManager;

    public String getCacheID() {
        return cacheID;
    }

    public void setCacheID(String cacheID) {
        this.cacheID = cacheID;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public org.infinispan.Cache getCache() {
        return cache;
    }

    @Override
    public AbstractCacheGear start() {
        if (cacheManager != null ) {
            cache = cacheManager.getCache(cacheID);
            log.info("{} is started", cacheName);
        } else {
            log.error("Cache manager is not defined !");
        }
        return this;
    }

    @Override
    public void stop() {
        cache.stop();
        log.info("{} is stopped", cacheName);
    }

    @Override
    public Properties getConfiguration() {
        return cacheManager.getCacheConfiguration(cache);
    }

    @Override
    public Collection<Gear> values() {
        Collection<Gear> ret = new CopyOnWriteArrayList<>();
        if (cache!=null) {
            Set<Long> keys = cache.keySet();
            for (Long key : keys) ret.add((Gear)cache.get(key));
        }
        return ret;
    }

    @Override
    public boolean containsValue(Gear gear) {
        if (cache!=null) return cache.containsKey(gear.getGearId());
        else return false;
    }

    @Override
    public int size() {
        if (cache!=null) return cache.size();
        else return 0;
    }

    @Override
    public void putEntityToCache(Gear gear) {
        if (cache!=null) cache.put(gear.getGearId(), gear);
    }

    @Override
    public void removeEntityFromCache(Gear gear) {
        if (cache!=null) cache.remove(gear.getGearId());
    }

    @Override
    public Gear getEntityFromCache(Long id) {
        if (cache!=null) return (Gear) cache.get(id);
        else return null;
    }

    public boolean containsComponentGear(String containerURL) {
        boolean ret = false;
        if (cache!=null) {
            for (long key : (Set<Long>)cache.keySet()) {
                Gear gear = (Gear)cache.get(key);
                if (gear.getComponentURL()!=null) {
                    if (gear.getComponentURL().equals(containerURL)) {
                        ret = true;
                        break;
                    }
                }
            }
        }
        return ret;
    }
}