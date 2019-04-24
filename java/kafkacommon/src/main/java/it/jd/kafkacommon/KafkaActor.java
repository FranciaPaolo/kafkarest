package it.jd.kafkacommon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 *
 * @author paul
 */
public class KafkaActor {

    private KafkaProducer producer = null;
    private KafkaConsumer consumer = null;
    private IKafkaMessageReceived msgHandler = null;
    private List<String> subscribedTopics = null;
    private KafkaReaderRunnable consumerReaderRunnable = null;
    private Thread consumerReaderThread = null;

    protected KafkaActor() {
    }

    public void subscribe(List<String> topics, IKafkaMessageReceived handler) {
        if (consumerReaderRunnable == null) {
            this.subscribedTopics = topics;
            this.msgHandler = handler;

            consumerReaderRunnable = new KafkaReaderRunnable();
            consumerReaderRunnable.subscribe(consumer, topics, handler);
            consumerReaderThread = new Thread(consumerReaderRunnable);
            consumerReaderThread.start();
        }
    }

    public void unsubscribe() {
        this.consumerReaderRunnable.unsubscribe();
    }

    public List<ConsumerRecord> read(List<String> topics)
    {
        consumer.subscribe(topics);
        ConsumerRecords<Long, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
        List<ConsumerRecord> messages=new ArrayList<>();
        // 1000 is the time in milliseconds consumer will wait if no record is found at broker.
        if (consumerRecords.count() > 0) {
            consumerRecords.forEach(record -> messages.add(record));
        }

        // commits the offset of record to broker. 
        consumer.commitAsync();
        
        return messages;
    }
    
    public void write(String topic, String message) {
        ProducerRecord<Long, String> record = new ProducerRecord<>(topic, message);
        try {
            producer.send(record);
            producer.flush();
//            System.out.println("Record sent with key " + index + " to partition " + metadata.partition()
//                    + " with offset " + metadata.offset());
        }
        catch (Exception e) { //catch (ExecutionException | InterruptedException e) {

            System.out.println("Error in sending record");
            System.out.println(e);

        }
    }

    public void close()
    {
        if(consumer!=null)
        {
            consumer.close();
        }
        if(producer!=null)
        {
            producer.close();
        }
    }
    
    public static class Builder {

        KafkaActor kActor;
        String server="localhost:9092";
        
        String producer_clientid="client1";
        Properties producer_props=new Properties();
        
        String consumer_group="consumerGroup1";
        Properties consumer_props=new Properties();
        
        Boolean withProducer=false;
        Boolean withConsumer=false;

        public Builder() {
            this.kActor = new KafkaActor();
        }
        
        public Builder withProducerServer(String server) {
            this.server=server;
            this.withProducer=true;
            return this;
        }
        
        public Builder withProducerId(String clientid) {
            this.producer_clientid=clientid;
            this.withProducer=true;
            return this;
        }

        public Builder withProducerProperties(Properties props) {
            this.producer_props=props;
            this.withProducer=true;
            return this;
        }
        
        public Builder withConsumerServer(String server) {
            this.server=server;
            this.withConsumer=true;
            return this;
        }
        
        public Builder withConsumerGroup(String consumerGroup) {
            this.consumer_group=consumerGroup;
            this.withConsumer=true;
            return this;
        }
        
        public Builder withConsumerProperties(Properties props) {
            this.consumer_props=props;
            this.withConsumer=true;
            return this;
        }

        public KafkaActor build() {
            if (!this.withConsumer && !this.withProducer) {
                throw new RuntimeException("you have to configure at least a consumer or producer");
            }
            if(this.withProducer)
            {
                kActor.producer = new KafkaProducer(getDefaultProducerProps(producer_props));
            }
            if(this.withConsumer)
            {
                kActor.consumer = new KafkaConsumer(getDefaultConsumerProps(consumer_props));
            }
            
            return this.kActor;
        }

        private Properties getDefaultProducerProps(Properties customProp) {
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
            props.put(ProducerConfig.CLIENT_ID_CONFIG, producer_clientid);
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            for (String key : props.keySet().toArray(new String[0])) {
                if (customProp.containsKey(key)) {
                    props.put(key, customProp.get(key));
                }
            }

            return props;
        }

        private Properties getDefaultConsumerProps(Properties customProp) {
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, consumer_group);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

            for (String key : props.keySet().toArray(new String[0])) {
                if (customProp.containsKey(key)) {
                    props.put(key, customProp.get(key));
                }
            }

            return props;
        }
    }

}
