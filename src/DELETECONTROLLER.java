import DB.DataBase;

import java.util.ArrayList;
import java.util.HashMap;

public class DELETECONTROLLER {
    private String table_Name;
    private String[]Requests;
    DELETECONTROLLER(String table_Name,String[]Requests){
        this.table_Name=table_Name;
        this.Requests=Requests;
    }
    public String Delete_Row() {
        DataBase db = new DataBase();
        HashMap<String,Integer> mp=new HashMap<>();
        ArrayList<String> list=new ArrayList<>();
        db.Insert_Columns_in_map(table_Name,mp,list);
        String[] Rows=Requests[1].split("&");
        StringBuilder Query=new StringBuilder("UPDATE "+table_Name+" SET is_deleted=TRUE WHERE ");
        System.out.println(Rows.length);
        ArrayList<String> list2=new ArrayList<>();
        if(Rows.length>=2){
            for(int i=0;i<Rows.length;i++){
                String[] temp1=Rows[i].split("=");
                if(mp.containsKey(temp1[0])){
                    if(i>1){
                        Query.append(" AND ").append(temp1[0]).append(" = ? " );
                    }
                    else{
                        Query.append(temp1[0]).append(" = ? ");
                    }
                    list2.add(temp1[1]);
                }
            }
        }
        else if(Rows.length==1&&Rows[0].contains(",")){
            String[] temp1=Rows[0].split("[=,]");
            System.out.println(temp1[0]);
            System.out.println(temp1[1]);
           if(mp.containsKey(temp1[0])) {
               Query.append(temp1[0]).append(" in ").append("(");
               for (int i = 1; i < temp1.length; i++) {
                   list2.add(temp1[i]);
                Query.append(" ? ");
                if(i+1<temp1.length){
                    Query.append(",");
                }
               }
               Query.append(")");
           }
        }
        else if(Rows.length==1){
            Rows[0]=Rows[0].replace("%20"," ");
                String[] temp1=Rows[0].split("=");
                if(mp.containsKey(temp1[0])) {
                    Query.append(temp1[0]).append(" = ? ");
                    list2.add(temp1[1]);
                }
        }
        else{
            System.out.println("ErrorInDeletePanel");
        }
        System.out.println(Query);
        String sql_Query = Query.toString();
        String body=db.insert_Values(sql_Query,list2);
        StringBuilder main_Body=new StringBuilder();
        main_Body.append("{").append(body).append("}");
        return main_Body.toString();
    }

}
