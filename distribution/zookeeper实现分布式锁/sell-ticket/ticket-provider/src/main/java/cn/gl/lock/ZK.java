package cn.gl.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZK {

    CuratorFramework client;
    String lockPath = "/lock";
    {
        String servers = "192.168.9.130:2181,192.168.9.130:2182,192.168.9.130:2183";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1);
        client = CuratorFrameworkFactory.builder()
                .connectString(servers)
                .sessionTimeoutMs(3000)
                .retryPolicy(retryPolicy)
                .namespace("ticket")
                .build();
        client.start();
    }

    public InterProcessLock getLock(){
        InterProcessLock lock = new InterProcessMutex(client, lockPath);
        return lock;
    }

}
