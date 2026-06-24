package DB;
import java.sql.*;
import java.util.*;
public class DataBase {
    private  String sql_Query;
    private int rows_Effect;
    private final Connection conn;
    private ResultSetMetaData metaData;
    public DataBase(Connection conn){
        this.conn=conn;
    }
    public ResultSetMetaData get_MeteData(){
        return this.metaData;
    }
    public void Insert_Columns_in_map(String table_Name,HashMap<String,Integer> map,ArrayList<String> list2){
        String Query="Select * from "+ table_Name+" LIMIT 0";
        try(
            Statement stmt=conn.createStatement();
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
    public int insert_Values(String Query,ArrayList<String> list) {
        this.sql_Query = Query;
        try {
            PreparedStatement pstmt=conn.prepareStatement(sql_Query);
            for(int i=0;i<list.size();i++){
                pstmt.setObject(i+1,list.get(i));
            }
            System.out.println(pstmt.toString());
            rows_Effect = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return  rows_Effect;
    }
    public int read_Data(ArrayList<String>Data,ArrayList<String>list2,String Query,ArrayList<String> list3){
        this.sql_Query=Query;
        try{
            PreparedStatement pstmt=conn.prepareStatement(sql_Query);
            for(int i=0;i<list3.size();i++){
                pstmt.setString(i+1,list3.get(i));
            }
            ResultSet get_Data_From_Mysql=pstmt.executeQuery();
            ResultSetMetaData liveMetaData = get_Data_From_Mysql.getMetaData();
            boolean indicator = true;
            int n=liveMetaData.getColumnCount();
                while (get_Data_From_Mysql.next()) {
                    if(get_Data_From_Mysql.getString("is_deleted").equals("1")){
                        continue;
                    }
                    for(int i=1;i<n;i++){
                        Data.add(list2.get(i-1));
                        Data.add(get_Data_From_Mysql.getString(i));
                    }
                    indicator=false;
                }
            if(indicator){
               return 0;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

}