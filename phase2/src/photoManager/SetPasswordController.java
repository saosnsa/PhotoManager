package photoManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.imageSafeSystem.SafeManager;

import java.net.URL;
import java.util.ResourceBundle;

public class SetPasswordController implements Initializable{
    @FXML
    private Button setPassword = new Button();

    @FXML
    private TextField password = new TextField();

    private SafeManager safeManager;

    /**
     * Construct this class.
     */
    SetPasswordController(){}

    /**
     * Initialize this class
     *
     * @param location location needed to initialize
     * @param resources resources  needed to initialize
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Set safeManager of this class.
     *
     * @param safeManager the safeManager pass in to modify.
     */
    void setSafeManager(SafeManager safeManager) {
        this.safeManager = safeManager;
    }

    /**
     * Set password of this safeManager.
     */
    public void setPassword(){
            this.safeManager.setPassword(password.getText());
            Stage stage = (Stage)setPassword.getScene().getWindow();
            stage.close();
    }
}
