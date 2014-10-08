/* Copyright 2009 EB2 International Limited */
package helpers;

import java.util.List;

public class TableRowList
{
    private TableRow currentRow;
    private List<TableRow> rowList;

    public TableRowList(List<TableRow> rowList, TableRow currentRow)
    {
        this.rowList = rowList;
        this.currentRow = currentRow;
    }

    public void getPrevious()
    {
        int index = getCurrentRowIndex();

        if (indexInCorrectBounds(index))
        {
            currentRow = rowList.get(index - 1);
        }
    }

    public void getNext()
    {
        int index = getCurrentRowIndex();

        if (indexInCorrectBounds(index))
        {
            currentRow = rowList.get(index + 1);
        }
    }

    public TableRow getCurrent()
    {
        return currentRow;
    }

    public int getCurrentRowIndex()
    {
        return rowList.indexOf(currentRow);
    }

    public int getSize()
    {
        return rowList.size();
    }

    private boolean indexInCorrectBounds(int index)
    {
        if (index != rowList.size() && index > -1)
            return true;

        return false;
    }
}
