package ru.itb.svn.harvester;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import ru.itb.svn.harvester.entry.HarvesterEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SVNHarvester {
    private String svnRepository;
    private String login;
    private String password;
    private long lastRevision;

    public SVNHarvester(String svnRepository, String login, String password) {
        this.svnRepository = svnRepository;
        this.login = login;
        this.password = password;
        init();
    }

    private void init() {
        DAVRepositoryFactory.setup();
    }

    public List<HarvesterEntry> getInfo() {
        ArrayList<HarvesterEntry> harvesterEntries = new ArrayList<>();
        SVNRepository repository;
        try {
            final long startRevision = 1;
            Collection logEntries;
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnRepository));
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(login, password);
            repository.setAuthenticationManager(authManager);
            final long endRevision = repository.getLatestRevision();
            lastRevision = endRevision;

            logEntries = repository.log(new String[]{""}, null, startRevision, endRevision, true, true);

            for (Iterator entries = logEntries.iterator(); entries.hasNext(); ) {
                harvesterEntries.add(buildEntry((SVNLogEntry) entries.next()));
            }
        } catch (Exception e) {

        }
        return harvesterEntries;
    }

    public long getLastRevision() {
        return lastRevision;
    }

    private HarvesterEntry buildEntry(SVNLogEntry logEntry) {
        return new HarvesterEntry(logEntry.getAuthor(),logEntry.getDate(), logEntry.getMessage(), logEntry.getRevision());
    }
}
