package com.cmp.core.snapshot.model.res;

import java.util.List;

public class ResSnapshots {

    private List<ResSnapshotInfo> snapshots;

    public ResSnapshots() {
    }

    public ResSnapshots(List<ResSnapshotInfo> snapshots) {
        this.snapshots = snapshots;
    }

    public List<ResSnapshotInfo> getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(List<ResSnapshotInfo> snapshots) {
        this.snapshots = snapshots;
    }
}
