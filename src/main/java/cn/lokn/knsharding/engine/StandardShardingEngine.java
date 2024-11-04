package cn.lokn.knsharding.engine;

import cn.lokn.knsharding.config.ShardingProperties;
import cn.lokn.knsharding.demo.model.User;
import cn.lokn.knsharding.strategy.HashShardingStrategy;
import cn.lokn.knsharding.strategy.ShardingStrategy;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: lokn
 * @date: 2024/11/03 22:33
 */
public class StandardShardingEngine implements ShardingEngine {

    /**
     * key -> databaseName
     * value -> values
     */
    private final MultiValueMap<String, String> actualDatabaseNames = new LinkedMultiValueMap<>();
    private final MultiValueMap<String, String> actualTableNames    = new LinkedMultiValueMap<>();

    private final Map<String, ShardingStrategy> databaseStrategies = new HashMap<>();
    private final Map<String, ShardingStrategy> tableStrategies    = new HashMap<>();

    public StandardShardingEngine(ShardingProperties properties) {

        properties.getTables().forEach((table, tableProperties) -> {
            tableProperties.getActualDataNodes().forEach(actualDataNode -> {
                String[] split = actualDataNode.split("\\.");
                String databaseName = split[0];
                String tableName    = split[1];
                actualDatabaseNames.add(databaseName, tableName);
                actualTableNames.add(tableName, databaseName);
            });
            databaseStrategies.put(table, new HashShardingStrategy(tableProperties.getDatabaseStrategy()));
            tableStrategies.put(table, new HashShardingStrategy(tableProperties.getTableStrategy()));
        });
    }

    @Override
    public ShardingResult shading(String sql, Object[] args) {

        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        // insert 与其它类型sql语句不一样
        if (sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
            String table = sqlInsertStatement.getTableName().getSimpleName();
            Map<String, Object> shardingColumsMap = new HashMap<>();
            List<SQLExpr> columns = sqlInsertStatement.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                SQLExpr column = columns.get(i);
                SQLIdentifierExpr columnExpr = (SQLIdentifierExpr) column;
                String columnName = columnExpr.getSimpleName();
                shardingColumsMap.put(columnName, args[i]);
            }
            ShardingStrategy databaseStrategy = databaseStrategies.get(table);
            String targetDatabase = databaseStrategy.doSharing(actualDatabaseNames.get(table), table, shardingColumsMap);

            ShardingStrategy tableStrategy = tableStrategies.get(table);
            String targetTable = tableStrategy.doSharing(actualTableNames.get(table), table, shardingColumsMap);

            System.out.println(" ===>>> target db.table = " + targetDatabase + "." + targetTable);

        } else {

            // select / update / delete

        }

        Object parameterObject = args[0];
        System.out.println(" ===> getObject sql statement: " + sql);
        int id = 0;
        if (parameterObject instanceof User user) {
            id = user.getId();
        } else if (parameterObject instanceof Integer uid) {
           id = uid;
        }
        return new ShardingResult(id % 2 == 0 ? "ds0" : "ds1", sql);

    }
}
