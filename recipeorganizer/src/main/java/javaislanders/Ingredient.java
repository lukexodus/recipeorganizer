package javaislanders;

import java.util.List;

public class Ingredient {
    private String text;
    private List<ParsedIngredient> parsed;

    public Ingredient() {
        // Default constructor
    }

    public Ingredient(String text, List<ParsedIngredient> parsed) {
        this.text = text;
        this.parsed = parsed;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<ParsedIngredient> getParsed() {
        return parsed;
    }

    public void setParsed(List<ParsedIngredient> parsed) {
        this.parsed = parsed;
    }
}
