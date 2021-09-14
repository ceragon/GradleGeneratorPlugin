package com.ceragon.protobuf.protoc;


import com.ceragon.PluginContext;
import com.ceragon.protobuf.bean.ProtoFileDescPojo;
import com.ceragon.protobuf.bean.ProtoMessageDescPojo;
import com.ceragon.protobuf.extension.EveryMsgBuildConfig;
import com.ceragon.protobuf.extension.EveryProtoBuildConfig;
import com.ceragon.protobuf.extension.TotalMsgBuildConfig;
import com.ceragon.util.CodeGenTool;
import com.ceragon.util.PathFormat;
import com.ceragon.util.VelocityUtil;
import lombok.Value;
import org.gradle.api.logging.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Value
public class MsgCodeBuild {
    List<ProtoFileDescPojo> protoFileDescPojoList;

    //region -------- 使用全部消息的集合生成代码 ------------
    public boolean buildTotalMsgCode(String resourceRoot, List<TotalMsgBuildConfig> configList) {
        Logger log = PluginContext.log();
        Map<String, Object> content = new HashMap<>();
        List<ProtoMessageDescPojo> messageDescPojoList = protoFileDescPojoList.stream().flatMap(pojo -> pojo.getMessageList().stream())
                .collect(Collectors.toList());
        content.put("totalMsgList", messageDescPojoList);
        content.put("totalMsgGroupList", protoFileDescPojoList);
        return configList.stream().allMatch(config -> processTotalMsgCode(log, resourceRoot, config, content));
    }

    private boolean processTotalMsgCode(org.gradle.api.logging.Logger log, String resourceRoot, TotalMsgBuildConfig config, Map<String, Object> content) {
        PathFormat pathFormat = PluginContext.pathFormat();
        String sourceName = config.getVmFile();
        String destPath = pathFormat.format(config.getTargetFile());
        try {
            CodeGenTool.createCodeByPath(resourceRoot, sourceName, destPath, config.isOverwrite(), content);
            return true;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
    //endregion

    //region -------- 使用every消息的集合生成代码 ------------
    public boolean buildEveryMsgCode(String resourceRoot, List<EveryMsgBuildConfig> everyMsg) {
        return everyMsg.stream().allMatch(config -> processEveryMsgCodeConfig(resourceRoot, config));
    }

    private boolean processEveryMsgCodeConfig(String resourceRoot, EveryMsgBuildConfig config) {
        return this.protoFileDescPojoList.stream()
                .filter(pojo -> config.getProtoNameMatch().stream().anyMatch(protoNameMatch -> pojo.getName().matches(protoNameMatch)))
                .allMatch(pojo -> processEveryMsgCodePojoAndConfig(resourceRoot, pojo, config));
    }

    private boolean processEveryMsgCodePojoAndConfig(String resourceRoot, ProtoFileDescPojo pojo, EveryMsgBuildConfig config) {
        return pojo.getMessageList().stream()
                .filter(protoMessagePojo -> config.getMsgNameMatch().stream().anyMatch(msgNameMatch -> protoMessagePojo.getName().matches(msgNameMatch)))
                .allMatch(protoMessagePojo -> processEveryMsgCode(resourceRoot, protoMessagePojo, config));
    }

    private boolean processEveryMsgCode(String resourceRoot, ProtoMessageDescPojo protoMessagePojo, EveryMsgBuildConfig config) {
        try {
            Map<String, Object> content = new HashMap<>();
            content.put("msg", protoMessagePojo);
            content.put("util", VelocityUtil.getInstance());
            String destPath = PluginContext.pathFormat().format(config.getTargetFile(), "MsgName", protoMessagePojo.getName());
            CodeGenTool.createCodeByPath(resourceRoot, config.getVmFile(), destPath, config.isOverwrite(), content);
            return true;
        } catch (IOException e) {
            PluginContext.log().error(e.getMessage(), e);
            return false;
        }
    }

    //endregion

    public boolean buildEveryProtoCode(String resourceRoot, List<EveryProtoBuildConfig> everyProto) {
        return everyProto.stream().allMatch(config -> processEveryProtoCodeConfig(resourceRoot, config));
    }

    private boolean processEveryProtoCodeConfig(String resourceRoot, EveryProtoBuildConfig config) {
        return this.protoFileDescPojoList.stream()
                .filter(pojo -> config.getProtoNameMatch().stream().anyMatch(protoNameMatch -> pojo.getName().matches(protoNameMatch)))
                .allMatch(pojo -> processEveryProtoCode(resourceRoot, pojo, config));
    }

    private boolean processEveryProtoCode(String resourceRoot, ProtoFileDescPojo protoFileDescPojo, EveryProtoBuildConfig config) {
        try {
            Map<String, Object> content = new HashMap<>();
            content.put("proto", protoFileDescPojo);
            content.put("util", VelocityUtil.getInstance());
            String destPath = PluginContext.pathFormat().format(config.getTargetFile());
            CodeGenTool.createCodeByPath(resourceRoot, config.getVmFile(), destPath, config.isOverwrite(), content);
            return true;
        } catch (IOException e) {
            PluginContext.log().error(e.getMessage(), e);
            return false;
        }
    }


}
