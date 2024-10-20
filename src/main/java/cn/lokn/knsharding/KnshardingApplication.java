package cn.lokn.knsharding;

import cn.lokn.knsharding.demo.User;
import cn.lokn.knsharding.demo.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KnshardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnshardingApplication.class, args);
    }

    @Autowired
    UserMapper userMapper;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {

            System.out.println(" ===> 1. test insert...");
            userMapper.insert(new User(1, "lokn", 18));

            System.out.println(" ===> 2. test find...");
            User user = userMapper.findById(1);
            System.out.println(" ===> find = " + user);

            System.out.println(" ===> 3. test update...");
            user.setName("kk");
            int update = userMapper.update(user);
            System.out.println(" ===> update = " + update);

            System.out.println(" ===> 4. test find...");
            user = userMapper.findById(1);
            System.out.println(" ===> find = " + user);

            System.out.println(" ===> 5. test delete...");
            int delete = userMapper.delete(1);
            System.out.println(" ===> delete = " + delete);

        };
    }

}
