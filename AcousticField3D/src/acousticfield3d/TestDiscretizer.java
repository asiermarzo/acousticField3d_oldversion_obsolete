/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d;

import acousticfield3d.math.FastMath;

/**
 *
 * @author Asier
 */
public class TestDiscretizer {
    public static void main(String[] args){
        for(int i = 0; i < 100; ++i){
            final float a = FastMath.random(-360 , 360 );
            final float b = FastMath.random(-360 , 360 );
            final float d = FastMath.angleDiff( FastMath.DEG_TO_RAD * a, FastMath.DEG_TO_RAD * b) * FastMath.RAD_TO_DEG;
            System.out.println(a + " " + b + " -- " + d);
        }
        
        /*
        for(int i = -20; i < 20; ++i){
            System.out.println("N " + i + " mod " + (i%10));
        }
        */
        /*
        // Gaussian dist
        for(int i = 0; i < 100; ++i){
            System.out.println("" + FastMath.randomGaussian(0, 1));
        }*/
        
        /*
        float disc = FastMath.PI / 10.f;
        for(int i = 0; i < 20; ++i){
            float n = FastMath.random(FastMath.PI * -2, FastMath.PI * 2);
            float d = FastMath.discretize(n, disc);
            System.out.println("N is " + n + " d is " + d + " and it is "  + (d/disc));
        }
                */
    }
}
