package View;

import Core.FileUtils;

import java.io.File;
import java.util.Optional;
import javax.swing.*;
import javax.swing.filechooser.*;


public class ImageFileView extends FileView {

    public String getName(File f) {
        return null;
    }

    public String getDescription(File f) {
        return null;
    }

    public Boolean isTraversable(File f) {
        return null;
    }

    public String getTypeDescription(File file) {
        Optional<ImageTypeEnum> imageTypeEnum = FileUtils.getImageType(file);
        return imageTypeEnum.map(ImageTypeEnum::getDescription).orElse(null);
    }

    public Icon getIcon(File file) {
        Optional<ImageTypeEnum> imageTypeEnum = FileUtils.getImageType(file);
        return imageTypeEnum.map(ImageTypeEnum::getIcon).orElse(null);
    }
}
