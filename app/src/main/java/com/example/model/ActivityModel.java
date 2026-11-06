package com.example.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ActivityModel implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private String category; // CARDIO, BRAÇO, PERNA, COSTA, OUTROS

    @SerializedName("xpReward")
    private int xpReward;

    @SerializedName("targetFrequency")
    private String targetFrequency; // ex: "3x por semana", "Diário"

    @SerializedName("sets")
    private int sets; // ex: 3 séries

    @SerializedName("repetitions")
    private int repetitions; // ex: 10 repetições

    @SerializedName("weight")
    private String weight; // ex: "12 kg" ou "Peso corporal"

    @SerializedName("isActive")
    private boolean isActive;

    public ActivityModel() {}

    public ActivityModel(String id, String name, String description, String category, int xpReward, String targetFrequency, boolean isActive) {
        this(id, name, description, category, xpReward, targetFrequency, 3, 10, null, isActive);
    }

    public ActivityModel(String id, String name, String description, String category, int xpReward, 
                         String targetFrequency, int sets, int repetitions, String weight, boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.xpReward = xpReward;
        this.targetFrequency = targetFrequency;
        this.sets = sets;
        this.repetitions = repetitions;
        this.weight = weight;
        this.isActive = isActive;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }

    public String getTargetFrequency() { return targetFrequency; }
    public void setTargetFrequency(String targetFrequency) { this.targetFrequency = targetFrequency; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }

    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getSeriesAndRepsFormatted() {
        if (sets > 0 && repetitions > 0) {
            String text = sets + " x " + repetitions + " repetições";
            if (weight != null && !weight.trim().isEmpty()) {
                text += " (" + weight.trim() + ")";
            }
            return text;
        }
        if (targetFrequency != null && !targetFrequency.trim().isEmpty()) {
            return targetFrequency;
        }
        return "3 x 10 repetições";
    }
}
