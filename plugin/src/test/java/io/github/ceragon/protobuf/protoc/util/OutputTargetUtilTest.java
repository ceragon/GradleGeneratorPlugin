package io.github.ceragon.protobuf.protoc.util;

import io.github.ceragon.PluginContext;
import io.github.ceragon.protobuf.constant.ContextKey;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import io.github.ceragon.util.PluginTaskException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 校验 outputTargets 拼装出的 protoc 命令行：插件路径、通用 --<type>_opt 与 -I 目录。
 */
class OutputTargetUtilTest {

    private static final String PROTOC_VERSION = "3.16.0";

    private final List<File> includeDirectories = new ArrayList<>();

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        Project project = ProjectBuilder.builder().build();
        PluginContext.init(project);
        PluginContext.buildContext().setValue(ContextKey.PROTOC_VERSION, PROTOC_VERSION);
        PluginContext.buildContext().setValue(ContextKey.INCLUDE_IMPORTS, false);
        includeDirectories.clear();
    }

    private Collection<String> command(String type, String pluginPath, String outputOptions) throws Exception {
        return OutputTargetUtil.buildCommand(includeDirectories, new File("demo/Demo.proto"), type,
                pluginPath, "out", outputOptions);
    }

    @Test
    void builtinTypeGetsOutputOptions() throws Exception {
        Collection<String> cmd = command("java", null, "annotate_code=true");
        assertTrue(cmd.contains("--java_out=out"), "cmd=" + cmd);
        assertTrue(cmd.contains("--java_opt=annotate_code=true"), "非 js 类型统一走 --<type>_opt，cmd=" + cmd);
    }

    @Test
    void csharpAndGoKeepOutputOptions() throws Exception {
        Collection<String> csharp = command("csharp", null, "base_namespace=Demo");
        assertTrue(csharp.contains("--csharp_out=out"), "cmd=" + csharp);
        assertTrue(csharp.contains("--csharp_opt=base_namespace=Demo"), "cmd=" + csharp);

        Collection<String> go = command("go", null, "paths=source_relative");
        assertTrue(go.contains("--go_out=out"), "cmd=" + go);
        assertTrue(go.contains("--go_opt=paths=source_relative"), "cmd=" + go);
    }

    @Test
    void jsKeepsOptionsInsideJsOut() throws Exception {
        Collection<String> cmd = command("js", null, "import_style=commonjs");
        assertTrue(cmd.contains("--js_out=import_style=commonjs:out"), "cmd=" + cmd);
        assertFalse(cmd.stream().anyMatch(arg -> arg.startsWith("--js_opt=")), "js 不应再拼 --js_opt，cmd=" + cmd);
    }

    @Test
    void blankOutputOptionsEmitsOnlyOutFlag() throws Exception {
        Collection<String> cmd = command("csharp", null, "  ");
        assertTrue(cmd.contains("--csharp_out=out"), "cmd=" + cmd);
        assertFalse(cmd.stream().anyMatch(arg -> arg.contains("_opt=")), "没有 outputOptions 时不应拼 --<type>_opt，cmd=" + cmd);
    }

    @Test
    void pluginPathBecomesPluginFlag() throws Exception {
        Collection<String> cmd = command("go", "C:/tools/protoc-gen-go.exe", null);
        assertTrue(cmd.contains("--plugin=protoc-gen-go=C:/tools/protoc-gen-go.exe"), "cmd=" + cmd);
        assertTrue(cmd.contains("--go_out=out"), "cmd=" + cmd);
    }

    @Test
    void absentPluginPathAddsNoPluginFlag() throws Exception {
        Collection<String> cmd = command("go", null, null);
        assertFalse(cmd.stream().anyMatch(arg -> arg.startsWith("--plugin=")), "cmd=" + cmd);
    }

    @Test
    void includeDirectoriesBecomeIncludeFlags() throws Exception {
        File includeDir = new File(tempDir, "includes");
        assertTrue(includeDir.mkdirs(), "failed to create " + includeDir);
        includeDirectories.add(includeDir);

        Collection<String> cmd = command("java", null, null);
        assertTrue(cmd.contains("-I" + includeDir.getPath()), "cmd=" + cmd);
        assertTrue(cmd.contains("-v" + PROTOC_VERSION), "cmd=" + cmd);
        assertTrue(cmd.contains(new File("demo/Demo.proto").toString()), "cmd=" + cmd);
    }

    @Test
    void missingIncludeDirectoryIsRejected() {
        includeDirectories.add(new File(tempDir, "not-exists"));
        assertThrows(PluginTaskException.class, () -> command("java", null, null));
    }
}
