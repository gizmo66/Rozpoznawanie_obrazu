package View.Window;

import Core.ImageRecognizer;

import javax.swing.*;

public class Window extends JFrame {

    public ImageRecognizer imageRecognizer;

    public Window(String title, ImageRecognizer imageRecognizer) {
        super(title);
        this.imageRecognizer = imageRecognizer;
        setLocation(560, 340);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
