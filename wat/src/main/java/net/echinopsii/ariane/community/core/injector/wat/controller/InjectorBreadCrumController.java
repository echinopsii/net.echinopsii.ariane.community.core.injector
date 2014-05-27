/**
 * Injector wat
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
package net.echinopsii.ariane.community.core.injector.wat.controller;

import net.echinopsii.ariane.community.core.injector.wat.consumer.InjectorPluginFacesMBeanRegistryConsumer;
import net.echinopsii.ariane.community.core.injector.wat.consumer.InjectorTreeMenuRootsRegistryServiceConsumer;
import net.echinopsii.ariane.community.core.portal.base.model.TreeMenuEntity;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import java.util.ArrayList;

/**
 * Injector bread crum controller transform injector roots registry into primefaces menu model to be used in injector layout bread crum component.<br/>
 * This is a request managed bean
 */
public class InjectorBreadCrumController {
    private static final Logger log = LoggerFactory.getLogger(InjectorBreadCrumController.class);
    private static String MAIN_MENU_INJECTOR_CONTEXT = InjectorPluginFacesMBeanRegistryConsumer.getInstance().getPortalPluginFacesMBeanRegistry().getRegisteredServletContext().getContextPath();

    private MenuModel model     = new DefaultMenuModel();

    private MenuItem createMenuItemFromEntity(TreeMenuEntity entity) {
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
                                MAIN_MENU_INJECTOR_CONTEXT + "/views/main.jsf");
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
                            MAIN_MENU_INJECTOR_CONTEXT + "/views/main.jsf");
        item.setValue("Injector");
        item.setStyleClass("menuItem");
        return item;
    }

    public MenuModel getModel() {
        FacesContext context = FacesContext.getCurrentInstance();
        String contextAddress = MAIN_MENU_INJECTOR_CONTEXT + context.getExternalContext().getRequestServletPath();
        ArrayList<TreeMenuEntity> orderedBreadScrumMenuFromRootToLeaf = new ArrayList<TreeMenuEntity>();
        if (InjectorTreeMenuRootsRegistryServiceConsumer.getInstance()!=null) {
            TreeMenuEntity leaf   = InjectorTreeMenuRootsRegistryServiceConsumer.getInstance().getTreeMenuRootsRegistry().getTreeMenuEntityFromContextAddress(contextAddress);
            if (leaf!=null) {
                orderedBreadScrumMenuFromRootToLeaf.add(0,leaf);
                TreeMenuEntity parent = leaf.getParentTreeMenuEntity();
                while (parent!=null) {
                    orderedBreadScrumMenuFromRootToLeaf.add(0,parent);
                    parent = parent.getParentTreeMenuEntity();
                }

                model.addMenuItem(createRootMenuItem());
                for (TreeMenuEntity dir: orderedBreadScrumMenuFromRootToLeaf)
                    model.addMenuItem(createMenuItemFromEntity(dir));
            }
        }
        return model;
    }

}