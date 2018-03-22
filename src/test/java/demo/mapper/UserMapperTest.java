package demo.mapper;

import demo.bean.User;
import demo.util.CommonRequestVo;
import demo.util.ConditionUtil;
import demo.util.ConditionsPo;
import demo.util.ConditionVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

/**
 * INSERT INTO `user` VALUES ('0', 'rl', '123', '1', 'test');
 * INSERT INTO `user` VALUES ('1', 'test', '123', '1', 'test');
 * INSERT INTO `user` VALUES ('2', 'abc', '123', '0', 'test');
 * INSERT INTO `user` VALUES ('3', 'qwq', '123', '1', 'bg');
 * INSERT INTO `user` VALUES ('4', 'req', '112', '0', 'bg');
 * INSERT INTO `user` VALUES ('5', 'dasd', '1111', '1', 'cehn');
 * INSERT INTO `user` VALUES ('6', 'qeqw', '112', '0', 'bg');
 * INSERT INTO `user` VALUES ('7', 'ttet', '1111', '1', 'cehn');
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void selectByPrimaryKey() {
        Assert.assertEquals("测试失败", userMapper.selectByPrimaryKey(1).getUserId(), (Integer) 1);
        Assert.assertEquals("测试失败", userMapper.selectByPrimaryKey(2).getUserId(), (Integer) 2);
    }

    /**
     * 测试根据查询条件查数据
     *
     * @throws Exception
     */
    @Test
    public void findByConditions() throws Exception {

        //--------------------页数与页大小测试 --------------------------------

        /**  页码为1  页大小2   **/
        CommonRequestVo commonRequestVo1 = new CommonRequestVo();
        commonRequestVo1.setPageIndex(1);
        commonRequestVo1.setPageSize(2);
        ConditionsPo conditionsPo1 = ConditionUtil.CommonRequestVo2ConditionsPo(commonRequestVo1, User.class);
        List<User> userList1 = userMapper.findByConditions(conditionsPo1);
        Assert.assertEquals("页码与页大小测试失败", userList1.size(), 2);
        Assert.assertEquals("页码与页大小测试失败", userList1.get(0).getUserId(), (Integer) 0);
        Assert.assertEquals("页码与页大小测试失败", userList1.get(1).getUserId(), (Integer) 1);

        /**  页码为2  页大小3   **/
        CommonRequestVo commonRequestVo2 = new CommonRequestVo();
        commonRequestVo2.setPageIndex(2);
        commonRequestVo2.setPageSize(3);
        ConditionsPo conditionsPo2 = ConditionUtil.CommonRequestVo2ConditionsPo(commonRequestVo2, User.class);
        List<User> userList2 = userMapper.findByConditions(conditionsPo2);
        Assert.assertEquals("页码与页大小测试失败", userList2.size(), 3);
        Assert.assertEquals("页码与页大小测试失败", userList2.get(0).getUserId(), (Integer) 3);
        Assert.assertEquals("页码与页大小测试失败", userList2.get(1).getUserId(), (Integer) 4);
        Assert.assertEquals("页码与页大小测试失败", userList2.get(2).getUserId(), (Integer) 5);


        //-----------------条件查询测试-----------------------------------------------------------


        /**  查询user_id 为1 **/
        CommonRequestVo commonRequestVo3 = new CommonRequestVo();
        ConditionVo conditionVo = new ConditionVo();
        conditionVo.setConditionName("user_id");
        conditionVo.setComparisonSymbol("=");
        conditionVo.setConditionValue("1");
        commonRequestVo3.setConditions(Arrays.asList(conditionVo));
        ConditionsPo conditionsPo3 = ConditionUtil.CommonRequestVo2ConditionsPo(commonRequestVo3, User.class);
        List<User> userList3 = userMapper.findByConditions(conditionsPo3);
        Assert.assertEquals("条件查询测试失败", userList3.size(), 1);
        Assert.assertEquals("条件查询测试失败", userList3.get(0).getUserId(), (Integer) 1);


        /**  查询scope 为cehn **/
        CommonRequestVo commonRequestVo4 = new CommonRequestVo();
        ConditionVo conditionVo1 = new ConditionVo();
        conditionVo1.setConditionName("scope");
        conditionVo1.setComparisonSymbol("=");
        conditionVo1.setConditionValue("cehn");
        commonRequestVo4.setConditions(Arrays.asList(conditionVo1));
        ConditionsPo conditionsPo4 = ConditionUtil.CommonRequestVo2ConditionsPo(commonRequestVo4, User.class);
        List<User> userList4 = userMapper.findByConditions(conditionsPo4);
        Assert.assertEquals("条件查询测试失败", userList4.size(), 2);
        Assert.assertEquals("条件查询测试失败", userList4.get(0).getUserId(), (Integer) 5);
        Assert.assertEquals("条件查询测试失败", userList4.get(1).getUserId(), (Integer) 7);


        /**  查询scope 为 test &&  status = 1**/
        CommonRequestVo commonRequestVo5 = new CommonRequestVo();
        ConditionVo conditionVo2 = new ConditionVo();
        conditionVo2.setConditionName("scope");
        conditionVo2.setComparisonSymbol("=");
        conditionVo2.setConditionValue("test");
        ConditionVo conditionVo3 = new ConditionVo();
        conditionVo3.setConditionName("status");
        conditionVo3.setComparisonSymbol("=");
        conditionVo3.setConditionValue("1");
        commonRequestVo5.setConditions(Arrays.asList(conditionVo2, conditionVo3));
        ConditionsPo conditionsPo5 = ConditionUtil.CommonRequestVo2ConditionsPo(commonRequestVo5, User.class);
        List<User> userList5 = userMapper.findByConditions(conditionsPo5);
        Assert.assertEquals("条件查询测试失败", userList5.size(), 2);
        Assert.assertEquals("条件查询测试失败", userList5.get(0).getUserId(), (Integer) 0);
        Assert.assertEquals("条件查询测试失败", userList5.get(1).getUserId(), (Integer) 1);


        /**  查询scope 为 test &&  status = 1   &&  页码为2  页大小为1 **/
        CommonRequestVo commonRequestVo6 = new CommonRequestVo();
        commonRequestVo6.setPageIndex(2);
        commonRequestVo6.setPageSize(1);
        commonRequestVo6.setConditions(Arrays.asList(conditionVo2, conditionVo3));
        ConditionsPo conditionsPo6 = ConditionUtil.CommonRequestVo2ConditionsPo(commonRequestVo6, User.class);
        List<User> userList6 = userMapper.findByConditions(conditionsPo6);
        Assert.assertEquals("条件查询测试失败", userList6.size(), 1);
        Assert.assertEquals("条件查询测试失败", userList6.get(0).getUserId(), (Integer) 1);

        //------------------------字段不存在测试--------------------------
        /** 字段不存在   **/
        try {
            CommonRequestVo commonRequestVo7 = new CommonRequestVo();
            ConditionVo conditionVo7 = new ConditionVo();
            conditionVo7.setConditionName("user_test");
            conditionVo7.setComparisonSymbol("=");
            conditionVo7.setConditionValue("1");
            commonRequestVo7.setConditions(Arrays.asList(conditionVo7));
            ConditionsPo conditionsPo7 = ConditionUtil.CommonRequestVo2ConditionsPo(commonRequestVo7, User.class);
            List<User> userList7 = userMapper.findByConditions(conditionsPo7);
        } catch (Exception ex) {
            Assert.assertEquals("字段不存在测试失败", ex.getMessage(), "转化异常:demo.bean.User不存在userTest字段");
        }


        //----------------------注入测试-----------------------

        /** 注入user_id = 1 or 1 = 1**/
        try {
            CommonRequestVo commonRequestVo7 = new CommonRequestVo();
            ConditionVo conditionVo7 = new ConditionVo();
            conditionVo7.setConditionName("user");
            conditionVo7.setComparisonSymbol("=");
            conditionVo7.setConditionValue("1 or 1 = 1");
            commonRequestVo7.setConditions(Arrays.asList(conditionVo7));
            ConditionsPo conditionsPo7 = ConditionUtil.CommonRequestVo2ConditionsPo(commonRequestVo7, User.class);
            List<User> userList7 = userMapper.findByConditions(conditionsPo7);
        } catch (Exception ex) {
            Assert.assertEquals("注入测试失败", ex.getMessage(), "参数有误");
        }


        //----------------------排序测试-----------------------

        /**  根据 user_id  逆序  **/
        CommonRequestVo commonRequestVo8 = new CommonRequestVo();
        commonRequestVo8.setSorts(Arrays.asList("user_id DESC"));
        ConditionsPo conditionsPo8 = ConditionUtil.CommonRequestVo2ConditionsPo(commonRequestVo8, User.class);
        List<User> userList8 = userMapper.findByConditions(conditionsPo8);
        Assert.assertEquals("排序测试失败", userList8.size(), 8);
        Assert.assertEquals("排序测试失败", userList8.get(0).getUserId(), (Integer) 7);
        Assert.assertEquals("排序测试失败", userList8.get(1).getUserId(), (Integer) 6);
        Assert.assertEquals("排序测试失败", userList8.get(7).getUserId(), (Integer) 0);

    }

    /**
     * 测试根据查询条件查数量
     *
     * @throws Exception
     */
    @Test
    public void countByConditions() throws Exception {

        CommonRequestVo commonRequestVo1 = new CommonRequestVo();
        ConditionVo conditionVo1 = new ConditionVo();
        conditionVo1.setConditionName("scope");
        conditionVo1.setComparisonSymbol("=");
        conditionVo1.setConditionValue("cehn");
        commonRequestVo1.setConditions(Arrays.asList(conditionVo1));
        ConditionsPo conditionsPo1 = ConditionUtil.CommonRequestVo2ConditionsPo(commonRequestVo1, User.class);
        Integer size = userMapper.countByConditions(conditionsPo1);
        Assert.assertEquals("测试失败", size, (Integer) 2);


        CommonRequestVo commonRequestVo5 = new CommonRequestVo();
        ConditionVo conditionVo2 = new ConditionVo();
        conditionVo2.setConditionName("scope");
        conditionVo2.setComparisonSymbol("=");
        conditionVo2.setConditionValue("test");
        ConditionVo conditionVo3 = new ConditionVo();
        conditionVo3.setConditionName("status");
        conditionVo3.setComparisonSymbol("=");
        conditionVo3.setConditionValue("1");
        commonRequestVo5.setConditions(Arrays.asList(conditionVo2, conditionVo3));
        ConditionsPo conditionsPo5 = ConditionUtil.CommonRequestVo2ConditionsPo(commonRequestVo5, User.class);
        Integer size2 = userMapper.countByConditions(conditionsPo5);
        Assert.assertEquals("测试失败", size2, (Integer) 2);

    }


}