package main.behavior;

import main.model.Boid;
import main.simulation.Forces;
import main.simulation.Vector2D;

import java.util.List;

public class ForageBehavior extends FlockBehavior {

    private final List<Food> foodSources;
    private final double attractionRadius;
    private final double eatRadius;

    public ForageBehavior(List<Food> foodSources, double attractionRadius, double eatRadius) {
        this.foodSources = foodSources;
        this.attractionRadius = attractionRadius;
        this.eatRadius = eatRadius;
    }

    @Override
    public Forces calculateForces(Boid boid, List<Boid> neighbors) {

        // Først: normal flokadfærd
        Forces flockForces = super.calculateForces(boid, neighbors);

        // Dernæst: fødesøgning
        Vector2D forageForce = calculateForageForce(boid);

        Vector2D newSeparation = new Vector2D(
                flockForces.separation().x() + forageForce.x(),
                flockForces.separation().y() + forageForce.y()
        );

        // Summér kræfter (Forces er en container for tre vektorer)
        return new Forces(
                newSeparation,
                flockForces.alignment(),
                flockForces.cohesion()
        );
    }

    private Vector2D calculateForageForce(Boid boid) {
        Food closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Food food : foodSources) {
            double dx = food.x() - boid.getX();
            double dy = food.y() - boid.getY();
            double dist = Math.sqrt(dx*dx + dy*dy);

            // Hvis boid er tæt nok til at spise maden
            if (dist < eatRadius) {
                food.markAsEaten();
                return Vector2D.ZERO;
            }

            // Hvis mad er inden for tiltrækningsradius
            if (dist < attractionRadius && dist < closestDist) {
                closestDist = dist;
                closest = food;
            }
        }

        // Ingen mad i nærheden
        if (closest == null) {
            return Vector2D.ZERO;
        }

        // Retning mod den nærmeste mad
        double fx = closest.x() - boid.getX();
        double fy = closest.y() - boid.getY();

        double mag = Math.sqrt(fx*fx + fy*fy);
        if (mag == 0) {
            return Vector2D.ZERO;
        }

        fx /= mag;
        fy /= mag;

        // Gange med en passende styrke
        double strength = 0.02;
        fx *= strength;
        fy *= strength;

        return new Vector2D(fx, fy);
    }
}
