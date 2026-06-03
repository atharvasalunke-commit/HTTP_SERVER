import DB.DataBase;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GETCONTROLLER {


    public void handle_Get_Request(String[] Strs, HashMap<String, ArrayList<String>> mp, BufferedWriter out){
        ResponseBuilder rb=new ResponseBuilder();
        HashMap<String,Integer>mp1=new HashMap<>();
        mp1.put("products",1);
        mp1.put("users",2);
        mp1.put("orders",3);
        mp1.put("order_items",4);

        String request_Type=Strs[1];
        String[] Requests=request_Type.split("\\?");

        ArrayList<String>list=new ArrayList<>();
        int n=Requests.length;
        String Query="SELECT * FROM ";
        String table_name=Requests[0].replace("/","");
        ArrayList<String>list2=new ArrayList<>();
        if(n==2 && mp1.containsKey(table_name)){
            Query+=table_name+" ";
            String[] temp=Requests[1].split("&");
            int x=temp.length;
            int count=0;
            int count2=0;
            for(int i=0; i<x; i++) {
                String[] temp2 = temp[i].split("=");
                if (temp2.length < 2) continue;
                StringBuilder temp4=new StringBuilder();
                if(temp2[0].equalsIgnoreCase("product_name")||temp2[0].equalsIgnoreCase("Full_name")) {
                    String temp3 = rb.space_Between_Queries(temp2[1]);
                    temp4.append(temp3);
                }
                else{
                    temp4.append(temp2[1]);
                }
                String temp5=temp4.toString();
                if (new DataBase(Query).checkIfCoulmnExists(table_name, temp2[0],list2)) {
                    count++;
                }
                else{
                    count2++;
                    continue;
                }
                // BUG FIX 5: Added single quotes around temp2[1] so MySQL doesn't throw a syntax error
                if (count == 1) {
                    Query += "WHERE " + temp2[0] + " = '" + temp5 + "'";
                } else if (count > 1) {
                    Query += " AND " + temp2[0] + " = '" + temp5 + "'";
                }
            }
            if(count2==x){
                try {
                    out.write("HTTP/1.1 404 Not Found\r\n\r\n");
                    out.flush();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                return;
            }
        }
        else if(mp1.containsKey(table_name)){
            boolean temp_flag=new DataBase(Query).checkIfCoulmnExists(table_name,table_name,list2);
            Query+=table_name;
        }
        else{
            try{
                out.write("HTTP/1.1 404 Not Found\r\n\r\n");
                out.flush();
                return;
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        DataBase db=new DataBase(Query);
        boolean flag=db.read_Data(list,list2);
        if(flag){
            try{
                out.write("HTTP/1.1 404 Not Found\r\n\r\n");
                out.flush();
                return;
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        mp.put(table_name,list);

        // Ensure category param doesn't throw out of bounds if no parameters were sent
        StringBuilder sb=new StringBuilder(table_name);
        sb.append("/");
        String z=sb.toString();

        String categoryParam = (Requests.length > 1) ? z+Requests[1] : table_name;
        String main_Body=rb.create_main_Body(mp, categoryParam);
        String Response=rb.Response(main_Body);
        rb.send(Response,out);
    }

}
