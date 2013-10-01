package org.almuallim.theholyquran.browseraddin.fontstyle;

import com.google.common.base.Strings;
import com.osbcp.cssparser.PropertyValue;
import com.osbcp.cssparser.Rule;
import com.osbcp.cssparser.Rules;
import com.osbcp.cssparser.Selector;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import javax.swing.SwingWorker;
import org.almuallim.service.database.Database;
import org.almuallim.service.helpers.LanguageUtils;
import org.almuallim.theholyquran.api.Translator;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Naveed
 */
public class FontStylePanel extends javax.swing.JPanel {

    private final JFontChooser pe;
    private List<Rule> rules;
    private Rule currentRule;
    private boolean styleChanged;

    /**
     * Creates new form FontStylePanel
     */
    public FontStylePanel(List<Rule> ruless) {
        initComponents();
        this.rules = ruless;
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
                Translator translator = (Translator) e.getItem();
                String langCode = translator.getLanguage().getIso2Code();

                switch (e.getStateChange()) {
                    case ItemEvent.SELECTED:
                        //update checkbox label
                        jCheckBox1.setText(String.format("Use this as a default style for %s", LanguageUtils.getLanguageName(langCode)));
                        Selector translatorSelector = new Selector(MessageFormat.format("p[data-translator-id=\"{0}\"]", translator.getId()));
                        Selector langSelector = new Selector(MessageFormat.format("p[data-language=\"{0}\"]", translator.getId()));
                        for (Rule rule : rules) {
                            if (rule.getSelectors().contains(translatorSelector)) {
                                currentRule = rule;
                                Set<PropertyValue> propertyValues = rule.getPropertyValues();
                                String fontFamily = "", foreground, background, fontStyle = "", fontWeight = "";
                                int size = 0;
                                for (PropertyValue propertyValue : propertyValues) {
                                    switch (propertyValue.getProperty()) {
                                        //font: [font-stretch] [font-style] [font-variant] [font-weight] [font-size]/[line-height] [font-family];
                                        case "font-family":
                                            fontFamily = propertyValue.getValue();
                                            break;
                                        case "font-size":
                                            String value = propertyValue.getValue();
                                            size = Integer.parseInt(value.substring(0, value.length() - 2));
                                            break;
                                        case "font-style":
                                            fontStyle = propertyValue.getValue();
                                            break;
                                        case "font-weight":
                                            fontWeight = propertyValue.getValue();
                                            break;
                                        case "foreground":
                                            foreground = propertyValue.getValue();
                                            break;
                                        case "background":
                                            background = propertyValue.getValue();
                                            break;
                                    }
                                }
                                Font f = new Font(fontFamily, toJavaStyle(fontStyle, fontWeight), size);
                                pe.setSelectedFont(f);
                            }
                            jCheckBox1.setSelected(rule.getSelectors().contains(langSelector));
                            break;
                        }
                        break;
                    case ItemEvent.DESELECTED:
                        if (currentRule != null && styleChanged) {
                        Rules.addOrUpdate(rules, currentRule);
                        styleChanged = false;
                    }
//                        
                        break;
                }
            }

        });

        SwingWorker<Void, Translator> worker = new SwingWorker<Void, Translator>() {

            @Override
            protected Void doInBackground() throws Exception {
                Database database = Lookup.getDefault().lookup(Database.class);
                try (Connection connection = database.getConnection()) {
                    List<Translator> all = Translator.all(connection);
                    for (Translator translator : all) {
                        //ignore the verbatim
                        if (translator.getId() == 1) {
                            continue;
                        }
                        publish(translator);
                    }
                } catch (SQLException ex) {
                    Exceptions.printStackTrace(ex);
                }
                return null;
            }

            @Override
            protected void process(List<Translator> chunks) {
                for (Translator translator : chunks) {
                    jComboBox1.addItem(translator);
                }
            }

        };
        worker.execute();

        pe.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equalsIgnoreCase("FONT_STYLE_CHANGE")) {
                    if (currentRule == null) {
                        Translator translator = (Translator) jComboBox1.getSelectedItem();
                        Selector translatorSelector = new Selector(MessageFormat.format("p[data-translator-id=\"{0}\"]", translator.getId()));
                        currentRule = new Rule(translatorSelector);
                    }
                    Font f = (Font) pe.getSelectedFont();
                    currentRule.addPropertyValue(new PropertyValue("font-family", f.getFontName()));
                    currentRule.addPropertyValue(new PropertyValue("font-size", "" + f.getSize() + "px"));
                    String css = toCssFontStyle(f.getStyle());
                    for (String prop : css.split(";")) {
                        String[] s = prop.split(":");
                        currentRule.addPropertyValue(new PropertyValue(s[0], s[1]));
                    }
                    styleChanged = true;
                }
            }
        });
        jCheckBox1.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                Translator translator = (Translator) jComboBox1.getSelectedItem();
                Selector langSelector = new Selector(MessageFormat.format("p[data-language=\"{0}\"]", translator.getId()));
                if (currentRule == null) {
                    currentRule = new Rule();
                }

                if (jCheckBox1.isSelected()) {
                    currentRule.addSelector(langSelector);
                } else {
                    currentRule.removeSelector(langSelector);
                }
                styleChanged = true;
            }
        });

    }

    private String toCssFontStyle(int style) {
        switch (style) {
            case Font.BOLD:
                return "font-weight:bold";
            case Font.PLAIN:
                return "font-style:normal";
            case Font.ITALIC:
                return "font-style:italic";
            case Font.BOLD | Font.ITALIC:
                return "font-weight:bold;font-style:italic";
        }
        return "";
    }

    private int toJavaStyle(String fontStyle, String fontWeight) {
        int style = Font.PLAIN;
        if (!Strings.isNullOrEmpty(fontWeight)) {
            style |= Font.BOLD;
        }
        if (!Strings.isNullOrEmpty(fontStyle)) {
            if ("italic".equalsIgnoreCase(fontStyle)) {
                style |= Font.ITALIC;
            }
        }
        return style;

    }

    public List<Rule> getRules() {
        if (currentRule != null && styleChanged) {
            Rules.addOrUpdate(rules, currentRule);
            styleChanged = false;
        }
        return rules;
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


}
