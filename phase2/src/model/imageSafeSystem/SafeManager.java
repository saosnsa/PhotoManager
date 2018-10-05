package model.imageSafeSystem;

import java.io.*;

public class SafeManager {

    /**
     * The password of this SafeManager.
     */
    private String passWord;

    public void setPassword(String passWord){
        this.passWord = passWord;
    }

    /**
     * Check if a password exists in SafeManager.
     *
     * @return true iff there exists a password.
     */
    public boolean isPassWordExist(){
        return this.passWord != null;
    }

    /**
     *  Check if the inputPassWord matches the password stores in SafeManager.
     * @param inputPassWord The password input from user.
     * @return true iff the password matches the inputPassword.
     */
    public boolean isPasswordCorrect(String inputPassWord){
        return inputPassWord.equals(this.passWord);
    }

    /**
     * Deserialize the password stores in this SafeManager.
     *
     * @param path the path of the serialization file.
     * @throws ClassNotFoundException the password being deserialized does not exists.
     * @throws IOException throws when the ser file does not exist.
     */
    public void readFromFile(String path) throws ClassNotFoundException, IOException {
        InputStream file = new FileInputStream(path);
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);

        this.passWord = (String) input.readObject();
        input.close();
    }

    /**
     * Serializes the password and stores in serialization file.
     *
     * @param path The path of the serialization file.
     * @throws IOException throws when the ser file does not exist.
     */
    public void saveToFile(String path) throws IOException {
        OutputStream file = new FileOutputStream(path);
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutput output = new ObjectOutputStream(buffer);

        output.writeObject(this.passWord);
        output.close();
    }
}
