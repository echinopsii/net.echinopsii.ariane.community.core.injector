# installer injector components cache processor
#
# Copyright (C) 2015 Mathilde Ffrench
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
import getpass
import json
import os
import platform
import socket
from tools.AConfParamNotNone import AConfParamNotNone
from tools.AConfUnit import AConfUnit

__author__ = 'mffrench'


class cpInjectorMessagingMoMCliRBQVersion(AConfParamNotNone):

    name = "##MCLI_RBQ_VERSION"
    description = "Injector MoM Client Version"
    hide = False

    def __init__(self):
        self.value = None


class cpInjectorMessagingMoMHostFQDN(AConfParamNotNone):

    name = "##MHOST_FQDN"
    description = "Injector MoM Host FQDN"
    hide = False

    def __init__(self):
        self.value = None


class cpInjectorMessagingMoMHostPort(AConfParamNotNone):

    name = "##MHOST_PORT"
    description = "Injector MoM Host Port"
    hide = False

    def __init__(self):
        self.value = None


class cpInjectorMessagingMoMHostUser(AConfParamNotNone):

    name = "##MHOST_USER"
    description = "Injector MoM Host User"
    hide = False

    def __init__(self):
        self.value = None


class cpInjectorMessagingMoMHostPasswd(AConfParamNotNone):

    name = "##MHOST_PASSWD"
    description = "Injector MoM Host Password"
    hide = False

    def __init__(self):
        self.value = None


class cpInjectorMessagingMoMHostVhost(AConfParamNotNone):

    name = "##MHOST_VHOST"
    description = "Injector MoM Host Vhost"
    hide = False

    def __init__(self):
        self.value = None


class cpInjectorArianeFQDN(AConfParamNotNone):

    name = "##ARIANE_FQDN"
    description = "Ariane server FQDN"
    hide = False

    def __init__(self):
        self.value = None


class cpInjectorArianeHost(AConfParamNotNone):

    name = "##ARIANE_HOST"
    description = "Ariane server hostname"
    hide = False

    def __init__(self):
        self.value = None


class cpInjectorArianeOPST(AConfParamNotNone):

    name = "##ARIANE_OPS_TEAM"
    description = "Ariane OPS team"
    hide = False

    def __init__(self):
        self.value = None


class cuInjectorMessagingManagedServiceProcessor(AConfUnit):

    def __init__(self, targetConfDir):
        self.confUnitName = "Injector Messaging Managed Service Processor"
        self.confTemplatePath = os.path.abspath("resources/templates/components/net.echinopsii.ariane.community.core.InjectorMessagingManagedService.properties.tpl")
        self.confFinalPath = targetConfDir + "net.echinopsii.ariane.community.core.InjectorMessagingManagedService.properties"

        injectorMessagingMoMCliRBQVersion = cpInjectorMessagingMoMCliRBQVersion()
        injectorMessagingMoMHostFQDN = cpInjectorMessagingMoMHostFQDN()
        injectorMessagingMoMHostPort = cpInjectorMessagingMoMHostPort()
        injectorMessagingMoMHostUser = cpInjectorMessagingMoMHostUser()
        injectorMessagingMoMHostPasswd = cpInjectorMessagingMoMHostPasswd()
        injectorMessagingMoMHostVhost = cpInjectorMessagingMoMHostVhost()
        injectorArianeFQDN = cpInjectorArianeFQDN()
        injectorArianeHost = cpInjectorArianeHost()
        injectorArianeOPST = cpInjectorArianeOPST()

        self.paramsDictionary = {
            injectorMessagingMoMCliRBQVersion.name: injectorMessagingMoMCliRBQVersion,
            injectorMessagingMoMHostFQDN.name: injectorMessagingMoMHostFQDN,
            injectorMessagingMoMHostPort.name: injectorMessagingMoMHostPort,
            injectorMessagingMoMHostUser.name: injectorMessagingMoMHostUser,
            injectorMessagingMoMHostPasswd.name: injectorMessagingMoMHostPasswd,
            injectorMessagingMoMHostVhost.name: injectorMessagingMoMHostVhost,
            injectorArianeFQDN.name: injectorArianeFQDN,
            injectorArianeHost.name: injectorArianeHost,
            injectorArianeOPST.name: injectorArianeOPST
        }


class injectorMessagingManagedServiceSyringe:

    def __init__(self, targetConfDif, silent):
        self.silent = silent
        self.injectorMessagingManagedServiceCUProcessor = cuInjectorMessagingManagedServiceProcessor(targetConfDif)
        injectorMessagingManagedServiceCUJSON = open("resources/configvalues/components/cuInjectorMessagingManagedService.json")
        self.injectorMessagingManagedServiceCUValues = json.load(injectorMessagingManagedServiceCUJSON)
        injectorMessagingManagedServiceCUJSON.close()

    def shootBuilder(self):
        injectorMessagingManagedServiceConnectionDefined = False
        for key in self.injectorMessagingManagedServiceCUProcessor.getParamsKeysList():

            if (key == cpInjectorMessagingMoMHostFQDN.name or key == cpInjectorMessagingMoMHostPort.name or 
                key == cpInjectorMessagingMoMHostUser.name or key == cpInjectorMessagingMoMHostPasswd.name) \
                    and not injectorMessagingManagedServiceConnectionDefined:

                momHostFQDNDefault = self.injectorMessagingManagedServiceCUValues[cpInjectorMessagingMoMHostFQDN.name]
                momHostFQDNDefaultUI = "[default - " + momHostFQDNDefault + "] "
                momHostFQDN = momHostFQDNDefault

                momHostPortDefault = self.injectorMessagingManagedServiceCUValues[cpInjectorMessagingMoMHostPort.name]
                momHostPortDefaultUI = "[default - " + momHostPortDefault + "] "
                momHostPort = int(momHostPortDefault)

                momHostUserDefault = self.injectorMessagingManagedServiceCUValues[cpInjectorMessagingMoMHostUser.name]
                momHostUserDefaultUI = "[default - " + momHostUserDefault + "] "
                momHostUser = momHostUserDefault

                momHostPasswordDefault = self.injectorMessagingManagedServiceCUValues[cpInjectorMessagingMoMHostPasswd.name]
                momHostPassword = momHostPasswordDefault

                momHostVhostDefault = self.injectorMessagingManagedServiceCUValues[cpInjectorMessagingMoMHostVhost.name]
                momHostVhostDefaultUI = "[default - " + momHostVhostDefault + "] "
                momHostVhost = momHostVhostDefault

                arianeOpsTeamDefault = self.injectorMessagingManagedServiceCUValues[cpInjectorArianeOPST.name]
                arianeOpsTeamDefaultUI = "[default - " + arianeOpsTeamDefault + "] "
                arianeOpsTeam = arianeOpsTeamDefault

                while not injectorMessagingManagedServiceConnectionDefined:

                    if not self.silent:
                        momUserFQDN = input("%-- >> Define Injector Messaging RabbitMQ User " + momHostUserDefaultUI + ": ")
                        if momUserFQDN == "" or momUserFQDN is None:
                            momHostUser = momHostUserDefault
                        else:
                            momHostUserDefaultUI = "[default - " + momHostUser + "] "
                            momHostUserDefault = momHostUser

                    if not self.silent:
                        momHostPassword = getpass.getpass("%-- >> Define Injector Messaging RabbitMQ Password: ")
                        while momHostPassword == "" or momHostPassword is None:
                            momHostPassword = getpass.getpass("%-- >> Define Injector Messaging RabbitMQ Password: ")
                        momHostPasswordDefault = momHostPassword

                    if not self.silent:
                        momHostFQDN = input("%-- >> Define Injector Messaging RabbitMQ server FQDN " + momHostFQDNDefaultUI + ": ")
                        if momHostFQDN == "" or momHostFQDN is None:
                            momHostFQDN = momHostFQDNDefault
                        else:
                            momHostFQDNDefaultUI = "[default - " + momHostFQDN + "] "
                            momHostFQDNDefault = momHostFQDN

                    if not self.silent:
                        momPortIsValid = False
                        momPortStr = ""
                        while not momPortIsValid:
                            momHostPort = 0
                            momPortStr = input("%-- >> Define Injector Messaging RabbitMQ server port " + momHostPortDefaultUI + ": ")
                            if momPortStr == "" or momPortStr is None:
                                momPortStr = momHostPortDefault
                                momHostPort = int(momHostPortDefault)
                                momPortIsValid = True
                            else:
                                try:
                                    momHostPort = int(momPortStr)
                                    if (momHostPort <= 0) or (momHostPort > 65535):
                                        print("%-- !! Invalid Injector MoM Messaging RabbitMQ port " + str(momHostPort) + ": not in port range")
                                    else:
                                        momHostPortDefaultUI = "[default - " + momPortStr + "] "
                                        momHostPortDefault = momPortStr
                                        momPortIsValid = True
                                except ValueError:
                                    print("%-- !! Invalid Injector MoM Messaging RabbitMQ port " + momPortStr + " : not a number")

                    if not self.silent:
                        momVhostIsValid = False
                        momHostVhost = ""
                        while not momVhostIsValid:
                            momHostVhost = input("%-- >> Define Injector MoM Messaging RabbitMQ vhost " + momHostVhostDefaultUI + ": ")
                            if momHostVhost != "":
                                momVhostIsValid = True
                                momHostVhostDefault = momHostVhost
                                momHostVhostDefaultUI = "[default - " + momHostVhost + "] "
                            elif momHostVhostDefault != "":
                                momHostVhost = momHostVhostDefault
                                momVhostIsValid = True

                    injectorMessagingManagedServiceConnectionDefined = True

                if not self.silent:
                    arianeOpsTeam = input("%-- >> Define Ariane OPS team name " + arianeOpsTeamDefaultUI + ": ")
                    if arianeOpsTeam == "" or arianeOpsTeam is None:
                        arianeOpsTeam = arianeOpsTeam
                    else:
                        arianeOpsTeamDefaultUI = "[default - " + arianeOpsTeam + "] "
                        arianeOpsTeamDefault = arianeOpsTeam

                self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorMessagingMoMHostFQDN.name, momHostFQDN)
                self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorMessagingMoMHostPort.name, momHostPort)
                self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorMessagingMoMHostUser.name, momHostUser)
                self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorMessagingMoMHostPasswd.name, momHostPassword)
                self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorMessagingMoMHostVhost.name, momHostVhost)
                self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorArianeFQDN.name, socket.getfqdn())
                self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorArianeHost.name, platform.node())
                self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorArianeOPST.name, arianeOpsTeam)

            elif key == cpInjectorMessagingMoMCliRBQVersion.name:
                self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorMessagingMoMCliRBQVersion.name, "0.6.2-SNAPSHOT")

    def inject(self):
        injectorMessagingManagedServiceCUJSON = open("resources/configvalues/components/cuInjectorMessagingManagedService.json", "w")
        jsonStr = json.dumps(self.injectorMessagingManagedServiceCUValues, sort_keys=True, indent=4, separators=(',', ': '))
        injectorMessagingManagedServiceCUJSON.write(jsonStr)
        injectorMessagingManagedServiceCUJSON.close()
        self.injectorMessagingManagedServiceCUProcessor.process()