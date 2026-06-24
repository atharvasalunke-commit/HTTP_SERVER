import DB.DataBase;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
public class POSTCONTROLLER {
    private String Payload;
    private final Connection conn;
    private HashMap<String, ArrayList<String>> new_Rows = new HashMap<>();
    private String table;
    private BufferedReader in;
    private String Request;
    public POSTCONTROLLER(BufferedReader in, String table,String Request,Connection conn) {
        this.Request=Request;
        this.in=in;
         this.table = table;
         this.conn=conn;
    }
    public void Post_Handler(BufferedWriter out,HashMap<String,Integer>mp1) {
        ResponseBuilder rb=new ResponseBuilder();
        DataBase db = new DataBase(conn);
        PAYLOAD_HANDLER PH=new PAYLOAD_HANDLER(in);
        ArrayList<String> list2 = new ArrayList<>();
        HashMap<String, Integer> map = new HashMap<>();
        if(mp1.containsKey(table)){
            db.Insert_Columns_in_map(table, map, list2);
        }
        else{
            rb.send("400 Bad Request",out);
            return;
        }

        int len=PH.Find_Con_Length(this.Request);
        if(len==-1){
            rb.send("400 Bad Request",out);
            return;
        }
        Payload=PH.Payload_Receiver(len);
        ArrayList<String> list = new ArrayList<>();
        PH.Payload_Create(Payload,list);
        String query = "Select * from " + table;


        for (int i = 1; i < list2.size(); i++) {
            new_Rows.put(list2.get(i), new ArrayList<>());
        }
        int i=1;
       while(i<list.size()) {
           if(list.get(i-1).equals("Ends here")){
               i++;
               continue;
           }
            if (new_Rows.containsKey(list.get(i - 1))) {
                ArrayList<String> temp = new_Rows.get(list.get(i - 1));
                temp.add(list.get(i));
                new_Rows.remove(list.get(i - 1));
                new_Rows.put(list.get(i - 1), temp);
            }
            i+=2;
        }
       ArrayList<String> list3 = new ArrayList<>();
        String Query=Make_Post_Query(table,list3,db);
        int rows_Inserted=db.insert_Values(Query,list3);
        StringBuilder Status_Code=new StringBuilder();
        StringBuilder Body=new StringBuilder();
        if(rows_Inserted<=0){
            Status_Code.append("400 Bad Request");
            Body.append("Mysql throws an error or no row were inserted");
        }
        else if(rows_Inserted>=1){
            Status_Code.append("201 Created");
            Body.append(rows_Inserted).append("rows inserted");
        }
        StringBuilder main_Body = new StringBuilder();
        main_Body.append("{\"message\":\"").append(Body).append("\"}");
        String Response=rb.Response(main_Body.toString(),Status_Code.toString());
        rb.send(Response,out);
    }

    public String Make_Post_Query(String table_name,ArrayList<String>list3,DataBase db) {
        StringBuilder sql_Q = new StringBuilder("Insert into " + table_name + " (");
        try {
            ResultSetMetaData metaData = db.get_MeteData();
            int n = metaData.getColumnCount();
            ArrayList<ArrayList<String>> list = new ArrayList<>();
            n-=1;
            for (int i = 2; i <= n; i++) {
                String column = metaData.getColumnName(i);
                ArrayList<String> list2 = new_Rows.get(column);
                sql_Q.append(column);
                if (i + 1 <= n) {
                    sql_Q.append(",");
                }

                list.add(list2);
            }
            sql_Q.append(") Values ");
            int i = 1;
            int j = 0;
            boolean flag = false;
            sql_Q.append("(");
            int size = list.get(0).size();
            while (j < size) {
                sql_Q.append(" ? ");
                list3.add(list.get(i - 1).get(j));
                if (i + 1 == n) {
                    sql_Q.append(")");
                }
                if (j + 1 < size) {
                    sql_Q.append(",");
                } else if (i + 1 < n) {
                    sql_Q.append(",");
                }
                if (i + 1 == n) {
                    sql_Q.append("(");
                    i = 0;
                    j++;
                }
                i++;
            }
            sql_Q.deleteCharAt(sql_Q.length() - 1);
            System.out.println(sql_Q);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sql_Q.toString();
    }
}

