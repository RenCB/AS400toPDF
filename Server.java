package com.sanbo.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Server {

    public static void main(String[] arg) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/prt", new TestHandler());
        server.start();
        System.out.println("PRT Server running...");
    }

    static class TestHandler implements HttpHandler{
        //private static Object PRT2 = PRT52;

        @Override
        public void handle(HttpExchange exchange) {
            //String response = "hello world";

            try{
                //获得查询字符串(get)
                String states = null;
                String sfileListData = null;
                String queryString =  exchange.getRequestURI().getQuery();
                Map<String,String> queryStringInfo = formData2Dic(queryString);

                if("getlist".equals(queryStringInfo.get("option"))){
                    sfileListData = SoolledList.getList();
                    System.out.println(queryStringInfo);
                    exchange.sendResponseHeaders(200,0);
                    OutputStream os = exchange.getResponseBody();
                    //os.write(states.getBytes());
                    os.write(sfileListData.getBytes());
                    os.close();

                }else if("getfile".equals(queryStringInfo.get("option"))){
                    String printer = queryStringInfo.get("printer");
                    String sname = queryStringInfo.get("sname");
                    int snumber = Integer.parseInt(queryStringInfo.get("snumber"));
                    String jname = queryStringInfo.get("jname");
                    String juser = queryStringInfo.get("juser");
                    String jnumber = queryStringInfo.get("jnumber");

                    //获得表单提交数据(post)
                    String postString = IOUtils.toString(exchange.getRequestBody());
                    Map<String,String> postInfo = formData2Dic(postString);

                    if("PRT51".equals(printer)){
                        states = PRT51.createPDF(sname, snumber, jname, juser, jnumber);

                    }else if("PRT52".equals(printer)){
                        states = PRT52.createPDF(sname, snumber, jname, juser, jnumber);

                    }else if("PRT53".equals(printer)){
                        states = PRT53.createPDF(sname, snumber, jname, juser, jnumber);

                    }else if("PRT54_MCH".equals(printer)){
                        states = PRT54_MCH.createPDF(sname, snumber, jname, juser, jnumber);

                    }else if("PRT54_MTS".equals(printer)){
                        states = PRT54_MTS.createPDF(sname, snumber, jname, juser, jnumber);

                    }else if("PRT55".equals(printer)){
                        states = PRT55.createPDF(sname, snumber, jname, juser, jnumber);

                    }else {
                        states="PRINTER ERROR";
                    }

                    System.out.println(queryStringInfo);
                    exchange.sendResponseHeaders(200,0);
                    OutputStream os = exchange.getResponseBody();
                    os.write(states.getBytes());
                    //os.write(sfileData.getBytes());
                    os.close();

                }
                else{

                    exchange.sendResponseHeaders(200,0);
                    OutputStream os = exchange.getResponseBody();
                    os.write("ERROR".getBytes());

                    os.close();
                }


            }catch (IOException ie) {


            } catch (Exception e) {

            }

        }
    }

    public static Map<String,String> formData2Dic(String formData ) {
        Map<String,String> result = new HashMap<>();
        if(formData== null || formData.trim().length() == 0) {
            return result;
        }
        final String[] items = formData.split("&");
        Arrays.stream(items).forEach(item ->{
            final String[] keyAndVal = item.split("=");
            if( keyAndVal.length == 2) {
                try{
                    final String key = URLDecoder.decode( keyAndVal[0],"utf8");
                    final String val = URLDecoder.decode( keyAndVal[1],"utf8");
                    result.put(key,val);
                }catch (UnsupportedEncodingException e) {}
            }
        });
        return result;
    }

//    public static String PRT(String prtname){
//        if("PRT51".equals(prtname)){
//
//        }
//    }
}

