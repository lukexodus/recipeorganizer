package javaislanders.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Recipe {
    private String uri;
    private String title;
    private String group;
    private double yield;
    private int calories;
    private double totalCO2Emissions;
    private String co2EmissionsClass;
    private double totalWeight;
    private List<String> dietLabels;
    private List<String> healthLabels;
    private List<String> cautions;
    private Map<String, Nutrient> totalNutrients;
    private Map<String, Nutrient> totalDaily;
    private List<Ingredient> ingredients;
    private List<String> cuisineType;
    private List<String> mealType;
    private List<String> dishType;
    private Map<String, Nutrient> totalNutrientsKCal;

    public Recipe() {
        // Default constructor
    }

    public Recipe(String uri, String title, String group, double yield, int calories, double totalCO2Emissions, String co2EmissionsClass, double totalWeight, List<String> dietLabels, List<String> healthLabels, List<String> cautions, Map<String, Nutrient> totalNutrients, Map<String, Nutrient> totalDaily, List<Ingredient> ingredients, List<String> cuisineType, List<String> mealType, List<String> dishType, Map<String, Nutrient> totalNutrientsKCal) {
        this.uri = uri;
        this.title = title;
        this.group = group;
        this.yield = yield;
        this.calories = calories;
        this.totalCO2Emissions = totalCO2Emissions;
        this.co2EmissionsClass = co2EmissionsClass;
        this.totalWeight = totalWeight;
        this.dietLabels = dietLabels;
        this.healthLabels = healthLabels;
        this.cautions = cautions;
        this.totalNutrients = totalNutrients;
        this.totalDaily = totalDaily;
        this.ingredients = ingredients;
        this.cuisineType = cuisineType;
        this.mealType = mealType;
        this.dishType = dishType;
        this.totalNutrientsKCal = totalNutrientsKCal;
    }

    // Getters and setters
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public double getYield() {
        return yield;
    }

    public void setYield(double yield) {
        this.yield = yield;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getTotalCO2Emissions() {
        return totalCO2Emissions;
    }

    public void setTotalCO2Emissions(double totalCO2Emissions) {
        this.totalCO2Emissions = totalCO2Emissions;
    }

    public String getCo2EmissionsClass() {
        return co2EmissionsClass;
    }

    public void setCo2EmissionsClass(String co2EmissionsClass) {
        this.co2EmissionsClass = co2EmissionsClass;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public List<String> getDietLabels() {
        return dietLabels;
    }

    public void setDietLabels(List<String> dietLabels) {
        this.dietLabels = dietLabels;
    }

    public List<String> getHealthLabels() {
        return healthLabels;
    }

    public void setHealthLabels(List<String> healthLabels) {
        this.healthLabels = healthLabels;
    }

    public List<String> getCautions() {
        return cautions;
    }

    public void setCautions(List<String> cautions) {
        this.cautions = cautions;
    }

    public Map<String, Nutrient> getTotalNutrients() {
        return totalNutrients;
    }

    public void setTotalNutrients(Map<String, Nutrient> totalNutrients) {
        this.totalNutrients = totalNutrients;
    }

    public Map<String, Nutrient> getTotalDaily() {
        return totalDaily;
    }

    public void setTotalDaily(Map<String, Nutrient> totalDaily) {
        this.totalDaily = totalDaily;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(List<String> cuisineType) {
        this.cuisineType = cuisineType;
    }

    public List<String> getMealType() {
        return mealType;
    }

    public void setMealType(List<String> mealType) {
        this.mealType = mealType;
    }

    public List<String> getDishType() {
        return dishType;
    }

    public void setDishType(List<String> dishType) {
        this.dishType = dishType;
    }

    public Map<String, Nutrient> getTotalNutrientsKCal() {
        return totalNutrientsKCal;
    }

    public void setTotalNutrientsKCal(Map<String, Nutrient> totalNutrientsKCal) {
        this.totalNutrientsKCal = totalNutrientsKCal;
    }

    // Method to check if a recipe matches all criteria
    public boolean matchesCriteria(List<String> dietLabels, List<String> healthLabels, List<String> cuisineType, List<String> mealType, List<String> dishType) {
        // Check if all diet labels are present in the recipe
        if (!checkLabels(dietLabels, this.dietLabels)) {
            return false;
        }

        // Check if all health labels are present in the recipe
        if (!checkLabels(healthLabels, this.healthLabels)) {
            return false;
        }

        // Check if all cuisine types are present in the recipe
        if (!checkLabels(cuisineType, this.cuisineType)) {
            return false;
        }

        // Check if all meal types are present in the recipe
        if (!checkLabels(mealType, this.mealType)) {
            return false;
        }

        // Check if all dish types are present in the recipe
        if (!checkLabels(dishType, this.dishType)) {
            return false;
        }

        // If all criteria match, return true
        return true;
    }

    // Helper method to check if all labels are present in the recipe
    private boolean checkLabels(List<String> labels, List<String> recipeLabels) {
        if (labels.size() == 0) {
            return true; // No labels to match, so return true
        }
        for (String label : labels) {
            if (!recipeLabels.contains(label)) {
                return false;
            }
        }
        return true;
    }

    // Method to find recipes matching the specified criteria in the list
    public static List<Recipe> findRecipesByCriteria(List<Recipe> recipes, List<String> dietLabels, List<String> healthLabels, List<String> cuisineType, List<String> mealType, List<String> dishType) {
        List<Recipe> matchedRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipe.matchesCriteria(dietLabels, healthLabels, cuisineType, mealType, dishType)) {
                matchedRecipes.add(recipe);
            }
        }
        return matchedRecipes;
    }
}
