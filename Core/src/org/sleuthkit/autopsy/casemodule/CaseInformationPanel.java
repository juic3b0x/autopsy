/*
 * Autopsy Forensic Browser
 * 
 * Copyright 2011-2016 Basis Technology Corp.
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
package org.sleuthkit.autopsy.casemodule;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import org.openide.util.NbBundle.Messages;

/**
 * Panel for displaying the case information, including both case details and
 * ingest job history.
 */
public class CaseInformationPanel extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(CaseInformationPanel.class.getName());

    /**
     * Creates new form CaseInformationPanel
     */
    public CaseInformationPanel() {
        initComponents();
        customizeComponents();
    }

    @Messages({"CaseInformationPanel.caseDetails.header=Case Details",
        "CaseInformationPanel.ingestJobInfo.header=Ingest History",
        "CaseInformationPanel.loadMetadataFail.message=Failed to load case metadata.",
        "CaseInformationPanel.loadMetadataFail.title=Metadata load failure",})
    private void customizeComponents() {
        try {
            Case currentCase = Case.getCurrentCase();
            String crDate = currentCase.getCreatedDate();
            String caseDir = currentCase.getCaseDirectory();

            // put the image paths information into hashmap
            Map<Long, String> imgPaths = Case.getImagePaths(currentCase.getSleuthkitCase());
            CasePropertiesForm cpf = new CasePropertiesForm(currentCase, crDate, caseDir, imgPaths);
            cpf.setSize(cpf.getPreferredSize());
            this.tabbedPane.addTab(Bundle.CaseInformationPanel_caseDetails_header(), cpf);
            this.tabbedPane.addTab(Bundle.CaseInformationPanel_ingestJobInfo_header(), new IngestJobInfoPanel());
            this.tabbedPane.addChangeListener((ChangeEvent e) -> {
                tabbedPane.getSelectedComponent().setSize(tabbedPane.getSelectedComponent().getPreferredSize());
            });
        } catch (CaseMetadata.CaseMetadataException ex) {
            logger.log(Level.SEVERE, "Failed to load case metadata.", ex);
            JOptionPane.showMessageDialog(null, Bundle.IngestJobInfoPanel_loadIngestJob_error_text(), Bundle.IngestJobInfoPanel_loadIngestJob_error_title(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setSelectedTab(int tab) {
        this.tabbedPane.setSelectedComponent(this.tabbedPane.getComponent(1));
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
        tabbedPane = new javax.swing.JTabbedPane();

        tabbedPane.setPreferredSize(new java.awt.Dimension(420, 200));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
