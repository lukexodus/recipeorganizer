package javaislanders.types;

import java.util.Map;

import javaislanders.types.Nutrient;

public class ParsedIngredient {
    private double quantity;
    private String measure;
    private String foodMatch;
    private String food;
    private String foodId;
    private double weight;
    private double retainedWeight;
    private Map<String, Nutrient> nutrients;
    private String measureURI;
    private String status;

    public ParsedIngredient() {
        // Default constructor
    }

    public ParsedIngredient(double quantity, String measure, String foodMatch, String food, String foodId, double weight, double retainedWeight, Map<String, Nutrient> nutrients, String measureURI, String status) {
        this.quantity = quantity;
        this.measure = measure;
        this.foodMatch = foodMatch;
        this.food = food;
        this.foodId = foodId;
        this.weight = weight;
        this.retainedWeight = retainedWeight;
        this.nutrients = nutrients;
        this.measureURI = measureURI;
        this.status = status;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getFoodMatch() {
        return foodMatch;
    }

    public void setFoodMatch(String foodMatch) {
        this.foodMatch = foodMatch;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getRetainedWeight() {
        return retainedWeight;
    }

    public void setRetainedWeight(double retainedWeight) {
        this.retainedWeight = retainedWeight;
    }

    public Map<String, Nutrient> getNutrients() {
        return nutrients;
    }

    public void setNutrients(Map<String, Nutrient> nutrients) {
        this.nutrients = nutrients;
    }

    public String getMeasureURI() {
        return measureURI;
    }

    public void setMeasureURI(String measureURI) {
        this.measureURI = measureURI;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
