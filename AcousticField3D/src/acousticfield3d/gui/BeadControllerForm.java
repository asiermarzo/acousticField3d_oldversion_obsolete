/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.gui;

import acousticfield3d.math.Vector3f;
import acousticfield3d.scene.Entity;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.scene.Resources;
import acousticfield3d.utils.Parse;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Asier
 */
public class BeadControllerForm extends javax.swing.JFrame {
    private final MainForm mf;
    
    private long lastMillis;
    private final Vector3f speed = new Vector3f();
    private final Vector3f lastPosition = new Vector3f();
    private boolean hasJumped;
    
    public BeadControllerForm(MainForm mf) {
        this.mf = mf;
        initComponents();
    }
    
    private HashMap<Integer, Integer> pressedKeys = new HashMap<Integer, Integer>();
    private boolean keyPressed(int code){
        return pressedKeys.containsKey( code );
    }
    
    private void raceLogic(Entity bead, float dt) {
        final float movAmount = dt * Parse.stringToFloat( movSpeedText.getText() );
        final float rotAmount = dt * Parse.stringToFloat( rotSpeedText.getText() );
        
        if (keyPressed(KeyEvent.VK_A)){
            bead.getTransform().rotateLocal(0, rotAmount, 0);
        }else if(keyPressed(KeyEvent.VK_D)){
            bead.getTransform().rotateLocal(0, -rotAmount, 0);
        }
        if (keyPressed(KeyEvent.VK_W)){
            bead.getTransform().moveLocalSpace(0, 0, -movAmount);
        }else if (keyPressed(KeyEvent.VK_S)){
            bead.getTransform().moveLocalSpace(0, 0, movAmount);
        }
        
        //down & up (position)
        if (keyPressed(KeyEvent.VK_T)){
            bead.getTransform().moveLocalSpace(0, movAmount, 0);
        }else if (keyPressed(KeyEvent.VK_G)){
            bead.getTransform().moveLocalSpace(0, -movAmount, 0);
        }
    }
    private void spaceshipLogic(Entity bead, float dt) {
        final float movAmount = dt * Parse.stringToFloat( movSpeedText.getText() );
        final float rotAmount = dt * Parse.stringToFloat( rotSpeedText.getText() );
        final float truAmount = dt * Parse.stringToFloat( truSpeedText.getText() );
        final float gravityGain = Parse.stringToFloat( gravityText.getText() ) / 100.0f;
        
        //get the earth (first misc with sphere mesh)
        final Entity earth = mf.scene.getFirstWithTag( Entity.TAG_MASK );
        if (earth != null){
            //F = m1 * m2 * r / (r^3) just assume unity masses
            final Vector3f fGrav = bead.getTransform().getTranslation().subtract( earth.getTransform().getTranslation());
            final float dist = fGrav.length();
            fGrav.multLocal( -gravityGain / (dist*dist*dist));
            
            //apply gravity
            speed.addLocalInc(fGrav, dt);
        }
        
        
        //apply speed
        bead.getTransform().getTranslation().addLocalInc(speed, dt);
        
        //forward & backward (speed)
        Vector3f speedRot = new Vector3f(0,0, truAmount);
        bead.getTransform().getRotation().multLocal( speedRot );
        if (keyPressed(KeyEvent.VK_W)){
            speed.addLocal( speedRot );
        }else if(keyPressed(KeyEvent.VK_S)){
            speed.subtractLocal( speedRot );
        }
        
        //rotate
        if (keyPressed(KeyEvent.VK_A)){
            bead.getTransform().rotateLocal(0, rotAmount, 0);
        }else if(keyPressed(KeyEvent.VK_D)){
            bead.getTransform().rotateLocal(0, -rotAmount, 0);
        }
        
        //down & up (position)
        if (keyPressed(KeyEvent.VK_T)){
            bead.getTransform().moveLocalSpace(0, movAmount, 0);
        }else if (keyPressed(KeyEvent.VK_G)){
            bead.getTransform().moveLocalSpace(0, -movAmount, 0);
        }
    }
    
    private void platformLogic(Entity bead, float dt) {
        final float movAmount = dt * Parse.stringToFloat( movSpeedText.getText() );
        final float rotAmount = dt * Parse.stringToFloat( rotSpeedText.getText() );
        final float truAmount = dt * Parse.stringToFloat( truSpeedText.getText() );
        final float gravityGain = 500 * Parse.stringToFloat( gravityText.getText() );
        
        final Vector3f pos = bead.getTransform().getTranslation();
        lastPosition.set( pos );
        
        //apply gravity
        speed.addLocalInc( Vector3f.UNIT_Y, -gravityGain * dt);
        //apply speed
        bead.getTransform().getTranslation().addLocalInc(speed, dt);
          
        //walking
        if (keyPressed(KeyEvent.VK_A)){
            bead.getTransform().getTranslation().addLocalInc( Vector3f.UNIT_X, -movAmount);
        }else if(keyPressed(KeyEvent.VK_D)){
            bead.getTransform().getTranslation().addLocalInc( Vector3f.UNIT_X, movAmount);
        }
              
        //collide with any platform (misc with box mesh)
        final ArrayList<MeshEntity> platforms = new ArrayList<>();
        mf.scene.gatherMeshEntitiesWithTag(platforms, Entity.TAG_MASK );
        for(MeshEntity p : platforms){
            if ( p.getMesh().equals(Resources.MESH_BOX) ){ 
                final Vector3f pScale = p.getTransform().getScale();
                final Vector3f pPosition = p.getTransform().getTranslation();
                
                //contained within XZ
                final float xmin = pPosition.x - pScale.x /2.0f;
                final float xmax = pPosition.x + pScale.x /2.0f;
                final float ymin = pPosition.y - pScale.y /2.0f;
                final float ymax = pPosition.y + pScale.y /2.0f;
                final float zmin = pPosition.z - pScale.z /2.0f;
                final float zmax = pPosition.z + pScale.z /2.0f;
                
                if (pos.x > xmin && pos.x < xmax && pos.z > zmin && pos.z < zmax){
                    if (lastPosition.y < ymin && pos.y > ymin){ //headbut
                        pos.y = ymin - 0.00001f;
                        speed.y = 0;
                    }else if (lastPosition.y > ymax && pos.y < ymax){ //landing
                        pos.y = ymax + 0.00001f;
                        hasJumped = false;
                        speed.y = 0;
                    }
                }
                
            }
        }
        
        
        //jumping
        if (keyPressed(KeyEvent.VK_W) && !hasJumped){
            hasJumped = true;
            speed.y += truAmount * 100;
        }
        
    }
    
    private void freeLogic(Entity bead, float dt) {
        final float movAmount = dt * Parse.stringToFloat( movSpeedText.getText() );
       
        final Vector3f pos = bead.getTransform().getTranslation();
        
       
        //left / right
        if (keyPressed(KeyEvent.VK_A)){
            bead.getTransform().getTranslation().addLocalInc( Vector3f.UNIT_X, -movAmount);
        }else if(keyPressed(KeyEvent.VK_D)){
            bead.getTransform().getTranslation().addLocalInc( Vector3f.UNIT_X, movAmount);
        }
        
        //forward / backward
        if (keyPressed(KeyEvent.VK_W)){
            bead.getTransform().getTranslation().addLocalInc( Vector3f.UNIT_Z, -movAmount);
        }else if(keyPressed(KeyEvent.VK_S)){
            bead.getTransform().getTranslation().addLocalInc( Vector3f.UNIT_Z, movAmount);
        }
        
        //up / down
        if (keyPressed(KeyEvent.VK_T)){
            bead.getTransform().getTranslation().addLocalInc( Vector3f.UNIT_Y, movAmount);
        }else if(keyPressed(KeyEvent.VK_G)){
            bead.getTransform().getTranslation().addLocalInc( Vector3f.UNIT_Y, -movAmount);
        }
    }
    
    public void tick(){
        final int selectedController = controllerCombo.getSelectedIndex();
        if (selectedController == 0) {return;}
        
        //get dt
        final long millis = System.currentTimeMillis();
        float dt = (millis - lastMillis) / 1000.0f;
        if(dt > 1){
            dt = 0.0f;
        }
        lastMillis = millis;
            
        //get the first bead
        final Entity bead = mf.scene.getFirstWithTag( Entity.TAG_BEAD | Entity.TAG_CONTROL_POINT );
        if (bead == null){ return; }
        
        //apply logic
        if(selectedController == 1){ //race
            raceLogic(bead, dt);
        }else if(selectedController == 2){ // spaceship
            spaceshipLogic(bead, dt);
        }else if(selectedController == 3){ //platform
            platformLogic(bead, dt);
        }else if(selectedController == 4){ //free
            freeLogic(bead, dt);
        }
        
        //autocalc and send
        if(autosendCheck.isSelected()){
            mf.movePanel.doAutoCalcAndSend(false);
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controllerCombo = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        movSpeedText = new javax.swing.JTextField();
        autosendCheck = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        rotSpeedText = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        gravityText = new javax.swing.JTextField();
        typingText = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        truSpeedText = new javax.swing.JTextField();

        setTitle("Bead Controller");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        controllerCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "Race", "Spaceship", "Platform", "Free" }));
        controllerCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controllerComboActionPerformed(evt);
            }
        });

        jLabel1.setText("Mov speed");

        movSpeedText.setText("0.006");

        autosendCheck.setText("autosend");

        jLabel2.setText("Adjust the speed in the phys tab");

        jLabel3.setText("Controller type");

        jLabel4.setText("Rot speed");

        rotSpeedText.setText("1");

        jLabel5.setText("gravity:");

        gravityText.setText("0.000002");

        typingText.setText("type here: asdw tg");
        typingText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                typingTextKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                typingTextKeyReleased(evt);
            }
        });

        jLabel6.setText("Tru speed");

        truSpeedText.setText("0.006");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(controllerCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(movSpeedText, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rotSpeedText))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(autosendCheck)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(gravityText)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(truSpeedText, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(typingText))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autosendCheck)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(controllerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(movSpeedText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(rotSpeedText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(gravityText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(truSpeedText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(typingText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    private void typingTextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_typingTextKeyPressed
        final int keycode = evt.getKeyCode();
        pressedKeys.put(keycode, keycode);
    }//GEN-LAST:event_typingTextKeyPressed

    private void typingTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_typingTextKeyReleased
        final int keycode = evt.getKeyCode();
        pressedKeys.remove(keycode);
    }//GEN-LAST:event_typingTextKeyReleased

    private void controllerComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_controllerComboActionPerformed
        final int selectedController = controllerCombo.getSelectedIndex();
        if (selectedController == 0) {return;}
        
        /*
        if(selectedController == 1){ //race
            movSpeedText.setText("0.015");
            rotSpeedText.setText("3");
        }else if(selectedController == 2){ // spaceship
            movSpeedText.setText("0.015");
            gravityText.setText("0.0000002");
            rotSpeedText.setText("3");
            truSpeedText.setText("0.003");
        }else if(selectedController == 3){ //platform
            movSpeedText.setText("0.0045");
            gravityText.setText("0.00002");
            truSpeedText.setText("0.005");
        }else if(selectedController == 4){ //free
            movSpeedText.setText("0.005");
        }
                */
        resetLogics();
    }//GEN-LAST:event_controllerComboActionPerformed

    private void resetLogics() {
        speed.reset();
        hasJumped = false;
        lastMillis = System.currentTimeMillis();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autosendCheck;
    private javax.swing.JComboBox controllerCombo;
    private javax.swing.JTextField gravityText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField movSpeedText;
    private javax.swing.JTextField rotSpeedText;
    private javax.swing.JTextField truSpeedText;
    private javax.swing.JTextField typingText;
    // End of variables declaration//GEN-END:variables

    


}
