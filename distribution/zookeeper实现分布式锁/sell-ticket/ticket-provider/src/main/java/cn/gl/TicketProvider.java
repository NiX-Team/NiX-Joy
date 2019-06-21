package cn.gl;

import cn.gl.lock.ZK;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class TicketProvider {
    public static ZK zk;

    public static void main(String[] args) {
        zk = new ZK();
        System.out.println("zk 完成");
        SpringApplication.run(TicketProvider.class, args);
    }



}
