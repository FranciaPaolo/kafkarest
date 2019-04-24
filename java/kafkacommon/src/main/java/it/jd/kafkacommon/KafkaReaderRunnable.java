package it.jd.kafkacommon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 *
 * @author paul
 */
public class KafkaReaderRunnable implements Runnable {

    private KafkaConsumer consumer = null;
    private IKafkaMessageReceived msgHandler = null;
    private List<String> subscribedTopics = null;
    private Boolean stopSubscription = false;

    @Override
    public void run() {

    }

    public void subscribe(KafkaConsumer consumer, List<String> topics, IKafkaMessageReceived handler) {
        this.subscribedTopics = topics;
        this.msgHandler = handler;
        this.stopSubscription = false;
        this.consumer = consumer;

        consumer.subscribe(topics);
        
        while (!stopSubscription) {
            ConsumerRecords<Long, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
            // 1000 is the time in milliseconds consumer will wait if no record is found at broker.
            if (consumerRecords.count() > 0 && msgHandler != null) {
                List<ConsumerRecord> msg = new ArrayList<>();
                consumerRecords.forEach(record -> msg.add(record));
                msgHandler.messageReceived(msg.get(0).topic(), msg);
            }

            // commits the offset of record to broker. 
            consumer.commitAsync();
        }
    }

    public void unsubscribe() {
        this.stopSubscription = true;
    }
}
