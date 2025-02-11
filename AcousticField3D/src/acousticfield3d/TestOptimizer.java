/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d;

import acousticfield3d.algorithms.bfgs.BFGS;
import acousticfield3d.algorithms.bfgs.IFunction;
import acousticfield3d.math.FastMath;

/**
 *
 * @author Asier
 */
public class TestOptimizer {

    
    public static void main(String[] args){
        TestOptimizer dump = new TestOptimizer();
    }
    
    public TestOptimizer(){
        BFGS bfgs = new BFGS();
        
        System.out.println("One var function");
        IFunction oneVar = new OneVar();
        double[] initial = new double[1];
        double[] result = new double[1];
        for(int i = 0; i < 10; ++i){
            initial[0] = result[0] = FastMath.random(-100, 100);
            double fx = bfgs.minimize(oneVar, initial, result);
            System.out.format("x = %f, fx = %f, start = %f, iters = %d",
                    result[0], fx, initial[0], bfgs.getLastNumberOfIterations());
            System.out.println();
        }
        
        System.out.println("Two var function");
        oneVar = new TwoVars();
        initial = new double[2];
        result = new double[2];
        for(int i = 0; i < 10; ++i){
            initial[0] = result[0] = FastMath.random(-100, 100);
            initial[1] = result[1] = FastMath.random(-100, 100);
            double fx = bfgs.minimize(oneVar, initial, result);
            System.out.format("x = %f %f, fx = %f, start = %f %f, iters = %d",
                    result[0], result[1], 
                    fx, 
                    initial[0], initial[1],
                    bfgs.getLastNumberOfIterations());
            System.out.println();
        }
    }
    
    // y = x^2
    public class OneVar implements IFunction{
        @Override
        public int getDimensions() {
            return 1;
        }

        @Override
        public double evaluate(double[] vars) {
            final double x = vars[0];
            return x*x;
        }

        @Override
        public void gradient(double[] vars, double[] g) {
            final double x = vars[0];
            g[0] = 2.0 * x;
        }  
    }
        
    //z = x^2 + y^2
     public class TwoVars implements IFunction{
        @Override
        public int getDimensions() {
            return 2;
        }

        @Override
        public double evaluate(double[] vars) {
            final double x = vars[0];
            final double y = vars[1];
            return x*x + y*y;
        }

        @Override
        public void gradient(double[] vars, double[] g) {
            g[0] = 2.0 * vars[0];
            g[1] = 2.0 * vars[1];
        }  
    }   
}
