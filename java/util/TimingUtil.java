package com.alibaba.it.asset.web.workflow.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author keray
 * @date 2018/08/30 下午1:54
 * 性能计时工具
 */
public class TimingUtil {
    private final static Logger log = LoggerFactory.getLogger(TimingUtil.class);
    private static final ThreadLocal<Map<String,Long>> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 基本计时器 只能单流程执行 start -> stop
     * */
    public static void start() {
       start("simple");
    }

    /**
     * 单流程 <b>停止</b> 于上面的start配合使用
     * */
    public static void stop(String sign) {
        stop("simple",sign,true);
    }

    /**
     * 可暂停计时，提供一个计时签名
     * */
    public static void start(String sign) {
        try {

            Map<String, Long> map = THREAD_LOCAL.get();
            if (map == null) {
               map = new HashMap<>(4);
               map.put(sign + "_time",0L);
               THREAD_LOCAL.set(map);
            } else if (!map.containsKey(sign + "_time")) {
                map.put(sign + "_time",0L);
            }
            map.put(sign,System.currentTimeMillis());
        }catch (Exception ignored) {}
    }
    /**
     * 重入计时停止 于上面的start配合使用
     * @param stop
     * stop 为false时  计时不会停止 只是  <b>暂停</b>！！！
     * stop 为true时 计时<b>停止</b>  进行日志输出
     * */
    public static void stop(String sign,boolean stop) {
        stop(sign, sign,stop);
    }

    private static void stop(String sign,String desc,boolean stop) {
        try {
            Map<String, Long> map = THREAD_LOCAL.get();
            if (stop) {
                log.info("{} 耗时 ： {}",desc,(map.get(sign + "_time") +  (System.currentTimeMillis() - map.get(sign))));
            } else {
                map.put(sign + "_time",map.get(sign + "_time") + (System.currentTimeMillis() - map.get(sign)));
            }
        }catch (Exception ignored) {}
    }
    public static void stop() {
        THREAD_LOCAL.remove();
    }
}
