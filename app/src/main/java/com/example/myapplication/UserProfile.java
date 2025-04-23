
package com.example.myapplication;


public class UserProfile {
    private String name, age, gender, weight, height, profileImage;

    public UserProfile() {

    }

    public UserProfile(String name, String age, String gender, String weight, String height) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
    }

    public String getName() { return name; }
    public String getAge() { return age; }
    public String getGender() { return gender; }
    public String getWeight() { return weight; }
    public String getHeight() { return height; }
    public String getProfileImage() { return profileImage; }

    public void setName(String name) { this.name = name; }
    public void setAge(String age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setWeight(String weight) { this.weight = weight; }
    public void setHeight(String height) { this.height = height; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
}
