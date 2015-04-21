/**
 * Injector Messaging Module
 * Remote Component Injector Messaging worker
 * Copyright (C) 21/04/15 echinopsii
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

package net.echinopsii.ariane.community.core.injector.messaging.worker;

import net.echinopsii.ariane.community.core.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.core.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RemoteComponentWorker implements AppMsgWorker {
    private static final Logger log = LoggerFactory.getLogger(RemoteComponentWorker.class);

    @Override
    public Map<String, Object> apply(Map<String, Object> message) {
        log.debug("Remote Injector Component Worker on  : {" + message.get(MomMsgTranslator.MSG_APPLICATION_ID) +  " }...");
        //TODO WORK
        log.debug("Remote Injector Component Worker return DONE");
        Map<String, Object> reply = new HashMap<String, Object>();
        reply.put(MomMsgTranslator.MSG_BODY, "DONE");
        return reply;
    }
}