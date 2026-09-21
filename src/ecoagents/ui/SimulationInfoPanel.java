package ecoagents.ui;

import ecoagents.agents.Agent;
import ecoagents.agents.Animal;
import ecoagents.simulation.Simulation;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class SimulationInfoPanel extends JPanel {

    private final JLabel maximumGenerationLabel;

    private final JLabel totalBirthsLabel;

    private final PopulationChartPanel chartPanel;

    private final JTextArea eventLog;

    private int previousHerbivores;
    private int previousCarnivores;
    private int previousPlants;
    private int previousBirths;

    public SimulationInfoPanel(
            Simulation simulation
    ) {

        setLayout(
                new BorderLayout(
                        5,
                        5
                )
        );

        maximumGenerationLabel =
                new JLabel();

        totalBirthsLabel =
                new JLabel();

        chartPanel =
                new PopulationChartPanel();

        eventLog =
                new JTextArea();

        eventLog.setEditable(
                false
        );

        eventLog.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        11
                )
        );

        JPanel summaryPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                1
                        )
                );

        summaryPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Evolution"
                )
        );

        summaryPanel.add(
                maximumGenerationLabel
        );

        summaryPanel.add(
                totalBirthsLabel
        );

        JScrollPane logScroll =
                new JScrollPane(
                        eventLog
                );

        logScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Event Log"
                )
        );

        add(
                summaryPanel,
                BorderLayout.NORTH
        );

        add(
                chartPanel,
                BorderLayout.CENTER
        );

        add(
                logScroll,
                BorderLayout.SOUTH
        );

        logScroll.setPreferredSize(
                new java.awt.Dimension(
                        310,
                        180
                )
        );

        reset(
                simulation
        );
    }

    public void reset(
            Simulation simulation
    ) {

        previousHerbivores =
                simulation.getLivingHerbivoreCount();

        previousCarnivores =
                simulation.getLivingCarnivoreCount();

        previousPlants =
                simulation.getLivingPlantCount();

        previousBirths =
                simulation.getTotalBirths();

        eventLog.setText(
                "Simulation initialized.\n"
        );

        chartPanel.reset(
                simulation
        );

        refreshLabels(
                simulation
        );
    }

    public void update(
            Simulation simulation
    ) {

        int currentHerbivores =
                simulation.getLivingHerbivoreCount();

        int currentCarnivores =
                simulation.getLivingCarnivoreCount();

        int currentPlants =
                simulation.getLivingPlantCount();

        int currentBirths =
                simulation.getTotalBirths();

        int iteration =
                simulation.getIteration();

        if (
                currentPlants
                        <
                previousPlants
        ) {

            int eaten =
                    previousPlants
                            -
                    currentPlants;

            appendEvent(
                    iteration,
                    eaten
                            +
                    " plant(s) eaten"
            );
        }

        if (
                currentHerbivores
                        <
                previousHerbivores
        ) {

            int lost =
                    previousHerbivores
                            -
                    currentHerbivores;

            appendEvent(
                    iteration,
                    lost
                            +
                    " herbivore(s) lost"
            );
        }

        if (
                currentCarnivores
                        <
                previousCarnivores
        ) {

            int lost =
                    previousCarnivores
                            -
                    currentCarnivores;

            appendEvent(
                    iteration,
                    lost
                            +
                    " carnivore(s) lost"
            );
        }

        if (
                currentBirths
                        >
                previousBirths
        ) {

            int births =
                    currentBirths
                            -
                    previousBirths;

            appendEvent(
                    iteration,
                    births
                            +
                    " new offspring born"
            );
        }

        previousHerbivores =
                currentHerbivores;

        previousCarnivores =
                currentCarnivores;

        previousPlants =
                currentPlants;

        previousBirths =
                currentBirths;

        chartPanel.addSnapshot(
                simulation
        );

        refreshLabels(
                simulation
        );
    }

    private void appendEvent(
            int iteration,
            String message
    ) {

        eventLog.append(
                String.format(
                        "[%03d] %s%n",
                        iteration,
                        message
                )
        );

        eventLog.setCaretPosition(
                eventLog
                        .getDocument()
                        .getLength()
        );
    }

    private void refreshLabels(
            Simulation simulation
    ) {

        maximumGenerationLabel.setText(
                "Maximum generation: "
                        +
                findMaximumGeneration(
                        simulation
                )
        );

        totalBirthsLabel.setText(
                "Total births: "
                        +
                simulation.getTotalBirths()
        );
    }

    private int findMaximumGeneration(
            Simulation simulation
    ) {

        int maximumGeneration = 0;

        for (
                Agent agent
                : simulation
                .getEnvironment()
                .getAgents()
        ) {

            if (
                    agent instanceof Animal animal
            ) {

                maximumGeneration =
                        Math.max(
                                maximumGeneration,
                                animal.getGeneration()
                        );
            }
        }

        return maximumGeneration;
    }
}