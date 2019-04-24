/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jd.kafkarest.app;

import it.jd.kafkarest.app.socket.SocketConfigurationServlet;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 *
 * @author paul
 */
public class App {

    public void start() throws Exception {
        
        startWebServer();
    }
    
    private void startWebServer() throws Exception
    {
        // Jetty Servet Server
        Server jettyServer = new Server(7070);
        ServletContextHandler servletContextHandler = new ServletContextHandler(SESSIONS);
        servletContextHandler.setContextPath("/");
        
        // setup an authentication filter
        FilterHolder authFilter =servletContextHandler.addFilter(AuthenticationFilter.class, "/*", EnumSet.of(DispatcherType.ASYNC, 
                                                                                    DispatcherType.ERROR, 
                                                                                    DispatcherType.FORWARD,
                                                                                    DispatcherType.INCLUDE, 
                                                                                    DispatcherType.REQUEST));
        jettyServer.setHandler(servletContextHandler);
        
        // Jersey for RestAPI
        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/api/*");
        servletHolder.setInitOrder(0);
        servletHolder.setInitParameter(
        "jersey.config.server.provider.packages",
        "it.jd.kafkarest.app.res"
        );
        
        // WebSocket to push messages to the clients
        ServletHolder servletHolderWs = new ServletHolder("ws-events", SocketConfigurationServlet.class);
        servletContextHandler.addServlet(servletHolderWs, "/messaging/*");
        
        //ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(servletContextHandler);
        //wscontainer.addEndpoint(SubscribeMsgWebSocket.class);
                //servletContextHandler.addServlet(ServletContainer.class, "/messaging/*");
        //servletHolderWs.setInitOrder(1);
        //ServerContainer wsContainer = WebSocketServerContainerInitializer.configureContext(servletContextHandler);
        //wsContainer.addEndpoint(SubscribeMsgWebSocket.class);
        //servletContextHandler.addServlet(SocketConfigurationServlet.class, "/messaging");
        //servletContextHandler.addServlet(SubscribeMsgWebSocket.class, "/messaging");
        
        //ServletContextHandler handler = new ServletContextHandler(server, "/");
        //servletContextHandler.addServlet(ServletListTopics.class, "/");
        jettyServer.start();
        jettyServer.join();
    }

    
}
