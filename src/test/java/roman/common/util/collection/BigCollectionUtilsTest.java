package roman.common.util.collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BigCollectionUtilsTest {

    private static List<Integer> numbers = new ArrayList<>();

    @Before
    public void init(){
        IntStream.range(0, 10000).forEach(numbers::add);
    }

    @Test
    public void sliceMap() {
        Set<String> threadNames = new HashSet<>();
        List<String> numbersDesc = BigCollectionUtils.sliceMap(100, numbers,
                subList -> {
                    threadNames.add(Thread.currentThread().getName());
                    return subList.stream().map(i -> "当前number:"+i)
                            .peek(System.out::println).collect(Collectors.toList());
                });
        threadNames.forEach(System.out::println);
        Assert.assertEquals(threadNames.size(), 1);
        Assert.assertEquals(numbersDesc.size(), 10000);
    }

    @Test
    public void parallelSliceMap() {
        Set<String> threadNames = new CopyOnWriteArraySet<>();
        List<String> numbersDesc = BigCollectionUtils.parallelSliceMap(100, numbers,
                subList -> {
                    threadNames.add(Thread.currentThread().getName());
                    return subList.stream().map(i -> "当前number:"+i)
                            .peek(System.out::println).collect(Collectors.toList());
                });
        threadNames.forEach(System.out::println);
        Assert.assertTrue(threadNames.size()>1);
        Assert.assertEquals(numbersDesc.size(), 10000);
    }

    @Test
    public void sliceForEach() {
        Set<String> threadNames = new HashSet<>();
        List<String> numbersDesc = new ArrayList<>();
        BigCollectionUtils.sliceForEach(100, numbers,
                subList -> {
                    threadNames.add(Thread.currentThread().getName());
                    subList.stream().map(i -> "当前number:"+i).forEach(numbersDesc::add);
                });
        numbersDesc.forEach(System.out::println);
        threadNames.forEach(System.out::println);
        Assert.assertEquals(threadNames.size(), 1);
        Assert.assertEquals(numbersDesc.size(), 10000);
    }

    @Test
    public void parallelSliceForEach() {
        Set<String> threadNames = new CopyOnWriteArraySet<>();
        List<String> numbersDesc = new CopyOnWriteArrayList<>();
        BigCollectionUtils.parallelSliceForEach(100, numbers,
                subList -> {
                    threadNames.add(Thread.currentThread().getName());
                    subList.stream().map(i -> "当前number:"+i).forEach(numbersDesc::add);
                });
        numbersDesc.forEach(System.out::println);
        threadNames.forEach(System.out::println);
        Assert.assertTrue(threadNames.size()>1);
        Assert.assertEquals(numbersDesc.size(), 10000);
    }
}