package View;

import Core.ContextEnum;
import View.Utils.ImageFileView;
import View.Utils.ImageFilter;
import View.Utils.ImagePreview;

public class ImageFileChoosePanel extends FileChoosePanel {

    ImageFileChoosePanel(ContextEnum context, Window window) {
        super(context, window);
    }

    @Override
    protected void initFileChooser() {
        super.initFileChooser();
        fileChooser.addChoosableFileFilter(new ImageFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileView(new ImageFileView());
        fileChooser.setAccessory(new ImagePreview(fileChooser));
    }
}
