package cn.lokn.knsharding;

import cn.lokn.knsharding.demo.User;
import cn.lokn.knsharding.demo.UserMapper;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ShardingAutoConfiguration.class)
@MapperScan(value = "cn.lokn.knsharding.demo",
        factoryBean = ShardingMapperFactoryBean.class)
public class KnshardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnshardingApplication.class, args);
    }

    @Autowired
    UserMapper userMapper;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {

            for (int i = 0; i < 10; i++) {
                test(i);
            }

        };
    }

    private void test(int id) {
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

//            System.out.println(" ===> 5. test delete...");
//            int delete = userMapper.delete(id);
//            System.out.println(" ===> delete = " + delete);
    }

}
