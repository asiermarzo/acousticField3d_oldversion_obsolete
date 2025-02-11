/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.gui;

import acousticfield3d.scene.Entity;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.simulation.Animation;
import acousticfield3d.utils.Color;
import java.util.ArrayList;

/**
 *
 * @author Asier
 */
public class ColorizeBeadsForm extends javax.swing.JFrame {
    MainForm mf;
    
    public ColorizeBeadsForm(MainForm mf) {
        this.mf = mf;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        area = new javax.swing.JTextArea();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        allAnimsCheck = new javax.swing.JCheckBox();
        controlPointCheck = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Colorize Beads");

        area.setColumns(20);
        area.setRows(5);
        area.setText("255,255,255,255\n0,0,255,255\n0,255,0,255\n0,255,255,255\n255,0,0,255\n255,0,255,255\n255,255,0,255\n256,64,64,255");
        jScrollPane1.setViewportView(area);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        allAnimsCheck.setSelected(true);
        allAnimsCheck.setText("All anims");

        controlPointCheck.setSelected(true);
        controlPointCheck.setText("control points");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(controlPointCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(allAnimsCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton)
                    .addComponent(allAnimsCheck)
                    .addComponent(controlPointCheck))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        String[] lines = area.getText().split("\\n");
        ArrayList<Integer> colors = new ArrayList<>(lines.length);
        for(String l : lines){
            colors.add( Color.parse(l) );
        }
        if (allAnimsCheck.isSelected()){
            for(Animation a : mf.getSimulation().getAnimations().getElements()){
               colorizeAnim(colors, a); 
            }
        }else{
            colorizeAnim(colors, mf.animPanel.currentAnimation);
        }
        
        if (controlPointCheck.isSelected()){
            ArrayList<MeshEntity> cps = new ArrayList<>();
            mf.scene.gatherMeshEntitiesWithTag(cps, Entity.TAG_CONTROL_POINT);
            int index = 0;
            final int nColors = colors.size();
            for(MeshEntity e : cps){
                e.setColor( colors.get(index % nColors) );
                index++;
            }
        }
        
        mf.needUpdate();
    }//GEN-LAST:event_okButtonActionPerformed

    private void colorizeAnim(ArrayList<Integer> colors, Animation a){
        final int n = colors.size();
        for(MeshEntity me : a.getControlPoints()){
            me.setColor( colors.get( me.getNumber() % n));
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox allAnimsCheck;
    private javax.swing.JTextArea area;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox controlPointCheck;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
