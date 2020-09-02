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

public class PRT54_MCH {
    //    public static void creatAS400(String server,String uname,String upw){
//        AS400 sys = new AS400("192.168.1.49","NMTS","NMTS");
//        System.out.println(sys);
//    }
    public static String createPDF(String sname,int snumber,String jname,String juser,String jnumber) throws IOException {
        PdfFont font = PdfFontFactory.createFont("d:/simsun.TTF", PdfEncodings.IDENTITY_H, true);
        String s= "NOT FOUND";

        //create PDF
        PdfDocument pdf = new PdfDocument(new PdfWriter("D://pic.pdf"));
        Document document = new Document(pdf);

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
                    sname,       // splf name
                    snumber,           // splf number
                    jname,    // job name
                    juser,      // job user
                    jnumber );   // job number

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
                if(p>0){
                    document.add(new AreaBreak());
                }
                float ls = 820;
                String[] strs = pageStr[p].replaceAll("\r","").split("\n");

                document.add(img);
                for (int i =0;i<strs.length;i++){
                    //从底部删除3个\n
                    if(i==59||i==60||i==58){
//                        System.out.println("!");
                        continue;
                    }
                    float n=0;
                    //String[] tempstr = strs[i].split("");
                    char[] chars = strs[i].toCharArray();
//                System.out.println(chars.length);


                    for(int j=0;j<chars.length;j++){
                        if(chars[j]==' '){
                            n=n+1;
                        }else{
                            break;
                        }
                    }
                    if(n==3){
                        n=n+13;
                    }else if(n==43){
                        n=n+217;
                    }else if(n==72){
                        n=n+348;
                    }else if(n==11){
                        n=n+48;
                    }else if(n==12){
                        n=n+47;
                    }else if(n==7){
                        n=n+31;
                    }else if(n==70){
                        n=n+315;
                    }else if(n==51){
                        n=n+229;
                    }else if(n==41){
                        n=n+189;
                    }else if (n==4){
                        n=n+18;
                    }

                    // System.out.println(n);
                    Text t = new Text(strs[i])
                            .setFont(font)
                            .setFontSize(9)
                            .setStrokeColor(DeviceGray.GRAY);
                    document.add(new Paragraph(t)
                            .setFixedPosition(37,ls,5000)
                            .setCharacterSpacing(1)
                            .setFirstLineIndent(n)
                    );
                    ls=ls-13;


                }
            }


            byte[] b = dataStr.getBytes();
            fs.write(b);
            fs.close();

        } catch (Exception e) {
            System.out.println(e);
            // document.close();
            return "NOT FOUND";
        }


        document.close();
        System.out.println("PDF Created");
        return "OK";
    }

}



