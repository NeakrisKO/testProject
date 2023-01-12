package ru.itb.web.model.ui.version;

import java.util.ArrayList;

public class VersionInfo {

    private long lastRevision;
    private ArrayList<VersionEntry> entries = new ArrayList<>();

    public ArrayList<VersionEntry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<VersionEntry> entries) {
        this.entries = entries;
    }

    public void add(VersionEntry entry) {
        entries.add(entry);
    }

    public long getLastRevision() {
        return lastRevision;
    }

    public void setLastRevision(long lastRevision) {
        this.lastRevision = lastRevision;
    }
}
