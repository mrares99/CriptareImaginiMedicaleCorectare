
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ParallelDecryption extends Thread{

    private int [][] randomSequenceMatrix;
    private BufferedImage colorChannel;
    private String stringChannel;
    private final Decryption decryption;
    private static List<BufferedImage> outputDecryptedImageList;

    public ParallelDecryption(){
        this.decryption=new Decryption();
        outputDecryptedImageList=new ArrayList<>();
    }

    public void run(){
        try{
            addDecryptedImageInList(decryption.doDecryption(randomSequenceMatrix, colorChannel,stringChannel));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void addDecryptedImageInList(BufferedImage bufferedImage){
        outputDecryptedImageList.add(bufferedImage);
    }

    public  List<BufferedImage> getOutputDecryptedImageList() {
        return outputDecryptedImageList;
    }


    public void setRandomSequenceMatrix(int[][] randomSequenceMatrix) {
        this.randomSequenceMatrix = randomSequenceMatrix;
    }

    public void setColorChannel(BufferedImage colorChannel) {
        this.colorChannel = colorChannel;
    }

    public void setStringChannel(String stringChannel) {
        this.stringChannel = stringChannel;
    }

}
