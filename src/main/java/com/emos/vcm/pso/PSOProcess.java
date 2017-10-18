package com.emos.vcm.pso;

// this is the heart of the PSO program
// the code is for 2-dimensional space problem
// but you can easily modify it to solve higher dimensional space problem

import com.emos.vcm.model.Model;
import com.emos.vcm.util.MatrixProcess;
import org.ejml.simple.SimpleMatrix;

import java.util.Random;
import java.util.Vector;

public class PSOProcess implements PSOConstants {
    Random generator = new Random();
    private Vector<Particle> swarm = new Vector<Particle>();
    private double[] pBest = new double[SWARM_SIZE];
    private Vector<Location> pBestLocation = new Vector<Location>();
    private double gBest;
    private Location gBestLocation;
    private double[] fitnessValueList = new double[SWARM_SIZE];

    public void execute(Model model) {
        initializeSwarm(model);
        updateFitnessList(model);

        for (int i = 0; i < SWARM_SIZE; i++) {
            pBest[i] = fitnessValueList[i];
            pBestLocation.add(swarm.get(i).getLocation());
        }

        int t = 0;
        double w;
        double err = 9999;
//        && err > ProblemSet.ERR_TOLERANCE

        while (t < MAX_ITERATION) {
            // step 1 - update pBest
            for (int i = 0; i < SWARM_SIZE; i++) {
                if (fitnessValueList[i] < pBest[i]) {
                    pBest[i] = fitnessValueList[i];
                    pBestLocation.set(i, swarm.get(i).getLocation());
                }
            }

            // step 2 - update gBest
            int bestParticleIndex = PSOUtility.getMinPos(fitnessValueList);
            if (t == 0 || fitnessValueList[bestParticleIndex] < gBest) {
                gBest = fitnessValueList[bestParticleIndex];
                gBestLocation = swarm.get(bestParticleIndex).getLocation();
            }

            w = W_UPPERBOUND - (((double) t) / MAX_ITERATION) * (W_UPPERBOUND - W_LOWERBOUND);

            for (int i = 0; i < SWARM_SIZE; i++) {
                double r1 = generator.nextDouble();
                double r2 = generator.nextDouble();

                Particle p = swarm.get(i);

                // step 3 - update velocity
                double[] newVel = new double[model.getvNum() * model.getcNum()];
                for (int j = 0; j < newVel.length; j++) {
                    newVel[j] = (w * p.getVelocity().getPos()[i]) +
                            (r1 * C1) * (pBestLocation.get(i).getLoc()[i] - p.getLocation().getLoc()[i]) +
                            (r2 * C2) * (gBestLocation.getLoc()[i] - p.getLocation().getLoc()[i]);
                }
                Velocity vel = new Velocity(newVel);
                p.setVelocity(vel);

                // step 4 - update location
                double[] newLoc = new double[model.getvNum() * model.getcNum()];
                for (int j = 0; j < newLoc.length; j++) {
                    newLoc[i] = p.getLocation().getLoc()[i] + newVel[i];
                    if (newLoc[j] > 0.5) {
                        newLoc[j] = 1;
                    } else {
                        newLoc[j] = 0;
                    }
                }
                Location loc = new Location(newLoc);
                p.setLocation(loc);
            }

            err = ProblemSet.evaluate(gBestLocation, model) - 0; // minimizing the functions means it's getting closer to 0

            double[] gBestArray = gBestLocation.getLoc();
            SimpleMatrix gBestMatrix = MatrixProcess.oneArrayToMatrix(gBestArray, model.getvNum(), model.getcNum());
            System.out.println("ITERATION " + t + ": ");
            gBestMatrix.print();
            System.out.println("     Error: " + err);
            System.out.println("     Value: " + ProblemSet.evaluate(gBestLocation, model));
            System.out.println();

            t++;
            updateFitnessList(model);
        }

        double[] gBestArray = gBestLocation.getLoc();
        SimpleMatrix gBestMatrix = MatrixProcess.oneArrayToMatrix(gBestArray, model.getvNum(), model.getcNum());
        System.out.println("\nSolution found at iteration " + (t - 1) + ", the solutions is:");
        gBestMatrix.print();
        System.out.println("     Error: " + err);
        System.out.println("     Value: " + ProblemSet.evaluate(gBestLocation, model));
    }

    public void initializeSwarm(Model model) {
        Particle p;
        for (int i = 0; i < SWARM_SIZE; i++) {
            p = new Particle();

            // randomize location inside a space defined in Problem Set
            double[] loc = new double[model.getvNum() * model.getcNum()];

            for (int j = 0; j < loc.length; j++) {
                loc[j] = ProblemSet.LOC_LOW + generator.nextDouble() * (ProblemSet.LOC_HIGH - ProblemSet.LOC_LOW);
                if (loc[j] > 0.5) {
                    loc[j] = 1;
                } else {
                    loc[j] = 0;
                }
            }

            Location location = new Location(loc);

            // randomize velocity in the range defined in Problem Set
            double[] vel = new double[model.getvNum() * model.getcNum()];

            for (int j = 0; j < vel.length; j++) {
                vel[j] = ProblemSet.VEL_LOW + generator.nextDouble() * (ProblemSet.VEL_HIGH - ProblemSet.VEL_LOW);
            }
            Velocity velocity = new Velocity(vel);

            p.setLocation(location);
            p.setVelocity(velocity);
            swarm.add(p);
        }
    }

    public void updateFitnessList(Model model) {
        for (int i = 0; i < SWARM_SIZE; i++) {
            fitnessValueList[i] = swarm.get(i).getFitnessValue(model);
        }
    }
}
