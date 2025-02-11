/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.gui;

import acousticfield3d.math.FastMath;
import acousticfield3d.math.Vector3f;
import acousticfield3d.simulation.Transducer;
import acousticfield3d.utils.FileUtils;
import acousticfield3d.utils.Parse;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import ktools.utils.TextFrame;

/**
 *
 * @author Asier
 */
public class ExportPhasesForm extends javax.swing.JFrame {
    public static final int TRANS_PER_FILE = 16;

    static void exportPosAndPhases(MainForm aThis) {
        StringBuilder sb = new StringBuilder();
        
        final ArrayList<Transducer> trans = aThis.simulation.getTransducers();
        
        for(Transducer t : trans){
            final Vector3f pos = t.getTransform().getTranslation();
            final float phase = t.getPhase() * FastMath.PI;
            final Vector3f nor = t.getTransform().getRot().mult( Vector3f.UNIT_Y );
            
            sb.append(pos.x + " " + (-pos.z) + " " + pos.y + " ");
            sb.append(nor.x + " " + (-nor.z) + " " + nor.y + " ");
            sb.append(" " + phase + "\n");
        }
        
        TextFrame.showText("Positions and phases", sb.toString(), aThis);
    }
    
    MainForm mf;
    
    public ExportPhasesForm(MainForm mf) {
        this.mf = mf;
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField3 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        pathText = new javax.swing.JTextField();
        selectButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        maxTransText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        divsText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        defaultPhaseText = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        area = new javax.swing.JTextArea();
        exportButton = new javax.swing.JButton();
        exportTxtButton = new javax.swing.JButton();

        jTextField3.setText("jTextField3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Export phases");

        jLabel1.setText("path:");

        selectButton.setText("Select");
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("max transducers");

        maxTransText.setText("64");

        jLabel3.setText("DIVs:");

        divsText.setText("50");

        jLabel4.setText("default phase (DIVS=off)");

        defaultPhaseText.setText("0");

        jLabel5.setText("Overrides (nPin phaseValue)");

        area.setColumns(20);
        area.setRows(5);
        jScrollPane1.setViewportView(area);

        exportButton.setText("export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        exportTxtButton.setText("export txt");
        exportTxtButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportTxtButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pathText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(selectButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxTransText, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(divsText, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(defaultPhaseText))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(exportButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(exportTxtButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(pathText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(maxTransText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(divsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(defaultPhaseText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exportButton)
                    .addComponent(exportTxtButton)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        String path = FileUtils.selectDirectory(this, "select", null);
        if(path != null){
            pathText.setText( path );
        }
    }//GEN-LAST:event_selectButtonActionPerformed

    private HashMap<Integer, Integer> gatherPhases(){
        final int divs = Parse.stringToInt( divsText.getText() );
        final int phaseDisc = divs / 2;
        final String[] overS = area.getText().split("\\n");
        
        HashMap<Integer, Integer> transPhase = new HashMap<>();
        final ArrayList<Transducer> trans = mf.simulation.getTransducers();
        
        for (Transducer t : trans){
            int phase;
            if (t.getPAmplitude() > 0.5f) {
                phase = t.getXMOSPhase(phaseDisc, divs);
                transPhase.put( t.getOrderNumber(), phase);
            }
        }
        for(String s : overS){
            String[] s2 = s.split(" ");
            if (s2.length >= 2){
                int nTrans = Parse.stringToInt( s2[0] );
                int phase = Parse.stringToInt( s2[1] );
                transPhase.put( nTrans, phase);
            }
        }
        
        return transPhase;
    }
    
    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        final String path = pathText.getText();
        final int maxTrans = Parse.stringToInt( maxTransText.getText() );
        final int nFiles = maxTrans / TRANS_PER_FILE;
        final int divs = Parse.stringToInt( divsText.getText() );
        final int phaseDisc = divs / 2;
        
        final HashMap<Integer, Integer> transPhase = gatherPhases();
        
        int[][] patterns = new int[nFiles][divs];
        for (Integer n : transPhase.keySet()) {
            final int number = n;
            final int phase = transPhase.get(n);

            final int targetFile = number / TRANS_PER_FILE;
            final int value = 1 << (number - targetFile * TRANS_PER_FILE);
            for (int i = 0; i < phaseDisc; ++i) {
                patterns[targetFile][ (i + phase) % divs] |= value;
            }
        }
        
        for(int i = 0; i < nFiles; ++i){
            StringBuilder sb = new StringBuilder();
            for(int j = 0; j < divs; ++j){
                sb.append( patterns[i][j] + ",\n");
            }
            sb.setCharAt(sb.length() - 2, ' ');
            sb.setCharAt(sb.length() - 1, ' ');
            File targetfile = new File(path, i + ".tbl");
            try {
                FileUtils.writeBytesInFile(targetfile, sb.toString().getBytes());
            } catch (IOException ex) {
                Logger.getLogger(ExportPhasesForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_exportButtonActionPerformed

    private void exportTxtButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportTxtButtonActionPerformed
        final String path = pathText.getText();
        final int maxTrans = Parse.stringToInt(maxTransText.getText());
        final int divs = Parse.stringToInt( divsText.getText() );
        final HashMap<Integer, Integer> transPhase = gatherPhases();

        int[] phasesOrdered = new int[maxTrans];
        Arrays.fill(phasesOrdered, -1);
        for (Integer n : transPhase.keySet()) {
            final int number = n;
            final int phase = transPhase.get(n);
            if (number >= 0 && number < maxTrans) {
                phasesOrdered[number] = phase;
            }
        }
        
        final float fr = mf.simulation.getTransducers().get(0).getFrequency();
        final float msDelay = 1000.0f / fr / divs;
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < maxTrans; ++j) {
            int number = j;
            int phase = phasesOrdered[j];
            String phaseIntStr = "off";
            String phaseString = "off";
            String phaseStringNeg = "off";
            if(phase != -1){
                phaseIntStr = phase + "";
                phaseString = (msDelay*phase) + "ms";
                phaseStringNeg = (msDelay* (divs-phase)) + "ms";
            }
            sb.append(number + " --> " + phaseIntStr + " | " + phaseString + " | " + phaseStringNeg + "\n");
        }

        File targetfile = new File(path, "phases.txt");
        try {
            FileUtils.writeBytesInFile(targetfile, sb.toString().getBytes());
        } catch (IOException ex) {
            Logger.getLogger(ExportPhasesForm.class.getName()).log(Level.SEVERE, null, ex);
        }
      
    }//GEN-LAST:event_exportTxtButtonActionPerformed

    public void initValues(){
        maxTransText.setText( mf.miscPanel.getTransducersNumber() + "");
        divsText.setText( (mf.miscPanel.getPhaseDiscretization()*2) + "");
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea area;
    private javax.swing.JTextField defaultPhaseText;
    private javax.swing.JTextField divsText;
    private javax.swing.JButton exportButton;
    private javax.swing.JButton exportTxtButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField maxTransText;
    private javax.swing.JTextField pathText;
    private javax.swing.JButton selectButton;
    // End of variables declaration//GEN-END:variables
}
