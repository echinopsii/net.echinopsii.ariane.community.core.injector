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

import net.echinopsii.ariane.community.core.injector.base.model.Gear;
import net.echinopsii.ariane.community.core.injector.messaging.InjectorMessagingBootstrap;
import net.echinopsii.ariane.community.messaging.api.MomClient;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RemoteGear implements Gear, Serializable {

    private static final long serialVersionUID = 1L;

    private String gearId;
    private String gearName;
    private String gearDescription;
    private int sleepingPeriod;
    private boolean running = false;

    private String gearAdminQueue;

    private MomClient client = null;

    @Override
    public String getGearId() {
        return gearId;
    }

    @Override
    public void setGearId(String id) {
        gearId=id;
    }

    @Override
    public String getGearName() {
        return gearName;
    }

    @Override
    public void setGearName(String gearName) {
        this.gearName = gearName;
    }

    @Override
    public String getGearDescription() {
        return gearDescription;
    }

    @Override
    public void setGearDescription(String gearDescription) {
        this.gearDescription = gearDescription;
    }

    @Override
    public int getSleepingPeriod() {
        return sleepingPeriod;
    }

    @Override
    public void setSleepingPeriod(int sleepingPeriod) {
        this.sleepingPeriod = sleepingPeriod;
    }

    public String getGearAdminQueue() {
        return gearAdminQueue;
    }

    public void setGearAdminQueue(String gearAdminQueue) {
        this.gearAdminQueue = gearAdminQueue;
    }

    @Override
    public void start() {
        if (InjectorMessagingBootstrap.sharedMoMConnection!=null && gearAdminQueue!=null) {
            Map<String, Object> message = new HashMap<String, Object>();
            message.put(MomMsgTranslator.OPERATION_FDN, "START");
            InjectorMessagingBootstrap.sharedMoMConnection.createRequestExecutor().FAF(message, gearAdminQueue);
        }
    }

    @Override
    public void stop() {
        if (InjectorMessagingBootstrap.sharedMoMConnection!=null && gearAdminQueue!=null) {
            Map<String, Object> message = new HashMap<String, Object>();
            message.put(MomMsgTranslator.OPERATION_FDN, "STOP");
            InjectorMessagingBootstrap.sharedMoMConnection.createRequestExecutor().FAF(message, gearAdminQueue);
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void setRunning(boolean isRunning) {
        this.running = isRunning;
    }
}
