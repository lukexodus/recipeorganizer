package javaislanders;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.text.NumberFormat;

// import java.util.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RecipeOrganizer extends JFrame implements ActionListener, ListSelectionListener {
    // App Global Variables
    private APIConnectionInterface connection;
    private String dbFileName = "recipe-organizer-database.json";

    // 
    JPanel mainPanel;

    // Nutrition Analysis Global Variables
    private JList<String> ingrList;
    DefaultListModel<String> ingrListModel;
    private JFormattedTextField quantityTextField;
    private JTextField unitTextField;
    private JTextField ingrTextField;
    private JButton removeIngrBtn;

    public RecipeOrganizer() {
        // [API Connection]
        // connection = new APIConnectionInterface();



        // [GUI Setup]
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JavaIslanders: Recipe Organizer");
        this.setResizable(true);
        this.setSize(1500, 800);
        this.setVisible(true);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);



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
        headerTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(headerTitle, BorderLayout.CENTER);
        this.add(header, BorderLayout.NORTH);



        // [Main Panel]
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JScrollPane mainPanelPane = new JScrollPane(mainPanel);
        mainPanelPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);


        // [Nutrition Analysis Panel]
        // Add ingredient panel
        JPanel analyzeRecipePanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        JLabel analyzeRecipeHeader = new JLabel("Analyze Recipe", SwingConstants.LEFT);
        analyzeRecipeHeader.setFont(new Font("Inter", Font.BOLD, 20));
        addComponent(analyzeRecipePanel, analyzeRecipeHeader, gbc, 
        0, 0, 2, 1, GridBagConstraints.EAST);

        JLabel addIngrLabel = new JLabel("Add Ingredient", SwingConstants.LEFT);
        addIngrLabel.setFont(new Font("Inter", Font.PLAIN, 15));
        addComponent(analyzeRecipePanel, addIngrLabel, gbc, 
        0, 1, 2, 1, GridBagConstraints.EAST);

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
        quantityTextField = new JFormattedTextField(numberFormat);
        quantityTextField.setValue(0);  // Initial value
        quantityTextField.setPreferredSize(new Dimension(150, 30));
        addComponent(quantityPanel, quantityTextField, gbc, 
        0, 1, 2, 1, GridBagConstraints.SOUTH);
        // Add to parent
        addComponent(analyzeRecipePanel, quantityPanel, gbc, 
        0, 2, 1, 1, GridBagConstraints.CENTER);
        
        // Unit Input
        // Panel
        JPanel unitPanel = new JPanel(new GridBagLayout());
        // Label
        JLabel unitLabel = new JLabel("Unit");
        unitLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        addComponent(unitPanel, unitLabel, gbc, 
        0, 0, 1, 1, GridBagConstraints.NORTHEAST);
        // Text Field
        unitTextField = new JTextField();
        unitTextField.setPreferredSize(new Dimension(150, 30));
        addComponent(unitPanel, unitTextField, gbc, 
        0, 1, 2, 1, GridBagConstraints.SOUTH);
        // Add to parent
        addComponent(analyzeRecipePanel, unitPanel, gbc, 
        1, 2, 1, 1, GridBagConstraints.CENTER);
        
        // Ingredient Input
        // Panel
        JPanel ingrPanel = new JPanel(new GridBagLayout());
        // Label
        JLabel ingrLabel = new JLabel("Ingredient");
        ingrLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        addComponent(ingrPanel, ingrLabel, gbc, 
        0, 0, 1, 1, GridBagConstraints.NORTHEAST);
        // Text Field
        ingrTextField = new JTextField();
        ingrTextField.setPreferredSize(new Dimension(325, 30));
        addComponent(ingrPanel, ingrTextField, gbc, 
        0, 1, 2, 1, GridBagConstraints.SOUTH);
        // Add to parent
        addComponent(analyzeRecipePanel, ingrPanel, gbc, 
        0, 3, 2, 1, GridBagConstraints.CENTER);

        // Add Ingredient Button
        JButton addIngrBtn = new JButton("Add");
        addIngrBtn.setActionCommand("Add Ingredient");
        addIngrBtn.addActionListener(this);
        addComponent(analyzeRecipePanel, addIngrBtn, gbc, 
        0, 4, 2, 1, GridBagConstraints.CENTER);

        // Ingredients List Label
        JPanel ingrListPanel = new JPanel();
        ingrListPanel.setLayout(new BoxLayout(ingrListPanel, BoxLayout.X_AXIS));
        ingrListPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        JLabel ingrListLabel = new JLabel("Ingredients List", SwingConstants.LEFT);
        ingrListLabel.setFont(new Font("Inter", Font.PLAIN, 15));
        removeIngrBtn = new JButton("Remove");
        removeIngrBtn.setEnabled(false);
        removeIngrBtn.addActionListener(this);
        removeIngrBtn.setActionCommand("Remove Ingredient");
        ingrListPanel.add(ingrListLabel);
        ingrListPanel.add(Box.createHorizontalGlue());
        ingrListPanel.add(removeIngrBtn);
        addComponent(analyzeRecipePanel, ingrListPanel, gbc, 
        0, 5, 2, 1, GridBagConstraints.EAST);

        // Ingredients List
        ingrListModel = new DefaultListModel<>();
        ingrList = new JList<>(ingrListModel);
        ingrList.addListSelectionListener(this);
        ingrList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane ingrListPane = new JScrollPane(ingrList);
        ingrListPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        ingrListPane.setPreferredSize(new Dimension(325, 400));
        addComponent(analyzeRecipePanel, ingrListPane, gbc, 
        0, 6, 2, 4, GridBagConstraints.NORTH);

        JSeparator spEnd = new JSeparator();
        addComponent(analyzeRecipePanel, spEnd, gbc, 
        0, 10, 2, 1, GridBagConstraints.CENTER);

        // Analyze Recipe Button
        JButton analyzeRecipeBtn = new JButton("Analyze Recipe");
        analyzeRecipeBtn.setActionCommand("Analyze Recipe");
        analyzeRecipeBtn.addActionListener(this);
        addComponent(analyzeRecipePanel, analyzeRecipeBtn, gbc, 
        0, 11, 2, 1, GridBagConstraints.CENTER);

        mainPanel.add(analyzeRecipePanel, BorderLayout.CENTER);



        this.add(mainPanelPane, BorderLayout.CENTER);
    }

    // Sends a request to the API with the ingredients list
    // Returns nutrition data
    public String analyzeIngredients(String payload) throws IOException, InterruptedException {
        return connection.sendRequest(payload);
    }

    // Handles clicks to a button
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command == "Add Ingredient") {
            String quantity = quantityTextField.getText();
            String unit = unitTextField.getText();
            String ingredient = ingrTextField.getText();

            if (unit == null || unit.trim().isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Unit cannot be empty.");
            } else if (ingredient == null || ingredient.trim().isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Ingredient cannot be empty.");
            } else {
                quantityTextField.setText("0");
                unitTextField.setText("");
                ingrTextField.setText("");
                String ingrEntry = quantity + " " + unit + " " + ingredient;
                ingrListModel.addElement(ingrEntry);
            }
        } else if (command == "Remove Ingredient") {
            int selectedIndex = ingrList.getSelectedIndex();
            if (selectedIndex != -1) {
                ingrListModel.remove(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Nothing is selected.");
            }
        } else if (command == "Analyze Ingredient") {
            
        }
    }

    // Handle changes in the selection of items in a JList
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            // Check which JList triggered the event
            JList<?> sourceList = (JList<?>) e.getSource();

            // Ingredient List (Analyze Recipe)
            if (sourceList == ingrList) {
                // Access the selected indices or values from the JList
                // String selectedValue = ingrList.getSelectedValue().toString();

                int selectedIndex = ingrList.getSelectedIndex();
                if (selectedIndex != -1) {
                    removeIngrBtn.setEnabled(true);
                } else {
                    removeIngrBtn.setEnabled(false);
                }
            }
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

    // JsonElement jsonElement = JsonParser.parseString(payload);
    // JsonObject jsonObject = jsonElement.getAsJsonObject();
    // System.out.println(jsonObject.get("ingr"));
}
