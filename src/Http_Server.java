import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import DB.DataBase;

class Http_Server {
    public static void main(String[] args){
        try {
            ServerSocket Server = new ServerSocket(8080);
            ExecutorService Executor=Executors.newFixedThreadPool(5);
            while(true){
                    Socket Socket = Server.accept();
                    Executor.submit(() -> {
                        RequestHandler rH = new RequestHandler(Socket);
                        rH.handle_cilent();
                        try {
                            Socket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });


            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}