package roman.common.util.concurrent;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureUtilsTest {

    @Test
    public void orchestrate() {
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
    }
}