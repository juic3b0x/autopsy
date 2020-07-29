/*
 * Autopsy Forensic Browser
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
package org.sleuthkit.autopsy.casemodule.datasourcesummary;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JPanel;
import org.sleuthkit.datamodel.DataSource;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import java.text.DecimalFormat;
import javax.swing.JLabel;
import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.coreutils.FileTypeUtils;

/**
 * A Pie Chart that shows file mime types in a data source.
 */
public class FileTypePieChart extends JPanel {
    private static final Font DEFAULT_FONT = new JLabel().getFont(); 
    private static final Font DEFAULT_HEADER_FONT = new Font(DEFAULT_FONT.getName(), DEFAULT_FONT.getStyle(), (int) (DEFAULT_FONT.getSize() * 1.5));
    
    private final DefaultPieDataset dataset = new DefaultPieDataset();
    private DataSource dataSource;


    public FileTypePieChart() {
        // Create chart
        JFreeChart chart = ChartFactory.createPieChart(
                NbBundle.getMessage(DataSourceSummaryCountsPanel.class, "DataSourceSummaryCountsPanel.byMimeTypeLabel.text"),
                dataset,
                true,
                true,
                false);
        
        chart.setBackgroundPaint(null);
        chart.getLegend().setItemFont(DEFAULT_FONT);
        chart.getTitle().setFont(DEFAULT_HEADER_FONT);
        
        PiePlot plot = ((PiePlot) chart.getPlot());
        
        //Format Label
        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0.0%"));
        
        plot.setLabelGenerator(labelGenerator);
        plot.setLabelFont(DEFAULT_FONT);

        plot.setBackgroundPaint(null);
        plot.setOutlinePaint(null);

        
        // Create Panel
        ChartPanel panel = new ChartPanel(chart);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }

    DataSource getDataSource() {
        return this.dataSource;
    }

    void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.dataset.clear();

        if (dataSource != null) {
            this.dataset.setValue(Bundle.DataSourceSummaryCountsPanel_FilesByMimeTypeTableModel_images_row(),
                    getCount(dataSource, FileTypeUtils.FileTypeCategory.IMAGE));
            this.dataset.setValue(Bundle.DataSourceSummaryCountsPanel_FilesByMimeTypeTableModel_videos_row(),
                    getCount(dataSource, FileTypeUtils.FileTypeCategory.VIDEO));
            this.dataset.setValue(Bundle.DataSourceSummaryCountsPanel_FilesByMimeTypeTableModel_audio_row(),
                    getCount(dataSource, FileTypeUtils.FileTypeCategory.AUDIO));
            this.dataset.setValue(Bundle.DataSourceSummaryCountsPanel_FilesByMimeTypeTableModel_documents_row(),
                    getCount(dataSource, FileTypeUtils.FileTypeCategory.DOCUMENTS));
            this.dataset.setValue(Bundle.DataSourceSummaryCountsPanel_FilesByMimeTypeTableModel_executables_row(),
                    getCount(dataSource, FileTypeUtils.FileTypeCategory.EXECUTABLE));
        }
    }

    /**
     * Retrieves the counts of files of a particular mime type for a particular
     * DataSource.
     *
     * @param dataSource The DataSource.
     * @param category   The mime type category.
     *
     * @return The count.
     */
    private static Long getCount(DataSource dataSource, FileTypeUtils.FileTypeCategory category) {
        return DataSourceInfoUtilities.getCountOfFilesForMimeTypes(dataSource, category.getMediaTypes());
    }
}
