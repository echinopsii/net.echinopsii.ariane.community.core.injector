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
from components.injector.dbIDMMySQLPopulator import dbIDMMySQLPopulator

__author__ = 'mffrench'


class injectorProcessor:

    def __init__(self, homeDirPath, idmDBConfig, silent):
        print("\n%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--\n")
        print("%-- Injector configuration : \n")
        self.silent = silent
        self.homeDirPath = homeDirPath
        kernelRepositoryDirPath = self.homeDirPath + "/repository/cc-distrib/"
        if not os.path.exists(kernelRepositoryDirPath):
            os.makedirs(kernelRepositoryDirPath, 0o755)
        self.injectorIDMSQLPopulator = dbIDMMySQLPopulator(idmDBConfig)

    def process(self):
        self.injectorIDMSQLPopulator.process()
        return self
