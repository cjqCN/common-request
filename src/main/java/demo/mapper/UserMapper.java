package demo.mapper;


import demo.bean.User;
import demo.util.ConditionsPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 根据查询条件查数量
     *
     * @param conditionsPo
     * @return
     */
    int countByConditions(@Param("conditionsPo") ConditionsPo conditionsPo);

    /**
     * 根据查询条件查数据
     *
     * @param conditionsPo
     * @return
     */
    List<User> findByConditions(@Param("conditionsPo") ConditionsPo conditionsPo);
}