package main;

import main.simulation.FlockSimulation;
import main.model.BoidType;
import main.spatial.*;

public class Microbench {

    // Parametre til eksperimentet
    private static final int NUM_BOIDS = 2000;      // antal boids
    private static final int NUM_ITERATIONS = 500;  // iterationer der måles på
    private static final int WARMUP_ITER = 50;      // warmup runder
    private static final double RADIUS = 20;        // nabosøgningsradius

    public static void main(String[] args) {

        SpatialIndex[] spacialIndexes = new SpatialIndex[] {
                new NaiveSpatialIndex(),
                new KDTreeSpatialIndex(),
                new QuadTreeSpatialIndex(1000, 700),
                new SpatialHashIndex(1000, 700, 50)
        };

        String[] names = {
                "NaiveSpatialIndex",
                "KDTreeSpatialIndex",
                "QuadTreeSpatialIndex",
                "SpatialHashIndex"
        };

        System.out.println("Benchmark med " + NUM_BOIDS + " boids, radius = " + RADIUS);
        System.out.println("Warmup: " + WARMUP_ITER + " iterationer");
        System.out.println("Måling: " + NUM_ITERATIONS + " iterationer\n");

        for (int i = 0; i < spacialIndexes.length; i++) {
            runBenchmark(spacialIndexes[i], names[i]);
        }
    }

    private static void runBenchmark(SpatialIndex index, String name) {

        FlockSimulation sim = new FlockSimulation(1200, 800);
        sim.setNeighborRadius(RADIUS);
        sim.setSpatialIndex(index);

        sim.setBoidCount(NUM_BOIDS);

        // Warmup
        for (int i = 0; i < WARMUP_ITER; i++) {
            sim.update();
        }

        // Mål tid
        long start = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            sim.update();
        }
        long end = System.nanoTime();

        double avgMs = ((end - start) / 1_000_000.0) / NUM_ITERATIONS;

        System.out.printf("%-20s:\t%.4f ms per iteration\n", name, avgMs);
    }
}