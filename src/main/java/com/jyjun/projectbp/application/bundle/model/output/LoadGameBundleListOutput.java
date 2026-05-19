package com.jyjun.projectbp.application.bundle.model.output;

import java.util.List;

public record LoadGameBundleListOutput(List<PlatformBundleEntry> platforms) {

    public record PlatformBundleEntry(String platform, List<String> filenames) {
    }
}
