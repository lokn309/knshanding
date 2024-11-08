package cn.lokn.knsharding.demo;

import cn.lokn.knsharding.config.ShardingAutoConfiguration;
import cn.lokn.knsharding.demo.mapper.OrderMapper;
import cn.lokn.knsharding.demo.model.Order;
import cn.lokn.knsharding.mybatis.ShardingMapperFactoryBean;
import cn.lokn.knsharding.demo.mapper.UserMapper;
import cn.lokn.knsharding.demo.model.User;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ShardingAutoConfiguration.class)
@MapperScan(value = "cn.lokn.knsharding.demo.mapper",
        factoryBean = ShardingMapperFactoryBean.class)
public class KnshardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnshardingApplication.class, args);
    }

    @Autowired
    UserMapper userMapper;

    @Autowired
    OrderMapper orderMapper;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            for (int i = 1; i <= 40; i++) {
                testOrder(i);
            }

            for (int i = 0; i <= 10; i++) {
                testUser(i);
            }

        };
    }

    private void testUser(int id) {
        System.out.println(" \n\n================> id = " + id);
        System.out.println(" ===> 1. test insert...");
        userMapper.insert(new User(id, "lokn", 18));

        System.out.println(" ===> 2. test find...");
        User user = userMapper.findById(id);
        System.out.println(" ===> find = " + user);

        System.out.println(" ===> 3. test update...");
        user.setName("kk");
        int update = userMapper.update(user);
        System.out.println(" ===> update = " + update);

        System.out.println(" ===> 4. test find...");
        user = userMapper.findById(id);
        System.out.println(" ===> find = " + user);

        System.out.println(" ===> 5. test delete...");
        int delete = userMapper.delete(id);
        System.out.println(" ===> delete = " + delete);
    }

    private void testOrder(int id) {
        int id2 = id + 100;
        System.out.println(" \n\n================> id = " + id);
        System.out.println(" ===> 1. test insert...");
        int insert = orderMapper.insert(new Order(id, 1, 10d));
        System.out.println(" ===> inserted = " + insert);
        insert = orderMapper.insert(new Order(id2, 2, 20d));
        System.out.println(" ===> inserted = " + insert);

        System.out.println(" ===> 2. test find...");
        Order order = orderMapper.findById(id, 1);
        System.out.println(" ===> find = " + order);
        Order order2 = orderMapper.findById(id2, 2);
        System.out.println(" ===> find = " + order2);

        System.out.println(" ===> 3. test update...");
        order.setPrice(11d);
        int update = orderMapper.update(order);
        System.out.println(" ===> update = " + update);
        order2.setPrice(11d);
        update = orderMapper.update(order2);
        System.out.println(" ===> update = " + update);

        System.out.println(" ===> 4. test find...");
        order = orderMapper.findById(id, 1);
        System.out.println(" ===> find = " + order);
        order = orderMapper.findById(id2, 2);
        System.out.println(" ===> find = " + order);

        System.out.println(" ===> 5. test delete...");
        int delete = orderMapper.delete(id, 1);
        System.out.println(" ===> delete = " + delete);
        delete = orderMapper.delete(id2, 2);
        System.out.println(" ===> delete = " + delete);
    }
}
