/**
 * Injector base
 * Injector model abstract simple gear
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public abstract class AbstractSimpleGear implements Runnable, Gear, Serializable {

    private static final Logger log = LoggerFactory.getLogger(AbstractSimpleGear.class);

    private           String  gearId     ;
    private           String  gearName   ;
    private           String  gearDesc   ;
    private transient Thread  gearThread ;
    private volatile  boolean running = false;

    @Override
    public String getGearName() {
        return gearName;
    }

    @Override
    public void setGearName(String gearName_) {
        this.gearName = gearName_;
    }

    @Override
    public String getGearDescription() {
        return gearDesc;
    }

    @Override
    public void setGearDescription(String gearDescription) {
        this.gearDesc = gearDescription;
    }

    @Override
    public void start() {
        if (!running) {
            gearThread = new Thread(this);
            gearThread.start();
        }
    }

    @Override
    public void stop() {
        running = false;
        if (gearThread!=null) {
            gearThread.interrupt();
            while(gearThread.isAlive()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            gearThread = null;
        }
        log.info("{} is stopped", gearName);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void setRunning(boolean isRunning) {
        this.running = isRunning;
    }

    @Override
    public String getGearId() {
        return gearId;
    }

    @Override
    public void setGearId(String id) {
        this.gearId = id;
    }
}