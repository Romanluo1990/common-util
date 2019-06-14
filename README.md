# common-util
一些工具类

### BigCollectionUtils：  
大集合分片处理，主要用于底层接口限制穿参个数时分片请求  
example:  
```
List<Integer> numbers = IntStream.range(0, 10000).forEach(numbers::add);
//限制一次只能传100个number
List<String> numbersDesc = BigCollectionUtils.parallelSliceMap(100, numbers,
                //底层接口传subList
                subList -> subList.stream().map(i -> "当前number:"+i).collect(Collectors.toList())});
                
```


### CompletableFutureUtils： 
CompletableFuture多组合工具类，主要用于多个互不依赖串行获取数据后对结果聚合  
example:  
```
CompletableFuture<String> nameCf = CompletableFuture.supplyAsync(() -> "Tom");
CompletableFuture<Integer> ageCf = CompletableFuture.supplyAsync(() -> 18);
CompletableFuture<Date> birthdayCf = CompletableFuture.supplyAsync(() -> {
    Calendar instance = Calendar.getInstance();
    instance.set(2000,1,1);
    return instance.getTime();
});
String r1 = CompletableFutureUtils.orchestrate(nameCf, (name) -> "他叫"+name).join();
String r2 = CompletableFutureUtils.orchestrate(nameCf,ageCf, (name,age) -> "他叫"+name+",今年"+age).join();
String r3 = CompletableFutureUtils.orchestrate(nameCf,ageCf,birthdayCf, (name,age,birthday) -> "他叫"+name+",今年"+age+",出生日期"+birthday).join();
System.out.println(r1);
System.out.println(r2);
System.out.println(r3);
```

### SafeRetryCallUtils
重试调用工具类，主要用于调用失败重试机制，比如http请求时超时重试，数据库乐观锁更新重试  
example:  
```
final AtomicInteger runCount = new AtomicInteger(0);
Runnable runnable = () -> {
    System.out.println("运行次数" + (runCount.get()+1));
    if( runCount.incrementAndGet() == 3){
        System.out.println("调用成功");
        return;
    }
    throw new IllegalStateException();
};
//重试3次
SafeRetryCallUtils.call(runnable, 3);
```

### CatchExceptionCallUtils
异常捕获调用，主要用于拉姆达表达式调用时异常不中断，像跑批任务时  
example: 
```
List<Integer> numbers = IntStream.range(0, 10).boxed().collect(Collectors.toList());
Consumer<Integer> printCon = number -> {
    if(number == 5)
        throw new RuntimeException();
    System.out.println(number);
};
//5时异常，但遍历不中断
numbers.forEach(CatchExceptionCallUtils.call(printCon));
```
