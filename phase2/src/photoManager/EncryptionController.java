package photoManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EncryptionController implements Initializable{
    @FXML
    private Button encrypt = new Button();
    @FXML
    private Button decrypt = new Button();
    @FXML
    private ListView<model.imageSystem.Image> archive = new ListView<>();
    @FXML
    private ListView<model.imageSystem.Image>encryptedImage = new ListView<>();
    @FXML
    private ImageView image = new ImageView();
    @FXML
    private Label imagePath = new Label();
    @FXML
    private Button close = new Button();
    @FXML
    private Button resetPassword = new Button();

    private model.imageSystem.Image curImage;

    private EncryptedImageManager encryptedImageManager;

    private Controller controller;

    /**
     * Initialize this class.
     *
     * @param location location to initialize
     * @param resources resources to initialize
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     *  Construct this class.
     */
    EncryptionController() {}

    public void setController(Controller controller){
        this.controller = controller;
        for (model.imageSystem.Image img: this.controller.model.im.getImages()) {
            archive.getItems().add(img);
        }
    }

    /**
     * Set EncryptedImageManager for this class.
     *
     * @param encryptedImageManager The manager for encryptedImages.
     * @throws IOException When file for input/output is not found.
     * @throws NoSuchAlgorithmException When the cryptographic algorithm requested does not exist.
     * @throws NoSuchPaddingException  When the padding mechanism requested does not exist.
     * @throws BadPaddingException When the data is not padded properly for the padding mechanism requested.
     * @throws InvalidKeyException When the coding/length of the key is invalid or key is uninitialized.
     * @throws IllegalBlockSizeException When the block provided does not match the block size of the Cipher.
     */
    void setEncryptedImageManager(EncryptedImageManager encryptedImageManager) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {
        this.encryptedImageManager = encryptedImageManager;
        ArrayList<model.imageSystem.Image> images = this.encryptedImageManager.allToImages();
        this.encryptedImage.getItems().addAll(images);
    }

    /**
     * Action to perform when an image in archive is selected.
     */
    public void selectArchive(){
        if(archive.getSelectionModel().getSelectedItem() != null){
            curImage = archive.getSelectionModel().getSelectedItem();
            imagePath.setText(curImage.getPath());
            Image img = new Image(curImage.toURI().toString());
            image.setImage(img);
            encryptedImage.getSelectionModel().clearSelection();
        }
    }

    /**
     * Action to perform when an image in Encrypted Image is selected.
     */
    public void selectEncryptedImage(){
        if(encryptedImage.getSelectionModel().getSelectedItem() != null){
            curImage = encryptedImage.getSelectionModel().getSelectedItem();
            imagePath.setText(curImage.getPath());
            Image img = new Image(curImage.toURI().toString());
            image.setImage(img);
            archive.getSelectionModel().clearSelection();
        }
    }

    /**
     * Perform action of encrypting image.
     *
     * @throws IOException When file for input/output is not found.
     * @throws NoSuchAlgorithmException When the cryptographic algorithm requested does not exist.
     * @throws NoSuchPaddingException  When the padding mechanism requested does not exist.
     * @throws BadPaddingException When the data is not padded properly for the padding mechanism requested.
     * @throws InvalidKeyException When the coding/length of the key is invalid or key is uninitialized.
     * @throws IllegalBlockSizeException When the block provided does not match the block size of the Cipher.
     */
    public void encryptImage() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        if(archive.getSelectionModel().getSelectedItem() != null){
            curImage = archive.getSelectionModel().getSelectedItem();
            this.controller.model.removeImage(curImage);
            archive.getItems().remove(curImage);
            encryptedImage.getItems().add(curImage);
        }
    }

    /**
     * Decrypt selected image from EncryptedImages
     *
     * @throws IOException When file for input/output is not found.
     * @throws NoSuchAlgorithmException When the cryptographic algorithm requested does not exist.
     * @throws NoSuchPaddingException  When the padding mechanism requested does not exist.
     * @throws BadPaddingException When the data is not padded properly for the padding mechanism requested.
     * @throws InvalidKeyException When the coding/length of the key is invalid or key is uninitialized.
     * @throws IllegalBlockSizeException When the block provided does not match the block size of the Cipher.
     */
    public void DecryptImage() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        if(encryptedImage.getSelectionModel().getSelectedItem() != null){
            curImage = encryptedImage.getSelectionModel().getSelectedItem();
            this.controller.model.addImage(curImage);
            encryptedImage.getItems().remove(curImage);
            archive.getItems().add(curImage);
        }
    }

    /**
     * Opens setPassword interface in this system.
     *
     * @throws IOException When file for input/output is not found.
     */
    private void setPassword() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("SetPassword.fxml"));
        fxmlLoader.setControllerFactory(
                new Callback<Class<?>, Object>() {
                    @Override
                    public Object call(Class<?> aClass) {
                        return new SetPasswordController();
                    }
                });
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        SetPasswordController sc = fxmlLoader.getController();
        sc.setSafeManager(this.controller.model.sm);
        stage.setScene(new Scene(root, 500, 400));
        stage.setTitle("Set password");
        stage.showAndWait();
    }

    /**
     * Open the reset password interface in the system.
     *
     * @throws IOException When file for input/output is not found.
     */
    public void openReset() throws IOException {
        if(this.controller.model.sm.isPassWordExist()) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("ResetPassword.fxml"));
            fxmlLoader.setControllerFactory(
                    new Callback<Class<?>, Object>() {
                        @Override
                        public Object call(Class<?> aClass) {
                            return new ResetPasswordController();
                        }
                    });
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOpacity(1);
            ResetPasswordController resetController = fxmlLoader.getController();
            resetController.SetSafeManager(this.controller.model.sm);
            stage.setTitle("Please enter your password.");
            stage.setScene(new Scene(root, 500, 500));
            stage.show();
        }else{
            this.setPassword();
        }
    }

    /**
     * Action to perform when closing this system
     *
     * @throws IOException When file for input/output is not found.
     * @throws NoSuchAlgorithmException When the cryptographic algorithm requested does not exist.
     * @throws NoSuchPaddingException  When the padding mechanism requested does not exist.
     * @throws BadPaddingException When the data is not padded properly for the padding mechanism requested.
     * @throws InvalidKeyException When the coding/length of the key is invalid or key is uninitialized.
     * @throws IllegalBlockSizeException When the block provided does not match the block size of the Cipher.
     */
    //https://stackoverflow.com/questions/13567019/close-fxml-window-by-code-javafx
    public void close() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidRenameException {
        Stage stage = (Stage)close.getScene().getWindow();
        ArrayList<model.imageSystem.Image> imagesToEncrypt = new ArrayList<>();
        imagesToEncrypt.addAll(encryptedImage.getItems());
        encryptedImageManager.reEncryptAll(imagesToEncrypt);
        if(!this.controller.model.sm.isPassWordExist()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to set a password?",
                    ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if(alert.getResult()==ButtonType.YES){
                this.setPassword();
                stage.close();
            }
        }
        stage.close();
        this.controller.curImage = null;
        this.controller.refresh();
    }
}
