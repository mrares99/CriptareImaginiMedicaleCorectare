
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Encryption {

    public List<int[][]> generateRandomSequenceForChannels(long seed,int rows,int columns) throws NoSuchAlgorithmException {
        int[][] redMatrixRandomSequence = new int[rows][columns];
        int[][] greenMatrixRandomSequence = new int[rows][columns];
        int[][] blueMatrixRandomSequence = new int[rows][columns];
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG") ;
        random.setSeed(seed);
        for(int i=-1;++i<rows;){
            for(int j=-1;++j<columns;){
                redMatrixRandomSequence[i][j]=random.nextInt(columns);
                greenMatrixRandomSequence[i][j]=random.nextInt(columns);
                blueMatrixRandomSequence[i][j]=random.nextInt(columns);
            }
        }
        List<int[][]> list2d=new ArrayList<>();
        list2d.add(redMatrixRandomSequence);
        list2d.add(greenMatrixRandomSequence);
        list2d.add(blueMatrixRandomSequence);
        return list2d;
    }

    public BufferedImage doEncryption(int [][] matrix,BufferedImage colorChannel, String channel) {
        int width=colorChannel.getWidth(),height= colorChannel.getHeight();
        List<List<Integer>> bitsFromRows=handlepixels(colorChannel,0,0,width,height,channel);
        int distance;
        List<Integer> auxiliaryList;
        for(int i=-1;++i<height;){
            distance=0;
            for(int j=-1;++j<width;){
                distance+=matrix[i][j];
            }
            auxiliaryList=doCircularRightShift(bitsFromRows.get(i),distance % width);
            bitsFromRows.set(i,auxiliaryList);
        }
        return recreateImageFromList(bitsFromRows,width,height,channel);
    }

    public List<List<Integer>> handlepixels(Image img, int x, int y, int w, int h,String channel) {
        int[] pixels = new int[w * h];
        List<List<Integer>> outputRows= new ArrayList<>();
        PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("interrupted waiting for pixels!");
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            System.err.println("image fetch aborted or errored");
        }
        switch(channel){
            case "red":
                for (int j = -1; ++j < h; ) {
                    List<Integer> row=new ArrayList<>();
                    for (int i = -1; ++i < w; ) {
                        byte byteRGB=(byte)((pixels[j * w + i] >> 16) & 0xff);
                        String bitRepresentaton=String.format("%8s", Integer.toBinaryString(byteRGB & 0xFF)).replace(' ', '0');
                        for(int counter=-1;++counter<8;){
                            row.add(Integer.parseInt(String.valueOf(bitRepresentaton.charAt(counter))));
                        }
                    }
                    outputRows.add(row);
                }
                break;
            case "green":
                for (int j = -1; ++j < h; ) {
                    List<Integer> row=new ArrayList<>();
                    for (int i = -1; ++i < w; ) {
                        byte byteRGB=(byte)((pixels[j * w + i] >>  8) & 0xff);
                        String bitRepresentaton=String.format("%8s", Integer.toBinaryString(byteRGB & 0xFF)).replace(' ', '0');
                        for(int counter=-1;++counter<8;){
                            row.add(Integer.parseInt(String.valueOf(bitRepresentaton.charAt(counter))));
                        }
                    }
                    outputRows.add(row);
                }
                break;
            case "blue":
                for (int j = -1; ++j < h; ) {
                    List<Integer> row=new ArrayList<>();
                    for (int i = -1; ++i < w; ) {
                        byte byteRGB=(byte)((pixels[j * w + i]      ) & 0xff);
                        String bitRepresentaton=String.format("%8s", Integer.toBinaryString(byteRGB & 0xFF)).replace(' ', '0');
                        for(int counter=-1;++counter<8;){
                            row.add(Integer.parseInt(String.valueOf(bitRepresentaton.charAt(counter))));
                        }
                    }
                    outputRows.add(row);
                }
                break;
        }
        return outputRows;
    }

    public List<Integer> doCircularRightShift(List<Integer> inputList,int distance){
        Collections.rotate(inputList,distance);
        return inputList;
    }

    public BufferedImage recreateImageFromList(List<List<Integer>> inputList,int width,int height,String color){
        BufferedImage outputBufferedImage=new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        int nrRows=inputList.size(),nrBitsInARow=inputList.get(0).size(),counter=0,columnNumber;
        StringBuilder  result=new StringBuilder();
        switch(color){
            case "red":
                for(int i=-1;++i<nrRows;){
                    result.delete(0,8);
                    columnNumber=0;
                    for(int j=-1;++j<nrBitsInARow;){
                        result.append(inputList.get(i).get(j));
                        counter++;
                        if(counter==8){
                            byte byteRGB= (byte) Integer.parseInt(result.toString(),2);
                            result.delete(0,8);
                            counter=0;
                            outputBufferedImage.setRGB(columnNumber++,i,((int)byteRGB<<16) & 0x00ff0000);
                            //columnNumber++;
                        }
                    }
                }
                break;
            case "green":
                for(int i=-1;++i<nrRows;){
                    result.delete(0,8);
                    columnNumber=0;
                    for(int j=-1;++j<nrBitsInARow;){
                        result.append(inputList.get(i).get(j));
                        counter++;
                        if(counter==8){
                            byte byteRGB= (byte) Integer.parseInt(result.toString(),2);
                            result.delete(0,8);
                            counter=0;
                            outputBufferedImage.setRGB(columnNumber++,i,((int)byteRGB<<8) & 0x0000ff00);
                            //columnNumber++;
                        }
                    }
                }
                break;
            case "blue":
                for(int i=-1;++i<nrRows;){
                    result.delete(0,8);
                    columnNumber=0;
                    for(int j=-1;++j<nrBitsInARow;){
                        result.append(inputList.get(i).get(j));
                        counter++;
                        if(counter==8){
                            byte byteRGB= (byte) Integer.parseInt(result.toString(),2);
                            result.delete(0,8);
                            counter=0;
                            outputBufferedImage.setRGB(columnNumber++,i,((int)byteRGB) & 0x000000ff);
                            //columnNumber++;
                        }
                    }
                }
                break;
        }
        return outputBufferedImage;
    }
}
