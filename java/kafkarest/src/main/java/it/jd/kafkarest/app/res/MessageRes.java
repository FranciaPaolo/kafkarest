/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jd.kafkarest.app.res;

import it.jd.kafkarest.app.res.dto.ReadMessageResponseDto;
import it.jd.kafkacommon.KafkaActor;
import it.jd.kafkarest.app.Config;
import it.jd.kafkarest.app.JsonSerializer;
import it.jd.kafkarest.app.res.dto.ReadMessageRequestDto;
import it.jd.kafkarest.app.res.dto.WriteMessageDto;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 *
 * @author paul
 */
@Path("/message")
public class MessageRes {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ReadMessageResponseDto readMessage(@QueryParam("query") String json) {
        
        // parse dto request
        ReadMessageRequestDto reqDto=(ReadMessageRequestDto)JsonSerializer.deserialize(json, ReadMessageRequestDto.class);
        
        // read from kafka
        KafkaActor kafka=new KafkaActor.Builder()
                .withConsumerProperties(Config.getInstance().getProp())
                .withConsumerGroup(reqDto.consumerGroup)
                .build();
        List<ConsumerRecord> messages=kafka.read(Arrays.asList(new String[]{reqDto.topic}));
        kafka.close();
        
        // create the response dto
        ReadMessageResponseDto responseDto=new ReadMessageResponseDto();
        responseDto.topic=reqDto.topic;
        messages.forEach(m->responseDto.messages.add(new ReadMessageResponseDto.MsgDto(m.offset(),String.valueOf(m.value()))));

        return responseDto;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void writeMessage(WriteMessageDto request) {
        
        // write tokafka
        KafkaActor kafka=new KafkaActor.Builder()
                .withProducerProperties(Config.getInstance().getProp())
                .withProducerId(request.producerId)
                .build();
        
        kafka.write(request.topic, request.message);
        kafka.close();
    }
}
