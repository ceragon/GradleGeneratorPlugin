package io.github.ceragon.protobuf.constant;

import java.util.Arrays;
import java.util.List;

public class ProtoConstant {
    public final static String UTIL_KEY = "util";
    public final static ThreadLocal<List<Integer>> MESSAGE_PATH_LIST = ThreadLocal.withInitial(() -> Arrays.asList(4, 0));
    public final static ThreadLocal<List<Integer>> MESSAGE_FIELD_PATH_LIST = ThreadLocal.withInitial(() -> Arrays.asList(4, 0, 2, 0));

    public static List<Integer> modifyMessagePath(int messageIndex) {
        List<Integer> result = MESSAGE_PATH_LIST.get();
        result.set(1, messageIndex);
        return result;
    }

    public static List<Integer> modifyMessageFieldPath(int messageIndex, int fieldIndex) {
        List<Integer> result = MESSAGE_FIELD_PATH_LIST.get();
        result.set(1, messageIndex);
        result.set(3, fieldIndex);
        return result;
    }
}
