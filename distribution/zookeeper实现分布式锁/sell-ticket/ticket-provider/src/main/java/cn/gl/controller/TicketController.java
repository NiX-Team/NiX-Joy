package cn.gl.controller;

import cn.gl.dao.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TicketController {

    @Value("${server.port}")
    Integer port;

    @Autowired
    TicketRepository ticketRepository;

    @RequestMapping("ticket")
    public String sellTicket(){
        String res = ticketRepository.sellTicket();
        return res + " ,server port: " + port;
    }


}
