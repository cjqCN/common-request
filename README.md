# common-request (通用条件查询)
## 根据条件对数据进行查询
- 配合mybatis使用
- 暂时只支持对单表进行查询，不支持联表查询
- 暂时只支持条件的并查询
- 支持字段排序
- 支持翻页(默认页码为1，页大小为10)
- 支持反Sql注入

## 配置
### mapper
- mapper.java

		@Mapper
		public interface UserMapper {
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

- mapper.xml

	    <select id="findByConditions" resultMap="BaseResultMap">
	        select
	        <include refid="Base_Column_List"/>
	        from user
	        <if test="conditionsPo.conditions!=null">
	            <![CDATA[  ${conditionsPo.conditions} ]]>
	        </if>
	        <choose>
	            <when test="conditionsPo.sort!=null">
	                <![CDATA[  ${conditionsPo.sort} ]]>
	            </when>
	            <otherwise>
	                ORDER BY
	                user_id ASC
	            </otherwise>
	        </choose>
	        <if test="conditionsPo.limit!=null">
	            <![CDATA[  ${conditionsPo.limit} ]]>
	        </if>
	    </select>
	
	    <select id="countByConditions" resultType="java.lang.Integer">
	        select
	        count(*)
	        from user
	        <if test="conditionsPo.conditions!=null">
	            <![CDATA[  ${conditionsPo.conditions} ]]>
	        </if>
	    </select>



### CommonRequestVo && ConditionVo

	@Data
	public class CommonRequestVo {
	
	    @ApiModelProperty(value = "查询第几页")
	    private Integer pageIndex;
	
	    @ApiModelProperty(value = "页码")
	    private Integer pageSize;
	
	    @ApiModelProperty(value = "查询条件")
	    private List<ConditionVo> conditions;
	
	    @ApiModelProperty(value = "排序字段组")
	    private List<String> sorts;
	}
 

	@Data
	public class ConditionVo {
	    /**
	     * 一般为字段名
	     */
	    @ApiModelProperty(value = "条件名称")
	    private String conditionName;
	
	    /**
	     * "<", "<=", "=", ">", ">=", "like", "LIKE", "in", "IN"
	     */
	    @ApiModelProperty(value = "比较符号")
	    private String comparisonSymbol;
	
	
	    @ApiModelProperty(value = "比较值")
	    private String conditionValue;
	}

## 使用
- 查询代码：       

		CommonRequestVo commonRequestVo = new CommonRequestVo();
        ConditionVo conditionVo = new ConditionVo();
		// 添加条件
        conditionVo.setConditionName("scope");
        conditionVo.setComparisonSymbol("=");
        conditionVo.setConditionValue("cehn");
        commonRequestVo.setConditions(Arrays.asList(conditionVo));
		// commonRequestVo -> conditionsPo
        ConditionsPo conditionsPo = ConditionUtil.CommonRequestVo2ConditionsPo(commonRequestVo, User.class);
		// 进行查询
        List<User> userList = userMapper.findByConditions(conditionsPo);


- 结果：

		==>  Preparing: select user_id, user_name, password, status, scope from user
			 WHERE scope = 'cehn' ORDER BY user_id ASC LIMIT 0 ,10 
		==> Parameters: 
		<==    Columns: USER_ID, USER_NAME, PASSWORD, STATUS, SCOPE
		<==        Row: 5, dasd, 1111, 1, cehn
		<==        Row: 7, ttet, 1111, 1, cehn
		<==      Total: 2



## 更多例子

	
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