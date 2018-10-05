package model.tagSystem;

import model.InvalidRenameException;
import model.imageSystem.Image;
import model.imageSystem.ImageManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagManagerTest {
    private TagManager tm;
    private ImageManager im;
    private Image coke;
    private Image pepsi;
    private final String testParentPath =
            "." + File.separator + "tests" + File.separator + "testImages" + File.separator;

    @BeforeEach
    void setUp() throws Exception {
        tm = new TagManager();
        im = new ImageManager();
        coke = new Image(testParentPath + "coke.jpg");
        pepsi = new Image(testParentPath + "pepsi.png");
        im.addImage(coke);
        im.addImage(pepsi);
    }

    @AfterEach
    void tearDown() {
        im.getImages().get(0).renameTo(new File(testParentPath + "coke.jpg"));
        im.getImages().get(1).renameTo(new File(testParentPath + "pepsi.png"));
    }

    @Test
    void addToAvailableTags() {
        Tag tag = new Tag("aTag");
        tm.addImageToTag(tag, coke);
        int place = tm.getAvailableTags().indexOf(tag);
        assertEquals(tm.getAvailableTags().size(), 1);
        assertEquals(tm.getAvailableTags().get(place).getAllImages().size(), 1);
    }

    @Test
    void deleteFromAvailableTags() {
        Tag tag = new Tag("aTag");
        tm.addImageToTag(tag, coke);
        tm.removeImageFromTag(tag, coke);
        int place = tm.getAvailableTags().indexOf(tag);
        assertEquals((tm.getAvailableTags().get(place).getAllImages().size()), 0);
    }

}
