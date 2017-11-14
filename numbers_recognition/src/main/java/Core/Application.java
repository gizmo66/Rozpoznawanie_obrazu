package Core;

import View.*;

public class Application {

    public static void main(String args[]) {
        Window window = new Window("Choose training data");
        window.add(new FileChoosePanel(ContextEnum.TRAINING, window));
        window.pack();
        window.setVisible(true);
    }
}
