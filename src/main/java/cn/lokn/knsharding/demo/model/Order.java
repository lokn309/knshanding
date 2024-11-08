package cn.lokn.knsharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: lokn
 * @date: 2024/11/08 22:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private int id;
    private int uid;
    private double price;

}
