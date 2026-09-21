package ecoagents;

import ecoagents.ui.GameFrame;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    GameFrame frame =
                            new GameFrame();

                    frame.setVisible(
                            true
                    );
                }
        );
    }
}