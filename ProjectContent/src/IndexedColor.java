import java.io.*;
import java.util.*;
import java.lang.*;

public class IndexedColor {
    //the color subclass helps popularity method count the color values
    public static class Color{
        int r;
        int g;
        int b;
        int freq;

        Color(int red, int green, int blue){
            r = red;
            g = green;
            b = blue;
            freq = 1;
        }
    }

    public static void main(String args[]){
        String fileName;
        String algorithm;
        int w = 0;
        int h = 0;
        boolean loopVar = true;
        Scanner keyboard = new Scanner(System.in);


        //read file name from user
        System.out.print("Please provide the file name: ");
        fileName = keyboard.next();

        //read algorithm to be used from user
        System.out.print("Please select algorithm (p for popularity, u for uniform partitioning): ");
        algorithm = keyboard.next();
        //ask the user to reenter if input is not valid
        while ((!algorithm.equals("u")) && (!algorithm.equals("p"))){
            System.out.print("Please enter p or u: ");
            algorithm = keyboard.next();
        }

        //read width
        System.out.print("Please enter the width of the image: ");
        while (loopVar){
            try{
                w = Integer.parseInt(keyboard.next());
                loopVar = false;
            }
            catch(NumberFormatException ee){
                System.out.print("Please enter valid number: ");
            }
        }
        //read height
        loopVar = true;
        System.out.print("Please enter the height of the image: ");
        while (loopVar){
            try{
                h = Integer.parseInt(keyboard.next());
                loopVar = false;
            }
            catch(NumberFormatException ee){
                System.out.print("Please enter valid number: ");
            }
        }
        loopVar = true;


        int[][] arrayOrigin = getArrayOrigin(w, h);


        //prompt number of color for the color table
        int colorNum = 0;
        loopVar = true;
        System.out.print("Please enter the number of color you want for the new image: ");
        while (loopVar){
            try{
                colorNum = Integer.parseInt(keyboard.next());
                loopVar = false;
                if ((colorNum < 1) || (colorNum > 256)){
                    System.out.println("The number must be within [1,256]");
                    continue;
                }
            }
            catch(NumberFormatException ee){
                System.out.print("Please enter valid number: ");
            }
        }

        System.out.println("Computing. Please wait...");


        int[] colorTable = new int[colorNum * 3];
        int[][] indexTable = new int[h][w];

        popularity(arrayOrigin, colorTable, indexTable, w, h, colorNum);


    }

    public static int[][] getArrayOrigin(int w, int h){
        int[][] target = new int[h][3*w];

        //read file
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

        //read file and insert the corresponding pixel value into the array
        for (int i = 0; i < h; i++){
            for (int j = 0; j < 3 * w; j++){
                try{
                    target[i][j] = input.read();
                    if (target[i][j] == -1){
                        //if reader gets a -1, it means that the dimension of the image is entered wrong
                        System.out.println("w/h Error!");
                        System.exit(-1);
                    }
                }
                catch(IOException e4){
                    System.out.println("Read File Error!");
                    System.exit(-1);
                }
            }
        }
        return target;
    }

    //method that makes sure for popularity method whether the new color has appeared in the list
    //if the color exists in the list, the method will return the index of the already existing color
    //else, the method will return -1
    public static int search(ArrayList<Color> list, int r, int g, int b){
        for (int i = 0; i < list.size(); i++){
            if ((list.get(i).r == r) && (list.get(i).g == g) && (list.get(i).b == b)){
                return i;
            }
        }
        return -1;
    }

    //this method sorts the Color ArrayList in descending order using selection sort
    public static void sortInDescending(ArrayList<Color> list){
        for (int i = 0; i < list.size()-1; i++){
            int indexMax = i;
            for (int j = i+1; j < list.size(); j++){
                if (list.get(j).freq > list.get(indexMax).freq){
                    indexMax = j;
                }
            }
            Collections.swap(list, indexMax, i);
        }
    }

    public static void popularity(int[][] arrayOrigin, int[] colorTable, int[][] indexTable, int w, int h, int colorNum){
        ArrayList<Color> colorList = new ArrayList<>();
        //populate colorList with the color values in arrayOrigin and record the frequency of them
        for (int i = 0; i < h; i++){
            for (int j = 0; j < 3 * w; j+=3){
                int index;
                index = search(colorList, arrayOrigin[i][j], arrayOrigin[i][j+1], arrayOrigin[i][j+2]);
                if (index != -1){
                    colorList.get(index).freq++;
                }
                else{
                    Color c = new Color(arrayOrigin[i][j], arrayOrigin[i][j+1], arrayOrigin[i][j+2]);
                    colorList.add(c);
                }
            }
        }
        System.out.println("colorList populated");
        System.out.println("colorList size: " + colorList.size());

        //sort colorList
        sortInDescending(colorList);
        System.out.println("colorList sorted");

        //populate colorTable
        //if colorNum entered by the user is too big, print error message and end program
        if (colorNum > colorList.size()){
            System.out.println("The image has only " + colorList.size() + " colors. The color you entered is too big.");
            System.exit(-1);
        }
        for (int i = 0; i < colorTable.length; i += 3){
            colorTable[i] = colorList.get(i).r;
            colorTable[i+1] = colorList.get(i).g;
            colorTable[i+2] = colorList.get(i).b;
        }
        System.out.println("colorTable populated");

        //populate indexTable
        for (int i = 0; i < h; i++){
            for (int j = 0; j < w; j++){
                int rOrigin = arrayOrigin[i][j*3];
                int gOrigin = arrayOrigin[i][j*3+1];
                int bOrigin = arrayOrigin[i][j*3+2];
                int target = 0;
                double lowest = Math.pow(rOrigin - colorTable[target], 2) + Math.pow(gOrigin - colorTable[target + 1], 2) +
                        Math.pow(bOrigin - colorTable[target + 2], 2);
                //traverse through colorTable to see which color is the closest to the current one in arrayOrigin
                for (int k = 0; k < colorTable.length; k+=3){
                    double newVal = Math.pow(rOrigin - colorTable[k], 2) + Math.pow(gOrigin - colorTable[k + 1], 2) +
                            Math.pow(bOrigin - colorTable[k + 2], 2);
                    if (lowest - newVal > 0.0001){
                        lowest = newVal;
                        target = k;
                    }
                }
                indexTable[i][j] = target;
            }
        }
        System.out.println("indexTable populated");
    }
}