package javaislanders;

// Built-in Classes
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.NumberFormat;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

// 3rd-Party (Downloaded) Classes
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// Local Classes
import javaislanders.components.NutritionFacts;
import javaislanders.components.LabelInfo;
import javaislanders.components.TypeInfo;
import javaislanders.types.Ingredient;
import javaislanders.types.Recipe;

public class RecipeOrganizer extends JFrame implements ActionListener, ListSelectionListener {
    // Global Variables
    private APIConnectionInterface connection; // Connection to the edamam.com API
    final private String dbFileName = "recipe-organizer-database.json"; // Filename of the JSON file

    // Layout Variables
    int hgap = 60;

    // Global Data
    private ArrayList<String> recipeGroups = new ArrayList<>();
    private HashSet<String> recipeTitles = new HashSet<>();
    private ArrayList<Recipe> recipeItems = null;  // This is one stored in the JSON file

    // Main Panel
    JPanel mainPanel;

    // Nutrition Analysis
    private JPanel firstPanel;
    private JList<String> ingrList;
    DefaultListModel<String> ingrListModel;
    private JFormattedTextField quantityTextField;
    private JTextField unitTextField;
    private JTextField ingrTextField;
    private JButton removeIngrButton;
    private JButton clearIngrListButton;
    private JButton analyzeRecipeButton;
    private JButton saveRecipeButton;
    private Recipe currentRecipe;
    private boolean analyzedCurrentRecipe = false;

    // Recipe Groups
    private JList<String> recipeGroupsList;  // Left Panel
    DefaultListModel<String> recipeGroupsModel;
    private JList<String> recipesList;
    DefaultListModel<String> recipesModel;
    private JButton addRecipeGroupButton;
    private JButton removeRecipeGroupButton;
    private JButton renameRecipeGroupButton;
    private JButton moveRecipeButton;
    private JButton removeRecipeButton;
    private JButton renameRecipeButton;
    private JList<String> ingredientsList;
    DefaultListModel<String> ingredientsModel;  // Right Panel

    // Recipe Categorization
    private JButton unselectAllButton;
    private JList<String> dietLabelsList;
    private JList<String> healthLabelsList;
    private JList<String> cuisineTypesList;
    private JList<String> mealTypesList;
    private JList<String> dishTypesList;

    List<String> dietLabels = new ArrayList<>();
    List<String> healthLabels = new ArrayList<>();
    List<String> cuisineTypes = new ArrayList<>();
    List<String> mealTypes = new ArrayList<>();
    List<String> dishTypes = new ArrayList<>();


    public RecipeOrganizer() {
        // [API Connection]
        connection = new APIConnectionInterface();



        // [GUI Setup]
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JavaIslanders: Recipe Organizer");
        this.setResizable(true);
        this.setSize(1900, 1000);
        // this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setMinimumSize(new Dimension(1100, 900));
        this.setVisible(true);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);



        // [Create DB file (JSON) if it does not exist]
        File dbFile = new File(dbFileName);
        try {
            // Check if the JSON file exists
            if (!dbFile.exists()) {
                // Create the JSON file
                boolean created = dbFile.createNewFile();

                if (created) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile))) {
                        // Add empty list to the file
                        writer.write("[]");
                    } catch (IOException e) {
                        System.out.println("An error occurred while writing to the file: " + e.getMessage());
                        e.printStackTrace();
                    }

                    System.out.println("DB File created successfully.");
                } else {
                    System.out.println("DB File creation failed.");
                }
            }
        } catch (IOException e) {
            // If an error occurs
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }



        // [Load DB Data]
        try {
            Gson gson = new Gson();

            // Load the JSON file, reads the content as a String
            String json = new String(Files.readAllBytes(Paths.get(dbFileName)));

            // Converts each recipe objects in the String to a Recipe object,
            // and then puts them in an array.
            Recipe[] recipeItemsArray = gson.fromJson(json, Recipe[].class);

            // Converts the array (fixed-size) to a list (dynamic-size)
            List<Recipe> tempList = Arrays.asList(recipeItemsArray);
            recipeItems = new ArrayList<>(tempList);

            // Enhanced for loop
            for (Recipe recipe : recipeItems) {
                // Add the recipe title to the recipeTitles list
                recipeTitles.add(recipe.getTitle());

                // The null value means the recipe is not in any group
                if (recipe.getGroup() != null) {
                    // Only add the group name to recipeGroups list if it
                    // is not added already
                    if (!recipeGroups.contains(recipe.getGroup())) {
                        // Use Recipe getter
                        recipeGroups.add(recipe.getGroup());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // JFrame contains two components:
        // 1. Header Panel (NORTH)
        // 2. Main Panel (CENTER)

        // --------------------------------------------------------------
        // --                       Header Panel                       --
        // --------------------------------------------------------------
 
        JPanel header = new JPanel(new BorderLayout());
        // SwingConstants specifies the location of the text. Choices:
        // SwingConstants.CENTER
        // SwingConstants.LEFT
        // SwingConstants.RIGHT
        JLabel headerTitle = new JLabel("Recipe Organizer", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Inter", Font.BOLD, 32));
        // Margins
        headerTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(headerTitle, BorderLayout.CENTER);
        // Add to JFrame
        this.add(header, BorderLayout.NORTH);

        // --------------------------------------------------------------
        // --                       MAIN PANEL                         -- 
        // --------------------------------------------------------------
        // Contains the 3 features:
        // 
        // First Panel
        //  - Nutrition Analysis
        // 
        // Second Panel
        //  - Recipe Lists
        //  - Recipe Categorization

        // GridBagLayout is like GridLayout but more flexible
        // and more customizable (by using GridBagConstraints)
        mainPanel = new JPanel(new GridBagLayout());
        // GridBagConstraints specify the placement rules of items
        // in the grid layout.
        GridBagConstraints mainGbc = new GridBagConstraints();
        // Direction (Vertical or horizontal)
        mainGbc.fill = GridBagConstraints.VERTICAL;
        // Padding
        mainGbc.insets = new Insets(25, 15, 25, 15);
        // Margins
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // Add scroll bar
        JScrollPane mainPanelPane = new JScrollPane(mainPanel);
        // Scroll Bar Policy Choices:
        // 1. Always
        // 2. Never
        // 3. As needed
        mainPanelPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        // Add to JFrame
        this.add(mainPanelPane, BorderLayout.CENTER);


        // ----------------------------------------------------
        // --                    1st Panel                   --
        // ----------------------------------------------------
        // - Contains Nutrition Analyzer panel, Nutrition Facts panel (NutritionFacts.java),
        //   Labels Info Panel (LabelInfo.java), And Types Info Panel (TypeInfo.java)

        firstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, 0));

        // -------------------------------------
        // --     Nutrition Analyzer Panel    --
        // -------------------------------------

        JPanel analyzeRecipePanel = new JPanel(new GridBagLayout());

        GridBagConstraints analyzeRecipeGbc = new GridBagConstraints();
        analyzeRecipeGbc.fill = GridBagConstraints.HORIZONTAL;
        analyzeRecipeGbc.insets = new Insets(5, 5, 5, 5); // Padding


        JLabel analyzeRecipeHeader = new JLabel("Nutrition Analyzer", SwingConstants.LEFT);
        analyzeRecipeHeader.setFont(new Font("Inter", Font.BOLD, 20));
        // Utility method for adding component in a GridBagLayout.
        // Defined below.
        addComponent(analyzeRecipePanel, analyzeRecipeHeader, analyzeRecipeGbc, 
        0, 0, 2, 1, GridBagConstraints.CENTER);


        JLabel addIngrLabel = new JLabel("Add Ingredient", SwingConstants.LEFT);
        addIngrLabel.setFont(new Font("Inter", Font.PLAIN, 15));
        addComponent(analyzeRecipePanel, addIngrLabel, analyzeRecipeGbc, 
        0, 1, 2, 1, GridBagConstraints.CENTER);


        // -- Quantity Input --

        // Panel
        JPanel quantityPanel = new JPanel(new GridBagLayout());

        // Label
        JLabel quantityTextFieldLabel = new JLabel("Quantity");
        quantityTextFieldLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        addComponent(quantityPanel, quantityTextFieldLabel, analyzeRecipeGbc, 
        0, 0, 1, 1, GridBagConstraints.NORTHEAST);

        // Text Field
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        quantityTextField = new JFormattedTextField(numberFormat);
        quantityTextField.setValue(0);  // Initial value
        quantityTextField.setPreferredSize(new Dimension(150, 30));
        addComponent(quantityPanel, quantityTextField, analyzeRecipeGbc, 
        0, 1, 2, 1, GridBagConstraints.SOUTH);

        // Add to parent
        addComponent(analyzeRecipePanel, quantityPanel, analyzeRecipeGbc, 
        0, 2, 1, 1, GridBagConstraints.CENTER);
        

        // -- Unit Input --

        // Panel
        JPanel unitPanel = new JPanel(new GridBagLayout());

        // Label
        JLabel unitLabel = new JLabel("Unit");
        unitLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        addComponent(unitPanel, unitLabel, analyzeRecipeGbc, 
        0, 0, 1, 1, GridBagConstraints.NORTHEAST);

        // Text Field
        unitTextField = new JTextField();
        unitTextField.setPreferredSize(new Dimension(180, 30));
        addComponent(unitPanel, unitTextField, analyzeRecipeGbc, 
        0, 1, 2, 1, GridBagConstraints.SOUTH);

        // Add to parent
        addComponent(analyzeRecipePanel, unitPanel, analyzeRecipeGbc, 
        1, 2, 1, 1, GridBagConstraints.CENTER);
        

        // -- Ingredient Input --

        // Panel
        JPanel ingrPanel = new JPanel(new GridBagLayout());

        // Label
        JLabel ingrLabel = new JLabel("Ingredient");
        ingrLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        addComponent(ingrPanel, ingrLabel, analyzeRecipeGbc, 
        0, 0, 1, 1, GridBagConstraints.NORTHEAST);

        // Text Field
        ingrTextField = new JTextField();
        ingrTextField.setPreferredSize(new Dimension(350, 30));
        addComponent(ingrPanel, ingrTextField, analyzeRecipeGbc, 
        0, 1, 2, 1, GridBagConstraints.SOUTH);

        // Add to parent
        addComponent(analyzeRecipePanel, ingrPanel, analyzeRecipeGbc, 
        0, 3, 2, 1, GridBagConstraints.CENTER);


        // -- Add Ingredient Button --
        JButton addIngrButton = new JButton("Add");
        addIngrButton.setActionCommand("Add Ingredient");
        addIngrButton.addActionListener(this);
        addComponent(analyzeRecipePanel, addIngrButton, analyzeRecipeGbc, 
        0, 4, 2, 1, GridBagConstraints.CENTER);


        // -- Ingredients List Label --
        JPanel ingrListHeaderPanel = new JPanel();
        ingrListHeaderPanel.setLayout(new BoxLayout(ingrListHeaderPanel, BoxLayout.X_AXIS));
        ingrListHeaderPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        JLabel ingrListLabel = new JLabel("Ingredients List", SwingConstants.LEFT);
        ingrListLabel.setFont(new Font("Inter", Font.PLAIN, 15));


        // -- Ingredients List Buttons --
        
        removeIngrButton = new JButton("Remove");
        removeIngrButton.setEnabled(false);
        removeIngrButton.addActionListener(this);
        removeIngrButton.setActionCommand("Remove Ingredient");

        clearIngrListButton = new JButton("Clear");
        clearIngrListButton.setEnabled(false);
        clearIngrListButton.addActionListener(this);
        clearIngrListButton.setActionCommand("Clear Ingredients List");

        ingrListHeaderPanel.add(ingrListLabel);
        ingrListHeaderPanel.add(Box.createHorizontalGlue()); // Full space
        ingrListHeaderPanel.add(clearIngrListButton);
        ingrListHeaderPanel.add(Box.createHorizontalStrut(5)); // Fixed pace
        ingrListHeaderPanel.add(removeIngrButton);

        addComponent(analyzeRecipePanel, ingrListHeaderPanel, analyzeRecipeGbc, 
        0, 5, 2, 1, GridBagConstraints.CENTER);


        // -- Ingredients List --

        // List
        // A model is used since the list items are dynamic.
        ingrListModel = new DefaultListModel<>();
        ingrList = new JList<>(ingrListModel);
        ingrList.addListSelectionListener(this);
        // Selection Mode choices:
        // 1. Single selection
        // 2. Single-interval selection
        // 3. Multiple-interval selection
        ingrList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane ingrListPane = new JScrollPane(ingrList);
        ingrListPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        ingrListPane.setPreferredSize(new Dimension(350, 250));
        addComponent(analyzeRecipePanel, ingrListPane, analyzeRecipeGbc, 
        0, 6, 2, 4, GridBagConstraints.NORTH);

        // Horizontal Line
        JSeparator nutritionAnalyzerSp1 = new JSeparator();
        addComponent(analyzeRecipePanel, nutritionAnalyzerSp1, analyzeRecipeGbc, 
        0, 10, 2, 1, GridBagConstraints.CENTER);

        // Analyze Recipe Button
        analyzeRecipeButton = new JButton("Analyze Recipe");
        analyzeRecipeButton.setActionCommand("Analyze Recipe");
        analyzeRecipeButton.addActionListener(this);
        addComponent(analyzeRecipePanel, analyzeRecipeButton, analyzeRecipeGbc, 
        0, 11, 2, 1, GridBagConstraints.CENTER);

        // Save Recipe Button
        saveRecipeButton = new JButton("Save Recipe");
        saveRecipeButton.setActionCommand("Save Recipe");
        saveRecipeButton.addActionListener(this);
        addComponent(analyzeRecipePanel, saveRecipeButton, analyzeRecipeGbc, 
        0, 12, 2, 1, GridBagConstraints.CENTER);
        // Button is disabled by default.
        // User should not be able to save a recipe without being analyzed.
        saveRecipeButton.setEnabled(false);


        // -----------------------------------
        // --     Nutrition Facts panel     --
        // --     (NutritionFacts.java)     --   
        // -----------------------------------

        NutritionFacts nutritionFactsPanel = new NutritionFacts(currentRecipe);

        // -----------------------------------
        // --     Labels Info panel         --
        // --     (LabelInfo.java)          --   
        // -----------------------------------

        LabelInfo labelInfoPanel = new LabelInfo(currentRecipe);

        /// -----------------------------------
        // --     Types Info panel           --
        // --     (TypeInfo.java)            --   
        // -----------------------------------

        TypeInfo typeInfoPanel = new TypeInfo(currentRecipe);

        firstPanel.add(analyzeRecipePanel);

        // Adds the initial NutritionFacts, LabelInfo, and TypeInfo panels
        // (has no data) (for placeholder purposes)
        firstPanel.add(nutritionFactsPanel);
        firstPanel.add(labelInfoPanel);
        firstPanel.add(typeInfoPanel);

        // Add First Panel to JFrame
        addComponent(mainPanel, firstPanel, mainGbc, 
        0, 0, 1, 1, GridBagConstraints.CENTER);




        // ----------------------------------------------------
        // --                   2nd Panel                    --
        // ----------------------------------------------------
        // - Contains Recipe Groups panel and Recipe Categorization panel
        // - Refer to:
        // https://excalidraw.com/#json=xr-SIzO1nSNy2yksaXUnb,qvfIpSQxJvFlyzVfEx22_A
        JPanel secondPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));



        // -------------------------------------
        // --       Recipe Groups panel       --
        // -------------------------------------
        // Contains Left panel (Recipe Groups panel & Recipes panel)
        // and Right panel (Ingredients Panel)

        JPanel recipeGroupsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, 0));

        // ---------------------------
        // --   Left column panel   --
        // ---------------------------

        // Panel Containing Recipe Groups and Recipes (Left Column)
        JPanel recipeGroupsLeftPanel = new JPanel();
        recipeGroupsLeftPanel.setLayout(new BoxLayout(recipeGroupsLeftPanel, BoxLayout.Y_AXIS));
        recipeGroupsLeftPanel.setPreferredSize(new Dimension(350, 700));


        // [Recipe Groups]

        JPanel recipeGroupsContainer = new JPanel(new BorderLayout());

        // Header (NORTH)
        JPanel recipeGroupsHeader = new JPanel();
        recipeGroupsHeader.setLayout(new BoxLayout(recipeGroupsHeader, BoxLayout.X_AXIS));
        recipeGroupsHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel recipeGroupsTitle = new JLabel("Recipe Groups", SwingConstants.LEFT);
        recipeGroupsTitle.setFont(new Font("Inter", Font.BOLD, 20));

        // Buttons
        addRecipeGroupButton = new JButton("Add"); // Add
        addRecipeGroupButton.setActionCommand("Add Recipe Group");
        addRecipeGroupButton.addActionListener(this);
        addRecipeGroupButton.setMargin(new Insets(2, 10, 2, 10));

        removeRecipeGroupButton = new JButton("Remove"); // Remove
        removeRecipeGroupButton.setActionCommand("Remove Recipe Group");
        removeRecipeGroupButton.addActionListener(this);
        removeRecipeGroupButton.setMargin(new Insets(2, 10, 2, 10));
        removeRecipeGroupButton.setEnabled(false);

        renameRecipeGroupButton = new JButton("Rename"); // Rename
        renameRecipeGroupButton.setActionCommand("Rename Recipe Group");
        renameRecipeGroupButton.addActionListener(this);
        renameRecipeGroupButton.setMargin(new Insets(2, 10, 2, 10));
        renameRecipeGroupButton.setEnabled(false);

        // Add to Header Panel
        recipeGroupsHeader.add(recipeGroupsTitle);
        recipeGroupsHeader.add(Box.createHorizontalGlue()); // space
        recipeGroupsHeader.add(addRecipeGroupButton);
        recipeGroupsHeader.add(removeRecipeGroupButton);        
        recipeGroupsHeader.add(renameRecipeGroupButton);        

        // List (CENTER)
        recipeGroupsModel = new DefaultListModel<>(); // Dynamic content
        recipeGroupsList = new JList<>(recipeGroupsModel);
        reloadRecipeGroupsList();
        recipeGroupsList.addListSelectionListener(this);
        recipeGroupsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane recipeGroupsPane = new JScrollPane(recipeGroupsList);
        recipeGroupsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add Recipe Groups panel to Recipe Groups container
        recipeGroupsContainer.add(recipeGroupsHeader, BorderLayout.NORTH);
        recipeGroupsContainer.add(recipeGroupsPane, BorderLayout.CENTER);


        // [Recipes]
        JPanel recipesContainer = new JPanel(new BorderLayout());

        // Header (NORTH)
        JPanel recipesHeader = new JPanel();
        recipesHeader.setLayout(new BoxLayout(recipesHeader, BoxLayout.X_AXIS));
        recipesHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel recipesTitle = new JLabel("Recipes", SwingConstants.LEFT);
        recipesTitle.setFont(new Font("Inter", Font.BOLD, 20));

        // Buttons
        moveRecipeButton = new JButton("Move");
        moveRecipeButton.setActionCommand("Move Recipe");
        moveRecipeButton.addActionListener(this);
        moveRecipeButton.setMargin(new Insets(2, 10, 2, 10));
        moveRecipeButton.setEnabled(false);

        removeRecipeButton = new JButton("Remove");
        removeRecipeButton.setActionCommand("Remove Recipe");
        removeRecipeButton.addActionListener(this);
        removeRecipeButton.setMargin(new Insets(2, 10, 2, 10));
        removeRecipeButton.setEnabled(false);

        renameRecipeButton = new JButton("Rename");
        renameRecipeButton.setActionCommand("Rename Recipe");
        renameRecipeButton.addActionListener(this);
        renameRecipeButton.setMargin(new Insets(2, 10, 2, 10));
        renameRecipeButton.setEnabled(false);

        // Add to Header
        recipesHeader.add(recipesTitle);
        recipesHeader.add(Box.createHorizontalGlue()); // space
        recipesHeader.add(moveRecipeButton);        
        recipesHeader.add(removeRecipeButton);        
        recipesHeader.add(renameRecipeButton);
        
        // List
        recipesModel = new DefaultListModel<>();
        recipesList = new JList<>(recipesModel);
        recipesList.addListSelectionListener(this);
        recipesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane recipesPane = new JScrollPane(recipesList);
        recipesPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add components to Recipes
        recipesContainer.add(recipesHeader, BorderLayout.NORTH);
        recipesContainer.add(recipesPane, BorderLayout.CENTER);

        // Add Recipes to Recipe Groups (left panel)
        recipeGroupsLeftPanel.add(recipeGroupsContainer);
        recipeGroupsLeftPanel.add(Box.createVerticalStrut(30));
        recipeGroupsLeftPanel.add(recipesContainer);

        // Add recipe groups to Recipe Groups (whole feature) panel
        recipeGroupsPanel.add(recipeGroupsLeftPanel);


        // ---------------------------
        // --   Right column panel  --
        // --   (Ingredients panel) --
        // ---------------------------

        // [Recipes]
        JPanel recipeGroupsRightPanel = new JPanel(new BorderLayout());
        recipeGroupsRightPanel.setPreferredSize(new Dimension(350, 700));

        // Header (NORTH)
        JPanel ingredientsHeader = new JPanel();
        ingredientsHeader.setLayout(new BoxLayout(ingredientsHeader, BoxLayout.X_AXIS));
        ingredientsHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel ingredientsTitle = new JLabel("Ingredients", SwingConstants.LEFT);
        ingredientsTitle.setFont(new Font("Inter", Font.BOLD, 20));
        ingredientsHeader.add(ingredientsTitle);

        // List (CENTER)
        ingredientsModel = new DefaultListModel<>();
        ingredientsList = new JList<>(ingredientsModel);
        ingredientsList.addListSelectionListener(this);
        ingredientsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane ingredientsPane = new JScrollPane(ingredientsList);
        ingredientsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add components to right panel (ingredients list panel)
        recipeGroupsRightPanel.add(ingredientsHeader, BorderLayout.NORTH);
        recipeGroupsRightPanel.add(ingredientsPane, BorderLayout.CENTER);
       
        // Add recipes panel Recipe Groups (whole feature) panel
        recipeGroupsPanel.add(recipeGroupsRightPanel);


        // -------------------------------------
        // --   Recipe Categorization panel   --
        // -------------------------------------
        // Contains
        // 1. Header  (NORTH)
        // 2. Content (CENTER)
        //     - Contains a left and right panel
        //     - Left panel is for labels (diet and health)
        //     - Right panel is for types (cuisine, meal, and dish)

        JPanel recipeCategorizationContainer = new JPanel(new BorderLayout());
        recipeCategorizationContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        recipeCategorizationContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, hgap));


        // -- Header (NORTH) --

        JPanel recipeCategorizationHeader = new JPanel();
        recipeCategorizationHeader.setLayout(new BoxLayout(recipeCategorizationHeader, BoxLayout.X_AXIS));
        recipeCategorizationHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Title
        JLabel recipeCategorizationTitle = new JLabel("Recipe Categorization", SwingConstants.LEFT);
        recipeCategorizationTitle.setFont(new Font("Inter", Font.BOLD, 20));

        // Buttons
        unselectAllButton = new JButton("Unselect All");
        unselectAllButton.setActionCommand("Unselect All Labels");
        unselectAllButton.addActionListener(this);
        recipeCategorizationHeader.add(recipeCategorizationTitle);
        recipeCategorizationHeader.add(Box.createHorizontalGlue());
        recipeCategorizationHeader.add(unselectAllButton);


        // -- Content (CENTER) --
        // Contains:
        // 1. Left panel 
        //      - Diet Labels
        //      - Health Labels
        // 2. Right panel (types)
        //      - Cuisine Types
        //      - Meal Types
        //      - Dish Types
        JPanel recipeCategorizationContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        
        // ---------------------------
        // --   Left column panel   --
        // ---------------------------
        // Contains:
        //   - Diet Labels
        //   - Health Labels

        JPanel categorizationLeftPanel = new JPanel();
        categorizationLeftPanel.setLayout(new BoxLayout(categorizationLeftPanel, BoxLayout.Y_AXIS));
        categorizationLeftPanel.setPreferredSize(new Dimension(380, 650));
        categorizationLeftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, hgap));

        
        // --  Diet Labels  --
        JPanel dietLabelsPanel = new JPanel(new BorderLayout());

        // Header (NORTH)
        JPanel dietLabelsHeader = new JPanel();
        dietLabelsHeader.setLayout(new BoxLayout(dietLabelsHeader, BoxLayout.X_AXIS));
        dietLabelsHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel dietLabelsTitle = new JLabel("Diet Labels", SwingConstants.LEFT);
        dietLabelsTitle.setFont(new Font("Inter", Font.PLAIN, 17));
        dietLabelsHeader.add(dietLabelsTitle);
        
        // List (CENTER)
        String[] dietLabelsOptions = {"BALANCED", "HIGH FIBER", "HIGH PROTEIN", "LOW CARB", "LOW FAT", "LOW SODIUM"};
        dietLabelsList = new JList<>(dietLabelsOptions);
        dietLabelsList.addListSelectionListener(this);
        dietLabelsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane dietLabelsPane = new JScrollPane(dietLabelsList);
        dietLabelsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add components to Diet Labels panel
        dietLabelsPanel.add(dietLabelsHeader, BorderLayout.NORTH);
        dietLabelsPanel.add(dietLabelsPane, BorderLayout.CENTER);


        // --  Health Labels  --

        JPanel healthLabelsPanel = new JPanel(new BorderLayout());

        // Header (NORTH)
        JPanel healthLabelsHeader = new JPanel();
        healthLabelsHeader.setLayout(new BoxLayout(healthLabelsHeader, BoxLayout.X_AXIS));
        healthLabelsHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel healthLabelsTitle = new JLabel("Health Labels", SwingConstants.LEFT);
        healthLabelsTitle.setFont(new Font("Inter", Font.PLAIN, 17));
        healthLabelsHeader.add(healthLabelsTitle);

        // List (CENTER)
        String[] healthLabelsOptions = {"ALCOHOL COCKTAIL", "ALCOHOL FREE", "CELERY FREE", "CRUSTACEAN FREE", "DAIRY FREE", "DASH", "EGG FREE", "FISH FREE", "FODMAP FREE", "GLUTEN FREE", "IMMUNO SUPPORTIVE", "KETO FRIENDLY", "KIDNEY FRIENDLY", "KOSHER", "LOW POTASSIUM", "LOW SUGAR", "LUPINE FREE", "MEDITERRANEAN", "MOLLUSK FREE", "MUSTARD FREE", "NO OIL ADDED", "PALEO", "PEANUT FREE", "PESCATARIAN", "PORK FREE", "RED MEAT FREE", "SESAME FREE", "SHELLFISH FREE", "SOY FREE", "SUGAR CONSCIOUS", "SULPHITE FREE", "TREE NUT FREE", "VEGAN", "VEGETARIAN", "WHEAT FREE"};
        healthLabelsList = new JList<>(healthLabelsOptions);
        healthLabelsList.addListSelectionListener(this);
        healthLabelsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane healthLabelsPane = new JScrollPane(healthLabelsList);
        ingredientsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add components to Health Labels panel
        healthLabelsPanel.add(healthLabelsHeader, BorderLayout.NORTH);
        healthLabelsPanel.add(healthLabelsPane, BorderLayout.CENTER);

        
        // Add Diet Labels and Health Labels panels
        // to Left column panel
        categorizationLeftPanel.add(dietLabelsPanel);
        categorizationLeftPanel.add(Box.createVerticalStrut(30)); // space
        categorizationLeftPanel.add(healthLabelsPanel);


        // ---------------------------
        // --   Right column panel  --
        // ---------------------------
        // Contains:
        //   - Cuisine Types
        //   - Meal Types
        //   - Dish Types

        JPanel categorizationRightPanel = new JPanel();
        categorizationRightPanel.setLayout(new BoxLayout(categorizationRightPanel, BoxLayout.Y_AXIS));
        categorizationRightPanel.setPreferredSize(new Dimension(350, 650));

        // --  Cuisine Types  --
        JPanel cuisineTypesPanel = new JPanel(new BorderLayout());

        // Header (NORTH)
        JPanel cuisineTypesHeader = new JPanel();
        cuisineTypesHeader.setLayout(new BoxLayout(cuisineTypesHeader, BoxLayout.X_AXIS));
        cuisineTypesHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel cuisineTypesTitle = new JLabel("Cuisine Types", SwingConstants.LEFT);
        cuisineTypesTitle.setFont(new Font("Inter", Font.PLAIN, 17));
        cuisineTypesHeader.add(cuisineTypesTitle);

        // List (CENTER)
        String[] cuisineTypesOptions = {"american", "asian", "british", "caribbean", "central europe", "chinese", "eastern europe", "french", "greek", "indian", "italian", "japanese", "korean", "kosher", "mediterranean", "mexican", "middle eastern", "nordic", "south american", "south east asian", "world"};
        cuisineTypesList = new JList<>(cuisineTypesOptions);
        cuisineTypesList.addListSelectionListener(this);
        cuisineTypesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane cuisineTypesPane = new JScrollPane(cuisineTypesList);
        ingredientsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add components to Cuisine Types panel
        cuisineTypesPanel.add(cuisineTypesHeader, BorderLayout.NORTH);
        cuisineTypesPanel.add(cuisineTypesPane, BorderLayout.CENTER);


        // --  Meal Types  --
        JPanel mealTypesPanel = new JPanel(new BorderLayout());

        // Header (NORTH)
        JPanel mealTypesHeader = new JPanel();
        mealTypesHeader.setLayout(new BoxLayout(mealTypesHeader, BoxLayout.X_AXIS));
        mealTypesHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel mealTypesTitle = new JLabel("Meal Types", SwingConstants.LEFT);
        mealTypesTitle.setFont(new Font("Inter", Font.PLAIN, 17));
        mealTypesHeader.add(mealTypesTitle);

        // List (CENTER)
        String[] mealTypesOptions = {"breakfast", "brunch", "lunch/dinner", "snack", "teatime"};
        mealTypesList = new JList<>(mealTypesOptions);
        mealTypesList.addListSelectionListener(this);
        mealTypesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane mealTypesPane = new JScrollPane(mealTypesList);
        ingredientsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add components to Meal Types panel
        mealTypesPanel.add(mealTypesHeader, BorderLayout.NORTH);
        mealTypesPanel.add(mealTypesPane, BorderLayout.CENTER);


        // --  Dish Types  --
        JPanel dishTypesPanel = new JPanel(new BorderLayout());

        // Header (NORTH)
        JPanel dishTypesHeader = new JPanel();
        dishTypesHeader.setLayout(new BoxLayout(dishTypesHeader, BoxLayout.X_AXIS));
        dishTypesHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel dishTypesTitle = new JLabel("Dish Types", SwingConstants.LEFT);
        dishTypesTitle.setFont(new Font("Inter", Font.PLAIN, 17));
        dishTypesHeader.add(dishTypesTitle);

        // List (CENTER)
        String[] dishTypesOptions = {"alcohol cocktail", "biscuits and cookies", "bread", "cereals", "condiments and sauces", "desserts", "drinks", "egg", "ice cream and custard", "main course", "pancake", "pasta", "pastry", "pies and tarts", "pizza", "preps", "preserve", "salad", "sandwiches", "seafood", "side dish", "soup", "special occasions", "starter", "sweets"};
        dishTypesList = new JList<>(dishTypesOptions);
        dishTypesList.addListSelectionListener(this);
        dishTypesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane dishTypesPane = new JScrollPane(dishTypesList);
        ingredientsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add components to Dish Types panel
        dishTypesPanel.add(dishTypesHeader, BorderLayout.NORTH);
        dishTypesPanel.add(dishTypesPane, BorderLayout.CENTER);


        // Add Cuisine Types Panel, Meal Types Panel, Dish Types Panel
        // to right column panel
        categorizationRightPanel.add(cuisineTypesPanel);
        categorizationRightPanel.add(Box.createVerticalStrut(30)); // space
        categorizationRightPanel.add(mealTypesPanel);
        categorizationRightPanel.add(Box.createVerticalStrut(30)); // space
        categorizationRightPanel.add(dishTypesPanel);


        // Add Left column and Right column panel
        // Recipe Categorization content panel (CENTER)
        recipeCategorizationContent.add(categorizationLeftPanel);
        recipeCategorizationContent.add(categorizationRightPanel);


        // Add Header (NORTH) and Recipe Categorization content panel (CENTER)
        recipeCategorizationContainer.add(recipeCategorizationHeader, BorderLayout.NORTH);
        recipeCategorizationContainer.add(recipeCategorizationContent, BorderLayout.CENTER);


        // Add Recipe Groups and Recipe Categorization Panel
        // to Second Panel
        secondPanel.add(recipeGroupsPanel);        
        secondPanel.add(recipeCategorizationContainer);       
        

        // Add Second Panel to Main Panel
        addComponent(mainPanel, new JSeparator(), mainGbc, 
        0, 1, 1, 1, GridBagConstraints.CENTER);
        addComponent(mainPanel, secondPanel, mainGbc, 
        0, 2, 1, 1, GridBagConstraints.CENTER);



        // [Other Event Handlers]

        // Event when the contents of the Ingredients List changes
        ingrListModel.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                // Nothing yet
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                // Nothing yet
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                if (analyzedCurrentRecipe == true) {
                    analyzedCurrentRecipe = false;
                }
            }
        });



        // [Operation to be done when the program closes]
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closing the program...");

                // Creates the gson instance
                Gson gson = new GsonBuilder()
                                .serializeNulls()
                                .setPrettyPrinting()
                                .create();

                // Converts the recipeItems (ArrayList<Recipe>) to a String
                String json = gson.toJson(recipeItems);

                // Stores the converted string to the JSON file
                try {
                    Files.write(Paths.get(dbFileName), json.getBytes());
                } catch (IOException e1) {
                    System.out.println("Error writing data to the JSON file.");
                    e1.printStackTrace();
                }

                // Lastly, exit the program
                System.exit(0);
            }
        });
    }

    // Sends a request to the API (edamam.com) with the ingredients list.
    // Returns nutrition data in JSON form (String).
    public String analyzeIngredients(String payload) throws IOException, InterruptedException {
        return connection.sendRequest(payload);
    }

    // Handles clicks to a button
    @Override
    public void actionPerformed(ActionEvent e) {
        // Determines which button is clicked
        String command = e.getActionCommand();

        if (command.equals("Add Ingredient")) {
            // Get text field contents
            String quantity = quantityTextField.getText();
            String unit = unitTextField.getText();
            String ingredient = ingrTextField.getText();

            // If ingredient textfield is empty, alert user
            if (ingredient == null || ingredient.trim().isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Ingredient cannot be empty.");
                return;
            } 
            // If ingredient textfield is not empty
            else {
                // Reset text field contents
                quantityTextField.setText("0");
                unitTextField.setText("");
                ingrTextField.setText("");

                // Add ingredient to list
                String ingrEntry = quantity + " " + unit + " " + ingredient;
                ingrListModel.addElement(ingrEntry);
            }

            // Ingredient items number
            int ingrNum = ingrListModel.getSize();

            // Enables the 'Clear' button if there are items in the list
            if (ingrNum > 0) {
                clearIngrListButton.setEnabled(true);
            } 
            // Otherwise, disables it
            else {
                clearIngrListButton.setEnabled(false);
            }
        } 
        else if (command.equals("Remove Ingredient")) {
            // Gets the index of the selected ingredient item
            int selectedIndex = ingrList.getSelectedIndex();
            
            // If there is a selected item (not equal to -1)
            if (selectedIndex != -1) {
                // Remove selected ingredient from the list
                ingrListModel.remove(selectedIndex);
            } 
            // If there is no selected item
            else {
                JOptionPane.showMessageDialog(mainPanel, "Nothing is selected.");
            }

            // Disables the 'Clear' button if there are no items in the list
            if (ingrListModel.getSize() == 0) {
                clearIngrListButton.setEnabled(false);
            } 
        } 

        else if (command.equals("Clear Ingredients List")) {
            // Remove all items in the ingredients list
            ingrListModel.clear();
        } 

        else if (command.equals("Analyze Recipe")) {
            // Alerts the user if the ingredients list is empty
            int ingrNum = ingrListModel.getSize();
            if (ingrNum == 0) {
                JOptionPane.showMessageDialog(mainPanel, "Ingredients list is empty.");
                return;
            }

            // Do not analyze if the recipe is already analyzed
            if (analyzedCurrentRecipe) {
                JOptionPane.showMessageDialog(mainPanel, "Already analyzed.");
                return;
            }

            // Creates the JSON payload for the request
            StringBuilder payloadBuilder = new StringBuilder();
            payloadBuilder.append("{\"ingr\": [");
            // Append individual ingredients
            for (int i = 0; i < ingrNum; i++) {
                String ingr = ingrListModel.getElementAt(i);
                payloadBuilder.append('"' + ingr + '"');
                // Adds a comma after every ingredient
                // if it hasn't reached the end yet.
                if (i != (ingrNum - 1)) {
                    payloadBuilder.append(',');
                }
            }
            payloadBuilder.append("]}");
            String payload = payloadBuilder.toString();;

            try {
                // Disables the button to prevent multiple/simultaneous
                // requests to the server.
                analyzeRecipeButton.setEnabled(false);

                // Sends the request to the API
                String response = analyzeIngredients(payload);

                // gson instant
                Gson gson = new Gson();

                // Converts the String response to a Recipe object (Recipe.java)
                Recipe recipe = gson.fromJson(response, Recipe.class);
                currentRecipe = recipe;

                // Already analyzed
                analyzedCurrentRecipe = true;
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(mainPanel, "Error sending request to the API.\n" + exception.getLocalizedMessage());

                // Analysis failed, revert variables
                currentRecipe = null;
                analyzedCurrentRecipe = false;
            } finally {
                // Enables back the button
                analyzeRecipeButton.setEnabled(true);

                // Checks if the response has nutrition data or not.
                // If it doesn't, it may be that the recipe inputted by the user
                // is not valid.
                if (currentRecipe.getCalories() == 0) {
                    JOptionPane.showMessageDialog(mainPanel, "Invalid recipe.\nPlease check the spelling of the ingredients and then try again.");

                    // After being modified, recipe is expected to be analyzed again
                    analyzedCurrentRecipe = false;
                }

                // Updates the NutritionFacts, LabelInfo, and TypeInfo panels.
                // Removes the previous panels
                // and then adds the updated ones.
                Component[] components = firstPanel.getComponents();
                if (components.length > 0) {
                    firstPanel.remove(components[components.length - 1]);
                    firstPanel.remove(components[components.length - 2]);
                    firstPanel.remove(components[components.length - 3]);
                }
                NutritionFacts nutritionFactsPanel = new NutritionFacts(currentRecipe);
                firstPanel.add(nutritionFactsPanel);

                LabelInfo labelInfoPanel = new LabelInfo(currentRecipe);
                firstPanel.add(labelInfoPanel);

                TypeInfo healthInfoPanel = new TypeInfo(currentRecipe);
                firstPanel.add(healthInfoPanel);

                // Renders the new changes
                firstPanel.revalidate();
                firstPanel.repaint();
            }
        } 

        else if (command.equals("Save Recipe")) {
            // Alerts the user if the ingredients list is empty
            int ingrNum = ingrListModel.getSize();
            if (ingrNum == 0) {
                JOptionPane.showMessageDialog(mainPanel, "Ingredients list is empty.");
                return;
            }

            // Asks for the title of the recipe
            String recipeTitle = null;
            do {
                recipeTitle = JOptionPane.showInputDialog(null, "Enter Recipe Title: ", "Add Title", JOptionPane.QUESTION_MESSAGE);

                // Cancels the save recipe operation if the user closes the dialog
                if (recipeTitle == null) {
                    return;
                }

                // Title is inputted is already used
                if (recipeTitle != null && recipeTitles.contains(recipeTitle)) {
                    JOptionPane.showMessageDialog(mainPanel, "Title is already used. Please create a new one.");
                    return;
                }
            } while (recipeTitle == null);


            if (recipeGroups.size() > 0) {
                // Asks if the user would like to add the recipe to a group
                int choice = JOptionPane.showConfirmDialog(null, "Would you like to add the recipe to a group?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                // "Yes"
                if (choice == JOptionPane.YES_OPTION) {
                    String group = selectGroup("Select the number of the group to which the recipe will be added.", false);

                    // User cancelled the operation
                    if (group == null) {
                        return;
                    }

                    currentRecipe.setGroup(group);
                } 
                // "No" or cancelled
                else if (choice == JOptionPane.NO_OPTION || choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                    // No group
                    currentRecipe.setGroup(null);
                }
            } else {
                // No group/s added yet
                currentRecipe.setGroup(null);
            }

            // Add recipe title to existing list of titles
            recipeTitles.add(recipeTitle);

            // Set the inputted title as the title of the recipe
            currentRecipe.setTitle(recipeTitle);

            // Add Recipe to the ArrayList (to be stored later on the JSON file)
            recipeItems.add(currentRecipe);

            // Add/analyze recipe session is finished, 
            // clears current recipe.
            currentRecipe = null;
            ingrListModel.clear();
            analyzedCurrentRecipe = false;

            reloadRecipesList();
        }

        if (command.equals("Add Ingredient") || command.equals("Remove Ingredient") || command.equals("Clear Ingredients List") || command.equals("Analyze Recipe") || command.equals("Save Recipe")) {
            // User can only save a recipe (i.e. the 'Save Recipe' button is enabled) 
            // if the current recipe has been analyzed.
            if (analyzedCurrentRecipe == true) {
                saveRecipeButton.setEnabled(true);
            } else {
                saveRecipeButton.setEnabled(false);
            }
        }

        if (command.equals("Add Recipe Group")) {
            String groupName;

            // Use while loop for error-handling
            while (true) {
                groupName = JOptionPane.showInputDialog(null, "Enter group name: ", "Add Recipe Group", JOptionPane.QUESTION_MESSAGE);

                // User cancelled the operation
                if (groupName == null) {
                    return;
                }

                // Empty string inputted
                if (groupName.equals("")) {
                    JOptionPane.showMessageDialog(mainPanel, "Invalid name. Please try again.");
                    continue;
                }

                // User should not be able to add a group with the title "Ungrouped"
                if (groupName.equals("Ungrouped")) {
                    JOptionPane.showMessageDialog(mainPanel, "Cannot use the group name. Please try other names.");
                    continue;
                }

                // Group name is already used
                if (recipeGroups.contains(groupName)) {
                    JOptionPane.showMessageDialog(mainPanel, "Group name already used. Please try again.");
                    continue;
                }

                break;
            }

            // Adds new group to the ArrayList
            recipeGroups.add(groupName);

            // Refresh group names in the list
            reloadRecipeGroupsList();

            // Items selected are unselected,
            // disables the 'Remove' and 'Rename' buttons
            removeRecipeGroupButton.setEnabled(false);
            renameRecipeGroupButton.setEnabled(false);
        } 
        
        if (command.equals("Remove Recipe Group")) {
            // Gets the index of the selected recipe
            int selectedIndex = recipeGroupsList.getSelectedIndex();
 
            // User should not be able to remove the 'Ungrouped' group (first item)
            if (selectedIndex == 0) {
                JOptionPane.showMessageDialog(mainPanel, "Cannot remove the 'Ungrouped' group.");
                return;
            }

            // Warns the user that the recipes in the group will also be deleted
            int choice = JOptionPane.showConfirmDialog(null, "All recipes that are in this group will be deleted. Press \"Yes\" to confirm deleting the group.", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                // If there is a selected item
                if (selectedIndex != -1) {
                    // Gets the selected value
                    String selectedGroup = recipeGroupsList.getSelectedValue().toString();

                    // Remove group
                    recipeGroups.remove(selectedGroup);
                    recipeGroupsModel.remove(selectedIndex);

                    // Iterates recipeItems,
                    // deletes recipes that are in the group
                    for (int i = 0; i < recipeItems.size(); i++) {
                        String groupName = recipeItems.get(i).getGroup();
                        if (groupName != null && groupName.equals(selectedGroup)) {
                            recipeItems.remove(i);
                        }
                    }   
                }
    
                // Refresh group names in the list
                reloadRecipeGroupsList();

                // Items selected are unselected,
                // disables the 'Remove' and 'Rename' buttons
                removeRecipeGroupButton.setEnabled(false);
                renameRecipeGroupButton.setEnabled(false);
            }
        } 
        
        if (command.equals("Rename Recipe Group")) {
            int selectedIndex = recipeGroupsList.getSelectedIndex();
            
            // User should not be able to rename the 'Ungrouped' group (first item)
            if (selectedIndex == 0) {
                JOptionPane.showMessageDialog(mainPanel, "Cannot rename the 'Ungrouped' group.");
                return;
            }

            String groupName;

            // Use while loop for error-handling
            while (true) {
                groupName = JOptionPane.showInputDialog(null, "Rename the group name to: ", "Rename Group", JOptionPane.QUESTION_MESSAGE);

                // User cancelled the operation
                if (groupName == null) {
                    return;
                }

                if (groupName.equals("")) {
                    JOptionPane.showMessageDialog(mainPanel, "Invalid name. Please try again.");
                    continue;
                }

                if (groupName.equals("Ungrouped")) {
                    JOptionPane.showMessageDialog(mainPanel, "Cannot use that as a group name. Please try other names.");
                    continue;
                }

                if (recipeGroups.contains(groupName)) {
                    JOptionPane.showMessageDialog(mainPanel, "Group name already used. Please try again.");
                    continue;
                }

                break;
            }

            String oldGroupName = recipeGroupsList.getSelectedValue().toString();

            // Changes all occurences of the old group name to the new group name
            recipeGroups.remove(oldGroupName);
            recipeGroups.add(groupName);
            for (Recipe recipe : recipeItems) {
                if (recipe.getGroup() != null && recipe.getGroup().equals(oldGroupName)) {
                    recipe.setGroup(groupName);
                }
            }

            reloadRecipeGroupsList();
        } 


        if (command.equals("Move Recipe")) {
            String newGroupName = selectGroup("Select the number of the group to which the recipe will be moved.", true);

            // User cancelled the operation
            if (newGroupName == null) {
                return;
            }

            // Ungrouped recipes should have `null` as the group value
            // in the JSON file
            if (newGroupName.equals("Ungrouped")) {
                newGroupName = null;  
            }

            // Finds the selected recipe,
            // and then changes its group name to the new group name
            String selectedRecipeTitle = recipesList.getSelectedValue().toString();
            for (Recipe recipe : recipeItems) {
                if (recipe.getTitle().equals(selectedRecipeTitle)) {
                    recipe.setGroup(newGroupName);
                }
            }

            reloadRecipesList();
        } 
        
        if (command.equals("Remove Recipe")) {
            String selectedRecipe = recipesList.getSelectedValue().toString();

            int choice = JOptionPane.showConfirmDialog(null, "Confirm deletion of recipe \"" + selectedRecipe + "\".", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                // Remove recipe from the recipeTitles HashSet<String> 
                recipeTitles.remove(selectedRecipe);
                
                // Remove recipe from the recipeItems ArrayList<Recipe>
                for (int i = 0; i < recipeItems.size(); i++) {
                    if (recipeItems.get(i).getTitle().equals(selectedRecipe)) {
                        recipeItems.remove(i);
                    }
                } 
            } else {
                return;
            }

            reloadRecipesList();
        } 
        
        if (command.equals("Rename Recipe")) {
            String newName;

            // Use while loop for error-handling
            while (true) {
                newName = JOptionPane.showInputDialog(null, "Change the recipe name to: ", "Rename Recipe", JOptionPane.QUESTION_MESSAGE);

                // User cancelled the operation
                if (newName == null) {
                    return;
                }

                if (newName.equals("")) {
                    JOptionPane.showMessageDialog(mainPanel, "Invalid name. Please try again.");
                    continue;
                }

                // Recipe title already used
                if (recipeTitles.contains(newName)) {
                    JOptionPane.showMessageDialog(mainPanel, "Recipe name is already used. Please try again.");
                    continue;
                }

                break;
            }

            String oldName = recipesList.getSelectedValue().toString();

            // Changes all occurences of the old name to the new name
            // - In the recipeTitles HashSet<String>
            recipeTitles.remove(oldName);
            recipeTitles.add(newName);
            // - In the recipeItems ArrayList<Recipe>
            for (Recipe recipe : recipeItems) {
                if (recipe.getTitle().equals(oldName)) {
                    recipe.setTitle(newName);
                    break;
                }
            }

            reloadRecipesList();
        } 

        if (command.equals("Unselect All Labels")) {
            dietLabelsList.clearSelection();
            healthLabelsList.clearSelection();
            cuisineTypesList.clearSelection();
            mealTypesList.clearSelection();
            dishTypesList.clearSelection();
        }
    }

    // Handle changes in the selection of items in a JList
    @Override
    public void valueChanged(ListSelectionEvent e) {
        // Determines which JList triggered the event
        JList<?> sourceList = (JList<?>) e.getSource();

        if (!e.getValueIsAdjusting()) {
            // Ingredient List (Nutrition Analysis)
            if (sourceList == ingrList) {
                int selectedIndex = ingrList.getSelectedIndex();

                // Enables the 'Remove' button if there is an item selected
                if (selectedIndex != -1) {
                    removeIngrButton.setEnabled(true);
                } 
                // Otherwise, disables it
                else {
                    removeIngrButton.setEnabled(false);
                }
            }

            // Recipe Groups List
            if (sourceList == recipeGroupsList) {
                String selectedGroup = recipeGroupsList.getSelectedValue();
                
                // If there is a selected item
                if (selectedGroup != null) {
                    // Enables the 'Remove' and 'Rename' buttons
                    removeRecipeGroupButton.setEnabled(true);
                    renameRecipeGroupButton.setEnabled(true);

                    // Reloads Recipe list
                    reloadRecipesList();
                } 
                // i.e. there are no selected item/s
                else {
                    // Disables the 'Remove' and 'Rename' buttons
                    removeRecipeGroupButton.setEnabled(false);
                    renameRecipeGroupButton.setEnabled(false);

                    // Clears Recipe list
                    recipesModel.clear();
                }
            }

            // Recipes List
            if (sourceList == recipesList) {
                String selectedRecipe = recipesList.getSelectedValue();

                // If there is a selected item
                if (selectedRecipe != null) {

                    // Enables the 'Move', 'Remove' and 'Rename' buttons
                    moveRecipeButton.setEnabled(true);
                    removeRecipeButton.setEnabled(true);
                    renameRecipeButton.setEnabled(true);

                    // Gets the recipe object
                    Recipe targetRecipe = null;
                    for (Recipe recipe : recipeItems) {
                        if (recipe.getTitle().equals(selectedRecipe)) {
                            targetRecipe = recipe;
                            break;
                        }
                    }

                    // Shows the ingredients of the recipe
                    // on the Ingredients list
                    ingredientsModel.clear();
                    List<Ingredient> ingredients = targetRecipe.getIngredients();
                    for (Ingredient ingr : ingredients) {
                        ingredientsModel.addElement(ingr.getText());
                    }

                    // Shows the NutritionFacts, LabelInfo, and TypeInfo of the recipe.
                    // Removes the previous panels
                    // and then adds the updated ones.
                    Component[] components = firstPanel.getComponents();
                    if (components.length > 0) {
                        firstPanel.remove(components[components.length - 1]);
                        firstPanel.remove(components[components.length - 2]);
                        firstPanel.remove(components[components.length - 3]);
                    }
                    NutritionFacts nutritionFactsPanel = new NutritionFacts(targetRecipe);
                    firstPanel.add(nutritionFactsPanel);

                    LabelInfo labelInfoPanel = new LabelInfo(targetRecipe);
                    firstPanel.add(labelInfoPanel);

                    TypeInfo healthInfoPanel = new TypeInfo(targetRecipe);
                    firstPanel.add(healthInfoPanel);

                    firstPanel.revalidate();
                    firstPanel.repaint();
                } else {
                    // Disables the 'Move', 'Remove' and 'Rename' buttons
                    moveRecipeButton.setEnabled(false);
                    removeRecipeButton.setEnabled(false);
                    renameRecipeButton.setEnabled(false);

                    // Clears the Ingredients list
                    ingredientsModel.clear();

                    // Clears the NutritionFacts, LabelInfo, and TypeInfo.
                    // Removes the previous panels
                    // and then adds the updated ones.
                    Component[] components = firstPanel.getComponents();
                    if (components.length > 0) {
                        firstPanel.remove(components[components.length - 1]);
                        firstPanel.remove(components[components.length - 2]);
                        firstPanel.remove(components[components.length - 3]);
                    }
                    
                    // Use null when there are no data
                    NutritionFacts nutritionFactsPanel = new NutritionFacts(null);
                    firstPanel.add(nutritionFactsPanel);

                    LabelInfo labelInfoPanel = new LabelInfo(null);
                    firstPanel.add(labelInfoPanel);

                    TypeInfo healthInfoPanel = new TypeInfo(null);
                    firstPanel.add(healthInfoPanel);

                    // Renders the changes
                    firstPanel.revalidate();
                    firstPanel.repaint();
                }
            }

            // Diet Labels List
            if (sourceList == dietLabelsList) {
                // Gets the index of the first element selected
                int selectedIndex = dietLabelsList.getSelectedIndex();

                // Get the list of selected values
                List<String> selectedValues = dietLabelsList.getSelectedValuesList();

                // Replaces the spaces to underscores.
                // Labels have underscores instead of spaces in the database,
                // and in the server, so we have to convert them.
                dietLabels.clear();
                for (String label : selectedValues) {
                    String labelWithUnderscores = label.replace(' ', '_');
                    dietLabels.add(labelWithUnderscores);
                }

                // Clears the Recipes list
                recipesModel.clear();

                // Fills the Recipes list with the matched recipes
                List<Recipe> matchedRecipes;
                if (selectedIndex != -1) {
                    // Finds the recipes that matches the specified labels and types
                    matchedRecipes = Recipe.findRecipesByCriteria(recipeItems, dietLabels, healthLabels, cuisineTypes, mealTypes, dishTypes);

                    // Adds them to the list
                    for (Recipe recipe : matchedRecipes) {
                        recipesModel.addElement(recipe.getTitle());
                    }
                }

                // Unselects (if any) the selected values in the Recipe Groups list
                recipeGroupsList.clearSelection();
            }


            // Health Labels List
            if (sourceList == healthLabelsList) {
                // Gets the index of the first element selected
                int selectedIndex = healthLabelsList.getSelectedIndex();
                // Get the list of selected values
                List<String> selectedValues = healthLabelsList.getSelectedValuesList();

                // Updates healthLabels with the new selected values
                healthLabels.clear();
                for (String label : selectedValues) {
                    String labelWithUnderscores = label.replace(' ', '_');
                    healthLabels.add(labelWithUnderscores);
                }

                // Clears the Recipes list
                recipesModel.clear();

                // Fills the Recipes list with the matched recipes
                List<Recipe> matchedRecipes;
                if (selectedIndex != -1) {
                    // Finds the recipes that matches the specified labels and types
                    matchedRecipes = Recipe.findRecipesByCriteria(recipeItems, dietLabels, healthLabels, cuisineTypes, mealTypes, dishTypes);

                    // Adds the to the list
                    for (Recipe recipe : matchedRecipes) {
                        recipesModel.addElement(recipe.getTitle());
                    }
                }

                // Unselects (if any) the selected values in the Recipe Groups list
                recipeGroupsList.clearSelection();
            }


            // Cuisine Types List
            if (sourceList == cuisineTypesList) {
                // Gets the index of the first element selected
                int selectedIndex = cuisineTypesList.getSelectedIndex();
                // Get the list of selected values
                List<String> selectedValues = cuisineTypesList.getSelectedValuesList();

                // Updates cuisineTypes with the new selected values
                cuisineTypes.clear();
                for (String label : selectedValues) {
                    cuisineTypes.add(label);
                }

                // Clears the Recipes list
                recipesModel.clear();

                // Fills the Recipes list with the matched recipes
                List<Recipe> matchedRecipes;
                if (selectedIndex != -1) {
                    // Finds the recipes that matches the specified labels and types
                    matchedRecipes = Recipe.findRecipesByCriteria(recipeItems, dietLabels, healthLabels, cuisineTypes, mealTypes, dishTypes);

                    // Adds the to the list
                    for (Recipe recipe : matchedRecipes) {
                        recipesModel.addElement(recipe.getTitle());
                    }
                }

                // Unselects (if any) the selected values in the Recipe Groups list
                recipeGroupsList.clearSelection();
            }

            // Meal Types List
            if (sourceList == mealTypesList) {
                // Gets the index of the first element selected
                int selectedIndex = mealTypesList.getSelectedIndex();
                // Get the list of selected values
                List<String> selectedValues = mealTypesList.getSelectedValuesList();

                // Updates mealTypes with the new selected values
                mealTypes.clear();
                for (String label : selectedValues) {
                    mealTypes.add(label);
                }

                // Clears the Recipes list
                recipesModel.clear();

                // Fills the Recipes list with the matched recipes
                List<Recipe> matchedRecipes;
                if (selectedIndex != -1) {
                    // Finds the recipes that matches the specified labels and types
                    matchedRecipes = Recipe.findRecipesByCriteria(recipeItems, dietLabels, healthLabels, cuisineTypes, mealTypes, dishTypes);

                    // Adds the to the list
                    for (Recipe recipe : matchedRecipes) {
                        recipesModel.addElement(recipe.getTitle());
                    }
                }

                // Unselects (if any) the selected values in the Recipe Groups list
                recipeGroupsList.clearSelection();
            }

            
            // Dish Types List
            if (sourceList == dishTypesList) {
                // Gets the index of the first element selected
                int selectedIndex = dishTypesList.getSelectedIndex();
                // Get the list of selected values
                List<String> selectedValues = dishTypesList.getSelectedValuesList();

                // Updates dishTypes with the new selected values
                dishTypes.clear();
                for (String label : selectedValues) {
                    dishTypes.add(label);
                }

                // Clears the Recipes list
                recipesModel.clear();

                // Fills the Recipes list with the matched recipes
                List<Recipe> matchedRecipes;
                if (selectedIndex != -1) {
                    // Finds the recipes that matches the specified labels and types
                    matchedRecipes = Recipe.findRecipesByCriteria(recipeItems, dietLabels, healthLabels, cuisineTypes, mealTypes, dishTypes);

                    // Adds the to the list
                    for (Recipe recipe : matchedRecipes) {
                        recipesModel.addElement(recipe.getTitle());
                    }
                }

                // Unselects (if any) the selected values in the Recipe Groups list
                recipeGroupsList.clearSelection();
            }
        }
    }

    // Utility method for adding component in a GridBagLayout
    private static void addComponent(Container container, Component component, 
                                     GridBagConstraints gbc, int gridx, int gridy,
                                     int gridwidth, int gridheight, int anchor) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.anchor = anchor;
        container.add(component, gbc);
    }

    private String selectGroup(String message, boolean includeUngrouped) {
        // Asks which group to which the user would like to add
        // the recipe
        StringBuilder groupsStrBuilder = new StringBuilder();
        int i;
        for (i = 0; i < recipeGroups.size(); i++) {
            groupsStrBuilder.append("[" + (i + 1) + "] " + recipeGroups.get(i) + "\n");
        }
        if (includeUngrouped) {
            groupsStrBuilder.append("[" + (i + 1) + "] " + "Ungrouped" + "\n");
        }        
        String groupsStr = groupsStrBuilder.toString();

        // Gets the index of the group chosen
        int groupIndex;

        // Use while loop for error handling
        while (true) {
            String groupIndexStr = JOptionPane.showInputDialog(null, groupsStr + "\n" + message, "Select Group", JOptionPane.QUESTION_MESSAGE);

            // User cancelled the operation
            if (groupIndexStr == null) {
                return null;
            }

            // Retries if the user did not input any number
            try {
                groupIndex = Integer.parseInt(groupIndexStr);
            } catch (NumberFormatException exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(mainPanel, "Invalid input. Please try again.");
                continue;
            }

            // Gets the real index
            groupIndex--;

            // Retries if the user inputted a number that is outside
            // of the valid range
            if (
                (!includeUngrouped && (groupIndex < 0 || groupIndex >= recipeGroups.size())) || 
                (includeUngrouped && groupIndex < 0 || groupIndex >= recipeGroups.size() + 1)
            ) {
                JOptionPane.showMessageDialog(mainPanel, "Invalid input. Please try again.");
                continue;
            }

            break;
        }

        if (includeUngrouped && (groupIndex == i)) {
            return "Ungrouped";
        }

        return recipeGroups.get(groupIndex);
    }

    private void reloadRecipeGroupsList() {
        recipeGroupsModel.clear();

        // Always the the "Ungrouped" group
        // representing the recipes that are without groups.
        recipeGroupsModel.addElement("Ungrouped");

        for (String group : recipeGroups) {
            recipeGroupsModel.addElement(group);
        }
    }

    private void reloadRecipesList() {
        // Updates the recipes in the Recipes List
        recipesModel.clear();
        String selectedGroup = recipeGroupsList.getSelectedValue();
        // If there is a selected value
        if (selectedGroup != null) {
            // Converts it to string
            selectedGroup = selectedGroup.toString();

            // If "Ungrouped" is selected, show the recipes 
            // that have no groups on the "Recipes" list.
            if (selectedGroup.equals("Ungrouped")) {
                for (Recipe recipe : recipeItems) {
                    if (recipe.getGroup() == null) {
                        recipesModel.addElement(recipe.getTitle());
                    }
                }
            } 
            // Else, show the recipes in the selected group
            else {
                for (Recipe recipe : recipeItems) {
                    if (recipe.getGroup() != null && recipe.getGroup().equals(selectedGroup)) {
                        recipesModel.addElement(recipe.getTitle());
                    }
                }
            }
        }
    }
}
 