/* Copyright 2009 EB2 International Limited */
package fileManagers;

import java.util.ArrayList;
import java.util.List;

public class OrderFileInfo
{
    private List<String> orderList = new ArrayList<String>();
    
    public void addFile(String file) {
        orderList.add(file);
    }
    
    public List<String> getOrderList()
    {
        return orderList;
    }
}
