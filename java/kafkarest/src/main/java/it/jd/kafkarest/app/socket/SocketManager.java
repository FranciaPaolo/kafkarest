/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jd.kafkarest.app.socket;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocket connected clients Manager
 * @author paul
 */
public class SocketManager {
    private static final SocketManager INSTANCE = new SocketManager();
    private List<SubscribeMsgWebSocket> members = new ArrayList<>();
    
    private static final Logger log = LoggerFactory.getLogger(SocketManager.class);
    
    public static SocketManager getInstance()
    {
        return INSTANCE;
    }
    
    public void join(SubscribeMsgWebSocket socket) 
    {
        members.add(socket);        
        log.info("joined: "+socket.id);
    }
    
    public void leave(SubscribeMsgWebSocket socket) 
    {
        members.remove(socket);
        log.info("leaved: "+socket.id);
    }
    
    public void writeAllMembers(String message) 
    {
        for(SubscribeMsgWebSocket member: members)
        {
            member.session.getRemote().sendStringByFuture(message);
        }
    }
    
    public void messageReceived(SubscribeMsgWebSocket from, String message)
    {
        for(SubscribeMsgWebSocket member: members)
        {
            if(member.equals(from))
            {
                log.info("messageReceived: "+member.id);
                return;
            }
        }
        
    }
}
