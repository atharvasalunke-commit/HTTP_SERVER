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
   public ResultSetMetaData get_MeteData(){
        return this.metaData;
    }
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
    public String insert_Values(String Query,ArrayList<String> list) {
        this.sql_Query = Query;
        try (Connection conn = DriverManager.getConnection(Url, User, Pass);) {
            PreparedStatement pstmt=conn.prepareStatement(sql_Query);
            for(int i=0;i<list.size();i++){
                pstmt.setObject(i+1,list.get(i));
                System.out.println(list.get(i));
            }
            System.out.println(pstmt.toString());
            rows_Effect = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rows_Effect >=1) {
            String x = rows_Effect+" row is updated successfully";
            return x;
        }
        return "Rows could not be update";
    }
    public boolean read_Data(ArrayList<String>Data,ArrayList<String>list2,String Query,ArrayList<String> list3){
        this.sql_Query=Query;
        try(Connection conn=DriverManager.getConnection(Url,User,Pass);){
            PreparedStatement pstmt=conn.prepareStatement(sql_Query);
            for(int i=0;i<list3.size();i++){
                pstmt.setString(i+1,list3.get(i));
            }
            ResultSet manage_Mysql=pstmt.executeQuery();
            ResultSetMetaData liveMetaData = manage_Mysql.getMetaData();
            boolean indicator = true;
            int n=liveMetaData.getColumnCount();
                while (manage_Mysql.next()) {
                    if(manage_Mysql.getString(n).equals("1")){
                        continue;
                    }
                    for(int i=1;i<n;i++){
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