
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ParallelDecryption extends Thread{

    private int [][] randomSequenceMatrix;
    private BufferedImage colorChannel;
    private String stringChannel;
    private final Decryption decryption;
    private static List<BufferedImage> outputDecryptedImageList;

    /**
     * Reprezinta datele necesare pentru efectuarea decriptarii in mod paralel.
     */

    /**
     * Constructor.
     */
    public ParallelDecryption(){
        this.decryption=new Decryption();
        outputDecryptedImageList=new ArrayList<>();
    }

    /**
     * Metoda 'run' pentru efectuarea decriptarii paralel.
     */
    public void run(){
        try{
            addDecryptedImageInList(decryption.doDecryption(randomSequenceMatrix, colorChannel,stringChannel));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** Adauga imaginea decriptata intr-o lista statica, in mod sincronizat.
     * @param bufferedImage Imaginea pentru adaugat in lista.
     */
    public synchronized void addDecryptedImageInList(BufferedImage bufferedImage){
        outputDecryptedImageList.add(bufferedImage);
    }

    /** Returneaza lista de imagini decriptate.
     * @return Lista de imagini decriptate.
     */
    public  List<BufferedImage> getOutputDecryptedImageList() {
        return outputDecryptedImageList;
    }

    /** Setter
     * @param randomSequenceMatrix Matricea de valori aleatoare.
     */
    public void setRandomSequenceMatrix(int[][] randomSequenceMatrix) {
        this.randomSequenceMatrix = randomSequenceMatrix;
    }

    /** Setter - Seteaza imaginea pentru decriptat.
     * @param colorChannel Imaginea de intrare
     */
    public void setColorChannel(BufferedImage colorChannel) {
        this.colorChannel = colorChannel;
    }

    /** Setter - Seteazacanalul de culoare asupra caruia se efectueaza decriptarea.
     * @param stringChannel Canalul de culoare
     */
    public void setStringChannel(String stringChannel) {
        this.stringChannel = stringChannel;
    }

}
