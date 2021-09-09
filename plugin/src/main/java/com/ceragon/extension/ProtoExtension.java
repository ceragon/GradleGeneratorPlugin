package com.ceragon.extension;

import com.github.os72.protocjar.ProtocVersion;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.Internal;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

abstract public class ProtoExtension {
    @Internal("protoc 的版本号")
    abstract public Property<String> getProtocVersion();

    @Internal("proto 文件的目录")
    abstract public RegularFileProperty getInputDirectory();

    @Inject
    public ProtoExtension(ObjectFactory objects, ProviderFactory providers) {
        getProtocVersion().convention(ProtocVersion.PROTOC_VERSION.mVersion);
    }
}
