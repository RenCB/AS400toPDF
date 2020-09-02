package com.sanbo.httpserver;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.SpooledFile;
import com.ibm.as400.access.SpooledFileList;
import org.json.JSONObject;

import java.util.Enumeration;

public class SoolledList {
    public static String getList(){

        JSONObject obj = new JSONObject();
        try {


            AS400 server = new AS400("192.168.1.49","NMTS","NMTS");
            System.out.println("Loading Soopled lists...");

            SpooledFileList splfList = new SpooledFileList(server);

            // set filters, all users, on all queues
            splfList.setUserFilter("NMTS");
            splfList.setUserDataFilter("HL0120R");
            //splfList.set
//            splfList.setJobSystemFilter("QUSRSYS/PRT51");
            splfList.setQueueFilter("/QSYS.LIB/%ALL%.LIB/%ALL%.OUTQ");


            // open list, openSynchronously() returns when the list is completed.
            splfList.openSynchronously();
            // Enumeration enum = splfList.getObjects();
            Enumeration enumx = splfList.getObjects();

            int index = 0;
            while (enumx.hasMoreElements()) {
                SpooledFile splf = (SpooledFile) enumx.nextElement();

                if (splf != null) {
                    JSONObject FileProperty = new JSONObject();
                    String sName = splf.getName();
                    int sNumber = splf.getNumber();
                    String jName = splf.getJobName();
                    String jUser = splf.getJobUser();
                    String jNumber = splf.getJobNumber();

                    FileProperty.put("sname",sName);
                    FileProperty.put("snumber",sNumber);
                    FileProperty.put("jname",jName);
                    FileProperty.put("juser",jUser);
                    FileProperty.put("jnumber",jNumber);

                    //  strSpooledNumber = splf.getStringAttribute(SpooledFile.)
                    obj.put(index+"",FileProperty);
                    System.out.println(obj.toString());
                    index = index+1;
                }
            }
            // clean up after we are done with the list
            splfList.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return obj.toString();
    }
}
