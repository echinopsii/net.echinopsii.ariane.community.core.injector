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

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.injector.messaging.worker.model.RemoteComponent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RemoteComponentJSON {

    public final static String REMOTE_COMPONENT_ID = "componentId";
    public final static String REMOTE_COMPONENT_NAME = "componentName";
    public final static String REMOTE_COMPONENT_TYPE  = "componentType";
    public final static String REMOTE_COMPONENT_NEXT_ACTION = "nextAction";
    public final static String REMOTE_COMPONENT_REFRESHING = "refreshing";
    public final static String REMOTE_COMPONENT_JSON_LAST_REFRESH = "jsonLastRefresh";
    public final static String REMOTE_COMPONENT_ATTACHED_GEAR_ID = "attachedGearId";
    public final static String REMOTE_COMPONENT_ADMIN_QUEUE = "componentAdminQueue";


    public final static void remoteComponent2JSON(RemoteComponent remoteComponent, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeStringField(REMOTE_COMPONENT_ID, remoteComponent.getComponentId());
        jgenerator.writeStringField(REMOTE_COMPONENT_NAME, remoteComponent.getComponentName());
        jgenerator.writeStringField(REMOTE_COMPONENT_TYPE, remoteComponent.getComponentType());
        jgenerator.writeNumberField(REMOTE_COMPONENT_NEXT_ACTION, remoteComponent.getNextAction());
        jgenerator.writeBooleanField(REMOTE_COMPONENT_REFRESHING, remoteComponent.isRefreshing());
        jgenerator.writeStringField(REMOTE_COMPONENT_JSON_LAST_REFRESH, remoteComponent.getJsonLastRefresh());
        jgenerator.writeStringField(REMOTE_COMPONENT_ATTACHED_GEAR_ID, remoteComponent.getAttachedGearId());
        jgenerator.writeStringField(REMOTE_COMPONENT_ADMIN_QUEUE, remoteComponent.getComponentAdminQueue());
        jgenerator.writeEndObject();
    }

    public final static String remoteComponent2JSON(RemoteComponent remoteComponent) throws IOException {
        JsonFactory jFactory = new JsonFactory();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        JsonGenerator jgenerator = jFactory.createGenerator(outStream, JsonEncoding.UTF8);
        remoteComponent2JSON(remoteComponent, jgenerator);
        jgenerator.close();
        return ToolBox.getOuputStreamContent(outStream, "UTF-8");
    }

    public final static RemoteComponent JSON2RemoteComponent(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        RemoteComponent component = mapper.readValue(payload, RemoteComponent.class);
        return component;
    }
}
