package model;

import model.imageSystem.ImageManager;
import model.tagSystem.TagManager;

import java.io.IOException;

public class Model {
    public TagManager tm;
    public ImageManager im;

    public Model() throws IOException, ClassNotFoundException{}

    public void setTm(TagManager tm) {
        this.tm = tm;
    }

    public void setIm(ImageManager im) {
        this.im = im;
    }
}
