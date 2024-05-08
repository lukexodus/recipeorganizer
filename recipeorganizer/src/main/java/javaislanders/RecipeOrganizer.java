package javaislanders;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.NumberFormat;

// import java.util.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RecipeOrganizer extends JFrame implements ActionListener {
    private APIConnectionInterface connection;
    private String dbFileName = "recipe-organizer-database.json";

    public RecipeOrganizer() {
        // [API Connection]
        // connection = new APIConnectionInterface();


        // [GUI Setup]
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Recipe Organizer");
        this.setResizable(true);
        this.setSize(1500, 800);
        this.setVisible(true);
        this.setLayout(new BorderLayout());


        // [Create DB file if it does not exist]
        File dbFile = new File(dbFileName);
        try {
            // Check if the file exists
            if (!dbFile.exists()) {
                // Create a new file
                boolean created = dbFile.createNewFile();
                if (created) {
                    System.out.println("DB File created successfully.");
                } else {
                    System.out.println("DB File creation failed.");
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }


        // [Load DB Data]


        // [Header Panel]
        JPanel header = new JPanel(new BorderLayout());
        JLabel headerTitle = new JLabel("Recipe Organizer", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Inter", Font.BOLD, 32));
        header.add(headerTitle, BorderLayout.CENTER);
        this.add(header, BorderLayout.NORTH);


        // [Nutrition Analysis Panel]
        JPanel addIngrPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        JLabel nutritionAnalysisLabel = new JLabel("Add Ingredient");
        nutritionAnalysisLabel.setFont(new Font("Inter", Font.BOLD, 20));
        addComponent(addIngrPanel, nutritionAnalysisLabel, gbc, 
        0, 0, 2, 1, GridBagConstraints.CENTER);

        // Quantity Input
        // Panel
        JPanel quantityPanel = new JPanel(new GridBagLayout());
        // Label
        JLabel quantityTextFieldLabel = new JLabel("Quantity");
        quantityTextFieldLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        addComponent(quantityPanel, quantityTextFieldLabel, gbc, 
        0, 0, 1, 1, GridBagConstraints.NORTHEAST);
        // Text Field
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        JFormattedTextField quantityTextField = new JFormattedTextField(numberFormat);
        quantityTextField.setValue(0);  // Initial value
        quantityTextField.setPreferredSize(new Dimension(150, 30));
        addComponent(quantityPanel, quantityTextField, gbc, 
        0, 1, 2, 1, GridBagConstraints.SOUTH);
        // Add to parent
        addComponent(addIngrPanel, quantityPanel, gbc, 
        0, 1, 1, 1, GridBagConstraints.CENTER);
        
        // Unit Input
        // Panel
        JPanel unitPanel = new JPanel(new GridBagLayout());
        // Label
        JLabel unitLabel = new JLabel("Unit");
        unitLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        addComponent(unitPanel, unitLabel, gbc, 
        0, 0, 1, 1, GridBagConstraints.NORTHEAST);
        // Text Field
        JTextField unitTextField = new JTextField();
        unitTextField.setPreferredSize(new Dimension(150, 30));
        addComponent(unitPanel, unitTextField, gbc, 
        0, 1, 2, 1, GridBagConstraints.SOUTH);
        // Add to parent
        addComponent(addIngrPanel, unitPanel, gbc, 
        1, 1, 1, 1, GridBagConstraints.CENTER);
        
        // Ingredient Input
        // Panel
        JPanel ingrPanel = new JPanel(new GridBagLayout());
        // Label
        JLabel ingrLabel = new JLabel("Ingredient");
        ingrLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        addComponent(ingrPanel, ingrLabel, gbc, 
        0, 0, 1, 1, GridBagConstraints.NORTHEAST);
        // Text Field
        JTextField ingrTextField = new JTextField();
        ingrTextField.setPreferredSize(new Dimension(325, 30));
        addComponent(ingrPanel, ingrTextField, gbc, 
        0, 1, 2, 1, GridBagConstraints.SOUTH);
        // Add to parent
        addComponent(addIngrPanel, ingrPanel, 
        gbc, 0, 2, 2, 1, GridBagConstraints.CENTER);

        this.add(addIngrPanel, BorderLayout.CENTER);
    }

    public String analyzeIngredients(String payload) throws IOException, InterruptedException {
        return connection.sendRequest(payload);
    }

    public void actionPerformed(ActionEvent e) {

    }

    private static void addComponent(Container container, Component component, GridBagConstraints gbc,
                                     int gridx, int gridy, int gridwidth, int gridheight, int anchor) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.anchor = anchor;
        container.add(component, gbc);
    }

    // JsonElement jsonElement = JsonParser.parseString(payload);
    // JsonObject jsonObject = jsonElement.getAsJsonObject();
    // System.out.println(jsonObject.get("ingr"));
}
