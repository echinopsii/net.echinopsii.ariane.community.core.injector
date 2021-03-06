/**
 * Injector base
 * Injector model cache manager
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

import org.infinispan.Cache;

import java.io.File;
import java.util.Dictionary;
import java.util.Properties;

public interface CacheManager {
    public CacheManager start(File confFile);

    CacheManager start(Dictionary properties);

    public CacheManager stop();
    public boolean      isStarted();
    public Cache getCache(String id);
    public Properties getCacheConfiguration(Cache cache);
}