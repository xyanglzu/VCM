package com.emos.vcm.pso;

// this is the heart of the PSO program
// the code is for 2-dimensional space problem
// but you can easily modify it to solve higher dimensional space problem

import com.emos.vcm.model.Model;

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

    public double[] execute(Model model) {
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
                double[] newVel = new double[model.getcNum()];
                for (int j = 0; j < newVel.length; j++) {
                    newVel[j] = (w * p.getVelocity().getPos()[j]) +
                            (r1 * C1) * (pBestLocation.get(i).getLoc()[j] - p.getLocation().getLoc()[j]) +
                            (r2 * C2) * (gBestLocation.getLoc()[j] - p.getLocation().getLoc()[j]);
                }
                Velocity vel = new Velocity(newVel);
                p.setVelocity(vel);

                // step 4 - update location
                double[] newLoc = new double[model.getcNum()];
                for (int j = 0; j < newLoc.length; j++) {
                    newLoc[j] = p.getLocation().getLoc()[j] + newVel[j];
                    if (newLoc[j] > model.getvNum()) {
                        newLoc[j] = model.getvNum();
                    }
                    if (newLoc[j] < 0) {
                        newLoc[j] = 0;
                    }
                }
                Location loc = new Location(newLoc);
                p.setLocation(loc);
            }

            err = ProblemSet.evaluate(gBestLocation, model) - 0; // minimizing the functions means it's getting closer to 0

            double[] gBestArray = gBestLocation.getLoc();
            System.out.println("ITERATION " + t + ": ");
            for (int i = 0; i < gBestArray.length; i++) {
                System.out.print((int) gBestArray[i] + " ");
            }
            System.out.println();
            ProblemSet.printParameter(gBestLocation, model);
            System.out.println("     Error: " + err);
            System.out.println("     Value: " + ProblemSet.evaluate(gBestLocation, model));
            System.out.println();

            t++;
            updateFitnessList(model);

//            if ((t >= 10 && Math.abs(gBest - fitnessValueList[t-10]) >= ProblemSet.ERR_TOLERANCE)) {
//                break;
//            }
        }

        // 最优结果
        double[] gBestArray = gBestLocation.getLoc();
        System.out.println("\nSolution found at iteration " + (t - 1) + ", the solutions is:");
        int index = 0;
        for (index = 0; index < gBestArray.length - 1; index++) {
            System.out.print((int) gBestArray[index] + " ,");
        }
        System.out.println((int) gBestArray[index]);
        ProblemSet.printParameter(gBestLocation, model);
        System.out.println("     Error: " + err);
        System.out.println("     Value: " + ProblemSet.evaluate(gBestLocation, model));
        return gBestArray;
    }

    public void initializeSwarm(Model model) {
        Particle p;
        for (int i = 0; i < SWARM_SIZE; i++) {
            p = new Particle();

            // randomize location inside a space defined in Problem Set
            double[] loc = new double[model.getcNum()];

            for (int j = 0; j < loc.length; j++) {
                loc[j] = 0 + generator.nextDouble() * (model.getvNum() + 1 - 0);
            }

            Location location = new Location(loc);

            // randomize velocity in the range defined in Problem Set
            double[] vel = new double[model.getcNum()];

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
