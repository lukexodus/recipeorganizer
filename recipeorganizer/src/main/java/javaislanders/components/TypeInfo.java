package javaislanders.components;

import java.awt.*;
import javax.swing.*;

import javaislanders.types.Recipe;

public class TypeInfo extends JScrollPane {
    public TypeInfo (Recipe recipe) {
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setBorder(new RoundedBorder(20));

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints nutritionFactsGbc = new GridBagConstraints();
        nutritionFactsGbc.fill = GridBagConstraints.HORIZONTAL;
        nutritionFactsGbc.insets = new Insets(5, 5, 5, 5);

        int gridY = 0;

        JLabel nutritionFactsHeader = new JLabel("Type Information", SwingConstants.LEFT);
        nutritionFactsHeader.setFont(new Font("Inter", Font.BOLD, 20));
        addComponent(panel, nutritionFactsHeader, nutritionFactsGbc, 
        0, gridY, 2, 1, GridBagConstraints.CENTER);

        JSeparator sp1 = new JSeparator();
        sp1.setPreferredSize(new Dimension(300, 10));
        addComponent(panel, sp1, nutritionFactsGbc, 
        0, ++gridY, 2, 1, GridBagConstraints.CENTER);

        if (recipe == null || (recipe.getCalories() == 0)) {
            JLabel noData = new JLabel("No Data", SwingConstants.LEFT);
            noData.setFont(new Font("Inter", Font.BOLD, 15));
            addComponent(panel, noData, nutritionFactsGbc, 
            0, ++gridY, 2, 1, GridBagConstraints.CENTER);
        } 
        else { 
            int cuisineTypeNum = recipe.getCuisineType().size();
            if (cuisineTypeNum > 0) {
                JLabel cusineType = new JLabel("Cuisine Type", SwingConstants.LEFT);
                cusineType.setFont(new Font("Inter", Font.BOLD, 15));
                addComponent(panel, cusineType, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
                cusineType.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

                // For tracking column
                // 0 for the left column
                // 1 for the right column
                int columnNum = 0;

                for (String type : recipe.getCuisineType()) {
                    int position = SwingConstants.LEFT;
                    if (columnNum == 0) {
                        // Moves to the next row if starting from the left column (again)
                        gridY++;

                        // Positions label to the left
                        position = SwingConstants.LEFT;
                    } else {
                        // Positions label to the right
                        position = SwingConstants.RIGHT;
                    }

                    // Create the label
                    JLabel label = new JLabel(capitalizeEveryWord(type.replace('_', ' ')), position);
                    label.setFont(new Font("Inter", Font.PLAIN, 12));
                    addComponent(panel, label, nutritionFactsGbc, 
                    columnNum, gridY, 1, 1, GridBagConstraints.CENTER);

                    // Alternate the column
                    columnNum = (columnNum == 0) ? 1 : 0;
                }
            }

            int mealTypeNum = recipe.getMealType().size();
            if (mealTypeNum > 0) {
                JLabel mealType = new JLabel("Meal Type", SwingConstants.LEFT);
                mealType.setFont(new Font("Inter", Font.BOLD, 15));
                mealType.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
                addComponent(panel, mealType, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
                mealType.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

                // For tracking column
                // 0 for the left column
                // 1 for the right column
                int columnNum = 0;

                for (String type : recipe.getMealType()) {
                    int position = SwingConstants.LEFT;
                    if (columnNum == 0) {
                        // Moves to the next row if starting from the left column (again)
                        gridY++;

                        // Positions label to the left
                        position = SwingConstants.LEFT;
                    } else {
                        // Positions label to the right
                        position = SwingConstants.RIGHT;
                    }

                    // Create the label
                    JLabel label = new JLabel(capitalizeEveryWord(type.replace('_', ' ')), position);
                    label.setFont(new Font("Inter", Font.PLAIN, 12));
                    addComponent(panel, label, nutritionFactsGbc, 
                    columnNum, gridY, 1, 1, GridBagConstraints.CENTER);

                    // Alternate the column
                    columnNum = (columnNum == 0) ? 1 : 0;
                }
            }

            int dishTypeNum = recipe.getDishType().size();
            if (dishTypeNum > 0) {
                JLabel dishType = new JLabel("Dish Type", SwingConstants.LEFT);
                dishType.setFont(new Font("Inter", Font.BOLD, 15));
                dishType.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
                addComponent(panel, dishType, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
                dishType.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

                // For tracking column
                // 0 for the left column
                // 1 for the right column
                int columnNum = 0;

                for (String type : recipe.getDishType()) {
                    int position = SwingConstants.LEFT;
                    if (columnNum == 0) {
                        // Moves to the next row if starting from the left column (again)
                        gridY++;

                        // Positions label to the left
                        position = SwingConstants.LEFT;
                    } else {
                        // Positions label to the right
                        position = SwingConstants.RIGHT;
                    }

                    // Create the label
                    JLabel label = new JLabel(capitalizeEveryWord(type.replace('_', ' ')), position);
                    label.setFont(new Font("Inter", Font.PLAIN, 12));
                    addComponent(panel, label, nutritionFactsGbc, 
                    columnNum, gridY, 1, 1, GridBagConstraints.CENTER);

                    // Alternate the column
                    columnNum = (columnNum == 0) ? 1 : 0;
                }
            }
        }

        this.setViewportView(panel);
    }

    // Add component in a GridBagLayout
    private static void addComponent(Container container, Component component, GridBagConstraints gbc,
                                     int gridx, int gridy, int gridwidth, int gridheight, int anchor) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.anchor = anchor;
        container.add(component, gbc);
    }

    private static String capitalizeEveryWord(String str) {
        StringBuilder result = new StringBuilder();
        
        // Split the string into words
        // Split the string by spaces of slashes (/)
        String[] words = str.split("(?<!/)[\\s]+");

        
        // Capitalize the first letter of each word and append to result
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        
        // Remove trailing space and return the result
        return result.toString().trim();
    }
}
