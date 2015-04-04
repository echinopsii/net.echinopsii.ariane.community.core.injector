/**
 * Injector base
 * Injector model cache
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

import java.util.Collection;
import java.util.Properties;

public interface Cache<E,I> {
    public Cache      start();
    public void       stop();
    public Properties getConfiguration();

    public Collection<E> values();
    public boolean       containsValue(E entity);
    public int           size();

    public void putEntityToCache(E Entity);
    public void removeEntityFromCache(E Entity);
    public E    getEntityFromCache(I id);
}