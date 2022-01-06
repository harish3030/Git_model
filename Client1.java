import java.io.*;                                                                                          
import java.net.Socket;                                                                                    
import java.util.*;                                                                                        
import java.util.Scanner;                                                                                  
                                  

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Client1 {                                                                                      
    private static DataOutputStream dataOutputStream = null;                                               
    private static DataInputStream dataInputStream = null;     
    private static File file;
    private static FileInputStream fileInputStream;                                            
                                                                                                           
    public static void main(String[] args) {                                                               
        try(Socket socket = new Socket("localhost",2000)) {                                                
            dataInputStream = new DataInputStream(socket.getInputStream());                                
            dataOutputStream = new DataOutputStream(socket.getOutputStream());                             
           
            /*sendFile("path/to/file1.pdf");                                                               
            sendFile("path/to/file2.pdf");                                                                 
            */                                                                                             
            Scanner input = new Scanner(System.in);                                                        
            
            System.out.println("Enter: ");                                                                 
            System.out.println(">> ");                                                                       
            String cmd = input.nextLine();                                                                     

            if (cmd.equals("git push")){    
                dataOutputStream.writeInt(1);  
                
                //os.println("1");                                                             
                push_file();                                                                               
            }                                                                                              
            else if(cmd.equals("git pull")){
                   dataOutputStream.writeInt(2); 
                   
                   // os.println("2");                                                              
                    pull_file();                                                                           
            }                                                                                              
            else{                                                                                          
                   System.out.println("Invalid command");                                                  
            }            
            //ProjectGUI print = new ProjectGUI();
            //print.showFiles();
            input.close();
            dataOutputStream.close();
            dataInputStream.close();                                                                                  
                                                                                                
    }catch(Exception e){}                    
    
    
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
         file = new File(path);     
                                                                
         fileInputStream = new FileInputStream(file);                                       
        
        // send file size       
        System.out.println(path);    
                                         
        try{
        dataOutputStream.writeUTF(path);  
                                                                        
        dataOutputStream.writeLong(file.length());    
        }
        catch(Exception e ){
            e.printStackTrace();
        }
                   
                                                                    
        byte[] buffer = new byte[4*1024];                                                                  
        while ((bytes=fileInputStream.read(buffer))!=-1){                                                  
            dataOutputStream.write(buffer,0,bytes);                                                        
            dataOutputStream.flush();                                                                      
        }                                                                                                  
        fileInputStream.close();                                                                           
    }                                                                                                      
                                                                                                           
    private static void push_file() throws IOException {                                                                      
        String dir_path="commited_files/";                                                                       
        List<String>p=textFiles(dir_path);                                                             
        dataOutputStream.writeInt(p.size());

        
        System.out.println(p.size());                                                                         
        int cnt=0;                                                                                     
        for(String s:p){                                                                               
            sendFile(dir_path+s);      
              
                                                    
            System.out.println("Pushing "+s+"...");                                                    
            cnt++;                                                                                     
        }                                                                                              
        System.out.println("Pushed "+cnt+" files to remote repo"); 
     }                                                                        
    
    private static void pull_file() throws IOException {
    
        String dir_path="pulled_files/";
        int sz=dataInputStream.readInt();
        for(int i=0;i<sz;i++){
            receiveFile(dir_path);                                                                     
        }
    }
    
    private static void receiveFile(String dir_path) throws IOException{                                     
            int bytes = 0;                                                                                     
            String f=dataInputStream.readUTF();                                                                
            String[] split_o=f.split("/");                                                                     
            String file_name=split_o[split_o.length-1];                                                        
    
            File new_file=new File(dir_path+file_name);                                                                                                                                                             
            FileOutputStream file_stream = new FileOutputStream(dir_path+file_name);                           
            long size = dataInputStream.readLong();     // read file size                                      
            byte[] buffer = new byte[4*1024];                                                                  
            while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) !=
     -1) {                                                                                                     
                file_stream.write(buffer,0,bytes);                                                             
                size -= bytes;      // read upto file size                                                     
            }                                                                                                  
            file_stream.close();                                                                               
        }
}           
