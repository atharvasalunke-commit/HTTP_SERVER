import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class RequestHandler {
    private final Socket Socket;
    private  BufferedReader in;
    private BufferedWriter out;
    public RequestHandler(Socket Socket){
        this.Socket=Socket;
    }
    public void handle_cilent(){
        try {
             in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
             out =new BufferedWriter(new OutputStreamWriter(Socket.getOutputStream()));
            StringBuilder Str=new StringBuilder();
            ArrayList<String>con_len=new ArrayList<>();
            while(true){
                String Temp=in.readLine();
                if(Temp == null || Temp.isEmpty()){
                    break;
                }
                Str.append(Temp);
                Str.append("\r\n");
                con_len.add(Temp);
            }
            String S=Str.toString();
            System.out.println(S);
            Router rh=new Router(in,out,S,con_len);
            rh.handle_Request();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
