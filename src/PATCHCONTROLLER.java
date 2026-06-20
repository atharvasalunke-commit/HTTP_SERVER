import DB.DataBase;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;

public class PATCHCONTROLLER {
    private String Payload;
    private BufferedReader in;
    private int len;
    public PATCHCONTROLLER(BufferedReader in,int len)
    {
        this.in=in;
        this.len=len;
    }
    public String Patch_Handler(String[] Requests ,HashMap<String,Integer>mp){
        PAYLOAD_HANDLER PR=new PAYLOAD_HANDLER(in,len);
        Payload=PR.Payload_Receiver();
        int n=Requests.length;
        String table_Name=Requests[0].replace("/","");
        String Query="UPDATE "+table_Name+" SET ";
        ArrayList<String> list=new ArrayList<>();
        PR.Payload_Create(Payload,list);
        DataBase db=new DataBase();
        System.out.println(Payload);
        ArrayList<String> list3=new ArrayList<>();
        System.out.println(n);
        if(n>1&&mp.containsKey(table_Name)){
            System.out.println("Here");
            for(int i=1;i<list.size();i+=2){
            System.out.print(list.get(i-1));
            System.out.print(list.get(i));
            Query+=list.get(i-1);
            Query+=" = ";
            Query+="'"+list.get(i)+"'";
            if(i+2<list.size()){
                Query+=",";
            }
        }
        Query+=" WHERE ";
        if(Requests[1].contains(",")){
            String[] temp=Requests[1].split("=");
            Query+=temp[0]+" IN ("+temp[1]+")";
        }
        else {
            Query += Requests[1];
        }
        Query+=" AND is_deleted = FALSE";
        String x=db.insert_Values(Query);
        System.out.println(Query);
        list3.add(x);
        }
        else if(n==1&&mp.containsKey(table_Name)){
            HashMap<String,Integer>mp2=new HashMap<>();
            ArrayList<String> list2=new ArrayList<>();
            db.Insert_Columns_in_map(table_Name,mp2,list2);
            StringBuilder Query1=new StringBuilder(Query);
            StringBuilder Where=new StringBuilder();
            StringBuilder Value=new StringBuilder();
            int count=0;
            int i=1;
            boolean indicator=false;
            while(i<list.size()){
                System.out.println(list.get(i-1));
                System.out.println(list.get(i));
                if(list.get(i-1).equals("Ends here")||indicator){
                    Query1.append(Value).append(" WHERE ");
                    Query1.append(Where);
                    Query1.append(" AND is_deleted = FALSE");
                    String x=db.insert_Values(Query1.toString());
                    System.out.println(Query1);
                    Query1=new StringBuilder(Query);
                    list3.add(x);
                    Where=new StringBuilder();
                    Value=new StringBuilder();
                    count=0;
                    i++;
                    continue;
                }
                else if(mp2.containsKey(list.get(i-1))){
                    if(count%2==0) {
                        Where.append(list.get(i - 1)).append(" = ").append(list.get(i));
                    }
                    else{
                        Value.append(list.get(i-1)).append(" = ").append("'").append(list.get(i)).append("'");
                    }
                    count++;
                }
                if(i+2>=list.size()){
                    indicator=true;
                    i++;
                }
                else {
                    i += 2;
                }
            }
        }
        String main_Body=create_Main_Body(list3);
        return main_Body;
    }
    public String create_Main_Body(ArrayList<String> list){
        StringBuilder body=new StringBuilder("{");
        for(int i=0;i<list.size();i++){
            body.append("\n").append(list.get(i));
        }
        body.append("}");
        return body.toString();
    }
}
