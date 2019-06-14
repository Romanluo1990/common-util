package roman.common.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class SafeRetryCallUtilsTest {

    @Test
    public void call() {
        final AtomicInteger runCount = new AtomicInteger(0);
        Runnable runnable = () -> {
            System.out.println("运行次数" + (runCount.get()+1));
            if( runCount.incrementAndGet() == 3)
                return;
            throw new IllegalStateException();
        };
        SafeRetryCallUtils.call(runnable,3, IllegalStateException.class);
        Assert.assertTrue(runCount.get() == 3);
    }
}