package View;

import javax.swing.*;
import java.io.File;

public class ImageRecognitionPanel extends JPanel {

    public ImageRecognitionPanel(File image, Window window) {
        setVisible(true);
        window.setTitle("Recognizing image");
        //TODO: podgląd obrazka, przycisk "recognize", wynik rozpoznawania (może być zwykły tekst np. "3" ).
    }
}
