/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.algorithms;

import acousticfield3d.gui.AlgorithmsForm;
import acousticfield3d.gui.MainForm;
import acousticfield3d.scene.MeshEntity;
import acousticfield3d.simulation.Simulation;
import java.util.ArrayList;

/**
 *
 * @author Asier
 */
public abstract class Algorithm {
    public final ArrayList<MeshEntity> controlPoints;
    public final AlgorithmsForm form;
    
    public Algorithm(AlgorithmsForm form) {
        this.form = form;
        controlPoints = new ArrayList<>();
    }
    
    public abstract void calc(MainForm mf, Simulation s);
}
