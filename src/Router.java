import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Router {
    private  BufferedWriter out;
    private String Request;
    Router(BufferedWriter out,String Request){
        this.out=out;
        this.Request=Request;
    }
    public void handle_Request(){
        String[] Strs=Request.split(" ");

        if(Strs.length > 1 && Strs[0].equals("GET")){
            HashMap<String, ArrayList<String>> mp=new HashMap<>();
            GETCONTROLLER gc=new GETCONTROLLER();
            gc.handle_Get_Request(Strs,mp,out);
        }
    }

}
