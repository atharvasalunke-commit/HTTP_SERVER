package DB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class ConnectionPool {
private final BlockingQueue<Connection>pool;
    public ConnectionPool(String url,String user,String pass,int poolSize){
        pool=new ArrayBlockingQueue<>(poolSize);
        for(int i=0;i<poolSize;i++){
            try{
                Connection conn=DriverManager.getConnection(url,user,pass);
                pool.add(conn);
            }
            catch(SQLException e){
                e.printStackTrace();
                throw new RuntimeException("Failed to connect to database");
            }
        }
    }
    public Connection getConnection()throws InterruptedException{
        return pool.take();
    }
    public void returnConnection(Connection conn){
        if(conn!=null){
            pool.offer(conn);
        }
    }
}
