/*
 * Copyright (C) 2020 mijitr <MijitR.xyz>
 *
 * Dis How Ballers Do...
 */
package MijitGroup.Workspace.Math;

import MijitGroup.Workspace.Functions.VectorizeMe;

/**
 *
 * @author mijitr <MijitR.xyz>
 */
 public class Matrix implements Cloneable {
    
    private final MAJOR orientation;
    
    private final Vector[] space;
    
    private final int rowCount, colCount;
    
    public Matrix(final MAJOR orientation, final int rows, final int cols) {
        this(orientation, new double[rows][cols]);
    }
    
    //Row by Column input, the rest should be handled appropriately
    //Always test first using the print method
    public Matrix(final MAJOR orientation, final double[][] rawDat) {
        this.rowCount = rawDat.length; this.colCount = rawDat[0].length;
        this.orientation = orientation;
        switch(this.orientation) {
            default:
                throw new UnsupportedOperationException
                        ("Unknown vector type in matrix initialization");
            case ROW:
                space = new Vector[rawDat.length];
                for(int r = 0; r < rawDat.length; r ++) {
                    space[r] = new Vector(rawDat[r]);
                }
                break;
            case COLUMN:
                space = new Vector[rawDat[0].length];
                for(int c = 0; c < rawDat[0].length; c ++) {
                    space[c] = new Vector(rawDat.length);
                    for(int r = 0; r < rawDat.length; r ++) {
                        space[c].set(r, rawDat[r][c]);
                    }
                }
                break;        
        }
    }
    
    public Matrix(final Vector[] space) {
        this.rowCount = space.length;
        this.orientation = MAJOR.ROW;
        this.colCount = space[0].size();
        this.space = new Vector[space.length];
        for(int r = 0; r < rowCount; r ++) {
            this.space[r] = new Vector(space[r].size());
            this.space[r].copyFrom(space[r]);
        }
    }
    
    public final Matrix transpose() {
        final Matrix tranny;
        switch(orientation) {
            default:
                throw new UnsupportedOperationException("FOR THE LOVE OF GOD");
            case ROW:
                tranny = new Matrix(orientation, space[0].size(), space.length);
                break;
            case COLUMN:
                tranny = new Matrix(orientation, space.length, space[0].size());
                break;
        }

        for(int r = 0; r < space.length; r ++) {
            for(int c = 0; c < space[r].size(); c ++) {
                tranny.instance(c).set(r, this.instance(r).get(c));
            }
        } 
        return tranny;
    }
    
    public final Matrix flip() {
        final Matrix tranny;
        switch(orientation) {
            default:
                throw new UnsupportedOperationException("FUCKING STOP THAT");
            case ROW:
                tranny = new Matrix(MAJOR.COLUMN, space[0].size(), space.length);
                break;
            case COLUMN:
                tranny = new Matrix(MAJOR.ROW, space.length, space[0].size());
                break;
        }
        for(int i = 0; i < Math.max(space.length, tranny.space.length); i ++) {
            tranny.space[i].copyFrom(space[i]);
        }
        return tranny;
    }
    
    public final double hadamard(final Matrix a, final Matrix b) {
        if(a.orientation() != b.orientation()) {
            System.out.println("Misaligned Matrices in Hadamard op");
        }
        double sum =0d;
        for(int i = 0; i < Math.max(a.space.length, b.space.length); i ++) {
            sum += a.space[i].dot(b.space[i]);
        } return sum;
    }
    
    public final Matrix process(final Matrix inputField) {
        //if(this.orientation != MAJOR.COLUMN) {
        //    throw new UnsupportedOperationException("Weight Matrix orientation incorrect");
        //}=
        if(inputField.orientation() != MAJOR.ROW) {
            throw new UnsupportedOperationException("Input Field orientation incorrect");
        }
        final double[][] rawOutput;
        if(this.orientation != inputField.orientation) {
            rawOutput = new double[inputField.space.length][space.length];
            for(int r = 0; r < inputField.space.length; r ++) {
                for(int c = 0; c < space.length; c ++) {
                    rawOutput[r][c] = inputField.space[r].dot(space[c]);
                }
            }
        } else {
            rawOutput = new double[inputField.space.length][space[0].size()];
            for(int r = 0; r < rawOutput.length; r ++) {
                for(int c = 0; c < rawOutput[r].length; c ++) {
                    double sum = 0d;
                    for(int i = 0; i < vectorCount(); i ++) {
                        sum += space[i].get(c) * inputField.space[r].get(i);
                    }
                    rawOutput[r][c] = sum;
                }
            }
        }
        return new Matrix(MAJOR.ROW, rawOutput);
    }
    
    public final void scale(final double scalar) {
        for(int i = 0; i < space.length; i ++) {
            space[i].scale(scalar);
        }
    }
    
    public final Matrix matrixScale(final Matrix scalar) {
        for(int i = 0; i < Math.max(space.length, scalar.space.length); i ++) {
            space[i].vectorScale(scalar.space[i]);
        }
        return this;
    }
    
    public final Matrix trimColumn() {
        if(orientation == MAJOR.COLUMN) {
            throw new UnsupportedOperationException("Too simple, solve it on your end");
        } else {
            final Vector[] mySpace = new Vector[this.vectorCount()];
            for(int r = 0; r < mySpace.length; r ++) {
                mySpace[r] = this.instance(r).deleteBias();
            }
            return new Matrix(mySpace);
        }
    }
    
    public final void absorb(final Matrix kin) {
        if(this.orientation != kin.orientation()) {
            System.out.println("Risky business inside absorb method");
        }
        for(int i = 0; i < Math.max(space.length, kin.space.length); i ++) {
            space[i].elementAdd(kin.space[i]);
        }
    }
    
    public final void lose(final Matrix antiKin) {
        if(this.orientation != antiKin.orientation()) {
            switch(this.orientation) {
                case COLUMN:
                    for(int r = 0; r < antiKin.space.length; r ++) {
                        for(int c = 0; c < antiKin.space[r].size(); c ++) {
                            space[c].set(r, space[c].get(r) - antiKin.instance(r).get(c));
                        }
                    }
                    break;
                default:
                case ROW:
                    System.out.println("STOP BREAKING SHIT");
                    break;
            }
        } else {
            for(int i = 0; i < Math.max(space.length, antiKin.space.length); i ++) {
                space[i].elementSubtract(antiKin.space[i]);
            }
        }
    }
    
    public final void huberLose(final Matrix antiKin) {
        double dif;
        if(this.orientation != antiKin.orientation()) {
            switch(this.orientation) {
                case COLUMN:
                    for(int r = 0; r < antiKin.space.length; r ++) {
                        for(int c = 0; c < antiKin.space[r].size(); c ++) {
                            dif = Math.abs(space[c].get(r) - antiKin.instance(r).get(c));
                            space[c].set(r, 
                                    dif > 1d ? VectorizeMe.sign(space[c].get(r)-antiKin.instance(r).get(c)) :
                                            space[c].get(r)-antiKin.instance(r).get(c));
                        }
                    }
                    break;
                default:
                case ROW:
                    System.out.println("STOP BREAKING SHIT");
                    break;
            }
        } else {
            for(int i = 0; i < Math.max(space.length, antiKin.space.length); i ++) {
                space[i].huberSubtract(antiKin.space[i]);
            }
        }
    }
    
    public final void clip(final double clip) {
        for(final Vector v : space) {
            v.clip(clip);
        }
    }
    
    public final MAJOR orientation() {
        return this.orientation;
    }
    
    public final int vectorCount() {
        return space.length;
    }
    
    public final Vector instance(final int i) {
        return space[i];
    }
    
    public final double sumSquared() {
        double sum = 0d;
        for(final Vector v : space) {
            sum += v.sumSquared();
        }
        return sum;
    }
    
    public final void setDat(final double[][] dat) {
        switch(orientation) {
            case ROW:
                int h = Math.max(space.length, dat.length);
                for(int r = 0; r < h; r ++) {
                    final int w = Math.max(space[r].size(), dat[r].length);
                    for(int c = 0; c < w; c ++) {
                        space[r].set(c, dat[r][c]);
                    }
                }
                break;
            case COLUMN:
                final int w = Math.max(this.colCount, dat[0].length);
                for(int c = 0; c < w; c ++) {
                    h = Math.max(this.rowCount, dat.length);
                    for(int r = 0; r < h; r ++) {
                        space[c].set(r, dat[r][c]);
                    }
                }
                break;
        }
    }
    
    public final double[][] ripDat() {
        final double[][] builder;
       switch(orientation) {
           default:
                throw new UnsupportedOperationException("STOP TRYING TO MAKE SHIT DIFFERENT");
            case ROW:
                builder = new double[space.length][];
                for(int i = 0; i < space.length; i ++) {
                    builder[i] = space[i].ripDat();
                } break;
            case COLUMN:
                builder = new double[space[0].size()][];
                for(int c = 0; c < space[0].size(); c ++) {
                    builder[c] = new double[space.length];
                    for(int r = 0; r < space.length; r ++) {
                        builder[r][c] = (space[r].get(c));
                    } 
                } break;
        }
       return builder;
    }
    
    public final void print() {
        final StringBuilder builder
                = new StringBuilder(this.orientation.toString())
            .append("_MAJOR\n");
        switch(orientation) {
            case ROW:
                for(int i = 0; i < space.length; i ++) {
                    builder.append(space[i].toString()).append("\n");
                } break;
            case COLUMN:
                for(int c = 0; c < space[0].size(); c ++) {
                    builder.append("["); 
                    for(int r = 0; r < space.length; r ++) {
                        builder.append(space[r].get(c))
                                .append(r < space.length - 1
                                        ? ", " : "]\n");
                    } 
                } break;
        }
        System.out.println(builder.toString());
    }
    
    @Override
    public final Matrix clone() {
        //for future
        try {
            super.clone();
            final Matrix copy = new Matrix(this.orientation, rowCount, colCount);
            copy.absorb(this);
            return copy;
        } catch (final CloneNotSupportedException e) {
            throw new UnsupportedOperationException("Fuck this shit");
        }
    }
    
}
