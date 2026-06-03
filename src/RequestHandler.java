import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class RequestHandler {
    private Socket Socket;
    public RequestHandler(Socket Socket){
        this.Socket=Socket;
    }
    public void handle_cilent(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
            BufferedWriter out =new BufferedWriter(new OutputStreamWriter(Socket.getOutputStream()));
            StringBuilder Str=new StringBuilder();
            while(true){
                String Temp=in.readLine();
                if(Temp == null || Temp.isEmpty()){
                    break;
                }
                Str.append(Temp);
                Str.append("\r\n");
            }
            String S=Str.toString();
            System.out.println(S);
            Router rh=new Router(out,S);
            rh.handle_Request();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
