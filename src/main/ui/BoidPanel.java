package main.ui;

import main.behavior.*;
import main.model.Boid;
import main.simulation.FlockSimulation;
import main.spatial.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BoidPanel extends JPanel implements ActionListener {

    private final FlockSimulation simulation;
    private final Timer animationTimer;

    private boolean running = false;

    private final JSlider boidCountSlider;
    private final JSlider radiusSlider;

    private final JSlider foodIntervalSlider;
    private final JSlider foodAmountSlider;

    private final JButton playPauseButton;

    private final JComboBox<SpatialIndexOption> spatialIndexCombo;
    private final JComboBox<String> behaviorSelector;

    private final JLabel performanceLabel;

    private static final int PANEL_WIDTH = 1000;
    private static final int PANEL_HEIGHT = 700;
    private static final int CONTROL_HEIGHT = 140;

    private static class SpatialIndexOption {
        private final String name;
        private final SpatialIndex spatialIndex;

        public SpatialIndexOption(String name, SpatialIndex index) {
            this.name = name;
            this.spatialIndex = index;
        }

        @Override
        public String toString() { return name; }
        public SpatialIndex getSpatialIndex() { return spatialIndex; }
    }

    public BoidPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT + CONTROL_HEIGHT));
        setBackground(Color.BLACK);

        simulation = new FlockSimulation(PANEL_WIDTH, PANEL_HEIGHT - CONTROL_HEIGHT);
        simulation.setBoidCount(100);

        animationTimer = new Timer(16, this);

        boidCountSlider = new JSlider(10, 5000, 100);
        boidCountSlider.setMajorTickSpacing(1000);
        boidCountSlider.setMinorTickSpacing(500);
        boidCountSlider.setPaintTicks(true);
        boidCountSlider.setPaintLabels(true);
        boidCountSlider.addChangeListener(e -> simulation.setBoidCount(boidCountSlider.getValue()));

        radiusSlider = new JSlider(10, 150, 50);
        radiusSlider.setMajorTickSpacing(50);
        radiusSlider.setMinorTickSpacing(25);
        radiusSlider.setPaintTicks(true);
        radiusSlider.setPaintLabels(true);
        radiusSlider.addChangeListener(e -> simulation.setNeighborRadius(radiusSlider.getValue()));

        foodIntervalSlider = new JSlider(500, 10000, 3000);
        foodIntervalSlider.setMajorTickSpacing(2500);
        foodIntervalSlider.setPaintTicks(true);
        foodIntervalSlider.setPaintLabels(true);
        foodIntervalSlider.addChangeListener(e -> simulation.setFoodSpawnInterval(foodIntervalSlider.getValue()));

        foodAmountSlider = new JSlider(1, 20, 5);
        foodAmountSlider.setMajorTickSpacing(5);
        foodAmountSlider.setPaintTicks(true);
        foodAmountSlider.setPaintLabels(true);
        foodAmountSlider.addChangeListener(e -> simulation.setFoodSpawnAmount(foodAmountSlider.getValue()));

        playPauseButton = new JButton("Start");
        playPauseButton.addActionListener(e -> togglePlayPause());

        SpatialIndexOption[] spatialOptions = {
                new SpatialIndexOption("Naive O(n²)", new NaiveSpatialIndex()),
                new SpatialIndexOption("KD-Tree", new KDTreeSpatialIndex()),
                new SpatialIndexOption("Spatial Hashing", new SpatialHashIndex(PANEL_WIDTH, PANEL_HEIGHT - CONTROL_HEIGHT, 50)),
                new SpatialIndexOption("QuadTree", new QuadTreeSpatialIndex(PANEL_WIDTH, PANEL_HEIGHT - CONTROL_HEIGHT))
        };

        spatialIndexCombo = new JComboBox<>(spatialOptions);
        spatialIndexCombo.addActionListener(e -> {
            SpatialIndexOption opt = (SpatialIndexOption) spatialIndexCombo.getSelectedItem();
            if (opt != null) simulation.setSpatialIndex(opt.getSpatialIndex());
        });

        behaviorSelector = new JComboBox<>(new String[]{
                "FlockBehavior",
                "ForageBehavior"
        });
        behaviorSelector.addActionListener(e -> {
            String selected = (String) behaviorSelector.getSelectedItem();
            simulation.setBehavior(selected);
        });

        performanceLabel = new JLabel("Iteration time: 0.0 ms");

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        controlPanel.setPreferredSize(new Dimension(PANEL_WIDTH, CONTROL_HEIGHT));
        controlPanel.setBackground(Color.DARK_GRAY);

        controlPanel.add(new JLabel("Boids:"));
        controlPanel.add(boidCountSlider);

        controlPanel.add(new JLabel("Radius:"));
        controlPanel.add(radiusSlider);

        controlPanel.add(new JLabel("Food interval (ms):"));
        controlPanel.add(foodIntervalSlider);

        controlPanel.add(new JLabel("Food amount:"));
        controlPanel.add(foodAmountSlider);

        controlPanel.add(new JLabel("Behavior:"));
        controlPanel.add(behaviorSelector);

        controlPanel.add(playPauseButton);
        controlPanel.add(spatialIndexCombo);
        controlPanel.add(performanceLabel);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void togglePlayPause() {
        if (running) {
            animationTimer.stop();
            playPauseButton.setText("Start");
            running = false;
        } else {
            animationTimer.start();
            simulation.resetFoodSpawnClock();
            playPauseButton.setText("Pause");
            running = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        simulation.update();
        updateLabels();
        repaint();
    }

    private void updateLabels() {
        performanceLabel.setText(String.format(
                "Iteration time: %.2f ms", simulation.getLastIterationTimeMs()
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        for (Boid boid : simulation.getBoids()) {
            boid.render(g2d);
        }

        g2d.setColor(Color.GREEN);
        for (Food food : simulation.getFood()) {
            food.render(g2d);
        }

        g2d.dispose();
    }
}
