/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 8/4/14 echinopsii
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

    private static final String INJECTOR_GEARS_REGISTRY_SERVICE_NAME = "Ariane Injector Gears Registry";
    private static final String INJECTOR_GEARS_REGISTRY_CACHE_ID     = "ariane.community.core.injector.shared.gears.cache";
    private static final String INJECTOR_GEARS_REGISTRY_CACHE_NAME   = "Ariane Injector Shared Gears Cache";

    public static final String INJECTOR_GEARS_REGISTRY_CACHE_CONFIGURATION_PATH_KEY = "ariane.community.injector.gears.cache.configuration.path";

    private static Dictionary config = null;
    private static File infConfFile  = null;
    private static CacheManager cacheManager = null;

    private static boolean isValid(Dictionary properties) {
        boolean ret = false;
        if (properties!=null) {
            Object path = properties.get(INJECTOR_GEARS_REGISTRY_CACHE_CONFIGURATION_PATH_KEY);
            if (path != null && path instanceof String) {
                infConfFile = new File((String)path);
                if (infConfFile.exists() && infConfFile.isFile())
                    ret = true;
                else {
                    log.error("infinispan configuration file path ({}) is not correct ! ", new Object[]{(String)path});
                    infConfFile = null;
                }
            } else {
                log.error("{} configuration parameters is not defined correctly !", new Object[]{INJECTOR_GEARS_REGISTRY_CACHE_CONFIGURATION_PATH_KEY});
            }
        }
        return ret;
    }


    public InjectorGearsRegistryImpl() {
        super();
        super.setCacheID(INJECTOR_GEARS_REGISTRY_CACHE_ID);
        super.setCacheName(INJECTOR_GEARS_REGISTRY_CACHE_NAME);
    }


    @Validate
    public void validate() throws InterruptedException {
        if (infConfFile!=null) {
            cacheManager = new CacheManagerEmbeddedInfinispanImpl().start(infConfFile);
            super.setCacheManager(cacheManager);
            super.start();
            log.debug("{} is started", new Object[]{INJECTOR_GEARS_REGISTRY_SERVICE_NAME});
        } else {
            log.error("{} can't be started... Infinispan configuration file is missing !", new Object[]{INJECTOR_GEARS_REGISTRY_SERVICE_NAME});
        }
    }

    @Invalidate
    public void invalidate() throws InterruptedException {
        if (cacheManager!=null && cacheManager.isStarted()) {
            super.stop();
            cacheManager.stop();
            log.debug("{} is stopped", new Object[]{INJECTOR_GEARS_REGISTRY_SERVICE_NAME});
        } else {
            log.error("{} can't be stopped as Infinispan cache manager has not been started !", new Object[]{INJECTOR_GEARS_REGISTRY_SERVICE_NAME});
        }
    }

    @Updated
    public static void updated(Dictionary properties) {
        log.debug("{} is being updated by {}", new Object[]{INJECTOR_GEARS_REGISTRY_SERVICE_NAME, Thread.currentThread().toString()});
        log.debug("properties : {}", properties);
        if (!isValid(properties))
            log.error("Invalid configuration for service " + INJECTOR_GEARS_REGISTRY_SERVICE_NAME);
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