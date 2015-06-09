#sudo pip3 install python3-pika
import uuid
import pika
import json
from pprint import pprint
__author__ = 'mffrench'


class Requestor(object):

    def __init__(self, connection, requestQ):
        self.connection = connection
        self.channel = connection.channel()
        self.requestQ = requestQ
        self.channel.queue_declare(queue=requestQ)
        self.result = self.channel.queue_declare(exclusive=True)
        self.callback_queue = self.result.method.queue
        self.response = None
        self.corr_id = None

    def start(self):
        self.channel.basic_consume(self.on_response, no_ack=True,
                                   queue=self.callback_queue)

    def stop(self):
        self.channel.close()

    def on_response(self, ch, method, props, body):
        if self.corr_id == props.correlation_id:
            self.response = {'props': props, 'body': body }

    def call(self, p=None, n=None):
        self.response = None
        self.corr_id = str(uuid.uuid4())

        properties=pika.BasicProperties(content_type=None, content_encoding=None,
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

requestor = Requestor(connection, 'remote.injector.cachefactory')
requestor.start()

result = requestor.call({'OPERATION': 'MAKE_GEARS_REGISTRY'
                         ,'ariane.community.injector.gears.registry.name': registry_name
                         ,'ariane.community.injector.gears.registry.cache.id': registry_cache_id
                         ,'ariane.community.injector.gears.registry.cache.name': registry_cache_name
                         ,'ariane.community.injector.cache.mgr.name': cache_mgr_name
})

requestor = Requestor(connection, 'remote.injector.gear')
requestor.start()

result = requestor.call({'OPERATION': 'PUSH_GEAR_IN_CACHE'
                         ,'PUSH_GEAR_IN_CACHE': '{"gearId": "ariane.community.plugin.docker.gears.cache.localhost", "gearName": "docker@localhost", "gearDescription": "Ariane remote injector for localhost", "gearAdminQueue": "ariane.community.plugin.docker.gears.cache.localhost", "isStarted", "false"}'
})
#RC = 1 - No cache id

result = requestor.call({'OPERATION': 'PUSH_GEAR_IN_CACHE'
                         ,'PUSH_GEAR_IN_CACHE': '{"gearId": "ariane.community.plugin.docker.gears.cache.localhost", "gearName": "docker@localhost", "gearDescription": "Ariane remote injector for localhost", "gearAdminQueue": "ariane.community.plugin.docker.gears.cache.localhost", "isStarted", "false"}'
                         ,'CACHE_ID': registry_cache_id
})
#RC = 1 - No gear (error on field name)

result = requestor.call({'OPERATION': 'PUSH_GEAR_IN_CACHE'
                         ,'REMOTE_GEAR': '{"gearId": "ariane.community.plugin.docker.gears.cache.localhost", "gearName": "docker@localhost", "gearDescription": "Ariane remote injector for localhost", "gearAdminQueue": "ariane.community.plugin.docker.gears.cache.localhost", "isStarted", "false"}'
                         ,'CACHE_ID': registry_cache_id
})
#RC = 1 - serialization PB

result = requestor.call({'OPERATION': 'PUSH_GEAR_IN_CACHE'
    ,'REMOTE_GEAR': '{"gearId": "ariane.community.plugin.docker.gears.cache.localhost", "gearName": "docker@localhost", "gearDescription": "Ariane remote injector for localhost", "gearAdminQueue": "ariane.community.plugin.docker.gears.cache.localhost", "running": "false"}'
    ,'CACHE_ID': registry_cache_id
})
#RC = 0 - successfull

result = requestor.call({'OPERATION': 'DEL_GEAR_FROM_CACHE'
    ,'REMOTE_GEAR': '{"gearId": "ariane.community.plugin.docker.gears.cache.localhost", "gearName": "docker@localhost", "gearDescription": "Ariane remote injector for localhost", "gearAdminQueue": "ariane.community.plugin.docker.gears.cache.localhost", "running": "false"}'
    ,'CACHE_ID': registry_cache_id
})