/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.wordpad;

import com.google.common.base.Strings;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.atlanticbb.tantlinger.io.IOUtils;
import net.atlanticbb.tantlinger.shef.HTMLEditorPane;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.almuallim.wordpad//WordPad//EN",
        autostore = false
        )
@TopComponent.Description(
        preferredID = "WordPadTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
        )
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "org.almuallim.wordpad.WordPadTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_WordPadAction",
        preferredID = "WordPadTopComponent"
        )
@Messages({
    "CTL_WordPadAction=Word Pad",
    "CTL_WordPadTopComponent=Word Pad",
    "HINT_WordPadTopComponent=Word Pad"
})
public final class WordPadTopComponent extends TopComponent {

    private static final long serialVersionUID = 1L;
    private String defaultLocation = System.getProperty("netbeans.user") + File.separatorChar + "Notes" + File.separatorChar;
    private String title;
    private File fileLocation;
    private HTMLEditorPane editor = new HTMLEditorPane();

    public WordPadTopComponent() {
        initComponents();
        setName(Bundle.CTL_WordPadTopComponent());
        setToolTipText(Bundle.HINT_WordPadTopComponent());
        setLayout(new BorderLayout());
        add(editor, BorderLayout.CENTER);
        editor.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String propName = evt.getPropertyName();
                switch (propName) {
                    case "NEW":
                        System.out.println("NEW");
                        newDocument();
                        break;
                    case "OPEN":
                        System.out.println("OPEN");
                        openDocument();
                        break;
                    case "SAVE":
                        System.out.println("SAVE");
                        saveDocument();
                        break;
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    private void openDocument() {
        if (saveDocument()) {
            clearDocument();
            title = null;
            fileLocation = null;
            JFileChooser jfc = new JFileChooser(defaultLocation);
            int res = jfc.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                fileLocation = jfc.getSelectedFile();
                title = IOUtils.getName(fileLocation);
                try {
                    editor.setText(IOUtils.read(fileLocation));
                } catch (FileNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

    }

    private boolean saveDocument() {
        if (requireSave()) {
            boolean docSaved = false;
            if (Strings.isNullOrEmpty(title)) {
                docSaved = saveNewDocument();
            } else {
                try {
                    IOUtils.write(new File(defaultLocation, title), editor.getText());
                    docSaved = true;
                } catch (FileNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return docSaved;
        }
        return true;
    }

    private boolean saveNewDocument() {
        //get the title
//        DialogDescriptor.InputLine
        final InputLineEx dd = new InputLineEx("text", "title");
        dd.setModal(true);
        final NotificationLineSupport nls = dd.createNotificationLineSupport();
        nls.setInformationMessage("Title cannot be empty");
        //add the button listener
        dd.setButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("Cancel".equals(e.getActionCommand())) {
                    dd.setClosingOptions(null);
                    return;
                }
                if (dd.getInputText().isEmpty()) {
                    nls.setInformationMessage("Title cannot be empty");
                    return;
                }
                boolean fileAlreadyExists = new File(defaultLocation + File.separatorChar + dd.getInputText()).exists();
                if (fileAlreadyExists) {
                    nls.setErrorMessage("File already exists...Aisa mat karo bhaisaab");
                } else {
                    dd.setClosingOptions(null);
                }
                System.out.println(e);

            }
        });
        dd.setClosingOptions(new Object[]{});
        //propert change for the textbox
        dd.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case "DOCUMENT_CHANGE_UPDATE":
                    case "DOCUMENT_INSERT_UPDATE":
                    case "DOCUMENT_REMOVE_UPDATE":
                        if (dd.getInputText().isEmpty()) {
                        nls.setInformationMessage("Title cannot be empty");
                        return;
                    }
                        boolean fileAlreadyExists = new File(defaultLocation + File.separatorChar + dd.getInputText()).exists();
                        if (fileAlreadyExists) {
                            nls.setErrorMessage("File already exists...Aisa mat karo bhaisaab");
                        } else {
                            nls.clearMessages();
                        }
                        break;
                }
            }
        });

        Object retval = DialogDisplayer.getDefault().notify(dd);

        if (retval == DialogDescriptor.CANCEL_OPTION) {
            return false;
        }
        //commit the note
        try {
            IOUtils.write(new File(defaultLocation, dd.getInputText()), editor.getText());
            this.title = dd.getInputText();
            clearDocument();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    private void newDocument() {
        if (requireSave()) {
            int res = JOptionPane.showConfirmDialog(editor, "Save?", "Save?", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.NO_OPTION) {
                clearDocument();
                title = null;
                fileLocation = null;
                return;
            }
            boolean docSaved = saveDocument();
            if (!docSaved) {
                return;
            }
        }
        title = null;
        fileLocation = null;
        clearDocument();

    }

    private void clearDocument() {
        editor.setText("<p>         </p>");
    }

    private boolean requireSave() {
        //oh please oh please find a regex!
        String txt = editor.getText();
        txt = txt.replace(" ", "");
        txt = txt.replace("\n", "");
        txt = txt.replace("\r", "");
        if (txt.equalsIgnoreCase("<p></p>") || txt.isEmpty()) {
            return false;
        } else {
            return true;
        }
//        return !editor.getText().isEmpty();
    }
}