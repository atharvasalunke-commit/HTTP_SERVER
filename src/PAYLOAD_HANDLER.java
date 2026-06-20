import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class PAYLOAD_HANDLER {
    private BufferedReader in;
    private int len;
    PAYLOAD_HANDLER(BufferedReader in, int len){
        this.in=in;
        this.len=len;
    }
    public String Payload_Receiver(){
        char[] buffer=new char[len];
        int Total_Char=0;
        while(Total_Char<len){
            try {
                Total_Char+= in.read(buffer, Total_Char, len - Total_Char);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        return new String(buffer);
    }
    public void Payload_Create(String Payload, ArrayList<String> list){
        String[] Payloads;
        boolean flag1=false;
        boolean flag2=false;
        boolean flag3=false;
        StringBuilder sb1=new StringBuilder();
        StringBuilder sb2=new StringBuilder();
        if(Payload.contains("[")) {
             Payloads = Payload.split("[\\[\\]]");
        }
       else{
           for(int i=0;i<Payload.length();i++){
               if(Payload.charAt(i)=='"'){}
               else if(Payload.charAt(i)=='{'){
                   flag1=true;
               }
               else if(Payload.charAt(i)=='}'){
                   list.add(sb2.toString().trim());
                   return;
               }
               else if(Payload.charAt(i)==':'){
                   flag1=false;
                   flag2=true;
                   list.add(sb1.toString().trim());
                   sb1=new StringBuilder();
               }
               else if(Payload.charAt(i)==','){
                   flag1=true;
                   flag2=false;
                   list.add(sb2.toString().trim());
                   sb2=new StringBuilder();
               }
               else if(flag1){
                   sb1.append(Payload.charAt(i));
               }
               else if(flag2){
                   sb2.append(Payload.charAt(i));
               }
           }
           return;
        }
        char[] c=Payloads[1].toCharArray();
        for(int i=0;i<c.length;i++){
            if(c[i]=='"'){

            }
            else if(c[i]==' '){

            }
            else if(c[i]=='{'){
                flag1=true;
                flag3=true;
            }
            else if(c[i]=='}'){
                flag2=false;
                flag3=false;
                list.add(sb2.toString().trim());
                list.add("Ends here");
                sb2=new StringBuilder();
            }
            else if(c[i]==':'){
                flag1=false;
                flag2=true;
                list.add(sb1.toString().trim());
                sb1=new StringBuilder();
            }
            else if(c[i]==','&&flag3){
                flag1=true;
                flag2=false;
                list.add(sb2.toString().trim());
                sb2=new StringBuilder();
            }
            else if(flag1&&flag3){
                sb1.append(c[i]);
            }
            else if(flag2&&flag3){
                sb2.append(c[i]);
            }
        }
    }

}
