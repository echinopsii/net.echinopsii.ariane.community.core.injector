# installer injector gears registry processor
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
from tools.AConfParamNotNone import AConfParamNotNone
from tools.AConfUnit import AConfUnit

__author__ = 'mffrench'


class CPInjectorGearsCacheConfFilePath(AConfParamNotNone):

    name = "##injectorGearsCacheConfFilePath"
    description = "Injector gears shared cache configuration file path"
    hide = False

    def __init__(self):
        self.value = None

    def is_valid(self):
        if not super().is_valid:
            return False
        else:
            if os.path.exists(self.value) and os.path.isfile(self.value):
                return True
            else:
                print(self.description + " (" + str(self.value) +
                      ") is not valid. Check if it exists and it has good rights.")
                return False


class CUInjectorGearsRegistryProcessor(AConfUnit):

    def __init__(self, target_conf_dir):
        self.confUnitName = "Injector Gears Registry"
        self.confTemplatePath = os.path.abspath(
            "resources/templates/components/net.echinopsii.ariane.community.core.InjectorGearsRegistry.properties.tpl"
        )
        self.confFinalPath = target_conf_dir + "net.echinopsii.ariane.community.core.InjectorGearsRegistry.properties"
        gears_cache_conf_file_path = CPInjectorGearsCacheConfFilePath()
        self.paramsDictionary = {
            gears_cache_conf_file_path.name: gears_cache_conf_file_path
        }

    def set_key_param_value(self, key, value):
        return super(CUInjectorGearsRegistryProcessor, self).set_key_param_value(key, value)

    def get_params_keys_list(self):
        return super(CUInjectorGearsRegistryProcessor, self).get_params_keys_list()

    def process(self):
        return super(CUInjectorGearsRegistryProcessor, self).process()

    def get_param_from_key(self, key):
        return super(CUInjectorGearsRegistryProcessor, self).get_param_from_key(key)
