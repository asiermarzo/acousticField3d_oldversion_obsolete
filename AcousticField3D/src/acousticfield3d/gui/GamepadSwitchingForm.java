/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.gui;

import acousticfield3d.math.FastMath;
import acousticfield3d.utils.Parse;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 *
 * @author Asier
 */
public class GamepadSwitchingForm extends javax.swing.JFrame {
    private final static int N_COMP = 2;
    private final static float CONTROLLER_MIN = -1.0f;
    private final static float CONTROLLER_MAX = 1.0f;
    
    final MainForm mf;
    Controller currentController = null;
    final ArrayList<Controller> controllers = new ArrayList<>();
    final ArrayList<Component> components = new ArrayList<>();
          
    final float[] componentLastValue = new float[N_COMP];
    final JCheckBox[] checkBoxes = new JCheckBox[N_COMP];
    final JProgressBar[] progressBars = new JProgressBar[N_COMP];
    final JTextField[] values = new JTextField[N_COMP];
    final JTextField[] mins = new JTextField[N_COMP];
    final JTextField[] maxs = new JTextField[N_COMP];
    final JTextField[] rawValues = new JTextField[N_COMP];
    final JComboBox[] combos = new JComboBox[N_COMP];
    
    int stepsSinceSending = 0;
    
    Thread pollingThread;
    
    public GamepadSwitchingForm(MainForm mf) {
        this.mf = mf;
        initComponents();
        
        checkBoxes[0] = aCheck; checkBoxes[1] = bCheck;
        progressBars[0] = aBar; progressBars[1] = bBar;
        values[0] = aValueText; values[1] = bValueText;
        mins[0] = aMinText; mins[1] = bMinText;
        maxs[0] = aMaxText; maxs[1] = bMaxText;
        rawValues[0] = aCurrentText; rawValues[1] = bCurrentText;
        combos[0] = aButtonCombo; combos[1] = bButtonCombo;
        
        updateControllerInfo();
        startPollingThread();
    }

    private void startPollingThread(){
        stopPollingThread();
        pollingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(! Thread.interrupted() ){
                    tick();
                }
            }
        }, "controller polling thread");
        pollingThread.start();
    }
    
    private void stopPollingThread(){
        if (pollingThread != null){
            pollingThread.interrupt();
            pollingThread = null;
        }
    }
    
    private void updateControllerInfo(){
        controllerCombo.removeAllItems();
        controllers.clear();
        
        final ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
	final Controller[] ca = ce.getControllers();
	
        for(Controller c : ca){
            controllers.add(c);
            controllerCombo.addItem( c.getName() );
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

        jLabel1 = new javax.swing.JLabel();
        controllerCombo = new javax.swing.JComboBox();
        connectButton = new javax.swing.JButton();
        aCheck = new javax.swing.JCheckBox();
        aBar = new javax.swing.JProgressBar();
        aValueText = new javax.swing.JTextField();
        bCheck = new javax.swing.JCheckBox();
        bBar = new javax.swing.JProgressBar();
        bValueText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        aMinText = new javax.swing.JTextField();
        aMaxText = new javax.swing.JTextField();
        aButtonCombo = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        bMinText = new javax.swing.JTextField();
        bMaxText = new javax.swing.JTextField();
        bButtonCombo = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        updateText = new javax.swing.JTextField();
        sendEveryCheck = new javax.swing.JCheckBox();
        sendEveryText = new javax.swing.JTextField();
        aCurrentText = new javax.swing.JTextField();
        bCurrentText = new javax.swing.JTextField();
        maxChangeCheck = new javax.swing.JCheckBox();
        maxChangeText = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("GamePad Control");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("Controller:");

        controllerCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        connectButton.setText("Connect");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        aCheck.setText("A");

        aValueText.setEditable(false);
        aValueText.setText("255.5");

        bCheck.setText("B");

        bValueText.setEditable(false);
        bValueText.setText("255.5");

        jLabel2.setText("A");

        aMinText.setText("0");

        aMaxText.setText("100");

        aButtonCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("B");

        bMinText.setText("0");

        bMaxText.setText("100");

        bButtonCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setText("Update every (ms):");

        updateText.setText("25");

        sendEveryCheck.setText("send every (steps):");

        sendEveryText.setText("5");

        aCurrentText.setEditable(false);
        aCurrentText.setText("0.00000000");

        bCurrentText.setEditable(false);
        bCurrentText.setText("0.00000000");

        maxChangeCheck.setText("max of");

        maxChangeText.setText("0.01");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(aCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(aBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(aValueText, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(bCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bValueText, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bMinText))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(aMinText, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(bMaxText, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                            .addComponent(aMaxText))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(aButtonCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(bButtonCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(aCurrentText)
                            .addComponent(bCurrentText)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(controllerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(connectButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updateText, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendEveryCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendEveryText, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxChangeCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxChangeText, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(aValueText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(controllerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(connectButton))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(aCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(aBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(bValueText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(bCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bBar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(aMinText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aMaxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aButtonCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aCurrentText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(bMinText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bMaxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bButtonCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bCurrentText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(updateText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendEveryCheck)
                    .addComponent(sendEveryText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxChangeCheck)
                    .addComponent(maxChangeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        stopPollingThread();
        dispose();
    }//GEN-LAST:event_formWindowClosing

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        final int index = controllerCombo.getSelectedIndex();
        if (index >= 0 && index < controllers.size()){
            currentController = controllers.get(index);
            final Component[] comps = currentController.getComponents();
            components.clear();
            aButtonCombo.removeAllItems();
            bButtonCombo.removeAllItems();
            for (Component co : comps){
                if (co.isAnalog()){
                    components.add( co );
                    aButtonCombo.addItem( co.getName() );
                    bButtonCombo.addItem( co.getName() );
                }
            }
        }
    }//GEN-LAST:event_connectButtonActionPerformed

    public static void main(String[] args){
        GamepadSwitchingForm gsf = new GamepadSwitchingForm(null);
        gsf.setLocationRelativeTo(null);
        gsf.setVisible(true);
    }
    
    void updateComponentsValue(){
        if (currentController != null){
            currentController.poll();
        }
        final float[] vs = new float[N_COMP];
        
        for(int i = 0; i < N_COMP; ++i){
            final int index = combos[i].getSelectedIndex();
            
            //is the component valid
            if (index < 0 || index >= components.size()){
                continue;
            }
            final Component c = components.get(index);
            if (c == null) { continue; }

            //get raw value: update it and the bar
            final float rawValue = c.getPollData();
            rawValues[i].setText( rawValue + "" );
            progressBars[i].setValue( (int) FastMath.linearMix(0, 100, CONTROLLER_MIN, CONTROLLER_MAX, rawValue) );
           
            //scale with min, max
            final float min = Parse.stringToFloat( mins[i].getText() );
            final float max = Parse.stringToFloat( maxs[i].getText() );
            vs[i] = FastMath.linearMix(min, max, CONTROLLER_MIN, CONTROLLER_MAX, rawValue);

            //update value
            values[i].setText( vs[i] + "");
        }
        
        if(sendEveryCheck.isSelected() && mf != null){
            ++stepsSinceSending;
            if (stepsSinceSending > Parse.stringToInt( sendEveryText.getText())){
                stepsSinceSending = 0;
                
                if (maxChangeCheck.isSelected()){
                    final float minChange = Parse.stringToFloat( maxChangeText.getText() );
                    boolean shouldSend = false;
                    for(int i = 0; i < N_COMP; ++i){
                        if (FastMath.abs( vs[i] - componentLastValue[i] ) > minChange){
                            shouldSend = true;
                            break;
                        }
                    }
                    if (!shouldSend) { return; }
                }
                
                mf.transControlPanel.sendSwitchValues( (int) vs[0], (int) vs[1]);
                
                for(int i = 0; i < N_COMP; ++i){
                    componentLastValue[i] = vs[i];
                }
            }
        }
    }
    
    void tick(){
        updateComponentsValue();
        
        final int sleepMs = Parse.stringToInt( updateText.getText() );
        try {
            Thread.sleep( sleepMs );
        } catch (InterruptedException ex) {
     
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar aBar;
    private javax.swing.JComboBox aButtonCombo;
    private javax.swing.JCheckBox aCheck;
    private javax.swing.JTextField aCurrentText;
    private javax.swing.JTextField aMaxText;
    private javax.swing.JTextField aMinText;
    private javax.swing.JTextField aValueText;
    private javax.swing.JProgressBar bBar;
    private javax.swing.JComboBox bButtonCombo;
    private javax.swing.JCheckBox bCheck;
    private javax.swing.JTextField bCurrentText;
    private javax.swing.JTextField bMaxText;
    private javax.swing.JTextField bMinText;
    private javax.swing.JTextField bValueText;
    private javax.swing.JButton connectButton;
    private javax.swing.JComboBox controllerCombo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JCheckBox maxChangeCheck;
    private javax.swing.JTextField maxChangeText;
    private javax.swing.JCheckBox sendEveryCheck;
    private javax.swing.JTextField sendEveryText;
    private javax.swing.JTextField updateText;
    // End of variables declaration//GEN-END:variables
}
