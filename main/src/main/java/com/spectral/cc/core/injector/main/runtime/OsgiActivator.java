/**
 * Injector Main
 * OSGI activator
 * Copyright (C) 2013 Mathilde Ffrench
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.spectral.cc.core.injector.main.runtime;

import com.spectral.cc.core.injector.commons.consumer.InjectorRootsTreeRegistryServiceConsumer;
import com.spectral.cc.core.injector.commons.model.InjectorMenuEntity;
import com.spectral.cc.core.portal.commons.consumer.MainMenuRegistryConsumer;
import com.spectral.cc.core.portal.commons.model.MainMenuEntity;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class OsgiActivator implements BundleActivator {

    private static final String INJECTOR_SERVICE_NAME                     = "Injector Main Service";
    private static final Logger log = LoggerFactory.getLogger(OsgiActivator.class);

    protected static ArrayList<MainMenuEntity>  injectorMainMenuEntityList = new ArrayList<MainMenuEntity>() ;
    protected static ArrayList<InjectorMenuEntity> injectorTreeEntityList     = new ArrayList<InjectorMenuEntity>();

    @Override
    public void start(BundleContext context) {
        new Thread(new Registrator()).start();
        log.info("{} is started...", new Object[]{INJECTOR_SERVICE_NAME});
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        log.info("{} is stopping...", new Object[]{INJECTOR_SERVICE_NAME});
        if (MainMenuRegistryConsumer.getInstance().getMainMenuEntityRegistry()!=null) {
            for(MainMenuEntity entity : injectorMainMenuEntityList) {
                MainMenuRegistryConsumer.getInstance().getMainMenuEntityRegistry().unregisterMainMenuEntity(entity);
            }
        }
        injectorMainMenuEntityList.clear();

        if (InjectorRootsTreeRegistryServiceConsumer.getInstance().getInjectorMenuRootsTreeRegistry()!=null) {
            for(InjectorMenuEntity entity : injectorTreeEntityList) {
                InjectorRootsTreeRegistryServiceConsumer.getInstance().getInjectorMenuRootsTreeRegistry().unregisterRootInjectorEntity(entity);
            }
        }
        injectorTreeEntityList.clear();

        log.info("{} is stopped...", new Object[]{INJECTOR_SERVICE_NAME});
    }
}