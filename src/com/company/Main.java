package com.company;

import javax.imageio.ImageIO;           //class which provides lots of utility method related to images processing (reading,writing, read BufferedImg). 
import java.awt.Color;                  //creates color by using the given RGBA values
import java.awt.image.BufferedImage;    //a subclass of Image class. It is used to handle and manipulate the image data
import java.io.File;                    
import java.io.IOException;             //Signals that an I/O exception of some sort has occurred
import java.util.ArrayList;



class Pair {
    int x;
    int y;

    // Constructor
    public Pair(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
}

 class digitalSignature{

    ArrayList<Pair> embeddingPoints;
    char[]signature;
    digitalSignature(String sign){

        embeddingPoints = getEmbeddingPoints();
        signature=normalize_signature(sign);

    }
    digitalSignature(){

    }
    int[] getBits(int n) {
       int[] bits =new int [3];
        bits[0]=n>>5;      //[1,2,3]
        bits[1]=(n&28)>>2; //[4,5,6]
        bits[2]=n&3;       //[7,8]
        return bits;
    }

    int getByte(int []bits) {
        return (((bits[0]<<3) | bits[1])<<2) | bits[2] ;
    }

   char[] normalize_signature(String x){


        char[] y =x.toCharArray();
        char[] z =new char[50];
        for(int i=0;i<50;i++) {
            if(i<x.length()) z[i]=y[i];
            else z[i]='*';
        }

        return z;
    }

    ArrayList<Pair> getEmbeddingPoints() {
        ArrayList<Pair > v= new ArrayList<>();
            for(int i=0;i<50;i++){
                v.add(new Pair (8,2 * i));
            }
            return v;
    }

}
class Embedding extends digitalSignature{

    String srcImage;
    String resultImage;
    Embedding(String src, String des, String sign) {
        super(sign);
        srcImage=src;
        resultImage=des;
    }
    public void embed() throws IOException {

        BufferedImage buf_img = ImageIO.read(new File(srcImage));
        int cnt = 0;
        for (Pair pair : embeddingPoints) {

            int data = signature[cnt];
            int[] bits = getBits(data);
            int pixel = buf_img.getRGB(pair.x, pair.y);
            
            //Creating a Color object from pixel value
            Color color = new Color(pixel);
            //Retrieving the R G B values
            int r = color.getRed() ;
            int g = color.getGreen();
            int b = color.getBlue();
            int a=color.getAlpha();
            //System.out.print(pair.x+" "+ pair.y+" "+"[" + r + "," + g + " "+ b+"]");


            r = (r & ~7) | bits[0];         //last three bits contains the bit[0]
            g = (g & ~7) | bits[1];         //last three bits contains the bit[1]
            b = (b & ~3) | bits[2];         //last two bits contains the bit[2]



            buf_img.setRGB(pair.x, pair.y, new Color(r, g, b,a).getRGB());

            cnt += 1;

        }


        ImageIO.write(buf_img, "png",new File(resultImage));
        System.out.println("embedding Done.......................................................................");

         }

}
class Extraction extends digitalSignature{
    String img;
    Extraction(String img, String sign) {
        super(sign);
        this.img=img;
    }
    String extract() throws IOException {
        BufferedImage image = ImageIO.read(new File(img));
        ArrayList<Pair> extractFrom = embeddingPoints;



        StringBuilder sign= new StringBuilder();
        for (Pair pair: extractFrom) {


            int []bit=new int[3];
            //It returns an integer pixel in the default RGB color model (TYPE_INT_ARGB) and default sRGB colorspace.
            /*The TYPE_INT_ARGB represents Color as an int (4 bytes) with alpha channel in bits 24-31, 
              red channels in 16-23, green in 8-15 and blue in 0-7.*/
            
            int pixel = image.getRGB(pair.x, pair.y);
            Color color = new Color(pixel);
           
            //Retrieving the R G B values
            bit[0] = color.getRed()  & 7;
            bit[1] = color.getGreen()& 7;
            bit[2] = color.getBlue() & 3;
            
            int data = getByte(bit);
            char s;
            s=(char)data;

            sign.append(s); //chr converts ASCII to text

        }

        System.out.println("extraction Done...................................................");
        return sign.toString();
    }

}

public class Main {

    public static void main(String[] args) throws IOException {
        // write your code here
        String src = "d:/images/hibi.png";
        String des = "d:/images/work.png";
        String sign = "  First java project in stegnography by Rishu Rai ";
        //digitalSignature obj = new digitalSignature();
        System.out.println("1");
        Embedding emb = new Embedding(src, des, sign);
        emb.embed();
        Extraction ext = new Extraction(des, sign);

        System.out.println("Digital Signature in image: " + ext.extract());
    }
}




