package javaislanders;

import java.awt.*;
import javax.swing.*;

import com.google.gson.JsonObject;

public class NutritionFacts extends JPanel {
    public NutritionFacts(JsonObject nutritionDataJson) {
        this.setLayout(new GridBagLayout());

        GridBagConstraints nutritionFactsGbc = new GridBagConstraints();
        nutritionFactsGbc.fill = GridBagConstraints.HORIZONTAL;
        nutritionFactsGbc.insets = new Insets(5, 5, 5, 5);

        JLabel nutritionFactsHeader = new JLabel("Nutrition Facts", SwingConstants.LEFT);
        nutritionFactsHeader.setFont(new Font("Inter", Font.BOLD, 20));
        nutritionFactsHeader.setPreferredSize(new Dimension(325, 40));
        addComponent(this, nutritionFactsHeader, nutritionFactsGbc, 
        0, 0, 2, 1, GridBagConstraints.CENTER);

        JSeparator nutrFactsSp1 = new JSeparator();
        addComponent(this, nutrFactsSp1, nutritionFactsGbc, 
        0, 1, 2, 1, GridBagConstraints.CENTER);

        if (nutritionDataJson == null || (nutritionDataJson.get("calories") == null)) {
            JLabel noData = new JLabel("No Data", SwingConstants.LEFT);
            noData.setFont(new Font("Inter", Font.BOLD, 15));
            addComponent(this, noData, nutritionFactsGbc, 
            0, 2, 2, 1, GridBagConstraints.CENTER);
        } else {
            String caloriesNum = nutritionDataJson.get("calories").toString();
            
            JLabel calories = new JLabel("Calories", SwingConstants.LEFT);
            calories.setFont(new Font("Inter", Font.BOLD, 25));
            addComponent(this, calories, nutritionFactsGbc, 
            0, 3, 1, 1, GridBagConstraints.CENTER);

            JLabel caloriesAmount = new JLabel(caloriesNum, SwingConstants.RIGHT);
            caloriesAmount.setFont(new Font("Inter", Font.BOLD, 25));
            addComponent(this, caloriesAmount, nutritionFactsGbc, 
            1, 3, 1, 1, GridBagConstraints.CENTER);

            JLabel dailyValue = new JLabel("% Daily Value*", SwingConstants.RIGHT);
            dailyValue.setFont(new Font("Inter", Font.BOLD, 12));
            addComponent(this, dailyValue, nutritionFactsGbc, 
            1, 4, 1, 1, GridBagConstraints.CENTER);            
        }
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
}
