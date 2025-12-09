package main.behavior;

import java.awt.*;

public class Food {

    private final double x, y;
    private boolean eaten = false;

    public Food(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() { return x; }
    public double y() { return y; }

    public boolean isEaten() { return eaten; }

    public void markAsEaten() {
        eaten = true;
    }

    public void render(Graphics2D g2d) {
        g2d.fillOval((int) this.x() - 4, (int) this.y() - 4, 8, 8);
    }
}
