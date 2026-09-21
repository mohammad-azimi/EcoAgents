# EcoAgents

**EcoAgents** is a Java Swing artificial-life simulation that models an ecosystem containing herbivores, carnivores, and plants.

The project demonstrates how autonomous agents can perceive a local environment, process sensory information through a simple neural decision system, choose actions, consume energy, learn from experience, reproduce, and pass an inherited neural network to the next generation.

---

## Features

- Toroidal grid environment
- Herbivore and carnivore autonomous agents
- Static plant resources
- Local sensor-based perception
- 12 neural-network inputs
- 4 neural-network outputs
- Winner-takes-all action selection
- Energy and metabolism system
- Herbivore feeding
- Carnivore hunting
- Reinforcement-based neural adaptation
- Exploration during decision making
- Reproduction at 90% energy
- Neural-network inheritance
- Mutation in offspring
- Generation tracking
- Java Swing graphical interface
- Start, Pause, Step, Reset, and Speed controls
- Interactive agent inspector
- Population history chart
- Simulation event log

---

## Artificial Life Model

Each animal follows the general cycle:

```text
Environment
    ↓
Sensors
    ↓
Neural Network
    ↓
Action Selection
    ↓
Action
    ↓
Reward
    ↓
Learning
    ↓
Energy
    ↓
Reproduction
```

The simulation contains two autonomous animal species:

- Herbivores
- Carnivores

Plants are static environmental resources.

---

## Environment

The ecosystem is represented as a two-dimensional grid.

The environment uses **toroidal topology**.

This means that when an agent crosses one boundary of the world, it appears on the opposite side.

For example:

```text
Right edge  → Left edge
Left edge   → Right edge
Top edge    → Bottom edge
Bottom edge → Top edge
```

This allows the simulation world to behave as a continuous surface without hard borders.

---

## Ecosystem Entities

### Herbivore

A herbivore can:

- move forward
- turn left
- turn right
- detect nearby objects
- search for plants
- eat plants
- gain energy
- learn from actions
- reproduce
- pass its neural network to offspring

A herbivore receives:

```text
+1 energy
```

when it successfully eats a plant.

---

### Carnivore

A carnivore can:

- move forward
- turn left
- turn right
- detect nearby objects
- search for herbivores
- hunt herbivores
- gain energy
- learn from actions
- reproduce
- pass its neural network to offspring

A carnivore receives:

```text
+2 energy
```

when it successfully eats a herbivore.

---

### Plant

A plant is a static environmental resource.

Plants:

- occupy one cell
- do not move
- do not have a neural network
- do not perform actions
- can be eaten by herbivores

---

## Sensor System

Animals do not have access to the entire environment.

Each animal perceives only its local surroundings.

The sensor system is divided into four regions:

- Front
- Left
- Right
- Nearness

The sensors detect three categories:

- Herbivores
- Carnivores
- Plants

This creates a total of **12 sensor inputs**.

```text
HF CF PF
HL CL PL
HR CR PR
HN CN PN
```

The abbreviations mean:

| Input | Meaning                 |
| ----- | ----------------------- |
| HF    | Herbivores in Front     |
| CF    | Carnivores in Front     |
| PF    | Plants in Front         |
| HL    | Herbivores on the Left  |
| CL    | Carnivores on the Left  |
| PL    | Plants on the Left      |
| HR    | Herbivores on the Right |
| CR    | Carnivores on the Right |
| PR    | Plants on the Right     |
| HN    | Herbivores in Nearness  |
| CN    | Carnivores in Nearness  |
| PN    | Plants in Nearness      |

In this implementation, **Nearness** consists of the eight cells directly surrounding an animal.

The other sensor regions depend on the current direction of the animal.

---

## Neural Decision System

Each animal contains its own neural decision system.

The network has:

```text
12 inputs
4 outputs
```

For each output neuron, the following equation is calculated:

```text
Output(i) = Bias(i) + Σ Weight(i,j) × Input(j)
```

The four outputs represent the available actions:

| Output | Action       |
| ------ | ------------ |
| 0      | TURN_LEFT    |
| 1      | TURN_RIGHT   |
| 2      | MOVE_FORWARD |
| 3      | EAT          |

The action with the largest neural output is selected.

This is a **winner-takes-all** decision mechanism.

Example:

```text
TURN_LEFT    = 0.70
TURN_RIGHT   = 0.10
MOVE_FORWARD = 1.50
EAT          = 2.10
```

The selected action is:

```text
EAT
```

because it has the largest output.

---

## Available Actions

Each animal can select one of four actions:

```text
TURN_LEFT
TURN_RIGHT
MOVE_FORWARD
EAT
```

### TURN_LEFT

Rotates the animal 90 degrees to the left.

### TURN_RIGHT

Rotates the animal 90 degrees to the right.

### MOVE_FORWARD

Moves the animal one cell in its current direction.

Toroidal wrapping is automatically applied.

### EAT

Attempts to consume an appropriate food source inside the local nearness area.

For herbivores:

```text
Plant → food
```

For carnivores:

```text
Herbivore → food
```

---

## Learning

The neural-network parameters can change during an animal's lifetime.

The project uses a simple reinforcement-style adaptation rule.

The basic weight update is:

```text
weight =
weight + learningRate × reward × input
```

The bias of the selected action is also updated.

A positive reward strengthens useful behavior.

A negative reward weakens unsuccessful behavior.

Examples:

```text
Successful eating    → positive reward
Failed eating        → negative reward
Successful movement  → small positive reward
Blocked movement     → negative reward
Turning repeatedly   → small penalty
```

---

## Behavioral Reward Shaping

Additional feedback helps agents associate sensor information with useful actions.

### Herbivore behavior

```text
Plant in Nearness → EAT
Plant in Front    → MOVE_FORWARD
Plant on Left     → TURN_LEFT
Plant on Right    → TURN_RIGHT
```

### Carnivore behavior

```text
Herbivore in Nearness → EAT
Herbivore in Front    → MOVE_FORWARD
Herbivore on Left     → TURN_LEFT
Herbivore on Right    → TURN_RIGHT
```

---

## Exploration

During the real simulation, agents do not always select the current best neural-network action.

A small exploration probability is used.

Current value:

```text
10%
```

This means that approximately 10% of decisions are random exploratory actions.

Exploration allows an agent to discover new behaviors instead of remaining permanently stuck with its initial neural-network preferences.

---

## Energy System

Every animal has a maximum energy level.

```text
Maximum energy = 20.0
```

Initial energy:

```text
Initial energy = 16.0
```

Metabolism cost per simulation iteration:

```text
0.02
```

Food rewards:

```text
Herbivore eats Plant     → +1 energy
Carnivore eats Herbivore → +2 energy
```

If an animal's energy reaches zero, the animal dies.

---

## Reproduction

An animal becomes eligible for reproduction when its energy reaches at least 90% of its maximum energy.

```text
Maximum energy = 20

90% × 20 = 18
```

Therefore:

```text
Energy >= 18
```

allows reproduction.

During reproduction:

1. The simulation searches for an empty neighboring cell.
2. A child of the same species is created.
3. Energy is transferred from the parent to the child.
4. The child inherits the parent's neural network.
5. Small mutations are applied to the inherited network.
6. The child's generation number is increased.

Example:

```text
Parent generation = 0
Child generation  = 1
```

---

## Neural Inheritance and Mutation

The offspring receives a copy of the parent's current neural network.

This includes the neural-network parameters learned by the parent during its lifetime.

Small random mutations are then applied to some parameters.

This creates variation between parent and offspring.

The mechanism allows the simulation to represent both:

```text
learning during lifetime
```

and:

```text
evolution between generations
```

---

## Graphical Interface

The application uses **Java Swing**.

The main window contains:

- artificial-life grid
- iteration counter
- herbivore population
- carnivore population
- plant population
- total births
- maximum generation
- simulation status
- speed control
- agent inspector
- population chart
- event log

---

## Simulation Controls

The graphical interface provides the following buttons:

### Start

Starts automatic simulation.

### Pause

Pauses the simulation.

### Step

Executes exactly one simulation iteration.

### Reset

Creates the original ecosystem again using the configured random seed.

### Speed

Controls the delay between simulation iterations.

---

## Grid Visualization

The grid displays the ecosystem visually.

### Herbivore

Displayed as a green circle:

```text
H
```

### Carnivore

Displayed as a red circle:

```text
C
```

### Plant

Displayed as a small green dot.

Animals also contain a direction marker showing the direction in which they are currently facing.

---

## Agent Inspector

A user can click a living object on the grid.

For animals, the inspector displays:

- type
- position
- direction
- energy
- energy percentage
- generation
- last action
- selected neural action
- learning reward
- 12 sensor inputs
- 4 neural outputs

This makes the internal decision-making process of the agent visible during the simulation.

---

## Population History

The graphical interface contains a live population chart.

The chart tracks:

- Herbivore population
- Carnivore population
- Plant population

over time.

This makes ecosystem changes easier to observe.

---

## Event Log

Important simulation events are recorded in the event log.

Examples:

```text
[014] 1 plant(s) eaten
[056] 1 herbivore(s) lost
[121] 1 new offspring born
```

The log helps explain how population changes occurred.

---

## Project Structure

```text
EcoAgents/
│
├── src/
│   └── ecoagents/
│       │
│       ├── Main.java
│       │
│       ├── agents/
│       │   ├── Agent.java
│       │   ├── Animal.java
│       │   ├── Herbivore.java
│       │   ├── Carnivore.java
│       │   └── Plant.java
│       │
│       ├── brain/
│       │   └── NeuralBrain.java
│       │
│       ├── environment/
│       │   └── Environment.java
│       │
│       ├── model/
│       │   ├── Action.java
│       │   ├── Direction.java
│       │   ├── Position.java
│       │   └── SensorData.java
│       │
│       ├── simulation/
│       │   ├── Simulation.java
│       │   └── SimulationConfig.java
│       │
│       └── ui/
│           ├── GameFrame.java
│           ├── EnvironmentPanel.java
│           ├── AgentDetailsPanel.java
│           ├── SimulationInfoPanel.java
│           └── PopulationChartPanel.java
│
├── docs/
│
├── README.md
│
└── .gitignore
```

The source directory also contains development and regression-test classes.

---

## Regression Tests

The project contains several tests for important simulation components.

### Toroidal World

```cmd
java -cp out ecoagents.ToroidalTest
```

Tests movement across environment boundaries.

---

### Sensor System

```cmd
java -cp out ecoagents.SensorTest
```

Tests the 12-input local perception vector.

---

### Neural Network

```cmd
java -cp out ecoagents.NeuralBrainTest
```

Tests neural output calculation and winner-takes-all selection.

---

### Animal-Brain Integration

```cmd
java -cp out ecoagents.AnimalBrainTest
```

Tests the complete:

```text
Sensors → Brain → Action → Environment
```

cycle.

---

### Learning

```cmd
java -cp out ecoagents.LearningTest
```

Tests reinforcement-based neural adaptation.

---

### Nearness Interaction

```cmd
java -cp out ecoagents.NearnessEatTest
```

Tests eating inside the eight-cell nearness region.

---

### Reproduction

```cmd
java -cp out ecoagents.ReproductionTest
```

Tests:

- reproduction threshold
- energy transfer
- child generation
- neural inheritance
- mutation

---

### Ecosystem Balance

```cmd
java -cp out ecoagents.EcosystemBalanceTest
```

Runs a complete multi-agent ecosystem simulation.

---

## Validated Simulation Result

Using random seed:

```text
42
```

the ecosystem successfully produced natural reproduction.

Example result:

```text
Iteration 121
Births: 1
Maximum Generation: 1
```

The ecosystem test continued successfully until:

```text
Iteration 400
```

with no simulation crash.

One example final result was:

```text
Herbivores: 4
Carnivores: 3
Plants: 2
Births: 1
Maximum Generation: 1
```

---

## Requirements

- Java Development Kit 21 or newer
- Windows, Linux, or macOS
- No external Java libraries are required

The graphical interface is implemented using the standard Java Swing library.

---

## Compile the Project

Open CMD and move to the project directory:

```cmd
cd /d G:\Projects\Uni-Projects\EcoAgents
```

Remove the previous compilation output:

```cmd
rmdir /s /q out
```

Create a clean output directory:

```cmd
mkdir out
```

Compile the project:

```cmd
javac -d out src\ecoagents\model\*.java src\ecoagents\brain\*.java src\ecoagents\environment\*.java src\ecoagents\agents\*.java src\ecoagents\game\*.java src\ecoagents\simulation\*.java src\ecoagents\ui\*.java src\ecoagents\*.java
```

---

## Run the Application

After compilation:

```cmd
java -cp out ecoagents.Main
```

The EcoAgents graphical application will open.

---

## Run All Main Tests

```cmd
java -cp out ecoagents.ToroidalTest
java -cp out ecoagents.SensorTest
java -cp out ecoagents.NeuralBrainTest
java -cp out ecoagents.AnimalBrainTest
java -cp out ecoagents.LearningTest
java -cp out ecoagents.NearnessEatTest
java -cp out ecoagents.ReproductionTest
java -cp out ecoagents.EcosystemBalanceTest
```

---

## Technologies and Concepts

The project demonstrates concepts from:

- Java 21
- Java Swing
- Object-Oriented Programming
- Multi-Agent Systems
- Artificial Life
- Neural Networks
- Reinforcement-Based Learning
- Evolutionary Adaptation
- Simulation Modeling
- Agent-Based Modeling

---

## Future Improvements

Possible future extensions include:

- plant regeneration
- larger ecosystems
- configurable simulation parameters
- multiple generations
- improved neural-network learning
- saving simulation statistics
- exporting population data
- additional animal species
- more advanced mutation strategies
- improved sensor geometry
- visualization of neural-network weights

---

## Author

**Mohammad Azimi**

Master's Program in Intelligent Systems
