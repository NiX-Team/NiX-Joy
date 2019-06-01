package cn.gl.service;


import org.springframework.stereotype.Service;

@Service
public class TicketServiceHytrix implements TicketService {
    @Override
    public String sellTicket() {
        return "停止服务!!!";
    }
}
