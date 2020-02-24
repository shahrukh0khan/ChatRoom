package ChatRoom;

import java.io.File;
import java.io.IOException;

public class Coordinator {

   public static File contactlist() throws IOException {
       File file = new File("/Users/abdullahzulfiqar/NetBeansProjects/ChatRoom/src/contactlist.txt");
       if (file.createNewFile()) {
           System.out.println("File is created!");
       } else {
           System.out.println("File already exists.");
       }
       return file;
   }

}
