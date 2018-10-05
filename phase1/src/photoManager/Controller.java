package photoManager;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import model.InvalidRenameException;
import model.Model;
import model.directorySystem.Directory;
import model.imageSystem.ImageManager;
import model.tagSystem.Tag;
import model.tagSystem.TagManager;
import sun.security.ssl.HandshakeInStream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Controller {
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

    private boolean toggled = false;
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
    private Text imageName = new Text();

    private Text toggledImageName = new Text(imageName.getText());
    @FXML
    ListView<String> allTagList = new ListView<>();
    @FXML
    ListView<String> tagList = new ListView<>();
    @FXML
    private ListView<model.imageSystem.Image> pendingImageList = new ListView<>();
    @FXML
    private ListView<model.imageSystem.Image> pendingImageListIn = new ListView<>();
    @FXML
    private ListView<ArrayList<String>> historyList = new ListView<>();
    @FXML
    private ListView<model.imageSystem.Image> imageList = new ListView<>();
    @FXML
    private TextField newImageName = new TextField();
    @FXML
    private TextField newTagName = new TextField();
    @FXML
    private ImageView image = new ImageView();
    @FXML
    private Button historyButton = new Button();
    @FXML
    private Button browseIn = new Button();
    private Model model;
    private Directory directory;
    private model.imageSystem.Image curImage;
    private static final Logger logger = Logger.getLogger(Controller.class.getName());
    private static FileHandler handler;

    /**
     * Initialize the model
     *
     * @param model Initialized model
     */
    public void setModel(Model model) throws IOException {
        this.model = model;
        for (Tag t : this.model.tm.getAvailableTags().keySet()) {
            allTagList.getItems().addAll(t.getTagName());
        }
        for (model.imageSystem.Image image : this.model.im.getImages()) {
            imageList.getItems().add(image);
        }
        File log = new File("./logger.txt");
        if (!log.exists()) {
            log.createNewFile();
        }
        handler = new FileHandler(log.getPath(), true);
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);
    }

    public TagManager getTagManager() {
        return model.tm;
    }

    public ImageManager getImageManager() {
        return model.im;
    }

    /**
     * Add the typed tag to the selected image
     *
     * @throws InvalidRenameException invalid renaming
     */
    public void AddTag() throws InvalidRenameException, IOException {
        if (curImage != null) {
            String name = newTagName.getText();
            Tag t = new Tag(name);
            if (curImage != null && !tagList.getItems().contains(name)) tagList.getItems().add(name);
            curImage = model.im.addTag(name, curImage, model.tm);
            if (!allTagList.getItems().contains(name)) {
                allTagList.getItems().add(name);
                model.tm.addToAvailableTags(t);
            }
            refresh();
            logger.log(Level.FINE, "Add tag " + name + "to " + curImage.getName());
        }
    }

    /**
     * Remove a selected tag from the current image
     *
     * @throws InvalidRenameException invalid renaming
     */
    // Adapted from code provided by https://www.youtube.com/watch?v=uz2sWCnTq6E.
    public void RemoveTag() throws InvalidRenameException, IOException {
        String tagSelected = tagList.getSelectionModel().getSelectedItem();
        if (curImage != null) {
            curImage = model.im.deleteTag(new Tag(tagSelected), curImage, model.tm);
            refresh();
            logger.log(Level.FINE, "Remove tag " + tagSelected + "to " + curImage.getName());
        }
    }

    /**
     * Change the name of current image
     *
     * @throws InvalidRenameException Invalid renaming
     */
    public void ChangeName() throws InvalidRenameException, IOException {
    if (curImage != null) {
        String oldName = curImage.getName();
        curImage = model.im.changeImageName(curImage, newImageName.getText());
        refresh();
        logger.log(Level.FINE, "Change image's name: " + oldName + " -> " + curImage.getName() );
    }
    }

    /**
     * User choose to check name with or without tags
     */
    public void ToggleTag() {
        if (!toggled) {
            ObservableList<String> tags = tagList.getItems();
            final String[] hashTags = {""};
            tags.forEach(
                    (tag) -> {
                        hashTags[0] += "@" + tag;
                    });
            imageName.setText(imageName.getText() + hashTags[0]);
            toggled = true;
        } else {
            imageName.setText(toggledImageName.getText());
            toggled = false;
        }
    }

    /**
     * Add the selected image into the application
     */
    // Adapted from https://docs.oracle.com/javase/8/javafx/api/javafx/stage/FileChooser.html
    // Adapted from http://java-buddy.blogspot.ca/2013/01/use-javafx-filechooser-to-open-image.html
    public void Add() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser
                .getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("imageSystem Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);
        this.model.im.addImage(new model.imageSystem.Image(selectedFile.getPath()));
        refresh();
        logger.log(Level.FINE,"Add new image: " + selectedFile.getName());
    }

    /**
     * Show a list of all images under the selected directory (includes subdirectories)
     *
     * @throws IOException Directory may not found
     */
    public void Browse() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open all images under Folder");
        File selectDirectory = directoryChooser.showDialog(null);
        directory = new Directory(selectDirectory.getPath());
        ArrayList<model.imageSystem.Image> imagesUnder = directory.findUnderDirectory();
        for (model.imageSystem.Image image1 : imagesUnder) {
            pendingImageList.getItems().add(image1);
        }
        ArrayList<model.imageSystem.Image> imagesIn = directory.findInDirectory();
        for (model.imageSystem.Image image2 : imagesIn) {
            pendingImageListIn.getItems().add(image2);
        }
    }

    /**
     * Method called when an image selected in Chosen imageSystem
     */
    public void SelectImageChosenImage() throws IOException {
        ArrayList temp = new ArrayList();
        tagList.getItems().remove(0, tagList.getItems().size());
        historyList.getItems().remove(0, historyList.getItems().size());
        if (imageList.getSelectionModel().getSelectedItem() != null) {
            curImage = imageList.getSelectionModel().getSelectedItem();
            Image img = new Image(curImage.toURI().toString());
            this.model.im.addImage(curImage);
            image.setImage(img);
            Path path = Paths.get(curImage.getPath());
            imageName.setText(path.getFileName().toString());
            for (Tag t : curImage.getTags()) {
                tagList.getItems().add(t.getTagName());
            }
            for (ArrayList<Tag> a : curImage.getTagHistory()) {
                for (Tag t : a) {
                    temp.add(t.getTagName());
                }
                historyList.getItems().add(temp);
                temp = new ArrayList<>();
            }
            pendingImageList.getSelectionModel().clearSelection();
            pendingImageListIn.getSelectionModel().clearSelection();
        }
    }

    /**
     * Method called when select image in browse under. When operate an image, it will be automatically added to chosen ImageSystem
     */
    public void SelectImageBrowseUnder() {
        ArrayList temp = new ArrayList();
        tagList.getItems().remove(0, tagList.getItems().size());
        historyList.getItems().remove(0, historyList.getItems().size());
        if (pendingImageList.getSelectionModel().getSelectedItem() != null) {
            curImage =
                    new model.imageSystem.Image(
                            pendingImageList.getSelectionModel().getSelectedItem().toString());
            Image img = new Image(curImage.toURI().toString());
            this.model.im.addImage(curImage);
            image.setImage(img);
            for (Tag t : curImage.getTags()) {
                tagList.getItems().add(t.getTagName());
            }
            for (ArrayList<Tag> a : curImage.getTagHistory()) {
                for (Tag t : a) {
                    temp.add(t.getTagName());
                }
                historyList.getItems().add(temp);
                temp = new ArrayList<>();
            }
            Path path = Paths.get(curImage.getPath());
            imageName.setText(path.getFileName().toString());
            imageList.getSelectionModel().clearSelection();
            pendingImageListIn.getSelectionModel().clearSelection();
            imageList.getSelectionModel().clearSelection();
        }
    }

    /**
     * Method called when select image in browse in. When operate an image, it will be automatically added to chosen ImageSystem
     */
    public void SelectImageBrowseIn() {
        ArrayList temp = new ArrayList();
        tagList.getItems().remove(0, tagList.getItems().size());
        historyList.getItems().remove(0, historyList.getItems().size());
        if (pendingImageListIn.getSelectionModel().getSelectedItem() != null) {
            curImage =
                    new model.imageSystem.Image(
                            pendingImageListIn.getSelectionModel().getSelectedItem().toString());
            Image img = new Image(curImage.toURI().toString());
            boolean contains = false;
            for (model.imageSystem.Image i : this.model.im.getImages()) {
                if (i.getPath().equals(curImage.getPath())) contains = true;
                curImage = i;
            }
            if (!contains) this.model.im.addImage(curImage);
            this.model.im.addImage(curImage);
            image.setImage(img);
            for (Tag t : curImage.getTags()) {
                tagList.getItems().add(t.getTagName());
            }
            for (ArrayList<Tag> a : curImage.getTagHistory()) {
                for (Tag t : a) {
                    temp.add(t.getTagName());
                }
                historyList.getItems().add(temp);
                temp = new ArrayList<>();
            }
            Path path = Paths.get(curImage.getPath());
            imageName.setText(path.getFileName().toString());
            pendingImageList.getSelectionModel().clearSelection();
            imageList.getSelectionModel().clearSelection();
        }
    }

    /**
     * remove an image from chosen image system
     */
    public void removeImage() {
        model.imageSystem.Image imageSelected = imageList.getSelectionModel().getSelectedItem();
        ObservableList<model.imageSystem.Image> allImages = imageList.getItems();
        allImages.removeAll(imageSelected);
        image.setImage(null);
        imageName.setText("");
        tagList.getItems().removeAll();
        this.model.im.removeImage(imageSelected.getPath(), this.model.tm);
    }

    /**
     * remove a selected tag from all images who have this tag
     *
     * @throws InvalidRenameException Invalid renaming
     */
    public void PopUp() throws InvalidRenameException {
        String tagSelected = allTagList.getSelectionModel().getSelectedItem();
        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION,
                        "This will delete selected tag from all images, are you sure?",
                        ButtonType.YES,
                        ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            imageList.getItems().clear();
            this.model.tm.deleteAllUsage(new Tag(tagSelected), this.model.im);
            allTagList.getItems().remove(tagSelected);
            tagList.getItems().remove(tagSelected);
            for (model.imageSystem.Image i : this.model.im.getImages()) {
                imageList.getItems().add(i);
            }
        }
    }

    /**
     * Move the image to a selected directory
     *
     * @throws InvalidRenameException Invalid renaming
     */
    public void moveImage() throws InvalidRenameException, IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Move the selected image to the selected directory");
        File selectDirectory = directoryChooser.showDialog(null);
        if (curImage != null && selectDirectory != null) {
            imageList.getItems().add(model.im.moveImage(curImage, selectDirectory.getPath()));
            refresh();
        }
    }


    public void switchTo() throws InvalidRenameException, IOException {
        int i = historyList.getItems().indexOf(historyList.getSelectionModel().getSelectedItem());
        if(curImage != null){
            this.model.im.goToPreviousTags(curImage, curImage.getTagHistory().get(i), this.directory);
        }
    }

    private void refresh() throws IOException {
        historyList.getItems().remove(0, historyList.getItems().size());
        imageList.getItems().remove(0, imageList.getItems().size());
        ArrayList<String> temp = new ArrayList<>();
        tagList.getItems().remove(0, tagList.getItems().size());
        allTagList.getItems().remove(0, allTagList.getItems().size());
        for (Tag t : this.model.tm.getAvailableTags().keySet()) {
            allTagList.getItems().add(t.getTagName());
        }
        if (curImage != null) {
            for (Tag t : curImage.getTags()) {
                tagList.getItems().add(t.getTagName());

        for (ArrayList<Tag> a : curImage.getTagHistory()) {
            for (Tag tag : a) {
                temp.add(tag.getTagName());
            }
            historyList.getItems().add(temp);
            temp = new ArrayList<>();}
        }
        }for(model.imageSystem.Image i: this.model.im.getImages()){
            imageList.getItems().add(i);
        }
        if (directory != null) {
            Directory dir = new Directory(directory.getPath());
            pendingImageList.getItems().remove(0, pendingImageList.getItems().size());
            pendingImageListIn.getItems().remove(0, pendingImageListIn.getItems().size());
            for (model.imageSystem.Image i1 : dir.findUnderDirectory()) {
                pendingImageList.getItems().add(i1);
            }
            for (model.imageSystem.Image i2 : dir.findInDirectory()) {
                pendingImageListIn.getItems().add(i2);
            }
            }
            curImage = null;
        }
    }

