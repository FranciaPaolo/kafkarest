/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jd.kafkarest.app;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import org.slf4j.LoggerFactory;


/**
 *
 * @author paul
 */
public class Config {
    
    private static Config instance;
    private Properties prop = new Properties();
    
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Config.class);
    
    public static Config getInstance()
    {
        if(instance==null){
            instance=new Config();
            instance.loadPropFromFile();
        }
        return instance;
    }
    
    public Properties getProp()
    {
        return prop;
    }
    
    public Properties getProp(String prefix)
    {
        Properties propTmp= new Properties();
        
        Enumeration<String> enums = (Enumeration<String>) prop.propertyNames();
        while (enums.hasMoreElements()) {
            String key = enums.nextElement();
            if(key.startsWith(prefix))
            {
                propTmp.put(key,prop.getProperty(key));
            }
        }
        
        return propTmp;
    }
    
    private void loadPropFromFile()
    {
        try {
            prop.load(Config.class.getClassLoader().getResourceAsStream("kafkarest.properties"));
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }
    
}
