package cn.lokn.knsharding.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: sharding result.
 * @author: lokn
 * @date: 2024/10/20 23:07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShardingResult {

    private String targetDataSourceName;

}
