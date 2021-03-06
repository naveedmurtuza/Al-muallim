/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public final class AddTranslationVisualPanel1 extends JPanel {

    /**
     * Creates new form AddTranslationVisualPanel1
     */
    public AddTranslationVisualPanel1() {
        initComponents();
        jFileChooser1.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String file = f.getAbsolutePath();
                if (file.endsWith("txt") || file.endsWith("xml") || !f.isFile()) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "Translation Files";
            }
        });
        jFileChooser1.addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChange("PROP_FILE_SELECTED", evt.getOldValue(), evt.getNewValue());
            }
        });
    }

    @Override
    public String getName() {
        return "Select Translation File";
    }

    public File getSelectedFile() {
        return jFileChooser1.getSelectedFile();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();

        setLayout(new java.awt.BorderLayout());

        jFileChooser1.setControlButtonsAreShown(false);
        jFileChooser1.setDialogTitle(org.openide.util.NbBundle.getMessage(AddTranslationVisualPanel1.class, "AddTranslationVisualPanel1.jFileChooser1.dialogTitle")); // NOI18N
        add(jFileChooser1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser jFileChooser1;
    // End of variables declaration//GEN-END:variables
}
