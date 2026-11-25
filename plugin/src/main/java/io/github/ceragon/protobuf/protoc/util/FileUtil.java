package io.github.ceragon.protobuf.protoc.util;

import io.github.ceragon.util.PluginTaskException;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

public class FileUtil {
    public static File createTempDir(String name) throws PluginTaskException {
        try {
            File tmpDir = Files.createTempDirectory(name).toFile();
            tmpDir.deleteOnExit();
            return tmpDir;
        } catch (IOException e) {
            throw new PluginTaskException("Error creating temporary directory: " + name, e);
        }
    }

    public static void streamCopy(InputStream in, OutputStream out) throws IOException {
        int read = 0;
        byte[] buf = new byte[4096];
        while ((read = in.read(buf)) > 0) out.write(buf, 0, read);
    }

    public static void deleteOnExitRecursive(File dir) {
        dir.deleteOnExit();
        for (File f : dir.listFiles()) {
            f.deleteOnExit();
            if (f.isDirectory()) deleteOnExitRecursive(f);
        }
    }
    public static boolean nameFilter(List<String> matches, String targetName) {
        if (matches == null || matches.isEmpty()) {
            return true;
        }
        return matches.stream().anyMatch(targetName::matches);
    }

    /**
     * 取 proto 文件名的主名：去掉目录前缀和 .proto 后缀。
     * "ProtoLogin.proto" -> "ProtoLogin"
     * "net/ProtoLogin.proto" -> "ProtoLogin"
     *
     * @param protoFileName proto 文件名，可能带目录前缀
     * @return 去掉目录和扩展名后的文件名
     */
    public static String protoNameOf(String protoFileName) {
        return FilenameUtils.getBaseName(protoFileName);
    }
}
