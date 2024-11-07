package cn.lokn.knsharding.engine;

import cn.lokn.knsharding.config.ShardingProperties;
import cn.lokn.knsharding.strategy.HashShardingStrategy;
import cn.lokn.knsharding.strategy.ShardingStrategy;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        String table;
        Map<String, Object> shardingColumnsMap;
        // insert 与其它类型sql语句不一样
        if (sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
             table = sqlInsertStatement.getTableName().getSimpleName();
            shardingColumnsMap = new HashMap<>();
            List<SQLExpr> columns = sqlInsertStatement.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                SQLExpr column = columns.get(i);
                SQLIdentifierExpr columnExpr = (SQLIdentifierExpr) column;
                String columnName = columnExpr.getSimpleName();
                shardingColumnsMap.put(columnName, args[i]);
            }
        } else {
            // select / update / delete
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            visitor.setParameters(List.of(args));
            sqlStatement.accept(visitor);

            LinkedHashSet<SQLName> sqlNames = new LinkedHashSet<>(visitor.getOriginalTables());
            if (sqlNames.size() > 1) {
                throw new RuntimeException("not support multi tables sharding: " + sqlNames);
            }
             table = sqlNames.iterator().next().getSimpleName();
            System.out.println(" ===>>> visitor.getOriginalTables = " + table);
            shardingColumnsMap = visitor.getConditions().stream()
                    .collect(Collectors.toMap(c -> c.getColumn().getName(), c -> c.getValues().get(0)));
            System.out.println(" ===>>> visitor.getConditions = " + table);
        }

        ShardingStrategy databaseStrategy = databaseStrategies.get(table);
        String targetDatabase = databaseStrategy.doSharing(actualDatabaseNames.get(table), table, shardingColumnsMap);

        ShardingStrategy tableStrategy = tableStrategies.get(table);
        String targetTable = tableStrategy.doSharing(actualTableNames.get(table), table, shardingColumnsMap);

        System.out.println(" ===>>>");
        System.out.println(" ===>>> target db.table = " + targetDatabase + "." + targetTable);
        System.out.println(" ===>>>");

        return new ShardingResult(targetDatabase, sql.replace(table, targetTable));
    }
}
