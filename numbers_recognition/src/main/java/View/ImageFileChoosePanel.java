package View;

import Core.ContextEnum;

public class ImageFileChoosePanel extends FileChoosePanel {

    public ImageFileChoosePanel(ContextEnum context, Window window) {
        super(context, window);
    }

    @Override
    protected void initFileChooser() {
        super.initFileChooser();
        fileChooser.addChoosableFileFilter(new ImageFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileView(new ImageFileView());
        fileChooser.setAccessory(new ImagePreview(fileChooser));
        fileChooser.setMultiSelectionEnabled(true);
    }
}
