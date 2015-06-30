import uuid
import pika
import json
from pprint import pprint
__author__ = 'mffrench'


class Requestor(object):

    def __init__(self, connection_, requestQ_):
        self.connection = connection_
        self.channel = self.connection.channel()
        self.requestQ = requestQ_
        self.channel.queue_declare(queue=requestQ_)
        self.result = self.channel.queue_declare(exclusive=True)
        self.callback_queue = self.result.method.queue
        self.response = None
        self.corr_id = None

    def start(self):
        self.channel.basic_consume(self.on_response, no_ack=True,
                                   queue=self.callback_queue)

    def stop(self):
        self.channel.close()
        self.connection.close()

    def on_response(self, ch, method_frame, props, body):
        if self.corr_id == props.correlation_id:
            self.response = {'props': props, 'body': body }

    def call(self, p=None, n=None):
        self.response = None
        self.corr_id = str(uuid.uuid4())

        properties = pika.BasicProperties(content_type=None, content_encoding=None,
                                          headers=p, delivery_mode=None,
                                          priority=None, correlation_id=self.corr_id,
                                          reply_to=self.callback_queue, expiration=None,
                                          message_id=None, timestamp=None,
                                          type=None, user_id=None,
                                          app_id=None, cluster_id=None)

        if n is not None:
            self.channel.basic_publish(exchange='',
                                       routing_key=self.requestQ,
                                       properties=properties,
                                       body=str(n))
        else:
            self.channel.basic_publish(exchange='',
                                       routing_key=self.requestQ,
                                       properties=properties,
                                       body='')
        while self.response is None:
            self.connection.process_data_events()
        return self.response


class Service(object):
    def __init__(self, connection_, serviceQ_, cb_):
        self.connection = connection_
        self.channel = self.connection.channel()
        self.channel.queue_declare(queue=serviceQ_)
        self.serviceQ = serviceQ_
        self.cb = cb_

    def start(self):
        self.channel.basic_consume(self.on_request, self.serviceQ)
        self.channel.start_consuming()

    def stop(self):
        self.channel.stop_consuming()
        self.channel.stop()
        self.connection.stop()

    def on_request(self, ch, method_frame, props, body):
        self.cb(ch, props, body)
        ch.basic_ack(delivery_tag=method_frame.delivery_tag)



client_properties = {
    'product': 'Ariane',
    'information': 'Ariane - Docker Injector',
    'ariane.pgurl': 'ssh://localhost',
    'ariane.osi': 'localhost',
    'ariane.otm': 'DOCKops',
    'ariane.app': 'Ariane',
    'ariane.cmp': 'echinopsii'
}

cache_mgr_name = 'ARIANE_PLUGIN_DOCKER_GEARS_CACHE_MGR'
registry_name = 'Ariane Docker plugin gears registry'
registry_cache_id = 'ariane.community.plugin.docker.gears.cache'
registry_cache_name = 'Ariane Docker plugin gears cache'

credentials = pika.PlainCredentials('ariane', 'password')
parameters = pika.ConnectionParameters("localhost", 5672, '/ariane',
                                       credentials=credentials, client_props=client_properties)
connection = pika.BlockingConnection(parameters)

requestorFactory = Requestor(connection, 'remote.injector.cachefactory')
requestorFactory.start()
result = requestorFactory.call({'OPERATION': 'MAKE_GEARS_REGISTRY'
    ,'ariane.community.injector.gears.registry.name': registry_name
    ,'ariane.community.injector.gears.registry.cache.id': registry_cache_id
    ,'ariane.community.injector.gears.registry.cache.name': registry_cache_name
    ,'ariane.community.injector.cache.mgr.name': cache_mgr_name
})

requestorGear = Requestor(connection, 'remote.injector.gear')
requestorGear.start()

result = requestorGear.call({'OPERATION': 'PUSH_GEAR_IN_CACHE'
    ,'REMOTE_GEAR': '{"gearId": "ariane.community.plugin.docker.gears.cache.localhost", "gearName": "docker@localhost", '
                    '"gearDescription": "Ariane remote injector for localhost", "gearAdminQueue": "ariane.community.plugin.docker.gears.cache.localhost", "running": "false"}'
    ,'CACHE_ID': registry_cache_id
})

cache_mgr_name = 'ARIANE_PLUGIN_DOCKER_COMPONENTS_CACHE_MGR'
registry_name = 'Ariane Docker plugin components registry'
registry_cache_id = 'ariane.community.plugin.docker.components.cache'
registry_cache_name = 'Ariane Docker plugin components cache'

result = requestorFactory.call({'OPERATION': 'MAKE_COMPONENTS_REGISTRY'
    ,'ariane.community.injector.components.registry.name': registry_name
    ,'ariane.community.injector.components.registry.cache.id': registry_cache_id
    ,'ariane.community.injector.components.registry.cache.name': registry_cache_name
    ,'ariane.community.injector.cache.mgr.name': cache_mgr_name
})

requestorComp = Requestor(connection, 'remote.injector.comp')
requestorComp.start()

result = requestorComp.call({'OPERATION': 'PUSH_COMPONENT_IN_CACHE'
    ,'REMOTE_COMPONENT': '{"componentId": "ariane.community.plugin.docker.components.cache.localhost", '
                         '"componentName": "docker@localhost", "componentType": "Docker Daemon",'
                         '"componentAdminQueue": "ariane.community.plugin.docker.components.cache.localhost", "refreshing": "false", '
                         '"nextAction": 0, "jsonLastRefresh": "2013-03-11 01:38:18.309", '
                         '"attachedGearId": "ariane.community.plugin.docker.gears.cache.localhost"}'
    ,'CACHE_ID': registry_cache_id
})

requestorTree = Requestor(connection, 'remote.injector.tree')
requestorTree.start()

result = requestorTree.call({'OPERATION': 'GET_TREE_MENU_ENTITY_I', 'TREE_MENU_ENTITY_ID': 'mappingDir'})
idMap = json.loads(result['body'].decode("UTF-8"))['id']
result = requestorTree.call({'OPERATION': 'REGISTER', 'TREE_MENU_ENTITY': '{"id": "systemDir", "value": "System", "type": 2, "contextAddress": "", '
                                                                      '"description": "", "icon": "cog", "displayRoles": ["sysadmin"], '
                                                                      '"displayPermissions": ["injMapSysDocker:display"]}'})

result = requestorTree.call({'OPERATION': 'SET_PARENT', 'TREE_MENU_ENTITY_ID': 'systemDir', 'TREE_MENU_ENTITY_PARENT_ID': idMap})

result = requestorTree.call({'OPERATION': 'REGISTER', 'TREE_MENU_ENTITY': '{"id": "docker", "value": "Docker", "type": 1, "contextAddress": "/ariane/views/injectors/external.jsf?id=docker", '
                                                                      '"description": "Docker injector", "icon": "cog", "displayRoles": ["sysadmin", "sysreviewer"], '
                                                                      '"displayPermissions": ["injMapSysDocker:display"], '
                                                                      '"remoteInjectorTreeEntityGearsCacheId": "ariane.community.plugin.docker.gears.cache.localhost", '
                                                                      '"remoteInjectorTreeEntityComponentsCacheId":"ariane.community.plugin.docker.components.cache.localhost"}'})

result = requestorTree.call({'OPERATION': 'SET_PARENT', 'TREE_MENU_ENTITY_ID': 'docker', 'TREE_MENU_ENTITY_PARENT_ID': 'systemDir'})

result = requestorTree.call({'OPERATION': 'UPDATE', 'TREE_MENU_ENTITY': '{"id": "docker", "value": "Docker", "type": 1, "contextAddress": "/ariane/views/injectors/external.jsf?id=docker", '
                                                                    '"description": "Docker injector", "icon": "icon-cog", "displayRoles": ["sysadmin", "sysreviewer"], '
                                                                    '"displayPermissions": ["injMapSysDocker:display"],'
                                                                    '"otherActionsRoles": {"action": ["sysadmin"]}, "otherActionsPerms": {"action": ["injMapSysDocker:action"]},'
                                                                    '"remoteInjectorTreeEntityGearsCacheId": "ariane.community.plugin.docker.gears.cache", '
                                                                    '"remoteInjectorTreeEntityComponentsCacheId":"ariane.community.plugin.docker.components.cache"}'})