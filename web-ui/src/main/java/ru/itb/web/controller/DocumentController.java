package ru.itb.web.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import ru.itb.testautomation.core.manager.CoreObjectManager;
import ru.itb.testautomation.core.manager.SystemPreferenceManager;
import ru.itb.web.model.ui.UIDocument;
import ru.itb.web.util.UIObjectConverter;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping(value = "/document")
public class DocumentController {

    private static final Logger LOGGER = LogManager.getLogger(DocumentController.class);
    private static final String DATE_FORMAt = "dd.MM.yy HH:mm:ss";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAt);


    @RequestMapping(value = "getById/{id}", method = RequestMethod.GET)
    @ResponseBody
    public UIDocument getById(@PathVariable Integer id) {
        return UIObjectConverter.getInstance().convertDocument(CoreObjectManager.getInstance().getManager().getDocument(id));
    }

    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse response, @PathVariable Integer id) throws IOException {

        UIDocument document = UIObjectConverter.getInstance().convertDocument(CoreObjectManager.getInstance().getManager().getDocument(id));

        File file = new File(document.getPath());

        if (testForExist(response, file)) return;

        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            LOGGER.info("mimetype is not detectable, will take default");
            mimeType = "application/octet-stream";
        }

        LOGGER.info("mimetype : " + mimeType);

        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
        response.setContentLength((int) file.length());

        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }


    @RequestMapping(value = "/downloadDataSet/{name}", method = RequestMethod.GET)
    public void downloadDataSet(HttpServletResponse response, @PathVariable String name) throws IOException {

        File file = new File(SystemPreferenceManager.getInstance().getPreference("dataset_path") + "/" + name);

        if (testForExist(response, file)) return;

        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            LOGGER.info("mimetype is not detectable, will take default");
            mimeType = "application/octet-stream";
        }

        LOGGER.info("mimetype : " + mimeType);

        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
        response.setContentLength((int) file.length());

        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }

    private boolean testForExist(HttpServletResponse response, File file) throws IOException {
        if (!file.exists()) {
            String errorMessage = "Sorry. The file you are looking for does not exist";
            LOGGER.error(errorMessage);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
            return true;
        }
        return false;
    }
}
