package main.behavior;

import main.model.Boid;
import main.simulation.Forces;
import main.simulation.Vector2D;

import java.util.List;

public class FlockBehavior implements BehaviorStrategy {

    @Override
    public Forces calculateForces(Boid boid, List<Boid> neighbors) {
        if (neighbors.isEmpty()) {
            return new Forces();
        }

        FlockWeights weights = getFlockWeights();

        Vector2D separation = calculateSeparation(boid, neighbors, weights);
        Vector2D alignment = calculateAlignment(boid, neighbors, weights);
        Vector2D cohesion = calculateCohesion(boid, neighbors, weights);

        return new Forces(separation, alignment, cohesion);
    }

    public double distanceTo(Boid boid, Boid other) {
        double dx = boid.getX() - other.getX();
        double dy = boid.getY() - other.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public FlockWeights getFlockWeights() {
        return FlockWeights.standard();
    }

    private Vector2D calculateSeparation(Boid boid, List<Boid> neighbors, FlockWeights weights) {
        double steerX = 0, steerY = 0;
        int count = 0;

        for (Boid neighbor : neighbors) {
            double distance = distanceTo(boid, neighbor);
            if (distance > 0 && distance < 25) {
                double diffX = boid.getX() - neighbor.getX();
                double diffY = boid.getY() - neighbor.getY();

                diffX /= distance;
                diffY /= distance;

                steerX += diffX;
                steerY += diffY;
                count++;
            }
        }

        if (count > 0) {
            steerX /= count;
            steerY /= count;

            double magnitude = Math.sqrt(steerX * steerX + steerY * steerY);
            if (magnitude > 0) {
                steerX = (steerX / magnitude) * 2.0;
                steerY = (steerY / magnitude) * 2.0;

                steerX -= boid.getVx();
                steerY -= boid.getVy();

                double force = Math.sqrt(steerX * steerX + steerY * steerY);
                if (force > 0.03) {
                    steerX = (steerX / force) * 0.03;
                    steerY = (steerY / force) * 0.03;
                }
            }
        }

        return new Vector2D(steerX * weights.separation(), steerY * weights.separation());
    }

    private Vector2D calculateAlignment(Boid boid, List<Boid> neighbors, FlockWeights weights) {
        double avgVx = 0, avgVy = 0;
        int count = 0;

        for (Boid neighbor : neighbors) {
            double distance = distanceTo(boid,neighbor);
            if (distance > 0 && distance < 50) {
                avgVx += neighbor.getVx();
                avgVy += neighbor.getVy();
                count++;
            }
        }

        if (count > 0) {
            avgVx /= count;
            avgVy /= count;

            double magnitude = Math.sqrt(avgVx * avgVx + avgVy * avgVy);
            if (magnitude > 0) {
                avgVx = (avgVx / magnitude) * 2.0;
                avgVy = (avgVy / magnitude) * 2.0;

                double steerX = avgVx - boid.getVx();
                double steerY = avgVy - boid.getVy();

                double force = Math.sqrt(steerX * steerX + steerY * steerY);
                if (force > 0.03) {
                    steerX = (steerX / force) * 0.03;
                    steerY = (steerY / force) * 0.03;
                }

                return new Vector2D(steerX * weights.alignment(), steerY * weights.alignment());
            }
        }

        return Vector2D.ZERO;
    }

    private Vector2D calculateCohesion(Boid boid, List<Boid> neighbors, FlockWeights weights) {
        double centerX = 0, centerY = 0;
        int count = 0;

        for (Boid neighbor : neighbors) {
            double distance = distanceTo(boid, neighbor);
            if (distance > 0 && distance < 50) {
                centerX += neighbor.getX();
                centerY += neighbor.getY();
                count++;
            }
        }

        if (count > 0) {
            centerX /= count;
            centerY /= count;

            double steerX = centerX - boid.getX();
            double steerY = centerY - boid.getY();

            double magnitude = Math.sqrt(steerX * steerX + steerY * steerY);
            if (magnitude > 0) {
                steerX = (steerX / magnitude) * 2.0;
                steerY = (steerY / magnitude) * 2.0;

                steerX -= boid.getVx();
                steerY -= boid.getVy();

                double force = Math.sqrt(steerX * steerX + steerY * steerY);
                if (force > 0.03) {
                    steerX = (steerX / force) * 0.03;
                    steerY = (steerY / force) * 0.03;
                }

                return new Vector2D(steerX * weights.cohesion(), steerY * weights.cohesion());
            }
        }

        return Vector2D.ZERO;
    }
}
