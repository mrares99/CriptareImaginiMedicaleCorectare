
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ParallelEncryption extends Thread{

    private int [][] randomSequenceMatrix;
    private BufferedImage colorChannel;
    private String stringChannel;
    private final Encryption encryption;
    private static List<BufferedImage> outputEncryptedImageList;

    public ParallelEncryption(){
        this.encryption=new Encryption();
        outputEncryptedImageList=new ArrayList<>();
    }

    public void run(){
        try{
            addEncryptedImageInList(encryption.doEncryption(randomSequenceMatrix, colorChannel,stringChannel));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public  List<BufferedImage> getOutputEncryptedImageList() {
        return outputEncryptedImageList;
    }

    public synchronized void addEncryptedImageInList(BufferedImage bufferedImage){
        outputEncryptedImageList.add(bufferedImage);
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
