import java.io.*;

public class Test {
    public static void main(String args[]){
        InputStream is = null;
        try{
            is = new FileInputStream("Addie.raw");
            // create data input stream
        }
        catch (FileNotFoundException e){
            System.out.println("File not found.");
            System.exit(-1);
        }
        DataInputStream input = new DataInputStream(is);
        try{
            System.out.println(input.read());
            System.out.println(input.read());
            System.out.println(input.read());
            System.out.println(input.read());
            System.out.println(input.read());
        }
        catch(IOException e2){
            System.exit(1);
        }
        System.exit(1);
    }
}