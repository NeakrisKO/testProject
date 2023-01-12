package ru.itb.testautomation.core.dataset.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import ru.itb.testautomation.core.context.impl.JSONContextImpl;
import ru.itb.testautomation.core.context.intf.JSONContext;
import ru.itb.testautomation.core.dataset.intf.DataSet;
import ru.itb.testautomation.core.dataset.util.ExcelDSUtils;

public class ExcelDataSet implements DataSet {
    private Object id;
    private String description;
    private static final String PARAMETER_COL_NAME = "Parameter";
    private static final String ENTITY_COL_NAME = "Entity";
    private final ExcelDSUtils excelDSUtils = new ExcelDSUtils();
    private int paramNameColumnInd = -1;
    private String name;
    private File file;
    private int entityColumnId = -1;
    private int valueColumnIndex = -1;
    private final DataFormatter dataFormatter = new DataFormatter();
    private String storagePath;

    public ExcelDataSet(String name, File file) {
        this.name = name;
        this.file = file;
    }

    @Override
    public JSONContext read() {
        JSONContext context = new JSONContextImpl();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            Workbook workBook = WorkbookFactory.create(fileInputStream);
            buildDataSet(workBook.getSheet(getStoragePath()), workBook, context);
        } catch (Exception e) {
            //LOGGER.error("Error reading data set {}, data set file is {}", name, file, e);
            System.out.println(String.format("Error reading data set {}, data set file is {}", name, file));
            e.printStackTrace();
        }
        return context;
    }

    public void addModifiedToName(boolean flag) {
        if (flag) {
            name = "[Modified Dataset]" + name;
        } else {
            if (name.startsWith("[Modified Dataset]")) {
                name = name.replace("[Modified Dataset]", "");
            }
        }
    }

    private void defineColumnsIndexes(Row headerRow) {
        Iterator<Cell> cellIterator = headerRow.cellIterator();
        while (cellIterator.hasNext() && (entityColumnId < 0 || paramNameColumnInd < 0 || valueColumnIndex < 0)) {
            Cell cell = cellIterator.next();
            if (cell == null) {
                continue;
            }
            String cellValue = cell.getStringCellValue();
            switch (cellValue) {
                case PARAMETER_COL_NAME: {
                    this.paramNameColumnInd = cell.getColumnIndex();
                    break;
                }
                case ENTITY_COL_NAME: {
                    this.entityColumnId = cell.getColumnIndex();
                    break;
                }
                default: {
                    if (name.equalsIgnoreCase(cellValue)) {
                        this.valueColumnIndex = cell.getColumnIndex();
                    }
                }
            }
        }
    }

    private void buildDataSet(Sheet sheet, Workbook workBook, JSONContext context) {
        Iterator<Row> rowIterator = sheet.rowIterator();
        //check header
        if (rowIterator.hasNext()) {
            Row rowHeader = rowIterator.next();
            defineColumnsIndexes(rowHeader);
            if (!isDataSetContainsColumns()) {
                return;
            }
            excelDSUtils.setEntityColumnId(entityColumnId);
        }
        FormulaEvaluator evaluator = workBook.getCreationHelper().createFormulaEvaluator();
        LinkedHashSet<Group> groups = excelDSUtils.findGroups(sheet);
        for (Group group : groups) {
            context.put(group.getName(), getValues(group, sheet, evaluator));
        }
    }

    private Object getValues(Group group, Sheet sheet, FormulaEvaluator evaluator) {
        JSONContext paramItem = new JSONContextImpl();
        for (int rowIndex = group.getStartRowIndex(); rowIndex <= group.getEndRowIndex(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            String paramName = (String) getCellValue(row, paramNameColumnInd, null);
            if (paramName == null) {
                continue;
            }
            Object paramValue = getCellValue(row, valueColumnIndex, evaluator);
            paramItem.put(paramName, paramValue);
        }
        return paramItem;
    }

    private Object getCellValue(Row row, int cellIndex, FormulaEvaluator evaluator) {
        Cell cell = row.getCell(cellIndex);
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    return cell.getStringCellValue();
                case Cell.CELL_TYPE_FORMULA: {
                    if (evaluator != null) {
                        //for use format from excel
                        return dataFormatter.formatCellValue(cell,evaluator); // evaluator.evaluate(cell).getStringValue();
                    }
                    //LOGGER.error("Unexpected formula occurred at '{}' sheet '{}'", cell.getAddress().formatAsString(), row.getSheet().getSheetName());
                    return StringUtils.EMPTY;
                }
                case Cell.CELL_TYPE_NUMERIC:
                    return dataFormatter.formatCellValue(cell);
                case Cell.CELL_TYPE_BLANK:
                    return StringUtils.EMPTY;
                case Cell.CELL_TYPE_BOOLEAN:
                    return cell.getBooleanCellValue();
            }
        }
        return null;
    }

    public LinkedHashSet<Group> findGroups(Sheet sheet) {
        return excelDSUtils.findGroups(sheet);
    }

    private boolean isDataSetContainsColumns() {
        return entityColumnId != -1 && paramNameColumnInd != -1 && valueColumnIndex != -1;
    }

    public int getParamNameColumnInd() {
        return paramNameColumnInd;
    }

    public int getEntityColumnId() {
        return entityColumnId;
    }

    public int getValueColumnIndex() {
        return valueColumnIndex;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getId() {
        return this.id;
    }

    @Override
    public void setId(Object id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
