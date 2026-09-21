package ecoagents.model;

import java.util.Arrays;

public class SensorData {

    /*
     * FRONT AREA
     */
    private int herbivoreFront;
    private int carnivoreFront;
    private int plantFront;

    /*
     * LEFT AREA
     */
    private int herbivoreLeft;
    private int carnivoreLeft;
    private int plantLeft;

    /*
     * RIGHT AREA
     */
    private int herbivoreRight;
    private int carnivoreRight;
    private int plantRight;

    /*
     * NEARNESS AREA
     */
    private int herbivoreNear;
    private int carnivoreNear;
    private int plantNear;

    public void addHerbivoreFront() {
        herbivoreFront++;
    }

    public void addCarnivoreFront() {
        carnivoreFront++;
    }

    public void addPlantFront() {
        plantFront++;
    }

    public void addHerbivoreLeft() {
        herbivoreLeft++;
    }

    public void addCarnivoreLeft() {
        carnivoreLeft++;
    }

    public void addPlantLeft() {
        plantLeft++;
    }

    public void addHerbivoreRight() {
        herbivoreRight++;
    }

    public void addCarnivoreRight() {
        carnivoreRight++;
    }

    public void addPlantRight() {
        plantRight++;
    }

    public void addHerbivoreNear() {
        herbivoreNear++;
    }

    public void addCarnivoreNear() {
        carnivoreNear++;
    }

    public void addPlantNear() {
        plantNear++;
    }

    public int getHerbivoreFront() {
        return herbivoreFront;
    }

    public int getCarnivoreFront() {
        return carnivoreFront;
    }

    public int getPlantFront() {
        return plantFront;
    }

    public int getHerbivoreLeft() {
        return herbivoreLeft;
    }

    public int getCarnivoreLeft() {
        return carnivoreLeft;
    }

    public int getPlantLeft() {
        return plantLeft;
    }

    public int getHerbivoreRight() {
        return herbivoreRight;
    }

    public int getCarnivoreRight() {
        return carnivoreRight;
    }

    public int getPlantRight() {
        return plantRight;
    }

    public int getHerbivoreNear() {
        return herbivoreNear;
    }

    public int getCarnivoreNear() {
        return carnivoreNear;
    }

    public int getPlantNear() {
        return plantNear;
    }

    /*
     * Converts all sensor values into the exact
     * 12-input vector used by the neural brain.
     *
     * Order:
     *
     * HF CF PF
     * HL CL PL
     * HR CR PR
     * HN CN PN
     */
    public double[] toInputVector() {

        return new double[] {

                herbivoreFront,
                carnivoreFront,
                plantFront,

                herbivoreLeft,
                carnivoreLeft,
                plantLeft,

                herbivoreRight,
                carnivoreRight,
                plantRight,

                herbivoreNear,
                carnivoreNear,
                plantNear
        };
    }

    @Override
    public String toString() {

        return Arrays.toString(
                toInputVector()
        );
    }
}