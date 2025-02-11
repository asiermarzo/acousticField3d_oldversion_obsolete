/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acousticfield3d;

import acousticfield3d.utils.SimpleInput;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author am14010
 */
public class LensCalculation extends javax.swing.JFrame implements SimpleInput.SimpleInputEventListener{
    final CustomPanel drawPanel;
    final SimpleInput si;
    
    public LensCalculation() {
        initComponents();
        drawPanel = (CustomPanel)jPanel1;
        
        si = new SimpleInput(false, 10, true, false, false,  new SimpleInput.SimpleInputEventListener() {
            @Override
            public void simpleInputEvent(int event, String component) {
                okPressed();
            }
        });
        
        si.putVar("Algorithm", Enum.class, "Lens Maker;Phase");
        si.putVar("Pixels/mm", Float.class, "10");
        si.putVar("Width", Float.class, "60");
        si.putVar("Height", Float.class, "5");
        si.putVar("N", Float.class, "1.95");
        si.putVar("Focus", Float.class, "40");
        si.putVar("Type", Enum.class, "Continuos;Fresnell;Pixel");
        si.putVar("Fresnel Cut", Float.class, "3");
        
        si.update();
        
        si.setLocationRelativeTo(this);
        si.setVisible(true);
    }

    @Override
    public void simpleInputEvent(int event, String component) {
        if(event == SimpleInput.SimpleInputEventListener.EVENT_OK){
            okPressed();
        }
    }

    class CustomPanel extends JPanel{
        final LensCalculation f;

        public CustomPanel(LensCalculation f) {
            this.f = f;
        }

        @Override
        public void paint(Graphics g) {
            f.doPaint(g);
        }
       
    }
    
    
    private void okPressed(){
        jPanel1.repaint();
    }
    
    private void doPaint(Graphics g){
        final int w = jPanel1.getWidth();
        final int h = jPanel1.getHeight();
        
        //clean background
        g.clearRect(0, 0, w, h);
        
        //get vars
        final int Algorithm = si.getEnum("Algorithm");
        final float Pixelsmm = si.getFloat("Pixels/mm");
        final float Width = si.getFloat("Width");
        final float Height = si.getFloat("Height");
        final float N = si.getFloat("N");
        final float Focus = si.getFloat("Focus");
        final int Type = si.getEnum("Type");
        final float FresnelCut = si.getFloat("Fresnel Cut");

        //iterate trough each xPixel
        
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new CustomPanel(this);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Lens maker");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 627, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 418, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LensCalculation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LensCalculation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LensCalculation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LensCalculation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LensCalculation().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
