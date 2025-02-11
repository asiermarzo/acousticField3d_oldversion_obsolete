/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.simulation;

import static acousticfield3d.gui.ExportPhasesForm.TRANS_PER_FILE;
import acousticfield3d.utils.Parse;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Asier
 */
public class AnimKeyFrame {
    public int number;
    HashMap<Transducer, TransState> transStates;
    public float duration;

    public AnimKeyFrame() {
        transStates = new HashMap<>();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    

    public HashMap<Transducer, TransState> getTransStates() {
        return transStates;
    }

    public void setTransStates(HashMap<Transducer, TransState> transStates) {
        this.transStates = transStates;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return number + " " + duration;
    }
    
    public void deleteTrans(Transducer t){
        transStates.remove(t);
    }
    
    public void deleteTrans(List<Transducer> trans){
        for(Transducer t : trans ){
            transStates.remove(t);
        }
    }
    
    public void snap(Simulation s){
        transStates.clear();
        for(Transducer t : s.transducers){
            TransState ts = new TransState();
            transStates.put(t, ts);
            ts.transducer = t;
            ts.snap();
        }
    }
    
    public void apply(Simulation s){
        for(Transducer t : transStates.keySet()){
            TransState ts = transStates.get(t);
            ts.apply();
        }
    }
    
    public void applyInter(Simulation s, AnimKeyFrame b, float p){
        for(Transducer t : transStates.keySet()){
            TransState tsA = transStates.get(t);
            TransState tsB = b.transStates.get(t);
            if (tsA != null && tsB != null){
                tsA.applyMixed(tsB, p);
            }else if (tsB != null){
                tsA.apply();
            }
            
        }
    }

    void addTrans(Transducer t) {
        TransState ts = new TransState();
        ts.transducer = t;
        ts.snap();
    }
    

    //generates the XMOS representation of the contained transducers putting 8 transducers per position of the byte array
    public int[][] generateTables(final int divs, final int amountOfTrans){
        final int transPerByte = 8;
        final int nBytes = amountOfTrans / transPerByte;
        final int phaseDisc = divs / 2;
        
        final int[][] data = new int[nBytes][divs];
        
        //iterate over transducers
        for(Transducer t: transStates.keySet()){
            final TransState state = transStates.get(t);
            if(state.getAmplitude() > 0.5f){ //if it is on
                final int n = t.getOrderNumber();
                if (n >= 0 && n < amountOfTrans){ //is it within range
                    final int phase = Transducer.calcXMOSPhase(state.getPhase(), state.getAmplitude(), phaseDisc, divs);
                    final int targetByte = n / transPerByte;
                    
                    final int value = 1 << (n - targetByte * transPerByte);
                    for (int i = 0; i < phaseDisc; ++i) {
                        data[targetByte][ (i + phase) % divs] |= value;
                    }
                }
            }
        }
        
        return data;
    }
}
