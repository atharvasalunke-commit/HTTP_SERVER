import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.util.concurrent.*;
import DB.ConnectionPool;
class Http_Server {
    public static void main(String[] args){
        try {
            ServerSocket Server = new ServerSocket(8080);
            ExecutorService Executor=Executors.newFixedThreadPool(5);
            BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter url,username,password");
            String url=input.readLine();
            String username=input.readLine();
            String pass=input.readLine();
            ConnectionPool CL=new ConnectionPool(url,username,pass,5);
            while(true){
                Socket Socket = Server.accept();
                    Executor.submit(() -> {
                        try {
                            Connection conn = CL.getConnection();
                            RequestHandler rH = new RequestHandler(Socket,conn);
                            try {
                                rH.handle_cilent();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                CL.returnConnection(conn);
                                Socket.close();
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }

                    });


            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}