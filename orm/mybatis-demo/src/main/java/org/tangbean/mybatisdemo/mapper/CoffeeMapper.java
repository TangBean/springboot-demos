package org.tangbean.mybatisdemo.mapper;

import org.apache.ibatis.annotations.*;
import org.tangbean.mybatisdemo.model.Coffee;

@Mapper
public interface CoffeeMapper {
    @Insert("insert into t_coffee (name, price, create_time, update_time) " +
            "values (#{name}, #{price}, now(), now())")
    @Options(useGeneratedKeys = true)
    // 给 coffee 对象 id 赋值
    @SelectKey(statement="CALL IDENTITY()", keyProperty="id", before=false, resultType=Long.class)
    int save(Coffee coffee);

    @Select("select * from t_coffee where id=#{id}")
    Coffee findById(@Param("id") Long id);
}
