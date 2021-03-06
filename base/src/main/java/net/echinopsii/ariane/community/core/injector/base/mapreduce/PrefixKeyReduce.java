/**
 * Injector base
 * Injector mapreduce prefix key reduce
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

import org.infinispan.distexec.mapreduce.Reducer;

import java.util.Iterator;

public class PrefixKeyReduce implements Reducer<String, String> {
    @Override
    public String reduce(String reducedKey, Iterator<String> iter) {
        return reducedKey;
    }
}