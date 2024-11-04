package cn.lokn.knsharding.strategy;

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @description: hash sharding strategy.
 * @author: lokn
 * @date: 2024/11/03 23:08
 */
public class HashShardingStrategy implements ShardingStrategy {

    private final String shardingColumn;
    private final String  algorithmExpression;

    public HashShardingStrategy(Properties properties) {
        this.shardingColumn = properties.getProperty("shardingColumn");
        this.algorithmExpression = properties.getProperty("algorithmExpression");
    }

    @Override
    public List<String> getShardingColumns() {
        return List.of(this.shardingColumn);
    }

    @Override
    public String doSharing(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams) {
        String express = InlineExpressionParser.handlePlaceHolder(this.algorithmExpression);
        InlineExpressionParser parser = new InlineExpressionParser(express);
        Closure closure = parser.evaluateClosure();
        closure.setProperty(this.shardingColumn, shardingParams.get(this.shardingColumn));
        return closure.call().toString();
    }
}
