import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder {
    public void send(String Response, BufferedWriter out){
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
        // BUG FIX 1: Changed \\r\\n to \r\n to prevent "HTTP/0.9 when not allowed" error
        String Response="HTTP/1.1 200 OK\r\n"+"Content-Type: application/json\r\n" +
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
        sb.append("    \"info\": [\n");

        String Template =
                "      {\n" +
                        "        \"%s\": \"%s\",\n" +
                        "        \"%s\": \"%s\",\n" +
                        "        \"%s\": \"%s\",\n" +
                        "        \"%s\": \"%s\"\n" +
                        "      }";
        for (Map.Entry<String, ArrayList<String>> entry : mp.entrySet()) {
            boolean indicator=false;
            ArrayList<String> list = entry.getValue();
            for (int i = 0; i < list.size(); i += 8) {
                    String item = String.format(Template, list.get(i), list.get(i + 1), list.get(i + 2), list.get(i + 3),list.get(i+4),list.get(i+5),list.get(i+6),list.get(i+7));
                    sb.append(item);

                // Add a comma unless it is the last item
                if (i + 8 <list.size()) {
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
}
