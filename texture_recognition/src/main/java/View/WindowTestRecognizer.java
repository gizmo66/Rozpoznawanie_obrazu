package View;


import Core.ResultData;
import Extraction.FeaturesVector;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Map;


public class WindowTestRecognizer {

    public static Window getTestWindows(List<ResultData> testResults)
    {
        Window window = new Window("Recognized - " + String.valueOf(passToFailTest(testResults)) + "%");
        String data[][] = new String[testResults.size()][testResults.size()];
        int testID = 0;
        for(ResultData resultData:testResults)
        {
            data[testID][0] = String.valueOf(testID);
            data[testID][1] = resultData.pictureType;
            data[testID][2] = resultData.resultOfKnn;
            data[testID][3] = String.valueOf(resultData.result);

            testID++;
        }
        String column[]={"Test_ID","Digital","Result of KNN", "Recognized"};
        JTable jt=new JTable(data,column){
            @Override
            public Class<?> getColumnClass(int column) {
                if(convertColumnIndexToModel(column)==3) return Double.class;
                return super.getColumnClass(column);
            }
        };
        jt.setDefaultRenderer(Double.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column) {
                Component c = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
                c.setForeground((value) == "true" ? Color.GREEN : Color.RED);
                return c;
            }
        });
        jt.setBounds(30,40,200,300);
        JScrollPane sp=new JScrollPane(jt);

        window.add(sp);
        window.setSize(300,400);
        window.setVisible(true);

        return window;
    }

    private static float passToFailTest(List<ResultData> testResults)
    {
        int pass = 0;
        for(ResultData result : testResults)
        {
            if(result.result)
                pass++;
        }

        return (float)(100*pass/testResults.size());
    }
}
