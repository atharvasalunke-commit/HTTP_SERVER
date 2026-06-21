import DB.DataBase;

import java.io.*;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class POSTCONTROLLER {
    private String Payload;
    private HashMap<String, ArrayList<String>> new_Rows = new HashMap<>();
    private String table;
    private DataBase db;
    private BufferedReader in;
    private int len;
    public POSTCONTROLLER(BufferedReader in, String table,int len) {
      this.in=in;
      this.table = table;
      this.len=len;
    }


    public String Post_Handler() {
        PAYLOAD_HANDLER PR=new PAYLOAD_HANDLER(in,len);
        Payload=PR.Payload_Receiver();
        ArrayList<String> list = new ArrayList<>();
        PR.Payload_Create(Payload,list);
        String query = "Select * from " + table;
        this.db = new DataBase();
        ArrayList<String> list2 = new ArrayList<>();
        HashMap<String, Integer> map = new HashMap<>();
        db.Insert_Columns_in_map(table, map, list2);
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
                System.out.println(list.get(i - 1));
                System.out.println(list.get(i));
            }
            i+=2;
        }
       ArrayList<String> list3 = new ArrayList<>();
        String Query=Make_Post_Query(table,list3);
        String body=db.insert_Values(Query,list3);
        StringBuilder main_Body = new StringBuilder();
        main_Body.append("{").append(body).append("}");
        return main_Body.toString();
    }

    public String Make_Post_Query(String table_name,ArrayList<String>list3) {
        StringBuilder sql_Q = new StringBuilder("Insert into " + table_name + " (");
        try {
            ResultSetMetaData metaData = db.get_MeteData();
            int n = metaData.getColumnCount();
            ArrayList<ArrayList<String>> list = new ArrayList<>();
            System.out.println(table_name);
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
                try {
                    if (metaData.getColumnType(i + 1) == java.sql.Types.INTEGER || metaData.getColumnType(i + 1) == java.sql.Types.DECIMAL || metaData.getColumnType(i + 1) == java.sql.Types.DOUBLE || metaData.getColumnType(i + 1) == java.sql.Types.FLOAT) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sql_Q.append(" ? ");
                if (flag) {
                    list3.add(list.get(i - 1).get(j));
                } else {
                    list3.add(list.get(i - 1).get(j));
                }

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

