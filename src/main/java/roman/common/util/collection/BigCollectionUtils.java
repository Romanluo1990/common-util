package roman.common.util.collection;

import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 大集合工具类
 * @author  romanluo
 * @date  2019-06-13
 * @version 1.0
 */
public class BigCollectionUtils {

    private BigCollectionUtils() {
    }

    /**
     * 同步分片转换
     *
     * @param stepSize
     * @param origDatas
     * @param function
     * @param <S>
     * @param <R>
     * @return
     */
    public static <S, R> List<R> sliceMap(int stepSize, List<S> origDatas, Function<List<S>, List<R>> function) {
        return ListUtils.partition(origDatas, stepSize).stream().map(function)
                .flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * 并行分片转换
     *
     * @param stepSize
     * @param origDatas
     * @param function
     * @param <S>
     * @param <R>
     * @return
     */
    public static <S, R> List<R> parallelSliceMap(int stepSize, List<S> origDatas, Function<List<S>, List<R>> function) {
        return ListUtils.partition(origDatas, stepSize).parallelStream().map(function)
                .flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * 同步分片消费
     *
     * @param stepSize
     * @param origDatas
     * @param consumer
     * @param <S>
     * @return
     */
    public static <S> void sliceForEach(int stepSize, List<S> origDatas, Consumer<List<S>> consumer) {
        ListUtils.partition(origDatas, stepSize).stream().forEach(consumer);
    }

    /**
     * 并行分片消费
     *
     * @param stepSize
     * @param origDatas
     * @param consumer
     * @param <S>
     * @return
     */
    public static <S> void parallelSliceForEach(int stepSize, List<S> origDatas, Consumer<List<S>> consumer) {
        ListUtils.partition(origDatas, stepSize).parallelStream().forEach(consumer);
    }


}
