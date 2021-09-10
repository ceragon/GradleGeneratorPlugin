package com.ceragon.extension;

import com.github.os72.protocjar.ProtocVersion;
import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFile;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Internal;
import org.gradle.util.internal.ConfigureUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

abstract public class ProtoExtension {
    @Internal("protoc 的版本号")
    public String protocVersion = ProtocVersion.PROTOC_VERSION.mVersion;

    @Internal("proto 文件的目录")
    abstract public ListProperty<RegularFile> getInputDirectory();

    @Internal("输出配置")
    public List<OutputTarget> outputTargets = new ArrayList<>();

    @Inject
    public ProtoExtension(ObjectFactory factory) {

        getInputDirectory().convention();
    }

    public String getProtocVersion() {
        return protocVersion;
    }

    public List<OutputTarget> getOutputTargets() {
        return outputTargets;
    }

    public OutputTarget outputTarget(Action<OutputTarget> action) {
        OutputTarget outputTarget = new OutputTarget();
        action.execute(outputTarget);
        return outputTarget;
    }

    public OutputTarget outputTarget(Closure closure) {
        return ConfigureUtil.configure(closure, new OutputTarget());
    }

}
