package application.model;


import application.util.PropertyFields;
import application.util.PropertyManager;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.List;

public class RecentItems {
    private final int maxSize;

    ObservableList<String> items = FXCollections.observableArrayList();

    public RecentItems(final int maxSize) {
        this.maxSize = maxSize;
    }

    public void addListener(ListChangeListener<String> listChangeListener) {
        items.addListener(listChangeListener);
    }

    public void removeListener(ListChangeListener<String> listChangeListener) {
        items.removeListener(listChangeListener);
    }

    public void loadFromProperties(String string) {
        for (String fileName : string.split(",")) {
            push(fileName);
        }
    }

    public void storeToProperties() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(items.get(0));
        for (int i = 1; i < items.size(); i++) {
            stringBuilder.append(",").append(items.get(i));
        }
        PropertyManager.getInstance().getProperties().setProperty(PropertyFields.LAST_OPENED, stringBuilder.toString());
        try {
            PropertyManager.getInstance().storeProperties("Persist last opened");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void push(String item) {
        items.remove(item);
        items.add(0, item);
        if (items.size() > maxSize) {
            items.remove(items.size() - 1);
        }
        storeToProperties();
    }

    public void remove(String item) {
        items.remove(item);
        storeToProperties();
    }

    public String get(int index) {
        return items.get(index);
    }

    public List<String> getItems() {
        return items;
    }

    public int size() {
        return items.size();
    }
}

