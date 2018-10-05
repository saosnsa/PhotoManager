package photoManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.InvalidRenameException;
import model.imageSafeSystem.EncryptedImageManager;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

public class LoginController implements Initializable{
    private Controller controller;
    @FXML
    private Button logIn = new Button();
    @FXML
    private TextField password = new TextField();

    private Stage logInStage;

    /**
     * Construct this Class
     */
    LoginController(){

    }

    /**
     * Set the controller attribute of this Class.
     *
     * @param controller the Controller instance to set.
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Initialize this Controller.
     *
     * @param location location to initialize.
     * @param resources resources to initialize.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     *  Open the new safe/encryption system
     *
     * @throws IOException When file for input/output is not found.
     * @throws ClassNotFoundException When the class being de-serialized does not exist.
     * @throws NoSuchAlgorithmException When the cryptographic algorithm requested does not exist.
     * @throws NoSuchPaddingException  When the padding mechanism requested does not exist.
     * @throws BadPaddingException When the data is not padded properly for the padding mechanism requested.
     * @throws InvalidKeyException When the coding/length of the key is invalid or key is uninitialized.
     * @throws IllegalBlockSizeException When the block provided does not match the block size of the Cipher.
     */
    private void openEncryption() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("Encryption.fxml"));
        fxmlLoader.setControllerFactory(
                new Callback<Class<?>, Object>() {
                    @Override
                    public Object call(Class<?> aClass) {
                        return new EncryptionController();
                    }
                });
        Parent root = fxmlLoader.load();
        logInStage= new Stage();
        logInStage.initModality(Modality.APPLICATION_MODAL);
        EncryptedImageManager encrypted = new EncryptedImageManager();
        EncryptionController encryptController = fxmlLoader.getController();
        encryptController.setEncryptedImageManager(encrypted);
        encryptController.setController(this.controller);
        logInStage.setTitle("Encryption Interface");
        logInStage.setScene(new Scene(root, 1280, 900));
        logInStage.setOnCloseRequest(event -> {
            try {
                encryptController.close();
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidRenameException e) {
                e.printStackTrace();
            }
        });
        logInStage.show();
    }

    /**
     * Close this interface.
     */
    private void closeLogin() {
        logInStage= (Stage) logIn.getScene().getWindow();
        logInStage.close();
    }

    /**
     * Login the encryption/safe system.
     *
     * @throws IOException When file for input/output is not found.
     * @throws ClassNotFoundException When the class being de-serialized does not exist.
     * @throws NoSuchAlgorithmException When the cryptographic algorithm requested does not exist.
     * @throws NoSuchPaddingException  When the padding mechanism requested does not exist.
     * @throws BadPaddingException When the data is not padded properly for the padding mechanism requested.
     * @throws InvalidKeyException When the coding/length of the key is invalid or key is uninitialized.
     * @throws IllegalBlockSizeException When the block provided does not match the block size of the Cipher.
     */
    public void logIn() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, ClassNotFoundException {
        if(this.controller.model.sm.isPasswordCorrect(password.getText())){
            closeLogin();
            this.openEncryption();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Password incorrect");
            alert.showAndWait();
        }
    }
}
