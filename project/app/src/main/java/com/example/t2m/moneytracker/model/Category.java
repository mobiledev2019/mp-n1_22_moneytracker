package com.example.t2m.moneytracker.model;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {
    private int id;
    private TransactionTypes type;
    private String icon;
    private String category;
    private List<Category> subCategories;



    public Category() {
    }

    public Category(int id, int type, String category, String icon,  List<Category> subCategories) {
        this.id = id;
        this.type = TransactionTypes.from(type);
        this.icon = icon;
        this.category = category;
        this.subCategories = subCategories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TransactionTypes getType() {
        return type;
    }

    public void setType(int type) {
        this.type = TransactionTypes.from(type);
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
    }
}
