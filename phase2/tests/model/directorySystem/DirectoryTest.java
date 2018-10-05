package model.directorySystem;

import model.InvalidRenameException;
import model.imageSystem.Image;
import model.imageSystem.ImageManager;
import model.tagSystem.TagManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryTest {
    private Directory directory;
    private ArrayList<Image> images;

    @BeforeEach
    void setUp() throws IOException, ClassNotFoundException, InvalidRenameException {
        directory = new Directory("./tests/testImages");
        images = new ArrayList<>();
        images.add(new Image("./tests/testImages/coke.jpg"));
        images.add(new Image("./tests/testImages/dr.pepper.jpg"));
        images.add(new Image("./tests/testImages/pepsi.png"));
        images.add(new Image("./tests/testImages/sprite.jpg"));

    }

    @Test
    void findUnderDirectory(){
        images.add(new Image("./tests/testImages/innerDirectory/Nestea.jpg"));
        boolean isEqual = true;
        for (Image image: images){
            if(!directory.findUnderDirectory().contains(image)){
                isEqual = false;
            }
        }
        assertTrue(isEqual);
    }

    @Test
    void findInDirectory() {
        boolean isEqual = true;
        for(Image image: images){
            if(!directory.findInDirectory().contains(image)){
                isEqual = false;
            }
        }
        assertTrue(isEqual);
    }

}