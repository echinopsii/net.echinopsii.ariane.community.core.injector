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

credentials = pika.PlainCredentials('ariane', 'password')
parameters = pika.ConnectionParameters("localhost", 5672, '/ariane',
                                       credentials=credentials, client_props=client_properties)
connection = pika.BlockingConnection(parameters)

requestor = Requestor(connection, 'remote.injector.cachefactory')
requestor.start()

result = requestor.call({'OPERATION': 'MAKE_GEARS_REGISTRY'
                         ,'ariane.community.injector.gears.registry.name': 'Ariane Docker plugin gears registry'
                         ,'ariane.community.injector.gears.registry.cache.id': 'ariane.community.plugin.docker.gears.cache'
                         ,'ariane.community.injector.gears.registry.cache.name': 'Ariane Docker plugin gears cache'
                         ,'ariane.community.injector.cache.mgr.name': 'ARIANE_PLUGIN_DOCKER_GEARS_CACHE_MGR'
#                         ,'ariane.community.injector.cache.name': 'ariane.community.plugin.docker.gears.cache'
#                         ,'ariane.community.injector.cache.eviction.strategy': 'LRU'
#                         ,'ariane.community.injector.cache.eviction.max.entries': '2000'
#                         ,'ariane.community.injector.cache.persistence.passivation': 'true'
#                         ,'ariane.community.injector.cache.persistence.sf.fetch': 'true'
#                         ,'ariane.community.injector.cache.persistence.sf.ignore.diff': 'false'
#                         ,'ariane.community.injector.cache.persistence.sf.purge.startup': 'false'
#                         ,'ariane.community.injector.cache.persistence.async': 'true'
})

result = requestor.call({'OPERATION': 'MAKE_COMPONENTS_REGISTRY'
                         ,'ariane.community.injector.components.registry.name': 'Ariane Docker plugin components registry'
                         ,'ariane.community.injector.components.registry.cache.id': 'ariane.community.plugin.docke.components.cache'
                         ,'ariane.community.injector.components.registry.cache.name': 'Ariane Docker plugin components cache'
                         ,'ariane.community.injector.cache.mgr.name': 'ARIANE_PLUGIN_DOCKER_COMPONENTS_CACHE_MGR'
#                         ,'ariane.community.injector.cache.name': 'ariane.community.plugin.docker.components.cache'
#                         ,'ariane.community.injector.cache.persistence.passivation': 'false'
#                         ,'ariane.community.injector.cache.persistence.sf.fetch': 'true'
#                         ,'ariane.community.injector.cache.persistence.sf.ignore.diff': 'false'
#                         ,'ariane.community.injector.cache.persistence.sf.purge.startup': 'false'
#                         ,'ariane.community.injector.cache.persistence.sf.location': ''
})

