package DB;
import java.sql.*;
import java.util.*;

public class DataBase {
    private static final String Url="jdbc:mysql://localhost:3306/server_db";
    private static final String User="admin";
    private static final String Pass="147258";
    private final String sql_Query;

    public DataBase(String Query){
        this.sql_Query=Query;
    };

    public boolean checkIfCoulmnExists(String table_Name,String Column,ArrayList<String>list2){
        String Query="Select * from "+ table_Name+" LIMIT 0";
        boolean flag=false;
        try(Connection con=DriverManager.getConnection(Url,User,Pass);
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery(Query);
        ){
            ResultSetMetaData metaData=rs.getMetaData();
            int columnCount=metaData.getColumnCount();
            for(int i=1;i<=columnCount;i++){
                String columnName=metaData.getColumnName(i);
                list2.add(columnName);
                if(Column.equalsIgnoreCase(columnName)){
                    flag=true;
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return flag;
    }

    public boolean read_Data(ArrayList<String>Data,ArrayList<String>list2){
        try(Connection conn=DriverManager.getConnection(Url,User,Pass);
            PreparedStatement stmt=conn.prepareStatement(sql_Query);
            ResultSet read_Data_From_Mysql=stmt.executeQuery();){
            boolean indicator=true;
            while(read_Data_From_Mysql.next()){
                    Data.add(list2.get(0));
                    Data.add(read_Data_From_Mysql.getString(1));
                    Data.add(list2.get(1));
                    Data.add(read_Data_From_Mysql.getString(2));
                    Data.add(list2.get(2));
                    Data.add(read_Data_From_Mysql.getString(3));
                    Data.add(list2.get(3));
                    Data.add(read_Data_From_Mysql.getString(4));
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