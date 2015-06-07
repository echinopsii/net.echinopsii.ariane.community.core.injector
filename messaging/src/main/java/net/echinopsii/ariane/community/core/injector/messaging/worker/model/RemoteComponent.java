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
package net.echinopsii.ariane.community.core.injector.messaging.worker.model;

import net.echinopsii.ariane.community.core.injector.base.model.Component;

import java.io.Serializable;
import java.util.Date;

public class RemoteComponent implements Component, Serializable {

    private static final long serialVersionUID = 1L;

    private String componentId = null;
    private String componentName = null;
    private String componentType = null;
    private int nextAction = -1;
    private boolean refreshing = false;
    private Date lastRefresh = null;
    private String attachedGearId = null;

    private String gearAdminQueue = null;

    @Override
    public String getComponentId() {
        return componentId;
    }

    @Override
    public String getComponentName() {
        return componentName;
    }

    @Override
    public String getComponentType() {
        return componentType;
    }

    @Override
    public int getNextAction() {
        return nextAction;
    }

    @Override
    public boolean isRefreshing() {
        return refreshing;
    }

    @Override
    public Date getLastRefresh() {
        return lastRefresh;
    }

    @Override
    public String getAttachedGearId() {
        return attachedGearId;
    }

    @Override
    public void setAttachedGearId(String attachedGearId) {
        this.attachedGearId = attachedGearId;
    }

    public String getGearAdminQueue() {
        return gearAdminQueue;
    }

    public void setGearAdminQueue(String gearAdminQueue) {
        this.gearAdminQueue = gearAdminQueue;
    }

    @Override
    public void refresh() {

    }

    @Override
    public void refreshAndMap() {

    }
}
