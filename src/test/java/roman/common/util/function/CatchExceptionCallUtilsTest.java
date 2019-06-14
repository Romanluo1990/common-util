package roman.common.util.function;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CatchExceptionCallUtilsTest {

    @Test
    public void call() {
        List<Integer> numbers = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        List<Integer> result = new ArrayList<>();
        Consumer<Integer> printCon = number -> {
            if(number == 5)
                throw new RuntimeException();
            result.add(number);
        };
        numbers.forEach(CatchExceptionCallUtils.call(printCon));
        result.forEach(System.out::println);
        Assert.assertFalse(result.contains(5));
    }
}