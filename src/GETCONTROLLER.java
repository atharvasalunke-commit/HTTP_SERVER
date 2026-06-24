import DB.DataBase;

import java.io.BufferedWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
public class GETCONTROLLER {
    private final Connection conn;
    public GETCONTROLLER(Connection conn) {
        this.conn = conn;
    }
    public void handle_Get_Request(String[] Strs, BufferedWriter out,  HashMap<String,Integer>mp1) {
         HashMap<String,Integer>map=new HashMap<>();
        ResponseBuilder rb = new ResponseBuilder();
        String[] Requests = Strs[1].split("\\?");
        ArrayList<String> list = new ArrayList<>();
        int n = Requests.length;
        StringBuilder Query = new StringBuilder("SELECT * FROM ");
        String table_name = Requests[0].replace("/", "");
        ArrayList<String> list2 = new ArrayList<>();
        DataBase db = new DataBase(conn);
        if (mp1.containsKey(table_name)) {
            db.Insert_Columns_in_map(table_name, map, list2);
        } else {
            rb.send("HTTP/1.1 400 Bad Request\r\n\r\n", out);
            return;
        }
        ArrayList<String> list3 = new ArrayList<>();
        if (n == 2 && mp1.containsKey(table_name)) {
            Query.append(table_name).append(" ");
            String[] temp = Requests[1].split("&");
            int x = temp.length;
            int count = 0;
            int count2 = 0;
            for (int i = 0; i < x; i++) {
                String[] temp2 = temp[i].split("=");
                if (temp2.length < 2) continue;
                temp2[1] = temp2[1].replace("%20", " ");
                if (map.containsKey(temp2[0])) count++;
                    else {
                    count2++;
                    continue;
                }
                if (count == 1) {
                    Query.append("WHERE ").append(temp2[0]).append("= ? ");
                    list3.add(temp2[1]);
                } else if (count > 1) {
                    Query.append(" AND ").append(temp2[0]).append("= ? ");
                    list3.add(temp2[1]);
                }
            }
            if (count2 == x) {
                rb.send("HTTP/1.1 404 Not Found\r\n\r\n", out);
                return;
            }
        }
        else if (mp1.containsKey(table_name)) {
                Query.append(table_name);
        }
            int y = db.read_Data(list, list2, Query.toString(), list3);
            StringBuilder Status_Code = new StringBuilder();
            if (y == 1) {
                Status_Code.append("200 OK");
            } else if (y == -1) {
                Status_Code.append("400 Bad Request");
            } else if (y == 0) {
                Status_Code.append("404 Not Found");
            }
            String z = table_name+"/";
            String categoryParam = (Requests.length > 1) ? z + Requests[1] : table_name;
            int cols = list2.size() - 1;
            String main_Body = rb.create_main_Body(list, categoryParam, cols);
            String Response = rb.Response(main_Body, Status_Code.toString());
            rb.send(Response, out);
        }
    }

