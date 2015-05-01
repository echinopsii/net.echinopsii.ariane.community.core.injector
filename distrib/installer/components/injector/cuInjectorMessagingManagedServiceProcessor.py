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


class cuInjectorMessagingManagedServiceProcessor(AConfUnit):

    def __init__(self, targetConfDir):
        self.confUnitName = "Injector Messaging Managed Service Processor"
        self.confTemplatePath = os.path.abspath("resources/templates/components/net.echinopsii.ariane.community.core.injector.MessagingManagedService.properties.tpl")
        self.confFinalPath = targetConfDir + "net.echinopsii.ariane.community.core.injector.MessagingManagedService.properties"

        injectorMessagingMoMCliRBQVersion = cpInjectorMessagingMoMCliRBQVersion()
        injectorMessagingMoMHostFQDN = cpInjectorMessagingMoMHostFQDN()
        injectorMessagingMoMHostPort = cpInjectorMessagingMoMHostPort()
        injectorMessagingMoMHostUser = cpInjectorMessagingMoMHostUser()
        injectorMessagingMoMHostPasswd = cpInjectorMessagingMoMHostPasswd()
        injectorMessagingMoMHostVhost = cpInjectorMessagingMoMHostVhost()

        self.paramsDictionary = {
            injectorMessagingMoMCliRBQVersion.name: injectorMessagingMoMCliRBQVersion,
            injectorMessagingMoMHostFQDN.name: injectorMessagingMoMHostFQDN,
            injectorMessagingMoMHostPort.name: injectorMessagingMoMHostPort,
            injectorMessagingMoMHostUser.name: injectorMessagingMoMHostUser,
            injectorMessagingMoMHostPasswd.name: injectorMessagingMoMHostPasswd,
            injectorMessagingMoMHostVhost.name: injectorMessagingMoMHostVhost
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
                key == cpInjectorMessagingMoMHostUser.name or key == cpInjectorMessagingMoMHostPasswd.name) and not injectorMessagingManagedServiceConnectionDefined:

                momHostFQDNDefault   = self.injectorMessagingManagedServiceCUValues[cpInjectorMessagingMoMHostFQDN.name]
                momHostFQDNDefaultUI = "[default - " + momHostFQDNDefault + "] "

                momHostPortDefault = self.injectorMessagingManagedServiceCUValues[cpInjectorMessagingMoMHostPort.name]
                momHostPortDefaultUI = "[default - " + momHostPortDefault + "] "

                momHostUserDefault = self.injectorMessagingManagedServiceCUValues[cpInjectorMessagingMoMHostUser.name]
                momHostUserDefaultUI = "[default - " + momHostUserDefault + "] "

                momHostPasswordDefault = self.injectorMessagingManagedServiceCUValues[cpInjectorMessagingMoMHostPasswd.name]

                momHostVhostDefault = self.injectorMessagingManagedServiceCUValues[cpInjectorMessagingMoMHostVhost.name]
                momHostVhostDefaultUI = "[default - " + momHostVhostDefault + "] "

                while not injectorMessagingManagedServiceConnectionDefined:

                    if not self.silent:
                        momUserFQDN = input("%-- >> Define Injector Messaging RabbitMQ User " + momHostUserDefaultUI + ": ")
                        if momUserFQDN == "" or momUserFQDN is None:
                            momHostUser = momHostUserDefault
                        else:
                            momHostUserDefaultUI = "[default - " + momHostUser + "] "
                            momHostUserDefault = momHostUser
                    else:
                        momHostUser = momHostUserDefault

                    if not self.silent:
                        momHostPassword = getpass.getpass("%-- >> Define Injector Messaging RabbitMQ Password: ")
                        while momHostPassword == "" or momHostPassword is None:
                            momHostPassword = getpass.getpass("%-- >> Define Injector Messaging RabbitMQ Password: ")
                        momHostPasswordDefault = momHostPassword

                    else:
                        momHostPassword = momHostPasswordDefault

                    if not self.silent:
                        momHostFQDN = input("%-- >> Define Injector Messaging RabbitMQ server FQDN " + momHostFQDNDefaultUI + ": ")
                        if momHostFQDN == "" or momHostFQDN is None:
                            momHostFQDN = momHostFQDNDefault
                        else:
                            momHostFQDNDefaultUI = "[default - " + momHostFQDN + "] "
                            momHostFQDNDefault = momHostFQDN
                    else:
                        momHostFQDN = momHostFQDNDefault

                    if not self.silent:
                        momPortIsValid = False
                        momPortStr = ""
                        while not momPortIsValid:
                            momPort = 0
                            momPortStr = input("%-- >> Define Injector Messaging RabbitMQ server port " + momHostPortDefaultUI + ": ")
                            if momPortStr == "" or momPortStr is None:
                                momPortStr = momHostPortDefault
                                momPort = int(momHostPortDefault)
                                momPortIsValid = True
                            else:
                                try:
                                    momPort = int(momPortStr)
                                    if (momPort <= 0) or (momPort > 65535):
                                        print("%-- !! Invalid Injector MoM Messaging RabbitMQ port " + str(momPort) + ": not in port range")
                                    else:
                                        momHostPortDefaultUI = "[default - " + momPortStr + "] "
                                        momHostPortDefault = momPortStr
                                        momPortIsValid = True
                                except ValueError:
                                    print("%-- !! Invalid Injector MoM Messaging RabbitMQ port " + momPortStr + " : not a number")
                    else:
                        momPortStr = momHostPortDefault
                        momPort = int(momPortStr)

                    if not self.silent:
                        momVhostIsValid = False
                        momVhost = ""
                        while not momVhostIsValid:
                            momVhost = input("%-- >> Define Injector MoM Messaging RabbitMQ vhost " + momHostVhostDefaultUI + ": ")
                            if momVhost != "":
                                momVhostIsValid = True
                                momHostVhostDefault = momVhost
                                momHostVhostDefaultUI = "[default - " + momVhost + "] "
                            elif momHostVhostDefault != "":
                                momVhost = momHostVhostDefault
                                momVhostIsValid = True
                    else:
                        momVhost = momHostVhostDefault

                    injectorMessagingManagedServiceConnectionDefined = True
                    self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorMessagingMoMHostFQDN.name, momHostFQDN)
                    self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorMessagingMoMHostPort.name, momPort)
                    self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorMessagingMoMHostUser.name, momHostUser)
                    self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorMessagingMoMHostPasswd.name, momHostPassword)
                    self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorMessagingMoMHostVhost.name, momVhost)

            elif key == cpInjectorMessagingMoMCliRBQVersion.name:
                self.injectorMessagingManagedServiceCUProcessor.setKeyParamValue(cpInjectorMessagingMoMCliRBQVersion.name, "0.6.2-SNAPSHOT")

    def inject(self):
        injectorMessagingManagedServiceCUJSON = open("resources/configvalues/components/cuInjectorMessagingManagedService.json", "w")
        jsonStr = json.dumps(self.injectorMessagingManagedServiceCUValues, sort_keys=True, indent=4, separators=(',', ': '))
        injectorMessagingManagedServiceCUJSON.write(jsonStr)
        injectorMessagingManagedServiceCUJSON.close()
        self.injectorMessagingManagedServiceCUProcessor.process()