package ru.itb.testautomation.core.dataset.util;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import ru.itb.testautomation.core.dataset.impl.Group;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class ExcelDSUtils {
    private int entityColumnId;
    private int paramColumnId;

    public ExcelDSUtils() {
    }

    public LinkedHashSet<Group> findGroups(Sheet sheet) {
        LinkedHashSet<Group> groups = Sets.newLinkedHashSet();
        int count = sheet.getLastRowNum();
        int rowIndex = 1;
        while (rowIndex < count) {
            Cell cell = sheet.getRow(rowIndex).getCell(this.entityColumnId);
            if (cell != null) {
                String cellValue = cell.getStringCellValue();
                if (!Strings.isNullOrEmpty(cellValue)) {
                    Group group = new Group();
                    group.setName(cellValue);
                    group.setStartRowIndex(rowIndex++);
                    group.setEndRowIndex(getEndGroupRowIndex(sheet, count, rowIndex));
                    groups.add(group);
                    rowIndex = group.getEndRowIndex();
                }
            }
        }
        return groups;
    }

    private int getEndGroupRowIndex(Sheet sheet, int count, int startRowIndex) {
        for (int index = startRowIndex; index < count; index++) {
            Cell cell = sheet.getRow(index).getCell(this.entityColumnId);
            if (cell != null && !Strings.isNullOrEmpty(cell.getStringCellValue())) {
                return index;
            }
        }
        return count;
    }

    public int getEntityColumnId() {
        return entityColumnId;
    }

    public void setEntityColumnId(int entityColumnId) {
        this.entityColumnId = entityColumnId;
    }

    public int getParamColumnId() {
        return paramColumnId;
    }

    public void setParamColumnId(int paramColumnId) {
        this.paramColumnId = paramColumnId;
    }

    public static int findColumn(Sheet sheet, String entityColumn) {
        if (sheet.rowIterator().hasNext()) {
            Iterator<Cell> cellIterator = sheet.rowIterator().next().cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if (entityColumn.equals(cell.getStringCellValue())) {
                    return cell.getColumnIndex();
                }
            }

        }
        return 0;
    }


}
