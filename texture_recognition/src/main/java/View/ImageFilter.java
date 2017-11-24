package View;

import File.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageFilter extends FileFilter {

    public boolean accept(File file) {
        return file.isDirectory() || FileUtils.getImageType(file).isPresent();
    }

    public String getDescription() {
        List<String> extensions = new ArrayList<>();
        Arrays.asList(ImageTypeEnum.values()).forEach(it -> extensions.addAll(it.getExtensions()));
        return StringUtils.join(extensions, ", ");
    }
}
