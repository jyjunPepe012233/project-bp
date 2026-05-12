package com.jyjun.projectbp.application.patch.outbound;

import com.jyjun.projectbp.domain.patch.model.Patch;

public interface PatchRepositoryPort {

    Patch save(Patch patch);
}
