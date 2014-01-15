/**
 * Injector Commons Services bundle
 * Injector Entity
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

package com.spectral.cc.core.injector.commons.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeSet;

public class InjectorEntity implements Comparable<InjectorEntity> {
    private static final Logger log = LoggerFactory.getLogger(InjectorEntity.class);

    private String id             = null;
    private String value          = null;
    private int    type           = 0;
    private String contextAddress = "";
    private String description    = "";
    private String icon           = "";

    private InjectorEntity          parent = null;
    private TreeSet<InjectorEntity> childs = new TreeSet<InjectorEntity>();

    public String getId() {
        return id;
    }

    public InjectorEntity setId(String id) {
        this.id = id;
        return this;
    }

    public InjectorEntity setValue(String value) {
        this.value = value;
        return this;
    }

    public String getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public InjectorEntity setType(int type) {
        this.type = type;
        return this;
    }

    public String getContextAddress() {
        return contextAddress;
    }

    public InjectorEntity setContextAddress(String contextAddress) {
        this.contextAddress = contextAddress;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public InjectorEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public InjectorEntity setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public InjectorEntity setParentInjector(InjectorEntity parent) {
        this.parent = parent;
        return this;
    }

    public InjectorEntity getParentInjector() {
        return parent;
    }

    public InjectorEntity addChildInjector(InjectorEntity child) {
        this.childs.add(child);
        return this;
    }

    public TreeSet<InjectorEntity> getChildsInjector() {
        return this.childs;
    }

    public InjectorEntity findInjectorEntityFromValue(String value_) {
        InjectorEntity ret = null;
        if (this.value.equals(value_)) {
            ret = this;
        } else {
            for (InjectorEntity entity : childs) {
                ret = entity.findInjectorEntityFromValue(value_);
                if (ret!=null)
                    break;
            }
        }
        return ret;
    }

    public InjectorEntity findInjectorEntityFromID(String id_) {
        InjectorEntity ret = null;
        if (this.id.equals(id_)) {
            ret = this;
        } else {
            for (InjectorEntity entity : childs) {
                ret = entity.findInjectorEntityFromID(id_);
                if (ret!=null)
                    break;
            }
        }
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InjectorEntity that = (InjectorEntity) o;

        if (!id.equals(that.id)) {
            return false;
        }
        if (!value.equals(that.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "InjectorEntity{" +
                       "id='" + id + '\'' +
                       ", value='" + value + '\'' +
                       '}';
    }

    @Override
    public int compareTo(InjectorEntity that) {
        return this.value.compareTo(that.getValue());
    }
}