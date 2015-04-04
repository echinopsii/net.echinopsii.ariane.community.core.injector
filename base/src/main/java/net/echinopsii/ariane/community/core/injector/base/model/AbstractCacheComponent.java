/**
 * Injector base
 * Injector model abstract cache component
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractCacheComponent implements Cache<Component, String> {

    private static final Logger log = LoggerFactory.getLogger(AbstractCacheComponent.class);

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
    public AbstractCacheComponent start() {
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
    public Collection<Component> values() {
        Collection<Component> ret = new CopyOnWriteArrayList<>();
        if (cache!=null) {
            Set<String> keys = cache.keySet();
            for (String key : keys) ret.add((Component)cache.get(key));
        }
        return ret;
    }

    @Override
    public boolean containsValue(Component component) {
        if (cache!=null) return cache.containsKey(component.getComponentId());
        else return false;
    }

    @Override
    public int size() {
        if (cache!=null) return cache.size();
        else return 0;
    }

    @Override
    public void putEntityToCache(Component component) {
        if (cache!=null) cache.put(component.getComponentId(), component);
    }

    @Override
    public void removeEntityFromCache(Component component) {
        if (cache!=null) cache.remove(component.getComponentId());
    }

    @Override
    public Component getEntityFromCache(String id) {
        if (cache!=null) return (Component) cache.get(id);
        else return null;
    }
}