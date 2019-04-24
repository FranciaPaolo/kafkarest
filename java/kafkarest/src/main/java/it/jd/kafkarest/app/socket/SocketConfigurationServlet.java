/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jd.kafkarest.app.socket;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 *
 * @author paul
 */
/**
 * Well, it's just a servlet declaration.
 * @author amrishodiq
 */
public class SocketConfigurationServlet extends WebSocketServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("HTTP GET method not implemented.");
    }
    
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(SubscribeMsgWebSocket.class);
    }
    
}