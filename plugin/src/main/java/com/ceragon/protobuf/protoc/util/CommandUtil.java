package com.ceragon.protobuf.protoc.util;

import com.ceragon.PluginContext;
import com.ceragon.util.PluginTaskException;
import com.github.os72.protocjar.Protoc;
import com.github.os72.protocjar.ProtocVersion;

import java.io.File;
import java.io.IOException;

public class CommandUtil {
    public static String buildProtocExe(final String protocVersion) throws PluginTaskException {
        PluginContext.log().info("Protoc version: " + protocVersion);
        String protocCommand;
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



}
