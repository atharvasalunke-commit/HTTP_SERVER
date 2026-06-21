import java.io.*;
import java.net.*;
import java.util.concurrent.*;

class Http_Server {
    public static void main(String[] args){
        try {
            ServerSocket Server = new ServerSocket(8080);
            ExecutorService Executor=Executors.newFixedThreadPool(5);
            while(true){
                    Socket Socket = Server.accept();
                    Executor.submit(() -> {
                        try {
                            RequestHandler rH = new RequestHandler(Socket);
                            rH.handle_cilent();
                        } catch (Exception e) {
                            e.printStackTrace(); // Now you will see the hidden crashes!
                        } finally {
                            try { Socket.close(); } catch (IOException e) { e.printStackTrace(); }
                        }
                        try {
                            Socket.close();
                        }
                        catch (IOException e) {
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