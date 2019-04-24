/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jd.kafkarest.app.res.dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author paul
 */
public class ReadMessageResponseDto {
    public String topic;
    public List<MsgDto> messages=new ArrayList<>();

    
    public static class MsgDto
    {
        public MsgDto()
        {}
        public MsgDto(Long offset, String message)
        {
            this.message=message;
            this.offset=offset;
        }
        public String message;
        public Long offset;
        
    }
}
