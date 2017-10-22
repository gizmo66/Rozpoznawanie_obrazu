package View;

public class ImageFileChoosePanel extends FileChoosePanel {

    @Override
    protected void initFileChooser() {
        super.initFileChooser();
        fileChooser.addChoosableFileFilter(new ImageFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileView(new ImageFileView());
        fileChooser.setAccessory(new ImagePreview(fileChooser));
    }
}
