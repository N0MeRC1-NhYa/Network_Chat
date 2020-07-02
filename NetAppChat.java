import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NetAppChat {

    private static ArrayList<String> ComputerName;

    public static void main(String[]args){

    }

    private static void CheckLocalNetwork (){
        try{
            Process proc = Runtime.getRuntime().exec("net view");
            InputStream stdout = proc.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout, Charset.forName("cp866")));
            String line = "";
            while ( (line = reader.readLine()) != null){
                String name = "";
                if (!line.isBlank() && line.charAt(0) == '\\'  && line.charAt(1) == '\\') {
                    name = line.substring(2);
                }
                if (!ComputerName.contains(name)) {
                    ComputerName.add(name);
                }
                System.out.println(name);
            }
        } catch (IOException e) {
            System.out.println("Couldn't solve!");
        }
    }
    private static void WaitForMsg(){
        while (!){ // пока не нажата кнопка назад (то есть пока мы не хотим выйти из этого чата)
            while (!) // пока не нажата кнопка отправить
        }
    }
    private static void SendMsg(){
        
    }
}
