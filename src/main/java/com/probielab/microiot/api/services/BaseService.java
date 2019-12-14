package com.probielab.microiot.api.services;

import static com.probielab.microiot.mq.MqttHelperVerticle.CONFIG_MICRO_IOT_MQTT_QUEUE;
import static com.probielab.microiot.orm.SqlHelperVerticle.CONFIG_MICRO_IOT_DB_QUEUE;

public abstract class BaseService {
  final static String DbQueue = CONFIG_MICRO_IOT_DB_QUEUE;
  final static String MqttQueue = CONFIG_MICRO_IOT_MQTT_QUEUE;


}
