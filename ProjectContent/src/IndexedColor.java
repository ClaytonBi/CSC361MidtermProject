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
        int exp = 0;
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


        //run algorithm
        if (algorithm.equals("p")){
            popularity(arrayOrigin, colorTable, indexTable, w, h, colorNum);
        }
        else{
            partitioning(colorNum, arrayOrigin, colorTable, indexTable, h, w);
        }


//        //print colorTable
//        for (int i = 0; i < colorTable.length; i++){
//            System.out.print(colorTable[i] + " ");
//        }
//        System.out.println();
//        System.out.println();
//        //print indexTable
//        for (int i = 0; i < indexTable.length; i++){
//            for (int j = 0; j < indexTable[0].length; j++){
//                System.out.print(indexTable[i][j]+" ");
//            }
//            System.out.println();
//        }

    }

    public static int[][] getArrayOrigin(int w, int h){
        int[][] target = new int[h][3*w];

        //read file
        InputStream is = null;
        try{
            is = new FileInputStream("Ian.raw");
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

        //create colorTable based on colorNum
        colorTable = new int[colorNum * 3];

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

    //this method calculates variance
    public static double variance(int x, int y, int z){
        double mean = (x + y + z)/3.0;
        double v = Math.pow(x - mean, 2) + Math.pow(y - mean, 2) + Math.pow(z - mean, 2);
        return v;
    }

    public static void partitioning(int colorNum, int[][] arrayOrigin, int[] colorTable, int[][] indexTable, int h, int w){
        //decide how to partition the color space
        //rCut refers to the number of equal sub-parts we choose to divide the r axis into, same for gCut and bCut
        int rCut = 1;
        int gCut = 1;
        int bCut = 1;
        //construct a 5*5*5 array called splitOpt, with each axis corresponding to r/g/b axis in color space; for each axis,
        //index x refers to cutting the corresponding axis in color space into 2^x even slices; each position in splitOpt contains
        //the option of total number of blocks after partition
        int[][][] splitOpt = new int[5][5][5];
        //populate 3d array
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                for (int v = 0; v < 5; v++){
                    splitOpt[i][j][v] = (int)Math.round(Math.pow(2,i) * Math.pow(2,j) * Math.pow(2,v));
                }
            }
        }
        //traverse through the 3d array and find the most suitable partition for colorNum
        //requirement: the number of blocks after partition should be as close as it can to colorNum; it should be below
        //or equal to color num; the variance of the three slices of each axis should be as small as possible
        int diff = (int)Math.round(Math.pow(2,4) * Math.pow(2,4) * Math.pow(2,4));
        double var = 16.0 * 3;
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                for (int v = 0; v < 5; v++){
                    if (colorNum >= splitOpt[i][j][v]){
                        boolean change = false;
                        //if the new difference is smaller than the previous difference, update cutting strategy
                        if (colorNum - splitOpt[i][j][v] < diff){
                            change = true;
                        }
                        //if the new difference is equal to the previous difference, the slicing numbers of three axis with
                        //smaller variance are prioritized
                        else if (colorNum - splitOpt[i][j][v] == diff){
                            if (var - variance(i, j, v) > 0.0001){
                                change = true;
                            }
                        }
                        if (change){
//                            System.out.println("SWITCH!"+i+j+v+splitOpt[i][j][v] + " "+colorNum);
                            diff = colorNum - splitOpt[i][j][v];
//                            System.out.println(diff);
                            var = variance(i, j, v);
//                            System.out.println(var);
                            rCut = (int)Math.round(Math.pow(2,i));
                            gCut = (int)Math.round(Math.pow(2,j));
                            bCut = (int)Math.round(Math.pow(2,v));
                        }
                    }
                }
            }
        }

        System.out.println("Partitioning method choice: r slice * " + rCut + ", g slice * " + gCut + ", b slice * " + bCut);

//        System.out.println(rCut);
//        System.out.println(gCut);
//        System.out.println(bCut);

        //determine the subspace containing the existing colors
        int rMin = 255;
        int rMax = 0;
        int gMin = 255;
        int gMax = 0;
        int bMin = 255;
        int bMax = 0;
        for (int i = 0; i < arrayOrigin.length; i++){
            for (int j = 0; j < arrayOrigin[0].length; j+=3){
                if (arrayOrigin[i][j] < rMin){
                    rMin = arrayOrigin[i][j];
                }
                if (arrayOrigin[i][j] > rMax){
                    rMax = arrayOrigin[i][j];
                }
                if (arrayOrigin[i][j+1] < gMin){
                    gMin = arrayOrigin[i][j+1];
                }
                if (arrayOrigin[i][j+1] > gMax){
                    gMax = arrayOrigin[i][j+1];
                }
                if (arrayOrigin[i][j+2] < bMin){
                    bMin = arrayOrigin[i][j+2];
                }
                if (arrayOrigin[i][j+2] > bMax){
                    bMax = arrayOrigin[i][j+2];
                }
            }
        }

        System.out.println("Sub-space of existing colors determined");

        //populate colorTable
        int[] rValue = new int[rCut];
        int[] gValue = new int[gCut];
        int[] bValue = new int[bCut];
        int rStep = (rMax - rMin)/rCut;
        int gStep = (gMax - gMin)/gCut;
        int bStep = (bMax - bMin)/bCut;
        int k = 0;
        //populate the color value for r/g/b in separate arrays
        for (int i = 0; i < rMax; i += rStep + 1){
            if (k != rCut - 1){
                rValue[k] = (i + i + rStep)/2;
                k++;
            }
            else{
                rValue[k] = (i + rMax)/2;
            }
        }
        k = 0;
        for (int i = 0; i < gMax; i += gStep + 1){
            if (k != gCut - 1){
                gValue[k] = (i + i + gStep)/2;
                k++;
            }
            else{
                gValue[k] = (i + gMax)/2;
            }
        }
        for (int i = 0; i < bMax; i += bStep + 1){
            if (k != bCut - 1){
                bValue[k] = (i + i + bStep)/2;
                k++;
            }
            else{
                bValue[k] = (i + bMax)/2;
            }
        }
//        for (int i = 0; i < rValue.length; i++){
//            System.out.println(rValue[i]);
//        }
        //populate colorTable
        k = 0;
        for (int i = 0; i < rCut; i++){
            for (int j = 0; j < gCut; j++){
                for (int v = 0; v < bCut; v++){
                    colorTable[k] = rValue[i];
                    k++;
                    colorTable[k] = gValue[j];
                    k++;
                    colorTable[k] = bValue[v];
                    k++;
                }
            }
        }

        System.out.println("colorTable populated");

        //populate indexTable
        for (int i = 0; i < h; i++){
            for (int j = 0; j < w; j++){
                //get pixel value
                int rOrigin = arrayOrigin[i][j*3];
                int gOrigin = arrayOrigin[i][j*3+1];
                int bOrigin = arrayOrigin[i][j*3+2];
//                System.out.println(rOrigin+" "+gOrigin+" "+bOrigin);
                //search for the sub-block this pixel is located in
                int target = 0;
                int navigate = 0;
                for (int x = rMin; x < rMax; x += rStep + 1){
                    for (int y = gMin; y < gMax; y += gStep + 1){
                        for (int z = bMin; z < bMax; z += bStep + 1){
                            if ((rOrigin >= x) && (rOrigin <= x + rStep) &&
                                    (gOrigin >= y) && (gOrigin <= y + gStep) &&
                                    (bOrigin >= z) && (bOrigin <= z + bStep)){
                                target = navigate;
//                                System.out.println("Found!");
//                                System.out.println(x+" "+rOrigin+" "+(x+rStep));
//                                System.out.println(y+" "+gOrigin+" "+(y+gStep));
//                                System.out.println(z+" "+bOrigin+" "+(z+bStep));
//                                System.out.println("target = " + target);
                            }
                            else{
                                navigate += 3;
                            }
                        }
                    }
                }
                indexTable[i][j] = target;
            }
        }
        System.out.println("indexTable populated");

    }
}