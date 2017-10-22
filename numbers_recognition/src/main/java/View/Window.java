package View;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    public Window(String title) {
        super(title);
        setLocation(560, 340);
        setSize(500, 50);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
