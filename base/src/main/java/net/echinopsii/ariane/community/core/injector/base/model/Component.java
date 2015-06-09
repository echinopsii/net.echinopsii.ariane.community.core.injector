/**
 * Injector base
 * Injector model component
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

import java.util.Date;

public interface Component {

    public final static int ACTION_CREATE = 0;
    public final static int ACTION_DELETE = 1;
    public final static int ACTION_UPDATE = 2;

    public String  getComponentId();
    public String  getComponentName();
    public String  getComponentType();

    public int     getNextAction();
    public void    setNextAction(int nextAction);

    public boolean isRefreshing();
    public void setRefreshing(boolean refreshing);

    public Date    getLastRefresh();
    public void    setLastRefresh(Date date);

    public String  getLastRefreshDuration();
    public void setLastRefreshDuration(String lastRefreshDuration);

    public String  getAttachedGearId();
    public void    setAttachedGearId(String attachedGearId);

    public void refresh();

    public void refreshAndMap();

}