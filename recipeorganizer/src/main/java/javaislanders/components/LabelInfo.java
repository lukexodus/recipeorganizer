package javaislanders.components;

import java.awt.*;
import javax.swing.*;

import javaislanders.types.Recipe;

public class LabelInfo extends JScrollPane {
    public LabelInfo (Recipe recipe) {
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setBorder(new RoundedBorder(20));

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints nutritionFactsGbc = new GridBagConstraints();
        nutritionFactsGbc.fill = GridBagConstraints.HORIZONTAL;
        nutritionFactsGbc.insets = new Insets(5, 5, 5, 5);

        int gridY = 0;

        JLabel nutritionFactsHeader = new JLabel("Label Information", SwingConstants.LEFT);
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
            int dietLabelsNum = recipe.getDietLabels().size();
            if (dietLabelsNum > 0) {
                JLabel dietLabels = new JLabel("Diet Labels", SwingConstants.LEFT);
                dietLabels.setFont(new Font("Inter", Font.BOLD, 15));
                addComponent(panel, dietLabels, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
                dietLabels.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

                // For tracking column
                // 0 for the left column
                // 1 for the right column
                int columnNum = 0;

                for (String labelStr : recipe.getDietLabels()) {
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
                    JLabel label = new JLabel(labelStr.replace('_', ' '), position);
                    label.setFont(new Font("Inter", Font.PLAIN, 12));
                    addComponent(panel, label, nutritionFactsGbc, 
                    columnNum, gridY, 1, 1, GridBagConstraints.CENTER);

                    // Alternate the column
                    columnNum = (columnNum == 0) ? 1 : 0;
                }
            }


            int healthLabelsNum = recipe.getHealthLabels().size();
            if (healthLabelsNum > 0) {
                JLabel healthLabels = new JLabel("Health Labels", SwingConstants.LEFT);
                healthLabels.setFont(new Font("Inter", Font.BOLD, 15));
                addComponent(panel, healthLabels, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
                healthLabels.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

                // For tracking column
                // 0 for the left column
                // 1 for the right column
                int columnNum = 0;

                for (String labelStr : recipe.getHealthLabels()) {
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
                    JLabel label = new JLabel(labelStr.replace('_', ' '), position);
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
}