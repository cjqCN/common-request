package demo.util;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class CommonRequestVo {

    @ApiModelProperty(value = "查询第几页")
    private String pageIndex;

    @ApiModelProperty(value = "页码")
    private String pageSize;

    @ApiModelProperty(value = "查询条件")
    private List<ConditionsVo> conditions;

    @ApiModelProperty(value = "排序字段组")
    private List<String> sorts;
}
