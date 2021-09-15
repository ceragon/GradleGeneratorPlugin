package io.github.ceragon.table.bean;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
@Value
public class TableData {
    String tableName;
    /**
     * 表的标签集合
     */
    @Singular
    Set<String> labels;
    /**
     * 表格的列信息
     */
    @Singular
    List<TableColumnInfo> columnInfos;
    /**
     * map中 key：column名称，value：值
     */
    @Singular
    List<Map<String, Object>> cellDatas;
}
