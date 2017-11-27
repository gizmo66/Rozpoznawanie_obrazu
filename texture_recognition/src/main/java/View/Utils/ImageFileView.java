package View.Utils;

import File.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileView;
import java.io.File;
import java.util.Optional;


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
