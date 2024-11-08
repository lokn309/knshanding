package cn.lokn.knsharding.demo.mapper;

import cn.lokn.knsharding.demo.model.Order;
import cn.lokn.knsharding.demo.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderMapper {

    @Insert("insert into t_order (id,uid,price) values(#{id}, #{uid}, #{price})")
    int insert(Order order);

    @Select("select * from t_order where id=#{id} and uid = #{uid}")
    Order findById(int id, int uid);

    @Update("update t_order set price=#{price} where id=#{id} and uid = #{uid}")
    int update(Order order);

    @Delete("delete from t_order where id=#{id} and uid = #{uid}")
    int delete(int id, int uid);


}
