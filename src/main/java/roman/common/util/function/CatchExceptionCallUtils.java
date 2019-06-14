package roman.common.util.function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * 异常捕获调用
 */
public class CatchExceptionCallUtils {

    private static final Logger log = LoggerFactory.getLogger(CatchExceptionCallUtils.class);

    private CatchExceptionCallUtils() {
    }

    public static <T> Consumer<T> call(Consumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                log.error("调用失败：{}", e);
            }
        };
    }
}
