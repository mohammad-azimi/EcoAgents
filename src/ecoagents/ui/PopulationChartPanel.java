package ecoagents.ui;

import ecoagents.simulation.Simulation;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.util.ArrayList;
import java.util.List;

public class PopulationChartPanel extends JPanel {

    private final List<Integer> iterations =
            new ArrayList<>();

    private final List<Integer> herbivores =
            new ArrayList<>();

    private final List<Integer> carnivores =
            new ArrayList<>();

    private final List<Integer> plants =
            new ArrayList<>();

    public PopulationChartPanel() {

        setPreferredSize(
                new Dimension(
                        310,
                        230
                )
        );

        setBackground(
                Color.WHITE
        );

        setBorder(
                BorderFactory.createTitledBorder(
                        "Population History"
                )
        );
    }

    public void reset(
            Simulation simulation
    ) {

        iterations.clear();
        herbivores.clear();
        carnivores.clear();
        plants.clear();

        addSnapshot(
                simulation
        );
    }

    public void addSnapshot(
            Simulation simulation
    ) {

        iterations.add(
                simulation.getIteration()
        );

        herbivores.add(
                simulation.getLivingHerbivoreCount()
        );

        carnivores.add(
                simulation.getLivingCarnivoreCount()
        );

        plants.add(
                simulation.getLivingPlantCount()
        );

        repaint();
    }

    @Override
    protected void paintComponent(
            Graphics graphics
    ) {

        super.paintComponent(
                graphics
        );

        Graphics2D g2 =
                (Graphics2D)
                        graphics.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        /*
         * More space at the top prevents
         * the chart title and legend
         * from overlapping.
         */
        int left = 38;
        int right = 15;
        int top = 55;
        int bottom = 30;

        int chartWidth =
                getWidth()
                        -
                left
                        -
                right;

        int chartHeight =
                getHeight()
                        -
                top
                        -
                bottom;

        if (
                chartWidth <= 0
                        ||
                chartHeight <= 0
        ) {

            g2.dispose();

            return;
        }

        drawLegend(
                g2
        );

        drawAxes(
                g2,
                left,
                top,
                chartWidth,
                chartHeight
        );

        if (
                iterations.size() >= 2
        ) {

            int maximumPopulation =
                    findMaximumPopulation();

            drawHorizontalGuides(
                    g2,
                    left,
                    top,
                    chartWidth,
                    chartHeight,
                    maximumPopulation
            );

            drawSeries(
                    g2,
                    herbivores,
                    new Color(
                            55,
                            150,
                            80
                    ),
                    left,
                    top,
                    chartWidth,
                    chartHeight,
                    maximumPopulation
            );

            drawSeries(
                    g2,
                    carnivores,
                    new Color(
                            200,
                            65,
                            65
                    ),
                    left,
                    top,
                    chartWidth,
                    chartHeight,
                    maximumPopulation
            );

            drawSeries(
                    g2,
                    plants,
                    new Color(
                            65,
                            105,
                            200
                    ),
                    left,
                    top,
                    chartWidth,
                    chartHeight,
                    maximumPopulation
            );
        }

        g2.dispose();
    }

    private void drawAxes(
            Graphics2D g2,
            int left,
            int top,
            int chartWidth,
            int chartHeight
    ) {

        g2.setColor(
                new Color(
                        185,
                        185,
                        185
                )
        );

        g2.setStroke(
                new BasicStroke(
                        1.0f
                )
        );

        g2.drawLine(
                left,
                top,
                left,
                top + chartHeight
        );

        g2.drawLine(
                left,
                top + chartHeight,
                left + chartWidth,
                top + chartHeight
        );

        g2.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.PLAIN,
                        10
                )
        );

        g2.setColor(
                Color.DARK_GRAY
        );

        g2.drawString(
                "Pop.",
                6,
                top + 3
        );

        int lastIteration =
                iterations.isEmpty()
                        ?
                        0
                        :
                        iterations.get(
                                iterations.size() - 1
                        );

        g2.drawString(
                "0",
                left - 4,
                top + chartHeight + 14
        );

        String finalIteration =
                Integer.toString(
                        lastIteration
                );

        int textWidth =
                g2
                        .getFontMetrics()
                        .stringWidth(
                                finalIteration
                        );

        g2.drawString(
                finalIteration,
                left
                        +
                chartWidth
                        -
                textWidth,
                top
                        +
                chartHeight
                        +
                14
        );
    }

    private void drawHorizontalGuides(
            Graphics2D g2,
            int left,
            int top,
            int chartWidth,
            int chartHeight,
            int maximumPopulation
    ) {

        g2.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.PLAIN,
                        9
                )
        );

        for (
                int i = 0;
                i <= 4;
                i++
        ) {

            int y =
                    top
                            +
                    (
                            i
                                    *
                            chartHeight
                    )
                            /
                    4;

            g2.setColor(
                    new Color(
                            235,
                            235,
                            235
                    )
            );

            g2.drawLine(
                    left,
                    y,
                    left + chartWidth,
                    y
            );

            int value =
                    maximumPopulation
                            -
                    (
                            maximumPopulation
                                    *
                            i
                    )
                            /
                    4;

            g2.setColor(
                    Color.GRAY
            );

            g2.drawString(
                    Integer.toString(
                            value
                    ),
                    18,
                    y + 3
            );
        }
    }

    private void drawSeries(
            Graphics2D g2,
            List<Integer> values,
            Color color,
            int left,
            int top,
            int chartWidth,
            int chartHeight,
            int maximumPopulation
    ) {

        if (
                values.size() < 2
        ) {

            return;
        }

        g2.setColor(
                color
        );

        g2.setStroke(
                new BasicStroke(
                        2.0f
                )
        );

        int maximumIndex =
                values.size() - 1;

        for (
                int i = 1;
                i < values.size();
                i++
        ) {

            int x1 =
                    left
                            +
                    (
                            (i - 1)
                                    *
                            chartWidth
                    )
                            /
                    maximumIndex;

            int x2 =
                    left
                            +
                    (
                            i
                                    *
                            chartWidth
                    )
                            /
                    maximumIndex;

            int y1 =
                    top
                            +
                    chartHeight
                            -
                    (
                            values.get(
                                    i - 1
                            )
                                    *
                            chartHeight
                    )
                            /
                    maximumPopulation;

            int y2 =
                    top
                            +
                    chartHeight
                            -
                    (
                            values.get(
                                    i
                            )
                                    *
                            chartHeight
                    )
                            /
                    maximumPopulation;

            g2.drawLine(
                    x1,
                    y1,
                    x2,
                    y2
            );
        }
    }

    private int findMaximumPopulation() {

        int maximum = 1;

        for (
                int value
                : herbivores
        ) {

            maximum =
                    Math.max(
                            maximum,
                            value
                    );
        }

        for (
                int value
                : carnivores
        ) {

            maximum =
                    Math.max(
                            maximum,
                            value
                    );
        }

        for (
                int value
                : plants
        ) {

            maximum =
                    Math.max(
                            maximum,
                            value
                    );
        }

        return maximum;
    }

    private void drawLegend(
            Graphics2D g2
    ) {

        int y = 38;

        drawLegendItem(
                g2,
                20,
                y,
                new Color(
                        55,
                        150,
                        80
                ),
                "Herbivore"
        );

        drawLegendItem(
                g2,
                115,
                y,
                new Color(
                        200,
                        65,
                        65
                ),
                "Carnivore"
        );

        drawLegendItem(
                g2,
                215,
                y,
                new Color(
                        65,
                        105,
                        200
                ),
                "Plant"
        );
    }

    private void drawLegendItem(
            Graphics2D g2,
            int x,
            int y,
            Color color,
            String text
    ) {

        g2.setColor(
                color
        );

        g2.setStroke(
                new BasicStroke(
                        3.0f
                )
        );

        g2.drawLine(
                x,
                y,
                x + 14,
                y
        );

        g2.setColor(
                Color.DARK_GRAY
        );

        g2.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.PLAIN,
                        10
                )
        );

        g2.drawString(
                text,
                x + 18,
                y + 4
        );
    }
}