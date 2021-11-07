package io.github.ceragon.protobuf.bean;

import com.google.protobuf.DescriptorProtos.SourceCodeInfo;
import com.google.protobuf.DescriptorProtos.SourceCodeInfo.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DescriptorUtils {
    final static ThreadLocal<List<Integer>> MESSAGE_PATH_LIST = ThreadLocal.withInitial(() -> Arrays.asList(4, 0));
    final static ThreadLocal<List<Integer>> MESSAGE_FIELD_PATH_LIST = ThreadLocal.withInitial(() -> Arrays.asList(2, 0));
    final static ThreadLocal<List<Integer>> ENUM_PATH_LIST = ThreadLocal.withInitial(() -> Arrays.asList(5, 0));
    final static ThreadLocal<List<Integer>> ENUM_VALUE_PATH_LIST = ThreadLocal.withInitial(() -> Arrays.asList(2, 0));
    final static ThreadLocal<List<Integer>> INNER_MESSAGE_PATH_LIST = ThreadLocal.withInitial(() -> Arrays.asList(3, 0));
    final static ThreadLocal<List<Integer>> INNER_ENUM_PATH_LIST = ThreadLocal.withInitial(() -> Arrays.asList(4, 0));

    private static List<Integer> modifyMessagePath(List<Integer> parent, int messageIndex) {
        List<Integer> result;
        if (parent.isEmpty()) {
            result = MESSAGE_PATH_LIST.get();
        } else {
            result = INNER_MESSAGE_PATH_LIST.get();
        }
        result.set(1, messageIndex);
        List<Integer> pathList = new ArrayList<>(parent);
        pathList.addAll(result);
        return pathList;
    }

    private static List<Integer> modifyMessageFieldPath(List<Integer> parent, int fieldIndex) {
        List<Integer> result = MESSAGE_FIELD_PATH_LIST.get();
        result.set(1, fieldIndex);
        List<Integer> pathList = new ArrayList<>(parent);
        pathList.addAll(result);
        return pathList;
    }

    private static List<Integer> modifyEnumPath(List<Integer> parent, int enumIndex) {
        List<Integer> result;
        if (parent.isEmpty()) {
            result = ENUM_PATH_LIST.get();
        } else {
            result = INNER_ENUM_PATH_LIST.get();
        }
        result.set(1, enumIndex);
        List<Integer> pathList = new ArrayList<>(parent);
        pathList.addAll(result);
        return pathList;
    }

    private static List<Integer> modifyEnumValuePath(List<Integer> parent, int valueIndex) {
        List<Integer> result = ENUM_VALUE_PATH_LIST.get();
        result.set(1, valueIndex);
        List<Integer> pathList = new ArrayList<>(parent);
        pathList.addAll(result);
        return pathList;
    }

    private static Location getLocation(SourceCodeInfo sourceCodeInfo, List<Integer> pathList) {
        return sourceCodeInfo.getLocationList().stream().filter(location -> location.getPathList().equals(pathList)).findFirst().orElse(Location.getDefaultInstance());
    }

    public static Location getLocationOfMessage(SourceCodeInfo sourceCodeInfo, List<Integer> parent, int messageIndex) {
        List<Integer> pathList = modifyMessagePath(parent, messageIndex);
        return getLocation(sourceCodeInfo, pathList);
    }
    public static Location getLocationOfField(SourceCodeInfo sourceCodeInfo, List<Integer> parent, int fieldIndex) {
        List<Integer> pathList = modifyMessageFieldPath(parent, fieldIndex);
        return getLocation(sourceCodeInfo, pathList);
    }
    public static Location getLocationOfEnum(SourceCodeInfo sourceCodeInfo, List<Integer> parent, int index) {
        List<Integer> result = modifyEnumPath(parent, index);
        return getLocation(sourceCodeInfo, result);
    }

    public static Location getLocationOfEnumValue(SourceCodeInfo sourceCodeInfo, List<Integer> parent, int index) {
        List<Integer> result = modifyEnumValuePath(parent, index);
        return getLocation(sourceCodeInfo, result);
    }


}
