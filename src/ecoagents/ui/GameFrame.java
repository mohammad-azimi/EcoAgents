package ecoagents.ui;

import ecoagents.agents.Agent;
import ecoagents.agents.Animal;
import ecoagents.simulation.Simulation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class GameFrame extends JFrame {

    private static final long DEFAULT_SEED = 42L;

    private Simulation simulation;

    private final EnvironmentPanel environmentPanel;

    private final AgentDetailsPanel agentDetailsPanel;

    private final SimulationInfoPanel simulationInfoPanel;

    private final JLabel iterationLabel;
    private final JLabel herbivoreLabel;
    private final JLabel carnivoreLabel;
    private final JLabel plantLabel;
    private final JLabel birthLabel;
    private final JLabel generationLabel;
    private final JLabel statusLabel;

    private final JButton startButton;
    private final JButton pauseButton;
    private final JButton stepButton;
    private final JButton resetButton;

    private final JSlider speedSlider;

    private final Timer timer;

    public GameFrame() {

        super(
                "EcoAgents - Artificial Life Simulation"
        );

        simulation =
                new Simulation(
                        DEFAULT_SEED
                );

        environmentPanel =
                new EnvironmentPanel(
                        simulation.getEnvironment()
                );

        agentDetailsPanel =
                new AgentDetailsPanel();

        simulationInfoPanel =
                new SimulationInfoPanel(
                        simulation
                );

        iterationLabel =
                createStatisticLabel(
                        "Iteration: 0"
                );

        herbivoreLabel =
                createStatisticLabel(
                        "Herbivores: 0"
                );

        carnivoreLabel =
                createStatisticLabel(
                        "Carnivores: 0"
                );

        plantLabel =
                createStatisticLabel(
                        "Plants: 0"
                );

        birthLabel =
                createStatisticLabel(
                        "Births: 0"
                );

        generationLabel =
                createStatisticLabel(
                        "Max Gen: 0"
                );

        statusLabel =
                new JLabel(
                        "PAUSED",
                        SwingConstants.CENTER
                );

        statusLabel.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        14
                )
        );

        startButton =
                new JButton(
                        "Start"
                );

        pauseButton =
                new JButton(
                        "Pause"
                );

        stepButton =
                new JButton(
                        "Step"
                );

        resetButton =
                new JButton(
                        "Reset"
                );

        speedSlider =
                new JSlider(
                        50,
                        1000,
                        300
                );

        speedSlider.setPreferredSize(
                new Dimension(
                        180,
                        40
                )
        );

        speedSlider.setToolTipText(
                "Simulation delay in milliseconds"
        );

        timer =
                new Timer(
                        speedSlider.getValue(),
                        event ->
                                runOneStep()
                );

        buildInterface();

        registerEvents();

        updateStatistics();

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setMinimumSize(
                new Dimension(
                        1320,
                        800
                )
        );

        pack();

        setLocationRelativeTo(
                null
        );
    }

    private void buildInterface() {

        setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );

        add(
                createHeaderPanel(),
                BorderLayout.NORTH
        );

        add(
                environmentPanel,
                BorderLayout.CENTER
        );

        add(
                createRightPanel(),
                BorderLayout.EAST
        );

        add(
                createControlPanel(),
                BorderLayout.SOUTH
        );

        getRootPane()
                .setBorder(
                        BorderFactory.createEmptyBorder(
                                10,
                                10,
                                10,
                                10
                        )
                );
    }

    private JPanel createHeaderPanel() {

        JPanel mainHeader =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        JLabel title =
                new JLabel(
                        "EcoAgents Artificial Life Simulation",
                        SwingConstants.CENTER
                );

        title.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        22
                )
        );

        JPanel statistics =
                new JPanel(
                        new GridLayout(
                                1,
                                6,
                                8,
                                5
                        )
                );

        statistics.add(
                iterationLabel
        );

        statistics.add(
                herbivoreLabel
        );

        statistics.add(
                carnivoreLabel
        );

        statistics.add(
                plantLabel
        );

        statistics.add(
                birthLabel
        );

        statistics.add(
                generationLabel
        );

        mainHeader.add(
                title,
                BorderLayout.NORTH
        );

        mainHeader.add(
                statistics,
                BorderLayout.CENTER
        );

        mainHeader.add(
                statusLabel,
                BorderLayout.SOUTH
        );

        return mainHeader;
    }

    private JTabbedPane createRightPanel() {

        JTabbedPane tabs =
                new JTabbedPane();

        tabs.setPreferredSize(
                new Dimension(
                        340,
                        650
                )
        );

        tabs.addTab(
                "Agent",
                agentDetailsPanel
        );

        tabs.addTab(
                "Simulation",
                simulationInfoPanel
        );

        return tabs;
    }

    private JPanel createControlPanel() {

        JPanel controls =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                10,
                                5
                        )
                );

        controls.add(
                startButton
        );

        controls.add(
                pauseButton
        );

        controls.add(
                stepButton
        );

        controls.add(
                resetButton
        );

        controls.add(
                new JLabel(
                        "Speed:"
                )
        );

        controls.add(
                speedSlider
        );

        return controls;
    }

    private JLabel createStatisticLabel(
            String text
    ) {

        JLabel label =
                new JLabel(
                        text,
                        SwingConstants.CENTER
                );

        label.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.BOLD,
                        12
                )
        );

        label.setBorder(
                BorderFactory.createEtchedBorder()
        );

        return label;
    }

    private void registerEvents() {

        startButton.addActionListener(
                event ->
                        startSimulation()
        );

        pauseButton.addActionListener(
                event ->
                        pauseSimulation()
        );

        stepButton.addActionListener(
                event ->
                        stepSimulation()
        );

        resetButton.addActionListener(
                event ->
                        resetSimulation()
        );

        speedSlider.addChangeListener(
                event ->
                        timer.setDelay(
                                speedSlider.getValue()
                        )
        );

        environmentPanel.setSelectionListener(
                this::handleAgentSelection
        );
    }

    private void handleAgentSelection(
            Agent agent
    ) {

        if (
                agent == null
        ) {

            agentDetailsPanel
                    .clearSelection();

            return;
        }

        agentDetailsPanel
                .setSelectedAgent(
                        agent
                );
    }

    private void startSimulation() {

        if (
                simulation.isFinished()
        ) {

            return;
        }

        timer.start();

        statusLabel.setText(
                "RUNNING"
        );
    }

    private void pauseSimulation() {

        timer.stop();

        statusLabel.setText(
                "PAUSED"
        );
    }

    private void stepSimulation() {

        if (
                simulation.isFinished()
        ) {

            return;
        }

        pauseSimulation();

        runOneStep();
    }

    private void resetSimulation() {

        timer.stop();

        simulation =
                new Simulation(
                        DEFAULT_SEED
                );

        environmentPanel.setEnvironment(
                simulation.getEnvironment()
        );

        agentDetailsPanel
                .clearSelection();

        simulationInfoPanel
                .reset(
                        simulation
                );

        statusLabel.setText(
                "PAUSED"
        );

        updateStatistics();

        environmentPanel.repaint();
    }

    private void runOneStep() {

        if (
                simulation.isFinished()
        ) {

            timer.stop();

            statusLabel.setText(
                    "FINISHED"
            );

            return;
        }

        simulation.step();

        simulationInfoPanel.update(
                simulation
        );

        updateStatistics();

        refreshSelectedAgent();

        environmentPanel.repaint();

        if (
                simulation.isFinished()
        ) {

            timer.stop();

            statusLabel.setText(
                    "FINISHED"
            );
        }
    }

    private void refreshSelectedAgent() {

        Agent selected =
                environmentPanel
                        .getSelectedAgent();

        if (
                selected == null
        ) {

            return;
        }

        agentDetailsPanel.refresh();
    }

    private void updateStatistics() {

        iterationLabel.setText(
                "Iteration: "
                        +
                simulation.getIteration()
        );

        herbivoreLabel.setText(
                "Herbivores: "
                        +
                simulation.getLivingHerbivoreCount()
        );

        carnivoreLabel.setText(
                "Carnivores: "
                        +
                simulation.getLivingCarnivoreCount()
        );

        plantLabel.setText(
                "Plants: "
                        +
                simulation.getLivingPlantCount()
        );

        birthLabel.setText(
                "Births: "
                        +
                simulation.getTotalBirths()
        );

        generationLabel.setText(
                "Max Gen: "
                        +
                findMaximumGeneration()
        );
    }

    private int findMaximumGeneration() {

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