/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jd.kafkarest.app.socket;

import it.jd.kafkacommon.IKafkaMessageReceived;
import it.jd.kafkacommon.KafkaActor;
import it.jd.kafkarest.app.JsonSerializer;
import it.jd.kafkarest.app.res.dto.ReadMessageResponseDto;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 *
 * @author paul
 */
@WebSocket
public class SubscribeMsgWebSocket extends WebSocketAdapter implements IKafkaMessageReceived{

    public Session session;
    public String id;
    public String topic;
    public String consumerGroup;
    public KafkaActor kafkaActor;

    public SubscribeMsgWebSocket() {
        id = java.util.UUID.randomUUID().toString();
    }

    @OnWebSocketConnect
    public void onWebSocketConnect(Session session) {
        this.session = session;

        Map<String, List<String>> headers = this.session.getUpgradeRequest().getHeaders();
        String queryString = this.session.getUpgradeRequest().getQueryString();
        Map<String, String> queryMap = splitQuery(queryString);
        
        topic=queryMap.get("topic");
        consumerGroup=queryMap.get("consumerGroup");
        
        kafkaActor=new KafkaActor.Builder().withConsumerGroup(consumerGroup).build();
        
        SocketManager.getInstance().join(this);
        
        kafkaActor.subscribe(Arrays.asList(new String[]{topic}), this);
    }

    @OnWebSocketClose
    public void onWebSocketClose(int statusCode, String reason) {
        SocketManager.getInstance().leave(this);
    }

    @OnWebSocketError
    public void onWebSocketError(Throwable t) {
        //System.out.println("Error: " + t.getMessage());
    }

    @OnWebSocketMessage
    public void onWebSocketText(String text) {

        if (session.isOpen()) {
            SocketManager.getInstance().messageReceived(this, text);
        }
    }

    public void sendMessageToRemote(String text) {
        try {
            session.getRemote().sendString(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> splitQuery(String query) {
        Map<String, String> query_pairs = new LinkedHashMap<>();

        try {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
        } catch (Exception e) {
        }
        return query_pairs;
    }

    @Override
    public void messageReceived(String topic, List<ConsumerRecord> messages) {
        
        // create the response dto
        ReadMessageResponseDto responseDto=new ReadMessageResponseDto();
        responseDto.topic=topic;
        messages.forEach(m->responseDto.messages.add(new ReadMessageResponseDto.MsgDto(m.offset(),String.valueOf(m.value()))));
        
        sendMessageToRemote(JsonSerializer.serialize(responseDto));
    }
}
