import com.sun.net.httpserver.Request;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Router {
    private final BufferedReader in;
    private  final BufferedWriter out;
    private final String Request;
    private final ArrayList<String> con_len;
    Router(BufferedReader in,BufferedWriter out,String Request,ArrayList<String> con_len){
        this.in=in;
        this.out=out;
        this.Request=Request;
        this.con_len=con_len;
    }
    public void handle_Request(){
        String[] Strs=Request.split(" ");

        if(Strs.length > 1 && Strs[0].equals("GET")){
            HashMap<String, ArrayList<String>> mp=new HashMap<>();
            GETCONTROLLER gc=new GETCONTROLLER();
            gc.handle_Get_Request(Strs,mp,out);
        }
        else if(Strs.length>1&&Strs[0].equals("POST")){
            int n=con_len.size();
            String Target_Table=Strs[1].replace("/","");
            String Content_Length=con_len.get(n-1);
            String [] Content=Content_Length.split("[\\s:]+");
            System.out.println(Content[1]);
            int len=Integer.parseInt(Content[1]);
            POSTCONTROLLER pc=new POSTCONTROLLER(in,Target_Table);
          String Response=pc.Post_Handler(len);
          ResponseBuilder rb=new ResponseBuilder();;
          rb.send(Response,out);
        }

    }

}
