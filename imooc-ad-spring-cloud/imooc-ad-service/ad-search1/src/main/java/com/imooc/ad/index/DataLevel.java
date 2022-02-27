package com.imooc.ad.index;

import lombok.Getter;

@Getter
public enum DataLevel {

    LEVEL_2("2", "level 2"),
    LEVEL_3("3", "level 3"),
    LEVEL_4("3", "level 4");

    private String level;

    private String desc;

    DataLevel(String level, String desc) {
        this.level = level;
        this.desc = desc;
    }
}
