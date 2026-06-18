import DB.DataBase;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class GETCONTROLLER {
private HashMap<String,Integer>map=new HashMap<>();

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
        DataBase db=new DataBase();
        db.Insert_Columns_in_map(table_name,map,list2);
        if(n==2 && mp1.containsKey(table_name)){
            Query+=table_name+" ";
            String[] temp=Requests[1].split("&");
            int x=temp.length;
            int count=0;
            int count2=0;
            for(int i=0; i<x; i++) {
                String[] temp2 = temp[i].split("=");
                temp2[1]=temp2[1].replace("%20"," ");
                if (temp2.length < 2) continue;
                if (map.containsKey(temp2[0])) {
                    count++;
                }
                else{
                    count2++;
                    continue;
                 }
                if (count == 1) {
                    Query += "WHERE " + temp2[0] + " = '" + temp2[1] + "'";
                } else if (count > 1) {
                    Query += " AND " + temp2[0] + " = '" + temp2[1] + "'";
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

        boolean flag=db.read_Data(list,list2,Query);
        for(int i=0;i<list.size();i++){
            System.out.println(list.get(i));
        }
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
