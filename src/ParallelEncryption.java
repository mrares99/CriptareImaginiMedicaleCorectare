
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ParallelEncryption extends Thread{

    private int [][] randomSequenceMatrix;
    private BufferedImage colorChannel;
    private String stringChannel;
    private final Encryption encryption;
    private static List<BufferedImage> outputEncryptedImageList;

    /**
     * Reprezinta datele necesare pentru efectuarea criptarii in mod paralel.
     */

    /**
     * Constructor.
     */
    public ParallelEncryption(){
        this.encryption=new Encryption();
        outputEncryptedImageList=new ArrayList<>();
    }

    /**
     * Metoda 'run' pentru efectuarea decriptarii paralel.
     */
    public void run(){
        try{
            addEncryptedImageInList(encryption.doEncryption(randomSequenceMatrix, colorChannel,stringChannel));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** Returneaza lista de imagini criptate.
     * @return Lista care contine imaginile criptate.
     */
    public  List<BufferedImage> getOutputEncryptedImageList() {
        return outputEncryptedImageList;
    }

    /** Adauga imagini criptare intr- lista statica.
     * @param bufferedImage Imaginea criptata.
     */
    public synchronized void addEncryptedImageInList(BufferedImage bufferedImage){
        outputEncryptedImageList.add(bufferedImage);
    }

    /** Setter - Seteaza matricea de valori aleatoare.
     * @param randomSequenceMatrix Matrice de valori aleatoare.
     */
    public void setRandomSequenceMatrix(int[][] randomSequenceMatrix) {
        this.randomSequenceMatrix = randomSequenceMatrix;
    }

    /** Seteaza imaginea pentru criptare.
     * @param colorChannel Imaginea care se doreste a fi criptata.
     */
    public void setColorChannel(BufferedImage colorChannel) {
        this.colorChannel = colorChannel;
    }

    /** Setter - Seteaza numele canalului de culoare pentru criptare.
     * @param stringChannel Numele canalului de culoare.
     */
    public void setStringChannel(String stringChannel) {
        this.stringChannel = stringChannel;
    }

}
