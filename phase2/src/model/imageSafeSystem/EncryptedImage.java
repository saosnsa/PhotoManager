package model.imageSafeSystem;

import model.imageSystem.Image;
import java.security.Key;

/**
 * This is a Image that is being encrypted by the user and stores the information of the Image before
 * Encryption.
 */
class EncryptedImage extends Image {

    /**
     * Byte representation of the Image being encrypted.
     */
    private byte[] imageBytes;

    /**
     * Key that is used during encryption of the corresponding Image.
     */
    private Key key;

    /**
     * Constructs a new EncryptedImage that stores all the information of the Image being encrypted.
     *
     * @param pathName Path name of the Image being encrypted.
     * @param key Key that is used for encryption of the image.
     * @param imageBytes The byte representation of the encrypted image.
     */
    EncryptedImage(String pathName, Key key, byte[] imageBytes) {
        super(pathName);
        this.key = key;
        this.imageBytes = imageBytes;
    }

    byte[] getImageBytes() {
        return imageBytes;
    }

    Key getKey() {
        return key;
    }
}
