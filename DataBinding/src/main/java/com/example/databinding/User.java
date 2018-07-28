package com.example.databinding;

public class User {
  private final String firstName;
  private final String lastName;
  private boolean visible;

  public User(String firstName, String lastName, boolean visible) {
    this.visible = visible;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public boolean isVisible() {
    return visible;
  }

  public boolean getVisible() {
    return visible;
  }
}