package com.example.livestockguardian.models;

import com.google.gson.annotations.SerializedName;

public class Livestock {
    @SerializedName("id")
    private String id;

    @SerializedName("animal_name")
    private String animalName;

    @SerializedName("species")
    private String species;

    @SerializedName("breed")
    private String breed;

    @SerializedName("gender")
    private String gender;

    @SerializedName("age_years")
    private double ageYears;

    @SerializedName("weight_kg")
    private double weightKg;

    @SerializedName("color")
    private String color;

    @SerializedName("status")
    private String status;

    @SerializedName("notes")
    private String notes;

    @SerializedName("owner_id")
    private String ownerId;

    public String getId() {
        return id;
    }

    public String getName() {
        return animalName;
    }

    public String getSpecies() {
        return species;
    }

    public String getBreed() {
        return breed;
    }

    public String getGender() {
        return gender;
    }

    public double getAge() {
        return ageYears;
    }

    public double getWeight() {
        return weightKg;
    }

    public String getColor() {
        return color;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public String getOwnerId() {
        return ownerId;
    }
}
