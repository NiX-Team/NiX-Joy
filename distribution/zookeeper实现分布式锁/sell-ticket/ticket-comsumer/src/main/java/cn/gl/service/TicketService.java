package cn.gl.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "TicketProvider", fallback = TicketServiceHytrix.class)
public interface TicketService {

    @RequestMapping("ticket")
    String sellTicket();
}
