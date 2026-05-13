package com.jyjun.projectbp.application.patch.model.output;

import java.util.List;

public record LoadGameBundleListOutput(List<PlatformBundleEntry> platforms) {

    public record PlatformBundleEntry(String platform, List<String> filenames) {
    }
}
