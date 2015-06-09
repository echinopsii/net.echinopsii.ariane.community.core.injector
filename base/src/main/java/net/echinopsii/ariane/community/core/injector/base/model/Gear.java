/**
 * Injector base
 * Injector Gear interface
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

/**
 * Gear interface provide some commons method to implemnents injector plugins gears (to be binded to akka actors)
 */
public interface Gear {

    /**
     * Get the gear id (must be unique in the gear registry context).
     *
     * @return the gear id
     */
    public String   getGearId();

    /**
     * Set the gear id (must be unique in the gear registry context).
     * @param id
     */
    public void   setGearId(String id);

    /**
     * Get the gear name
     *
     * @return the gear name
     */
    public String getGearName();

    /**
     * Set the gear name
     *
     * @param gearName to set
     */
    public void setGearName(String gearName);

    /**
     * Get the gear description
     *
     * @return the gear description
     */
    public String getGearDescription();

    /**
     * Set the gear description
     *
     * @param gearDescription to set
     */
    public void setGearDescription(String gearDescription);

    /**
     * Get the gear sleeping period
     *
     * @return the gear sleeping period
     */
    public int getSleepingPeriod();

    /**
     * Set the sleeping period
     *
     * @param sleepingPeriod to set
     */
    public void setSleepingPeriod(int sleepingPeriod);

    /**
     * Start the gear
     */
    public void    start();

    /**
     * Stop the gear
     */
    public void    stop();

    /**
     * Return the gear status
     *
     * @return true if gear is running, false if not
     */
    public boolean isRunning();

    /**
     * Set running bool
     *
     * @param isRunning to set
     */
    public void setRunning(boolean isRunning);
}