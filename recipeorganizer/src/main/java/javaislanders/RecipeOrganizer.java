package javaislanders;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.NumberFormat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RecipeOrganizer extends JFrame implements ActionListener, ListSelectionListener {
    // Global Variables
    private APIConnectionInterface connection;
    private String dbFileName = "recipe-organizer-database.json";

    // 
    JPanel mainPanel;

    // Nutrition Analysis
    private JPanel nutritionAnalyzer;
    private JList<String> ingrList;
    DefaultListModel<String> ingrListModel;
    private JFormattedTextField quantityTextField;
    private JTextField unitTextField;
    private JTextField ingrTextField;
    private JButton removeIngrBtn;
    private JButton clearIngrListBtn;
    private JButton analyzeRecipeBtn;
    private Recipe currentRecipe;
    private JsonObject currentNutritionObject = null;

    public RecipeOrganizer() {
        // [API Connection]
        connection = new APIConnectionInterface();



        // [GUI Setup]
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JavaIslanders: Recipe Organizer");
        this.setResizable(true);
        this.setSize(1500, 800);
        this.setMinimumSize(new Dimension(1100, 900));
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


        // --------------------------------------------------------------
        // --                       Header Panel                       --
        // --------------------------------------------------------------

        JPanel header = new JPanel(new BorderLayout());
        JLabel headerTitle = new JLabel("Recipe Organizer", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Inter", Font.BOLD, 32));
        headerTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(headerTitle, BorderLayout.CENTER);
        this.add(header, BorderLayout.NORTH);

        // --------------------------------------------------------------
        // --                        MAIN PANEL                        --
        // --------------------------------------------------------------
        // Contains the 3 features:
        // - Nutrition Analysis
        // - Recipe Lists
        // - Recipe Categorization

        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.fill = GridBagConstraints.VERTICAL;
        mainGbc.insets = new Insets(15, 15, 15, 15); // Padding
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JScrollPane mainPanelPane = new JScrollPane(mainPanel);
        mainPanelPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(mainPanelPane, BorderLayout.CENTER);


        // ----------------------------------------------------
        // --            Nutrition Analysis Panel            --
        // ----------------------------------------------------
        // - Contains Nutrition Analyzer panel and Nutrition Facts panel

        nutritionAnalyzer = new JPanel(new FlowLayout(FlowLayout.LEFT, 75, 0));

        // -------------------------------------
        // --     Nutrition Analyzer panel    --
        // -------------------------------------

        JPanel analyzeRecipePanel = new JPanel(new GridBagLayout());

        GridBagConstraints analyzeRecipeGbc = new GridBagConstraints();
        analyzeRecipeGbc.fill = GridBagConstraints.HORIZONTAL;
        analyzeRecipeGbc.insets = new Insets(5, 5, 5, 5); // Padding

        JLabel analyzeRecipeHeader = new JLabel("Nutrition Analyzer", SwingConstants.LEFT);
        analyzeRecipeHeader.setFont(new Font("Inter", Font.BOLD, 20));
        addComponent(analyzeRecipePanel, analyzeRecipeHeader, analyzeRecipeGbc, 
        0, 0, 2, 1, GridBagConstraints.CENTER);

        JLabel addIngrLabel = new JLabel("Add Ingredient", SwingConstants.LEFT);
        addIngrLabel.setFont(new Font("Inter", Font.PLAIN, 15));
        addComponent(analyzeRecipePanel, addIngrLabel, analyzeRecipeGbc, 
        0, 1, 2, 1, GridBagConstraints.CENTER);

        // Quantity Input
        // Panel
        JPanel quantityPanel = new JPanel(new GridBagLayout());
        // Label
        JLabel quantityTextFieldLabel = new JLabel("Quantity");
        quantityTextFieldLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        addComponent(quantityPanel, quantityTextFieldLabel, analyzeRecipeGbc, 
        0, 0, 1, 1, GridBagConstraints.NORTHEAST);
        // Text Field
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        quantityTextField = new JFormattedTextField(numberFormat);
        quantityTextField.setValue(0);  // Initial value
        quantityTextField.setPreferredSize(new Dimension(150, 30));
        addComponent(quantityPanel, quantityTextField, analyzeRecipeGbc, 
        0, 1, 2, 1, GridBagConstraints.SOUTH);
        // Add to parent
        addComponent(analyzeRecipePanel, quantityPanel, analyzeRecipeGbc, 
        0, 2, 1, 1, GridBagConstraints.CENTER);
        
        // Unit Input
        // Panel
        JPanel unitPanel = new JPanel(new GridBagLayout());
        // Label
        JLabel unitLabel = new JLabel("Unit");
        unitLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        addComponent(unitPanel, unitLabel, analyzeRecipeGbc, 
        0, 0, 1, 1, GridBagConstraints.NORTHEAST);
        // Text Field
        unitTextField = new JTextField();
        unitTextField.setPreferredSize(new Dimension(150, 30));
        addComponent(unitPanel, unitTextField, analyzeRecipeGbc, 
        0, 1, 2, 1, GridBagConstraints.SOUTH);
        // Add to parent
        addComponent(analyzeRecipePanel, unitPanel, analyzeRecipeGbc, 
        1, 2, 1, 1, GridBagConstraints.CENTER);
        
        // Ingredient Input
        // Panel
        JPanel ingrPanel = new JPanel(new GridBagLayout());
        // Label
        JLabel ingrLabel = new JLabel("Ingredient");
        ingrLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        addComponent(ingrPanel, ingrLabel, analyzeRecipeGbc, 
        0, 0, 1, 1, GridBagConstraints.NORTHEAST);
        // Text Field
        ingrTextField = new JTextField();
        ingrTextField.setPreferredSize(new Dimension(325, 30));
        addComponent(ingrPanel, ingrTextField, analyzeRecipeGbc, 
        0, 1, 2, 1, GridBagConstraints.SOUTH);
        // Add to parent
        addComponent(analyzeRecipePanel, ingrPanel, analyzeRecipeGbc, 
        0, 3, 2, 1, GridBagConstraints.CENTER);

        // Add Ingredient Button
        JButton addIngrBtn = new JButton("Add");
        addIngrBtn.setActionCommand("Add Ingredient");
        addIngrBtn.addActionListener(this);
        addComponent(analyzeRecipePanel, addIngrBtn, analyzeRecipeGbc, 
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
        clearIngrListBtn = new JButton("Clear");
        clearIngrListBtn.setEnabled(false);
        clearIngrListBtn.addActionListener(this);
        clearIngrListBtn.setActionCommand("Clear Ingredients List");
        ingrListPanel.add(ingrListLabel);
        ingrListPanel.add(Box.createHorizontalGlue());
        ingrListPanel.add(clearIngrListBtn);
        ingrListPanel.add(Box.createHorizontalStrut(5));
        ingrListPanel.add(removeIngrBtn);
        addComponent(analyzeRecipePanel, ingrListPanel, analyzeRecipeGbc, 
        0, 5, 2, 1, GridBagConstraints.CENTER);

        // Ingredients List
        ingrListModel = new DefaultListModel<>();
        ingrList = new JList<>(ingrListModel);
        ingrList.addListSelectionListener(this);
        ingrList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane ingrListPane = new JScrollPane(ingrList);
        ingrListPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        ingrListPane.setPreferredSize(new Dimension(325, 250));
        addComponent(analyzeRecipePanel, ingrListPane, analyzeRecipeGbc, 
        0, 6, 2, 4, GridBagConstraints.NORTH);

        JSeparator nutritionAnalyzerSp1 = new JSeparator();
        addComponent(analyzeRecipePanel, nutritionAnalyzerSp1, analyzeRecipeGbc, 
        0, 10, 2, 1, GridBagConstraints.CENTER);

        // Analyze Recipe Button
        analyzeRecipeBtn = new JButton("Analyze Recipe");
        analyzeRecipeBtn.setActionCommand("Analyze Recipe");
        analyzeRecipeBtn.addActionListener(this);
        addComponent(analyzeRecipePanel, analyzeRecipeBtn, analyzeRecipeGbc, 
        0, 11, 2, 1, GridBagConstraints.CENTER);

        // Analyze Recipe Button
        JButton saveRecipeBtn = new JButton("Save Recipe");
        saveRecipeBtn.setActionCommand("Save Recipe");
        saveRecipeBtn.addActionListener(this);
        addComponent(analyzeRecipePanel, saveRecipeBtn, analyzeRecipeGbc, 
        0, 12, 2, 1, GridBagConstraints.CENTER);

        // -------------------------------------
        // --      Nutrition Facts panel      --
        // -------------------------------------

        NutritionFacts nutritionFactsPanel = new NutritionFacts(null);

        nutritionAnalyzer.add(analyzeRecipePanel);
        // Adds the initial NutritionFacts panel (has no data) (for placeholder purposes)
        nutritionAnalyzer.add(nutritionFactsPanel);

        addComponent(mainPanel, nutritionAnalyzer, mainGbc, 
        0, 0, 1, 1, GridBagConstraints.CENTER);
        
    }

    // Sends a request to the API with the ingredients list
    // Returns nutrition data
    public String analyzeIngredients(String payload) throws IOException, InterruptedException {
        return connection.sendRequest(payload);
    }

    // Handles clicks to a button
    public void actionPerformed(ActionEvent e) {
        // Determines which button is clicked
        String command = e.getActionCommand();

        if (command == "Add Ingredient") {
            // Get text field contents
            String quantity = quantityTextField.getText();
            String unit = unitTextField.getText();
            String ingredient = ingrTextField.getText();

            // If ingredient is empty, alert user
            if (ingredient == null || ingredient.trim().isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Ingredient cannot be empty.");
                return;
            } 
            else {
                // Reset text field contents
                quantityTextField.setText("0");
                unitTextField.setText("");
                ingrTextField.setText("");

                // Add ingredient to list
                String ingrEntry = quantity + " " + unit + " " + ingredient;
                ingrListModel.addElement(ingrEntry);
            }

            int ingrNum = ingrListModel.getSize();
            // Enables the 'Clear' button if there are items in the list
            if (ingrNum > 0) {
                clearIngrListBtn.setEnabled(true);
            } 
            // Otherwise, enables it
            else {
                clearIngrListBtn.setEnabled(false);
            }
        } 
        else if (command == "Remove Ingredient") {
            // Remove selected ingredient from the list
            int selectedIndex = ingrList.getSelectedIndex();
            if (selectedIndex != -1) {
                ingrListModel.remove(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Nothing is selected.");
            }

            int ingrNum = ingrListModel.getSize();
            // Disables the 'Clear' button if there are no items in the list
            if (ingrNum == 0) {
                clearIngrListBtn.setEnabled(false);
            } 
        } 
        else if (command == "Clear Ingredients List") {
            // Remove all items in the ingredients list
            ingrListModel.clear();
        } 
        else if (command == "Analyze Recipe") {
            // Alerts the user if the ingredients list is empty
            int ingrNum = ingrListModel.getSize();
            if (ingrNum == 0) {
                JOptionPane.showMessageDialog(mainPanel, "Ingredients list is empty.");
                return;
            }

            // Creates the json payload for the request
            StringBuilder payloadBuilder = new StringBuilder();
            payloadBuilder.append("{\"ingr\": [");
            for (int i = 0; i < ingrNum; i++) {
                String ingr = ingrListModel.getElementAt(i);
                payloadBuilder.append('"' + ingr + '"');
                if (i != (ingrNum - 1)) {
                    payloadBuilder.append(',');
                }
            }
            payloadBuilder.append("]}");
            String payload = payloadBuilder.toString();;

            try {
                // Disables the button to prevent potential problems
                analyzeRecipeBtn.setEnabled(false);
                // Alerts the user that the program is currently sending
                // // i.e. program is unusable at this point
                // Thread dialogThread = new Thread(new Runnable() {
                //     @Override
                //     public void run() {
                //         // Display the dialog
                //         JOptionPane.showMessageDialog(null, "Analyzing recipe...", "Alert", JOptionPane.PLAIN_MESSAGE);
                //     }
                // });
                // dialogThread.start();
                // Sends the request to the API
                String response = analyzeIngredients(payload);

                // Parses the response to a JsonObject
                // JsonElement nutritionElement = JsonParser.parseString(response);
                // JsonObject nutritionJson = nutritionElement.getAsJsonObject();
                // currentNutritionObject = nutritionJson;

                Gson gson = new Gson();
                Recipe recipe = gson.fromJson(response, Recipe.class);
                currentRecipe = recipe;

                // Closes the 'Analyzing recipe...' dialog
                // closeAlertDialog();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(mainPanel, "Error sending request to the API.\n" + exception.getLocalizedMessage());
                currentNutritionObject = null;
            } finally {
                // Enables back the button
                analyzeRecipeBtn.setEnabled(true);

                // Checks if the response has nutrition data or not.
                // If it doesn't, it may be that the recipe inputted by the user
                // is not valid.
                if (currentRecipe.getCalories() == 0) {
                    JOptionPane.showMessageDialog(mainPanel, "Invalid recipe.");
                }

                // Updates the NutritionFacts panel.
                // Removes the previous NutritionFacts panel
                // and then adds the updated one.
                Component[] components = nutritionAnalyzer.getComponents();
                if (components.length > 0) {
                    Component lastComponent = components[components.length - 1];
                    nutritionAnalyzer.remove(lastComponent);
                    nutritionAnalyzer.revalidate();
                    nutritionAnalyzer.repaint();
                }
                NutritionFacts nutritionFactsPanel = new NutritionFacts(currentRecipe);
                nutritionAnalyzer.add(nutritionFactsPanel);
                nutritionAnalyzer.revalidate();
                nutritionAnalyzer.repaint();
            }
        } 
        else if (command == "Save Recipe") {
            // Alerts the user if the ingredients list is empty
            int ingrNum = ingrListModel.getSize();
            if (ingrNum == 0) {
                JOptionPane.showMessageDialog(mainPanel, "Ingredients list is empty.");
                return;
            }
        }
    }

    // Handle changes in the selection of items in a JList
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            // Determines which JList triggered the event
            JList<?> sourceList = (JList<?>) e.getSource();

            // Ingredient List (Analyze Recipe)
            if (sourceList == ingrList) {
                // Access the selected indices or values from the JList
                // String selectedValue = ingrList.getSelectedValue().toString();

                int selectedIndex = ingrList.getSelectedIndex();
                // Enables the 'Remove' button if there are items in the list
                if (selectedIndex != -1) {
                    removeIngrBtn.setEnabled(true);
                } 
                // Otherwise, disables it
                else {
                    removeIngrBtn.setEnabled(false);
                }
            }
        }
    }

    // Utility function for adding component in a GridBagLayout
    private static void addComponent(Container container, Component component, GridBagConstraints gbc,
                                     int gridx, int gridy, int gridwidth, int gridheight, int anchor) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.anchor = anchor;
        container.add(component, gbc);
    }


    private static void closeAlertDialog() {
        // Close the dialog programmatically
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof JDialog) {
                JDialog dialog = (JDialog) window;
                if (dialog.getTitle().equals("Alert")) {
                    dialog.dispose(); // Close the dialog
                    break;
                }
            }
        }
    }
}
