package Core;

import View.Panel.FileChoosePanel;
import View.Window.Window;
import View.Window.WindowTitleEnum;

public class Application {

    public static void main(String args[]) {
        Window window = new Window(WindowTitleEnum.CHOOSE_TRAINING_DATA.getName());
        window.add(new FileChoosePanel(ContextEnum.TRAINING, window));
        window.pack();
        window.setVisible(true);
    }
}
