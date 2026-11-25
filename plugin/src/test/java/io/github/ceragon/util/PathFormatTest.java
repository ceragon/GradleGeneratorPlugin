package io.github.ceragon.util;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PathFormatTest {

    private PathFormat newPathFormat() {
        Project project = ProjectBuilder.builder().build();
        return new PathFormat(project);
    }

    @Test
    void protoNamePlaceholderSupportsAllCases() {
        PathFormat pf = newPathFormat();
        assertEquals("x/Foo.java", pf.format("x/${ProtoName}.java", "ProtoName", "Foo"));
        assertEquals("x/Foo.java", pf.format("x/${protoname}.java", "ProtoName", "Foo"));
        assertEquals("x/Foo.java", pf.format("x/${PROTONAME}.java", "ProtoName", "Foo"));
    }

    @Test
    void protoNamePlaceholderDoesNotTouchOtherPlaceholders() {
        PathFormat pf = newPathFormat();
        assertEquals("x/${MsgName}.java", pf.format("x/${MsgName}.java", "ProtoName", "Foo"));
    }

    @Test
    void twoArgFormatKeepsUnknownPlaceholder() {
        PathFormat pf = newPathFormat();
        assertEquals("x/${ProtoName}.java", pf.format("x/${ProtoName}.java"));
    }

    @Test
    void msgNamePlaceholderStillWorks() {
        PathFormat pf = newPathFormat();
        assertEquals("x/Bar.java", pf.format("x/${MsgName}.java", "MsgName", "Bar"));
    }
}
