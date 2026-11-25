package io.github.ceragon.protobuf.protoc.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUtilTest {

    @Test
    void protoNameOfSimple() {
        assertEquals("ProtoLogin", FileUtil.protoNameOf("ProtoLogin.proto"));
    }

    @Test
    void protoNameOfSubDirForwardSlash() {
        assertEquals("ProtoLogin", FileUtil.protoNameOf("net/ProtoLogin.proto"));
    }

    @Test
    void protoNameOfSubDirBackSlash() {
        assertEquals("ProtoLogin", FileUtil.protoNameOf("net\\ProtoLogin.proto"));
    }

    @Test
    void protoNameOfOnlyLastExtensionRemoved() {
        assertEquals("a.b", FileUtil.protoNameOf("a.b.proto"));
    }

    @Test
    void protoNameOfNoExtension() {
        assertEquals("NoSuffix", FileUtil.protoNameOf("NoSuffix"));
    }

    @Test
    void nameFilterMatchAllWhenEmpty() {
        assertTrue(FileUtil.nameFilter(null, "Any.proto"));
        assertTrue(FileUtil.nameFilter(Collections.emptyList(), "Any.proto"));
    }

    @Test
    void nameFilterIsFullMatchRegex() {
        assertTrue(FileUtil.nameFilter(Arrays.asList(".*ProtoLogin\\.proto"), "net/ProtoLogin.proto"));
        assertFalse(FileUtil.nameFilter(Arrays.asList("ProtoLogin\\.proto"), "net/ProtoLogin.proto"));
    }
}
