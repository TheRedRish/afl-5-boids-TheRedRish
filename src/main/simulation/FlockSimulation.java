package main.simulation;

import main.behavior.*;
import main.model.Boid;
import main.model.BoidType;
import main.spatial.*;

import java.util.ArrayList;
import java.util.List;

public class FlockSimulation {

    private final List<Boid> boids;
    private SpatialIndex spatialIndex;
    private final int width;
    private final int height;

    private double neighborRadius = 50.0;
    private double lastIterationTimeMs = 0;

    private BehaviorStrategy behaviorFactory;

    private final List<Food> food = new ArrayList<>();

    private int foodSpawnAmount = 5;
    private int foodSpawnInterval = 3000; // ms
    private long lastFoodSpawnTime = 0;

    public FlockSimulation(int width, int height) {
        this.width = width;
        this.height = height;
        this.boids = new ArrayList<>();
        this.spatialIndex = new NaiveSpatialIndex();
        this.behaviorFactory = new FlockBehavior();
    }

    public void setFoodSpawnAmount(int amount) {
        this.foodSpawnAmount = amount;
    }

    public void setFoodSpawnInterval(int interval) {
        this.foodSpawnInterval = interval;
    }

    public void resetFoodSpawnClock() {
        this.lastFoodSpawnTime = System.currentTimeMillis();
    }

    public void setSpatialIndex(SpatialIndex spatialIndex) {
        this.spatialIndex = spatialIndex;
    }

    public void addBoid() {
        addBoid(BoidType.STANDARD);
    }

    public void addBoid(BoidType type) {
        int id = boids.size();
        double x = Math.random() * width;
        double y = Math.random() * height;
        boids.add(new Boid(id, x, y, type, this.behaviorFactory));
    }

    public void setBoidCount(int count) {
        while (boids.size() < count) {
            addBoid();
        }
        while (boids.size() > count) {
            boids.remove(boids.size() - 1);
        }

        for (int i = 0; i < boids.size(); i++) {
            Boid oldBoid = boids.get(i);
            boids.set(i, new Boid(
                    i,
                    oldBoid.getX(),
                    oldBoid.getY(),
                    oldBoid.getType(),
                    this.behaviorFactory
            ));
        }
    }

    public void update() {
        spatialIndex.clear();
        for (Boid boid : boids) {
            spatialIndex.insert(boid);
        }

        for (Boid boid : boids) {
            List<Boid> neighbors = spatialIndex.findNeighbors(boid, neighborRadius);
            boid.update(neighbors, width, height);
        }

        if (this.behaviorFactory instanceof ForageBehavior) {
            food.removeIf(Food::isEaten);

            long now = System.currentTimeMillis();
            if (now - lastFoodSpawnTime >= foodSpawnInterval) {
                spawnFoodRandom(foodSpawnAmount);
                lastFoodSpawnTime = now;
            }
        }
    }

    public void setBehavior(String name) {
        switch (name) {
            case "ForageBehavior":
                this.behaviorFactory = new ForageBehavior(food, 150, 10);
                break;

            default:
                this.behaviorFactory = new FlockBehavior();
                break;
        }

        for (Boid b : boids) {
            b.setBehavior(behaviorFactory);
        }
    }

    public void spawnFoodRandom(int amount) {
        for (int i = 0; i < amount; i++) {
            double x = Math.random() * width;
            double y = Math.random() * height;
            food.add(new Food(x, y));
        }
    }

    public List<Boid> getBoids() {
        return boids;
    }

    public String getSpatialIndexName() {
        return spatialIndex.getName();
    }

    public double getLastIterationTimeMs() {
        return lastIterationTimeMs;
    }

    public void setNeighborRadius(double radius) {
        this.neighborRadius = radius;
    }

    public double getNeighborRadius() {
        return neighborRadius;
    }

    public List<Food> getFood() {
        return food;
    }

    public int getCountByType(BoidType type) {
        return (int) boids.stream().filter(b -> b.getType() == type).count();
    }
}
