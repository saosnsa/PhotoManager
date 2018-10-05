package model.imageSystem;

import model.tagSystem.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ImageTest {

    private final String testParentPath = "." + File.separator
            + "tests"
            + File.separator
            + "testImages"
            + File.separator;
    private Image coke = new Image(testParentPath + "coke.jpg");
    private Image pepsi = new Image(testParentPath + "pepsi.png");
    private Image sprite = new Image(testParentPath + "sprite.jpg");
    private Tag cokeTag = new Tag("coke");
    private Tag pepsiTag = new Tag("pepsi");
    private Tag spriteTag = new Tag("sprite");

    @Test
    void testAddTag() {
        coke.addTag(cokeTag);
        assertEquals(coke.getTags().get(0), cokeTag);
    }

    @Test
    void testDeleteTag() {
        coke.addTag(pepsiTag);
        coke.removeTag(pepsiTag);
        assertEquals(coke.getTags().size(), 0);
    }

    @Test
    void testUpdateTagHistory() {
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(cokeTag);
        tags.add(spriteTag);
        coke.setTags(tags);
        coke.updateTagHistory();
        assertEquals(coke.getTagHistory().get(0), tags);
    }

    @Test
    void testUpdateNameHistory(){
        coke.setOriginalName("Cocacola");
        coke.updateNameHistory();
        assertEquals(coke.getNameHistory().get(1), "Cocacola");
    }

    @Test
    void testGetTagHistory() {
        ArrayList<ArrayList<Tag>> history = new ArrayList<>();
        ArrayList<Tag> subHistory1 = new ArrayList<>();
        ArrayList<Tag> subHistory2 = new ArrayList<>();
        subHistory1.add(pepsiTag);
        subHistory2.add(pepsiTag);
        subHistory2.add(cokeTag);
        history.add(subHistory1);
        history.add(subHistory2);
        pepsi.addTag(pepsiTag);
        pepsi.updateTagHistory();
        pepsi.addTag(cokeTag);
        pepsi.updateTagHistory();
        assertEquals(pepsi.getTagHistory(), history);
    }

    @Test
    void testGetTags() {
        sprite.addTag(spriteTag);
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(spriteTag);
        assertEquals(sprite.getTags(),tags );
    }

    @Test
    void testGetOriginalName() {

        assertEquals(sprite.getOriginalName(), "sprite");
    }

    @Test
    void testSetTags() {
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(pepsiTag);
        pepsi.setTags(tags);
        assertEquals(pepsi.getTags(), tags);
    }

    @Test
    void testSetOriginalName() {
        coke.setOriginalName("drink");
        assertEquals(coke.getOriginalName(), "drink");
    }


    @Test
    void testTagSwitch(){
        coke.addTag(new Tag("cola"));
        assertEquals(coke.toString(), "coke @cola.jpg");
        Image.tagSwitch();
        assertEquals(coke.toString(), "coke.jpg");
    }
}