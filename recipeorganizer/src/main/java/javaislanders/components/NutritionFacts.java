package javaislanders.components;

import java.awt.*;
import javax.swing.*;

import javaislanders.types.Nutrient;
import javaislanders.types.Recipe;

public class NutritionFacts extends JScrollPane {
    public NutritionFacts(Recipe recipe) {
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setBorder(new RoundedBorder(20));

        JPanel nutritionFactsPanel = new JPanel(new GridBagLayout());
        // nutritionFactsPanel.setPreferredSize(new Dimension(325, 600));

        GridBagConstraints nutritionFactsGbc = new GridBagConstraints();
        nutritionFactsGbc.fill = GridBagConstraints.HORIZONTAL;
        nutritionFactsGbc.insets = new Insets(3, 3, 3, 3);

        int gridY = 0;

        JLabel nutritionFactsHeader = new JLabel("Nutrition Facts", SwingConstants.LEFT);
        nutritionFactsHeader.setFont(new Font("Inter", Font.BOLD, 20));
        addComponent(nutritionFactsPanel, nutritionFactsHeader, nutritionFactsGbc, 
        0, gridY, 2, 1, GridBagConstraints.CENTER);

        JSeparator sp1 = new JSeparator();
        sp1.setPreferredSize(new Dimension(300, 10));
        addComponent(nutritionFactsPanel, sp1, nutritionFactsGbc, 
        0, ++gridY, 2, 1, GridBagConstraints.CENTER);

        if (recipe == null || (recipe.getCalories() == 0)) {
            JLabel noData = new JLabel("No Data", SwingConstants.LEFT);
            noData.setFont(new Font("Inter", Font.BOLD, 15));
            addComponent(nutritionFactsPanel, noData, nutritionFactsGbc, 
            0, ++gridY, 2, 1, GridBagConstraints.CENTER);
        } 
        else {   

            JLabel amountPerServing = new JLabel("Amount Per Serving", SwingConstants.LEFT);
            amountPerServing.setFont(new Font("Inter", Font.BOLD, 15));
            addComponent(nutritionFactsPanel, amountPerServing, nutritionFactsGbc, 
            0, ++gridY, 1, 1, GridBagConstraints.CENTER);
            
            JLabel calories = new JLabel("Calories", SwingConstants.LEFT);
            calories.setFont(new Font("Inter", Font.BOLD, 25));
            addComponent(nutritionFactsPanel, calories, nutritionFactsGbc, 
            0, ++gridY, 1, 1, GridBagConstraints.CENTER);

            JLabel caloriesAmount = new JLabel(String.valueOf(recipe.getCalories()), SwingConstants.RIGHT);
            caloriesAmount.setFont(new Font("Inter", Font.BOLD, 25));
            addComponent(nutritionFactsPanel, caloriesAmount, nutritionFactsGbc, 
            1, gridY, 1, 1, GridBagConstraints.CENTER);

            addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
            0, ++gridY, 2, 1, GridBagConstraints.CENTER);
 
            JLabel dailyValue = new JLabel("% Daily Value*", SwingConstants.RIGHT);
            dailyValue.setFont(new Font("Inter", Font.BOLD, 12));
            addComponent(nutritionFactsPanel, dailyValue, nutritionFactsGbc, 
            1, ++gridY, 1, 1, GridBagConstraints.CENTER);  

            addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
            0, ++gridY, 2, 1, GridBagConstraints.CENTER);

            Nutrient FATNutrient = recipe.getTotalNutrients().get("FAT");
            if (FATNutrient != null) {
                JPanel FAT = new JPanel();
                FAT.setLayout(new BoxLayout(FAT, BoxLayout.X_AXIS));
                JLabel FATAmount = new JLabel("Total Fat " + String.format("%.2f", FATNutrient.getQuantity()) + " " + FATNutrient.getUnit());
                FATAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                JLabel FATDaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("FAT").getQuantity()) + " %");
                FATDaily.setFont(new Font("Inter", Font.PLAIN, 12));
                FAT.add(FATAmount);
                FAT.add(Box.createHorizontalGlue());
                FAT.add(FATDaily);
                addComponent(nutritionFactsPanel, FAT, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

                addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
            }

            Nutrient FASATNutrient = recipe.getTotalNutrients().get("FASAT");
            if (FASATNutrient != null) {
                JPanel FASAT = new JPanel();
                FASAT.setLayout(new BoxLayout(FASAT, BoxLayout.X_AXIS));
                JLabel FASATAmount = new JLabel("    Saturated Fat " + String.format("%.2f", FASATNutrient.getQuantity()) + " " + FASATNutrient.getUnit());
                FASATAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                JLabel FASATDaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("FASAT").getQuantity()) + " %");
                FASATDaily.setFont(new Font("Inter", Font.PLAIN, 12));
                FASAT.add(FASATAmount);
                FASAT.add(Box.createHorizontalGlue());
                FASAT.add(FASATDaily);
                addComponent(nutritionFactsPanel, FASAT, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

                addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
            }

            Nutrient FATRNNutrient = recipe.getTotalNutrients().get("FATRN");
            if (FATRNNutrient != null) {
                JPanel FATRN = new JPanel();
                FATRN.setLayout(new BoxLayout(FATRN, BoxLayout.X_AXIS));
                JLabel FATRNAmount = new JLabel("    Trans Fat " + String.format("%.2f", FATRNNutrient.getQuantity()) + " " + FATRNNutrient.getUnit());
                FATRNAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                FATRN.add(FATRNAmount);
                addComponent(nutritionFactsPanel, FATRN, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

                addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
            }

                
            Nutrient CHOLENutrient = recipe.getTotalNutrients().get("CHOLE");
            if (CHOLENutrient != null) {
                JPanel CHOLE = new JPanel();
                CHOLE.setLayout(new BoxLayout(CHOLE, BoxLayout.X_AXIS));
                JLabel CHOLEAmount = new JLabel("Cholesterol " + String.format("%.2f", CHOLENutrient.getQuantity()) + " " + CHOLENutrient.getUnit());
                CHOLEAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                JLabel CHOLEDaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("CHOLE").getQuantity()) + " %");
                CHOLEDaily.setFont(new Font("Inter", Font.PLAIN, 12));
                CHOLE.add(CHOLEAmount);
                CHOLE.add(Box.createHorizontalGlue());
                CHOLE.add(CHOLEDaily);
                addComponent(nutritionFactsPanel, CHOLE, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

                addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
            }

            Nutrient NANutrient = recipe.getTotalNutrients().get("NA");
            if (NANutrient != null) {
                JPanel NA = new JPanel();
                NA.setLayout(new BoxLayout(NA, BoxLayout.X_AXIS));
                JLabel NAAmount = new JLabel("Sodium " + String.format("%.2f", NANutrient.getQuantity()) + " " + NANutrient.getUnit());
                NAAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                JLabel NADaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("NA").getQuantity()) + " %");
                NADaily.setFont(new Font("Inter", Font.PLAIN, 12));
                NA.add(NAAmount);
                NA.add(Box.createHorizontalGlue());
                NA.add(NADaily);
                addComponent(nutritionFactsPanel, NA, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

                addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
            }

            Nutrient CHOCDFNutrient = recipe.getTotalNutrients().get("CHOCDF");
            if (CHOCDFNutrient != null) {
                JPanel CHOCDF = new JPanel();
                CHOCDF.setLayout(new BoxLayout(CHOCDF, BoxLayout.X_AXIS));
                JLabel CHOCDFAmount = new JLabel("Total Carbohydrate " + String.format("%.2f", CHOCDFNutrient.getQuantity()) + " " + CHOCDFNutrient.getUnit());
                CHOCDFAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                JLabel CHOCDFDaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("CHOCDF").getQuantity()) + " %");
                CHOCDFDaily.setFont(new Font("Inter", Font.PLAIN, 12));
                CHOCDF.add(CHOCDFAmount);
                CHOCDF.add(Box.createHorizontalGlue());
                CHOCDF.add(CHOCDFDaily);
                addComponent(nutritionFactsPanel, CHOCDF, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

                addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
            }

            Nutrient FIBTGNutrient = recipe.getTotalNutrients().get("FIBTG");
            if (FIBTGNutrient != null) {
                JPanel FIBTG = new JPanel();
                FIBTG.setLayout(new BoxLayout(FIBTG, BoxLayout.X_AXIS));
                JLabel FIBTGAmount = new JLabel("    Dietary Fiber " + String.format("%.2f", FIBTGNutrient.getQuantity()) + " " + FIBTGNutrient.getUnit());
                FIBTGAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                FIBTG.add(FIBTGAmount);
                addComponent(nutritionFactsPanel, FIBTG, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

                addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
            }

                
            Nutrient SUGARNutrient = recipe.getTotalNutrients().get("SUGAR");
            if (SUGARNutrient != null) {
                JPanel SUGAR = new JPanel();
                SUGAR.setLayout(new BoxLayout(SUGAR, BoxLayout.X_AXIS));
                JLabel SUGARAmount = new JLabel("    Total Sugars " + String.format("%.2f", SUGARNutrient.getQuantity()) + " " + SUGARNutrient.getUnit());
                SUGARAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                SUGAR.add(SUGARAmount);
                addComponent(nutritionFactsPanel, SUGAR, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

                addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
            }

                            
            Nutrient PROCNTNutrient = recipe.getTotalNutrients().get("PROCNT");
            if (PROCNTNutrient != null) {
                JPanel PROCNT = new JPanel();
                PROCNT.setLayout(new BoxLayout(PROCNT, BoxLayout.X_AXIS));
                JLabel PROCNTAmount = new JLabel("Protein " + String.format("%.2f", PROCNTNutrient.getQuantity()) + " " + PROCNTNutrient.getUnit());
                PROCNTAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                JLabel PROCNTDaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("PROCNT").getQuantity()) + " %");
                PROCNTDaily.setFont(new Font("Inter", Font.PLAIN, 12));
                PROCNT.add(PROCNTAmount);
                PROCNT.add(Box.createHorizontalGlue());
                PROCNT.add(PROCNTDaily);
                addComponent(nutritionFactsPanel, PROCNT, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

                addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
            }
            
            Nutrient VITA_RAENutrient = recipe.getTotalNutrients().get("VITA_RAE");
            if (VITA_RAENutrient != null) {
                JPanel VITA_RAE = new JPanel();
                VITA_RAE.setLayout(new BoxLayout(VITA_RAE, BoxLayout.X_AXIS));
                JLabel VITA_RAEAmount = new JLabel("Vitamin A " + String.format("%.2f", VITA_RAENutrient.getQuantity()) + " " + VITA_RAENutrient.getUnit());
                VITA_RAEAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                JLabel VITA_RAEDaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("VITA_RAE").getQuantity()) + " %");
                VITA_RAEDaily.setFont(new Font("Inter", Font.PLAIN, 12));
                VITA_RAE.add(VITA_RAEAmount);
                VITA_RAE.add(Box.createHorizontalGlue());
                VITA_RAE.add(VITA_RAEDaily);
                addComponent(nutritionFactsPanel, VITA_RAE, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

                addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
            }

            Nutrient VITCNutrient = recipe.getTotalNutrients().get("VITC");
            if (VITCNutrient != null) {
                JPanel VITC = new JPanel();
                VITC.setLayout(new BoxLayout(VITC, BoxLayout.X_AXIS));
                JLabel VITCAmount = new JLabel("Vitamin C " + String.format("%.2f", VITCNutrient.getQuantity()) + " " + VITCNutrient.getUnit());
                VITCAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                JLabel VITCDaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("VITC").getQuantity()) + " %");
                VITCDaily.setFont(new Font("Inter", Font.PLAIN, 12));
                VITC.add(VITCAmount);
                VITC.add(Box.createHorizontalGlue());
                VITC.add(VITCDaily);
                addComponent(nutritionFactsPanel, VITC, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

                addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
            }

            Nutrient VITDNutrient = recipe.getTotalNutrients().get("VITD");
            if (VITDNutrient != null) {
                JPanel VITD = new JPanel();
                VITD.setLayout(new BoxLayout(VITD, BoxLayout.X_AXIS));
                JLabel VITDAmount = new JLabel("Vitamin D " + String.format("%.2f", VITDNutrient.getQuantity()) + " " + VITDNutrient.getUnit());
                VITDAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                JLabel VITDDaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("VITD").getQuantity()) + " %");
                VITDDaily.setFont(new Font("Inter", Font.PLAIN, 12));
                VITD.add(VITDAmount);
                VITD.add(Box.createHorizontalGlue());
                VITD.add(VITDDaily);
                addComponent(nutritionFactsPanel, VITD, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

                addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
            }


            Nutrient CANutrient = recipe.getTotalNutrients().get("CA");
            if (CANutrient != null) {
                JPanel CA = new JPanel();
                CA.setLayout(new BoxLayout(CA, BoxLayout.X_AXIS));
                JLabel CAAmount = new JLabel("Calcium " + String.format("%.2f", CANutrient.getQuantity()) + " " + CANutrient.getUnit());
                CAAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                JLabel CADaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("CA").getQuantity()) + " %");
                CADaily.setFont(new Font("Inter", Font.PLAIN, 12));
                CA.add(CAAmount);
                CA.add(Box.createHorizontalGlue());
                CA.add(CADaily);
                addComponent(nutritionFactsPanel, CA, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

                addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
            }

            Nutrient FENutrient = recipe.getTotalNutrients().get("FE");
            if (FENutrient != null) {
                JPanel FE = new JPanel();
                FE.setLayout(new BoxLayout(FE, BoxLayout.X_AXIS));
                JLabel FEAmount = new JLabel("Iron " + String.format("%.2f", FENutrient.getQuantity()) + " " + FENutrient.getUnit());
                FEAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                JLabel FEDaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("FE").getQuantity()) + " %");
                FEDaily.setFont(new Font("Inter", Font.PLAIN, 12));
                FE.add(FEAmount);
                FE.add(Box.createHorizontalGlue());
                FE.add(FEDaily);
                addComponent(nutritionFactsPanel, FE, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

                addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER);
            }

            Nutrient KNutrient = recipe.getTotalNutrients().get("K");
            if (KNutrient != null) {
                JPanel K = new JPanel();
                K.setLayout(new BoxLayout(K, BoxLayout.X_AXIS));
                JLabel KAmount = new JLabel("Potassium " + String.format("%.2f", KNutrient.getQuantity()) + " " + KNutrient.getUnit());
                KAmount.setFont(new Font("Inter", Font.PLAIN, 12));
                JLabel KDaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("K").getQuantity()) + " %");
                KDaily.setFont(new Font("Inter", Font.PLAIN, 12));
                K.add(KAmount);
                K.add(Box.createHorizontalGlue());
                K.add(KDaily);

                addComponent(nutritionFactsPanel, K, nutritionFactsGbc, 
                0, ++gridY, 2, 1, GridBagConstraints.CENTER); 
            }

            // addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
            // 0, ++gridY, 2, 1, GridBagConstraints.CENTER);

            // JPanel FOLDFE = new JPanel();
            // FOLDFE.setLayout(new BoxLayout(FOLDFE, BoxLayout.X_AXIS));
            // Nutrient FOLDFENutrient = recipe.getTotalNutrients().get("FOLDFE");
            // JLabel FOLDFEAmount = new JLabel("Folate " + String.format("%.2f", FOLDFENutrient.getQuantity()) + " " + FOLDFENutrient.getUnit());
            // FOLDFEAmount.setFont(new Font("Inter", Font.PLAIN, 12));
            // JLabel FOLDFEDaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("FOLDFE").getQuantity()) + " %");
            // FOLDFEDaily.setFont(new Font("Inter", Font.PLAIN, 12));
            // FOLDFE.add(FOLDFEAmount);
            // FOLDFE.add(Box.createHorizontalGlue());
            // FOLDFE.add(FOLDFEDaily);
            // addComponent(nutritionFactsPanel, FOLDFE, nutritionFactsGbc, 
            // 0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

            // addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
            // 0, ++gridY, 2, 1, GridBagConstraints.CENTER);

            // JPanel NIA = new JPanel();
            // NIA.setLayout(new BoxLayout(NIA, BoxLayout.X_AXIS));
            // Nutrient NIANutrient = recipe.getTotalNutrients().get("NIA");
            // JLabel NIAAmount = new JLabel("Niacin " + String.format("%.2f", NIANutrient.getQuantity()) + " " + NIANutrient.getUnit());
            // NIAAmount.setFont(new Font("Inter", Font.PLAIN, 12));
            // JLabel NIADaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("NIA").getQuantity()) + " %");
            // NIADaily.setFont(new Font("Inter", Font.PLAIN, 12));
            // NIA.add(NIAAmount);
            // NIA.add(Box.createHorizontalGlue());
            // NIA.add(NIADaily);
            // addComponent(nutritionFactsPanel, NIA, nutritionFactsGbc, 
            // 0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

            // addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
            // 0, ++gridY, 2, 1, GridBagConstraints.CENTER);

            // JPanel RIBF = new JPanel();
            // RIBF.setLayout(new BoxLayout(RIBF, BoxLayout.X_AXIS));
            // Nutrient RIBFNutrient = recipe.getTotalNutrients().get("RIBF");
            // JLabel RIBFAmount = new JLabel("Riboflavin " + String.format("%.2f", RIBFNutrient.getQuantity()) + " " + RIBFNutrient.getUnit());
            // RIBFAmount.setFont(new Font("Inter", Font.PLAIN, 12));
            // JLabel RIBFDaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("RIBF").getQuantity()) + " %");
            // RIBFDaily.setFont(new Font("Inter", Font.PLAIN, 12));
            // RIBF.add(RIBFAmount);
            // RIBF.add(Box.createHorizontalGlue());
            // RIBF.add(RIBFDaily);
            // addComponent(nutritionFactsPanel, RIBF, nutritionFactsGbc, 
            // 0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

            // addComponent(nutritionFactsPanel, new JSeparator(), nutritionFactsGbc, 
            // 0, ++gridY, 2, 1, GridBagConstraints.CENTER);

            // JPanel THIA = new JPanel();
            // THIA.setLayout(new BoxLayout(THIA, BoxLayout.X_AXIS));
            // Nutrient THIANutrient = recipe.getTotalNutrients().get("THIA");
            // JLabel THIAAmount = new JLabel("Thiamin " + String.format("%.2f", THIANutrient.getQuantity()) + " " + THIANutrient.getUnit());
            // THIAAmount.setFont(new Font("Inter", Font.PLAIN, 12));
            // JLabel THIADaily = new JLabel(String.valueOf((int) recipe.getTotalDaily().get("THIA").getQuantity()) + " %");
            // THIADaily.setFont(new Font("Inter", Font.PLAIN, 12));
            // THIA.add(THIAAmount);
            // THIA.add(Box.createHorizontalGlue());
            // THIA.add(THIADaily);
            // addComponent(nutritionFactsPanel, THIA, nutritionFactsGbc, 
            // 0, ++gridY, 2, 1, GridBagConstraints.CENTER); 

            // final Map<String, String[]> nutrientsMap = new LinkedHashMap<String, String[]>() {{
            //     put("FAT", new String[] {
            //         "FASAT", "FATRN"
            //     });
            //     put("CHOLE", new String[0]);
            //     put("NA", new String[0]);
            //     put("CHOCDF",  new String[] {
            //         "FIBTG", "SUGAR"
            //     });
            //     put("PROCNT", new String[0]);
            //     put("VITA_RAE", new String[0]);
            //     put("VITC", new String[0]);
            //     put("VITD", new String[0]);
            //     put("CA", new String[0]);
            //     put("FE", new String[0]);
            //     put("K", new String[0]);
            //     put("FOLDFE", new String[0]);
            //     put("NIA", new String[0]);
            //     put("RIBF", new String[0]);
            //     put("THIA", new String[0]);
            // }};

        }

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
