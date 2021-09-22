package io.github.ceragon.util;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CodeGenTool {
    private CodeGenTool() {
    }

    public static void createCodeByPath(String sourcePath, String sourceName, String desPath, boolean overwrite, Map<String, Object> content) throws IOException {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.INPUT_ENCODING, "UTF-8");
        ve.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, sourcePath);
        ve.setProperty("globbing.resource.loader.class", "StructuredGlobbingResourceLoader");
        ve.init();
        createCode(ve, sourceName, desPath, overwrite, content);
    }

    public static void createCodeByClasspath(String sourceName, String desPath, boolean overwrite, Map<String, Object> content) throws IOException {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.INPUT_ENCODING, "UTF-8");
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        createCode(ve, sourceName, desPath, overwrite, content);
    }

    private static void createCode(VelocityEngine ve, String sourceName, String desPath, boolean overwrite, Map<String, Object> content) throws IOException {
        Template t = ve.getTemplate(sourceName);
        VelocityContext ctx = new VelocityContext();
        for (Map.Entry<String, Object> entry : content.entrySet()) {
            ctx.put(entry.getKey(), entry.getValue());
        }
        StringWriter sw = new StringWriter();

        t.merge(ctx, sw);
        FileOutputStream out = null;
        File file = new File(desPath);
        if (file.exists() && !overwrite) {
            return;
        }

        if (!file.exists()) {
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            file.createNewFile();
        }
        out = new FileOutputStream(file);

        out.write(sw.toString().getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }


}
