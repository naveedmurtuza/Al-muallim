/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.JComboBox;
import javax.swing.SwingWorker;
import static javax.swing.SwingWorker.StateValue.DONE;
import org.almuallim.theholyquran.api.Language;
import org.almuallim.theholyquran.api.TheHolyQuran;
import org.almuallim.theholyquran.nodes.ChapterCollection;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.almuallim.theholyquran//QuranExplorer//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "QuranExplorerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "explorer", openAtStartup = false)
@ActionID(category = "Window", id = "org.almuallim.theholyquran.QuranExplorerTopComponent")
@ActionReference(path = "Menu/Window", position = 10)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_QuranExplorerAction",
        preferredID = "QuranExplorerTopComponent")
@Messages({
    "CTL_QuranExplorerAction=The Holy Quran",
    "CTL_QuranExplorerTopComponent=The Holy Quran",
    "HINT_QuranExplorerTopComponent=The Holy Quran"
})
public final class QuranExplorerTopComponent extends TopComponent implements ExplorerManager.Provider {

    private BeanTreeView btv;
    private ExplorerManager em = new ExplorerManager();
    private JComboBox<Language> langComboBox = new JComboBox<>();
    private static final Logger LOG = Logger.getLogger(Installer.class.getName());
    private boolean uiLoaded;

    public QuranExplorerTopComponent() {
        initComponents();
        setName(Bundle.CTL_QuranExplorerTopComponent());
        setToolTipText(Bundle.HINT_QuranExplorerTopComponent());
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);
//        putClientProperty(TopComponent.PROP_KEEP_PREFERRED_SIZE_WHEN_SLIDED_IN, Boolean.TRUE);
        btv = new BeanTreeView();
        add(langComboBox, BorderLayout.PAGE_START);
        add(btv, BorderLayout.CENTER);
        btv.setRootVisible(false);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();

        setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setPreferredSize(new java.awt.Dimension(24, 24));
        add(jToolBar1, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    @Override
    protected void componentShowing() {
        //check if component activated
        boolean activated = NbPreferences.forModule(getClass()).getBoolean("activated", false);
        if (!activated) {
            boolean cancelled = ModuleActivation.activate();
            if (!cancelled) {
                
                loadUI();
                try {
                    NbPreferences.forModule(getClass()).flush();
                } catch (BackingStoreException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                close();
            }
        }
        if (!uiLoaded) {
            loadUI();

        }
    }

    private void loadUI() {
        fillLanguages();
        em.setRootContext(new AbstractNode(Children.create(new ChapterCollection(), true)));

        associateLookup(ExplorerUtils.createLookup(em, getActionMap()));

        langComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Language lang = (Language) langComboBox.getSelectedItem();
                    NbPreferences.forModule(getClass()).put("LANGUAGE_CHANGED", lang.isTransliteration() ? lang.getIso2Code() + ":translite" : lang.getIso2Code());
                    NbPreferences.forModule(getClass()).flush();
                } catch (BackingStoreException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
        //select arabic language => index 0
        langComboBox.setSelectedIndex(0);

        uiLoaded = true;
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

    @Override
    public ExplorerManager getExplorerManager() {
        return em;

    }

    private void fillLanguages() {
        List<Language> languages = TheHolyQuran.getInstance().getAvailableLanguagesForChapterNames();
        if (languages != null) {
            for (Language language : languages) {
                langComboBox.addItem(language);
            }
        }
    }

   
}
