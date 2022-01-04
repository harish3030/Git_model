import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.time.*;

public class ServiceClient1 implements Runnable {

    private Socket clientSocket;
    private BufferedReader in = null;

    public ServiceClient1(Socket client) {
        this.clientSocket = client;
    }
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    private static FileWriter log_file;
    private static String log_path;

    @Override
    public void run(){
        try {
           /*in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));*/
            //String clientSelection;
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            log_path="log\\"+java.time.LocalDate.now()+".txt";
            System.out.println(log_path);
            log_file=new FileWriter(log_path,true);

           
            int t=dataInputStream.readInt();
            System.out.println(t);
            if(t==1){
                 get_File();
            }
            else{
                  send_File();
            }            
            dataInputStream.close();
            dataOutputStream.close();
            log_file.close();
            

        } catch (IOException ex) {
          System.out.println("excep");
          ex.printStackTrace();
        }
    }

    public void get_File() throws IOException{

        String dir_path="Repo/";
        //System.out.println("Here");
        int sz=dataInputStream.readInt();
        //System.out.println("HHHH"+sz);
        for(int i=0;i<sz;i++){
            receiveFile(dir_path);
        }
    }

    private static void receiveFile(String dir_path) throws IOException{
        int bytes = 0;
        String f=dataInputStream.readUTF();
        String[] split_o=f.split("/");
        String file_name=split_o[split_o.length-1];
        //String f="a.txt";

        File new_file=new File(dir_path+file_name);

        if(new_file.exists()){
            String log="Updating "+file_name;
            System.out.println(log);
            log_action(log);
            log_file.flush();
            //System.out.println(log);
            //compare_file(dataInputStream,new_file);
        }
        else{
            String log="Adding "+file_name;
            log_action(log);
            log_file.flush();
            System.out.println("Adding "+f);
        }
       
        FileOutputStream file_stream = new FileOutputStream(dir_path+file_name);
        long size = dataInputStream.readLong();     // read file size
        byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) !=-1) {
            file_stream.write(buffer,0,bytes);
            size -= bytes;      // read upto file size
        }
        file_stream.close();
    }

    private static void log_action(String log) throws IOException{
        System.out.println(log);
        log_file.write(log +"\n");
       
    }

    public void send_File() throws IOException{
        String dir_path="Repo/";
        List<String>p=textFiles(dir_path);
        dataOutputStream.writeInt(p.size());
        System.out.println(p);
        int cnt=0;
        for(String s:p) {
            sendFile(dir_path + s);
            cnt++;
        }
        System.out.println(cnt+"files pushed");
    }

    public static List<String> textFiles(String directory) {
        List<String> textFiles = new ArrayList<String>();
        File dir = new File(directory);
        for (File file : dir.listFiles()) {
            // if (file.getName().endsWith((".txt"))) {
            textFiles.add(file.getName());
            // }
        }
        return textFiles;
    }

    private static void sendFile(String path) throws IOException{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        // send file size
        dataOutputStream.writeUTF(path);
        dataOutputStream.writeLong(file.length());
        // break file into chunks
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }
}