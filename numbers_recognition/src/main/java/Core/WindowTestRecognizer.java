package Core;

import View.FileChoosePanel;
import View.ImageFileChoosePanel;
import View.Window;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WindowTestRecognizer {

    public static Window getTestWindows(Map<String,Boolean> testResults)
    {
        Window window = new Window("Recognized - " + String.valueOf(passToFailTest(testResults)) + "%");
        String data[][] = new String[testResults.keySet().size()][testResults.keySet().size()];
        int testID = 0;
        for(String key:testResults.keySet())
        {
            data[testID][0] = String.valueOf(testID);
            data[testID][1] = key;
            data[testID][2] = testResults.get(key).toString();

            testID++;
        }
        String column[]={"Test_ID","Number","Result"};
        JTable jt=new JTable(data,column){
            @Override
            public Class<?> getColumnClass(int column) {
                if(convertColumnIndexToModel(column)==2) return Double.class;
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

    private static float passToFailTest(Map<String,Boolean> testResults)
    {
        int pass = 0;
        for(Boolean result : testResults.values())
        {
            if(result.equals(true))
                pass++;
        }

        return (float)(100*pass/testResults.values().size());

    }
}
