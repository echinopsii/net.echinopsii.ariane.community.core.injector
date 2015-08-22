/**
 * Injector wat
 * Injectors External Cache Components Controller
 * Copyright (C) 2015 echinopsii
 * Author: mffrench
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

import net.echinopsii.ariane.community.core.injector.base.model.Component;
import net.echinopsii.ariane.community.core.injector.base.registry.InjectorComponentsRegistry;
import net.echinopsii.ariane.community.core.injector.wat.InjectorWatBootstrap;
import net.echinopsii.ariane.community.core.portal.base.model.TreeMenuEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InjectorExternalCacheComponents implements Serializable, Runnable {
    private static final Logger log = LoggerFactory.getLogger(InjectorExternalCacheComponents.class);
    private static String TID_NOT_DEFINED = "not defined";

    private String treeMenuEntityID = TID_NOT_DEFINED;
    private TreeMenuEntity treeMenuEntity;
    private String treeMenuEntityValue;
    private InjectorComponentsRegistry componentsRegistry;

    private boolean running = false;
    private Thread  thread  ;

    private List<Component> cachedEntityList;
    private List<Component> filteredCachedEntityList;

    public void init() {
        if (treeMenuEntity == null && treeMenuEntityID != null && !treeMenuEntityID.equals(TID_NOT_DEFINED))
            treeMenuEntity = InjectorWatBootstrap.getTreeMenuRootsRegistry().getTreeMenuEntityFromID(treeMenuEntityID);
        if (treeMenuEntity != null && componentsRegistry == null)
            componentsRegistry = InjectorWatBootstrap.getInjectorRegistryFactory().getComponentsRegistry(treeMenuEntity.getRemoteInjectorTreeEntityComponentsCacheId());
        if (treeMenuEntity != null && treeMenuEntityValue == null)
            treeMenuEntityValue = treeMenuEntity.getValue();

        cachedEntityList = new ArrayList<>();
        if (componentsRegistry!=null) {
            for (String key: componentsRegistry.keySetFromPrefix(treeMenuEntity.getRemoteInjectorTreeEntityComponentsCacheId()))
                cachedEntityList.add(componentsRegistry.getEntityFromCache(key));
        }
        thread = new Thread(this);
        thread.start();
    }

    @PreDestroy
    public void clear(){
        running = false;
        if (thread!=null) {
            thread.interrupt();
            while(thread.isAlive()) {
                log.info("Cache controller associated to session {} is stopping...", FacesContext.getCurrentInstance().getExternalContext().getSession(false));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            thread = null;
        }
        cachedEntityList.clear();
        log.info("Cache controller associated to session {} is stopped...", FacesContext.getCurrentInstance().getExternalContext().getSession(false));
    }

    private void refresh() {
        cachedEntityList = new ArrayList<>();
        if (componentsRegistry!=null) {
            for (String key: componentsRegistry.keySetFromPrefix(treeMenuEntity.getRemoteInjectorTreeEntityComponentsCacheId()))
                cachedEntityList.add(componentsRegistry.getEntityFromCache(key));
        }
    }

    public String getTreeMenuEntityID() {
        return treeMenuEntityID;
    }

    public void setTreeMenuEntityID(String treeMenuEntityID) {
        this.treeMenuEntityID = treeMenuEntityID;
    }

    @Override
    public void run() {
        running = true;
        while(running) {
            refresh();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                if (running)
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                else
                    log.info("Cache controller associated to session {} is stopping...", FacesContext.getCurrentInstance().getExternalContext().getSession(false));
            }
        }
    }

    public List<Component> getCachedEntityList() {
        return cachedEntityList;
    }

    public void setCachedEntityList(List<Component> cachedEntityList) {
        this.cachedEntityList = cachedEntityList;
    }

    public List<Component> getFilteredCachedEntityList() {
        return filteredCachedEntityList;
    }

    public void setFilteredCachedEntityList(List<Component> filteredCachedEntityList) {
        this.filteredCachedEntityList = filteredCachedEntityList;
    }

    public String getEntityName(Component entity) {
        return entity.getComponentName();
    }

    public String getEntityType(Component entity) {
        return entity.getComponentType();
    }

    public String getTreeMenuEntityValue() {
        return treeMenuEntityValue;
    }

    public void setTreeMenuEntityValue(String treeMenuEntityValue) {
        this.treeMenuEntityValue = treeMenuEntityValue;
    }

    public String getEntityLastRefresh(Component entity) {
        String ret ;
        if (entity.isRefreshing()) {
            ret = "NOW !";
        } else {
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd '-' hh:mm:ss a zzz");
            ret = ft.format(entity.getLastRefresh());
        }
        return ret;
    }

    public void refreshEntity(Component entity) {
        entity.refreshAndMap();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                "RabbitMQ injector cache entity has been succesfully refreshed !",
                "RabbitMQ injector cache entity name : " + getEntityName(entity));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void refreshCache() {
        //RabbitmqInjectorBootstrap.getDirectoryAkkaGear().refresh();
        if (componentsRegistry!=null) {
            for (String key: componentsRegistry.keySetFromPrefix(treeMenuEntity.getRemoteInjectorTreeEntityComponentsCacheId()))
                componentsRegistry.getEntityFromCache(key).refreshAndMap();
        }

        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                "RabbitMQ injector cache is refreshed !",
                "RabbitMQ injector cache size : " + componentsRegistry.keySetFromPrefix(treeMenuEntity.getRemoteInjectorTreeEntityComponentsCacheId()).size());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
