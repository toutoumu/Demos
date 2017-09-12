package com.example.glidev4;

import com.example.glidev4.glide.IPicture;

public class Picture implements IPicture {

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
