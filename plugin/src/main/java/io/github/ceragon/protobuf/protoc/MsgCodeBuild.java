package io.github.ceragon.protobuf.protoc;


import io.github.ceragon.PluginContext;
import io.github.ceragon.protobuf.bean.EnumDescriptorDelegate;
import io.github.ceragon.protobuf.bean.FileDescriptorDelegate;
import io.github.ceragon.protobuf.bean.MessageDescriptorDelegate;
import io.github.ceragon.protobuf.constant.ProtoConstant;
import io.github.ceragon.protobuf.extension.EveryEnumBuildConfig;
import io.github.ceragon.protobuf.extension.EveryMsgBuildConfig;
import io.github.ceragon.protobuf.extension.EveryProtoBuildConfig;
import io.github.ceragon.protobuf.extension.TotalMsgBuildConfig;
import io.github.ceragon.protobuf.protoc.util.FileUtil;
import io.github.ceragon.util.CodeGenTool;
import io.github.ceragon.util.PathFormat;
import io.github.ceragon.util.TemplateUtil;
import lombok.Value;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Value
public class MsgCodeBuild {
    List<FileDescriptorDelegate> protoFileDescList;

    private Map<String, Object> createContentMap() {
        Map<String, Object> content = new HashMap<>();
        content.put(ProtoConstant.UTIL_KEY, TemplateUtil.getInstance());
        content.putAll(PluginContext.project().getExtensions().getExtraProperties().getProperties());
        return content;
    }

    //region -------- 使用全部消息的集合生成代码 ------------
    public boolean buildTotalMsgCode(String resourceRoot, Set<TotalMsgBuildConfig> configList) {
        List<MessageDescriptorDelegate> messageDescPojoList = protoFileDescList.stream().flatMap(pojo -> pojo.getMessageList().stream())
                .collect(Collectors.toList());
        Map<String, Object> content = createContentMap();
        content.put("totalMsgList", messageDescPojoList);
        content.put("totalMsgGroupList", protoFileDescList);
        return configList.stream().allMatch(config -> processTotalMsgCode(resourceRoot, config, content));
    }

    private boolean processTotalMsgCode(String resourceRoot, TotalMsgBuildConfig config, Map<String, Object> content) {
        PathFormat pathFormat = PluginContext.pathFormat();
        String sourceName = config.getVmFile();
        String destPath = pathFormat.format(config.getTargetFile());
        try {
            CodeGenTool.createCodeByPath(resourceRoot, sourceName, destPath, config.isOverwrite(), content);
            PluginContext.log().quiet("totalMsgCode generate success!name={}", config.getName());
            return true;
        } catch (IOException e) {
            PluginContext.log().error(e.getMessage(), e);
            return false;
        }
    }
    //endregion

    //region -------- 使用every消息的集合生成代码 ------------
    public boolean buildEveryMsgCode(String resourceRoot, Set<EveryMsgBuildConfig> everyMsg) {
        return everyMsg.stream().allMatch(config -> processEveryMsgCodeConfig(resourceRoot, config));
    }

    private boolean processEveryMsgCodeConfig(String resourceRoot, EveryMsgBuildConfig config) {
        return this.protoFileDescList.stream()
                .filter(desc -> FileUtil.nameFilter(config.getProtoNameMatch(), desc.getName()))
                .allMatch(desc -> processEveryMsgCodePojoAndConfig(resourceRoot, desc, config));
    }

    private boolean processEveryMsgCodePojoAndConfig(String resourceRoot, FileDescriptorDelegate pojo, EveryMsgBuildConfig config) {
        return pojo.getMessageList().stream()
                .filter(desc -> FileUtil.nameFilter(config.getMsgNameMatch(), desc.getName()))
                .allMatch(desc -> processEveryMsgCode(resourceRoot, desc, config));
    }

    private boolean processEveryMsgCode(String resourceRoot, MessageDescriptorDelegate protoMessagePojo, EveryMsgBuildConfig config) {
        try {
            Map<String, Object> content = createContentMap();
            content.put("msg", protoMessagePojo);
            String destPath = PluginContext.pathFormat().format(config.getTargetFile(), "MsgName", protoMessagePojo.getName());
            CodeGenTool.createCodeByPath(resourceRoot, config.getVmFile(), destPath, config.isOverwrite(), content);
            PluginContext.log().quiet("everyMsgCode generate success!name={}", config.getName());
            return true;
        } catch (IOException e) {
            PluginContext.log().error(e.getMessage(), e);
            return false;
        }
    }

    //endregion

    //region
    public boolean buildEveryProtoCode(String resourceRoot, Set<EveryProtoBuildConfig> everyProto) {
        return everyProto.stream().allMatch(config -> processEveryProtoCodeConfig(resourceRoot, config));
    }

    private boolean processEveryProtoCodeConfig(String resourceRoot, EveryProtoBuildConfig config) {
        return this.protoFileDescList.stream()
                .filter(desc -> FileUtil.nameFilter(config.getProtoNameMatch(), desc.getName()))
                .allMatch(desc -> processEveryProtoCode(resourceRoot, desc, config));
    }

    private boolean processEveryProtoCode(String resourceRoot, FileDescriptorDelegate protoFileDesc, EveryProtoBuildConfig config) {
        try {
            Map<String, Object> content = createContentMap();
            content.put("proto", protoFileDesc);
            String destPath = PluginContext.pathFormat().format(config.getTargetFile());
            CodeGenTool.createCodeByPath(resourceRoot, config.getVmFile(), destPath, config.isOverwrite(), content);
            PluginContext.log().quiet("everyProtoCode generate success!name={}", config.getName());
            return true;
        } catch (IOException e) {
            PluginContext.log().error(e.getMessage(), e);
            return false;
        }
    }
    //endregion
//region -------- 使用every枚举的集合生成代码 ------------
    public boolean buildEveryEnumCode(String resourceRoot, Set<EveryEnumBuildConfig> everyEnum) {
        return everyEnum.stream().allMatch(config -> processEveryEnumCodeConfig(resourceRoot, config));
    }

    private boolean processEveryEnumCodeConfig(String resourceRoot, EveryEnumBuildConfig config) {
        return this.protoFileDescList.stream()
                .filter(desc -> FileUtil.nameFilter(config.getProtoNameMatch(), desc.getName()))
                .allMatch(desc -> processEveryEnumCodePojoAndConfig(resourceRoot, desc, config));
    }

    private boolean processEveryEnumCodePojoAndConfig(String resourceRoot, FileDescriptorDelegate delegate, EveryEnumBuildConfig config) {
        return delegate.getEnumList().stream()
                .filter(desc -> FileUtil.nameFilter(config.getEnumNameMatch(), desc.getName()))
                .allMatch(desc -> processEveryEnumCode(resourceRoot, desc, config));
    }

    private boolean processEveryEnumCode(String resourceRoot, EnumDescriptorDelegate delegate, EveryEnumBuildConfig config) {
        try {
            Map<String, Object> content = createContentMap();
            content.put("enum", delegate);
            String destPath = PluginContext.pathFormat().format(config.getTargetFile(), "EnumName", delegate.getName());
            CodeGenTool.createCodeByPath(resourceRoot, config.getVmFile(), destPath, config.isOverwrite(), content);
            PluginContext.log().quiet("everyEnumCode generate success!name={}", config.getName());
            return true;
        } catch (IOException e) {
            PluginContext.log().error(e.getMessage(), e);
            return false;
        }
    }
    //endregion
}
