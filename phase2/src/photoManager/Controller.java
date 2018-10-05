package photoManager;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.InvalidRenameException;
import model.Model;
import model.directorySystem.Directory;
import model.imageSafeSystem.EncryptedImageManager;
import model.tagSystem.Tag;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Controller implements Initializable {
    private boolean toggled = true;
    @FXML
    private TextField addAllTag = new TextField();
    @FXML
    private Text toggleStatus = new Text();
    @FXML
    private Tab imageTags = new Tab();
    @FXML
    private Tab allTags = new Tab();
    @FXML
    private Tab chosenImage = new Tab();
    @FXML
    private Tab browseImageUnder = new Tab();
    @FXML
    private Tab browseImageIn = new Tab();
    @FXML
    private Text imageName = new Text();

    private Text toggledImageName = new Text(imageName.getText());
    @FXML
    ListView<Tag> allTagList = new ListView<>();
    @FXML
    ListView<Tag> tagList = new ListView<>();
    @FXML
    private ListView<String> nameHistoryList = new ListView<>();
    @FXML
    private ListView<model.imageSystem.Image> pendingImageList = new ListView<>();
    @FXML
    private ListView<model.imageSystem.Image> pendingImageListIn = new ListView<>();
    @FXML
    private ListView<ArrayList<String>> tagHistoryList = new ListView<>();
    @FXML
    private ListView<model.imageSystem.Image> imageList = new ListView<>();
    @FXML
    private TextField newImageName = new TextField();
    @FXML
    private TextField newTagName = new TextField();
    @FXML
    private ImageView image = new ImageView();
    @FXML
    private Button tagHistoryButton = new Button();
    @FXML
    private Button nameHistoryButton = new Button();
    @FXML
    private Button browseIn = new Button();
    @FXML
    private Button addToImage = new Button();
    @FXML
    private Button userLog = new Button();
    @FXML
    private Button removeFromAllImage = new Button();
    @FXML
    private Button browse = new Button();
    @FXML
    private Button move = new Button();
    @FXML
    private Button addTag = new Button();
    @FXML
    private Button removeTag = new Button();
    @FXML
    private Button add = new Button();
    @FXML
    private Button remove = new Button();
    @FXML
    private Button changeName = new Button();
    @FXML
    private Button toggleTag = new Button();
    @FXML
    private Button addToAllTag = new Button();
    @FXML
    private Button encryption = new Button();

    Model model;
    private Directory directory;
    model.imageSystem.Image curImage;
    private static final Logger logger = Logger.getLogger(Controller.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allTagList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tagList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public Controller(Model model) {
        this.model = model;
    }

    /**
     * Initialize the model
     *
     * @param model Initialized model
     */
    public void setModel(Model model) throws IOException {
        this.model = model;
        for (Tag t : this.model.tm.getAvailableTags()) {
            allTagList.getItems().addAll(t);
        }
        for (model.imageSystem.Image image : this.model.im.getImages()) {
            imageList.getItems().add(image);
        }
        File log = new File("./logger.txt");
        if (!log.exists()) {
            log.createNewFile();
        }
        FileHandler handler = new FileHandler(log.getPath(), true);
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);
    }

    /**
     * Add the typed tag to the selected image
     *
     * @throws InvalidRenameException invalid renaming
     */
    public void addTag() throws InvalidRenameException {
        if (curImage != null) {
            if (!model.im.getImages().contains(curImage)) {
                this.model.addImage(curImage);
            }
            String name = newTagName.getText();
            Tag t = new Tag(name);
            if (model.tm.getAvailableTags().contains(t)) {
                for (Tag tag : model.tm.getAvailableTags()) {
                    if (t.equals(tag)) {
                        t = tag;
                    }
                }
            }
            if (curImage != null && !tagList.getItems().contains(t)) tagList.getItems().add(t);
            curImage = model.addTag(t, curImage);
                model.tm.addImageToTag(t, curImage);
            logger.log(Level.FINE, "Add tag " + name + " to " + curImage.getName());
            refresh();
        }
    }

    /**
     * Add selected Tags to selected image.
     *
     * @throws InvalidRenameException invalid renaming
     */
    public void addToImage() throws InvalidRenameException {
        if (!allTagList.getSelectionModel().getSelectedItems().isEmpty()) {
            ArrayList<Tag> newTags = new ArrayList<>();
            for (Tag newTag : allTagList.getSelectionModel().getSelectedItems()) {
                if (!imageList.getSelectionModel().getSelectedItems().isEmpty() &&
                        !imageList.getSelectionModel().getSelectedItem().getTags().contains(newTag)) {
                    newTags.add(newTag);
                } else if (!pendingImageList.getSelectionModel().getSelectedItems().isEmpty() &&
                        !pendingImageList.getSelectionModel().getSelectedItem().getTags().contains(newTag)) {
                    newTags.add(newTag);
                } else if (!pendingImageListIn.getSelectionModel().getSelectedItems().isEmpty() &&
                        !pendingImageListIn.getSelectionModel().getSelectedItem().getTags().contains(newTag)) {
                    newTags.add(newTag);
                }
            }
            if (curImage != null) {
                if (!this.model.im.getImages().contains(curImage)) {
                    this.model.addImage(curImage);
                }

                curImage = this.model.addTags(newTags, curImage);
                logger.log(Level.FINE, "Add tags " + newTags + " to image " + curImage.getName());
            }
            refresh();
        }
    }

    /**
     * Remove a selected tag from the current image
     *
     * @throws InvalidRenameException invalid renaming
     */
    // Adapted from code provided by https://www.youtube.com/watch?v=uz2sWCnTq6E.
    public void removeTag() throws InvalidRenameException{
        if(!this.model.im.getImages().contains(curImage)){
            this.model.addImage(curImage);
        }
        if (!tagList.getSelectionModel().getSelectedItems().isEmpty()) {
            ArrayList<Tag> tags = new ArrayList<>();
            tags.addAll(tagList.getSelectionModel().getSelectedItems());
            if (curImage != null) {
                curImage = model.removeTags(tags, curImage);
                logger.log(Level.FINE, "Remove tags " + tags + " to " + curImage.getName());
                refresh();
            }
        }
    }

    /**
     * Change the name of current image
     *
     * @throws InvalidRenameException Invalid renaming
     */
    public void changeName() throws InvalidRenameException{
        if (curImage != null) {
            String oldName = curImage.getName();
            if (model.im.getImages().contains(curImage)) {
                curImage = model.im.changeImageName(curImage, newImageName.getText());
                logger.log(Level.FINE, "Change image's name: " + oldName + " -> " + curImage.getName());
                refresh();
            } else {
                model.addImage(curImage);
                curImage = model.im.changeImageName(curImage, newImageName.getText());
                logger.log(Level.FINE, "Change image's name: " + oldName + " -> " + curImage.getName());
                refresh();
            }
        }
    }

    /**
     * User choose to check name with or without tags
     *
     * @throws InvalidRenameException invalid renaming
     */
    public void toggleTag() throws InvalidRenameException {
        model.im.tagSwitch();
        toggled = !toggled;
        if (toggled) {
            toggleStatus.setText("Tags: ON");
        } else {
            toggleStatus.setText("Tags: OFF");
        }
        refresh();
    }

    /**
     * Add the selected image into the application
     *
     * @throws InvalidRenameException invalid naming
     */
    // Adapted from https://docs.oracle.com/javase/8/javafx/api/javafx/stage/FileChooser.html
    // Adapted from http://java-buddy.blogspot.ca/2013/01/use-javafx-filechooser-to-open-image.html
    public void addSingleImage() throws InvalidRenameException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser
                .getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("imageSystem Files",
                        "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.eng"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            this.model.addImage(new model.imageSystem.Image(selectedFile.getPath()));
            logger.log(Level.FINE, "Add new image: " + selectedFile.getName());
            refresh();
        }
    }

    /**
     * Show a list of all images under the selected directory (includes subdirectories)
     *
     * @throws InvalidRenameException invalid renaming
     */
    public void browse() throws InvalidRenameException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open all images under Folder");
        File selectDirectory = directoryChooser.showDialog(null);
        if (selectDirectory != null) {
            pendingImageListIn.getItems().clear();
            pendingImageList.getItems().clear();
            directory = new Directory(selectDirectory.getPath());
            ArrayList<model.imageSystem.Image> imagesUnder = model.browseUnder(directory);
                pendingImageList.getItems().addAll(imagesUnder);
            ArrayList<model.imageSystem.Image> imagesIn = model.browseIn(directory);
                pendingImageListIn.getItems().addAll(imagesIn);
        }
    }

    /**
     * Method called when an image selected in Chosen imageSystem
     */
    public void selectImageChosenImage(){
        selectImage(imageList);
        pendingImageList.getSelectionModel().clearSelection();
        pendingImageListIn.getSelectionModel().clearSelection();
    }

    /**
     * Method called when select image in browse under. When operate an image, it will be
     * automatically added to chosen ImageSystem
     */
    public void selectImageBrowseUnder() {
        selectImage(pendingImageList);
        pendingImageListIn.getSelectionModel().clearSelection();
        imageList.getSelectionModel().clearSelection();
    }

    /**
     * Method called when select image in browse in. When operate an image, it will be automatically
     * added to chosen ImageSystem
     */
    public void selectImageBrowseIn() {
        selectImage(pendingImageListIn);
        pendingImageList.getSelectionModel().clearSelection();
        imageList.getSelectionModel().clearSelection();
    }

    /**
     * remove an image from chosen image system
     */
    public void removeImage() throws InvalidRenameException {
        model.imageSystem.Image imageSelected = imageList.getSelectionModel().getSelectedItem();
        ObservableList<model.imageSystem.Image> allImages = imageList.getItems();
        allImages.removeAll(imageSelected);
        image.setImage(null);
        imageName.setText("");
        tagList.getItems().clear();
        tagHistoryList.getItems().clear();
        this.model.removeImage(imageSelected);
    }

    /**
     * remove a selected tag from all images who have this tag
     *
     * @throws InvalidRenameException Invalid renaming
     */
    public void removeFromAllImages() throws InvalidRenameException{
        if (!allTagList.getSelectionModel().getSelectedItems().isEmpty()) {
            Tag tagSelected = allTagList.getSelectionModel().getSelectedItem();
            Alert alert =
                    new Alert(
                            Alert.AlertType.CONFIRMATION,
                            "This tag has been used "
                                    + tagSelected.getAllImages().size()
                                    + " times. Are you sure you want to delete this tag from all the images?",
                            ButtonType.YES,
                            ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                this.model.deleteAllUsage(tagSelected);
                refresh();
            }
        }
    }

    /**
     * Move the image to a selected directory
     *
     * @throws InvalidRenameException Invalid renaming
     */
    public void moveImage() throws InvalidRenameException{
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Move the selected image to the selected directory");
        File selectDirectory = directoryChooser.showDialog(null);
        if (curImage != null && selectDirectory != null) {
            if(model.im.getImages().contains(curImage)) {
                model.im.moveImage(curImage, selectDirectory.getPath());
            } else {
                model.im.addImage(curImage);
                model.im.moveImage(curImage, selectDirectory.getPath());
            }
            refresh();
        }
    }

    /**
     * Revert the tags of image to selected set.
     *
     * @throws InvalidRenameException invalid renaming
     */
    public void tagRevertTo() throws InvalidRenameException{
        if (tagHistoryList.getSelectionModel().getSelectedItem() != null) {
            int targetHistory = tagHistoryList.getItems().indexOf(tagHistoryList.getSelectionModel().getSelectedItem());
            if (curImage != null) {
                curImage = this.model.changeTags(curImage, curImage.getTagHistory().get(targetHistory));
                refresh();
            }
        }
    }

    /**
     * Revert the name of image to selected name.
     *
     * @throws InvalidRenameException invalid renaming
     */
    public void nameRevertTo() throws InvalidRenameException {
        if (nameHistoryList.getSelectionModel().getSelectedItem() != null){
            String newName = nameHistoryList.getSelectionModel().getSelectedItem();
            if(curImage != null){
                curImage = this.model.im.changeImageName(curImage, newName);
                refresh();
            }
        }
    }


    /**
     *  Refreshes the interface.
     *
     * @throws InvalidRenameException invalid renaming
     */
    void refresh() throws InvalidRenameException {
        nameHistoryList.getItems().clear();
        tagHistoryList.getItems().clear();
        imageList.getItems().clear();
        tagList.getItems().clear();
        allTagList.getItems().clear();
        for (Tag t : this.model.tm.getAvailableTags()) {
            allTagList.getItems().add(t);
        }
        if (curImage != null) {
            imageName.setText(curImage.getPath());
            for (Tag tag : curImage.getTags()) {
                tagList.getItems().add(tag);
            }
            for (ArrayList<Tag> historyTag : curImage.getTagHistory()) {
                ArrayList<String> history = new ArrayList<>();
                for (Tag tag : historyTag) {
                    history.add(tag.getTagName());
                }
                tagHistoryList.getItems().add(history);
            }
            for (String historyName:curImage.getNameHistory()){
                nameHistoryList.getItems().add(historyName);
            }
            imageName.setText(curImage.getPath());
        }else{
            imageName.setText("");
            image.setImage(null);
        }
        for (model.imageSystem.Image i : this.model.im.getImages()) {
            imageList.getItems().add(i);
        }
        if (directory != null) {
            Directory dir = new Directory(directory.getPath());
            pendingImageList.getItems().clear();
            pendingImageListIn.getItems().clear();
            for (model.imageSystem.Image imageUnder : dir.findUnderDirectory()) {
                pendingImageList.getItems().add(imageUnder);
            }
            for (model.imageSystem.Image imageIn : dir.findInDirectory()) {
                pendingImageListIn.getItems().add(imageIn);
            }
        }
        if (imageList.getItems().contains(curImage)) {
            imageList.getSelectionModel().select(curImage);
        }
        if (pendingImageList.getItems().contains(curImage)) {
            pendingImageList.getSelectionModel().select(curImage);
        }
        if (pendingImageListIn.getItems().contains(curImage)) {
            pendingImageListIn.getSelectionModel().select(curImage);
        }
    }

    /**
     * Go to Log of User.
     *
     * @throws IOException File not found.
     */
    //
    // https://stackoverflow.com/questions/23704723/in-javafx-scene-builder-how-do-i-open-a-new-window-when-clicking-a-button
    public void goToUserLog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("UserLog.fxml"));
        fxmlLoader.setControllerFactory(
                aClass -> {
                    try {
                        return new UserLogController();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "what";
                    }
                });
        Parent root = fxmlLoader.load();
        UserLogController userLog = fxmlLoader.getController();
        userLog.showUserLog();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOpacity(1);
        stage.setTitle("User Log");
        stage.setScene(new Scene(root, 600, 450));
        stage.showAndWait();
    }


    /**
     * Add a tag to all tags.
     *
     * @throws IOException File not found
     * @throws InvalidRenameException invalid renaming
     */
    public void addToAllTags() throws IOException, InvalidRenameException {
        Tag t = new Tag(addAllTag.getText());
        if(!this.model.tm.getAvailableTags().contains(t)){
            this.model.tm.createTag(addAllTag.getText());
            refresh();
        }
    }

    /**
     * Open the Login interface if user has set password, else opens safe/encryption system.
     *
     *
     * @throws IOException When file for input/output is not found.
     * @throws ClassNotFoundException When the class being de-serialized does not exist.
     * @throws NoSuchAlgorithmException When the cryptographic algorithm requested does not exist.
     * @throws NoSuchPaddingException  When the padding mechanism requested does not exist.
     * @throws BadPaddingException When the data is not padded properly for the padding mechanism requested.
     * @throws InvalidKeyException When the coding/length of the key is invalid or key is uninitialized.
     * @throws IllegalBlockSizeException When the block provided does not match the block size of the Cipher.
     */
    public void openLogin() throws IOException, ClassNotFoundException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidRenameException {
        if(this.model.sm.isPassWordExist()) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("Login.fxml"));
            fxmlLoader.setControllerFactory(
                    new Callback<Class<?>, Object>() {
                        @Override
                        public Object call(Class<?> aClass) {
                            return new LoginController();
                        }
                    });
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOpacity(1);
            LoginController loginController = fxmlLoader.getController();
            loginController.setController(this);
            stage.setTitle("Please enter your password.");
            stage.setScene(new Scene(root, 500, 500));
            stage.showAndWait();
        }else{
            this.openEncryption();
            curImage = null;
            this.refresh();
        }
    }

    /**
     * When exit the new interface, pop up option for setting password.
     *
     *
     * @throws IOException File not found
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
        Stage setPassword = new Stage();
        setPassword.initModality(Modality.APPLICATION_MODAL);
        SetPasswordController sc = fxmlLoader.getController();
        sc.setSafeManager(this.model.sm);
        setPassword.setScene(new Scene(root, 500, 400));
        setPassword.setTitle("Set password");
        setPassword.show();
    }

    /**
     * Opens safe/encryption system.
     *
     *
     * @throws IOException When file for input/output is not found.
     * @throws ClassNotFoundException When the class being de-serialized does not exist.
     * @throws NoSuchAlgorithmException When the cryptographic algorithm requested does not exist.
     * @throws NoSuchPaddingException  When the padding mechanism requested does not exist.
     * @throws BadPaddingException When the data is not padded properly for the padding mechanism requested.
     * @throws InvalidKeyException When the coding/length of the key is invalid or key is uninitialized.
     * @throws IllegalBlockSizeException When the block provided does not match the block size of the Cipher.
     */

    private void openEncryption() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException, InvalidRenameException {
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
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        EncryptedImageManager encrypted = new EncryptedImageManager();
        EncryptionController controller = fxmlLoader.getController();
        controller.setController(this);
        controller.setEncryptedImageManager(encrypted);
        stage.setTitle("Encryption Interface");
        stage.setScene(new Scene(root, 1280, 900));
        if(!this.model.sm.isPassWordExist()) {
            stage.setOnCloseRequest(event -> {
                try {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to set password?",
                            ButtonType.YES,
                            ButtonType.NO);
                    alert.showAndWait();
                    if(alert.getResult()==(ButtonType.YES)){
                        this.setPassword();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        stage.showAndWait();
    }

    /**
     * Select image from ListView and update the interface.
     *
     * @param list the selected ListView
     */
    private void selectImage(ListView<model.imageSystem.Image> list) {
        ArrayList<String> tags = new ArrayList<>();
        tagList.getItems().clear();
        tagHistoryList.getItems().clear();
        nameHistoryList.getItems().clear();
        if (list.getSelectionModel().getSelectedItem() != null) {
            curImage = list.getSelectionModel().getSelectedItem();
            if(this.model.im.getImages().contains(curImage)){
                curImage = this.model.im.getImages().get(this.model.im.getImages().indexOf(curImage));
            }
            Image img = new Image(curImage.toURI().toString());
            image.setImage(img);
            Path path = Paths.get(curImage.getPath());
            imageName.setText(path.toString());
            nameHistoryList.getItems().addAll(curImage.getNameHistory());
            tagList.getItems().addAll(curImage.getTags());
            for (ArrayList<Tag> a : curImage.getTagHistory()) {
                for (Tag t : a) {
                    tags.add(t.getTagName());
                }
                tagHistoryList.getItems().add(tags);
                tags = new ArrayList<>();
            }
        }
    }
}
