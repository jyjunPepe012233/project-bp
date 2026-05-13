package com.jyjun.projectbp.domain.patch.enums;

public enum PatchPlatform {
    ANDROID("Android"),
    IOS("iOS"),
    STANDALONE_OSX("StandaloneOSX");

    private final String formattedName;

    PatchPlatform(String formattedName) {
        this.formattedName = formattedName;
    }

    public String getFormattedName() {
        return formattedName;
    }
}
