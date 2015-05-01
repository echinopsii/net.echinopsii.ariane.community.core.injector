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
from components.injector.cuInjectorMessagingManagedServiceProcessor import injectorMessagingManagedServiceSyringe
from components.injector.dbIDMMySQLPopulator import dbIDMMySQLPopulator
from components.injector.cuInjectorComponentsRegistryProcessor import cpInjectorComponentsCacheConfFilePath, cuInjectorComponentsRegistryProcessor
from components.injector.cuInjectorComponentsCacheProcessor import cpInjectorSharedComponentsCacheDir, cuInjectorComponentsCacheProcessor
from components.injector.cuInjectorGearsCacheProcessor import cuInjectorGearsCacheProcessor, cpInjectorSharedGearsCacheDir
from components.injector.cuInjectorGearsRegistryProcessor import cuInjectorGearsRegistryProcessor, cpInjectorGearsCacheConfFilePath

__author__ = 'mffrench'


class injectorProcessor:

    def __init__(self, homeDirPath, idmDBConfig, silent):
        print("\n%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--\n")
        print("%-- Injector configuration : \n")
        self.silent = silent
        self.homeDirPath = homeDirPath
        self.kernelRepositoryDirPath = self.homeDirPath + "/repository/ariane-core/"
        if not os.path.exists(self.kernelRepositoryDirPath):
            os.makedirs(self.kernelRepositoryDirPath, 0o755)
        self.injectorCachesDirPath = self.homeDirPath + "/ariane/cache/core/injector/"
        if not os.path.exists(self.injectorCachesDirPath):
            os.makedirs(self.injectorCachesDirPath, 0o755)

        self.injectorMessagingSyringe = injectorMessagingManagedServiceSyringe(self.kernelRepositoryDirPath, silent)
        self.injectorMessagingSyringe.shootBuilder()

        self.injectorComponentsCacheCUProcessor = cuInjectorComponentsCacheProcessor(self.injectorCachesDirPath)
        self.injectorComponentsRegistryCUProcessor = cuInjectorComponentsRegistryProcessor(self.kernelRepositoryDirPath)

        self.injectorGearsCacheCUProcessor = cuInjectorGearsCacheProcessor(self.injectorCachesDirPath)
        self.injectorGearsRegistryCUProcessor = cuInjectorGearsRegistryProcessor(self.kernelRepositoryDirPath)

        self.injectorIDMSQLPopulator = dbIDMMySQLPopulator(idmDBConfig)

    def process(self):
        self.injectorMessagingSyringe.inject()

        self.injectorComponentsCacheCUProcessor.setKeyParamValue(cpInjectorSharedComponentsCacheDir.name, self.injectorCachesDirPath)
        self.injectorComponentsCacheCUProcessor.process()

        self.injectorComponentsRegistryCUProcessor.setKeyParamValue(cpInjectorComponentsCacheConfFilePath.name, self.injectorCachesDirPath + "/infinispan.injector.components.cache.xml")
        self.injectorComponentsRegistryCUProcessor.process()

        self.injectorGearsCacheCUProcessor.setKeyParamValue(cpInjectorSharedGearsCacheDir.name, self.injectorCachesDirPath)
        self.injectorGearsCacheCUProcessor.process()

        self.injectorGearsRegistryCUProcessor.setKeyParamValue(cpInjectorGearsCacheConfFilePath.name, self.injectorCachesDirPath + "/infinispan.injector.gears.cache.xml")
        self.injectorGearsRegistryCUProcessor.process()

        self.injectorIDMSQLPopulator.process()
        return self
