package roman.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * 安全重试调用工具类
 *
 * @Auther: romanluo
 * @Date: 2018/10/25
 */
public class SafeRetryCallUtils {

    private static final Logger logger = LoggerFactory.getLogger(SafeRetryCallUtils.class);

    private static final int DEFAULT_RETRY = 3;

    private static final long DEFAULT_SLEEP = -1;

    private SafeRetryCallUtils() {
    }

    public static <E> E call(Callable<E> callable) {
        return call(callable, DEFAULT_RETRY);
    }

    public static <E> E call(Callable<E> callable, int retry, long sleep) {
        return call(callable, callable, retry, sleep, Exception.class);
    }

    public static <E> E call(Callable<E> callable, int retry) {
        return call(callable, retry, Exception.class);
    }

    public static <E> E call(Callable<E> callable, Class<? extends Exception>... errorTypes) {
        return call(callable, DEFAULT_RETRY, errorTypes);
    }

    public static <E> E call(Callable<E> callable, int retry,
                             Class<? extends Exception>... errorTypes) {
        return call(callable, callable, retry, errorTypes);
    }

    public static <E> E call(Callable<E> callable, Callable<E> reCallable, int retry,
                             Class<? extends Exception>... errorTypes) {
        return call(callable, reCallable, retry, DEFAULT_SLEEP, errorTypes);
    }

    /**
     *
     * @param callable 初次调用方式
     * @param reCallable 重试方式
     * @param retry 重试次数
     * @param sleep 重试休眠间隔
     * @param errorTypes 重试异常类型匹配，比如http调用时超时异常，默认所有异常
     * @param <E>
     * @return
     */
    public static <E> E call(Callable<E> callable, Callable<E> reCallable, int retry,
                             long sleep,
                             Class<? extends Exception>... errorTypes) {
        SafeRecursionCaller<E> safeRecursionCaller = new SafeRecursionCaller(callable, reCallable, retry, sleep,
                errorTypes);
        return safeRecursionCaller.call();
    }

    public static void call(Runnable runnable) {
        call(runnable, DEFAULT_RETRY);
    }

    public static void call(Runnable runnable, int retry, long sleep) {
        call(runnable, runnable, retry, sleep, Exception.class);
    }

    public static void call(Runnable runnable, int retry) {
        call(runnable, retry, Exception.class);
    }

    public static void call(Runnable runnable, Class<? extends Exception>... errorTypes) {
        call(runnable, DEFAULT_RETRY, errorTypes);
    }

    public static void call(Runnable runnable, int retry, Class<? extends Exception>... errorTypes) {
        call(runnable, runnable, retry, errorTypes);
    }

    public static void call(Runnable runnable, Runnable reRunnable, int retry,
                            Class<? extends Exception>... errorTypes) {
        call(runnable, reRunnable, retry, DEFAULT_SLEEP, errorTypes);
    }

    public static void call(Runnable runnable, Runnable reRunnable, int retry, long sleep,
                            Class<? extends Exception>... errorTypes) {
        Callable<Boolean> callable = () -> {
            runnable.run();
            return true;
        };
        Callable<Boolean> reCallable = () -> {
            reRunnable.run();
            return true;
        };
        call(callable, reCallable, retry, sleep, errorTypes);
    }

    static class SafeRecursionCaller<E> {

        int times;

        Callable<E> callable;

        Callable<E> reCallable;

        Class<? extends Exception>[] errorTypes;

        long sleep;

        public SafeRecursionCaller(Callable<E> callable, Callable<E> reCallable, int times,
                                   long sleep,
                                   Class<? extends Exception>... errorTypes) {
            this.times = times;
            this.callable = callable;
            this.reCallable = reCallable;
            this.errorTypes = errorTypes;
            this.sleep = sleep;
        }

        public E call() {
            Exception innerError = null;
            for (int i = 0; i < times; i++) {
                try {
                    if (i == 0) {
                        return callable.call();
                    } else {
                        if (sleep > 0) {
                            Thread.sleep(sleep);
                        }
                        return reCallable.call();
                    }
                } catch (Exception e) {
                    boolean match = Arrays.stream(errorTypes)
                            .anyMatch(eClass -> eClass.isInstance(e));
                    if (match) {
                        innerError = e;
                        logger.error("安全调用失败次数{}", i + 1, e);
                    } else {
                        throw new IllegalStateException(e);
                    }
                }
            }
            throw new IllegalStateException("安全调用失败达最大次数" + times, innerError);
        }
    }
}
