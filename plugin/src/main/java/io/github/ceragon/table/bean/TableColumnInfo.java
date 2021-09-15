package io.github.ceragon.table.bean;

import io.github.ceragon.table.constant.ColumnType;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Set;

@Builder
@Value
public class TableColumnInfo {
    String name;
    String desc;
    ColumnType type;
    @Singular
    Set<String> labels;
}
