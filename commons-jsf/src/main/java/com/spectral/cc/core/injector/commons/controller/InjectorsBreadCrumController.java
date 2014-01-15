/**
 * Injector Commons JSF bundle
 * Injectors BreadCrum Controller
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
package com.spectral.cc.core.injector.commons.controller;

import com.spectral.cc.core.injector.commons.consumer.InjectorRootsTreeRegistryServiceConsumer;
import com.spectral.cc.core.injector.commons.model.InjectorEntity;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import java.util.ArrayList;

public class InjectorsBreadCrumController {
    private static final Logger log = LoggerFactory.getLogger(InjectorsBreadCrumController.class);
    private static String MAIN_MENU_DIRECTORY_CONTEXT = "/CCinjector/";

    private MenuModel model     = new DefaultMenuModel();

    private MenuItem createMenuItemFromEntity(InjectorEntity entity) {
        FacesContext context = FacesContext.getCurrentInstance();
        MenuItem item = new MenuItem();
        item.setId(entity.getId());
        if (entity.getContextAddress()!=null && entity.getContextAddress()!="")
            item.setUrl(context.getExternalContext().getRequestScheme() + "://" +
                                context.getExternalContext().getRequestServerName() + ":" +
                                context.getExternalContext().getRequestServerPort() +
                                entity.getContextAddress());
        else
            item.setUrl(context.getExternalContext().getRequestScheme() + "://" +
                                context.getExternalContext().getRequestServerName() + ":" +
                                context.getExternalContext().getRequestServerPort() +
                                MAIN_MENU_DIRECTORY_CONTEXT + "views/main.jsf");
        item.setValue(entity.getValue());
        item.setStyleClass("menuItem");

        return item;
    }

    private MenuItem createRootMenuItem() {
        FacesContext context = FacesContext.getCurrentInstance();
        MenuItem item = new MenuItem();
        item.setId("mainDir");
        item.setUrl(context.getExternalContext().getRequestScheme() + "://" +
                            context.getExternalContext().getRequestServerName() + ":" +
                            context.getExternalContext().getRequestServerPort() +
                            MAIN_MENU_DIRECTORY_CONTEXT + "views/main.jsf");
        item.setValue("Injector");
        item.setStyleClass("menuItem");
        return item;
    }

    public MenuModel getModel() {
        FacesContext context = FacesContext.getCurrentInstance();
        String requestServletPath = context.getExternalContext().getRequestServletPath();
        String values[] = requestServletPath.split("/");
        String id = values[values.length-1].split("\\.")[0]+"DirID" ;
        log.debug("requestServletPath : {} ; value : {}", new Object[]{requestServletPath,id});
        log.debug("Get Menu Model...");
        ArrayList<InjectorEntity> orderedBreadScrumMenuFromRootToLeaf = new ArrayList<InjectorEntity>();
        if (InjectorRootsTreeRegistryServiceConsumer.getInstance()!=null) {
            InjectorEntity leaf   = InjectorRootsTreeRegistryServiceConsumer.getInstance().getInjectorRootsTreeRegistry().getInjectorEntityFromID(id);
            if (leaf!=null) {
                orderedBreadScrumMenuFromRootToLeaf.add(0,leaf);
                InjectorEntity parent = leaf.getParentInjector();
                while (parent!=null) {
                    orderedBreadScrumMenuFromRootToLeaf.add(0,parent);
                    parent = parent.getParentInjector();
                }

                model.addMenuItem(createRootMenuItem());
                for (InjectorEntity dir: orderedBreadScrumMenuFromRootToLeaf)
                    model.addMenuItem(createMenuItemFromEntity(dir));
            }
        }
        return model;
    }

}