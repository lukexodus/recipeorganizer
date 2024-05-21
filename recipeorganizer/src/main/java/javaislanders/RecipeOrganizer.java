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
    final private String dbFileName = "recipe-organizer-database.json"; // Filename of the json file

    // Layout Variables
    int hgap = 60;

    // Global Data
    private ArrayList<String> recipeGroups = new ArrayList<>();
    private HashSet<String> recipeTitles = new HashSet<>();
    private ArrayList<Recipe> recipeItems = null;  // This is one stored in the json file

    // Main Panel
    JPanel mainPanel;

    // Nutrition Analysis
    private JPanel firstPanel;
    private JList<String> ingrList;
    DefaultListModel<String> ingrListModel;
    private JFormattedTextField quantityTextField;
    private JTextField unitTextField;
    private JTextField ingrTextField;
    private JButton removeIngrBtn;
    private JButton clearIngrListBtn;
    private JButton analyzeRecipeBtn;
    private JButton saveRecipeBtn;
    private Recipe currentRecipe;
    private boolean analyzedCurrentRecipe = false;

    // Recipe Groups
    private JList<String> recipeGroupsList;  // Left Panel
    DefaultListModel<String> recipeGroupsModel;
    private JList<String> recipesList;
    DefaultListModel<String> recipesModel;
    private JButton addRecipeGrpBtn;
    private JButton removeRecipeGrpBtn;
    private JButton renameRecipeGrpBtn;
    private JButton moveRecipeBtn;
    private JButton removeRecipeBtn;
    private JButton renameRecipeBtn;
    private JList<String> ingredientsList;
    DefaultListModel<String> ingredientsModel;  // Right Panel

    // Recipe Categorization
    private JButton unselectAllBtn;
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



        // [Create DB file (json) if it does not exist]
        File dbFile = new File(dbFileName);
        try {
            // Check if the json file exists
            if (!dbFile.exists()) {
                // Create the json file
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
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }



        // [Load DB Data]
        try {
            Gson gson = new Gson();

            // Load the JSON file
            String json = new String(Files.readAllBytes(Paths.get(dbFileName)));

            Recipe[] recipeItemsArray = gson.fromJson(json, Recipe[].class);
            List<Recipe> tempList = Arrays.asList(recipeItemsArray);
            recipeItems = new ArrayList<>(tempList);

            for (Recipe recipe : recipeItems) {
                recipeTitles.add(recipe.getTitle());
                if (recipe.getGroup() != null) {
                    recipeGroups.add(recipe.getGroup());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



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
        // --                       MAIN PANEL                         -- 
        // --------------------------------------------------------------
        // Contains the 3 features:
        // - Nutrition Analysis
        // - Recipe Lists
        // - Recipe Categorization

        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.fill = GridBagConstraints.VERTICAL;
        // Padding
        mainGbc.insets = new Insets(25, 15, 25, 15);
        // Margins
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JScrollPane mainPanelPane = new JScrollPane(mainPanel);
        mainPanelPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(mainPanelPane, BorderLayout.CENTER);


        // ----------------------------------------------------
        // --                    1st Panel                   --
        // ----------------------------------------------------
        // - Contains Nutrition Analyzer panel, Nutrition Facts panel,
        //   Labels Info Panel, And Types Info Panel

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
        unitTextField.setPreferredSize(new Dimension(180, 30));
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
        ingrTextField.setPreferredSize(new Dimension(350, 30));
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
        JPanel ingrListHeaderPanel = new JPanel();
        ingrListHeaderPanel.setLayout(new BoxLayout(ingrListHeaderPanel, BoxLayout.X_AXIS));
        ingrListHeaderPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
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
        ingrListHeaderPanel.add(ingrListLabel);
        ingrListHeaderPanel.add(Box.createHorizontalGlue());
        ingrListHeaderPanel.add(clearIngrListBtn);
        ingrListHeaderPanel.add(Box.createHorizontalStrut(5));
        ingrListHeaderPanel.add(removeIngrBtn);
        addComponent(analyzeRecipePanel, ingrListHeaderPanel, analyzeRecipeGbc, 
        0, 5, 2, 1, GridBagConstraints.CENTER);

        // Ingredients List
        ingrListModel = new DefaultListModel<>();
        ingrList = new JList<>(ingrListModel);
        ingrList.addListSelectionListener(this);
        ingrList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane ingrListPane = new JScrollPane(ingrList);
        ingrListPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        ingrListPane.setPreferredSize(new Dimension(350, 250));
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

        // Save Recipe Button
        saveRecipeBtn = new JButton("Save Recipe");
        saveRecipeBtn.setActionCommand("Save Recipe");
        saveRecipeBtn.addActionListener(this);
        addComponent(analyzeRecipePanel, saveRecipeBtn, analyzeRecipeGbc, 
        0, 12, 2, 1, GridBagConstraints.CENTER);
        // Button is disabled by default.
        // User cannot save a recipe without being analyzed.
        saveRecipeBtn.setEnabled(false);


        // -------------------------------------
        // --      Nutrition Facts panel      --
        // -------------------------------------

        NutritionFacts nutritionFactsPanel = new NutritionFacts(currentRecipe);

        // -------------------------------------
        // --      Label Info panel      --
        // -------------------------------------

        LabelInfo labelInfoPanel = new LabelInfo(currentRecipe);

        // -------------------------------------
        // --      Type Info Facts panel      --
        // -------------------------------------

        TypeInfo typeInfoPanel = new TypeInfo(currentRecipe);

        firstPanel.add(analyzeRecipePanel);
        // Adds the initial NutritionFacts panel (has no data) (for placeholder purposes)
        firstPanel.add(nutritionFactsPanel);

        firstPanel.add(labelInfoPanel);

        firstPanel.add(typeInfoPanel);

        addComponent(mainPanel, firstPanel, mainGbc, 
        0, 0, 1, 1, GridBagConstraints.CENTER);




        // ----------------------------------------------------
        // --                   2nd Panel                    --
        // ----------------------------------------------------
        // - Contains Recipe Groups panel and Recipe Categorization panel
        // - Refer to:
        // https://excalidraw.com/#json=DBau19YisTOxO_BHixQ_r,yCyuvZhxDG6rgqgjZjsAVQ
        JPanel secondPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));



        // -------------------------------------
        // --       Recipe Groups panel       --
        // -------------------------------------
        // Contains Recipe Groups panel & Recipes panel
        // and Ingredients panel
        
        JPanel recipeGroupsRecipesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, 0));
        JLabel recipeGroupsRecipesPanelPlaceholder = new JLabel("Recipe Groups Placeholder");

        recipeGroupsRecipesPanel.add(recipeGroupsRecipesPanelPlaceholder);


        // -------------------------------------
        // --   Recipe Categorization panel   --
        // -------------------------------------
        // 1. Header
        // 2. Content
        //  - Contains a left and right panel
        //  - Left panel is for labels (diet and health)
        //  - Right panel is for types (cuisine, meal, and dish)

        JPanel recipeCategorizationContainer = new JPanel(new BorderLayout());
        recipeCategorizationContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        recipeCategorizationContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, hgap));

        // Header
        JPanel recipeCategorizationHeader = new JPanel();
        recipeCategorizationHeader.setLayout(new BoxLayout(recipeCategorizationHeader, BoxLayout.X_AXIS));
        recipeCategorizationHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JLabel recipeCategorizationTitle = new JLabel("Recipe Categorization", SwingConstants.LEFT);
        recipeCategorizationTitle.setFont(new Font("Inter", Font.BOLD, 20));
        unselectAllBtn = new JButton("Unselect All");
        unselectAllBtn.setActionCommand("Unselect All Labels");
        unselectAllBtn.addActionListener(this);
        recipeCategorizationHeader.add(recipeCategorizationTitle);
        recipeCategorizationHeader.add(Box.createHorizontalGlue());
        recipeCategorizationHeader.add(unselectAllBtn);

        // Content
        JPanel recipeCategorizationContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        // recipeCategorizationContent.setPreferredSize(new Dimension(760, 650));

        // ---------------------------
        // --   Left column panel   --
        // ---------------------------

        JPanel categorizationLeftPanel = new JPanel();
        categorizationLeftPanel.setLayout(new BoxLayout(categorizationLeftPanel, BoxLayout.Y_AXIS));
        categorizationLeftPanel.setPreferredSize(new Dimension(380, 650));
        categorizationLeftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, hgap));

        // --  Diet Labels  --
        JPanel dietLabelsPanel = new JPanel(new BorderLayout());

        // Header
        JPanel dietLabelsHeader = new JPanel();
        dietLabelsHeader.setLayout(new BoxLayout(dietLabelsHeader, BoxLayout.X_AXIS));
        dietLabelsHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel dietLabelsTitle = new JLabel("Diet Labels", SwingConstants.LEFT);
        dietLabelsTitle.setFont(new Font("Inter", Font.PLAIN, 17));
        dietLabelsHeader.add(dietLabelsTitle);

        // List
        String[] dietLabelsOptions = {"BALANCED", "HIGH FIBER", "HIGH PROTEIN", "LOW CARB", "LOW FAT", "LOW SODIUM"};
        dietLabelsList = new JList<>(dietLabelsOptions);
        dietLabelsList.addListSelectionListener(this);
        dietLabelsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane dietLabelsPane = new JScrollPane(dietLabelsList);
        dietLabelsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        dietLabelsPanel.add(dietLabelsHeader, BorderLayout.NORTH);
        dietLabelsPanel.add(dietLabelsPane, BorderLayout.CENTER);

        // --  Health Labels  --

        JPanel healthLabelsPanel = new JPanel(new BorderLayout());

        // Header
        JPanel healthLabelsHeader = new JPanel();
        healthLabelsHeader.setLayout(new BoxLayout(healthLabelsHeader, BoxLayout.X_AXIS));
        healthLabelsHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel healthLabelsTitle = new JLabel("Health Labels", SwingConstants.LEFT);
        healthLabelsTitle.setFont(new Font("Inter", Font.PLAIN, 17));
        healthLabelsHeader.add(healthLabelsTitle);

        // List
        String[] healthLabelsOptions = {"ALCOHOL COCKTAIL", "ALCOHOL FREE", "CELERY FREE", "CRUSTACEAN FREE", "DAIRY FREE", "DASH", "EGG FREE", "FISH FREE", "FODMAP FREE", "GLUTEN FREE", "IMMUNO SUPPORTIVE", "KETO FRIENDLY", "KIDNEY FRIENDLY", "KOSHER", "LOW POTASSIUM", "LOW SUGAR", "LUPINE FREE", "MEDITERRANEAN", "MOLLUSK FREE", "MUSTARD FREE", "NO OIL ADDED", "PALEO", "PEANUT FREE", "PESCATARIAN", "PORK FREE", "RED MEAT FREE", "SESAME FREE", "SHELLFISH FREE", "SOY FREE", "SUGAR CONSCIOUS", "SULPHITE FREE", "TREE NUT FREE", "VEGAN", "VEGETARIAN", "WHEAT FREE"};
        healthLabelsList = new JList<>(healthLabelsOptions);
        healthLabelsList.addListSelectionListener(this);
        healthLabelsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane healthLabelsPane = new JScrollPane(healthLabelsList);
        healthLabelsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        healthLabelsPanel.add(healthLabelsHeader, BorderLayout.NORTH);
        healthLabelsPanel.add(healthLabelsPane, BorderLayout.CENTER);


        categorizationLeftPanel.add(dietLabelsPanel);
        categorizationLeftPanel.add(Box.createVerticalStrut(30));
        categorizationLeftPanel.add(healthLabelsPanel);


        // ---------------------------
        // --   Right column panel  --
        // ---------------------------

        JPanel categorizationRightPanel = new JPanel();
        categorizationRightPanel.setLayout(new BoxLayout(categorizationRightPanel, BoxLayout.Y_AXIS));
        categorizationRightPanel.setPreferredSize(new Dimension(350, 650));

        // --  Cuisine Types  --

        JPanel cuisineTypesPanel = new JPanel(new BorderLayout());

        // Header
        JPanel cuisineTypesHeader = new JPanel();
        cuisineTypesHeader.setLayout(new BoxLayout(cuisineTypesHeader, BoxLayout.X_AXIS));
        cuisineTypesHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel cuisineTypesTitle = new JLabel("Cuisine Types", SwingConstants.LEFT);
        cuisineTypesTitle.setFont(new Font("Inter", Font.PLAIN, 17));
        cuisineTypesHeader.add(cuisineTypesTitle);

        // List
        String[] cuisineTypesOptions = {"american", "asian", "british", "caribbean", "central europe", "chinese", "eastern europe", "french", "greek", "indian", "italian", "japanese", "korean", "kosher", "mediterranean", "mexican", "middle eastern", "nordic", "south american", "south east asian", "world"};
        cuisineTypesList = new JList<>(cuisineTypesOptions);
        cuisineTypesList.addListSelectionListener(this);
        cuisineTypesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane cuisineTypesPane = new JScrollPane(cuisineTypesList);
        cuisineTypesPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        cuisineTypesPanel.add(cuisineTypesHeader, BorderLayout.NORTH);
        cuisineTypesPanel.add(cuisineTypesPane, BorderLayout.CENTER);

        // --  Meal Types  --

        JPanel mealTypesPanel = new JPanel(new BorderLayout());

        // Header
        JPanel mealTypesHeader = new JPanel();
        mealTypesHeader.setLayout(new BoxLayout(mealTypesHeader, BoxLayout.X_AXIS));
        mealTypesHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel mealTypesTitle = new JLabel("Meal Types", SwingConstants.LEFT);
        mealTypesTitle.setFont(new Font("Inter", Font.PLAIN, 17));
        mealTypesHeader.add(mealTypesTitle);

        // List
        String[] mealTypesOptions = {"breakfast", "brunch", "lunch/dinner", "snack", "teatime"};
        mealTypesList = new JList<>(mealTypesOptions);
        mealTypesList.addListSelectionListener(this);
        mealTypesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane mealTypesPane = new JScrollPane(mealTypesList);
        mealTypesPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        mealTypesPanel.add(mealTypesHeader, BorderLayout.NORTH);
        mealTypesPanel.add(mealTypesPane, BorderLayout.CENTER);

        // --  Dish Types  --
        JPanel dishTypesPanel = new JPanel(new BorderLayout());

        // Header
        JPanel dishTypesHeader = new JPanel();
        dishTypesHeader.setLayout(new BoxLayout(dishTypesHeader, BoxLayout.X_AXIS));
        dishTypesHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel dishTypesTitle = new JLabel("Dish Types", SwingConstants.LEFT);
        dishTypesTitle.setFont(new Font("Inter", Font.PLAIN, 17));
        dishTypesHeader.add(dishTypesTitle);

        // List
        String[] dishTypesOptions = {"alcohol cocktail", "biscuits and cookies", "bread", "cereals", "condiments and sauces", "desserts", "drinks", "egg", "ice cream and custard", "main course", "pancake", "pasta", "pastry", "pies and tarts", "pizza", "preps", "preserve", "salad", "sandwiches", "seafood", "side dish", "soup", "special occasions", "starter", "sweets"};
        dishTypesList = new JList<>(dishTypesOptions);
        dishTypesList.addListSelectionListener(this);
        dishTypesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane dishTypesPane = new JScrollPane(dishTypesList);
        dishTypesPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        dishTypesPanel.add(dishTypesHeader, BorderLayout.NORTH);
        dishTypesPanel.add(dishTypesPane, BorderLayout.CENTER);

        categorizationRightPanel.add(cuisineTypesPanel);
        categorizationRightPanel.add(Box.createVerticalStrut(30));
        categorizationRightPanel.add(mealTypesPanel);
        categorizationRightPanel.add(Box.createVerticalStrut(30));
        categorizationRightPanel.add(dishTypesPanel);


        recipeCategorizationContent.add(categorizationLeftPanel);
        recipeCategorizationContent.add(categorizationRightPanel);


        recipeCategorizationContainer.add(recipeCategorizationHeader, BorderLayout.NORTH);
        recipeCategorizationContainer.add(recipeCategorizationContent, BorderLayout.CENTER);


        // firstPanel.setBackground(Color.YELLOW);
        // secondPanel.setBackground(Color.RED);
        // recipeGroupsRecipesPanel.setBackground(Color.CYAN);
        // recipeCategorizationContainer.setBackground(Color.BLUE);

        secondPanel.add(recipeGroupsRecipesPanel);        
        secondPanel.add(recipeCategorizationContainer);       
        

        addComponent(mainPanel, new JSeparator(), mainGbc, 
        0, 1, 1, 1, GridBagConstraints.CENTER);
        addComponent(mainPanel, secondPanel, mainGbc, 
        0, 2, 1, 1, GridBagConstraints.CENTER);



        // [Other Event Handlers]

        // Handles the event when the contents of the Ingredients List changes
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


        // [Operation to be done when the program is closed]
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closing the program...");

                // Stores the `recipeItems` to the json file
                Gson gson = new GsonBuilder()
                                .serializeNulls()
                                .setPrettyPrinting()
                                .create();
                String json = gson.toJson(recipeItems);
                try {
                    Files.write(Paths.get(dbFileName), json.getBytes());
                } catch (IOException e1) {
                    System.out.println("Error writing data to the json file.");
                    e1.printStackTrace();
                }

                // Then exit the program
                System.exit(0);
            }
        });
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

            // Disables the 'Clear' button if there are no items in the list
            if (ingrListModel.getSize() == 0) {
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

                // Sends the request to the API
                String response = analyzeIngredients(payload);


                Gson gson = new Gson();
                Recipe recipe = gson.fromJson(response, Recipe.class);
                currentRecipe = recipe;
                analyzedCurrentRecipe = true;
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(mainPanel, "Error sending request to the API.\n" + exception.getLocalizedMessage());
                currentRecipe = null;
                analyzedCurrentRecipe = false;
            } finally {
                // Enables back the button
                analyzeRecipeBtn.setEnabled(true);

                // Checks if the response has nutrition data or not.
                // If it doesn't, it may be that the recipe inputted by the user
                // is not valid.
                if (currentRecipe.getCalories() == 0) {
                    JOptionPane.showMessageDialog(mainPanel, "Invalid recipe.\nPlease check the spelling of the ingredients and then try again.");
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

                firstPanel.revalidate();
                firstPanel.repaint();
            }
        } 
        else if (command == "Save Recipe") {
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

                if (recipeTitle != null && recipeTitles.contains(recipeTitle)) {
                    JOptionPane.showMessageDialog(mainPanel, "Title is already used. Please create a new one.");
                    return;
                }
            } while (recipeTitle == null);


            if (recipeGroups.size() > 0) {
                // Asks if the user would like to add the recipe to a group
                int choice = JOptionPane.showConfirmDialog(null, "Would you like to add the recipe to a group?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (choice == JOptionPane.YES_OPTION) {
                    String group = selectGroup("Select the number of the group to which the recipe will be added.", false);

                    // User cancelled the operation
                    if (group == null) {
                        return;
                    }

                    currentRecipe.setGroup(group);
                } else if (choice == JOptionPane.NO_OPTION || choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                    // No group
                    currentRecipe.setGroup(null);
                }
            } else {
                // No group
                currentRecipe.setGroup(null);
            }

            // Add recipe title to existing list of titles
            recipeTitles.add(recipeTitle);

            // Set the inputted title as the title of the recipe
            currentRecipe.setTitle(recipeTitle);

            recipeItems.add(currentRecipe);

            // Add/analyze recipe session is finished.
            // Clears current recipe.
            currentRecipe = null;
            ingrListModel.clear();
            analyzedCurrentRecipe = false;
        }

        if (command == "Add Ingredient" || command == "Remove Ingredient" || command == "Clear Ingredients List" || command == "Analyze Recipe" || command == "Save Recipe") {
            // User can only save a recipe (i.e. the 'Save Recipe' button is enabled) 
            // if the current recipe has been analyzed.
            if (analyzedCurrentRecipe == true) {
                saveRecipeBtn.setEnabled(true);
            } else {
                saveRecipeBtn.setEnabled(false);
            }
        }
    }

    // Handle changes in the selection of items in a JList
    public void valueChanged(ListSelectionEvent e) {
        // Determines which JList triggered the event
        JList<?> sourceList = (JList<?>) e.getSource();

        if (!e.getValueIsAdjusting()) {
            // Ingredient List (Analyze Recipe)
            if (sourceList == ingrList) {
                // Access the selected indices or values from the JList
                // String selectedValue = ingrList.getSelectedValue().toString();

                int selectedIndex = ingrList.getSelectedIndex();
                // Enables the 'Remove' button if there is an item selected
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
}
 