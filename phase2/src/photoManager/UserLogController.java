package photoManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserLogController implements Initializable{
    @FXML
    private ListView<String> log = new ListView<>();

    /**
     * Initialize this class.
     *
     * @param location location to initialize.
     * @param resources resources to initialize.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
    }

    /**
     * Construct this Class.
     *
     * @throws IOException When file for input/output is not found.
     */
    UserLogController() throws IOException {
        FileReader fileReader = new FileReader("logger.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            StringBuilder temp = new StringBuilder();
            line = line.trim();
            temp.append(line);
            temp.append(System.lineSeparator());
            log.getItems().add(temp.toString());
            line = bufferedReader.readLine();
            if (line != null) {
                line = line.trim();
                temp.append(line);
                log.getItems().add(temp.toString());
            }
        }
    }

    /**
     * Show the User Log.
     *
     * @throws IOException When file for input/output is not found.
     */
    void showUserLog() throws IOException {
        FileReader fileReader = new FileReader("logger.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            StringBuilder temp = new StringBuilder();
            line = line.trim();
            temp.append(line);
            temp.append(System.lineSeparator());
            line = bufferedReader.readLine();
            if (line != null) {
                line = line.trim();
                temp.append(line);
                log.getItems().add(temp.toString());
                line = bufferedReader.readLine();
            }
        }
    }
}
