/*
 * Copyright (C) 2020 mijitr <MijitR.xyz>
 *
 * Dis How Ballers Do...
 */
package MijitGroup.Workspace.Functions;

import MijitGroup.Workspace.Math.Matrix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mijitr <MijitR.xyz>
 */
public abstract class VectorizeMe {
    
    public static final double sign(final double input) {
        return Double.compare(input, 0d) >= 0d ? 1d : -1d;
    }
    
    public static final List<Integer> shuffle(final double[][] array) {
        final double[] temp = new double[array[0].length];
        final List<Integer> toMix = new ArrayList<>(array.length);
        for(int i = 0; i < array.length; i ++) {
            toMix.add(i);
        } Collections.shuffle(toMix);
        int abs = 0;
        final Iterator<Integer> randIt = toMix.iterator();
        while(randIt.hasNext() && abs < toMix.size()) {
            final int next = randIt.next();
            if(next != abs) {
                System.arraycopy(array[abs], 0, temp, 0, array[abs].length);
                System.arraycopy(array[next], 0, array[abs], 0, array[abs].length);
                System.arraycopy(temp, 0, array[next], 0, temp.length);
            } abs ++;
        }
        return toMix;
    }
    
    public static final void mixRows(final List<Integer> pattern, final double[][] array) {
        final double[][] ghost = new double[array.length][];
        for(int r = 0; r < array.length; r ++) {
            ghost[r] = Arrays.copyOf(array[r], array[r].length);
        }
        
        int runner = 0;
        for(final int index : pattern) {
            System.arraycopy(ghost[index], 0, array[runner], 0, ghost[index].length);
            runner ++;
        }
    }
    
}
