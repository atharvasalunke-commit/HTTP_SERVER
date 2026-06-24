import DB.DataBase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class PATCHCONTROLLER {
    private String Payload;
    private BufferedReader in;
    private int len;
    private String Request;
    private final Connection conn;
    private int rows_Updated;
    public PATCHCONTROLLER(BufferedReader in, String Request, Connection conn)
    {
        this.conn=conn;
        this.Request=Request;
        this.in=in;
    }
    public void Patch_Handler(String[] Requests , HashMap<String,Integer>mp, BufferedWriter out){
        ResponseBuilder rb=new ResponseBuilder();
        PAYLOAD_HANDLER PR=new PAYLOAD_HANDLER(in);
        this.len=PR.Find_Con_Length(Request);
        Payload=PR.Payload_Receiver(len);
        int n=Requests.length;
        String table_Name=Requests[0].replace("/","");
        StringBuilder Query=new StringBuilder("UPDATE "+table_Name+" SET ");
        ArrayList<String> list=new ArrayList<>();
        PR.Payload_Create(Payload,list);
        DataBase db=new DataBase(conn);
        System.out.println(Payload);
        StringBuilder Status_Code=new StringBuilder("200 OK");
        StringBuilder Body=new StringBuilder();
        ArrayList<String> list2=new ArrayList<>();
        boolean flag=false;
        if(n>1&&mp.containsKey(table_Name)){
            for(int i=1;i<list.size();i+=2){
            Query.append(list.get(i-1)).append(" = ? ");
            list2.add(list.get(i));
            if(i+2<list.size()){
                Query.append(",");
            }
        }
        Query.append(" WHERE ");
        if(Requests[1].contains(",")){
            String[] temp=Requests[1].split("=");
            Query.append(temp[0]).append(" IN (");
            String[] temp2=temp[1].split(",");
            for(int i=0;i<temp2.length;i++){
                list2.add(temp2[i]);
                Query.append(" ? ");
                if(i+1<temp2.length){
                    Query.append(",");
                }
            }
            Query.append(") ");
        }
        else {
            String[] temp=Requests[1].split("=");
            Query.append(temp[0]).append(" = ? ");
            list2.add(temp[1]);
        }
        Query.append(" AND is_deleted = FALSE");
        System.out.println(Query);
        int rows_Updated=db.insert_Values(Query.toString(),list2);
      if(rows_Updated>=1){
          Body.append(rows_Updated).append("rows Updated");
      }
       else if(rows_Updated==0){
            Status_Code.append("404 Not Found");
            Body.append("Could not find the rows to update");
        }
        else if(rows_Updated==-1){
            Status_Code.append("400 Bad Request");
            Body.append("Mysql  throws an error ");
        }
        }
        else if(n==1&&mp.containsKey(table_Name)){
            HashMap<String,Integer>mp2=new HashMap<>();
            db.Insert_Columns_in_map(table_Name,mp2,new ArrayList<>());
            StringBuilder Query1=new StringBuilder(Query);
            StringBuilder Where=new StringBuilder();
            int count=0;
            int i=1;
            boolean indicator=false;

            while(i<list.size()){
                if(list.get(i-1).equals("Ends here")||indicator){
                    String[] Where_temp=Where.toString().split("=");
                    Query1.deleteCharAt(Query1.length()-1);
                    Query1.append(" Where ").append(Where_temp[0]).append(" = ? ");
                    list2.add(Where_temp[1]);
                   Query1.append(" AND is_deleted = FALSE");
                    System.out.println(Query1);
                    int row_Updated=db.insert_Values(Query1.toString(),list2);
                    if(row_Updated>=1){
                        this.rows_Updated+=row_Updated;
                        flag=true;
                    }
                    else if(row_Updated==0){
                        Status_Code=new StringBuilder("404 Not Found");
                        Body.append("Could not find the rows to update");
                        flag=false;
                        break;
                    }
                    else if(row_Updated==-1){
                        Status_Code=new StringBuilder("400 Bad Request");
                        Body.append("Mysql throws an error");
                       flag=false;
                        break;
                    }
                    Query1=new StringBuilder(Query);
                    Where=new StringBuilder();
                    count=0;
                    i++;
                    list2=new ArrayList<>();
                    if(indicator==true){
                        break;
                    }
                    continue;
                }
                else if(mp2.containsKey(list.get(i-1))){
                    if(count==0) {
                        Where.append(list.get(i - 1)).append("=").append(list.get(i));
                    }
                    else{
                        Query1.append(list.get(i-1)).append(" = ? ").append(",");
                        list2.add(list.get(i));
                    }
                    count++;
                }
                if(i+2>=list.size()){
                    indicator=true;
                }
                else {
                    i += 2;
                }
            }

        }
        else{
            rb.send("400 Bad Request",out);
        }
        StringBuilder main_Body = new StringBuilder();
        if(flag) {
            Body.append(rows_Updated).append("rows Updated");
        }
        main_Body.append("{\"message\":\"").append(Body).append("\"}");
        String Response=rb.Response(main_Body.toString(),Status_Code.toString());
        rb.send(Response,out);
    }

}
