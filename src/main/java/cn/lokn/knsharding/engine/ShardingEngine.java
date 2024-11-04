package cn.lokn.knsharding.engine;

/**
 * core sharding engine.
 */
public interface ShardingEngine {

    ShardingResult shading(String sql, Object[] args);

}
