package ru.itb.testautomation.core.manager;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import ru.itb.testautomation.core.dataset.impl.ExcelDataSet;
import ru.itb.testautomation.core.dataset.impl.ExcelDataSetList;
import ru.itb.testautomation.core.dataset.impl.Group;
import ru.itb.testautomation.core.dataset.intf.DataSetList;
import ru.itb.testautomation.core.dataset.util.ExcelDSUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class ExcelDataSetListManager {
    private static final Logger LOGGER = LogManager.getLogger(ExcelDataSetListManager.class);
    private final String PARAMETER_COLUMN = "Parameter";
    private final String ENTITY_COLUMN = "Entity";
    private Map<String,DataSetList> storage = Maps.newLinkedHashMapWithExpectedSize(30);

    private static volatile ExcelDataSetListManager instance;

    private ExcelDataSetListManager() {
    }

    public static ExcelDataSetListManager getInstance() {
        ExcelDataSetListManager localInstance = instance;
        if (localInstance == null) {
            synchronized (ExcelDataSetListManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ExcelDataSetListManager();
                }
            }
        }
        return localInstance;
    }

    public Map<String,DataSetList> get() {
        return storage;
    }

    public void clear(){
        storage.clear();
    }

    public void loadDataSet(String path) throws IOException {
        storage.clear();
        List<File> files = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
        for (File file : files) {
            readDataSet(file);
        }
    }

    private void readDataSet(File file) throws IOException {
        Map<String, DataSetList> excelDataSetMap;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            excelDataSetMap = readDataSets(file, workbook);
            readVariables(file, workbook, excelDataSetMap);
            workbook.close();
            if (excelDataSetMap!= null) {
                for (Map.Entry<String, DataSetList> entry : excelDataSetMap.entrySet()) {
                    storage.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (IOException | InvalidFormatException exc) {
            LOGGER.error("Error while reading " + file.getName() + " data set",exc);
        } finally {
            if (fileInputStream != null)
                fileInputStream.close();
        }
    }

    @Nullable
    private Map<String, DataSetList> readDataSets(@Nonnull File file, @Nonnull Workbook workbook) {
        Map<String, DataSetList> excelDataSetList = null;
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        while (sheetIterator.hasNext()) {
            List<String> dataSetNames = Lists.newArrayListWithExpectedSize(20);
            Sheet sheet = sheetIterator.next();
            Iterator<Row> rowIterator = sheet.rowIterator();
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                int paramColumnIndex = findParameterColumnIndex(headerRow);
                if (paramColumnIndex < 0) {
                    continue;
                }
                Iterator<Cell> cellIterator = headerRow.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (cell.getColumnIndex() <= paramColumnIndex) {
                        continue;
                    }
                    String value = cell.getStringCellValue();
                    if (!Strings.isNullOrEmpty(value)) {
                        dataSetNames.add(value);
                    }
                }
            }
            if (dataSetNames.isEmpty())
                continue;
            ExcelDataSetList dataSetList = new ExcelDataSetList();
            String sheetName = sheet.getSheetName();
            dataSetList.setName(sheetName);
            dataSetList.setFileName(file.getName());
            for (String dataSetName : dataSetNames) {
                ExcelDataSet dataSet = new ExcelDataSet(dataSetName, file);
                dataSet.setStoragePath(sheet.getSheetName());
                dataSetList.getDataSets().add(dataSet);
            }
            if (excelDataSetList == null)
                excelDataSetList = Maps.newHashMap();
            excelDataSetList.put(sheetName, dataSetList);
        }
        return excelDataSetList;
    }

    private int findParameterColumnIndex(Row row) {
        for (Cell cell : row) {
            if (PARAMETER_COLUMN.equalsIgnoreCase(cell.getStringCellValue())) {
                return cell.getColumnIndex();
            }
        }
        return -1;
    }

    private void readVariables(File file, Workbook workbook, Map<String, DataSetList> excelDataSetList) {
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            readVariables(file, excelDataSetList, sheet);
        }
    }

    private void readVariables(File file, Map<String, DataSetList> excelDataSetList, Sheet sheet) {
        ExcelDSUtils dsUtils = new ExcelDSUtils();
        int entityColumn = ExcelDSUtils.findColumn(sheet, ENTITY_COLUMN);
        int paramsColumn = ExcelDSUtils.findColumn(sheet, PARAMETER_COLUMN);
        dsUtils.setEntityColumnId(entityColumn);
        LinkedHashSet<Group> groups = dsUtils.findGroups(sheet);
        for (Group group : groups) {
            fillParams(excelDataSetList, sheet, group, paramsColumn);
        }
    }

    private void fillParams(Map<String, DataSetList> excelDataSetList, Sheet sheet, Group group, int paramsColumn) {
        for (int index = group.getStartRowIndex(); index < group.getEndRowIndex(); index++) {
            Cell cell = sheet.getRow(index).getCell(paramsColumn);
            if (cell != null) {
                String cellValue = cell.getStringCellValue();
                if (!Strings.isNullOrEmpty(cellValue)) {
                    excelDataSetList.get(sheet.getSheetName()).getVariables().add(String.format("%s.%s", group.getName(), cellValue));
                }
            }
        }
    }

}
