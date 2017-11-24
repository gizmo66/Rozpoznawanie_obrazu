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

    public static Window getDebugWindows(Map<String,Float> surface, Map<String,Boolean> vertical, Map<String,Boolean> horizontal,
                                         Map<String,Integer> endeed, String featuresName1, String featuresName2, String featuresName3, String featuresName4)
    {
        Window window = new Window("DEBUG DATA");
        String data[][] = new String[surface.keySet().size()][surface.keySet().size()];
        int testID = 0;
        for(String key:surface.keySet())
        {
            data[testID][0] = key;
            data[testID][1] = surface.get(key).toString();
            data[testID][2] = vertical.get(key).toString();
            data[testID][3] = horizontal.get(key).toString();
            data[testID][4] = endeed.get(key).toString();

            testID++;
        }
        String column[]={"Digits",featuresName1,featuresName2,featuresName3,featuresName4};
        JTable jt=new JTable(data,column);
        jt.setBounds(30,40,200,300);
        JScrollPane sp=new JScrollPane(jt);

        window.add(sp);
        window.setSize(300,400);
        window.setVisible(true);
        return window;
    }

    public static Window getDebugWindows_v1(Map<String,List<Number>> surface,Map<String,List<Number>> vertical,Map<String,
            List<Number>> horizontal,
                                         Map<String,List<Number>> endeed,String featuresName1,String featuresName2,String featuresName3,String featuresName4)
    {
        Window window = new Window("DEBUG DATA");
        String data[][] = new String[surface.keySet().size()][surface.keySet().size()];
        int testID = 0;
        for(String key:surface.keySet())
        {
            data[testID][0] = key;
            data[testID][1] = surface.get(key).toString();
            data[testID][2] = vertical.get(key).toString();
            data[testID][3] = horizontal.get(key).toString();
            data[testID][4] = endeed.get(key).toString();

            testID++;
        }
        String column[]={"Digits",featuresName1,featuresName2,featuresName3,featuresName4};
        JTable jt=new JTable(data,column);
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
