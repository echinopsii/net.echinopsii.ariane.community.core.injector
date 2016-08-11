# installer injector processor
#
# Copyright (C) 2014 Mathilde Ffrench
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
import os
from components.injector.CUInjectorRegistryFactoryProcessor import CUInjectorRegistryFactoryProcessor, \
    CPInjectorComponentsRemoteCacheDirPath, CPInjectorGearsRemoteCacheDirPath
from components.injector.DBIDMMySQLPopulator import DBIDMMySQLPopulator
from components.injector.CUInjectorComponentsRegistryProcessor import CPInjectorComponentsCacheConfFilePath, \
    CUInjectorComponentsRegistryProcessor
from components.injector.CUInjectorComponentsCacheProcessor import CPInjectorSharedComponentsCacheDir, \
    CUInjectorComponentsCacheProcessor
from components.injector.CUInjectorGearsCacheProcessor import CUInjectorGearsCacheProcessor, \
    CPInjectorSharedGearsCacheDir
from components.injector.CUInjectorGearsRegistryProcessor import CUInjectorGearsRegistryProcessor, \
    CPInjectorGearsCacheConfFilePath

__author__ = 'mffrench'


class InjectorProcessor:

    def __init__(self, home_dir_path, dist_dep_type, directory_db_conf, idm_db_conf, bus_processor, silent):
        print("\n%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--"
              "%--%--%--%--%--%--%--%--%--\n")
        print("%-- Injector configuration : \n")
        self.silent = silent
        self.dist_dep_type = dist_dep_type
        self.homeDirPath = home_dir_path
        self.idmDBConfig = idm_db_conf
        self.directoryDBConfig = directory_db_conf
        self.busProcessor = bus_processor

        self.kernelRepositoryDirPath = self.homeDirPath + "/repository/ariane-core/"
        if not os.path.exists(self.kernelRepositoryDirPath):
            os.makedirs(self.kernelRepositoryDirPath, 0o755)
        self.injectorCachesDirPath = self.homeDirPath + "/ariane/cache/core/injector/"
        if not os.path.exists(self.injectorCachesDirPath):
            os.makedirs(self.injectorCachesDirPath, 0o755)
        self.injectorComponentsRemoteCacheDirPath = self.injectorCachesDirPath + "remote/components"
        if not os.path.exists(self.injectorComponentsRemoteCacheDirPath):
            os.makedirs(self.injectorComponentsRemoteCacheDirPath, 0o755)
        self.injectorGearsRemoteCacheDirPath = self.injectorCachesDirPath + "remote/gears"
        if not os.path.exists(self.injectorGearsRemoteCacheDirPath):
            os.makedirs(self.injectorGearsRemoteCacheDirPath, 0o755)

        self.injectorComponentsCacheCUProcessor = CUInjectorComponentsCacheProcessor(self.injectorCachesDirPath)
        self.injectorComponentsRegistryCUProcessor = CUInjectorComponentsRegistryProcessor(self.kernelRepositoryDirPath)

        self.injectorGearsCacheCUProcessor = CUInjectorGearsCacheProcessor(self.injectorCachesDirPath)
        self.injectorGearsRegistryCUProcessor = CUInjectorGearsRegistryProcessor(self.kernelRepositoryDirPath)

        self.injectorRegistryFactoryCUProcessor = CUInjectorRegistryFactoryProcessor(self.kernelRepositoryDirPath)

        self.injectorIDMSQLPopulator = DBIDMMySQLPopulator(idm_db_conf)

    def process(self):
        self.busProcessor.process(
            "resources/templates/components/"
            "net.echinopsii.ariane.community.core.InjectorMessagingManagedService.properties.tpl",
            self.kernelRepositoryDirPath +
            "net.echinopsii.ariane.community.core.InjectorMessagingManagedService.properties"
        )

        self.injectorComponentsCacheCUProcessor.set_key_param_value(
            CPInjectorSharedComponentsCacheDir.name, self.injectorCachesDirPath
        )
        self.injectorComponentsCacheCUProcessor.process()

        self.injectorComponentsRegistryCUProcessor.set_key_param_value(
            CPInjectorComponentsCacheConfFilePath.name,
            self.injectorCachesDirPath + "/infinispan.injector.components.cache.xml"
        )
        self.injectorComponentsRegistryCUProcessor.process()

        self.injectorGearsCacheCUProcessor.set_key_param_value(
            CPInjectorSharedGearsCacheDir.name, self.injectorCachesDirPath
        )
        self.injectorGearsCacheCUProcessor.process()

        self.injectorGearsRegistryCUProcessor.set_key_param_value(
            CPInjectorGearsCacheConfFilePath.name, self.injectorCachesDirPath + "/infinispan.injector.gears.cache.xml"
        )
        self.injectorGearsRegistryCUProcessor.process()

        self.injectorRegistryFactoryCUProcessor.set_key_param_value(
            CPInjectorComponentsRemoteCacheDirPath.name, self.injectorComponentsRemoteCacheDirPath
        )
        self.injectorRegistryFactoryCUProcessor.set_key_param_value(
            CPInjectorGearsRemoteCacheDirPath.name, self.injectorGearsRemoteCacheDirPath
        )
        self.injectorRegistryFactoryCUProcessor.process()

        self.injectorIDMSQLPopulator.process()
        return self
