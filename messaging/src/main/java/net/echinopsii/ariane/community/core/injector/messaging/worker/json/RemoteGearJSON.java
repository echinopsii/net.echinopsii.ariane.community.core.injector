/**
 *
 *
 * Copyright (C) 2015 mffrench
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
package net.echinopsii.ariane.community.core.injector.messaging.worker.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.injector.messaging.worker.model.RemoteGear;

import java.io.IOException;

public class RemoteGearJSON {

    public final static RemoteGear JSON2RemoteGear(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        RemoteGear gear = mapper.readValue(payload, RemoteGear.class);
        return gear;
    }
}
