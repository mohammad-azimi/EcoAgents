package ecoagents.agents;

import ecoagents.environment.Environment;
import ecoagents.model.Position;

public class Plant extends Agent {

    public Plant(Position position) {

        super(
                "Plant",
                'P',
                position
        );
    }

    /*
     * Plant is a static resource.
     *
     * It does not have a neural brain
     * and does not perform movement.
     *
     * This method remains only because
     * Plant still extends Agent.
     */
    @Override
    public Position decideMove(
            Environment environment
    ) {

        setLastAction(
                "STATIC RESOURCE"
        );

        return getPosition();
    }
}