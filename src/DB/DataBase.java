package DB;
import java.sql.*;
import java.util.*;

import static java.lang.Math.max;
import static java.util.Collections.list;

public class DataBase {
    private static final String Url="jdbc:mysql://localhost:3306/server_db";
    private static final String User="admin";
    private static final String Pass="147258";
    private  String sql_Query;
    private ResultSetMetaData metaData;
    private int rows_Effect;

    public void Insert_Columns_in_map(String table_Name,HashMap<String,Integer> map,ArrayList<String> list2){
        String Query="Select * from "+ table_Name+" LIMIT 0";
        try(Connection con=DriverManager.getConnection(Url,User,Pass);
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery(Query);
        ){
          this.metaData=rs.getMetaData();
            int columnCount=metaData.getColumnCount();
            for(int i=1;i<=columnCount;i++){
                String columnName=metaData.getColumnName(i);
                map.put(columnName,i);
                list2.add(columnName);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public String insert_Values(String table_name,HashMap<String,ArrayList<String>>map){
        try {
            int n = metaData.getColumnCount();
            ArrayList<ArrayList<String>> list = new ArrayList<>();
            StringBuilder sql_Q = new StringBuilder("Insert into " + table_name + " (");
            System.out.println(table_name);

            for (int i = 2; i <= n; i++) {
                String column = metaData.getColumnName(i);
                ArrayList<String> list2 = map.get(column);
                sql_Q.append(column);
                if(i+1<=n){
                    sql_Q.append(",");
                }

                list.add(list2);
            }
            sql_Q.append(") Values ");
            int i = 1;
            int j= 0;
            boolean flag = false;
            sql_Q.append("(");
            int size=list.get(0).size();
                while (j<size) {
                    try {
                        if (metaData.getColumnType(i + 1) == java.sql.Types.INTEGER || metaData.getColumnType(i + 1) == java.sql.Types.DECIMAL || metaData.getColumnType(i + 1) == java.sql.Types.DOUBLE || metaData.getColumnType(i + 1) == java.sql.Types.FLOAT) {
                            flag = true;
                        } else {
                            flag = false;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (flag) {
                        sql_Q.append(list.get(i - 1).get(j));
                    } else {
                        sql_Q.append("'").append(list.get(i - 1).get(j)).append("'");
                    }

                    if (i + 1 == n) {
                        sql_Q.append(")");
                    }
                    if(j+1<size){
                        sql_Q.append(",");
                    }
                    else if (i+1<n) {
                        sql_Q.append(",");
                    }
                    if(i+1==n){
                        sql_Q.append("(");
                        i=0;
                        j++;
                    }
                    i++;
                }
                sql_Q.deleteCharAt(sql_Q.length()-1);
            this.sql_Query = sql_Q.toString();
            System.out.println(sql_Query);
            try (Connection con = DriverManager.getConnection(Url, User, Pass);
                 Statement stmt = con.createStatement()) {
                rows_Effect = stmt.executeUpdate(sql_Query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
             catch(SQLException e){
                    e.printStackTrace();
                }
           if(rows_Effect>=1){
               String x="All rows are inserted successfully";
               return x;
           }
           return "Rows could not be inserted";


    }
    public boolean read_Data(ArrayList<String>Data,ArrayList<String>list2,String Query){
        this.sql_Query=Query;
        try(Connection conn=DriverManager.getConnection(Url,User,Pass);
            PreparedStatement stmt=conn.prepareStatement(sql_Query);
            ResultSet manage_Mysql=stmt.executeQuery();){
            boolean indicator = true;
            int n=metaData.getColumnCount();
                while (manage_Mysql.next()) {
                    for(int i=1;i<=n;i++){
                        Data.add(list2.get(i-1));
                        Data.add(manage_Mysql.getString(i));
                    }
                    indicator=false;
                }
            if(indicator){
                return true;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}