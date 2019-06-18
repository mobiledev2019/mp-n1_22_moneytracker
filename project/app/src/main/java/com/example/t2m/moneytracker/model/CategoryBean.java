package com.example.t2m.moneytracker.model;

import java.io.Serializable;

public class CategoryBean implements Serializable {
  private int id;
  private TransactionTypes type;
  private String icon;
  private String category;
  private int parentId;


  public CategoryBean() {
  }

  public CategoryBean(int id, int type, String category, String icon,  int parentId) {
    this.id = id;
    this.type = TransactionTypes.from(type);
    this.icon = icon;
    this.category = category;
    this.parentId = parentId;
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

  public void setType(TransactionTypes type) {
    this.type = type;
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

  public int getParentId() {
    return parentId;
  }

  public void setParentId(int parentId) {
    this.parentId = parentId;
  }


}
