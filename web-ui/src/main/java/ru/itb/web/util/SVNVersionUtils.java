package ru.itb.web.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.itb.svn.harvester.SVNHarvester;
import ru.itb.svn.harvester.entry.HarvesterEntry;
import ru.itb.web.model.ui.version.VersionEntry;
import ru.itb.web.model.ui.version.VersionInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SVNVersionUtils {
    private static final Logger LOGGER = LogManager.getLogger(SVNVersionUtils.class);
    private static volatile SVNVersionUtils instance;
    private SimpleDateFormat dateFormat;
    private static final String DATE_FORMAT = "dd.MM.yy HH:mm:ss";
    private VersionInfo versionInfo;

    private SVNVersionUtils() {
        dateFormat = new SimpleDateFormat(DATE_FORMAT);
    }

    public static SVNVersionUtils getInstance() {
        SVNVersionUtils localInstance = instance;
        if (localInstance == null) {
            synchronized (UIObjectConverter.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SVNVersionUtils();
                }
            }
        }
        return localInstance;
    }

    public void collectInfo(String svnRepository, String login, String password) {
        SVNHarvester svnHarvester = new SVNHarvester(svnRepository,login, password);
        List<HarvesterEntry> listEntry = svnHarvester.getInfo();
        versionInfo = new VersionInfo();
        versionInfo.setLastRevision(svnHarvester.getLastRevision());
        for(HarvesterEntry harvesterEntry : listEntry) {
            try {
                versionInfo.getEntries().addAll(buildVersionInfoEntry(harvesterEntry));
            } catch (ParseException exc) {
                LOGGER.warn(String.format("Commit:%d by %s not for issue",harvesterEntry.getRevision(), harvesterEntry.getAuthor()));
            }
        }
    }



    private List<VersionEntry> buildVersionInfoEntry(HarvesterEntry harvesterEntry) throws ParseException {
        ArrayList<VersionEntry> versionEntries = new ArrayList<>();

        if (harvesterEntry.getComments()!= null && !harvesterEntry.getComments().isEmpty()) {
            String[] data = harvesterEntry.getComments().split("\\r?\\n");
            JSONParser jsonParser = new JSONParser();
            for (String comment : data) {
                VersionEntry versionEntry = new VersionEntry();
                JSONObject json = (JSONObject) jsonParser.parse(comment);
                Iterator it = json.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next().toString();
                    if ("message".equalsIgnoreCase(key)) {
                        versionEntry.setComment(json.get(key).toString());
                    } else if ("url".equalsIgnoreCase(key)) {
                        versionEntry.setUrl(json.get(key).toString());
                    }
                }
                versionEntry.setAuthor(harvesterEntry.getAuthor());
                versionEntry.setDate(dateFormat.format(harvesterEntry.getDate()));
                versionEntry.setRevision(harvesterEntry.getRevision());
                versionEntries.add(versionEntry);
            }
        }

        return versionEntries;
    }

    public VersionInfo getVersionInfo() {
       return versionInfo;
    }

}
