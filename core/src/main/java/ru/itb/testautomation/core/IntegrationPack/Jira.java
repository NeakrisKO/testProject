package ru.itb.testautomation.core.IntegrationPack;

import org.apache.http.HttpEntity;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import ru.itb.testautomation.core.manager.SystemPreferenceManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;



/**
 * Jira класс
 */

public class Jira {

    private String issueKey;
    private String restapijira = "/jira/rest/api/latest/issue/";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy 'в' HH:mm:ss");

    private String screenshotpath = "SCREENSHOT_PATH";
    private String log = "LOG";
    private String pass = "PASS";

    private String screenshot_path = SystemPreferenceManager.getInstance().getPreference(screenshotpath);
    private String dblog = SystemPreferenceManager.getInstance().getPreference(log);
    private String dbpass = SystemPreferenceManager.getInstance().getPreference(pass);

    private Date sCurrentDate = java.util.Calendar.getInstance().getTime();
    private String date = dateFormat.format(sCurrentDate);

    private byte[] decodedArr1 = Base64.getUrlDecoder().decode(dblog);
    private byte[] decodedArr2 = Base64.getUrlDecoder().decode(dbpass);


    public Jira() {

    }

    /**
     * Метод для отправки ошибок в Jira
     */

    public boolean createIssue(String errtxt, String stepname, int stepn, String projectCode, String bank_path, String assigneeUser) {
        try {
            String log = new String(decodedArr1, "UTF-8");
            String pass = new String(decodedArr2, "UTF-8");

            String stepnumber = Integer.toString(stepn);
            String theme = date + " " + stepname + " " + stepnumber;
            String assigneeJson;
            if (!assigneeUser.equals("")) {
                assigneeJson = "\"assignee\":{\"name\": \"" + assigneeUser + "\"},";
            } else {
                assigneeJson = "";
            }
            if (theme.length() > 230)        // Тема не должна превышать 255 символов
            {
                theme = theme.substring(0, 230);
            }

            String input = "{\"fields\":{\"project\":{\"key\": \"" + projectCode + "\"},\"summary\": \"AT Selenium "
                    + theme.replaceAll("\"", "")
                    + "\",\"priority\": {\"id\": \"3\"},"
                    + assigneeJson
                    + "\"description\": \"В ходе выполнения автоматизированного тестирования "
                    + date
                    + " в ходе выполнения теста <"
                    + stepname.replaceAll("\"", "")
                    + ">, под номером <"
                    + stepnumber.replaceAll("\"", "")
                    + "> произошла ошибка: <"
                    + errtxt.replaceAll("\"", "")
                    + ">;\",\"issuetype\":{\"name\": \"Bug\"}}}";


//            java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
//            java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);

//            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
//            System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
//            System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
//            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
//            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");

            CloseableHttpClient client = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(bank_path + restapijira);
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(log, pass);
            StringEntity body = new StringEntity(input, "UTF-8");

            httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Accept", "application/json");
            httpPost.setEntity(body);


            CloseableHttpResponse response = client.execute(httpPost);
            System.out.println("Status:" + response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");

            JSONObject jsonObj = new JSONObject(responseString);
            issueKey = jsonObj.getString("key");
            client.close();
            JSONObject a;
            return true;

        } catch (Exception e) {
            System.out.println("Ошибка добавления тикета " + e);
            return false;
        }
    }

    /**
     * Добавление файла
     */

    public boolean addAttachmentToIssue(String bank_path, String pathpic) {

        try {
            String log = new String(decodedArr1, "UTF-8");
            String pass = new String(decodedArr2, "UTF-8");

            File fileUpload = new File(screenshot_path + pathpic);

            CloseableHttpClient client = HttpClients.createDefault();

            HttpPost httppost = new HttpPost(bank_path + restapijira + issueKey + "/attachments");
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(log, pass);
            httppost.addHeader("X-Atlassian-Token", "nocheck");
            httppost.addHeader(new BasicScheme().authenticate(creds, httppost, null));

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("file", new FileBody(fileUpload));

            httppost.setEntity(entity);

            CloseableHttpResponse response = client.execute(httppost);
            System.out.println("Status:" + response.getStatusLine().getStatusCode());
            client.close();
            return true;

        } catch (Exception e) {
            System.out.println("Ошибка добавления картинки " + e);
            return false;
        }
    }


    public boolean addwatcher(String bank_path, String watcher) {
        String user = "\""+watcher+"\"";

        try {
            String log = new String(decodedArr1, "UTF-8");
            String pass = new String(decodedArr2, "UTF-8");

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(bank_path + restapijira + issueKey + "/watchers");
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(log, pass);
            httppost.addHeader("Content-Type", "application/json");
            httppost.addHeader("Accept", "application/json");
            httppost.addHeader(new BasicScheme().authenticate(creds, httppost, null));
            StringEntity body = new StringEntity(user, "UTF-8");

            httppost.setEntity(body);
            CloseableHttpResponse response = client.execute(httppost);
            System.out.println("Status:" + response.getStatusLine().getStatusCode());
            client.close();
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка добавления пользователя " + e);
            return false;
        }
    }
}
