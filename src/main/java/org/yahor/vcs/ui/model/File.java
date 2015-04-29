package org.yahor.vcs.ui.model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.ImageView;

/**
 * @author yahor-filipchyk
 */
public class File {
    private final SimpleObjectProperty<ImageView> icon;
    private final SimpleStringProperty filePath;

    public File(ImageView icon, String filePath) {
        this.icon = new SimpleObjectProperty<>(icon);
        this.filePath = new SimpleStringProperty(filePath);
    }

    public ImageView getIcon() {
        return icon.get();
    }

    public void setIcon(ImageView icon) {
        this.icon.set(icon);
    }

    public String getFilePath() {
        return filePath.get();
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }

    @Override
    public String toString() {
        return "File{" +
                "icon=" + icon.get() +
                ", filePath=" + filePath.get() +
                '}';
    }
}
