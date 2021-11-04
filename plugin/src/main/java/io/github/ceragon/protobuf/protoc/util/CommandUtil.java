package io.github.ceragon.protobuf.protoc.util;

import io.github.ceragon.PluginContext;
import io.github.ceragon.util.PluginTaskException;
import com.github.os72.protocjar.Protoc;
import com.github.os72.protocjar.ProtocVersion;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class CommandUtil {
    public static String buildProtocExe(String protocCommand, final String protocVersion) throws PluginTaskException {
        if (protocCommand != null) {
            try {
                Protoc.runProtoc(protocCommand, new String[]{"--version"});
                return protocCommand;
            } catch (Exception e) {
                throw new PluginTaskException("the protocCommand is not available", e);
            }
        }
        PluginContext.log().quiet("Protoc version: " + protocVersion);
        try {
            File protocFile = Protoc.extractProtoc(ProtocVersion.getVersion("-v" + protocVersion), false);
            protocCommand = protocFile.getAbsolutePath();
            try {
                // some linuxes don't allow exec in /tmp, try one dummy execution, switch to user home if it fails
                Protoc.runProtoc(protocCommand, new String[]{"--version"});
            } catch (Exception e) {
                File tempRoot = new File(System.getProperty("user.home"));
                protocFile = Protoc.extractProtoc(ProtocVersion.getVersion("-v" + protocVersion), false, tempRoot);
                protocCommand = protocFile.getAbsolutePath();
            }
        } catch (IOException e) {
            throw new PluginTaskException("Error extracting protoc for version " + protocVersion, e);
        }
        return protocCommand;
    }


    public static String getVersion(String protocCommand) {
        if (StringUtils.isEmpty(protocCommand)) {
            return null;
        }
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Protoc.runProtoc(protocCommand, Collections.singletonList("--version"), outStream, System.err);
            String outStr = outStream.toString();
            if (StringUtils.isEmpty(outStr)) {
                return null;
            }
            return outStr.split(" ")[1].trim();
        } catch (Exception e) {
            PluginContext.log().quiet(e.getMessage());
            return null;
        }
    }
}
