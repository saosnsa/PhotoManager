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
import model.imageSafeSystem.SafeManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ResetPasswordController implements Initializable{
    @FXML
    private SafeManager safeManager;
    @FXML
    private Button confirm = new Button();
    @FXML
    private TextField password = new TextField();

    private Stage resetStage;

    /**
     * Initialize this class
     *
     * @param location location to initialize.
     * @param resources resources to initialize.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Construct this class.
     */
    ResetPasswordController(){}

    /**
     * Set the safeManager for this class.
     *
     * @param safeManager the SafeManager to pass in.
     */
    void SetSafeManager(SafeManager safeManager){
        this.safeManager = safeManager;
    }

    /**
     * Close this interface.
     */
    private void close(){
        resetStage= (Stage) confirm.getScene().getWindow();
        resetStage.close();
    }

    /**
     * Set the password for this system.
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
        resetStage = new Stage();
        resetStage.initModality(Modality.APPLICATION_MODAL);
        SetPasswordController sc = fxmlLoader.getController();
        sc.setSafeManager(this.safeManager);
        resetStage.setScene(new Scene(root, 500, 400));
        resetStage.setTitle("Set password");
        resetStage.show();
    }

    /**
     * An action to let user re-enter their old password to make sure they want to reset password.
     *
     * @throws IOException When file for input/output is not found.
     */
    public void confirm() throws IOException {
        if(this.safeManager.isPasswordCorrect(password.getText())){
            this.close();
            this.setPassword();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Password incorrect");
            alert.showAndWait();
        }

    }
}
