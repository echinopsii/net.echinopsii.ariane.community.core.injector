/**
 * Injector base
 * Injector components registry interface
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

package net.echinopsii.ariane.community.core.injector.base.registry;

import java.util.List;
import net.echinopsii.ariane.community.core.injector.base.model.Cache;
import net.echinopsii.ariane.community.core.injector.base.model.Component;

public interface InjectorComponentsRegistry extends Cache<Component, String> {
    public List<String> keySetFromPrefix(String prefix);

    public InjectorComponentsRegistry setRegistryName(String serviceName);
    public InjectorComponentsRegistry setRegistryCacheID(String cacheID);
    public InjectorComponentsRegistry setRegistryCacheName(String cacheName);

    public void startRegistry();
    public void stopRegistry();
}