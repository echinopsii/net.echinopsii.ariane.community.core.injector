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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RemoteComponent implements Component, Serializable {

    private static final long serialVersionUID = 1L;

    private String componentId = null;
    private String componentName = null;
    private String componentType = null;
    private int nextAction = -1;
    private boolean refreshing = false;
    private String jsonLastRefresh = null;
    private Date lastRefresh = null;
    private String attachedGearId = null;

    private String componentAdminQueue = null;

    private static Date parse( String input ) throws java.text.ParseException {
        //NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
        //things a bit.  Before we go on we have to repair this.
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );
        //this is zero time so we need to add that TZ indicator for
        if ( input.endsWith( "Z" ) ) {
            input = input.substring( 0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;
            String s0 = input.substring( 0, input.length() - inset );
            String s1 = input.substring( input.length() - inset, input.length() );
            input = s0 + "GMT" + s1;
        }
        return df.parse( input );
    }

    @Override
    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    @Override
    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    @Override
    public int getNextAction() {
        return nextAction;
    }

    public void setNextAction(int nextAction) {
        this.nextAction = nextAction;
    }

    @Override
    public boolean isRefreshing() {
        return refreshing;
    }

    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
    }

    public String getJsonLastRefresh() {
        return jsonLastRefresh;
    }

    public void setJsonLastRefresh(String jsonLastRefresh) throws ParseException {
        this.jsonLastRefresh = jsonLastRefresh;
        this.lastRefresh = parse(this.jsonLastRefresh);
    }

    @Override
    public Date getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(Date lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    @Override
    public String getAttachedGearId() {
        return attachedGearId;
    }

    @Override
    public void setAttachedGearId(String attachedGearId) {
        this.attachedGearId = attachedGearId;
    }

    public String getComponentAdminQueue() {
        return componentAdminQueue;
    }

    public void setComponentAdminQueue(String componentAdminQueue) {
        this.componentAdminQueue = componentAdminQueue;
    }

    @Override
    public void refresh() {

    }

    @Override
    public void refreshAndMap() {

    }
}
