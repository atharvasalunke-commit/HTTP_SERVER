import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.util.ArrayList;

public class RequestHandler {
    private final Socket Socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Connection conn;

    public RequestHandler(Socket Socket, Connection conn) {
        this.Socket = Socket;
        this.conn = conn;
    }

    public void handle_cilent() {
        try {
            in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(Socket.getOutputStream()));
            StringBuilder Str = new StringBuilder();
            ArrayList<String> Message_Header=new ArrayList<>();
            while (true) {
                String Temp = in.readLine();
                if (Temp == null || Temp.isEmpty()) {
                    break;
                }
                Str.append(Temp);
                Str.append("\n");
                Message_Header.add(Temp);
            }
            String Request = Str.toString();
            System.out.println(Request);
            Router rh = new Router(in, out, Request, conn);
            rh.handle_Request();
        } catch (Exception e) {
            e.printStackTrace();
            ResponseBuilder rb = new ResponseBuilder();
            rb.send("HTTP/1.1 500 Internal Server Error \r\n", out);
        }
    }
}
