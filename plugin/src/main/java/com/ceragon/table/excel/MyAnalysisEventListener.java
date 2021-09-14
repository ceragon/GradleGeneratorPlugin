package com.ceragon.table.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.ceragon.table.bean.TableColumnInfo;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class MyAnalysisEventListener extends AnalysisEventListener<Map<Integer, String>> {
    private static final int BATCH_COUNT = 5;
    private List<TableColumnInfo> columnInfos;
    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {

    }

    private void saveData() {
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
    }
}
