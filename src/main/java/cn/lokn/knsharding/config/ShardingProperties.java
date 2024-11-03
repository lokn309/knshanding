package cn.lokn.knsharding.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @description: Configuration for sharding.
 * @author: lokn
 * @date: 2024/10/20 22:51
 */
@Data
@ConfigurationProperties(prefix = "spring.sharding")
public class ShardingProperties {

    private Map<String, Properties> datasources = new LinkedHashMap<>();

}
