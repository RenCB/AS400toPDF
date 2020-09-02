package com.sanbo.httpserver;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.PrintObject;
import com.ibm.as400.access.PrintParameterList;
import com.ibm.as400.access.SpooledFile;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class PRT54_MTS {
    //    public static void creatAS400(String server,String uname,String upw){
//        AS400 sys = new AS400("192.168.1.49","NMTS","NMTS");
//        System.out.println(sys);
//    }
    public static String createPDF(String sname,int snumber,String jname,String juser,String jnumber) throws IOException {
        PdfFont font = PdfFontFactory.createFont("d:/simsun.TTF", PdfEncodings.IDENTITY_H, true);


        //create PDF
        PdfDocument /**/pdf = new PdfDocument(new PdfWriter("D://pic.pdf"));
        Document document = new Document(pdf, PageSize.A4.rotate());


        String imgPath = "D://h.png";
        ImageData data = ImageDataFactory.create(imgPath);
        Image img = new Image(data);
        img.setAutoScaleWidth(true);
        img.setAutoScaleHeight(true);
        img.setFixedPosition(35,700);

        // document.add(p);

        //read data
        try {

            AS400 sys = new AS400("192.168.1.49","NMTS","NMTS");
            FileOutputStream fs = new FileOutputStream("d://po.txt");
            SpooledFile sf = new SpooledFile( sys,          // AS400
                    "QPRINT",       // splf name
                    1,           // splf number
                    "DSP83",    // job name
                    "NMTS",      // job user
                    "285988" );   // job number

            PrintParameterList printParms = new PrintParameterList();
            printParms.setParameter(PrintObject.ATTR_WORKSTATION_CUST_OBJECT,   "/QSYS.LIB/QWPDEFAULT.WSCST");
            printParms.setParameter(PrintObject.ATTR_MFGTYPE, "*WSCST");

            InputStreamReader in = new
                    InputStreamReader(sf.getTransformedInputStream(printParms));
            char[] buf = new char[32767];
            StringBuffer sbuf = new StringBuffer();
            if (in.ready()) {
                int bytesRead = 0;
                bytesRead = in.read(buf, 0, buf.length);

                while (bytesRead> 0) {
                    sbuf.append(buf, 0, bytesRead);
                    bytesRead = in.read(buf, 0, buf.length);
                }
            }

            String dataStr = sbuf.toString();
            String[] pageStr = dataStr.split("\f");

            for(int p =0;p<pageStr.length;p++){
                String[] strs = pageStr[p].replaceAll("\r","").split("\n");
                if(p>0){
                    document.add(new AreaBreak());
                }
                //document.add(img);
                float ls = 550;
                System.out.println(strs[61]);
                for(int i =0;i<strs.length;i++){
                    if(40<i && i<61){
                        continue;
                    }

                    if(i==61){
                        Text t = new Text(strs[i])
                                .setFont(font)
                                .setFontSize(9)
                                .setStrokeColor(DeviceGray.GRAY);
                        document.add(new Paragraph(t)
                                        .setFixedPosition(10,ls,5000)
                                        .setFirstLineIndent(350)
                                //.setCharacterSpacing(1)
                        );
                        ls = ls-13;
                    }else{
                        Text t = new Text(strs[i])
                                .setFont(font)
                                .setFontSize(9)
                                .setStrokeColor(DeviceGray.GRAY);
                        document.add(new Paragraph(t)
                                        .setFixedPosition(10,ls,5000)
                                //.setCharacterSpacing(1)
                        );
                        ls = ls-13;
                    }


                }
            }


            byte[] b = dataStr.getBytes();
            fs.write(b);
            fs.close();

        } catch (Exception e) {
            System.out.println(e);
            return "NOT FOUND";
        }


        document.close();
        System.out.println("PDF Created");
        return "OK";
    }

}


