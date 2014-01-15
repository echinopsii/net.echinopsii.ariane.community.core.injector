/**
 * Injector Commons JSF bundle
 * Portal Facelets Resource Resolver Service consumer singleton
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

package com.spectral.cc.core.injector.commons.consumer;

import com.spectral.cc.core.portal.commons.facesplugin.FaceletsResourceResolverService;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(publicFactory = false, factoryMethod = "getInstance")
@Instantiate
public class InjectorFaceletsResourceResolverServicesConsumer {
    private static final Logger log = LoggerFactory.getLogger(InjectorFaceletsResourceResolverServicesConsumer.class);
    private static InjectorFaceletsResourceResolverServicesConsumer INSTANCE;

    @Requires(filter="(targetCCcomponent=injector)")
    private FaceletsResourceResolverService[] faceletsResolverList;

    public FaceletsResourceResolverService[] getFaceletsResourceResolverServices() {
        log.debug("{} FaceletsResourceResolverService are bound to this consumer...", (faceletsResolverList!=null) ? faceletsResolverList.length : "0");
        return faceletsResolverList;
    }

    public static InjectorFaceletsResourceResolverServicesConsumer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InjectorFaceletsResourceResolverServicesConsumer();
        }
        return INSTANCE;
    }
}