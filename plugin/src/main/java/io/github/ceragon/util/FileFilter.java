package io.github.ceragon.util;

import lombok.AllArgsConstructor;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;

@AllArgsConstructor
public class FileFilter implements IOFileFilter {
    String extension;

    public boolean accept(File dir, String name) {
        return name.endsWith(extension);
    }

    public boolean accept(File file) {
        return file.getName().endsWith(extension);
    }
}
