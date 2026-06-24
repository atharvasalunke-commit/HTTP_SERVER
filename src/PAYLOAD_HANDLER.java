import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class PAYLOAD_HANDLER {
    private BufferedReader in;
    PAYLOAD_HANDLER(BufferedReader in){
        this.in=in;
    }
    public int Find_Con_Length(String Cont){
        String[] Conts=Cont.split("\\s+");
        for(int i=0;i<Cont.length();i++){
            if(Conts[i].equals("Content-Length:")){
                int len=Integer.parseInt(Conts[i+1]);
                return len;
            }
        }
        return -1;
    }
    public String Payload_Receiver(int len){
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
               if(Payload.charAt(i)==' '){

               }
               else if(Payload.charAt(i)=='"'){}
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
                   int j=i;
                   while(Payload.charAt(j)!=':'){
                       if(Payload.charAt(j)=='"'){
                           j++;
                           continue;
                       }
                       sb1.append(Payload.charAt(j));
                       j++;
                   }
                   i=j-1;
               }
               else if(flag2){
                   int j=i;
                  while(Payload.charAt(j)!=','&&Payload.charAt(j)!='}'){
                      if(Payload.charAt(j)=='"'){
                          j++;
                          continue;
                      }
                      sb2.append(Payload.charAt(j));
                      j++;
                  }
                  i=j-1;
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
                int j=i;
                while(c[j]!=':'){
                    if(c[j]=='"'){
                        j++;
                        continue;
                    }
                    sb1.append(c[j]);
                    j++;
                }
                i=j-1;
            }
            else if(flag2&&flag3){
                int j=i;
                while(c[j]!=','&&c[j]!='}'){
                    if(c[j]=='"'){
                        j++;
                        continue;
                    }
                    sb2.append(c[j]);
                    j++;
                }
                i=j-1;
            }
        }
    }

}
