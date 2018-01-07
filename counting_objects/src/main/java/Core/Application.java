package Core;

import View.Panel.ImageFileChoosePanel;
import View.Window.Window;
import View.Window.WindowTitleEnum;

public class Application {

    public static void main(String args[]) {
        Window window = new Window(WindowTitleEnum.CHOOSE_IMAGE_FOR_COUNTING.getName());
        window.add(new ImageFileChoosePanel(window));
        window.pack();
        window.setVisible(true);
    }
}
