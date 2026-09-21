package ecoagents.ui;

import ecoagents.agents.Agent;
import ecoagents.agents.Animal;
import ecoagents.agents.Plant;
import ecoagents.model.SensorData;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

public class AgentDetailsPanel extends JPanel {

    private final JLabel titleLabel;

    private final JTextArea detailsArea;

    private Agent selectedAgent;

    public AgentDetailsPanel() {

        setLayout(
                new BorderLayout(
                        5,
                        5
                )
        );

        setPreferredSize(
                new Dimension(
                        290,
                        650
                )
        );

        setBorder(
                BorderFactory.createTitledBorder(
                        "Selected Agent"
                )
        );

        titleLabel =
                new JLabel(
                        "Click an agent",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        16
                )
        );

        detailsArea =
                new JTextArea();

        detailsArea.setEditable(
                false
        );

        detailsArea.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        13
                )
        );

        detailsArea.setLineWrap(
                false
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        detailsArea
                );

        add(
                titleLabel,
                BorderLayout.NORTH
        );

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        clearSelection();
    }

    public void setSelectedAgent(
            Agent agent
    ) {

        selectedAgent =
                agent;

        refresh();
    }

    public Agent getSelectedAgent() {

        return selectedAgent;
    }

    public void clearSelection() {

        selectedAgent = null;

        titleLabel.setText(
                "Click an agent"
        );

        detailsArea.setText(
                """
                Select a living object on the grid.

                H = Herbivore
                C = Carnivore
                Green dot = Plant

                For animals you can inspect:

                - position
                - direction
                - energy
                - generation
                - last action
                - learning reward
                - 12 sensor inputs
                - 4 neural outputs
                """
        );
    }

    public void refresh() {

        if (
                selectedAgent == null
        ) {

            return;
        }

        if (
                selectedAgent instanceof Animal animal
        ) {

            showAnimal(
                    animal
            );

        } else if (
                selectedAgent instanceof Plant plant
        ) {

            showPlant(
                    plant
            );

        } else {

            showGenericAgent(
                    selectedAgent
            );
        }
    }

    private void showAnimal(
            Animal animal
    ) {

        titleLabel.setText(
                animal.getName()
        );

        StringBuilder text =
                new StringBuilder();

        text.append(
                "TYPE\n"
        );

        text.append(
                "-------------------------\n"
        );

        text.append(
                animal.getClass()
                        .getSimpleName()
        );

        text.append(
                "\n\n"
        );

        text.append(
                "STATE\n"
        );

        text.append(
                "-------------------------\n"
        );

        text.append(
                "Alive      : "
        );

        text.append(
                animal.isAlive()
        );

        text.append(
                "\n"
        );

        text.append(
                "Position   : "
        );

        text.append(
                animal.getPosition()
        );

        text.append(
                "\n"
        );

        text.append(
                "Direction  : "
        );

        text.append(
                animal.getDirection()
        );

        text.append(
                "\n"
        );

        text.append(
                "Generation : "
        );

        text.append(
                animal.getGeneration()
        );

        text.append(
                "\n"
        );

        text.append(
                String.format(
                        "Energy     : %.2f / %.2f%n",
                        animal.getEnergy(),
                        animal.getMaxEnergy()
                )
        );

        text.append(
                String.format(
                        "Energy %%   : %.1f%%%n",
                        animal.getEnergyRatio()
                                *
                                100.0
                )
        );

        text.append(
                "\n"
        );

        text.append(
                "LAST DECISION\n"
        );

        text.append(
                "-------------------------\n"
        );

        text.append(
                "Action:\n"
        );

        text.append(
                animal.getLastAction()
        );

        text.append(
                "\n\n"
        );

        text.append(
                "Chosen     : "
        );

        text.append(
                animal.getLastChosenAction()
        );

        text.append(
                "\n"
        );

        text.append(
                String.format(
                        "Reward     : %.3f%n",
                        animal.getLastLearningReward()
                )
        );

        text.append(
                "\n"
        );

        appendSensorInformation(
                text,
                animal.getLastSensorData()
        );

        appendNeuralOutputs(
                text,
                animal
        );

        detailsArea.setText(
                text.toString()
        );

        detailsArea.setCaretPosition(
                0
        );
    }

    private void appendSensorInformation(
            StringBuilder text,
            SensorData sensors
    ) {

        double[] inputs =
                sensors.toInputVector();

        text.append(
                "SENSOR INPUTS\n"
        );

        text.append(
                "-------------------------\n"
        );

        text.append(
                String.format(
                        "HF : %.0f%n",
                        inputs[0]
                )
        );

        text.append(
                String.format(
                        "CF : %.0f%n",
                        inputs[1]
                )
        );

        text.append(
                String.format(
                        "PF : %.0f%n",
                        inputs[2]
                )
        );

        text.append(
                "\n"
        );

        text.append(
                String.format(
                        "HL : %.0f%n",
                        inputs[3]
                )
        );

        text.append(
                String.format(
                        "CL : %.0f%n",
                        inputs[4]
                )
        );

        text.append(
                String.format(
                        "PL : %.0f%n",
                        inputs[5]
                )
        );

        text.append(
                "\n"
        );

        text.append(
                String.format(
                        "HR : %.0f%n",
                        inputs[6]
                )
        );

        text.append(
                String.format(
                        "CR : %.0f%n",
                        inputs[7]
                )
        );

        text.append(
                String.format(
                        "PR : %.0f%n",
                        inputs[8]
                )
        );

        text.append(
                "\n"
        );

        text.append(
                String.format(
                        "HN : %.0f%n",
                        inputs[9]
                )
        );

        text.append(
                String.format(
                        "CN : %.0f%n",
                        inputs[10]
                )
        );

        text.append(
                String.format(
                        "PN : %.0f%n",
                        inputs[11]
                )
        );

        text.append(
                "\n"
        );
    }

    private void appendNeuralOutputs(
            StringBuilder text,
            Animal animal
    ) {

        double[] outputs =
                animal
                        .getBrain()
                        .getLastOutputs();

        text.append(
                "NEURAL OUTPUTS\n"
        );

        text.append(
                "-------------------------\n"
        );

        text.append(
                String.format(
                        "TURN_LEFT    : % .4f%n",
                        outputs[0]
                )
        );

        text.append(
                String.format(
                        "TURN_RIGHT   : % .4f%n",
                        outputs[1]
                )
        );

        text.append(
                String.format(
                        "MOVE_FORWARD : % .4f%n",
                        outputs[2]
                )
        );

        text.append(
                String.format(
                        "EAT          : % .4f%n",
                        outputs[3]
                )
        );

        text.append(
                "\n"
        );

        text.append(
                "Winner:\n"
        );

        text.append(
                animal.getLastChosenAction()
        );

        text.append(
                "\n"
        );
    }

    private void showPlant(
            Plant plant
    ) {

        titleLabel.setText(
                "Plant"
        );

        detailsArea.setText(
                """
                TYPE
                -------------------------
                Plant

                STATE
                -------------------------
                Alive      : %s
                Position   : %s

                Plants are static resources.

                Herbivores can eat a plant
                when it is directly inside
                their nearness area.
                """
                        .formatted(
                                plant.isAlive(),
                                plant.getPosition()
                        )
        );

        detailsArea.setCaretPosition(
                0
        );
    }

    private void showGenericAgent(
            Agent agent
    ) {

        titleLabel.setText(
                agent.getName()
        );

        detailsArea.setText(
                "Position: "
                        +
                agent.getPosition()
        );
    }
}