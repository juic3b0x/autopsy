/*
 * Autopsy Forensic Browser
 * 
 * Copyright 2011-2013 Basis Technology Corp.
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

package org.sleuthkit.autopsy.corecomponents;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.coreutils.Logger;
import javax.swing.JMenuItem;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.contentviewers.Utilities;
import org.sleuthkit.autopsy.corecomponentinterfaces.DataContentViewer;
import org.sleuthkit.autopsy.datamodel.ArtifactStringContent;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.BlackboardAttribute;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.Content;
import org.sleuthkit.datamodel.TskException;
import org.sleuthkit.datamodel.TskCoreException;

/**
 * Instances of this class display the BlackboardArtifacts associated with the Content represented by a Node.
 * Each BlackboardArtifact is rendered as an HTML representation of its BlackboardAttributes.
 */
@ServiceProvider(service = DataContentViewer.class, position=3)
public class DataContentViewerArtifact extends javax.swing.JPanel implements DataContentViewer{
    
    private final static Logger logger = Logger.getLogger(DataContentViewerArtifact.class.getName());
    private final static String WAIT_TEXT = NbBundle.getMessage(DataContentViewerArtifact.class, "DataContentViewerArtifact.waitText");
    private final static String ERROR_TEXT = NbBundle.getMessage(DataContentViewerArtifact.class, "DataContentViewerArtifact.errorText");
    private Node currentNode; // @@@ Remove this when the redundant setNode() calls problem is fixed. 
    private int currentPage = 1;
    private final Object lock = new Object();
    private List<ArtifactStringContent> artifactContentStrings; // Accessed by multiple threads, use getArtifactContentStrings() and setArtifactContentStrings()
    SwingWorker<ViewUpdate, Void> currentTask; // Accessed by multiple threads, use startNewTask()
    
    public DataContentViewerArtifact() {
        initComponents();
        customizeComponents();
        resetComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rightClickMenu = new javax.swing.JPopupMenu();
        copyMenuItem = new javax.swing.JMenuItem();
        selectAllMenuItem = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        outputViewPane = new JTextPane(){
            public boolean getScrollableTracksViewportWidth() {
                return (getSize().width < 400);
            }};
            totalPageLabel = new javax.swing.JLabel();
            ofLabel = new javax.swing.JLabel();
            currentPageLabel = new javax.swing.JLabel();
            pageLabel = new javax.swing.JLabel();
            nextPageButton = new javax.swing.JButton();
            pageLabel2 = new javax.swing.JLabel();
            prevPageButton = new javax.swing.JButton();

            copyMenuItem.setText(org.openide.util.NbBundle.getMessage(DataContentViewerArtifact.class, "DataContentViewerArtifact.copyMenuItem.text")); // NOI18N
            rightClickMenu.add(copyMenuItem);

            selectAllMenuItem.setText(org.openide.util.NbBundle.getMessage(DataContentViewerArtifact.class, "DataContentViewerArtifact.selectAllMenuItem.text")); // NOI18N
            rightClickMenu.add(selectAllMenuItem);

            setPreferredSize(new java.awt.Dimension(622, 424));

            jPanel1.setPreferredSize(new java.awt.Dimension(622, 424));

            outputViewPane.setEditable(false);
            outputViewPane.setFont(new java.awt.Font(outputViewPane.getFont().getName(), Font.PLAIN, 11));
            outputViewPane.setPreferredSize(new java.awt.Dimension(700, 400));
            jScrollPane1.setViewportView(outputViewPane);

            totalPageLabel.setText(org.openide.util.NbBundle.getMessage(DataContentViewerArtifact.class, "DataContentViewerArtifact.totalPageLabel.text")); // NOI18N

            ofLabel.setText(org.openide.util.NbBundle.getMessage(DataContentViewerArtifact.class, "DataContentViewerArtifact.ofLabel.text")); // NOI18N

            currentPageLabel.setText(org.openide.util.NbBundle.getMessage(DataContentViewerArtifact.class, "DataContentViewerArtifact.currentPageLabel.text")); // NOI18N
            currentPageLabel.setMaximumSize(new java.awt.Dimension(18, 14));
            currentPageLabel.setMinimumSize(new java.awt.Dimension(18, 14));
            currentPageLabel.setPreferredSize(new java.awt.Dimension(18, 14));

            pageLabel.setText(org.openide.util.NbBundle.getMessage(DataContentViewerArtifact.class, "DataContentViewerArtifact.pageLabel.text")); // NOI18N
            pageLabel.setMaximumSize(new java.awt.Dimension(33, 14));
            pageLabel.setMinimumSize(new java.awt.Dimension(33, 14));
            pageLabel.setPreferredSize(new java.awt.Dimension(33, 14));

            nextPageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_forward.png"))); // NOI18N NON-NLS
            nextPageButton.setText(org.openide.util.NbBundle.getMessage(DataContentViewerArtifact.class, "DataContentViewerArtifact.nextPageButton.text")); // NOI18N
            nextPageButton.setBorderPainted(false);
            nextPageButton.setContentAreaFilled(false);
            nextPageButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_forward_disabled.png"))); // NOI18N NON-NLS
            nextPageButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
            nextPageButton.setPreferredSize(new java.awt.Dimension(23, 23));
            nextPageButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_forward_hover.png"))); // NOI18N NON-NLS
            nextPageButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    nextPageButtonActionPerformed(evt);
                }
            });

            pageLabel2.setText(org.openide.util.NbBundle.getMessage(DataContentViewerArtifact.class, "DataContentViewerArtifact.pageLabel2.text")); // NOI18N
            pageLabel2.setMaximumSize(new java.awt.Dimension(29, 14));
            pageLabel2.setMinimumSize(new java.awt.Dimension(29, 14));
            pageLabel2.setPreferredSize(new java.awt.Dimension(29, 14));

            prevPageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_back.png"))); // NOI18N NON-NLS
            prevPageButton.setText(org.openide.util.NbBundle.getMessage(DataContentViewerArtifact.class, "DataContentViewerArtifact.prevPageButton.text")); // NOI18N
            prevPageButton.setBorderPainted(false);
            prevPageButton.setContentAreaFilled(false);
            prevPageButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_back_disabled.png"))); // NOI18N NON-NLS
            prevPageButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
            prevPageButton.setPreferredSize(new java.awt.Dimension(23, 23));
            prevPageButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_back_hover.png"))); // NOI18N NON-NLS
            prevPageButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    prevPageButtonActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(pageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(currentPageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(ofLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(totalPageLabel)
                    .addGap(41, 41, 41)
                    .addComponent(pageLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(prevPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addComponent(nextPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(366, Short.MAX_VALUE))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(currentPageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ofLabel)
                            .addComponent(totalPageLabel))
                        .addComponent(nextPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(prevPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(pageLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, 0)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE))
            );

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
        }// </editor-fold>//GEN-END:initComponents

    private void nextPageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextPageButtonActionPerformed
        currentPage = currentPage + 1;
        currentPageLabel.setText(Integer.toString(currentPage));  
        startNewTask(new SelectedArtifactChangedTask(currentPage));
    }//GEN-LAST:event_nextPageButtonActionPerformed

    private void prevPageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevPageButtonActionPerformed
        currentPage = currentPage - 1;
        currentPageLabel.setText(Integer.toString(currentPage));  
        startNewTask(new SelectedArtifactChangedTask(currentPage));
    }//GEN-LAST:event_prevPageButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JLabel currentPageLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton nextPageButton;
    private javax.swing.JLabel ofLabel;
    private javax.swing.JTextPane outputViewPane;
    private javax.swing.JLabel pageLabel;
    private javax.swing.JLabel pageLabel2;
    private javax.swing.JButton prevPageButton;
    private javax.swing.JPopupMenu rightClickMenu;
    private javax.swing.JMenuItem selectAllMenuItem;
    private javax.swing.JLabel totalPageLabel;
    // End of variables declaration//GEN-END:variables

    private void customizeComponents(){
        outputViewPane.setComponentPopupMenu(rightClickMenu);
        ActionListener actList = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                JMenuItem jmi = (JMenuItem) e.getSource();
                if(jmi.equals(copyMenuItem))
                    outputViewPane.copy();
                else if(jmi.equals(selectAllMenuItem))
                    outputViewPane.selectAll();
            }
        };
        copyMenuItem.addActionListener(actList);
        selectAllMenuItem.addActionListener(actList);
        
        Utilities.configureTextPaneAsHtml(outputViewPane);
    }

    /**
     * Resets the components to an empty view state.
     */
    private void resetComponents() {
        currentPage = 1;
        currentPageLabel.setText("");
        totalPageLabel.setText("");
        outputViewPane.setText("");
        prevPageButton.setEnabled(false);
        nextPageButton.setEnabled(false);
        currentNode = null;
    }
            
    @Override
    public void setNode(Node selectedNode) {
        if (currentNode == selectedNode) {
            return;
        }
        currentNode = selectedNode;
                
        // Make sure there is a node. Null might be passed to reset the viewer.
        if (selectedNode == null) {
            return;
        }
                
        // Make sure the node is of the correct type.
        Lookup lookup = selectedNode.getLookup();
        Content content = lookup.lookup(Content.class);
        if (content == null) {
            return; 
        }
        
        startNewTask(new SelectedNodeChangedTask(selectedNode));
    }

    @Override
    public String getTitle() {
        return NbBundle.getMessage(this.getClass(), "DataContentViewerArtifact.title");
    }

    @Override
    public String getToolTip() {
        return NbBundle.getMessage(this.getClass(), "DataContentViewerArtifact.toolTip");
    }

    @Override
    public DataContentViewer createInstance() {
        return new DataContentViewerArtifact();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void resetComponent() {
        resetComponents();
    }
    
    @Override
    public boolean isSupported(Node node) {
        if (node == null) {
            return false;
        }

        Content content = node.getLookup().lookup(Content.class);        
        if(content != null) {
            try {
                return content.getAllArtifactsCount() > 0;
            } 
            catch (TskException ex) {
                logger.log(Level.WARNING, "Couldn't get count of BlackboardArtifacts for content", ex); //NON-NLS
            }
        }
        return false;
    }

    @Override
    public int isPreferred(Node node) {
        BlackboardArtifact artifact = node.getLookup().lookup(BlackboardArtifact.class);
        if(artifact == null) {
            return 3;
        } 
        else {
            return 5;
        }
    }

    /**
     * Instances of this class are simple containers for view update information generated by a background thread.
     */
    private class ViewUpdate {
        int numberOfPages;
        int currentPage;
        String text;
        
        ViewUpdate(int numberOfPages, int currentPage, String text) {
            this.currentPage = currentPage;
            this.numberOfPages = numberOfPages;
            this.text = text;
        }
    }
       
    /**
     * Called from queued SwingWorker done() methods on the EDT thread, so doesn't need to be synchronized.
     * @param viewUpdate A simple container for display update information from a background thread.
     */
    private void updateView(ViewUpdate viewUpdate) {  
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        nextPageButton.setEnabled(viewUpdate.currentPage < viewUpdate.numberOfPages);
        prevPageButton.setEnabled(viewUpdate.currentPage > 1);
        currentPage = viewUpdate.currentPage;
        totalPageLabel.setText(Integer.toString(viewUpdate.numberOfPages));
        currentPageLabel.setText(Integer.toString(currentPage));  
        
        // @@@ This can take a long time. Perhaps a faster HTML renderer can be found.
        // Note that the rendering appears to be done on a background thread, since the
        // wait cursor reset below happens before the new text hits the JTextPane. On the
        // other hand, the UI is unresponsive...
        outputViewPane.setText(viewUpdate.text); 
        outputViewPane.moveCaretPosition(0);
        
        this.setCursor(null);
    }    
    
    /**
     * Start a new task on its own background thread, canceling the previous task.
     * @param task A new SwingWorker object to execute as a background thread. 
     */
    private synchronized void startNewTask(SwingWorker<ViewUpdate, Void> task) {   
        outputViewPane.setText(WAIT_TEXT);        
        outputViewPane.moveCaretPosition(0);
                
        // The output of the previous task is no longer relevant.
        if (currentTask != null) {
            // This call sets a cancellation flag. It does not terminate the background thread running the task. 
            // The task must check the cancellation flag and react appropriately.
            currentTask.cancel(false);
        }
                        
        // Start the new task.
        currentTask = task;
        currentTask.execute();
    }

    /**
     * Populate the cache of artifact represented as strings.
     * @param artifactStrings A list of string representations of artifacts.
     */
    private void setArtifactContentStrings(List<ArtifactStringContent> artifactStrings) {
        synchronized (lock) {
            this.artifactContentStrings = artifactStrings;
        }
    }
    
    /**
     * Retrieve the cache of artifact represented as strings.
     * @return A list of string representations of artifacts.
     */
    private List<ArtifactStringContent> getArtifactContentStrings() {
        synchronized (lock) {
            return artifactContentStrings;
        }
    }
    
    /**
     * Instances of this class use a background thread to generate a ViewUpdate when a node is selected,
     * changing the set of blackboard artifacts ("results") to be displayed.
     */
    private class SelectedNodeChangedTask extends SwingWorker<ViewUpdate, Void> {
        private final Node selectedNode;
        
        SelectedNodeChangedTask(Node selectedNode) {
            this.selectedNode = selectedNode;
        }
        
        @Override
        protected ViewUpdate doInBackground() {   
            // Get the lookup for the node for access to its underlying content and
            // blackboard artifact, if any.
            Lookup lookup = selectedNode.getLookup();
            
            // Get the content.
            Content content = lookup.lookup(Content.class);
            if (content == null) {
                return new ViewUpdate(getArtifactContentStrings().size(), currentPage, ERROR_TEXT); 
            }
            
            // Get all of the blackboard artifacts associated with the content. These are what this
            // viewer displays.
            ArrayList<BlackboardArtifact> artifacts;
            try {
                artifacts = content.getAllArtifacts();
            } 
            catch (TskException ex) {
                logger.log(Level.WARNING, "Couldn't get artifacts", ex); //NON-NLS
                return new ViewUpdate(getArtifactContentStrings().size(), currentPage, ERROR_TEXT); 
            }
            
            if (isCancelled()) {
                return null;
            }
            
            // Build the new artifact strings cache.
            ArrayList<ArtifactStringContent> artifactStrings = new ArrayList<>();
            for (BlackboardArtifact artifact : artifacts) {
                artifactStrings.add(new ArtifactStringContent(artifact));
            }

            // If the node has an underlying blackboard artifact, show it. If not,
            // show the first artifact.
            int index = 0;
            BlackboardArtifact artifact = lookup.lookup(BlackboardArtifact.class);
            if (artifact != null) {
                index = artifacts.indexOf(artifact);
                if (index == -1) {
                    index = 0;
                } else {
                    // if the artifact has an ASSOCIATED ARTIFACT, then we display the associated artifact instead
                    try {
                        for (BlackboardAttribute attr : artifact.getAttributes()) {
                           if (attr.getAttributeTypeID() == BlackboardAttribute.ATTRIBUTE_TYPE.TSK_ASSOCIATED_ARTIFACT.getTypeID()) {
                               long assocArtifactId = attr.getValueLong();
                               int assocArtifactIndex = -1;
                               for (BlackboardArtifact art: artifacts) {
                                   if (assocArtifactId == art.getArtifactID()) {
                                       assocArtifactIndex = artifacts.indexOf(art);
                                       break;
                                   }
                               }
                               if (assocArtifactIndex >= 0) {
                                    index = assocArtifactIndex;
                                }
                               break;
                           }
                        }
                    } 
                    catch (TskCoreException ex) {
                        logger.log(Level.WARNING, "Couldn't get associated artifact to display in Content Viewer.", ex); //NON-NLS
                    }
               }
                    
            }        

            if (isCancelled()) {
                return null;
            }
                        
            // Add one to the index of the artifact string for the corresponding page index. Note that the getString() method
            // of ArtifactStringContent does a lazy fetch of the attributes of the correspoding artifact and represents them as
            // HTML.
            ViewUpdate viewUpdate = new ViewUpdate(artifactStrings.size(), index + 1, artifactStrings.get(index).getString()); 

            // It may take a considerable amount of time to fetch the attributes of the selected artifact and render them
            // as HTML, so check for cancellation.
            if (isCancelled()) {
                return null;
            }

            // Update the artifact strings cache.
            setArtifactContentStrings(artifactStrings);
            
            return viewUpdate;
        }
        
        @Override
        protected void done() {
            if (!isCancelled()) {
                try {
                    ViewUpdate viewUpdate = get();
                    if (viewUpdate != null) {
                        updateView(viewUpdate);
                    }
                }
                catch (InterruptedException | ExecutionException ex) {
                    logger.log(Level.WARNING, "Artifact display task unexpectedly interrupted or failed", ex);                 //NON-NLS
                }                
            }
        }        
    }
    
    /**
     * Instances of this class use a background thread to generate a ViewUpdate when the user pages the view
     * to look at another blackboard artifact ("result").
     */
    private class SelectedArtifactChangedTask extends SwingWorker<ViewUpdate, Void> {
        private final int pageIndex;
        
        SelectedArtifactChangedTask(final int pageIndex) {
            this.pageIndex = pageIndex;
        }
        
        @Override
        protected ViewUpdate doInBackground() {
            // Get the artifact string to display from the cache. Note that one must be subtracted from the
            // page index to get the corresponding artifact string index.
            List<ArtifactStringContent> artifactStrings = getArtifactContentStrings();
            ArtifactStringContent artifactStringContent = artifactStrings.get(pageIndex - 1);

            // The getString() method of ArtifactStringContent does a lazy fetch of the attributes of the 
            // correspoding artifact and represents them as HTML.
            String artifactString = artifactStringContent.getString();
            
            // It may take a considerable amount of time to fetch the attributes of the selected artifact and render them
            // as HTML, so check for cancellation.
            if (isCancelled()) {
                return null;
            }
                                    
            return new ViewUpdate(artifactStrings.size(), pageIndex, artifactString);
        }
        
        @Override
        protected void done() {
            if (!isCancelled()) {
                try {
                    ViewUpdate viewUpdate = get();
                    if (viewUpdate != null) {
                        updateView(viewUpdate);
                    }
                }
                catch (InterruptedException | ExecutionException ex) {
                    logger.log(Level.WARNING, "Artifact display task unexpectedly interrupted or failed", ex);                 //NON-NLS
                }                
            }
        }
    }               
}
