package cn.gl.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TicketRepositoryTest {

    @Autowired
    TicketRepository ticketRepository;

    @Test
    public void test1(){
        String s = ticketRepository.sellTicket();
        System.out.println(s);
    }

}