package cn.gl.dao;

import cn.gl.TicketProvider;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TicketRepository {

    @Autowired
    StringRedisTemplate template;

    InterProcessLock lock = TicketProvider.zk.getLock();

    public String sellTicket(){
        try {
            lock.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }


        String res = null;
        String ticket = template.opsForValue().get("ticket");
        int num = Integer.parseInt(ticket);
        if (num > 0) {
            res = "get ticket no:" + num;
            num -= 1;
            template.opsForValue().set("ticket", num+"");
        }else {
            res = "no ticket";
        }

        try {
            lock.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

}
