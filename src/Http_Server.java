import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import DB.DataBase;
class Http_Server {
    public  void send(String Response,BufferedWriter out){
        try {
            out.write(Response);
            out.flush();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public String Response(String main_Body){
        String len=main_Body.length()+"";
    String Response="HTTP/1.1 200 OK\\r\\n"+"Content-Type: application/json\r\n" +
                "Content-Length: "+len + "\r\n" +
                "Connection: close\r\n\r\n"+
                main_Body;
    return Response;
    }
    public String create_main_Body(HashMap<String, ArrayList<String>> mp, String category) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        sb.append("  {\n");

        sb.append("    \"category\": \"").append(category).append("\",\n");
        sb.append("    \"status\": \"Available\",\n");
        sb.append("    \"items\": [\n");

        String Template =
                "      {\n" +
                        "        \"id\": %s,\n" +
                        "        \"product_name\": \"%s\",\n" +
                        "        \"price\": %s,\n" +
                        "        \"stock_quantity\": %s\n" +
                        "      }";
        for (Map.Entry<String, ArrayList<String>> entry : mp.entrySet()) {
            ArrayList<String> list = entry.getValue();

            for (int i = 0; i < list.size(); i += 4) {
                String item = String.format(Template, list.get(i), list.get(i+1), list.get(i+2), list.get(i+3));
                sb.append(item);

                // Add a comma unless it is the last item
                if (i + 4 < list.size()) {
                    sb.append(",\n");
                } else {
                    sb.append("\n");
                }
            }
        }

        // 5. Close the JSON
        sb.append("    ]\n");
        sb.append("  }\n");
        sb.append("]");

        return sb.toString();
    }
    public  void handle_Get_Request(String[] Strs, HashMap<String,ArrayList<String>>mp,BufferedWriter out){
        String request_Type=Strs[1];
        String[] Requests=request_Type.split("\\?");

        ArrayList<String>list=new ArrayList<>();

            int n=Requests.length;
            String query="SELECT * FROM"+" ";
            if(n==2){
                query+=Requests[1];
            }
            else{
                query+=Requests[1]+"WHERE PRODUCT_NAME ="+Requests[2];
            }
        DataBase db=new DataBase(query);
        db.read_Data(list);
        mp.put(Requests[0],list);
        String main_Body=create_main_Body(mp,Requests[1]);
        String Response=Response(main_Body);
        send(Response,out);
    }

    public  void handle_cilent(Socket Socket){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
            BufferedWriter out =new BufferedWriter(new OutputStreamWriter(Socket.getOutputStream()));
            StringBuilder Str=new StringBuilder();
            while(true){
                String Temp=in.readLine();
                if(Temp.isEmpty()){
                    break;
                }
                Str.append(Temp);
                Str.append("\r\n");
            }
            String S=Str.toString();
            System.out.println(S);
            String[] Strs=S.split(" ");
            if(Strs[0].equals("GET")){
                HashMap<String,ArrayList<String>>mp=new HashMap<>();
                handle_Get_Request(Strs,mp,out);
            }
            Socket.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        Http_Server htp=new Http_Server();
        try {
            ServerSocket Server = new ServerSocket(8080);
           ExecutorService Executor=Executors.newFixedThreadPool(5);
              while(true){
                  Socket Socket=Server.accept();
                  Executor.submit(()-> {
                      htp.handle_cilent(Socket);
                  });
              }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}