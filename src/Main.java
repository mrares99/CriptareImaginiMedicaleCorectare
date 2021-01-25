import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException {
        ImageOperations imageOperations=new ImageOperations();
        Encryption encryption=new Encryption();
        ViewImage viewImage=new ViewImage();
        BufferedImage inputBufferedImage = imageOperations.readImage();
        int width=inputBufferedImage.getWidth(), height=inputBufferedImage.getHeight();
        viewImage.displayImage(inputBufferedImage,"Original",width,height);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        File file = new File("TimpRulare.txt");
        try {
            if(file.createNewFile()) { //fisierul este creat
                Files.write(Paths.get("TimpRulare.txt"),("Date="+dateFormat.format(date)+"\n").getBytes(), StandardOpenOption.APPEND);
                Files.write(Paths.get("TimpRulare.txt"),("Width imagine="+inputBufferedImage.getWidth()+" Height imagine="+inputBufferedImage.getHeight()+"\n").getBytes(), StandardOpenOption.APPEND);
            }
            else { //fisierul exista deja la locatie
                Files.write(Paths.get("TimpRulare.txt"),("Date="+dateFormat.format(date)+"\n").getBytes(), StandardOpenOption.APPEND);
                Files.write(Paths.get("TimpRulare.txt"),("Width imagine="+inputBufferedImage.getWidth()+" Height imagine="+inputBufferedImage.getHeight()+"\n").getBytes(), StandardOpenOption.APPEND);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        //criptare
        long startTime=System.currentTimeMillis();
        List<BufferedImage> extractedChannelsList=imageOperations.extractColorChannels(inputBufferedImage);

        long seed=2157562;
        List<int[][]> randomSequenceMatrixForChannel=encryption.generateRandomSequenceForChannels(seed, height,width);

        ParallelEncryption parallelEncryption=new ParallelEncryption();
        ExecutorService executorService= Executors.newFixedThreadPool(3);

        parallelEncryption.setRandomSequenceMatrix(randomSequenceMatrixForChannel.get(0));
        parallelEncryption.setColorChannel(extractedChannelsList.get(0));
        parallelEncryption.setStringChannel("red");

        executorService.execute(parallelEncryption);

        parallelEncryption=new ParallelEncryption();
        parallelEncryption.setRandomSequenceMatrix(randomSequenceMatrixForChannel.get(1));
        parallelEncryption.setColorChannel(extractedChannelsList.get(1));
        parallelEncryption.setStringChannel("green");

        executorService.execute(parallelEncryption);

        parallelEncryption=new ParallelEncryption();
        parallelEncryption.setRandomSequenceMatrix(randomSequenceMatrixForChannel.get(2));
        parallelEncryption.setColorChannel(extractedChannelsList.get(2));
        parallelEncryption.setStringChannel("blue");

        executorService.execute(parallelEncryption);

        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.MINUTES);
        List<BufferedImage> outputEncryptedImages=parallelEncryption.getOutputEncryptedImageList();

        BufferedImage finalEncryptedImage = imageOperations.constructImageFromRGBChannels(outputEncryptedImages.get(0), outputEncryptedImages.get(1),outputEncryptedImages.get(2));

        long endTime=System.currentTimeMillis();
        NumberFormat formatter=new DecimalFormat("#0.00000");
        Files.write(Paths.get("TimpRulare.txt"),("Timpul total pentru criptare paralela="+formatter.format((endTime-startTime)/1000d)+" secunde\n").getBytes(), StandardOpenOption.APPEND);
        viewImage.displayImage(finalEncryptedImage,"finalEncryptedImage", width,height);

        //terminare criptare



        //decriptare
        startTime=System.currentTimeMillis();
        width=finalEncryptedImage.getWidth();
        height=finalEncryptedImage.getHeight();
        Decryption decryption=new Decryption();
        randomSequenceMatrixForChannel=decryption.generateRandomSequenceForChannels(seed, height,width);

        ParallelDecryption parallelDecryption=new ParallelDecryption();
        executorService= Executors.newFixedThreadPool(3);

        extractedChannelsList=imageOperations.extractColorChannels(finalEncryptedImage);

        parallelDecryption.setRandomSequenceMatrix(randomSequenceMatrixForChannel.get(0));
        parallelDecryption.setColorChannel(extractedChannelsList.get(0));
        parallelDecryption.setStringChannel("red");

        executorService.execute(parallelDecryption);

        parallelDecryption=new ParallelDecryption();
        parallelDecryption.setRandomSequenceMatrix(randomSequenceMatrixForChannel.get(1));
        parallelDecryption.setColorChannel(extractedChannelsList.get(1));
        parallelDecryption.setStringChannel("green");

        executorService.execute(parallelDecryption);

        parallelDecryption=new ParallelDecryption();
        parallelDecryption.setRandomSequenceMatrix(randomSequenceMatrixForChannel.get(2));
        parallelDecryption.setColorChannel(extractedChannelsList.get(2));
        parallelDecryption.setStringChannel("blue");

        executorService.execute(parallelDecryption);

        executorService.shutdown();
        executorService.awaitTermination(60,TimeUnit.MINUTES);

        List<BufferedImage> outputDecryptedImageList=parallelDecryption.getOutputDecryptedImageList();
        BufferedImage fin= imageOperations.constructImageFromRGBChannels(outputDecryptedImageList.get(0),outputDecryptedImageList.get(1),outputDecryptedImageList.get(2));


        endTime=System.currentTimeMillis();
        viewImage.displayImage(fin,"finalDecryptedImage", width,height);
        formatter=new DecimalFormat("#0.00000");
        Files.write(Paths.get("TimpRulare.txt"),("Timpul total pentru decriptare paralela="+formatter.format((endTime-startTime)/1000d)+" secunde\n").getBytes(), StandardOpenOption.APPEND);

        //terminare decriptare













//        //implementare secventiala
//
//
//        ImageOperations imageOperations=new ImageOperations();
//        ViewImage viewImage=new ViewImage();
//        BufferedImage inputBufferedImage = imageOperations.readImage();
//        int width=inputBufferedImage.getWidth(), height=inputBufferedImage.getHeight();
//        viewImage.displayImage(inputBufferedImage,"Original",width,height);
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date();
//
//        File file = new File("TimpRulare.txt");
//        try {
//            if(file.createNewFile()) { //fisierul este creat
//                Files.write(Paths.get("TimpRulare.txt"),("Date="+dateFormat.format(date)+"\n").getBytes(), StandardOpenOption.APPEND);
//                Files.write(Paths.get("TimpRulare.txt"),("Width imagine="+inputBufferedImage.getWidth()+" Height imagine="+inputBufferedImage.getHeight()+"\n").getBytes(), StandardOpenOption.APPEND);
//            }
//            else { //fisierul exista deja la locatie
//                Files.write(Paths.get("TimpRulare.txt"),("Date="+dateFormat.format(date)+"\n").getBytes(), StandardOpenOption.APPEND);
//                Files.write(Paths.get("TimpRulare.txt"),("Width imagine="+inputBufferedImage.getWidth()+" Height imagine="+inputBufferedImage.getHeight()+"\n").getBytes(), StandardOpenOption.APPEND);
//            }
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //criptare
//        long startTime=System.currentTimeMillis();
//        Encryption encryption=new Encryption();
//        List<BufferedImage> extractedChannelsList=imageOperations.extractColorChannels(inputBufferedImage);
//
//        long seed=2157562;
//        List<int[][]> randomSequenceMatrixForChannel=encryption.generateRandomSequenceForChannels(seed, height,width);
//
//        BufferedImage encryptedRed=encryption.doEncryption(randomSequenceMatrixForChannel.get(0),extractedChannelsList.get(0),"red");
//        BufferedImage encryptedGreen=encryption.doEncryption(randomSequenceMatrixForChannel.get(1),extractedChannelsList.get(1),"green");
//        BufferedImage encryptedBlue=encryption.doEncryption(randomSequenceMatrixForChannel.get(2),extractedChannelsList.get(2),"blue");
//
//        BufferedImage finalEncryptedImage = imageOperations.constructImageFromRGBChannels(encryptedRed, encryptedGreen,encryptedBlue);
//        long endTime=System.currentTimeMillis();
//        NumberFormat formatter=new DecimalFormat("#0.00000");
//        Files.write(Paths.get("TimpRulare.txt"),("Timpul total pentru criptare secventiala="+formatter.format((endTime-startTime)/1000d)+" secunde\n").getBytes(), StandardOpenOption.APPEND);
//
//        viewImage.displayImage(finalEncryptedImage,"finalEncryptedImage", width,height);
//
//        //terminare criptare
//
//
//
//
//
//        //decriptare
//        startTime=System.currentTimeMillis();
//        width=finalEncryptedImage.getWidth();
//        height=finalEncryptedImage.getHeight();
//        Decryption decryption=new Decryption();
//        randomSequenceMatrixForChannel=decryption.generateRandomSequenceForChannels(seed, height,width);
//
//
//        extractedChannelsList=imageOperations.extractColorChannels(finalEncryptedImage);
//
//        BufferedImage decryptedRed=decryption.doDecryption(randomSequenceMatrixForChannel.get(0),extractedChannelsList.get(0),"red");
//        BufferedImage decryptedGreen=decryption.doDecryption(randomSequenceMatrixForChannel.get(1),extractedChannelsList.get(1),"green");
//        BufferedImage decryptedBlue=decryption.doDecryption(randomSequenceMatrixForChannel.get(2),extractedChannelsList.get(2),"blue");
//
//        BufferedImage fin= imageOperations.constructImageFromRGBChannels(decryptedRed,decryptedGreen,decryptedBlue);
//
//        endTime=System.currentTimeMillis();
//        viewImage.displayImage(fin,"finalDecryptedImage", width,height);
//        formatter=new DecimalFormat("#0.00000");
//        Files.write(Paths.get("TimpRulare.txt"),("Timpul total pentru decriptare secventiala="+formatter.format((endTime-startTime)/1000d)+" secunde\n").getBytes(), StandardOpenOption.APPEND);
//
//        //terminare decriptare

    }

}
