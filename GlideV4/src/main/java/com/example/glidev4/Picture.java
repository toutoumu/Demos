package com.example.glidev4;

public class Picture {

  public Picture() {

  }

  public Picture(String fileName) {
    this.fileName = fileName;
  }

  private String fileName;

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
}
