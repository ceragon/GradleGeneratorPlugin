package com.ceragon.protobuf.protoc.util;

import com.ceragon.PluginContext;
import com.ceragon.protobuf.constant.ContextKey;
import com.ceragon.util.PluginTaskException;
import com.github.os72.protocjar.Protoc;
import com.github.os72.protocjar.ProtocVersion;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IncludeUtil {
    public static List<File> buildIncludePath(final boolean includeStdTypes, final File[] includeDirectories) throws PluginTaskException {
        Logger log = PluginContext.log();
        final String protocVersion = PluginContext.buildContext().getValue(ContextKey.PROTOC_VERSION);
        List<File> includeDirectoryList;
        if (includeDirectories != null) {
            includeDirectoryList = new ArrayList<>(Arrays.asList(includeDirectories));
        } else {
            includeDirectoryList = new ArrayList<>();
        }
        if (!includeStdTypes) {
            return includeDirectoryList;
        }

        File tmpDir = FileUtil.createTempDir("protocjar");
        try {
            File extraTypeDir = new File(tmpDir, "include");
            extraTypeDir.mkdir();
            log.info("Additional include types: " + extraTypeDir);
            includeDirectoryList.add(extraTypeDir);
            if (includeStdTypes) {
                Protoc.extractStdTypes(ProtocVersion.getVersion("-v" + protocVersion), tmpDir); // yes, tmpDir
            }
//            if (includeMavenTypes != IncludeMavenType.none) {
//                extractProtosFromDependencies(extraTypeDir, includeMavenTypes == IncludeMavenType.transitive);
//            }
            FileUtil.deleteOnExitRecursive(extraTypeDir);
        } catch (IOException e) {
            throw new PluginTaskException("Error extracting additional include types", e);
        }
        return includeDirectoryList;
    }

//    private static void extractProtosFromDependencies(File dir, boolean transitive) throws IOException {
//        Logger log = PluginContext.buildContext().getValue(ContextKey.LOG);
//        final String extension = PluginContext.buildContext().getValue(ContextKey.PROTO_EXTENSION);
//        Project project = PluginContext.project();
//        DependencyHandler dependencyHandler = project.getDependencies();
//        for (Artifact artifact : getArtifactsForProtoExtraction(project, transitive)) {
//            if (artifact.getFile() == null) continue;
//            log.debug("  Scanning artifact: " + artifact.getFile());
//            InputStream is = null;
//            try {
//                if (artifact.getFile().isDirectory()) {
//                    for (File f : listFilesRecursively(artifact.getFile(), extension, new ArrayList<File>())) {
//                        is = new FileInputStream(f);
//                        String name = f.getAbsolutePath().replace(artifact.getFile().getAbsolutePath(), "");
//                        if (name.startsWith("/")) name = name.substring(1);
//                        writeProtoFile(context, dir, is, name);
//                        is.close();
//                    }
//                } else {
//                    ZipInputStream zis = new ZipInputStream(new FileInputStream(artifact.getFile()));
//                    is = zis;
//                    ZipEntry ze;
//                    while ((ze = zis.getNextEntry()) != null) {
//                        if (ze.isDirectory() || !ze.getName().toLowerCase().endsWith(extension)) continue;
//                        writeProtoFile(context, dir, zis, ze.getName());
//                        zis.closeEntry();
//                    }
//                }
//            } catch (IOException e) {
//                log.info("  Error scanning artifact: " + artifact.getFile() + ": " + e);
//            } finally {
//                if (is != null) is.close();
//            }
//        }
//    }

//    private static Set<Artifact> getArtifactsForProtoExtraction(MavenProject project, boolean transitive) {
//        if (transitive) return project.getArtifacts();
//        return project.getDependencyArtifacts();
//    }


//    private static List<File> listFilesRecursively(File directory, String ext, List<File> list) {
//        for (File f : directory.listFiles()) {
//            if (f.isFile() && f.canRead() && f.getName().toLowerCase().endsWith(ext)) list.add(f);
//            else if (f.isDirectory() && f.canExecute()) listFilesRecursively(f, ext, list);
//        }
//        return list;
//    }

//    private static void writeProtoFile(File dir, InputStream zis, String name) throws IOException {
//        PluginContext.log().info("    " + name);
//        File protoOut = new File(dir, name);
//        protoOut.getParentFile().mkdirs();
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(protoOut);
//            FileUtil.streamCopy(zis, fos);
//        } finally {
//            if (fos != null) fos.close();
//        }
//    }


}
