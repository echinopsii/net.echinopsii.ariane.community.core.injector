/**
 * Injector base
 * Injector mapreduce prefix key mapper
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

package net.echinopsii.ariane.community.core.injector.base.mapreduce;

import org.infinispan.distexec.mapreduce.Collector;
import org.infinispan.distexec.mapreduce.Mapper;

public class PrefixKeyMapper implements Mapper<String, Object, String, String> {

    private static final long serialVersionUID = -64880662106872372L;
    private String prefix = null;

    public PrefixKeyMapper(String prefix) {
        super();
        this.prefix = prefix;
    }

    @Override
    public void map(String key, Object value, Collector<String, String> collector) {
        if (key.startsWith(this.prefix))
            collector.emit(key, key);
    }

}