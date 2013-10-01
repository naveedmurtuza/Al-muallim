package org.almuallim.wordpad;

import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public final class InputLineEx extends DialogDescriptor {

    /**
     * The text field used to enter the input.
     */
    protected JTextField textField;

    /**
     * Construct dialog with the specified title and label text.
     *
     * @param text label text
     * @param title title of the dialog
     */
//        public InputLineEx(final String text, final String title) {
//            this(text, title, OK_CANCEL_OPTION, PLAIN_MESSAGE);
//        }
//
//        /**
//         * Construct dialog with the specified title, label text, option and
//         * message types.
//         *
//         * @param text label text
//         * @param title title of the dialog
//         * @param optionType option type (ok, cancel, ...)
//         * @param messageType message type (question, ...)
//         */
//        public InputLineEx(final String text, final String title, final int optionType, final int messageType) {
//            super(null, title, optionType, messageType, null, null);
//            super.setMessage(createDesign(text));
//            init();
//        }
    public InputLineEx(String text, String title) {
        this(text, title, true, OK_CANCEL_OPTION, OK_OPTION, DEFAULT_ALIGN, null, null);
    }

    public InputLineEx(String text, String title, boolean isModal, ActionListener bl) {
        this(text, title, isModal, OK_CANCEL_OPTION, OK_OPTION, DEFAULT_ALIGN, null, bl);
    }

    public InputLineEx(String text, String title, boolean isModal, int optionType, Object initialValue, ActionListener bl) {
        this(text, title, isModal, optionType, initialValue, DEFAULT_ALIGN, null, bl);
    }

    public InputLineEx(String text, String title, boolean isModal, int optionType, Object initialValue, int optionsAlign, HelpCtx helpCtx, ActionListener bl) {
        super(null, title, isModal, optionType, initialValue, optionsAlign, helpCtx, bl);
        setMessage(createDesign(text));
        init();
    }

    /**
     * Get the text which the user typed into the input line.
     *
     * @return the text entered by the user
     */
    public String getInputText() {
        return textField.getText();
    }

    /**
     * Set the text on the input line.
     *
     * @param text the new text
     */
    public void setInputText(final String text) {
        textField.setText(text);
        textField.selectAll();
    }

    /**
     * Make a component representing the input line.
     *
     * @param text a label for the input line
     * @return the component
     */
    protected Component createDesign(final String text) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        JLabel textLabel = new JLabel();
        Mnemonics.setLocalizedText(textLabel, text);

        boolean longText = text.length() > 80;
        textField = new JTextField(25);
        textLabel.setLabelFor(textField);

        textField.requestFocus();

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        if (longText) {
            layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                    .addComponent(textLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(32, 32, 32))
                    .addComponent(textField))
                    .addContainerGap()));
        } else {
            layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(textLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(textField, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                    .addContainerGap()));
        }
        if (longText) {
            layout.setVerticalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(textLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        } else {
            layout.setVerticalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(textLabel)
                    .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        }

        javax.swing.KeyStroke enter = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0);
        javax.swing.text.Keymap map = textField.getKeymap();

        map.removeKeyStrokeBinding(enter);

        /*

         textField.addActionListener (new java.awt.event.ActionListener () {
         public void actionPerformed (java.awt.event.ActionEvent evt) {
         System.out.println("action: " + evt);
         InputLine.this.setValue (OK_OPTION);
         }
         }
         );
         */
        panel.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(NotifyDescriptor.class, "ACSD_InputPanel"));
        textField.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(NotifyDescriptor.class, "ACSD_InputField"));

        return panel;
    }

    private void init() {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                firePropertyChange(DOCUMENT_INSERT_UPDATE, null, e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                firePropertyChange(DOCUMENT_REMOVE_UPDATE, null, e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                firePropertyChange(DOCUMENT_CHANGE_UPDATE, null, e);
            }
        });
    }
    private String DOCUMENT_INSERT_UPDATE = "DOCUMENT_INSERT_UPDATE";
    private String DOCUMENT_REMOVE_UPDATE = "DOCUMENT_REMOVE_UPDATE";
    private String DOCUMENT_CHANGE_UPDATE = "DOCUMENT_CHANGE_UPDATE";
}
