/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 8/11/14 echinopsii
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

import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.Cancellable;
import net.echinopsii.ariane.community.core.injector.base.InjectorAkkaSystemActivator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AbstractAkkaGear implements Gear, Serializable {

    private static final Logger log = LoggerFactory.getLogger(AbstractSimpleGear.class);

    private          String          gearId    ;
    private          String          gearName  ;
    private          String          gearDesc  ;
    private volatile boolean         running = false ;

    private volatile transient ActorRef          gearActor ;
    private volatile transient ActorRefFactory   gearActorRefFactory ;
    private volatile transient List<Cancellable> cancellableList = new ArrayList<Cancellable>();

    @Override
    public String getGearId() {
        return gearId;
    }

    @Override
    public void setGearId(String id) {
        this.gearId = id;
    }

    @Override
    public String getGearName() {
        return gearName;
    }

    @Override
    public void setGearName(String gearName_) {
        this.gearName = gearName_;
    }

    @Override
    public String getGearDescription() {
        return gearDesc;
    }

    @Override
    public void setGearDescription(String gearDescription) {
        this.gearDesc = gearDescription;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void setRunning(boolean isRunning) {
        this.running = isRunning;
    }

    public ActorRef getGearActor() {
        return gearActor;
    }

    public void setGearActor(ActorRef gearActor) {
        this.gearActor = gearActor;
    }

    public ActorRefFactory getGearActorRefFactory() {
        if (gearActorRefFactory!=null)
            return gearActorRefFactory;
        else
            return InjectorAkkaSystemActivator.getSystem();
    }

    public void setGearActorRefFactory(ActorRefFactory gearActorRefFactory) {
        this.gearActorRefFactory = gearActorRefFactory;
    }

    public void scheduleMessage(String message, int millisecondPeriod) {
        log.debug("schedule message {} each {} millisecond", new Object[]{message, millisecondPeriod});
        cancellableList.add(InjectorAkkaSystemActivator.getSystem().scheduler().schedule(Duration.Zero(),
                                                                                            Duration.create(millisecondPeriod, TimeUnit.MILLISECONDS),
                                                                                            gearActor,
                                                                                            message,
                                                                                            InjectorAkkaSystemActivator.getSystem().dispatcher(),
                                                                                            null));
    }

    public void cancelMessagesScheduling() {
        log.debug("cancelMessagesScheduling");
        for (Cancellable cancellable : cancellableList)
            cancellable.cancel();
        cancellableList.removeAll(cancellableList);
    }

    public void tell(String message) {
        log.debug("tell {} to {}", new Object[]{message, gearActor.path().name()});
        InjectorAkkaSystemActivator.getSystem().scheduler().scheduleOnce(Duration.Zero(),
                                                                                gearActor,
                                                                                message,
                                                                                InjectorAkkaSystemActivator.getSystem().dispatcher(),
                                                                                null);
    }
}