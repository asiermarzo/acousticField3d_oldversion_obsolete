/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d;

import acousticfield3d.math.FastMath;
import acousticfield3d.utils.FileUtils;
import acousticfield3d.utils.Parse;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Asier
 */
public class DirectivityCalc extends javax.swing.JFrame {
    BufferedImage bi;
    int iw, ih;
    
    float distPerAtt = -0.02f;
    int paintFunction = -1;
    
    
    public DirectivityCalc() {
        bi = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        initComponents();
        updateImg();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new CustomPanel(this);
        jPanel2 = new javax.swing.JPanel();
        loadBackgroundImage = new javax.swing.JButton();
        kSincText = new javax.swing.JTextField();
        sincButton = new javax.swing.JButton();
        aLinearText = new javax.swing.JTextField();
        bLinearText = new javax.swing.JTextField();
        linearButton = new javax.swing.JButton();
        setAttButton = new javax.swing.JButton();
        attText = new javax.swing.JTextField();
        wSincText = new javax.swing.JTextField();
        cubicButton = new javax.swing.JButton();
        bCubicText = new javax.swing.JTextField();
        aCubicText = new javax.swing.JTextField();
        cCubicText = new javax.swing.JTextField();
        fitBessButton = new javax.swing.JButton();
        invSquareButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Directivity test");

        panel.setBackground(new java.awt.Color(255, 255, 255));
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 311, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        loadBackgroundImage.setText("Back img");
        loadBackgroundImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBackgroundImageActionPerformed(evt);
            }
        });

        kSincText.setText("725.5411");

        sincButton.setText("FitSinc");
        sincButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sincButtonActionPerformed(evt);
            }
        });

        aLinearText.setText("1");

        bLinearText.setText("-0.44");

        linearButton.setText("Fit linear sin");
        linearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linearButtonActionPerformed(evt);
            }
        });

        setAttButton.setText("Set att");

        attText.setText("-30");

        wSincText.setText("0.0077");

        cubicButton.setText("Fit s sin");
        cubicButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cubicButtonActionPerformed(evt);
            }
        });

        bCubicText.setText("-0.25");

        aCubicText.setText("1");

        cCubicText.setText("-0.6");

        fitBessButton.setText("FitBess");
        fitBessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fitBessButtonActionPerformed(evt);
            }
        });

        invSquareButton.setText("Fit i sin");
        invSquareButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invSquareButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(kSincText)
                    .addComponent(wSincText)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(invSquareButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cubicButton))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(attText, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(aLinearText, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bLinearText, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(loadBackgroundImage, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(setAttButton)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(aCubicText, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bCubicText, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cCubicText, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(linearButton, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(fitBessButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sincButton)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loadBackgroundImage)
                .addGap(18, 18, 18)
                .addComponent(kSincText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wSincText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sincButton)
                    .addComponent(fitBessButton))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aLinearText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bLinearText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(linearButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aCubicText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bCubicText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cCubicText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cubicButton)
                    .addComponent(invSquareButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setAttButton)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loadBackgroundImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadBackgroundImageActionPerformed
        String file = FileUtils.selectFile(this, "open", "png", null);
        //String file = "C:/Users/Asier/Desktop/transDirectivity.png";
        if(file != null){
            try {
                bi = ImageIO.read(new File(file));
                updateImg();
                repaint();
            } catch (IOException ex) {
                Logger.getLogger(DirectivityCalc.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_loadBackgroundImageActionPerformed

    private void updateImg(){
        iw = bi.getWidth();
        ih = bi.getHeight();
    }
    
    private void sincButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sincButtonActionPerformed
       
        paintFunction = 1;
        repaint();
    }//GEN-LAST:event_sincButtonActionPerformed

    private void linearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linearButtonActionPerformed
       
        paintFunction = 2;
        repaint();
    }//GEN-LAST:event_linearButtonActionPerformed

    private void panelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMouseClicked
        final float attLevel = Parse.stringToFloat( attText.getText() ); 
        final float y = evt.getY() / (float)(ih-1);
        
        distPerAtt = y / attLevel;
    }//GEN-LAST:event_panelMouseClicked

    private void cubicButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cubicButtonActionPerformed
        paintFunction = 3;
        repaint();
    }//GEN-LAST:event_cubicButtonActionPerformed

    private void fitBessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fitBessButtonActionPerformed
        paintFunction = 4;
        repaint();
    }//GEN-LAST:event_fitBessButtonActionPerformed

    private void invSquareButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invSquareButtonActionPerformed
        paintFunction = 5;
        repaint();
    }//GEN-LAST:event_invSquareButtonActionPerformed


    private void paintInPanel(Graphics g) {
        g.drawImage(bi, 0, 0, null);
        g.setColor( Color.RED );
        final float angleStep = 0.1f * FastMath.DEG_TO_RAD;
        
        float lx = 0.0f, ly = 0.0f;
        //draw the function
        if (paintFunction == 1){
            final float w = Parse.stringToFloat( wSincText.getText() );
            final float k = Parse.stringToFloat( kSincText.getText() );
            
            for(float angle = 0.0f; angle < FastMath.HALF_PI; angle += angleStep){
                
                final float ampAtt = FastMath.sinc( 0.5f * k * w * FastMath.sin(angle));
                
                final float db = 20.0f * FastMath.log10( ampAtt );
                final float distance = 1.0f - (db * distPerAtt);
                
                final float px = iw * distance * FastMath.cos( FastMath.HALF_PI- angle ) ;
                final float py = ih * (1.0f - (distance * FastMath.sin( FastMath.HALF_PI -  angle ) ));
                g.drawLine((int) px, (int) py, (int) lx, (int) ly);
                lx = px;
                ly = py;
            }
        }else if (paintFunction == 2){
            final float a = Parse.stringToFloat( aLinearText.getText() );
            final float b = Parse.stringToFloat( bLinearText.getText() );
            
            for(float angle = 0.0f; angle < FastMath.HALF_PI; angle += angleStep){
                
                final float ampAtt = a + b * FastMath.sin(angle);
                
                final float db = 20.0f * FastMath.log10( ampAtt );
                final float distance = 1.0f - (db * distPerAtt);
                
                final float px = iw * distance * FastMath.cos( FastMath.HALF_PI- angle ) ;
                final float py = ih * (1.0f - (distance * FastMath.sin( FastMath.HALF_PI -  angle ) ));
                g.drawLine((int) px, (int) py, (int) lx, (int) ly);
                lx = px;
                ly = py;
            }
        }else if (paintFunction == 3){
            final float a = Parse.stringToFloat( aCubicText.getText() );
            final float b = Parse.stringToFloat( bCubicText.getText() );
            final float c = Parse.stringToFloat( cCubicText.getText() );
            
            for(float angle = 0.0f; angle < FastMath.HALF_PI; angle += angleStep){
                final float sin = FastMath.sin(angle);
                final float ampAtt = a + b * sin + c*sin*sin;
                
                final float db = 20.0f * FastMath.log10( ampAtt );
                final float distance = 1.0f - (db * distPerAtt);
                
                final float px = iw * distance * FastMath.cos( FastMath.HALF_PI- angle ) ;
                final float py = ih * (1.0f - (distance * FastMath.sin( FastMath.HALF_PI -  angle ) ));
                g.drawLine((int) px, (int) py, (int) lx, (int) ly);
                lx = px;
                ly = py;
            }
        }else if (paintFunction == 4){
            final float w = Parse.stringToFloat( wSincText.getText() );
            final float k = Parse.stringToFloat( kSincText.getText() );
            
            for(float angle = 0.0f; angle < FastMath.HALF_PI; angle += angleStep){
                
                final float dum = k * (w/2.0f) * FastMath.sin(angle);
                final float ampAtt = 2.0f * (float)FastMath.j1(dum) / dum;
                
                final float db = 20.0f * FastMath.log10( ampAtt );
                final float distance = 1.0f - (db * distPerAtt);
                
                final float px = iw * distance * FastMath.cos( FastMath.HALF_PI- angle ) ;
                final float py = ih * (1.0f - (distance * FastMath.sin( FastMath.HALF_PI -  angle ) ));
                g.drawLine((int) px, (int) py, (int) lx, (int) ly);
                lx = px;
                ly = py;
            }
        }else if (paintFunction == 5){
            final float a = Parse.stringToFloat( aCubicText.getText() );
            final float b = Parse.stringToFloat( bCubicText.getText() );
            final float c = Parse.stringToFloat( cCubicText.getText() );
            
            for(float angle = 0.0f; angle < FastMath.HALF_PI; angle += angleStep){
                final float sin = FastMath.sin(angle);
                final float ampAtt = 1.0f / (1.0f + b * sin + c*sin*sin);
                
                final float db = 20.0f * FastMath.log10( ampAtt );
                final float distance = 1.0f - (db * distPerAtt);
                
                final float px = iw * distance * FastMath.cos( FastMath.HALF_PI- angle ) ;
                final float py = ih * (1.0f - (distance * FastMath.sin( FastMath.HALF_PI -  angle ) ));
                g.drawLine((int) px, (int) py, (int) lx, (int) ly);
                lx = px;
                ly = py;
            }
        }
        
    }
    
    
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
            java.util.logging.Logger.getLogger(DirectivityCalc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DirectivityCalc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DirectivityCalc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DirectivityCalc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DirectivityCalc().setVisible(true);
            }
        });
    }

    
    
    
    public class CustomPanel extends JPanel{
        private DirectivityCalc father;

        public CustomPanel(DirectivityCalc father) {
            this.father = father;
        }

        @Override
        public void paint(Graphics g) {
            father.paintInPanel(g);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField aCubicText;
    private javax.swing.JTextField aLinearText;
    private javax.swing.JTextField attText;
    private javax.swing.JTextField bCubicText;
    private javax.swing.JTextField bLinearText;
    private javax.swing.JTextField cCubicText;
    private javax.swing.JButton cubicButton;
    private javax.swing.JButton fitBessButton;
    private javax.swing.JButton invSquareButton;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField kSincText;
    private javax.swing.JButton linearButton;
    private javax.swing.JButton loadBackgroundImage;
    private javax.swing.JPanel panel;
    private javax.swing.JButton setAttButton;
    private javax.swing.JButton sincButton;
    private javax.swing.JTextField wSincText;
    // End of variables declaration//GEN-END:variables
}
