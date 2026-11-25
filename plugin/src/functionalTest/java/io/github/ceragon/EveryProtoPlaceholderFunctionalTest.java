package io.github.ceragon;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * everyProtoBuilds 的 targetFile 占位符 ${ProtoName} 与 msgNameMatch 过滤的功能测试。
 */
class EveryProtoPlaceholderFunctionalTest {

    private static final String TEMPLATE = "// from $proto.name\n";

    @Test
    void placeholderGeneratesOneFilePerProto() throws IOException {
        File projectDir = prepareProject("placeholder");
        writeString(new File(projectDir, "build.gradle"), ""
                + "plugins {\n"
                + "    id('io.github.ceragon.proto')\n"
                + "}\n"
                + "\n"
                + "proto {\n"
                + "    everyProtoBuilds {\n"
                + "        perProto {\n"
                + "            vmFile = 'ProtoRegister.vm'\n"
                + "            targetFile = baseDir('build/gen/${ProtoName}Register.txt')\n"
                + "        }\n"
                + "    }\n"
                + "}\n");

        runProto(projectDir);

        File alpha = new File(projectDir, "build/gen/AlphaRegister.txt");
        File beta = new File(projectDir, "build/gen/BetaRegister.txt");
        assertTrue(alpha.exists(), "expected build/gen/AlphaRegister.txt to be generated");
        assertTrue(beta.exists(), "expected build/gen/BetaRegister.txt to be generated");
        assertTrue(read(alpha).contains("Alpha.proto"), "AlphaRegister.txt should be rendered from Alpha.proto but was: " + read(alpha));
        assertTrue(read(beta).contains("Beta.proto"), "BetaRegister.txt should be rendered from Beta.proto but was: " + read(beta));
    }

    @Test
    void withoutPlaceholderStillGeneratesSingleFile() throws IOException {
        File projectDir = prepareProject("compat");
        writeString(new File(projectDir, "build.gradle"), ""
                + "plugins {\n"
                + "    id('io.github.ceragon.proto')\n"
                + "}\n"
                + "\n"
                + "proto {\n"
                + "    everyProtoBuilds {\n"
                + "        single {\n"
                + "            vmFile = 'ProtoRegister.vm'\n"
                + "            targetFile = baseDir('build/gen/Single.txt')\n"
                + "        }\n"
                + "    }\n"
                + "}\n");

        runProto(projectDir);

        File genDir = new File(projectDir, "build/gen");
        File single = new File(genDir, "Single.txt");
        assertTrue(single.exists(), "expected build/gen/Single.txt to be generated");
        String[] names = genDir.list();
        assertEquals(1, names == null ? 0 : names.length, "without the placeholder only a single file should be produced");
    }

    @Test
    void msgNameMatchSkipsProtosWithoutMatchingMessage() throws IOException {
        File projectDir = prepareProject("msgname");
        writeString(new File(projectDir, "protofiles/Beta.proto"), ""
                + "syntax = \"proto3\";\n"
                + "package demo;\n"
                + "message CL_Beta {\n"
                + "  string name = 1;\n"
                + "}\n");
        writeString(new File(projectDir, "build.gradle"), ""
                + "plugins {\n"
                + "    id('io.github.ceragon.proto')\n"
                + "}\n"
                + "\n"
                + "proto {\n"
                + "    everyProtoBuilds {\n"
                + "        clOnly {\n"
                + "            vmFile = 'ProtoRegister.vm'\n"
                + "            targetFile = baseDir('build/gen/${ProtoName}Register.txt')\n"
                + "            msgNameMatch = ['CL_.*']\n"
                + "        }\n"
                + "    }\n"
                + "}\n");

        runProto(projectDir);

        assertFalse(new File(projectDir, "build/gen/AlphaRegister.txt").exists(),
                "Alpha.proto has no message matching CL_.* so it must be skipped");
        assertTrue(new File(projectDir, "build/gen/BetaRegister.txt").exists(),
                "Beta.proto has CL_Beta so it must be generated");
    }

    @Test
    void placeholderGeneratesEveryFileEvenWithOverwriteDisabled() throws IOException {
        File projectDir = prepareProject("nooverwrite");
        writeString(new File(projectDir, "build.gradle"), ""
                + "plugins {\n"
                + "    id('io.github.ceragon.proto')\n"
                + "}\n"
                + "\n"
                + "proto {\n"
                + "    everyProtoBuilds {\n"
                + "        perProto {\n"
                + "            vmFile = 'ProtoRegister.vm'\n"
                + "            targetFile = baseDir('build/gen/${ProtoName}Register.txt')\n"
                + "            overwrite = false\n"
                + "        }\n"
                + "    }\n"
                + "}\n");

        runProto(projectDir);

        // overwrite=false 以前会让第二个 proto 直接跳过，带上占位符后每个 proto 各写各的文件
        assertTrue(new File(projectDir, "build/gen/AlphaRegister.txt").exists(),
                "expected build/gen/AlphaRegister.txt to be generated with overwrite = false");
        assertTrue(new File(projectDir, "build/gen/BetaRegister.txt").exists(),
                "expected build/gen/BetaRegister.txt to be generated with overwrite = false");
    }

    private File prepareProject(String name) throws IOException {
        File projectDir = new File("build/functionalTestEveryProto/" + name);
        deleteRecursively(projectDir);
        Files.createDirectories(new File(projectDir, "protofiles").toPath());
        Files.createDirectories(new File(projectDir, "template").toPath());
        writeString(new File(projectDir, "settings.gradle"), "");
        writeString(new File(projectDir, "protofiles/Alpha.proto"),
                "syntax = \"proto3\";\npackage demo;\nmessage Alpha {\n  string name = 1;\n}\n");
        writeString(new File(projectDir, "protofiles/Beta.proto"),
                "syntax = \"proto3\";\npackage demo;\nmessage Beta {\n  string name = 1;\n}\n");
        writeString(new File(projectDir, "template/ProtoRegister.vm"), TEMPLATE);
        return projectDir;
    }

    private void runProto(File projectDir) {
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("proto");
        runner.withProjectDir(projectDir);
        runner.build();
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

    private String read(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
