package View.Window;

import lombok.Getter;

@Getter
public enum WindowTitleEnum {

    CHOOSE_TRAINING_DATA("Choose training data"),
    RECOGNIZING_IMAGE("Recognizing image"),
    CHOOSE_IMAGE_TO_RECOGNIZE("Choose image to recognize"),
    TRAINING_DATA("Training data"),
    FEATURES_VECTOR_EXTRACTING("Features vector extracting"),
    TEXTURES_RECOGNIZING("Recognizing textures");

    private String name;

    WindowTitleEnum(String name) {
        this.name = name;
    }
}
