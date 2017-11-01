package View;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
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

    public static Window getDebugWindows(Map<String,Float> debugData,Map<String,Boolean> debugData1,
                                         Map<String,Integer> debugData2,String featuresName)
    {
        Window window = new Window("DEBUG DATA");
        if(debugData != null)
        {
            String data[][] = new String[debugData.keySet().size()][debugData.keySet().size()];
            int testID = 0;
            for(String key:debugData.keySet())
            {
                data[testID][0] = key;
                data[testID][1] = debugData.get(key).toString();

                testID++;
            }
            String column[]={"Number",featuresName};
            JTable jt=new JTable(data,column);
            jt.setBounds(30,40,200,300);
            JScrollPane sp=new JScrollPane(jt);

            window.add(sp);
        }
        else if(debugData1 != null)
        {
            String data[][] = new String[debugData1.keySet().size()][debugData1.keySet().size()];
            int testID = 0;
            for(String key:debugData1.keySet())
            {
                data[testID][0] = key;
                data[testID][1] = debugData1.get(key).toString();

                testID++;
            }
            String column[]={"Number",featuresName};
            JTable jt=new JTable(data,column);
            jt.setBounds(30,40,200,300);
            JScrollPane sp=new JScrollPane(jt);

            window.add(sp);
        }
        else if(debugData2 != null)
        {
            String data[][] = new String[debugData2.keySet().size()][debugData2.keySet().size()];
            int testID = 0;
            for(String key:debugData2.keySet())
            {
                data[testID][0] = key;
                data[testID][1] = debugData2.get(key).toString();

                testID++;
            }
            String column[]={"Number",featuresName};
            JTable jt=new JTable(data,column);
            jt.setBounds(30,40,200,300);
            JScrollPane sp=new JScrollPane(jt);

            window.add(sp);
        }
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
