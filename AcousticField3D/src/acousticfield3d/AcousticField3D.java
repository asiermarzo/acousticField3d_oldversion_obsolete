/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d;

import acousticfield3d.gui.MainForm;
import acousticfield3d.utils.Parse;

/**
 *
 * @author Asier
 */
public class AcousticField3D {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainForm t = new MainForm();
        t.setVisible(true);
        t.setLocationRelativeTo(null);
        
        if (args.length > 0){
            for(String s : args){
                char firstChar = s.charAt(0);
                s = s.substring(1);
                
                if(firstChar == 's'){ //autoload simulation s file
                    t.loadSimulation( s );
                }else if(firstChar == 'c'){ //auto connect c[s|u]number
                    final boolean serial = s.charAt(0) == 's';
                    s = s.substring(1);
                    final int portNumber = Parse.stringToInt( s );
                    t.transControlPanel.connect(serial, portNumber);
                }else if(firstChar == 'b'){ //autoselect first bead b
                    t.movePanel.selectFirstBead();
                }else if(firstChar == 'a'){ //algorithm to employ a0123
                    final int algNumber = Parse.stringToInt( s );
                    t.algorithmsForm.selectDefaultAlg( algNumber );
                }
               
            }
        }
    }
    
}
