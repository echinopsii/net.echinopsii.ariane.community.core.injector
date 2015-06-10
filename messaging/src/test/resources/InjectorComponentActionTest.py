import uuid
import pika
import json
from pprint import pprint
__author__ = 'mffrench'


class Requestor(object):

    def __init__(self, connection_, requestQ_):
        self.connection = connection_
        self.channel = connection.channel()
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
        self.channel = connection.channel()
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
        try:
            self.cb(ch, props, body)
        except Exception as e:
            print("### Packaging failed: {0}".format(e))
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

credentials = pika.PlainCredentials('ariane', 'password')
parameters = pika.ConnectionParameters("localhost", 5672, '/ariane',
                                       credentials=credentials, client_props=client_properties)
connection = pika.BlockingConnection(parameters)

requestorComp = Requestor(connection, 'remote.injector.comp')
requestorComp.start()

def localhost_component_service(channel, props, body):
    cache_mgr_name = 'ARIANE_PLUGIN_DOCKER_COMPONENTS_CACHE_MGR'
    registry_name = 'Ariane Docker plugin components registry'
    registry_cache_id = 'ariane.community.plugin.docker.components.cache'
    registry_cache_name = 'Ariane Docker plugin components cache'
    operation = props.headers['OPERATION']
    print(operation)
    if operation == "REFRESH":
        pass
    elif operation == "REFRESH_MAP":
        pass
    else:
        print("Unsupported operation " + str(operation))

localhost_comp_service = Service(connection, 'ariane.community.plugin.docker.components.cache.localhost', localhost_component_service)
localhost_comp_service.start()