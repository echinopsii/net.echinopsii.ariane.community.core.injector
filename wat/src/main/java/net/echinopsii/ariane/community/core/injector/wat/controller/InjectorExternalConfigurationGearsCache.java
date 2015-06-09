/**
 * Injector wat
 * Injectors External Configuration Components Cache Controller
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

import net.echinopsii.ariane.community.core.injector.base.registry.InjectorGearsRegistry;
import net.echinopsii.ariane.community.core.injector.wat.InjectorWatBootstrap;
import net.echinopsii.ariane.community.core.portal.base.model.TreeMenuEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

public class InjectorExternalConfigurationGearsCache {

    private static String TID_NOT_DEFINED = "not defined";

    private String treeMenuEntityID = TID_NOT_DEFINED;
    private TreeMenuEntity treeMenuEntity;
    private InjectorGearsRegistry gearsRegistry;

    private Properties gearsCacheConf;
    private List<String> keys = new ArrayList<>() ;

    public void init() {
        if (treeMenuEntity == null && treeMenuEntityID != null && !treeMenuEntityID.equals(TID_NOT_DEFINED))
            treeMenuEntity = InjectorWatBootstrap.getTreeMenuRootsRegistry().getTreeMenuEntityFromID(treeMenuEntityID);
        if (treeMenuEntity!=null && gearsRegistry==null)
            gearsRegistry = InjectorWatBootstrap.getInjectorRegistryFactory().getGearsRegistry(treeMenuEntity.getRemoteInjectorTreeEntityGearsCacheId());
        if (gearsRegistry!=null) {
            gearsCacheConf = gearsRegistry.getConfiguration();
            TreeSet<Object> sortedKeys = new TreeSet<>();
            sortedKeys.addAll(gearsCacheConf.keySet());
            for (Object key: sortedKeys) {
                if (key instanceof String)
                    keys.add((String)key);
            }
        }
    }

    public List<String> getKeys() {
        return keys;
    }

    public String getGearsCacheConf(String key) {
        String ret = "";
        if (gearsCacheConf!=null)
            ret = gearsCacheConf.get(key).toString();
        return ret;
    }

    public String getTreeMenuEntityID() {
        return treeMenuEntityID;
    }

    public void setTreeMenuEntityID(String treeMenuEntityID) {
        this.treeMenuEntityID = treeMenuEntityID;
    }
}
