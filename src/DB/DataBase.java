package DB;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DataBase {
        private static final String Url="jdbc:mysql://localhost:3306/server_db";
        private static final String User="admin";
        private static final String pass="147258";
        private String sql_Query;
       public DataBase(String Query){
            this.sql_Query=Query;
        };
        public void create_Query(String s){

        }

        public void read_Data(ArrayList<String>Data){
            try(Connection conn=DriverManager.getConnection(Url,User,pass);
                PreparedStatement stmt=conn.prepareStatement(sql_Query);
                ResultSet read_Data_From_Mysql=stmt.executeQuery();){
                while(read_Data_From_Mysql.next()){
                    Data.add(read_Data_From_Mysql.getString("id"));
                    Data.add(read_Data_From_Mysql.getString("product_name"));
                    Data.add(read_Data_From_Mysql.getString("price"));
                    Data.add(read_Data_From_Mysql.getString("stock_quantity"));
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }

}
