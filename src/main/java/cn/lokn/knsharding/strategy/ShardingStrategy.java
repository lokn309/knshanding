package cn.lokn.knsharding.strategy;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: lokn
 * @date: 2024/11/03 23:04
 */
public interface ShardingStrategy {

    List<String> getShardingColumns();

    String doSharing(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams);

}
