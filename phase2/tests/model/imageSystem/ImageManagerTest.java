package model.imageSystem;

import model.InvalidRenameException;
import model.tagSystem.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ImageManagerTest {
    private Image coke;
    private Image pepsi;
    private Image sprite;
    private ImageManager im;
    private ArrayList<Image> parallel;
    private final String testParentPath = "." + File.separator
            + "tests"
            + File.separator
            + "testImages"
            + File.separator;

    @BeforeEach
    void setUp() throws Exception {
        im = new ImageManager();
        coke = new Image(testParentPath + "coke.jpg");
        pepsi = new Image(testParentPath + "pepsi.png");
        sprite = new Image(testParentPath + "sprite.jpg");
        parallel = new ArrayList<>();
        im.addImage(coke);
        parallel.add(coke);
        im.addImage(pepsi);
        parallel.add(pepsi);
        im.addImage(sprite);
        parallel.add(sprite);
    }

    @AfterEach
    void tearDown() throws InvalidRenameException {
        if (im.getImages().size() >= 1)
        im.getImages().get(0).renameTo(new File(testParentPath + "coke.jpg"));

        if (im.getImages().size() >= 2)
        im.getImages().get(1).renameTo(new File(testParentPath + "pepsi.png"));

        if (im.getImages().size() >= 3)
        im.getImages().get(2).renameTo(new File(testParentPath + "sprite.jpg"));

    }

    @Test
    void getImages() {
        assertEquals(im.getImages(), parallel);
    }

    @Test
    void addImage() {
        Image drPepper = new Image(testParentPath + "dr.pepper.jpg");
        im.addImage(drPepper);
        assertEquals(im.getImages().get(im.getImages().size() - 1), drPepper);
    }

    @Test
    void removeImage() {
        im.removeImage(sprite);
        assertEquals(im.getImages().size(), 2);
    }

    @Test
    void addTag() throws InvalidRenameException {
        Image image = im.addTag(new Tag("coke"), coke);
        assertEquals(image.getPath(),testParentPath + "coke @coke.jpg");
        assertTrue(image.exists());
    }

    @Test
    void changeImageName() throws InvalidRenameException {
        Image image = im.changeImageName(coke, "CocaCola");
        assertEquals(image.getPath(), testParentPath + "CocaCola.jpg");
        assertTrue(image.exists());
    }

    @Test
    void testAddTagAfterMove() throws InvalidRenameException {
        Tag cold = new Tag("cold");
        Image image = im.moveImage(coke, testParentPath + "innerDirectory");
        image = im.addTag(cold, image);
        assertEquals(image.getPath(), testParentPath + "innerDirectory" + File.separator + "coke @cold.jpg");
        assertTrue(image.exists());
    }

    @Test
    void testRemoveTagAfterMove() throws InvalidRenameException {
        Tag cold = new Tag("cold");
        Image image = im.addTag(cold, coke);
        image = im.moveImage(image, testParentPath + "innerDirectory");
        image = im.removeTag(cold, image);
        assertEquals(image.getPath(),testParentPath + "innerDirectory" + File.separator + "coke.jpg");
        assertTrue(image.exists());
    }

    @Test
    void testAddTagAfterChangeName() throws InvalidRenameException {
        Tag cold = new Tag("cold");
        Image image = im.changeImageName(coke, "zeroCoke");
        image = im.addTag(cold, image);
        assertEquals(image.getPath(), testParentPath + "zeroCoke @cold.jpg");
        assertTrue(image.exists());
    }

    @Test
    void removeTag() throws InvalidRenameException {
        Tag cold = new Tag("cold");
        Image temp = im.addTag(cold, sprite);
        temp = im.removeTag(cold, temp);
        assertEquals(temp.getPath(), testParentPath + "sprite.jpg");
        assertTrue(temp.exists());
    }

    @Test
    void changeTags() throws InvalidRenameException {
        Tag cold = new Tag("cold");
        Tag icy = new Tag("ICY");
        Image temp = im.addTag(cold, pepsi);
        temp = im.addTag(icy, temp);
        ArrayList<Tag> tags = temp.getTagHistory().get(1);
        temp = im.changeTags(temp, tags);
        assertEquals(temp.getTags(), tags);
        assertEquals(temp.getPath(), testParentPath + "pepsi @cold @ICY.png");
    }

    @Test
    void moveImage() throws InvalidRenameException {
        Image temp = im.moveImage(pepsi, testParentPath + "innerDirectory");
        assertEquals(temp.getPath(), testParentPath + "innerDirectory" + File.separator + "pepsi.png");
        assertTrue(temp.exists());
    }

}