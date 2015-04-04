/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 03/04/15 echinopsii
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

import java.util.Dictionary;

public interface InjectorRegistryFactory {

    public static final String INJECTORY_GEARS_REGISTRY_TYPE="gears";
    public static final String INJECTOR_GEARS_REGISTRY_CACHE_CONFIGURATION_PATH_KEY = "ariane.community.injector.gears.cache.configuration.path";

    public static final String INJECTOR_COMPONENTS_REGISTRY_TYPE="components";
    public static final String INJECTOR_COMPONENTS_REGISTRY_CACHE_CONFIGURATION_PATH_KEY = "ariane.community.injector.components.cache.configuration.path";

    public static final String INJECTOR_REGISTRY_NAME="ariane.community.injector.registry.name";
    public static final String INJECTOR_REGISTRY_TYPE="ariane.community.injector.registry.type";
    public static final String INJECTOR_REGISTRY_CACHE_ID="ariane.community.injector.registry.cache.id";
    public static final String INJECTOR_REGISTRY_CACHE_NAME="ariane.community.injector.registry.cache.name";

    public boolean isValidProperties(Dictionary properties);

    public InjectorGearsRegistry makeGearsRegistry(Dictionary properties);
    public InjectorComponentsRegistry makeComponentsRegistry(Dictionary properties);
}