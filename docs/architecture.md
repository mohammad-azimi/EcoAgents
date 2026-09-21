# EcoAgents Architecture

## 1. Overview

EcoAgents is an artificial-life simulation implemented in Java.

The system models an ecosystem containing:

- Herbivores
- Carnivores
- Plants

Animals are autonomous agents.

Each animal:

1. perceives its local environment,
2. converts perception into sensor inputs,
3. processes the inputs using a neural decision system,
4. selects one action,
5. executes the action,
6. receives a learning reward,
7. adapts its neural parameters,
8. spends or gains energy,
9. may reproduce when enough energy is available.

The complete architecture can be summarized as:

```text
                 ┌───────────────────────┐
                 │      Environment      │
                 │    Toroidal Grid      │
                 └───────────┬───────────┘
                             │
                             ▼
                 ┌───────────────────────┐
                 │        Sensors        │
                 │      12 Inputs        │
                 └───────────┬───────────┘
                             │
                             ▼
                 ┌───────────────────────┐
                 │     Neural Brain      │
                 │  Weights + Biases     │
                 └───────────┬───────────┘
                             │
                             ▼
                 ┌───────────────────────┐
                 │    4 Neural Outputs   │
                 └───────────┬───────────┘
                             │
                             ▼
                 ┌───────────────────────┐
                 │   Winner Takes All    │
                 └───────────┬───────────┘
                             │
                             ▼
        ┌─────────────────────────────────────────┐
        │ TURN_LEFT | TURN_RIGHT | MOVE | EAT    │
        └────────────────────┬────────────────────┘
                             │
                             ▼
                 ┌───────────────────────┐
                 │   Environment Change  │
                 └───────────┬───────────┘
                             │
                             ▼
                 ┌───────────────────────┐
                 │        Reward         │
                 └───────────┬───────────┘
                             │
                             ▼
                 ┌───────────────────────┐
                 │       Learning        │
                 └───────────┬───────────┘
                             │
                             ▼
                 ┌───────────────────────┐
                 │ Energy / Reproduction │
                 └───────────────────────┘
```

---

# 2. Package Architecture

The project is divided into several packages.

```text
ecoagents
│
├── agents
│
├── brain
│
├── environment
│
├── model
│
├── simulation
│
└── ui
```

Each package has a different responsibility.

---

# 3. Model Package

Package:

```text
ecoagents.model
```

Main classes:

```text
Action.java
Direction.java
Position.java
SensorData.java
```

These classes represent the basic data structures used by the simulation.

---

## 3.1 Position

`Position` represents one cell in the environment.

A position contains:

```text
row
column
```

Example:

```text
(3, 5)
```

The class provides operations such as:

```text
translate()
move()
manhattanDistance()
```

Position objects are also used when implementing toroidal wrapping.

---

## 3.2 Direction

Every animal has one orientation.

Possible directions are:

```text
NORTH
EAST
SOUTH
WEST
```

Each direction contains:

```text
rowDelta
columnDelta
```

For example:

```text
NORTH = (-1, 0)
EAST  = ( 0, 1)
SOUTH = ( 1, 0)
WEST  = ( 0,-1)
```

The class also provides:

```text
turnLeft()
turnRight()
```

Example:

```text
NORTH → turnRight() → EAST
EAST  → turnRight() → SOUTH
```

---

## 3.3 Action

Each animal can choose one of four actions:

```text
TURN_LEFT
TURN_RIGHT
MOVE_FORWARD
EAT
```

These actions correspond directly to the four neural-network outputs.

---

## 3.4 SensorData

`SensorData` stores the twelve environmental inputs perceived by an animal.

The inputs are:

```text
HF CF PF
HL CL PL
HR CR PR
HN CN PN
```

They represent:

| Symbol | Meaning                |
| ------ | ---------------------- |
| HF     | Herbivores in Front    |
| CF     | Carnivores in Front    |
| PF     | Plants in Front        |
| HL     | Herbivores on Left     |
| CL     | Carnivores on Left     |
| PL     | Plants on Left         |
| HR     | Herbivores on Right    |
| CR     | Carnivores on Right    |
| PR     | Plants on Right        |
| HN     | Herbivores in Nearness |
| CN     | Carnivores in Nearness |
| PN     | Plants in Nearness     |

`SensorData` can convert these values into the neural-network input vector:

```text
double[12]
```

---

# 4. Environment Package

Package:

```text
ecoagents.environment
```

Main class:

```text
Environment.java
```

The Environment manages the artificial world.

Responsibilities include:

- grid dimensions
- agent storage
- toroidal wrapping
- movement
- collision detection
- local sensing
- searching neighboring cells
- random empty-cell selection
- reproduction placement

---

# 5. Toroidal World

The environment uses toroidal topology.

There are no hard boundaries.

For example, in a world with 12 columns:

```text
(3, 11) moving EAST
        ↓
(3, 0)
```

Similarly:

```text
Top → Bottom
Bottom → Top
Left → Right
Right → Left
```

The implementation uses:

```java
Math.floorMod(...)
```

to wrap coordinates.

Conceptually:

```text
┌──────────────────────────┐
│                          │
│  ←────────────────────→  │
│                          │
└──────────────────────────┘
        ↑              ↓
        └──────────────┘
```

This means the artificial world behaves like the surface of a torus.

---

# 6. Sensor Architecture

Animals cannot observe the entire grid.

Instead, every animal has local perception.

The sensor system contains four logical areas:

```text
Front
Left
Right
Nearness
```

Each region can detect:

```text
Herbivores
Carnivores
Plants
```

Therefore:

```text
4 regions × 3 object types = 12 inputs
```

---

## 6.1 Nearness

In the EcoAgents implementation, Nearness is defined as all eight neighboring cells around the animal.

```text
N N N
N A N
N N N
```

Where:

```text
A = current animal
N = Nearness cell
```

This implementation allows an animal to interact with nearby food even if the food is not directly in front of it.

---

## 6.2 Front

Front represents cells further ahead in the animal's current direction.

Example for an animal facing EAST:

```text
A . F F
```

The first adjacent cells are already part of Nearness.

The farther cells are classified as Front.

---

## 6.3 Left and Right

The Left and Right areas depend on the current orientation.

For an animal facing NORTH:

```text
      FRONT

LEFT    A    RIGHT
```

If the animal turns, the meaning of Front, Left, and Right rotates with it.

This makes perception relative to the agent rather than relative to the global grid.

---

# 7. Agent Architecture

Package:

```text
ecoagents.agents
```

Main classes:

```text
Agent
Animal
Herbivore
Carnivore
Plant
```

The inheritance structure is:

```text
             Agent
            /     \
           /       \
       Animal      Plant
       /    \
      /      \
Herbivore  Carnivore
```

---

# 8. Agent Base Class

`Agent` contains properties shared by ecosystem entities.

Important fields include:

```text
name
symbol
position
direction
alive
score
lastAction
```

It also contains methods for:

```text
getPosition()
setPosition()

getDirection()

turnLeft()
turnRight()

isAlive()
markDead()
```

---

# 9. Animal Class

`Animal` extends `Agent`.

It contains the functionality shared by Herbivores and Carnivores.

Important fields include:

```text
NeuralBrain brain

double energy
double maxEnergy

int generation

SensorData lastSensorData
Action lastChosenAction

double lastLearningReward
```

`Animal` is responsible for the complete decision cycle:

```text
sense
  ↓
choose action
  ↓
execute action
  ↓
calculate reward
  ↓
learn
  ↓
update energy
```

---

# 10. Herbivore

`Herbivore` extends `Animal`.

Its main ecological objective is to find and consume plants.

Food rule:

```text
Plant → +1 energy
```

The Herbivore has species-specific learning guidance.

Examples:

```text
Plant Near  → EAT
Plant Front → MOVE_FORWARD
Plant Left  → TURN_LEFT
Plant Right → TURN_RIGHT
```

---

# 11. Carnivore

`Carnivore` extends `Animal`.

Its ecological objective is to find and consume Herbivores.

Food rule:

```text
Herbivore → +2 energy
```

Species-specific learning guidance includes:

```text
Herbivore Near  → EAT
Herbivore Front → MOVE_FORWARD
Herbivore Left  → TURN_LEFT
Herbivore Right → TURN_RIGHT
```

---

# 12. Plant

`Plant` extends `Agent`, but it is not an autonomous animal.

Plants:

```text
do not move
do not turn
do not contain a neural network
do not learn
```

They are environmental food resources.

---

# 13. Neural Brain Architecture

Package:

```text
ecoagents.brain
```

Main class:

```text
NeuralBrain.java
```

Each animal owns one `NeuralBrain`.

The network contains:

```text
12 inputs
4 outputs
```

Internally:

```text
double[4][12] weights
double[4] biases
```

---

# 14. Neural Calculation

For every output:

```text
Out(i) = Bias(i) + Σ Weight(i,j) × Input(j)
```

Where:

```text
i = neural output
j = sensor input
```

For example:

```text
EAT output =
bias(EAT)
+
w1 × HF
+
w2 × CF
+
...
+
w12 × PN
```

---

# 15. Winner-Takes-All Decision

After calculating all four neural outputs, the largest output wins.

Example:

```text
TURN_LEFT    = 0.70
TURN_RIGHT   = 0.10
MOVE_FORWARD = 1.50
EAT          = 2.10
```

Maximum value:

```text
2.10
```

Therefore:

```text
Selected Action = EAT
```

The mapping is:

```text
Output 0 → TURN_LEFT
Output 1 → TURN_RIGHT
Output 2 → MOVE_FORWARD
Output 3 → EAT
```

---

# 16. Learning Architecture

The neural network adapts during the animal's lifetime.

The implemented update rule is:

```text
weight =
weight
+
learningRate × reward × input
```

The selected action bias is also updated:

```text
bias =
bias
+
learningRate × reward
```

Current learning rate:

```text
0.05
```

---

# 17. Reward System

Basic feedback includes:

```text
Successful EAT       → +1.00
Failed EAT           → -0.10

Successful MOVE      → +0.02
Blocked MOVE         → -0.05

TURN_LEFT / RIGHT    → -0.01
```

Additional species-specific reward shaping is also applied.

For example, a Herbivore receives extra positive feedback when it selects:

```text
EAT
```

while a Plant is detected in Nearness.

---

# 18. Exploration

Pure winner-takes-all behavior can cause an animal to repeat the same action forever.

To reduce this problem, the real simulation uses exploration.

Current exploration probability:

```text
10%
```

Therefore:

```text
90% → neural-network decision
10% → random exploratory action
```

Unit tests can disable exploration to remain deterministic.

---

# 19. Energy System

Animals have:

```text
Maximum Energy = 20.0
Initial Energy = 16.0
```

Every iteration consumes:

```text
0.02 energy
```

Food restores energy:

```text
Herbivore + Plant     → +1 energy
Carnivore + Herbivore → +2 energy
```

Energy cannot exceed:

```text
20.0
```

If:

```text
Energy <= 0
```

the animal dies.

---

# 20. Reproduction

The reproduction threshold is:

```text
90% of maximum energy
```

With:

```text
Maximum Energy = 20
```

this gives:

```text
20 × 0.90 = 18
```

Therefore an animal can reproduce when:

```text
Energy >= 18
```

---

# 21. Reproduction Process

The process is:

```text
Parent reaches energy threshold
             ↓
Find empty neighboring cell
             ↓
Copy parent's NeuralBrain
             ↓
Apply small mutations
             ↓
Create child
             ↓
Generation + 1
             ↓
Transfer energy from parent to child
```

The child receives:

```text
40% of maximum energy
```

For maximum energy 20:

```text
Child Energy = 8
```

Example:

```text
Parent before reproduction = 18

Child receives             = 8

Parent after reproduction  = 10
```

---

# 22. Neural Inheritance

The offspring receives a copy of the parent's current neural parameters.

This means that learned neural values can be inherited.

The child receives:

```text
parent weights
parent biases
```

before mutation.

---

# 23. Mutation

After inheritance, small random mutations are applied.

Current configuration:

```text
Mutation probability = 10%
Mutation strength    = 0.10
```

Mutation creates variation between generations.

This allows:

```text
parent
  ↓
similar child
  ↓
small neural differences
```

The simulation therefore combines:

```text
Learning during lifetime
+
Inheritance between generations
+
Mutation
```

---

# 24. Simulation Engine

Package:

```text
ecoagents.simulation
```

Main classes:

```text
Simulation.java
SimulationConfig.java
```

The Simulation class controls the complete ecosystem.

---

# 25. Simulation Iteration

One iteration follows approximately this sequence:

```text
Start iteration
      ↓
Create list of living animals
      ↓
Shuffle execution order
      ↓
Animal 1 acts
      ↓
Animal 2 acts
      ↓
...
      ↓
All living animals act
      ↓
Check reproduction
      ↓
Create offspring if possible
      ↓
Update population state
      ↓
Check simulation end condition
```

New offspring begin acting from the next iteration.

---

# 26. Randomized Action Order

The execution order of animals is randomized during each iteration.

This prevents one animal or species from always receiving the first opportunity to act.

Conceptually:

```text
Iteration 1:
H1 → C2 → H3 → C1

Iteration 2:
C1 → H3 → H1 → C2
```

---

# 27. Simulation Configuration

Current default configuration:

```text
Grid rows             = 15
Grid columns          = 20

Initial Herbivores    = 8
Initial Carnivores    = 3
Initial Plants        = 25

Maximum Iterations    = 400
```

The simulation uses:

```text
Seed = 42
```

for the default GUI run.

Using a fixed seed makes development behavior reproducible.

---

# 28. User Interface Architecture

Package:

```text
ecoagents.ui
```

Main classes:

```text
GameFrame
EnvironmentPanel
AgentDetailsPanel
SimulationInfoPanel
PopulationChartPanel
```

---

# 29. GameFrame

`GameFrame` is the main Swing window.

It combines:

```text
Header statistics
Environment grid
Agent inspector
Simulation inspector
Control buttons
Speed control
```

It also owns the Swing `Timer` used for automatic simulation execution.

---

# 30. EnvironmentPanel

`EnvironmentPanel` draws the artificial world.

It renders:

```text
Herbivore → green circle with H
Carnivore → red circle with C
Plant     → small green dot
```

Animals also display their current orientation.

The panel detects mouse clicks.

When the user clicks an Agent, it becomes the selected Agent.

---

# 31. AgentDetailsPanel

The Agent inspector displays internal state.

For animals it shows:

```text
Type
Position
Direction
Energy
Energy percentage
Generation
Last action
Chosen neural action
Learning reward
12 sensor inputs
4 neural outputs
```

This panel allows the internal decision process to be observed while the simulation is running.

---

# 32. SimulationInfoPanel

This panel displays ecosystem-level information.

It contains:

```text
Maximum generation
Total births
Population History
Event Log
```

---

# 33. PopulationChartPanel

The population chart records:

```text
Herbivore population
Carnivore population
Plant population
```

for every iteration.

The chart helps visualize ecosystem evolution over time.

---

# 34. Event Log

The Event Log records important population changes.

Examples:

```text
[014] 1 plant(s) eaten
[035] 1 herbivore(s) lost
[121] 1 new offspring born
```

This makes simulation events easier to explain during a demonstration.

---

# 35. Main Application Entry Point

The program starts from:

```text
ecoagents.Main
```

`Main` launches the Swing interface using:

```text
SwingUtilities.invokeLater(...)
```

This ensures that Swing components run on the Event Dispatch Thread.

---

# 36. Main Runtime Architecture

The runtime relationship between the major components is:

```text
Main
 │
 ▼
GameFrame
 │
 ├──────────────► EnvironmentPanel
 │                     │
 │                     ▼
 │                 Environment
 │                     │
 │                     ▼
 │                   Agents
 │
 ├──────────────► AgentDetailsPanel
 │
 ├──────────────► SimulationInfoPanel
 │                     │
 │                     ▼
 │              PopulationChartPanel
 │
 ▼
Simulation
 │
 ▼
Environment
 │
 ▼
Animals
 │
 ▼
NeuralBrain
```

---

# 37. Complete Agent Decision Flow

For one Herbivore:

```text
Herbivore
    │
    ▼
Environment.sense()
    │
    ▼
SensorData
    │
    │
    ├── HF
    ├── CF
    ├── PF
    ├── HL
    ├── CL
    ├── PL
    ├── HR
    ├── CR
    ├── PR
    ├── HN
    ├── CN
    └── PN
    │
    ▼
NeuralBrain.evaluate()
    │
    ▼
4 outputs
    │
    ▼
Winner Takes All
    │
    ▼
Action
    │
    ├── TURN_LEFT
    ├── TURN_RIGHT
    ├── MOVE_FORWARD
    └── EAT
    │
    ▼
Reward
    │
    ▼
NeuralBrain.learn()
```

---

# 38. Artificial Life Requirements vs Implementation Decisions

It is important to distinguish between the general artificial-life model and the specific implementation choices used in EcoAgents.

---

## Model Requirements Implemented

The project implements the following main artificial-life concepts:

```text
Toroidal environment

Carnivorous animals

Herbivorous animals

Plants

Local perception

Front / Left / Right / Nearness sensors

12 environmental sensor values

Neural decision system

Weights and biases

4 output actions

Winner-takes-all decision

Energy consumption

Food energy

Death from zero energy

Reproduction at 90% energy

Neural-network inheritance
```

---

## EcoAgents Design Decisions

Some implementation details are not uniquely defined by the general model.

The project therefore makes explicit engineering decisions.

### Nearness Geometry

EcoAgents uses:

```text
8 neighboring cells
```

for Nearness.

---

### Sensor Range

Front, Left, and Right use a limited local range.

---

### Learning Algorithm

EcoAgents uses a simple reinforcement-style update:

```text
weight += learningRate × reward × input
```

---

### Exploration

EcoAgents uses:

```text
10% exploration probability
```

to prevent agents from remaining permanently stuck in one action.

---

### Metabolism

EcoAgents uses:

```text
0.02 energy per iteration
```

as the metabolism cost.

---

### Initial Energy

Animals begin with:

```text
16 / 20 energy
```

---

### Offspring Energy

During reproduction:

```text
8 energy
```

is transferred to the child.

---

### Mutation

Inherited neural parameters receive small mutations.

---

### Random Execution Order

Animal execution order is shuffled every iteration.

These values are simulation design parameters and can be changed in future experiments.

---

# 39. Testing Architecture

The project uses several development tests.

```text
ToroidalTest
SensorTest
NeuralBrainTest
AnimalBrainTest
LearningTest
NearnessEatTest
ReproductionTest
EcosystemBalanceTest
```

Each test focuses on one important subsystem.

---

## ToroidalTest

Checks:

```text
East edge  → West side
West edge  → East side
North edge → South side
South edge → North side
```

---

## SensorTest

Checks the complete 12-value sensor vector.

---

## NeuralBrainTest

Checks:

```text
weighted neural outputs
biases
winner-takes-all
action mapping
```

---

## AnimalBrainTest

Checks the full:

```text
Sensor
↓
Brain
↓
Action
↓
Environment change
```

cycle.

---

## LearningTest

Checks whether successful behavior changes the neural output in the expected direction.

Example:

```text
EAT before = 1.0000
EAT after  = 1.1500
```

---

## NearnessEatTest

Checks whether food located in the local eight-cell Nearness region can be consumed.

---

## ReproductionTest

Checks:

```text
90% threshold
offspring creation
generation increment
energy transfer
neural inheritance
mutation
```

---

## EcosystemBalanceTest

Runs the complete multi-agent simulation.

A validated run using seed 42 produced:

```text
Iteration 121
Births: 1
Maximum Generation: 1
```

and successfully continued until iteration 400.

---

# 40. Current Validated Architecture

The current implementation has successfully demonstrated:

```text
Toroidal environment             ✓
Local sensing                    ✓
12 neural inputs                 ✓
4 neural outputs                 ✓
Winner-takes-all                 ✓
Movement                         ✓
Turning                          ✓
Herbivore feeding                ✓
Carnivore hunting                ✓
Energy metabolism                ✓
Agent death                      ✓
Neural adaptation                ✓
Exploration                      ✓
Reproduction                     ✓
Neural inheritance               ✓
Mutation                         ✓
Multiple generations             ✓
Interactive Swing interface      ✓
Agent inspection                 ✓
Population visualization         ✓
Event logging                    ✓
```

---

# 41. Possible Future Architecture Extensions

Possible future improvements include:

```text
Plant regeneration

More animal species

Longer evolutionary runs

Saving simulation history

CSV export

Different neural architectures

Hidden neural layers

More sophisticated reinforcement learning

Genetic crossover

Adaptive mutation rates

Configurable sensor geometry

Configurable energy parameters

Simulation parameter editor

Multiple random seeds

Statistical experiment mode

Neural-weight visualization
```

---

# 42. Conclusion

EcoAgents separates the artificial-life simulation into clear architectural layers:

```text
Model
Environment
Agents
Brain
Simulation
User Interface
```

The architecture supports both:

```text
individual adaptation
```

through neural learning, and:

```text
population evolution
```

through reproduction, inheritance, and mutation.

The result is an interactive multi-agent artificial-life system in which autonomous animals perceive their environment, make neural decisions, interact with other entities, learn during their lifetime, and can create new generations.
