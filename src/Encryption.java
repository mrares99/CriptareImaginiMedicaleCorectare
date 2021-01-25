
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

    /** Genereaza o secventa de dimensiunea imaginii, de numere aleatoare.
     * @param seed Valoare pe baza careia se va efectua generarea de numere pseudoaleatoare.
     * @param rows Numarul de linii al imaginii.
     * @param columns Numarul de coloane al imaginii.
     * @return Secventa de numere aleatoare.
     * @throws NoSuchAlgorithmException
     */
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

    /** Efectueaza criptarea.
     * @param matrix Matricea care contine secventa de numere aleatoare.
     * @param colorChannel Imaginea care se doreste a fi criptata.
     * @param channel Canalul de culoare asupra caruia se efectueaza criptarea.
     * @return Imaginea criptata.
     */
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
            auxiliaryList=doCircularRightShift(bitsFromRows.get(i),distance % (width*8));
            bitsFromRows.set(i,auxiliaryList);
        }
        return recreateImageFromList(bitsFromRows,width,height,channel);
    }

    /** Efectueaza despartirea pixelilor in biti.
     * @param img Imaginea asupra careia se aplica operatiile.
     * @param x Pozitia de inceput pentru extragerea bitilor pe axa X.
     * @param y Pozitia de inceput pentru extragerea bitilor pe axa Y.
     * @param w Latimea imaginii.
     * @param h Inaltimea imaginii.
     * @param channel Canalul de culoare asupra caruia se aplica operatiile.
     * @return Lista de biti, care reprezinta bitii din imagine.
     */
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

    /** Efectueaza rotire circulara spre dreapta.
     * @param inputList Lista asupra careia se aplica rotatia.
     * @param distance Distanta de rotire.
     * @return Lista asupra careia s-a efectuat rotatia.
     */
    public List<Integer> doCircularRightShift(List<Integer> inputList,int distance){
        Collections.rotate(inputList,distance);
        return inputList;
    }

    /** Creeaza imagine din lista de biti.
     * @param inputList Lista de biti.
     * @param width Latimea imaginii.
     * @param height Inaltimea imaginii.
     * @param color Canalul de culoare asupra caruia se aplica reconstuctia.
     * @return Imaginea creata din lista.
     */
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
