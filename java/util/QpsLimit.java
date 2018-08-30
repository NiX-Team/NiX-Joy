package com.alibaba.it.asset.web.workflow.util;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * @author keray
 * @date 2018/08/28 下午2:18
 * qps 检查分钟内的qps
 */
@Slf4j
public final class QpsLimit {
    private static final ConcurrentMap<String,SlidingWindow> WINDOWS = new ConcurrentHashMap<>(4);
    private static final int DEFAULT_WAIT_COUNT = 10;
    private static final int CLEAR_OVER_TIME = 10 * 60;
    static {
        new GuardianThread().start();
    }
    /**
     * 打到qps限制后的执行策略
     * */
    public enum Strategy{
        //拒绝抛出异常
        throw_exception,
        //等待qps降低到限制一下执行
        wait,
        //只是记录下日志
        noting_log
    }

    /**
     * 切面限制qps
     * @param sign qps限制签名
     * @param qps qps限制数
     * @param strategy 超出限制后执行策略
     * @param wait 等待策略等待时间  null时默认 {@value DEFAULT_WAIT_COUNT} 秒
     * */
    public static void acceptLimit(String sign,long qps,Strategy strategy,Integer wait) {
        wait = wait == null ? DEFAULT_WAIT_COUNT : wait;
        if (checkQps(sign,qps)) {
            switch (strategy) {
                case throw_exception:throw new RuntimeException("qps limit fail");
                case wait:wait(wait);break;
                case noting_log:log.warn("{} :qps to achieve limit . limit {}",Thread.currentThread().getName(),qps);break;
                default:break;
            }
        }
    }

    /**
     * 方法执行完成调用 将并发qps减一
     * @param sign 签名
     * */
    public static void acceptCancel(String sign) {
        WINDOWS.get(sign).cancel();
    }

    /**
     * 切面qps限制
     * @param sign
     * @param qps
     * @param pass 限制通过（没达到qps限制）执行函数
     * @param refused 限制未通过 执行函数
     * @return
     * */
    public static <R> R acceptLimit(String sign, long qps, Supplier<R> pass,Supplier<R> refused ) {
        if (checkQps(sign,qps)) {
            return pass.get();
        }
        return refused.get();
    }

    /**
     * 切面qps限制
     * @param sign
     * @param qps
     * @param refused
     * @return
     * */
    public static <R> R acceptLimit(String sign, long qps,Supplier<R> refused ) {
        if (checkQps(sign, qps)) {
            return null;
        }
        return refused.get();
    }

    private static void wait(int count) {
        for (int i = 0;i < count;i ++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 检查调用qps是否超过限制
     * */
    private static boolean checkQps(String sign,long qps) {
        synchronized (QpsLimit.class) {
            if (!WINDOWS.containsKey(sign)) {
                WINDOWS.put(sign,new SlidingWindow(sign,qps));
                return false;
            }
        }
        return WINDOWS.get(sign).check();
    }
    @ToString
    private static class SlidingWindow {
        private final String sign;
        private final long limitQps;
        //分段 一段15秒
        private static final int PIECEWISE = 15;
        //qps取样4段 15*4 60秒
        private static final int ALL_PIECEWISE = 4;
        private long start = 0;
        private long end = 0;
        //一分钟分四段 每15秒设置一段
        private final AtomicLong[] slidingRecord = new AtomicLong[ALL_PIECEWISE];
        private final Object lastWindowCLock = new Object();
        private AtomicBoolean init = new AtomicBoolean(false);

        private SlidingWindow(String sign, long limitQps) {
            this.sign = sign;
            this.limitQps = limitQps;
        }
        private void init() {
            for (int i = 0;i < ALL_PIECEWISE - 1;i ++) {
                slidingRecord[i] = new AtomicLong(0L);
            }
            slidingRecord[ALL_PIECEWISE - 1] = new AtomicLong(1L);
            start = nowSeconds() - PIECEWISE * ALL_PIECEWISE;
            end = nowSeconds() - PIECEWISE;
            init.set(true);
        }
        public boolean check() {
            if (!init.get()) {
                init();
            }
            return clickQps();
        }
        private boolean clickQps() {
            long now = nowSeconds();
            long nowQps;
            //保证滑动操作只执行一次 保证获取最新qps和窗口滑动原子性
            synchronized (sign) {
                if (now > end + PIECEWISE) {
                    sliding();
                }
            }
            nowQps = nowQps();
            return nowQps > limitQps;
        }

        /**
         * 窗口滑动
         * 保证只能有一个线程进行滑动操作
         * */
        private void sliding() {
            //获取这次qps之前的起始窗口
            long agoStart = start;
            start = nowSeconds() - PIECEWISE * ALL_PIECEWISE;
            end = nowSeconds() - PIECEWISE;
            //获取窗口开始到现在的窗口跨度
            int span = (int) ((start - agoStart) /PIECEWISE);
            //如果跨度大于窗口总大小
            if (span >= PIECEWISE) {
                for (int i = 0;i < ALL_PIECEWISE;i ++) {
                    slidingRecord[i].set(0L);
                }
            } else {
                //跨度小于窗口数量 按跨度空隙进行滑动窗口
                for (int i = 0;i < ALL_PIECEWISE - span;i ++) {
                    slidingRecord[i].set(slidingRecord[i + span].get());
                }
                for (int i = ALL_PIECEWISE - span;i < ALL_PIECEWISE;i ++) {
                    slidingRecord[i].set(0L);
                }
            }
        }
        /**
         * 获取当前的qps
         * */
        private long nowQps() {
            long qps = 0;
            for (int i = 0;i < ALL_PIECEWISE - 1;i ++) {
                qps += slidingRecord[i].get();
            }
            synchronized (lastWindowCLock) {
                slidingRecord[ALL_PIECEWISE - 1].getAndIncrement();
                qps += slidingRecord[ALL_PIECEWISE - 1].get();
            }
            return qps;
        }
        private long nowSeconds() {
            return System.currentTimeMillis() / 1_000;
        }

        /**
         * 执行完成 降低qps
         * */
        public void cancel() {
            synchronized (lastWindowCLock) {
                slidingRecord[ALL_PIECEWISE - 1].getAndAdd(-1L);
            }
        }
    }

    private static class GuardianThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    WINDOWS.entrySet().removeIf(entry -> entry.getValue().end - CLEAR_OVER_TIME > System.currentTimeMillis() / 1_000);
                }catch (Exception e) {
                    log.warn("guardian thread clear windows exception ",e);
                }
                try {
                    sleep(1000 * 60 * 10);
                } catch (InterruptedException e) {
                    log.error("guardian thread clear windows interrupted . stop!!!");
                    return;
                }
            }
        }
    }
}
