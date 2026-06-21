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
        StringBuilder Query=new StringBuilder("UPDATE "+table_Name+" SET ");
        ArrayList<String> list=new ArrayList<>();
        PR.Payload_Create(Payload,list);
        DataBase db=new DataBase();
        System.out.println(Payload);
        ArrayList<String> list2=new ArrayList<>();
        ArrayList<String> list3=new ArrayList<>();
        System.out.println(n);
        if(n>1&&mp.containsKey(table_Name)){
            System.out.println("Here");
            for(int i=1;i<list.size();i+=2){
            System.out.println(list.get(i-1));
            System.out.println(list.get(i));
            Query.append(list.get(i-1)).append(" = ? ");
            list3.add(list.get(i));
            if(i+2<list.size()){
                Query.append(",");
            }
        }
        Query.append(" WHERE ");
        if(Requests[1].contains(",")){
            String[] temp=Requests[1].split("=");
            Query.append(temp[0]).append(" IN (");
            String[] temp2=temp[1].split(" , ");
            for(int i=0;i<temp2.length;i++){
                list3.add(temp2[i]);
                Query.append(" ? ");
            }
            Query.append(" ) ");
        }
        else {
            String[] temp=Requests[1].split("=");
            Query.append(temp[0]).append(" = ? ");
            System.out.println(temp[1]);
            list3.add(temp[1]);
        }
        Query.append(" AND is_deleted = FALSE");
        System.out.println(Query);
        String x=db.insert_Values(Query.toString(),list3);
        list2.add(x);
        }
        else if(n==1&&mp.containsKey(table_Name)){
            HashMap<String,Integer>mp2=new HashMap<>();
            db.Insert_Columns_in_map(table_Name,mp2,list2);
            list2=new ArrayList<>();
            StringBuilder Query1=new StringBuilder(Query);
            StringBuilder Where=new StringBuilder();
            int count=0;
            int i=1;
            boolean indicator=false;
            while(i<list.size()){
                System.out.println(list.get(i-1));
                System.out.println(list.get(i));
                if(list.get(i-1).equals("Ends here")||indicator){
                    String[] Where_temp=Where.toString().split("=");
                    Query1.deleteCharAt(Query1.length()-1);
                    Query1.append(" Where ").append(Where_temp[0]).append(" = ? ");
                    list3.add(Where_temp[1]);
                    Query1.append(" AND is_deleted = FALSE");
                    System.out.println(Query1);
                    String x=db.insert_Values(Query1.toString(),list3);
                    Query1=new StringBuilder(Query);
                    list2.add(x);
                    Where=new StringBuilder();
                    count=0;
                    i++;
                    list3=new ArrayList<>();
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
                        list3.add(list.get(i));
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
        String main_Body=create_Main_Body(list2);
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
