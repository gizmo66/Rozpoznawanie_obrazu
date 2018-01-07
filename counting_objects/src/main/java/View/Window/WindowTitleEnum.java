package View.Window;

import lombok.Getter;

@Getter
public enum WindowTitleEnum {

    CHOOSE_IMAGE_FOR_COUNTING("Choose image for counting objects"),
    COUNTING_OBJECTS("Counting objects");

    private String name;

    WindowTitleEnum(String name) {
        this.name = name;
    }
}
