# installer injector components cache processor
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


class CPInjectorSharedComponentsCacheDir(AConfParamNotNone):

    name = "##injectorSharedComponentsCacheDir"
    description = "Injector shared components cache directory"
    hide = False

    def __init__(self):
        self.value = None

    def is_valid(self):
        if not super().is_valid:
            return False
        else:
            if os.path.exists(self.value) and os.path.isdir(self.value) and \
                    os.access(self.value, os.W_OK) and os.access(self.value, os.W_OK):
                return True
            else:
                print(self.description + " (" + str(self.value) +
                      ") is not valid. Check if it exists and it has good rights.")
                return False


class CUInjectorComponentsCacheProcessor(AConfUnit):

    def __init__(self, target_conf_dir):
        self.confUnitName = "Injector Components Cache"
        self.confTemplatePath = os.path.abspath(
            "resources/templates/components/infinispan.injector.components.cache.xml.tpl"
        )
        self.confFinalPath = target_conf_dir + "infinispan.injector.components.cache.xml"
        shared_components_cache_dir = CPInjectorSharedComponentsCacheDir()
        self.paramsDictionary = {
            shared_components_cache_dir.name: shared_components_cache_dir
        }

    def set_key_param_value(self, key, value):
        return super(CUInjectorComponentsCacheProcessor, self).set_key_param_value(key, value)

    def get_params_keys_list(self):
        return super(CUInjectorComponentsCacheProcessor, self).get_params_keys_list()

    def process(self):
        return super(CUInjectorComponentsCacheProcessor, self).process()

    def get_param_from_key(self, key):
        return super(CUInjectorComponentsCacheProcessor, self).get_param_from_key(key)
