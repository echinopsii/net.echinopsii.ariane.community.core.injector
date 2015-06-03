/**
 *
 *
 * Copyright (C) 2015 mffrench
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
package net.echinopsii.ariane.community.core.injector.messaging.worker;

import net.echinopsii.ariane.community.core.injector.base.model.AbstractCacheComponent;
import net.echinopsii.ariane.community.core.injector.base.model.AbstractCacheGear;
import net.echinopsii.ariane.community.core.injector.base.model.CacheManagerEmbeddedInfinispanImpl;
import net.echinopsii.ariane.community.core.injector.base.registry.InjectorComponentsRegistry;
import net.echinopsii.ariane.community.core.injector.base.registry.InjectorGearsRegistry;
import net.echinopsii.ariane.community.core.injector.base.registry.InjectorRegistryFactory;
import net.echinopsii.ariane.community.core.injector.messaging.InjectorMessagingBootstrap;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class RemoteCacheFactoryWorker implements AppMsgWorker, InjectorRegistryFactory {
    private static final Logger log = LoggerFactory.getLogger(RemoteCacheFactoryWorker.class);

    public final static String OPERATION_FDN                     = "OPERATION";
    public final static String OPERATION_MAKE_GEAR_REGISTRY      = "MAKE_GEARS_REGISTRY";
    public final static String OPERATION_MAKE_COMPONENT_REGISTRY = "MAKE_COMPONENTS_REGISTRY";
    public final static String OPERATION_NOT_DEFINED             = "NOT_DEFINED";

    public final static String REPLY_RC = "RC";
    public final static String REPLY_MSG = "SERVER_ERROR_MESSAGE";

    @Override
    public Map<String, Object> apply(Map<String, Object> message) {
        log.debug("Remote Injector Cache Factory Worker on  : { " + message.toString() +  " }...");

        log.debug("Injector Remote Tree Worker on  : { " + message.toString() + " }...");

        Map<String, Object>        reply              = new HashMap<>();
        InjectorGearsRegistry      gearsRegistry      = null;
        InjectorComponentsRegistry componentsRegistry = null;

        Object oOperation = message.get(OPERATION_FDN);
        String operation = null;
        Dictionary properties = null;
        boolean isValid = true;

        if (oOperation==null)
            operation = OPERATION_NOT_DEFINED;
        else
            operation = oOperation.toString();

        switch (operation) {
            case OPERATION_MAKE_GEAR_REGISTRY:
                properties = new Properties();

                if (message.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_NAME)==null) {
                    isValid = false;
                    reply.put(REPLY_RC, 1);
                    reply.put(MomMsgTranslator.MSG_BODY, "Operation parameters are not valid... " + InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_NAME + " is not defined !");
                } else properties.put(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_NAME, message.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_NAME).toString());

                if (isValid && message.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_ID)==null) {
                    isValid = false;
                    reply.put(REPLY_RC, 1);
                    reply.put(MomMsgTranslator.MSG_BODY, "Operation parameters are not valid... " + InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_ID + " is not defined !");
                } else {
                    // default cache name = registry cache id
                    properties.put(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_ID, message.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_ID).toString());
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_NAME, message.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_ID).toString());
                }

                if (isValid && message.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_NAME)==null){
                    isValid = false;
                    reply.put(REPLY_RC, 1);
                    reply.put(MomMsgTranslator.MSG_BODY, "Operation parameters are not valid... " + InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_NAME + " is not defined !");
                } else properties.put(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_NAME, message.get(InjectorRegistryFactory.INJECTOR_GEARS_REGISTRY_CACHE_NAME).toString());

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_MGR_NAME)==null) {
                    isValid=false;
                    reply.put(REPLY_RC, 1);
                    reply.put(MomMsgTranslator.MSG_BODY, "Operation parameters are not valid... " + CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_MGR_NAME + " is not defined !");
                } else properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_MGR_NAME, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_MGR_NAME).toString());

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_NAME)!=null)
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_NAME, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_NAME).toString());

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_EVICTION_STRATEGY)!=null)
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_EVICTION_STRATEGY, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_EVICTION_STRATEGY).toString());
                else properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_EVICTION_STRATEGY, "LRU");

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_EVICTION_MAX_ENTRIES)!=null)
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_EVICTION_MAX_ENTRIES, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_EVICTION_MAX_ENTRIES).toString());
                else properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_EVICTION_MAX_ENTRIES, "2000");

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_PASSIVATION)!=null)
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_PASSIVATION, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_PASSIVATION).toString());
                else properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_PASSIVATION, "true");

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_FETCH)!=null)
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_FETCH, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_FETCH).toString());
                else properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_FETCH, "true");

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_IGNORE_DIFF)!=null)
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_IGNORE_DIFF, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_IGNORE_DIFF).toString());
                else properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_IGNORE_DIFF, "false");

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_PURGE_STARTUP)!=null)
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_PURGE_STARTUP, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_PURGE_STARTUP).toString());
                else properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_PURGE_STARTUP, "false");

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_ASYNC)!=null)
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_ASYNC, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_ASYNC).toString());
                else properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_ASYNC, "true");

                if (isValid) {
                    try {
                        gearsRegistry = this.makeGearsRegistry(properties);

                        if (gearsRegistry==null) {
                            reply.put(REPLY_RC, 1);
                            reply.put(MomMsgTranslator.MSG_BODY, "Operation parameters are not valid... Have a look at Ariane server logs !");
                        } else {
                            reply.put(REPLY_RC, 0);
                            reply.put(MomMsgTranslator.MSG_BODY, "Gear registry " + ((AbstractCacheGear)gearsRegistry).getCacheID() + " successfully created or retrieved.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        reply.put(REPLY_RC, 1);
                        reply.put(MomMsgTranslator.MSG_BODY, "Exception while creating gears registry... Have a look at Ariane server logs ! ");
                    }
                }

                break;
            case OPERATION_MAKE_COMPONENT_REGISTRY:
                properties = new Properties();

                if (message.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_NAME)==null) {
                    isValid = false;
                    reply.put(REPLY_RC, 1);
                    reply.put(MomMsgTranslator.MSG_BODY, "Operation parameters are not valid... " + InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_NAME + " is not defined !");
                } else properties.put(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_NAME, message.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_NAME).toString());

                if (isValid && message.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_ID)==null) {
                    isValid = false;
                    reply.put(REPLY_RC, 1);
                    reply.put(MomMsgTranslator.MSG_BODY, "Operation parameters are not valid... " + InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_ID + " is not defined !");
                } else {
                    // default cache name = registry cache id
                    properties.put(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_ID, message.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_ID).toString());
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_NAME, message.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_ID).toString());
                }

                if (isValid && message.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_NAME)==null){
                    isValid = false;
                    reply.put(REPLY_RC, 1);
                    reply.put(MomMsgTranslator.MSG_BODY, "Operation parameters are not valid... " + InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_NAME + " is not defined !");
                } else properties.put(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_NAME, message.get(InjectorRegistryFactory.INJECTOR_COMPONENTS_REGISTRY_CACHE_NAME).toString());

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_MGR_NAME)==null) {
                    isValid=false;
                    reply.put(REPLY_RC, 1);
                    reply.put(MomMsgTranslator.MSG_BODY, "Operation parameters are not valid... " + CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_MGR_NAME + " is not defined !");
                } else properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_MGR_NAME, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_MGR_NAME).toString());

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_NAME)!=null)
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_NAME, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_NAME).toString());

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_PASSIVATION)!=null)
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_PASSIVATION, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_PASSIVATION).toString());
                else properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_PASSIVATION, "false");

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_FETCH)!=null)
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_FETCH, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_FETCH).toString());
                else properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_FETCH, "true");

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_IGNORE_DIFF)!=null)
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_IGNORE_DIFF, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_IGNORE_DIFF).toString());
                else properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_IGNORE_DIFF, "false");

                if (isValid && message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_PURGE_STARTUP)!=null)
                    properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_PURGE_STARTUP, message.get(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_PURGE_STARTUP).toString());
                else properties.put(CacheManagerEmbeddedInfinispanImpl.INJECTOR_CACHE_PERSISTENCE_SF_PURGE_STARTUP, "false");

                if (isValid) {
                    try {
                        componentsRegistry = this.makeComponentsRegistry(properties);
                        if (componentsRegistry==null) {
                            reply.put(REPLY_RC, 1);
                            reply.put(MomMsgTranslator.MSG_BODY, "Operation parameters are not valid... Have a look at Ariane server logs !");
                        } else {
                            reply.put(REPLY_RC, 0);
                            reply.put(MomMsgTranslator.MSG_BODY, "Component registry " + ((AbstractCacheComponent)componentsRegistry).getCacheID() + " successfully created or retrieved.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        reply.put(REPLY_RC, 1);
                        reply.put(MomMsgTranslator.MSG_BODY, "Exception while creating components registry... Have a look at Ariane server logs ! ");
                    }
                }
                break;
            case OPERATION_NOT_DEFINED:
                reply.put(REPLY_RC, 1);
                reply.put(MomMsgTranslator.MSG_BODY, "Operation not defined ! ");
                break;
            default:
                break;
        }

        return reply;
    }


    @Override
    public InjectorGearsRegistry makeGearsRegistry(Dictionary properties) {
        return InjectorMessagingBootstrap.getInjectorRegistryFactory().makeGearsRegistry(properties);
    }

    @Override
    public InjectorComponentsRegistry makeComponentsRegistry(Dictionary properties) {
        return InjectorMessagingBootstrap.getInjectorRegistryFactory().makeComponentsRegistry(properties);
    }
}