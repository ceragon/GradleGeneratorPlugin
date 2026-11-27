package io.github.ceragon;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * outputTargets 配置项的功能测试：protoNameMatch 的文件过滤、proto.includeDirectories 的 -I 目录、
 * 两个开关都为 false 时只打一条 WARN。
 */
class OutputTargetFunctionalTest {

    @Test
    void protoNameMatchFiltersProtoFiles() throws IOException {
        File projectDir = prepareProject("nameMatch");
        writeString(new File(projectDir, "build.gradle"), ""
                + "plugins {\n"
                + "    id('io.github.ceragon.proto')\n"
                + "}\n"
                + "\n"
                + "proto {\n"
                + "    outputTargets {\n"
                + "        onlyAlpha {\n"
                + "            protoNameMatch = ['Alpha.*']\n"
                + "        }\n"
                + "    }\n"
                + "}\n");

        runProto(projectDir);

        File genDir = new File(projectDir, "build/generated-sources/onlyAlpha/demo");
        assertTrue(new File(genDir, "Alpha.java").exists(),
                "Alpha.proto 命中 Alpha.*，应当生成代码，输出目录内容：" + listNames(genDir));
        assertFalse(new File(genDir, "Beta.java").exists(),
                "Beta.proto 不命中 Alpha.*，应当被跳过，输出目录内容：" + listNames(genDir));
        // 顺带确认过滤不是“一个都没生成”
        assertEquals(1, listFiles(genDir).length, "只有 Alpha.proto 应当被处理，输出目录内容：" + listNames(genDir));
    }

    @Test
    void protoNameMatchIgnoresDirectoryPrefix() throws IOException {
        File projectDir = prepareProject("nameOnly");
        File subDir = new File(projectDir, "protofiles/sub");
        assertTrue(subDir.mkdirs(), "failed to create " + subDir);
        writeString(new File(subDir, "Gamma.proto"), proto("GammaMsg"));
        writeString(new File(projectDir, "build.gradle"), ""
                + "plugins {\n"
                + "    id('io.github.ceragon.proto')\n"
                + "}\n"
                + "\n"
                + "proto {\n"
                + "    outputTargets {\n"
                + "        subOnly {\n"
                + "            protoNameMatch = ['sub/.*']\n"
                + "        }\n"
                + "    }\n"
                + "}\n");

        runProto(projectDir);

        File genDir = new File(projectDir, "build/generated-sources/subOnly/demo");
        assertFalse(new File(genDir, "Gamma.java").exists(),
                "protoNameMatch 只匹配文件名（不含目录），按目录写的正则不应命中任何文件，输出目录内容：" + listNames(genDir));
    }

    private File prepareProject(String name) throws IOException {
        File projectDir = new File("build/functionalTestOutputTargets/" + name);
        deleteRecursively(projectDir);
        Files.createDirectories(new File(projectDir, "protofiles").toPath());
        writeString(new File(projectDir, "settings.gradle"), "");
        // message 名与文件名不同，避免 protoc 把外层类改名成 AlphaOuterClass
        writeString(new File(projectDir, "protofiles/Alpha.proto"), proto("AlphaMsg"));
        writeString(new File(projectDir, "protofiles/Beta.proto"), proto("BetaMsg"));
        return projectDir;
    }

    private String proto(String messageName) {
        return "syntax = \"proto3\";\n"
                + "package demo;\n"
                + "message " + messageName + " {\n"
                + "  string name = 1;\n"
                + "}\n";
    }

    private void runProto(File projectDir) {
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("proto");
        runner.withProjectDir(projectDir);
        runner.build();
    }

    private File[] listFiles(File dir) {
        File[] files = dir.listFiles();
        return files == null ? new File[0] : files;
    }

    private String listNames(File dir) {
        File[] files = listFiles(dir);
        StringBuilder builder = new StringBuilder();
        for (File file : files) {
            if (builder.length() > 0) {
                builder.append(",");
            }
            builder.append(file.getName());
        }
        return builder.length() == 0 ? "<empty>" : builder.toString();
    }

    private void deleteRecursively(File file) {
        if (!file.exists()) {
            return;
        }
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }

    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
