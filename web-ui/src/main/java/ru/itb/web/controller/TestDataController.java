package ru.itb.web.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.itb.testautomation.core.dataset.impl.DataSetInfo;
import ru.itb.testautomation.core.manager.CoreObjectManager;
import ru.itb.testautomation.core.manager.ExcelDataSetListManager;
import ru.itb.testautomation.core.manager.SystemPreferenceManager;
import ru.itb.web.model.ui.UIDataSet;
import ru.itb.web.util.UIObjectConverter;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/test_data")
public class TestDataController {
    private static final Logger LOGGER = LogManager.getLogger(TestDataController.class);

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public void upload(@RequestParam("files") final MultipartFile[] files)
    {
        if (files.length!= 0) {
            try {
                for (MultipartFile multipartFile : files) {
                    String fullPath = String.format("%s/%s",SystemPreferenceManager.getInstance().getPreference("dataset_path"), multipartFile.getOriginalFilename().toLowerCase());
                    File destFile = new File(fullPath);
                    multipartFile.transferTo(destFile);
                    UIDataSet uiDataSet = new UIDataSet();
                    uiDataSet.setFileName(multipartFile.getOriginalFilename().toLowerCase());
                    uiDataSet.setFileSize(multipartFile.getSize());
                    addDataSet(uiDataSet);
                }
            } catch (IOException exc) {
                LOGGER.error("Can't execute IO Operation",exc);
            }
        }
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public List<UIDataSet> getList()
    {
        return UIObjectConverter.getInstance().convertDataSetInfoList(CoreObjectManager.getInstance().getManager().getDataSetList(false));
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public void update(@RequestBody UIDataSet uiDataSet)
    {
        CoreObjectManager.getInstance().getManager().updateDataSet(UIObjectConverter.getInstance().convertDataSetInfo(uiDataSet));
    }

    @RequestMapping(value = "remove/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void remove(@PathVariable("id") Integer id) {
        CoreObjectManager.getInstance().getManager().removeDataSet(id);
        reloadDataSetManger();
    }

    private void addDataSet(UIDataSet uiDataSet) {
        DataSetInfo dataSetInfo = new DataSetInfo();
        dataSetInfo.setName(uiDataSet.getFileName());
        dataSetInfo.setDescription(uiDataSet.getDescription());
        dataSetInfo.setFileSize(uiDataSet.getFileSize());
        DataSetInfo prevVersion = checkDataSet(uiDataSet.getFileName());
        if (prevVersion != null) {
            prevVersion.setFileSize(uiDataSet.getFileSize());
            updateExistDataSet(prevVersion);
        } else
        CoreObjectManager.getInstance().getManager().addDataSet(dataSetInfo);
        reloadDataSetManger();
    }

    private DataSetInfo checkDataSet(String name) {
        DataSetInfo dataSetInfo = CoreObjectManager.getInstance().getManager().getDataSetByName(name.toLowerCase());
        if (dataSetInfo!= null && dataSetInfo.getName().equalsIgnoreCase(name)) {
            return dataSetInfo;
        }
        return null;
    }

    private void updateExistDataSet(DataSetInfo dataSetInfo) {
        CoreObjectManager.getInstance().getManager().addDataSet(dataSetInfo);
        reloadDataSetManger();
    }

    private void reloadDataSetManger() {
        try {
            ExcelDataSetListManager.getInstance().clear();
            ExcelDataSetListManager.getInstance().loadDataSet(SystemPreferenceManager.getInstance().getPreference("dataset_path"));
        } catch (IOException exc) {
            LOGGER.error("Can't reload test data",exc);
        }
    }
}
