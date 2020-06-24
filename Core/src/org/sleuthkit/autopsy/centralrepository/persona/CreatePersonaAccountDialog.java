/*
 * Central Repository
 *
 * Copyright 2020 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.centralrepository.persona;

import java.awt.Component;
import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Level;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;
import org.sleuthkit.autopsy.centralrepository.datamodel.CentralRepoAccount;
import org.sleuthkit.autopsy.centralrepository.datamodel.CentralRepoAccount.CentralRepoAccountType;
import org.sleuthkit.autopsy.centralrepository.datamodel.CentralRepoException;
import org.sleuthkit.autopsy.centralrepository.datamodel.CentralRepository;
import org.sleuthkit.autopsy.coreutils.Logger;

/**
 * Configuration dialog for creating an account.
 */
@SuppressWarnings("PMD.SingularField") // UI widgets cause lots of false positives
public class CreatePersonaAccountDialog extends JDialog {

    private static final Logger logger = Logger.getLogger(CreatePersonaAccountDialog.class.getName());

    private static final long serialVersionUID = 1L;

    private final TypeChoiceRenderer TYPE_CHOICE_RENDERER = new TypeChoiceRenderer();

    /**
     * Creates new create account dialog.
     */
    @Messages({"CreatePersonaAccountDialog.title.text=Create Account",})
    public CreatePersonaAccountDialog(PersonaDetailsPanel pdp) {
        super(SwingUtilities.windowForComponent(pdp),
                Bundle.PersonaAccountDialog_title_text(),
                ModalityType.APPLICATION_MODAL);

        initComponents();
        typeComboBox.setRenderer(TYPE_CHOICE_RENDERER);
        display();
    }

    /**
     * This class handles displaying and rendering drop down menu for account
     * choices.
     */
    private class TypeChoiceRenderer extends JLabel implements ListCellRenderer<CentralRepoAccountType>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(
                JList<? extends CentralRepoAccountType> list, CentralRepoAccountType value,
                int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.getAcctType().getDisplayName());
            return this;
        }
    }

    private CentralRepoAccountType[] getAllAccountTypes() {
        Collection<CentralRepoAccountType> allAccountTypes;
        try {
            allAccountTypes = CentralRepository.getInstance().getAllAccountTypes();
        } catch (CentralRepoException e) {
            logger.log(Level.SEVERE, "Failed to access central repository", e);
            JOptionPane.showMessageDialog(this,
                    Bundle.PersonaAccountDialog_get_types_exception_Title(),
                    Bundle.PersonaAccountDialog_get_types_exception_msg(),
                    JOptionPane.ERROR_MESSAGE);
            return new CentralRepoAccountType[0];
        }
        return allAccountTypes.toArray(new CentralRepoAccountType[0]);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        settingsPanel = new javax.swing.JPanel();
        identiferLbl = new javax.swing.JLabel();
        identifierTextField = new javax.swing.JTextField();
        typeLbl = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox<>();
        cancelBtn = new javax.swing.JButton();
        okBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        settingsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.openide.awt.Mnemonics.setLocalizedText(identiferLbl, org.openide.util.NbBundle.getMessage(CreatePersonaAccountDialog.class, "CreatePersonaAccountDialog.identiferLbl.text")); // NOI18N

        identifierTextField.setText(org.openide.util.NbBundle.getMessage(CreatePersonaAccountDialog.class, "CreatePersonaAccountDialog.identifierTextField.text")); // NOI18N
        identifierTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                identifierTextFieldActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(typeLbl, org.openide.util.NbBundle.getMessage(CreatePersonaAccountDialog.class, "CreatePersonaAccountDialog.typeLbl.text")); // NOI18N

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(getAllAccountTypes()));

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addComponent(typeLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(typeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addComponent(identiferLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(identifierTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)))
                .addContainerGap())
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(identiferLbl)
                    .addComponent(identifierTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(cancelBtn, org.openide.util.NbBundle.getMessage(CreatePersonaAccountDialog.class, "CreatePersonaAccountDialog.cancelBtn.text")); // NOI18N
        cancelBtn.setMaximumSize(new java.awt.Dimension(79, 23));
        cancelBtn.setMinimumSize(new java.awt.Dimension(79, 23));
        cancelBtn.setPreferredSize(new java.awt.Dimension(79, 23));
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(okBtn, org.openide.util.NbBundle.getMessage(CreatePersonaAccountDialog.class, "CreatePersonaAccountDialog.okBtn.text")); // NOI18N
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(194, Short.MAX_VALUE)
                .addComponent(okBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(settingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelBtn, okBtn});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(settingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okBtn)
                    .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void display() {
        this.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
        setVisible(true);
    }
    
    private CentralRepoAccount createAccount(CentralRepoAccount.CentralRepoAccountType type, String identifier) {
        CentralRepoAccount ret = null;
        try {
            CentralRepository cr = CentralRepository.getInstance();
            if (cr != null) {
                ret = cr.getOrCreateAccount(type, identifier);
            }
        } catch (CentralRepoException e) {
            logger.log(Level.SEVERE, "Failed to access central repository", e);
            JOptionPane.showMessageDialog(this,
                    Bundle.PersonaAccountDialog_get_types_exception_Title(),
                    Bundle.PersonaAccountDialog_get_types_exception_msg(),
                    JOptionPane.ERROR_MESSAGE);
        }
        return ret;
    }

    @Messages({
        "CreatePersonaAccountDialog_dup_Title=Account creation failure",
        "CreatePersonaAccountDialog_dup_msg=An account with this identifier and type already exists.",})
    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        if (identifierTextField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    Bundle.PersonaAccountDialog_identifier_empty_msg(),
                    Bundle.PersonaAccountDialog_identifier_empty_Title(),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        CentralRepoAccount.CentralRepoAccountType type = 
                (CentralRepoAccount.CentralRepoAccountType) typeComboBox.getSelectedItem();
        String identifier = identifierTextField.getText();

        if (createAccount(type, identifier) != null) {
            dispose();
        }
    }//GEN-LAST:event_okBtnActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        dispose();
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void identifierTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_identifierTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_identifierTextFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelBtn;
    private javax.swing.JLabel identiferLbl;
    private javax.swing.JTextField identifierTextField;
    private javax.swing.JButton okBtn;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JComboBox<org.sleuthkit.autopsy.centralrepository.datamodel.CentralRepoAccount.CentralRepoAccountType> typeComboBox;
    private javax.swing.JLabel typeLbl;
    // End of variables declaration//GEN-END:variables
}
