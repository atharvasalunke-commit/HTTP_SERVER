import DB.DataBase;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class POSTCONTROLLER {
    private final BufferedReader in;
    private String payLoad;
    private HashMap<String, ArrayList<String>> new_Rows=new HashMap<>();
    private String table;
    public POSTCONTROLLER(BufferedReader in,String table) {
        this.in = in;
        this.table=table;
    }

    public String Post_Handler(int len) {
        char[] buffer = new char[len];
        int Total_char_read = 0;
        while (Total_char_read < len) {
            try {
                int read_char = in.read(buffer, Total_char_read, len - Total_char_read);
                Total_char_read += read_char;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        payLoad = new String(buffer);
        String main_Body=Post_Query();
        ResponseBuilder rb=new ResponseBuilder();
        String Response =rb.Response(main_Body);
        return Response;
    }

    public String Post_Query() {
        String[] payLoads = payLoad.split("[\\[\\]]");
        String Data = payLoads[1];
        boolean flag = false;
        boolean flag1=false;
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        ArrayList<String> list = new ArrayList<>();
        boolean flag2=false;
        String query="Select * from "+table;
        DataBase Db=new DataBase();
        ArrayList<String>list2=new ArrayList<>();
        HashMap<String,Integer>map=new HashMap<>();
        Db.Insert_Columns_in_map(table,map,list2);
       for(int i=1;i<list2.size();i++){
           new_Rows.put(list2.get(i),new ArrayList<>());
       }

            char[] c = payLoads[1].toCharArray();
            for (int j = 0; j < c.length; j++) {
                if (c[j] == ' ') {
                }
                else if(c[j]=='"'){

                }
                else if(c[j]=='{'){
                    flag=true;
                    flag2=true;
                }
                else if(c[j]=='}'){
                    flag1=false;
                    list.add(sb2.toString().trim());
                    sb2=new StringBuilder();
                    flag2=false;
                }
                else if (flag2&&c[j] == ',') {
                    list.add(sb2.toString().trim());
                    sb2=new StringBuilder();
                    flag = true;
                    flag1=false;
                }
                else if(c[j]==':'){
                    list.add(sb1.toString().trim());
                    sb1=new StringBuilder();
                    flag=false;
                    flag1=true;
                }
                else if (flag&&flag2) {
                    sb1.append(c[j]);
                }
                else if(flag1&&flag2){
                    sb2.append(c[j]);
                }
            }
        for(int i=1;i<list.size();i+=2){
            if(new_Rows.containsKey(list.get(i-1))){
                ArrayList<String>temp=new_Rows.get(list.get(i-1));
                temp.add(list.get(i));
                new_Rows.remove(list.get(i-1));
                new_Rows.put(list.get(i-1),temp);
                System.out.println(list.get(i-1));
                System.out.println(list.get(i));
            }
        }
        String body=Db.insert_Values(table,new_Rows);
        StringBuilder main_Body=new StringBuilder();
        main_Body.append("{").append(body).append("}");
        return main_Body.toString();
    }
}

