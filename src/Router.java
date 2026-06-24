import java.io.*;
import java.sql.Connection;
import java.util.HashMap;
public class Router {
    private final BufferedReader in;
    private  final BufferedWriter out;
    private final String Request;
    private Connection conn;
    private  HashMap<String,Integer>mp1=new HashMap<>();
    Router(BufferedReader in,BufferedWriter out,String Request,Connection conn){
        this.in=in;
        this.out=out;
        this.Request=Request;
        this.conn=conn;
    }
    public void handle_Request(){
        mp1.put("products",1);
        mp1.put("users",2);
        mp1.put("orders",3);
        mp1.put("order_items",4);
        String[] Strs=Request.split(" ");

        if(Strs.length > 1 && Strs[0].equals("GET")){
            GETCONTROLLER gc=new GETCONTROLLER(conn);
            gc.handle_Get_Request(Strs,out,mp1);
        }
        else if(Strs.length>1&&Strs[0].equals("POST")){
            String Target_Table=Strs[1].replace("/","");
            POSTCONTROLLER pc=new POSTCONTROLLER(in,Target_Table,Request,conn);
            pc.Post_Handler(out,mp1);
        }
        else if(Strs.length>1&&Strs[0].equals("DELETE")){
            String[] temp=Strs[1].split("\\?");
            String target_Table=temp[0].replace("/","");
            DELETECONTROLLER DE=new DELETECONTROLLER(target_Table,temp,conn,mp1);
            DE.Delete_Row(out);
        }
        else if(Strs.length>1&&Strs[0].equals("PATCH")){
            PATCHCONTROLLER PCC=new PATCHCONTROLLER(in,Request,conn);
            String[] temp=Strs[1].split("\\?");
            PCC.Patch_Handler(temp,mp1,out);
        }
    }

}
