/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jd.kafkarest;

import it.jd.kafkarest.app.App;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author paul
 */
public class Main {
    
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        try {
            App app=new App();
            app.start();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
