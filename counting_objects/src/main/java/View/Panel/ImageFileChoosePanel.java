package View.Panel;

import View.Utils.ImageFileView;
import View.Utils.ImageFilter;
import View.Utils.ImagePreview;
import View.Window.Window;

public class ImageFileChoosePanel extends FileChoosePanel {

    public ImageFileChoosePanel(Window window) {
        super(window);
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
