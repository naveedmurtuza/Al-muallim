/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.browseraddin.translation;

import com.jidesoft.swing.CheckBoxTree;
import com.jidesoft.swing.StyledLabelBuilder;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.almuallim.service.database.Database;
import org.almuallim.theholyquran.api.Language;
import org.almuallim.theholyquran.api.Translator;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Naveed
 */
public class TranslatorsPanel extends javax.swing.JPanel {

    private CheckBoxTree checkBoxTree;
    private JScrollPane scrollPane = new JScrollPane();

    /**
     * Creates new form TranslatorsPanel
     */
    public TranslatorsPanel() {
        initComponents();
        checkBoxTree = new CheckBoxTree() {
            @Override
            public Dimension getPreferredScrollableViewportSize() {
                return new Dimension(400, 400);
            }
        };
        checkBoxTree.setRootVisible(false);
        checkBoxTree.setDigIn(true);
        checkBoxTree.setShowsRootHandles(true);
//        checkBoxTree.getCheckBoxTreeSelectionModel().setDigIn(true);
        checkBoxTree.getCheckBoxTreeSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        scrollPane.setViewportView(checkBoxTree);
//        add(scrollPane, BorderLayout.CENTER);
        //
        // Change the default JTree icons

        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) checkBoxTree.getActualCellRenderer();
        Icon closedIcon = new ImageIcon("transparent.png");
        Icon openIcon = new ImageIcon("transparent.png");
        Icon leafIcon = new ImageIcon("transparent.png");
        renderer.setClosedIcon(closedIcon);
        renderer.setOpenIcon(openIcon);
        renderer.setLeafIcon(leafIcon);
        //
//        checkBoxTree.getCheckBoxTreeSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
//            @Override
//            public void valueChanged(TreeSelectionEvent e) {
//                TreePath[] paths = e.getPaths();
//                for (TreePath path : paths) {
//                    System.out.println((e.isAddedPath(path) ? "Added - " : "Removed - ") + path);
//                }
//            }
//        });
        //add a loading label
//        add(new JLabel("Loading ..."), BorderLayout.PAGE_START);
    }

    /**
     * for some reason TreePath.getLastPathComponent is not returning the last
     * node in the path in JCheckboxTree. Though working fine if Jtree. Probably
     * missing something here.. but as of now simple function to return the last
     * node as the max depth for this tree is 1.
     *
     * @param path
     * @return
     */
    private DefaultMutableTreeNode getLastChild(TreePath path) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        DefaultMutableTreeNode child = node;

        if (node.getUserObject() instanceof Language) {
            child = (DefaultMutableTreeNode) node.getChildAt(0);
        }
        return child;
    }

    private ArrayList<DefaultMutableTreeNode> getAllLeafNodes() {
        ArrayList<DefaultMutableTreeNode> nodes = new ArrayList<>();
        DefaultTreeModel model = (DefaultTreeModel) checkBoxTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        Enumeration<?> enumer = root.postorderEnumeration();
        while (enumer.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumer.nextElement();
            if (node.isLeaf()) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    public Integer[] getSelectedIds() {
//        getAllLeafNodes();
//        TreePath[] selectionPaths = checkBoxTree.getSelectionModel().getSelectionPaths();
        TreePath[] selectionPaths = checkBoxTree.getCheckBoxTreeSelectionModel().getSelectionPaths();
        ArrayList<Integer> selectedIds = new ArrayList<>();
//        Integer[] selectedIds = new Integer[selectionPaths.length];
        for (int i = 0; i < selectionPaths.length; i++) {
            DefaultMutableTreeNode lastChild = (DefaultMutableTreeNode) selectionPaths[i].getLastPathComponent();
            if (lastChild.isRoot()) {
                //this means all nodes are checked!
                //get all leaf nodes
                ArrayList<DefaultMutableTreeNode> nodes = getAllLeafNodes();
                for (DefaultMutableTreeNode node : nodes) {
                    Translator t = (Translator) node.getUserObject();
                    selectedIds.add(t.getId());
                }
                break;
            } else {
                Object userObject = lastChild.getUserObject();
                if (userObject instanceof Language) {
                    //if lang that means all children are selected
                    Enumeration e = lastChild.children();
                    while (e.hasMoreElements()) {
                        DefaultMutableTreeNode object = (DefaultMutableTreeNode) e.nextElement();
                        selectedIds.add(((Translator)object.getUserObject()).getId());
                    }
                } else {
                    //instanceof Translator
                    selectedIds.add(((Translator)userObject).getId());
                }
//                selectedIds.add(((Translator) getLastChild(selectionPaths[i]).getUserObject()).getId());
            }
        }
        return selectedIds.toArray(new Integer[selectedIds.size()]);
    }

    public void buildTree(final Set<Integer> selectedIds) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final TreePath[] selection = new TreePath[selectedIds.size()];
                int index = 0;
                Database database = Lookup.getDefault().lookup(Database.class);
                final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Languages");
                try (Connection connection = database.getConnection()) {
                    List<Language> languages = Language.all(connection);
                    int row = 0;
                    for (Language language : languages) {
                        //ignore id 1 => arabic-verbatim
                        if (language.getId() == 1) {
                            continue;
                        }
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(language);
                        root.add(node);

                        List<Translator> translators = Translator.forLanguage(connection, language);
                        for (Translator translator : translators) {
                            DefaultMutableTreeNode child = new DefaultMutableTreeNode(translator);
                            node.add(child);
                            if (selectedIds.contains(translator.getId())) {
                                TreePath path = new TreePath(child.getPath());
                                selection[index++] = path;
                            }

                        }
                        if (node.isLeaf()) {
                            root.remove(node);
                        }
                    }
                } catch (SQLException ex) {
                    Exceptions.printStackTrace(ex);
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!root.isLeaf()) {
                            add(scrollPane, BorderLayout.CENTER);
                            checkBoxTree.setModel(new DefaultTreeModel(root));
                            checkBoxTree.getCheckBoxTreeSelectionModel().addSelectionPaths(selection);
//                            checkBoxTree.setSelectionPaths(selection);
                        } else {
                            add(StyledLabelBuilder.createStyledLabel("No translations has been added yet. To download transaltion navigate to {www.tanzil.org/trans  : f:blue} .And use the add translation wizard (Tools | The Holy Quran | Add Translation) to add the translation. @rows:3:5:5"), BorderLayout.CENTER);
                            setSize(400, 500);
                            setPreferredSize(new Dimension(400, 500));
                        }
//                        checkBoxTree.getCheckBoxTreeSelectionModel().setSelectionPaths(selection);
//                        setLayout(new BorderLayout());

                    }
                });
            }
        }).start();
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
        jLabel2 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(400, 400));
        setPreferredSize(new java.awt.Dimension(400, 400));
        setLayout(new java.awt.BorderLayout(0, 15));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(TranslatorsPanel.class, "TranslatorsPanel.jLabel1.text")); // NOI18N
        jPanel1.add(jLabel1);

        jLabel2.setForeground(new java.awt.Color(0, 0, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(TranslatorsPanel.class, "TranslatorsPanel.jLabel2.text")); // NOI18N
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel2);

        add(jPanel1, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI("http://www.tanzil.net/trans/"));
            } catch (IOException | URISyntaxException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_jLabel2MouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
