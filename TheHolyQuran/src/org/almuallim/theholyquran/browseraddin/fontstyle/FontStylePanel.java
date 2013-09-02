/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.browseraddin.fontstyle;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.almuallim.service.database.Database;
import org.almuallim.service.helpers.LanguageUtils;
import org.almuallim.theholyquran.api.Translator;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

/**
 * Note: The color/background-color is disable as of now. The logic for saving the style
 * is not perfect. lots of shortcomings. one being if  press ok without changing anything 
 * the current style will saved!  hope i was clear. Instead we need a mechanism to save the style
 * only if it is changed!!! Since this is just cosmetic decided to disable it for now.
 * 
 * @author Naveed
 */
public class FontStylePanel extends javax.swing.JPanel {

    private final Preferences translatorStyles;
    private final Preferences defaultStyles;
    private final JFontChooser pe;

    /**
     * Creates new form FontStylePanel
     */
    public FontStylePanel() {
        initComponents();
        translatorStyles = NbPreferences.forModule(getClass()).node("styles/translator");
        defaultStyles = NbPreferences.forModule(getClass()).node("styles/default");
        pe = new JFontChooser();
        add(pe, BorderLayout.CENTER);
        ColorComboBox.init(cbForeground);
        ColorComboBox.init(cbBackground);
        cbBackground.setVisible(false);
        cbForeground.setVisible(false);
        lBackground.setVisible(false);
        lForeground.setVisible(false);
        jComboBox1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Translator item = (Translator) e.getItem();
                Preferences childTranslator = translatorStyles.node("" + item.getId());
                String langCode = item.getLanguage().getIso2Code();
                switch (e.getStateChange()) {
                    case ItemEvent.SELECTED:
                        //update checkbox label
                        jCheckBox1.setText(String.format("Use this as a default style for %s", LanguageUtils.getLanguageName(langCode)));
                        //load the values
                        String fontFamily = childTranslator.get("font-family", "");
                        if (!fontFamily.isEmpty()) {
                            int size = childTranslator.getInt("font-size", 12);
                            int style = childTranslator.getInt("font-style", Font.PLAIN);
                            Font f = new Font(fontFamily, style, size);
                            pe.setSelectedFont(f);
                        }
                        /*String foreground = childTranslator.get("color", "");
                        if (!foreground.isEmpty()) {
                            ColorComboBox.setColor(cbForeground, ColorUtils.fromHexFormat(foreground));
                        }
                        String background = childTranslator.get("background-color", "");
                        if (!background.isEmpty()) {
                            ColorComboBox.setColor(cbBackground, ColorUtils.fromHexFormat(background));
                        }*/
                        try {
                            //look if we have default for this
                            jCheckBox1.setSelected(java.util.Arrays.asList(defaultStyles.childrenNames()).contains(langCode));
                        } catch (BackingStoreException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        break;
                    case ItemEvent.DESELECTED:
                        //save the values
                        Font f = (Font) pe.getSelectedFont();
                        if (f == null) {
                            return;
                        }
                        if (!jCheckBox1.isSelected()) {
                            try {
                                //so lets see if there is any default for this
                                //if yes
                                //delete the node
                                if (java.util.Arrays.asList(defaultStyles.childrenNames()).contains(langCode)) {
                                    defaultStyles.node(langCode).clear();
                                }
                            } catch (BackingStoreException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                        saveStyle(childTranslator, f, item.getLanguage().getIso2Code());
                        break;
                }
            }
        });
    }

    private void saveStyle(Preferences childTranslator, Font f, String defaultNode) {
        childTranslator.put("font-family", f.getFontName());
        childTranslator.putInt("font-size", f.getSize());
        childTranslator.putInt("font-style", f.getStyle());
//        childTranslator.put("color", ColorUtils.toHexFormat(ColorComboBox.getColor(cbForeground)));
//        childTranslator.put("background-color", ColorUtils.toHexFormat(ColorComboBox.getColor(cbBackground)));
        if (jCheckBox1.isSelected()) {
            Preferences childDefault = defaultStyles.node(defaultNode);
            childDefault.put("font-family", f.getFontName());
            childDefault.putInt("font-size", f.getSize());
            childDefault.putInt("font-style", f.getStyle());
//            childTranslator.put("color", ColorUtils.toHexFormat(ColorComboBox.getColor(cbForeground)));
//            childTranslator.put("background-color", ColorUtils.toHexFormat(ColorComboBox.getColor(cbBackground)));

        }
    }

    public void save() {

        //save the current style
        Translator item = (Translator) jComboBox1.getSelectedItem();
        Preferences childTranslator = translatorStyles.node("" + item.getId());
        Font f = (Font) pe.getSelectedFont();
        if (f == null) {
            return;
        }
        saveStyle(childTranslator, f, item.getLanguage().getIso2Code());
        try {
            translatorStyles.flush();
            defaultStyles.flush();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        lForeground = new javax.swing.JLabel();
        cbForeground = new javax.swing.JComboBox();
        lBackground = new javax.swing.JLabel();
        cbBackground = new javax.swing.JComboBox();
        jCheckBox1 = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(FontStylePanel.class, "FontStylePanel.jLabel1.text")); // NOI18N
        jPanel1.add(jLabel1);

        jComboBox1.setPreferredSize(new java.awt.Dimension(200, 20));
        jPanel1.add(jComboBox1);

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        org.openide.awt.Mnemonics.setLocalizedText(lForeground, org.openide.util.NbBundle.getMessage(FontStylePanel.class, "FontStylePanel.lForeground.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lBackground, org.openide.util.NbBundle.getMessage(FontStylePanel.class, "FontStylePanel.lBackground.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox1, org.openide.util.NbBundle.getMessage(FontStylePanel.class, "FontStylePanel.jCheckBox1.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lForeground)
                            .addComponent(lBackground))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbForeground, 0, 195, Short.MAX_VALUE)
                            .addComponent(cbBackground, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(130, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lForeground)
                    .addComponent(cbForeground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lBackground)
                    .addComponent(cbBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(jCheckBox1)
                .addContainerGap())
        );

        add(jPanel2, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbBackground;
    private javax.swing.JComboBox cbForeground;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lBackground;
    private javax.swing.JLabel lForeground;
    // End of variables declaration//GEN-END:variables

    public void init() {
        Database database = Lookup.getDefault().lookup(Database.class);
        try (Connection connection = database.getConnection()) {
            List<Translator> all = Translator.all(connection);
            for (Translator translator : all) {
                //ignore the verbatim
                if (translator.getId() == 1) {
                    continue;
                }
                jComboBox1.addItem(translator);
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
