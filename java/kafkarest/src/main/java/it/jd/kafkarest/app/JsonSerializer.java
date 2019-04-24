/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jd.kafkarest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paul
 */
public class JsonSerializer {
    
    public static <T> T deserialize(String json, Class clazz)
    {
        try {
            return (T)new ObjectMapper().readValue(json, clazz);
        } catch (IOException ex) {
            Logger.getLogger(JsonSerializer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static String serialize(Object json)
    {
        try {
            return new ObjectMapper().writeValueAsString(json);
        } catch (IOException ex) {
            Logger.getLogger(JsonSerializer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
