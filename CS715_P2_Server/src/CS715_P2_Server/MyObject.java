/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CS715_P2_Server;

/**
 *
 * @author hke
 */
import java.io.Serializable;
import java.util.Date;
import java.util.Random;

// Serializable so can use ObjectStream with objects that extend MyObject
public abstract class MyObject extends Object implements Serializable {

// Comment this out if you get tired of seeing it every time you run a program.
    static {
        System.out.println("Java version=" + System.getProperty("java.version")
                + ", Java vendor=" + System.getProperty("java.vendor")
                + "\nOS name=" + System.getProperty("os.name")
                + ", OS arch=" + System.getProperty("os.arch")
                + ", OS version=" + System.getProperty("os.version")
                + "\n" + new Date());
    }
    /*
     * Windows 95 time-slices threads but Solaris does not as of JDK 1.1.
     * To enable time slicing on Solaris, set the boolean variable
     * timeSlicingEnsured to true or call ensureTimeSlicing().
     */
    private static final String OSname = System.getProperty("os.name");

    private static final long startTime = System.currentTimeMillis();
    private static final Random rnd = new Random();

    private String name = "MyObject";

    protected MyObject() {
        super();
    }   // this class is designed
    // to be extended only

    protected MyObject(String name) {
        super();
        this.name = name;
    }

    protected static final String getOSname() {
        return OSname;
    }

    protected final String getName() {
        return name;
    }

    protected static final String getThreadName() {
        return Thread.currentThread().getName();
    }

    protected static final long age() {
        return System.currentTimeMillis() - startTime;
    }

    protected static final int nap(int napTimeMS) {
        /*
         * Compute how much the actual sleep exceeded the request and return that
         * to the user.  If the sleep was interrupted, then the computation will
         * be negative.  Let the user decide what to do.
         */
        long napStart = age();
        try {
            Thread.sleep(napTimeMS);
        } catch (InterruptedException e) {
//       System.err.println("interrupted out of sleep");
        }
        return (int) (age() - napStart - (long) napTimeMS);
    }

    // returns double in range [0, 1)
    protected static final double random() {
        return rnd.nextDouble();
    }

    // returns double in range [0, ub)
    protected static final double random(int ub) {
        return rnd.nextDouble() * ub;
    }

    // returns double in range [lb, ub)
    protected static final double random(int lb, int ub) {
        return lb + rnd.nextDouble() * (ub - lb);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Copyright (c) 1997 Stephen J. Hartley. All Rights Reserved.");
        throw new InstantiationException();  // only extend this class
    }
}
