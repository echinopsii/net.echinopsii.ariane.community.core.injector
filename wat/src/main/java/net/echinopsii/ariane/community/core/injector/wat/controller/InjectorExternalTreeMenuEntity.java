/**
 * Injector wat
 * Injectors External Tree Menu Entity Controller
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

import net.echinopsii.ariane.community.core.injector.wat.InjectorWatBootstrap;
import net.echinopsii.ariane.community.core.portal.base.model.TreeMenuEntity;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

public class InjectorExternalTreeMenuEntity {

    private static String TID_NOT_DEFINED = "not defined";

    private String treeMenuEntityID = TID_NOT_DEFINED;
    private TreeMenuEntity treeMenuEntity;

    public void init() throws IOException {
        if (treeMenuEntity == null && treeMenuEntityID != null && !treeMenuEntityID.equals(TID_NOT_DEFINED))
            treeMenuEntity = InjectorWatBootstrap.getTreeMenuRootsRegistry().getTreeMenuEntityFromID(treeMenuEntityID);
        if (treeMenuEntity == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("/ariane/views/injectors/main.jsf");
        }
    }

    public String getTreeMenuEntityID() {
        return treeMenuEntityID;
    }

    public void setTreeMenuEntityID(String treeMenuEntityID) {
        this.treeMenuEntityID = treeMenuEntityID;
    }

    public TreeMenuEntity getTreeMenuEntity() {
        return treeMenuEntity;
    }
}
