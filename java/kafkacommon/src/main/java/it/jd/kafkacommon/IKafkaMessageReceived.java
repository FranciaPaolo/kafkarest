package it.jd.kafkacommon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 *
 * @author paul
 */
public interface IKafkaMessageReceived {
    
    public void kafkaMessageReceived(String topic, List<ConsumerRecord> messages);
}
