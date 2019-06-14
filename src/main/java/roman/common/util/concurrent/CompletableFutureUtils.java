package roman.common.util.concurrent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * CompletableFuture组合工具类
 * @author  romanluo
 * @date  2019-06-13
 * @version 1.0
 */
public class CompletableFutureUtils {

	private CompletableFutureUtils() {
	}

	private static final Method[] methodCache = new Method[10];

	public static <R1, U> CompletableFuture<U> orchestrate(CompletionStage<? extends R1> s1, Function1<? super R1, ? extends U> fn) {
		return orchestratorRoller(fn, s1);
	}

	public static <R1, R2, U> CompletableFuture<U> orchestrate(CompletionStage<? extends R1> s1,
                                                               CompletionStage<? extends R2> s2, Function2<? super R1, ? super R2, ? extends U> fn) {
		return orchestratorRoller(fn, s1, s2);
	}

	public static <R1, R2, R3, U> CompletableFuture<U> orchestrate(CompletionStage<? extends R1> s1,
			CompletionStage<? extends R2> s2, CompletionStage<? extends R3> s3,
			Function3<? super R1, ? super R2, ? super R3, ? extends U> fn) {
		return orchestratorRoller(fn, s1, s2, s3);
	}

	public static <R1, R2, R3, R4, U> CompletableFuture<U> orchestrate(CompletionStage<? extends R1> s1,
			CompletionStage<? extends R2> s2, CompletionStage<? extends R3> s3, CompletionStage<? extends R4> s4,
			Function4<? super R1, ? super R2, ? super R3, ? super R4, ? extends U> fn) {
		return orchestratorRoller(fn, s1, s2, s3, s4);
	}

	public static <R1, R2, R3, R4, R5, U> CompletableFuture<U> orchestrate(CompletionStage<? extends R1> s1,
			CompletionStage<? extends R2> s2, CompletionStage<? extends R3> s3, CompletionStage<? extends R4> s4,
			CompletionStage<? extends R5> s5,
			Function5<? super R1, ? super R2, ? super R3, ? super R4, ? super R5, ? extends U> fn) {
		return orchestratorRoller(fn, s1, s2, s3, s4, s5);
	}

	public static <R1, R2, R3, R4, R5, R6, U> CompletableFuture<U> orchestrate(CompletionStage<? extends R1> s1,
			CompletionStage<? extends R2> s2, CompletionStage<? extends R3> s3, CompletionStage<? extends R4> s4,
			CompletionStage<? extends R5> s5, CompletionStage<? extends R6> s6,
			Function6<? super R1, ? super R2, ? super R3, ? super R4, ? super R5, ? super R6, ? extends U> fn) {
		return orchestratorRoller(fn, s1, s2, s3, s4, s5, s6);
	}

	public static <R1, R2, R3, R4, R5, R6, R7, U> CompletableFuture<U> orchestrate(CompletionStage<? extends R1> s1,
			CompletionStage<? extends R2> s2, CompletionStage<? extends R3> s3, CompletionStage<? extends R4> s4,
			CompletionStage<? extends R5> s5, CompletionStage<? extends R6> s6, CompletionStage<? extends R7> s7,
			Function7<? super R1, ? super R2, ? super R3, ? super R4, ? super R5, ? super R6, ? super R7, ? extends U> fn) {
		return orchestratorRoller(fn, s1, s2, s3, s4, s5, s6, s7);
	}

	public static <R1, R2, R3, R4, R5, R6, R7, R8, U> CompletableFuture<U> orchestrate(CompletionStage<? extends R1> s1,
			CompletionStage<? extends R2> s2, CompletionStage<? extends R3> s3, CompletionStage<? extends R4> s4,
			CompletionStage<? extends R5> s5, CompletionStage<? extends R6> s6, CompletionStage<? extends R7> s7,
			CompletionStage<? extends R8> s8,
			Function8<? super R1, ? super R2, ? super R3, ? super R4, ? super R5, ? super R6, ? super R7, ? super R8, ? extends U> fn) {
		return orchestratorRoller(fn, s1, s2, s3, s4, s5, s6, s7, s8);
	}

	public interface Function1<R1, U> {
		U apply(R1 r1);
	}

	public interface Function2<R1, R2, U> {
		U apply(R1 r1, R2 r2);
	}

	public interface Function3<R1, R2, R3, U> {
		U apply(R1 r1, R2 r2, R3 r3);
	}

	public interface Function4<R1, R2, R3, R4, U> {
		U apply(R1 r1, R2 r2, R3 r3, R4 r4);
	}

	public interface Function5<R1, R2, R3, R4, R5, U> {
		U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5);
	}

	public interface Function6<R1, R2, R3, R4, R5, R6, U> {
		U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6);
	}

	public interface Function7<R1, R2, R3, R4, R5, R6, R7, U> {
		U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7);
	}

	public interface Function8<R1, R2, R3, R4, R5, R6, R7, R8, U> {
		U apply(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5, R6 r6, R7 r7, R8 r8);
	}

	private static <F> CompletableFuture orchestratorRoller(F function, CompletionStage... completionStages) {
		int index = completionStages.length - 1;
		Method method = methodCache[index];
		if (method == null) {
			method = findApplyMethod(function.getClass());
			methodCache[index] = method;
		}
		CompletableFuture<Object[]> resultFuture = CompletableFuture.supplyAsync(() -> new Object[index + 1]);
		for (int i = 0; i < index + 1; i++) {
			resultFuture = resultFuture.thenCombine(completionStages[i], addResult(i));
		}
		return resultFuture.thenApply(invokeMethod(method, function));
	}

	private static Method findApplyMethod(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if ("apply".equals(method.getName())) {
				return method;
			}
		}
		throw new IllegalArgumentException(clazz.getName() + "无apply方法");
	}

	private static Function<Object[], Object> invokeMethod(Method method, Object target) {
		return v -> {
			try {
				method.setAccessible(true);
				return method.invoke(target, v);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
		};
	}


	private static BiFunction<Object[], Object, Object[]> addResult(int index) {
		return (results, result) -> {
			results[index] = result;
			return results;
		};
	}
}