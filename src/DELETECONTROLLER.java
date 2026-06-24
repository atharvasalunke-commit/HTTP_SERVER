import DB.DataBase;

import java.io.BufferedWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class DELETECONTROLLER {
    private String table_Name;
    private String[]Requests;
    private final Connection conn;
    HashMap<String,Integer> mp1;
    DELETECONTROLLER(String table_Name,String[]Requests,Connection conn,HashMap<String,Integer>mp1) {
        this.table_Name=table_Name;
        this.Requests=Requests;
        this.conn=conn;
        this.mp1=mp1;
    }
    public void Delete_Row(BufferedWriter out) {
        ResponseBuilder rb=new ResponseBuilder();
        DataBase db = new DataBase(conn);
        HashMap<String,Integer> mp=new HashMap<>();
        ArrayList<String> list=new ArrayList<>();
        if(mp1.containsKey(table_Name)){
            db.Insert_Columns_in_map(table_Name,mp,list);
        }
        else{
            rb.send("400 Bad Request",out);
            return;
        }
        String[] Rows=Requests[1].split("&");
        StringBuilder Query=new StringBuilder("UPDATE "+table_Name+" SET is_deleted=TRUE WHERE ");
        ArrayList<String> list2=new ArrayList<>();
        if(Rows.length>=2){
            for(int i=0;i<Rows.length;i++){
                String[] temp1=Rows[i].split("=");
                if(mp.containsKey(temp1[0])){
                    if(i>0){
                        Query.append(" AND ").append(temp1[0]).append(" = ? " );
                    }
                    else{
                        Query.append(temp1[0]).append(" = ? ");
                    }
                    list2.add(temp1[1]);
                }
            }
        }
        else if(Rows.length==1&&Rows[0].contains(",")){
            String[] temp1=Rows[0].split("[=,]");
           if(mp.containsKey(temp1[0])) {
               Query.append(temp1[0]).append(" in ").append("(");
               for (int i = 1; i < temp1.length; i++) {
                   list2.add(temp1[i]);
                Query.append(" ? ");
                if(i+1<temp1.length){
                    Query.append(",");
                }
               }
               Query.append(")");
           }
        }
        else if(Rows.length==1){
            Rows[0]=Rows[0].replace("%20"," ");
                String[] temp1=Rows[0].split("=");
                if(mp.containsKey(temp1[0])) {
                    Query.append(temp1[0]).append(" = ? ");
                    list2.add(temp1[1]);
                }
        }
        else{
           rb.send("400 Bad Request",out);
        }
        System.out.println(Query);
        String sql_Query = Query.toString();
        int rows_Deleted=db.insert_Values(sql_Query,list2);
        StringBuilder Status_Code=new StringBuilder();
        StringBuilder Body=new StringBuilder();
        if(rows_Deleted==0){
            Status_Code.append("404 Not Found");
            Body.append("Could not find the rows to delete");
        }
        else if(rows_Deleted==-1){
            Status_Code.append("400 Bad Request");
            Body.append("Mysql throws an error");
        }
        else if(rows_Deleted>=1){
            Status_Code.append("200 OK");
            Body.append(rows_Deleted).append("rows Deleted");
        }
        StringBuilder main_Body = new StringBuilder();
        main_Body.append("{\"message\":\"").append(Body).append("\"}");
        String Response=rb.Response(main_Body.toString(),Status_Code.toString());
        rb.send(Response,out);
    }

}
