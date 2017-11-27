package Extraction;

import lombok.Getter;

@Getter
public enum FeatureNameEnum {

    DEFAULT("DEFAULT"),
    BRIGHTNESS("BRIGHTNESS");

    private String name;

    FeatureNameEnum(String name) {
        this.name = name;
    }
}
