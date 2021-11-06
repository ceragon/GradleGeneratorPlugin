package io.github.ceragon.protobuf.bean;

import com.google.protobuf.DescriptorProtos.SourceCodeInfo;
import com.google.protobuf.DescriptorProtos.SourceCodeInfo.Location;

import java.util.Arrays;
import java.util.List;

class DescriptorUtils {
    final static ThreadLocal<List<Integer>> MESSAGE_PATH_LIST = ThreadLocal.withInitial(() -> Arrays.asList(4, 0));
    final static ThreadLocal<List<Integer>> MESSAGE_FIELD_PATH_LIST = ThreadLocal.withInitial(() -> Arrays.asList(4, 0, 2, 0));
    final static ThreadLocal<List<Integer>> ENUM_PATH_LIST = ThreadLocal.withInitial(() -> Arrays.asList(5, 0));
    final static ThreadLocal<List<Integer>> ENUM_VALUE_PATH_LIST = ThreadLocal.withInitial(() -> Arrays.asList(5, 0, 2, 0));

    private static List<Integer> modifyMessagePath(int messageIndex) {
        List<Integer> result = MESSAGE_PATH_LIST.get();
        result.set(1, messageIndex);
        return result;
    }

    private static List<Integer> modifyMessageFieldPath(int messageIndex, int fieldIndex) {
        List<Integer> result = MESSAGE_FIELD_PATH_LIST.get();
        result.set(1, messageIndex);
        result.set(3, fieldIndex);
        return result;
    }

    private static Location getLocation(SourceCodeInfo sourceCodeInfo, List<Integer> pathList) {
        return sourceCodeInfo.getLocationList().stream()
                .filter(location -> location.getPathList().equals(pathList))
                .findFirst().orElse(Location.getDefaultInstance());
    }

    public static Location getLocationOfMessage(SourceCodeInfo sourceCodeInfo, int messageIndex) {
        List<Integer> pathList = modifyMessagePath(messageIndex);
        return getLocation(sourceCodeInfo, pathList);
    }

    public static Location getLocationOfEnum(SourceCodeInfo sourceCodeInfo, int index) {
        List<Integer> result = ENUM_PATH_LIST.get();
        result.set(1, index);
        return getLocation(sourceCodeInfo, result);
    }

    public static Location getLocationOfEnum(SourceCodeInfo sourceCodeInfo, int enumIndex, int index) {
        List<Integer> result = ENUM_VALUE_PATH_LIST.get();
        result.set(1, enumIndex);
        result.set(3, index);
        return getLocation(sourceCodeInfo, result);
    }

    public static Location getLocationOfField(SourceCodeInfo sourceCodeInfo, int messageIndex, int fieldIndex) {
        List<Integer> pathList = modifyMessageFieldPath(messageIndex, fieldIndex);
        return getLocation(sourceCodeInfo, pathList);
    }
}
