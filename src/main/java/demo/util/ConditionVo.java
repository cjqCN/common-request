package demo.util;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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
