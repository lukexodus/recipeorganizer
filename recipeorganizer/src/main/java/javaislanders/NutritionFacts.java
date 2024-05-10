package javaislanders;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.google.gson.JsonObject;

public class NutritionFacts extends JScrollPane {
    public NutritionFacts(Recipe recipe) {
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setBorder(new RoundedBorder(20));

        if (recipe != null) {
            System.out.println(recipe.getCalories());
        }

        JPanel nutritionFactsPanel = new JPanel(new GridBagLayout());
        // nutritionFactsPanel.setPreferredSize(new Dimension(325, 600));

        GridBagConstraints nutritionFactsGbc = new GridBagConstraints();
        nutritionFactsGbc.fill = GridBagConstraints.HORIZONTAL;
        nutritionFactsGbc.insets = new Insets(5, 5, 5, 5);

        JLabel nutritionFactsHeader = new JLabel("Nutrition Facts", SwingConstants.LEFT);
        nutritionFactsHeader.setFont(new Font("Inter", Font.BOLD, 20));
        addComponent(nutritionFactsPanel, nutritionFactsHeader, nutritionFactsGbc, 
        0, 0, 2, 1, GridBagConstraints.CENTER);

        JSeparator sp1 = new JSeparator();
        sp1.setPreferredSize(new Dimension(300, 10));
        addComponent(nutritionFactsPanel, sp1, nutritionFactsGbc, 
        0, 1, 2, 1, GridBagConstraints.CENTER);

        // if (recipe == null || (recipe.get("calories") == null)) {
        //     JLabel noData = new JLabel("No Data", SwingConstants.LEFT);
        //     noData.setFont(new Font("Inter", Font.BOLD, 15));
        //     addComponent(nutritionFactsPanel, noData, nutritionFactsGbc, 
        //     0, 2, 2, 1, GridBagConstraints.CENTER);
        // } else {
        //     String caloriesNum = recipe.get("calories").getAsString();

            JLabel amountPerServing = new JLabel("Amount Per Serving", SwingConstants.LEFT);
            amountPerServing.setFont(new Font("Inter", Font.BOLD, 15));
            addComponent(nutritionFactsPanel, amountPerServing, nutritionFactsGbc, 
            0, 2, 1, 1, GridBagConstraints.CENTER);
            
            JLabel calories = new JLabel("Calories", SwingConstants.LEFT);
            calories.setFont(new Font("Inter", Font.BOLD, 25));
            addComponent(nutritionFactsPanel, calories, nutritionFactsGbc, 
            0, 3, 1, 1, GridBagConstraints.CENTER);

            JLabel caloriesAmount = new JLabel("1774", SwingConstants.RIGHT);
            caloriesAmount.setFont(new Font("Inter", Font.BOLD, 25));
            addComponent(nutritionFactsPanel, caloriesAmount, nutritionFactsGbc, 
            1, 3, 1, 1, GridBagConstraints.CENTER);

            JSeparator sp2 = new JSeparator();
            addComponent(nutritionFactsPanel, sp2, nutritionFactsGbc, 
            0, 4, 2, 1, GridBagConstraints.CENTER);
 
            JLabel dailyValue = new JLabel("% Daily Value*", SwingConstants.RIGHT);
            dailyValue.setFont(new Font("Inter", Font.BOLD, 12));
            addComponent(nutritionFactsPanel, dailyValue, nutritionFactsGbc, 
            1, 5, 1, 1, GridBagConstraints.CENTER);  

            JSeparator sp3 = new JSeparator();
            addComponent(nutritionFactsPanel, sp3, nutritionFactsGbc, 
            0, 6, 2, 1, GridBagConstraints.CENTER);

            JPanel nutr1Panel = new JPanel();
            nutr1Panel.setLayout(new BoxLayout(nutr1Panel, BoxLayout.X_AXIS));
            JLabel nutr1Amount = new JLabel("Total Fat 18.3 g");
            nutr1Amount.setFont(new Font("Inter", Font.PLAIN, 15));
            JLabel nutr1Daily = new JLabel("28 %");
            nutr1Daily.setFont(new Font("Inter", Font.PLAIN, 15));
            nutr1Panel.add(nutr1Amount);
            nutr1Panel.add(Box.createHorizontalGlue());
            nutr1Panel.add(nutr1Daily);
            addComponent(nutritionFactsPanel, nutr1Panel, nutritionFactsGbc, 
            0, 7, 2, 1, GridBagConstraints.CENTER); 

        // }

        this.setViewportView(nutritionFactsPanel);
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
