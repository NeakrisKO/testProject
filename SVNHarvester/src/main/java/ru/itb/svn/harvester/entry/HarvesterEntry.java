package ru.itb.svn.harvester.entry;

import java.util.Date;

public class HarvesterEntry {
    private String author;
    private Date date;
    private String comments;
    private long revision;

    public HarvesterEntry(String author, Date date, String comments, long revision) {
        this.author = author;
        this.date = date;
        this.comments = comments;
        this.revision = revision;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public String getComments() {
        return comments;
    }

    public long getRevision() {
        return revision;
    }
}
