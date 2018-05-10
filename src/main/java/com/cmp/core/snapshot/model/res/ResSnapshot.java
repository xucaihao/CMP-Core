package com.cmp.core.snapshot.model.res;

public class ResSnapshot {

    private ResSnapshotInfo snapshot;

    public ResSnapshot() {
    }

    public ResSnapshot(ResSnapshotInfo snapshot) {
        this.snapshot = snapshot;
    }

    public ResSnapshotInfo getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(ResSnapshotInfo snapshot) {
        this.snapshot = snapshot;
    }
}
