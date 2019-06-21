package cn.gl.controller;

import cn.gl.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TicketController {
    @Autowired
    TicketService ticketService;

    @RequestMapping("/ticket")
    public String sellTicket(){
        String res = ticketService.sellTicket();
        return res + ", comsumer add";
    }

}
