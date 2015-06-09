/**
 * Injector base
 * Injector model abstract component
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

import java.io.Serializable;
import java.util.Date;

public abstract class AbstractComponent implements Component, Serializable {

    private int nextAction;
    private boolean isRefreshing = false;
    private Date lastRefresh = new Date();
    private String lastRefreshDuration;
    private String attachedGearId ;

    @Override
    public int getNextAction() {
        return nextAction;
    }

    @Override
    public void setNextAction(int nextAction) {
        this.nextAction = nextAction;
    }

    @Override
    public boolean isRefreshing() {
        return isRefreshing;
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
    }

    @Override
    public Date getLastRefresh() {
        return lastRefresh;
    }

    @Override
    public void setLastRefresh(Date lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    @Override
    public String getLastRefreshDuration() {
        return lastRefreshDuration;
    }

    @Override
    public void setLastRefreshDuration(String lastRefreshDuration) {
        this.lastRefreshDuration = lastRefreshDuration;
    }

    public String getAttachedGearId() {
        return attachedGearId;
    }

    public void setAttachedGearId(String attachedGearId) {
        this.attachedGearId = attachedGearId;
    }
}