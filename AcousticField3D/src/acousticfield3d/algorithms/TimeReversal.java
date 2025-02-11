/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.algorithms;

import acousticfield3d.gui.AlgorithmsForm;
import acousticfield3d.gui.MainForm;
import acousticfield3d.math.FastMath;
import acousticfield3d.math.Vector3f;
import acousticfield3d.simulation.Simulation;
import acousticfield3d.simulation.Transducer;

/**
 *
 * @author Asier
 */
public class TimeReversal extends Algorithm{

    public TimeReversal(AlgorithmsForm form) {
        super(form);
    }

    
    @Override
    public void calc(MainForm mf, Simulation s) {
        if (controlPoints.isEmpty()){
            return;
        }
        final Vector3f target = controlPoints.get(0).getTransform().getTranslation();
        final float temperature = mf.simPanel.getTemperature();
        final float airSpeed = Simulation.getSoundSpeedInAir(temperature);  // m/s, sound speed in air
        for(Transducer t : s.getTransducers()){
            final float distance = target.distance( t.getTransform().getTranslation() );
            final float waveLength = airSpeed / t.getFrequency();
            final float targetPhase = (1.0f - FastMath.decPart(distance / waveLength) ) * 2.0f * FastMath.PI;
            t.setPhase( targetPhase / FastMath.PI );
        }
    }
}
