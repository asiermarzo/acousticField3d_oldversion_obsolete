/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.utils;

import java.util.ArrayList;

/**
 *
 * @author Asier
 */
public class Parse {
    public static float stringToFloat(String text) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return Float.NaN;
        }
    }

    public static int stringToInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static double stringToDouble(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public static ArrayList<Float> parseFloats(String text, String separator){
        String[] ss = text.split(separator);
        ArrayList<Float> v = new ArrayList<>();
        for(String s : ss){
            v.add( stringToFloat(s) );
        }
        return v;
    }
    
    public static String printFloats(ArrayList<Float> floats){
        StringBuilder sb = new StringBuilder();
        for(Float f : floats){
            sb.append(f.toString() + "\n");
        }
        return sb.toString();
    }
    
    public static String printIntArray(int[] array){
        StringBuilder sb = new StringBuilder();
        final int size = array.length;
        for(int i = 0; i < size; ++i){
            sb.append(array[i] + "\n");
        }
        return sb.toString();
    }
    
    public static int[] parseIntArray(String text){
        String[] ss = text.split("\\n");
        final int size = ss.length;
        int[] v = new int[size];
        for(int i = 0; i < size; ++i){
            v[i] = Parse.stringToInt(ss[i]);
        }
        return v;
    }
}
