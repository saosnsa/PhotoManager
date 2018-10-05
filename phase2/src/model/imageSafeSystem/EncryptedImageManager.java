package model.imageSafeSystem;

import model.imageSystem.Image;

import javax.crypto.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * This is a class that manages all the EncryptedImage.
 * The code is adapted from https://stackoverflow.com/questions/26058823/correct-way-of-encryptincg-and-decrypting-an-image-using-aes
 * and modified.
 */
public class EncryptedImageManager implements Serializable {

    /**
     * Data stores the EncryptedImage and it's corresponding Image's information.
     */
    private ArrayList<EncryptedImage> data = new ArrayList<>();

    /**
     * The serialization file path that contains all the EncryptedImage.
     */
    private final String filePath = "./KeySer";


    /**
     * Initializes a new EncryptedImageManager to manage Encryption/Decryption.
     *
     * @throws IOException When file for input/output is not found.
     * @throws ClassNotFoundException When the class being de-serialized does not exist.
     * @throws NoSuchAlgorithmException When the cryptographic algorithm requested does not exist.
     * @throws NoSuchPaddingException  When the padding mechanism requested does not exist.
     * @throws BadPaddingException When the data is not padded properly for the padding mechanism requested.
     * @throws InvalidKeyException When the coding/length of the key is invalid or key is uninitialized.
     * @throws IllegalBlockSizeException When the block provided does not match the block size of the Cipher.
     */
    public EncryptedImageManager() throws IOException, ClassNotFoundException, NoSuchAlgorithmException,
            NoSuchPaddingException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        File file = new File(filePath);
        if (!file.createNewFile()) {
            this.readDataFromFile();
            decryptAllImages();
        }
    }

    /**
     * Return the byte representation of the Image.
     *
     * @param path Path name of the Image being encrypted.
     * @return Byte representation of the Image being requested for encryption.
     * @throws IOException When the Image being read from does not exist,
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private byte[] getFile(String path) throws IOException{
        File file = new File(path);
        InputStream is = new FileInputStream(file);
        byte[] content = new byte[is.available()];

        is.read(content);
        is.close();
        return content;
    }

    /**
     *  Encrypt the Image chosen by the user with Cipher class provided by Java.
     *
     * @param key The key generated for this particular instruction.
     * @param imageByte The byte representation of the image being requested for encryption.
     * @return The encrypted byte representation of the byte representation of the Image.
     * @throws NoSuchAlgorithmException When the cryptographic algorithm requested does not exist.
     * @throws NoSuchPaddingException  When the padding mechanism requested does not exist.
     * @throws BadPaddingException When the data is not padded properly for the padding mechanism requested.
     * @throws InvalidKeyException When the coding/length of the key is invalid or key is uninitialized.
     * @throws IllegalBlockSizeException When the block provided does not match the block size of the Cipher.
     */
    private byte[] encryptImage(Key key, byte[] imageByte) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException{
        Cipher cipher;
        byte[] encrypted;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        encrypted = cipher.doFinal(imageByte);
        return encrypted;
    }

    /**
     *  Decrypts the encrypted image that has been requested for decryption.
     *
     * @param key The key generated for encryption/decryption.
     * @param imageByte The byte representation of the encrypted image.
     * @return The decrypted byte representation of the image.
     * @throws NoSuchAlgorithmException When the cryptographic algorithm requested does not exist.
     * @throws NoSuchPaddingException  When the padding mechanism requested does not exist.
     * @throws BadPaddingException When the data is not padded properly for the padding mechanism requested.
     * @throws InvalidKeyException When the coding/length of the key is invalid or key is uninitialized.
     * @throws IllegalBlockSizeException When the block provided does not match the block size of the Cipher.
     */
    private byte[] decryptImage(Key key, byte[] imageByte) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        Cipher cipher;
        byte[] decrypted;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        decrypted = cipher.doFinal(imageByte);
        return decrypted;
    }

    /**
     *  Generates a new key and encrypts the image passed in along with the byte representation of the Image.
     *  Also passes on the information of the Image that is requested for encryption into EncryptedImage for storage,
     *  then stores the EncryptedImage in the ArrayList for storing upon closing the software.
     *
     * @param image The Image being requested for encryption.
     * @throws IOException When file for input/output is not found.
     * @throws NoSuchAlgorithmException When the cryptographic algorithm requested does not exist.
     * @throws NoSuchPaddingException  When the padding mechanism requested does not exist.
     * @throws BadPaddingException When the data is not padded properly for the padding mechanism requested.
     * @throws InvalidKeyException When the coding/length of the key is invalid or key is uninitialized.
     * @throws IllegalBlockSizeException When the block provided does not match the block size of the Cipher.
     */
    private void addImage(Image image) throws IOException, NoSuchAlgorithmException, InvalidKeyException,
            NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException{
        byte[] imageByte = getFile(image.getPath());
        Key key = getNewKey();
        byte[] encryptedByte = this.encryptImage(key, imageByte);
        EncryptedImage ei = new EncryptedImage(image.getPath(), key, encryptedByte);
        ei.setTagHistory(image.getTagHistory());
        ei.setNameHistory(image.getNameHistory());
        this.data.add(ei);
    }


    /**
     *  Generate a new key for encryption/decryption of a particular Image.
     *
     * @return The key for encryption/decryption.
     * @throws NoSuchAlgorithmException The cryptographic algorithm requested does not exist.
     */
    private Key getNewKey() throws NoSuchAlgorithmException{
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);

        return keyGenerator.generateKey();
    }

    /**
     * Overwrites a specific file with the encrypted version of the file, and then delete the image from
     * the operating system to prevent any access outside from the safe/encryption system.
     *
     * @param imageByte The encrypted version of the byte representation of the Image.
     * @param pathName The file that is being overwrite.
     * @throws IOException When the file being overwritten does not exist.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveEncryptedFile(byte[] imageByte, String pathName) throws IOException {
        File file = new File(pathName);
        FileOutputStream fos1 = new FileOutputStream(file);
        fos1.write(imageByte);
        fos1.close();
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Encrypts all the Image that the user want to hide.
     *
     * @throws IOException When the Image that is being encrypted does not exist or corrupted.
     */
    private void encryptAllImages() throws IOException{
        if (this.data != null) {
            for (EncryptedImage ei: this.data) {
                saveEncryptedFile(ei.getImageBytes(), ei.getPath());
            }
        }
    }

    /**
     * Recreates the encrypted and deleted Image so the user can see and modify it.
     *
     * @param imageByte The decrypted version of the byte representation of the Image.
     * @param pathName The path name of the Image that was encrypted.
     * @throws IOException When the file that is being recreated does not exist or corrupted.
     */
    private void saveDecryptedFile(byte[] imageByte, String pathName) throws IOException {
        File file = new File(pathName);
        if (file.createNewFile()) {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(imageByte);
            fos.close();
        }
    }

    /**
     * Decrypts and recreate all the Images that are encrypted by the user.
     *
     * @throws IOException When file for input/output is not found.
     * @throws NoSuchAlgorithmException When the cryptographic algorithm requested does not exist.
     * @throws NoSuchPaddingException  When the padding mechanism requested does not exist.
     * @throws BadPaddingException When the data is not padded properly for the padding mechanism requested.
     * @throws InvalidKeyException When the coding/length of the key is invalid or key is uninitialized.
     * @throws IllegalBlockSizeException When the block provided does not match the block size of the Cipher.
     */
    private void decryptAllImages() throws NoSuchAlgorithmException, NoSuchPaddingException,
            BadPaddingException, InvalidKeyException, IllegalBlockSizeException, IOException{
        if (this.data != null) {
            for (EncryptedImage ei: this.data) {
                byte[] decrypted = decryptImage(ei.getKey(), ei.getImageBytes());
                saveDecryptedFile(decrypted, ei.getPath());
            }
        }
    }

    /**
     * Deserialize the ArrayList that stores all the EncryptedImage that contains all the information
     * of all the Images encrypted by the user.
     *
     * @throws IOException When the serialization file does not exist or is corrupted.
     * @throws ClassNotFoundException When the class that is being requested does not exist.
     */
    @SuppressWarnings("unchecked")
    private void readDataFromFile() throws IOException, ClassNotFoundException{
        InputStream file = new FileInputStream(filePath);
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);

        this.data = (ArrayList<EncryptedImage>) input.readObject();
        input.close();
    }

    /**
     * Serializes the ArrayList that stores all the EncryptedImage and the corresponding information of the Image
     * requested for encryption.
     *
     * @throws IOException When the serializing file does not exist.
     */
    private void saveDataToFile() throws IOException{
        OutputStream file = new FileOutputStream(filePath);
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutput output = new ObjectOutputStream(buffer);

        output.writeObject(this.data);
        output.close();
    }

    /**
     * Recreates the Images that has been encrypted and deleted with its original information.
     *
     * @return An ArrayList of Images that was originally encrypted.
     */
    public ArrayList<Image> allToImages() {
        ArrayList<Image> allImages = new ArrayList<>();
        if (this.data != null) {
            for (EncryptedImage ei: this.data) {
                Image image = new Image(ei.getPath());
                image.setTagHistory(ei.getTagHistory());
                image.setNameHistory(ei.getNameHistory());
                allImages.add(image);
            }
        }
        return allImages;
    }

    /**
     * This encrypts all the Images that is requested for encryption and stores all of its information
     * upon closing the Encryption user interface.
     *
     * @param ar The Images that are being requested for encryption.
     * @throws IOException When file for input/output is not found.
     * @throws NoSuchAlgorithmException When the cryptographic algorithm requested does not exist.
     * @throws NoSuchPaddingException  When the padding mechanism requested does not exist.
     * @throws BadPaddingException When the data is not padded properly for the padding mechanism requested.
     * @throws InvalidKeyException When the coding/length of the key is invalid or key is uninitialized.
     * @throws IllegalBlockSizeException When the block provided does not match the block size of the Cipher.
     */
    public void reEncryptAll(ArrayList<Image> ar) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException{
        this.data.clear();
        if (ar != null) {
            for (Image image : ar) {
                addImage(image);
            }
            encryptAllImages();
            saveDataToFile();
        }
    }

}
