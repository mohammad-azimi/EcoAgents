package ecoagents.ui;

import ecoagents.agents.Agent;
import ecoagents.agents.Carnivore;
import ecoagents.agents.Herbivore;
import ecoagents.agents.Plant;
import ecoagents.environment.Environment;
import ecoagents.model.Direction;
import ecoagents.model.Position;

import javax.swing.JPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.function.Consumer;

public class EnvironmentPanel extends JPanel {

    private Environment environment;

    private Agent selectedAgent;

    private Consumer<Agent> selectionListener;

    private int lastCellSize = 0;
    private int lastStartX = 0;
    private int lastStartY = 0;

    public EnvironmentPanel(
            Environment environment
    ) {

        this.environment =
                environment;

        setPreferredSize(
                new Dimension(
                        850,
                        650
                )
        );

        setBackground(
                new Color(
                        245,
                        247,
                        250
                )
        );

        registerMouseHandling();
    }

    public void setEnvironment(
            Environment environment
    ) {

        this.environment =
                environment;

        selectedAgent =
                null;

        repaint();
    }

    public void setSelectionListener(
            Consumer<Agent> selectionListener
    ) {

        this.selectionListener =
                selectionListener;
    }

    public Agent getSelectedAgent() {

        return selectedAgent;
    }

    public void clearSelection() {

        selectedAgent =
                null;

        repaint();
    }

    private void registerMouseHandling() {

        addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent event
                    ) {

                        handleMouseClick(
                                event.getX(),
                                event.getY()
                        );
                    }
                }
        );
    }

    private void handleMouseClick(
            int mouseX,
            int mouseY
    ) {

        if (
                environment == null
                        ||
                lastCellSize <= 0
        ) {

            return;
        }

        int gridWidth =
                lastCellSize
                        *
                environment.getCols();

        int gridHeight =
                lastCellSize
                        *
                environment.getRows();

        if (
                mouseX < lastStartX
                        ||
                mouseY < lastStartY
                        ||
                mouseX >= lastStartX + gridWidth
                        ||
                mouseY >= lastStartY + gridHeight
        ) {

            clearSelectedAgent();

            return;
        }

        int col =
                (
                        mouseX
                                -
                        lastStartX
                )
                        /
                        lastCellSize;

        int row =
                (
                        mouseY
                                -
                        lastStartY
                )
                        /
                        lastCellSize;

        Position position =
                new Position(
                        row,
                        col
                );

        Agent clickedAgent =
                environment.getLivingAgentAt(
                        position,
                        null
                );

        if (
                clickedAgent == null
        ) {

            clearSelectedAgent();

            return;
        }

        selectedAgent =
                clickedAgent;

        if (
                selectionListener != null
        ) {

            selectionListener.accept(
                    selectedAgent
            );
        }

        repaint();
    }

    private void clearSelectedAgent() {

        selectedAgent =
                null;

        if (
                selectionListener != null
        ) {

            selectionListener.accept(
                    null
            );
        }

        repaint();
    }

    @Override
    protected void paintComponent(
            Graphics graphics
    ) {

        super.paintComponent(
                graphics
        );

        if (
                environment == null
        ) {

            return;
        }

        Graphics2D g2 =
                (Graphics2D)
                        graphics.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int rows =
                environment.getRows();

        int cols =
                environment.getCols();

        int availableWidth =
                getWidth();

        int availableHeight =
                getHeight();

        int cellSize =
                Math.min(
                        availableWidth / cols,
                        availableHeight / rows
                );

        if (
                cellSize <= 0
        ) {

            g2.dispose();

            return;
        }

        int gridWidth =
                cellSize
                        *
                cols;

        int gridHeight =
                cellSize
                        *
                rows;

        int startX =
                (
                        availableWidth
                                -
                        gridWidth
                )
                        /
                        2;

        int startY =
                (
                        availableHeight
                                -
                        gridHeight
                )
                        /
                        2;

        lastCellSize =
                cellSize;

        lastStartX =
                startX;

        lastStartY =
                startY;

        drawGrid(
                g2,
                startX,
                startY,
                cellSize,
                rows,
                cols
        );

        drawAgents(
                g2,
                startX,
                startY,
                cellSize
        );

        g2.dispose();
    }

    private void drawGrid(
            Graphics2D g2,
            int startX,
            int startY,
            int cellSize,
            int rows,
            int cols
    ) {

        g2.setColor(
                new Color(
                        225,
                        228,
                        232
                )
        );

        g2.setStroke(
                new BasicStroke(
                        1.0f
                )
        );

        for (
                int row = 0;
                row <= rows;
                row++
        ) {

            int y =
                    startY
                            +
                    row
                            *
                    cellSize;

            g2.drawLine(
                    startX,
                    y,
                    startX
                            +
                    cols
                            *
                    cellSize,
                    y
            );
        }

        for (
                int col = 0;
                col <= cols;
                col++
        ) {

            int x =
                    startX
                            +
                    col
                            *
                    cellSize;

            g2.drawLine(
                    x,
                    startY,
                    x,
                    startY
                            +
                    rows
                            *
                    cellSize
            );
        }
    }

    private void drawAgents(
            Graphics2D g2,
            int startX,
            int startY,
            int cellSize
    ) {

        for (
                Agent agent
                : environment.getAgents()
        ) {

            if (
                    !agent.isAlive()
            ) {

                continue;
            }

            int row =
                    agent
                            .getPosition()
                            .getRow();

            int col =
                    agent
                            .getPosition()
                            .getCol();

            int x =
                    startX
                            +
                    col
                            *
                    cellSize;

            int y =
                    startY
                            +
                    row
                            *
                    cellSize;

            if (
                    agent == selectedAgent
            ) {

                drawSelection(
                        g2,
                        x,
                        y,
                        cellSize
                );
            }

            if (
                    agent instanceof Herbivore
            ) {

                drawAnimal(
                        g2,
                        x,
                        y,
                        cellSize,
                        new Color(
                                65,
                                160,
                                90
                        ),
                        "H",
                        agent.getDirection()
                );

            } else if (
                    agent instanceof Carnivore
            ) {

                drawAnimal(
                        g2,
                        x,
                        y,
                        cellSize,
                        new Color(
                                205,
                                70,
                                70
                        ),
                        "C",
                        agent.getDirection()
                );

            } else if (
                    agent instanceof Plant
            ) {

                drawPlant(
                        g2,
                        x,
                        y,
                        cellSize
                );
            }
        }
    }

    private void drawSelection(
            Graphics2D g2,
            int x,
            int y,
            int cellSize
    ) {

        g2.setColor(
                new Color(
                        30,
                        100,
                        220
                )
        );

        g2.setStroke(
                new BasicStroke(
                        3.0f
                )
        );

        g2.drawRect(
                x + 2,
                y + 2,
                cellSize - 4,
                cellSize - 4
        );
    }

    private void drawAnimal(
            Graphics2D g2,
            int x,
            int y,
            int cellSize,
            Color color,
            String label,
            Direction direction
    ) {

        int margin =
                Math.max(
                        3,
                        cellSize / 8
                );

        int size =
                cellSize
                        -
                2
                        *
                margin;

        g2.setColor(
                color
        );

        g2.fillOval(
                x + margin,
                y + margin,
                size,
                size
        );

        drawDirectionMarker(
                g2,
                x,
                y,
                cellSize,
                direction
        );

        g2.setColor(
                Color.WHITE
        );

        int fontSize =
                Math.max(
                        10,
                        cellSize / 3
                );

        g2.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        fontSize
                )
        );

        int textWidth =
                g2
                        .getFontMetrics()
                        .stringWidth(
                                label
                        );

        int textHeight =
                g2
                        .getFontMetrics()
                        .getAscent();

        g2.drawString(
                label,
                x
                        +
                (
                        cellSize
                                -
                        textWidth
                )
                        /
                        2,
                y
                        +
                (
                        cellSize
                                +
                        textHeight
                )
                        /
                        2
                        -
                2
        );
    }

    private void drawDirectionMarker(
            Graphics2D g2,
            int x,
            int y,
            int cellSize,
            Direction direction
    ) {

        int centerX =
                x
                        +
                cellSize
                        /
                2;

        int centerY =
                y
                        +
                cellSize
                        /
                2;

        int distance =
                Math.max(
                        5,
                        cellSize / 3
                );

        int endX =
                centerX;

        int endY =
                centerY;

        switch (direction) {

            case NORTH ->
                    endY -= distance;

            case SOUTH ->
                    endY += distance;

            case EAST ->
                    endX += distance;

            case WEST ->
                    endX -= distance;
        }

        g2.setColor(
                Color.WHITE
        );

        g2.setStroke(
                new BasicStroke(
                        2.0f
                )
        );

        g2.drawLine(
                centerX,
                centerY,
                endX,
                endY
        );
    }

    private void drawPlant(
            Graphics2D g2,
            int x,
            int y,
            int cellSize
    ) {

        int size =
                Math.max(
                        6,
                        cellSize / 3
                );

        int plantX =
                x
                        +
                (
                        cellSize
                                -
                        size
                )
                        /
                        2;

        int plantY =
                y
                        +
                (
                        cellSize
                                -
                        size
                )
                        /
                        2;

        g2.setColor(
                new Color(
                        80,
                        185,
                        85
                )
        );

        g2.fillOval(
                plantX,
                plantY,
                size,
                size
        );
    }
}