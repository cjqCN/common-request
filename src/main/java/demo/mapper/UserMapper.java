package demo.mapper;


import demo.bean.User;
import demo.util.ConditionsPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int countByConditions(ConditionsPo conditionsPo);

    List<User> findByConditions(ConditionsPo conditionsPo);
}