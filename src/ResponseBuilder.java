import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

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

    public String Response(String main_Body,String Status_Code){
        String len=main_Body.length()+"";
        String Response="HTTP/1.1 "+Status_Code +"\r\n"+"Content-Type: application/json\r\n" +
                "Content-Length: "+len + "\r\n" +
                "Connection: close\r\n\r\n"+
                main_Body;
        return Response;
    }

    public String create_main_Body( ArrayList<String>list, String category,int cols) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        sb.append("  {\n");

        sb.append("    \"category\": \"").append(category).append("\",\n");
        sb.append("    \"status\": \"Available\",\n");
        sb.append("    \"info\": [\n");
            for (int i = 0; i < list.size(); i += cols*2) {
                sb.append("      {\n");
                for(int j=0;j<cols*2;j+=2) {
                    sb.append("        \"").append(list.get(i+j)).append("\": \"").append(list.get(i+j+1))
                            .append("\"");
                    if (j+2 < cols*2) {
                        sb.append(",\n");
                    } else {
                        sb.append("\n");
                    }
                }
                sb.append("      }");
                if (i+(cols*2) < list.size()) {
                    sb.append(",\n");
                } else {
                    sb.append("\n");
                }
            }

        sb.append("    ]\n");
        sb.append("  }\n");
        sb.append("]");

        return sb.toString();
    }
}
