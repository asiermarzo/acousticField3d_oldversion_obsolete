/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.simulation;

import acousticfield3d.math.FastMath;

/**
 *
 * @author Asier
 */
public class TransState {
    Transducer transducer;
    float amplitude, phase;

    public TransState() {
    }
    
    public Transducer getTransducer() {
        return transducer;
    }

    public void setTransducer(Transducer transducer) {
        this.transducer = transducer;
    }

    public float getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    public float getPhase() {
        return phase;
    }

    public void setPhase(float phase) {
        this.phase = phase;
    }
    
    void apply() {
        transducer.pAmplitude = (float)amplitude;
        transducer.phase = (float)phase;
    }

    void applyMixed(TransState tsB, float p) {
        transducer.pAmplitude = FastMath.lerp(amplitude, tsB.amplitude, p);
        transducer.phase = FastMath.lerp(phase, tsB.phase, p);
    }

    void snap() {
        amplitude = transducer.pAmplitude;
        phase = transducer.phase;
    }
}
