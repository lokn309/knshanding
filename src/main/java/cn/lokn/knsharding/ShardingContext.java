package cn.lokn.knsharding;

/**
 * @description: Sharding context.
 * @author: lokn
 * @date: 2024/10/20 23:05
 */
public class ShardingContext {

    private static final ThreadLocal<ShardingResult> LOCAL = new ThreadLocal<>();

    public static ShardingResult get() {
        return LOCAL.get();
    }

    public static void set(ShardingResult result) {
        LOCAL.set(result);
    }

}
