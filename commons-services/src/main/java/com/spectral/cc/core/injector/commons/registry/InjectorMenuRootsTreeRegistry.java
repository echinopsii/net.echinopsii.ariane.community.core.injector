/**
 * Injector Commons Services bundle
 * Root Injector Registry Interface
 * Copyright (C) 2013 Mathilde Ffrench
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
package com.spectral.cc.core.injector.commons.registry;

import com.spectral.cc.core.injector.commons.model.InjectorMenuEntity;

import java.util.TreeSet;

public interface InjectorMenuRootsTreeRegistry {
    public InjectorMenuEntity registerRootInjectorEntity(InjectorMenuEntity injectorMenuEntity) throws Exception;
    public InjectorMenuEntity unregisterRootInjectorEntity(InjectorMenuEntity injectorMenuEntity) throws Exception;

    public TreeSet<InjectorMenuEntity> getRootInjectorEntities();
    public InjectorMenuEntity getInjectorEntityFromValue(String value);
    public InjectorMenuEntity getInjectorEntityFromID(String id);
}