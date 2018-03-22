package demo.util;


import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author chenjinquan
 */
public final class ConditionUtil {

    final static String TRANSLATION_EXCEPTION_STR = "转化异常";
    final static String BLANK = " ";
    final static String WHERE = BLANK + "WHERE" + BLANK;
    final static String ORDER_BY = BLANK + "ORDER BY" + BLANK;
    final static String AND = BLANK + "AND" + BLANK;
    final static String LIMIT = BLANK + "LIMIT" + BLANK;
    final static String COMMA = " ,";
    final static String UNDERLINE = "_";
    final static String SINGLE_QUOTE = "'";
    final static Set<String> COMPARISON_SYM = Stream.of("<", "<=", "=", ">", ">=", "like", "LIKE", "in", "IN").collect(Collectors.toSet());
    final static Set<String> SORT_SYM = Stream.of("asc", "desc", "ASC", "DESC").collect(Collectors.toSet());

    /**
     * commonRequestVo转ConditionsPo , 只对单表查询有效,默认分页
     * <p>
     * WHERE product_detail_url LIKE 'www.baidu.com' AND sender_name LIKE 'chenjinquan'
     * ORDER BY sender_name asc , product_detail_url desc
     * LIMIT 3 ,3
     * </>
     *
     * @param commonRequestVo
     * @param poClass         po类
     * @return
     */
    public static ConditionsPo CommonRequestVo2ConditionsPo(CommonRequestVo commonRequestVo, Class poClass) throws Exception {
        ConditionsPo conditionsPo = new ConditionsPo();
        List<ConditionVo> conditions = commonRequestVo.getConditions();
        List<String> sorts = commonRequestVo.getSorts();
        if (!CollectionUtils.isEmpty(conditions)) {
            conditionsPo.setConditions(getConditionSql(conditions, poClass));
        }
        if (!CollectionUtils.isEmpty(sorts)) {
            conditionsPo.setSort(getSortSql(sorts, poClass));
        }
        conditionsPo.setLimit(getLimitSql(commonRequestVo.getPageIndex(), commonRequestVo.getPageSize()));
        return conditionsPo;
    }

    /**
     * commonRequestVo转ConditionsPo , 只对单表查询有效,默认不分页
     */
    public static ConditionsPo CommonRequestVo2ConditionsPoNoPage(CommonRequestVo commonRequestVo, Class poClass) throws Exception {
        ConditionsPo conditionsPo = new ConditionsPo();
        List<ConditionVo> conditions = commonRequestVo.getConditions();
        List<String> sorts = commonRequestVo.getSorts();
        if (!CollectionUtils.isEmpty(conditions)) {
            conditionsPo.setConditions(getConditionSql(conditions, poClass));
        }
        if (!CollectionUtils.isEmpty(sorts)) {
            conditionsPo.setSort(getSortSql(sorts, poClass));
        }
        if (!StringUtils.isEmpty(commonRequestVo.getPageIndex()) && !StringUtils.isEmpty(commonRequestVo.getPageSize())) {
            conditionsPo.setLimit(getLimitSql(commonRequestVo.getPageIndex(), commonRequestVo.getPageSize()));
        }
        return conditionsPo;
    }

    /**
     * commonRequestVo转ConditionsPo ,特殊条件解析
     */
    public static ConditionsPo CommonRequestVo2ConditionsPoByCustom(CommonRequestVo commonRequestVo, Class poClass, String conditionsSql) throws Exception {
        ConditionsPo conditionsPo = new ConditionsPo();
        List<ConditionVo> conditions = commonRequestVo.getConditions();
        List<String> sorts = commonRequestVo.getSorts();
        if (!CollectionUtils.isEmpty(conditions)) {
            conditionsPo.setConditions(conditionsSql);
        }
        if (!CollectionUtils.isEmpty(sorts)) {
            conditionsPo.setSort(getSortSql(sorts, poClass));
        }
        conditionsPo.setLimit(getLimitSql(commonRequestVo.getPageIndex(), commonRequestVo.getPageSize()));
        return conditionsPo;
    }


    /**
     * 判断ConditionsVo 是否符合规范
     *
     * @param conditionVo
     * @return
     * @throws Exception
     */
    public static boolean assertConditionsVo(ConditionVo conditionVo) throws Exception {
        String conditionValue = conditionVo.getConditionValue().trim();
        String conditionName = conditionVo.getConditionName();
        String comparisonSymbol = conditionVo.getComparisonSymbol();
        if (StringUtils.isEmpty(conditionName)
                || StringUtils.isEmpty(comparisonSymbol)
                || StringUtils.isEmpty(conditionValue)) {
            return false;
        }
        assertSqlInject(conditionName);
        assertConditionSymbol(comparisonSymbol);
        assertSqlInject(conditionValue);
        return true;
    }


    private static String getLimitSql(Integer pageIndexStr, Integer pageSizeStr) throws Exception {
        int pageIndex;
        int pageSize;
        try {
            pageIndex = Integer.valueOf(pageIndexStr);
        } catch (Exception ex) {
            pageIndex = 1;
        }
        try {
            pageSize = Integer.valueOf(pageSizeStr);
        } catch (Exception ex) {
            pageSize = 10;
        }
        if (pageIndex <= 0) {
            throw new Exception("页码不能为负数");
        }
        if (pageSize <= 0) {
            throw new Exception("页大小不能为负数");
        }
        return LIMIT + (pageIndex - 1) * pageSize + COMMA + pageSize;

    }


    private static String getConditionSql(List<ConditionVo> conditions, Class poClass) throws Exception {
        if (CollectionUtils.isEmpty(conditions)) {
            return null;
        }
        StringBuffer conditionSql = new StringBuffer(WHERE);
        for (ConditionVo condition : conditions) {
            String conditionName = condition.getConditionName().trim();
            String comparisonSymbol = condition.getComparisonSymbol().trim();
            String conditionValue = condition.getConditionValue().trim();
            if (!isValid(conditionValue)) {
                throw new Exception("参数有误");
            }
            if (!existsField(poClass, fieldToProperty(conditionName))) {
                throw new Exception(TRANSLATION_EXCEPTION_STR + ":" + poClass.getName()
                        + "不存在" + fieldToProperty(condition.getConditionName()) + "字段");
            }
            if (!COMPARISON_SYM.contains(comparisonSymbol)) {
                throw new Exception(TRANSLATION_EXCEPTION_STR + ":"
                        + "不存在" + comparisonSymbol + "比较符号");
            }
            conditionSql.append(conditionName)
                    .append(BLANK)
                    .append(comparisonSymbol)
                    .append(BLANK)
                    .append(SINGLE_QUOTE)
                    .append(conditionValue)
                    .append(SINGLE_QUOTE);
            conditionSql.append(AND);
        }
        return conditionSql.substring(0, conditionSql.length() - AND.length());
    }

    private static String getSortSql(List<String> sorts, Class poClass) throws Exception {
        StringBuffer sortSql = new StringBuffer(ORDER_BY);
        String field = null;
        String sortType = null;
        for (String sort : sorts) {
            try {
                field = sort.substring(0, sort.indexOf(BLANK)).trim();
                sortType = sort.substring(sort.indexOf(BLANK), sort.length()).trim();
            } catch (Exception ex) {
                throw new Exception(TRANSLATION_EXCEPTION_STR + ":" + sort + " 格式错误");
            }
            if (!existsField(poClass, fieldToProperty(field))) {
                throw new Exception(TRANSLATION_EXCEPTION_STR + ":" + poClass.getName()
                        + "不存在" + fieldToProperty(field) + "字段");
            }
            if (!SORT_SYM.contains(sortType)) {
                throw new Exception(TRANSLATION_EXCEPTION_STR + ":"
                        + "不存在" + sortType + "排序符号");
            }
            sortSql.append(field).append(BLANK).append(sortType).append(COMMA);
        }
        return sortSql.substring(0, sortSql.length() - COMMA.length());
    }

    public static boolean existsField(Class clz, String fieldName) {
        try {
            return clz.getDeclaredField(fieldName) != null;
        } catch (Exception e) {
        }
        if (clz != Object.class) {
            return existsField(clz.getSuperclass(), fieldName);
        }
        return false;
    }

    /**
     * 对象属性转换为字段  例如：userName to user_name
     *
     * @param property 字段名
     * @return
     */
    public static String propertyToField(String property) {
        if (null == property) {
            return BLANK;
        }
        char[] chars = property.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (char c : chars) {
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE + Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 字段转换成对象属性 例如：user_name to userName
     *
     * @param field
     * @return
     */
    public static String fieldToProperty(String field) {
        if (null == field) {
            return BLANK;
        }
        char[] chars = field.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '_') {
                int j = i + 1;
                if (j < chars.length) {
                    sb.append(Character.toUpperCase(chars[j]));
                    i++;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 防注入<br/>
     * 正则表达式
     **/
    private static String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
            + "(\\b(select|update|union|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
    private static Pattern sqlPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);

    public static boolean isValid(String str) {
        if (sqlPattern.matcher(str).find()) {
            return false;
        }
        return true;
    }


    /**
     * 自处理conditionVo时，判断是否有注入
     *
     * @param condition
     * @return
     * @throws Exception
     */
    public static boolean assertSqlInject(String condition) throws Exception {
        if (!isValid(condition)) {
            throw new Exception("条件参数有误");
        }
        return true;
    }

    public static boolean assertConditionSymbol(String conditionSymbol) throws Exception {
        if (!COMPARISON_SYM.contains(conditionSymbol)) {
            throw new Exception("条件参数有误");
        }
        return true;
    }

}
