# CZ3004_MDP-Grp18

## Introduction
Throughout MDP, students will be required to attend relevant seminars and workshops that would help them understand the process and tools for designing, developing and marketing of real-world multidisciplinary products. These include both general and technical seminars. 

General seminars may cover topics such as guided innovation, multidisciplinary problem solving, working effectively in engineering teams, understanding effective marketing strategies, project management, professional, ethical and moral responsibilities etc. Technical seminars and workshops will be related to specific engineering methodology and tools that can help students implement their respective ideas. These may include systems engineering and design, large-scale web systems, embedded systems development tools, etc. 

In their implementation, students will have to factor in appropriate considerations for public health and safety, cultural, societal factors, environmental and other constraints as well as the needs for sustainable development. 

Students will be grouped into teams of about 8 member. The composition of these team members will consist of a multidisciplinary mix of CS and CE students. 


## Overview

The algorithm acts a the brain of the robot to navigate it through the maze. It will direct the robot on which course of action to take base on the current state of the environment it is in. On top of that, a simulator will be created to validate the implementation of the algorithm for its effectiveness, efficiency and reliability on satisfying the conditions given.
This repository contains the algorithm(s) used to automate a robot through an unknown maze. 

Other components that is not included in this repository are the source code for the physical robot (Arduino Uno + Hardware), an Android application and a Raspberry Pi (communication server).


## Components

1. Editable 15 x 20 grid arena   
2. Non Editable 15 x 20 grid arena   
3. Speed terminal
4. Timer terminal in Simple Data Format (mm:ss)
5. Console terminal
6. Coverage  & Exploration stopwatch terminal
7. Simulate exploration
8. Simulate fastest path                                                                         
9. Import arena
10. Export arena                                                                                     
11. Reset arena
12. Switch to real run mode  

## Design Patterns & Practices

The algorithm team have studied and implemented design patterns and practices to ensure overall algorithm architecture components can be easily interchangeable and manageable.

1. MVCS Model
Source class files are classified and categorised into 4 category- View, Controller, Model and Service. View classes contained the simulator main, and user interface class. Controllers contains the decision manager classes such as SimAlgorithmManager.java, RealAlgorithmManager.java, StopWatch.java RPIManager.java, SocketClientManager.java, FastestPath.java. Model will consist of all object class file such as Arena.java, Robot.java, Grid.java and RobotArenaProtocol interface class. Lastly, Service will encompass Utilities.java which are used across all the other category.This paradigm is an ideal software architectural pattern as it avoids mixing code from the three categories into one class. This separation of responsibilities allows flexibility down the road. For example, because the view doesn’t care about the underlying model, supporting multiple file formats is easier by just adding a model subclass for each.

2. Multithread Communication
To reduce any dependency across the simulator software, the system architecture runs views and controllers in different threads, communicating through data only. So, even if the user-interface hangs, the main control of the physical robot will not be compromised.
3. Polymorphism 
All classes across the Controllers category implement RobotArenaProtocol class interface extensively by implementing inheritance.It plays an important role in allowing objects having different internal structures to share the same external (parent) interface. 

4. Robust TCP Connections
RPIManager is established and developed to resolve any message concatenation issues with 3 different function of a forward message for an effective communication overhead:
⋅⋅* sendInstruction – forward a message with not need of an acknowledgement.
⋅⋅* sendInstruction2 – forward a message with A NEED of BOTH acknowledgement & sensor reading from robot.
⋅⋅* sendInstruction3 – forward a message with A NEED of just an acknowledgement.
the design of SocketClient.java also handle any disconnection issue as any disconnection will allow the application to attempt for an re-connection. Each communication forward by the simulator strive to reduce the amount of possible overhead required. As such, each time the simulator forward a message, the string forwards a concatenated message for all parties which looks something like this:

> A*movement*#T*grid update*!*obstacle update*!*robot position & direction update*#

where A = Arduino, T = Tablet and # = token break for RPI. 

5. Simulation of incorrect sensor readings
During an actual run, the sensor readings forwarded by the robot might not be always accurate. Hence, a preventive measure for this, is to allow the simulator to add or remove obstacles each time the robot made a move. This would prevent “ghost” obstacles detection and failure to add detect an obstacle from happening. This resolved many issues that happen during real run even before the entire system is integrated, which led us to adopt our own unique approach to cater to certain problems, talked about in later parts.


## Simulator

The simulator was build using Java Swing which replicate the exact environment of the maze including additional feature to change the condition of the maze run. This allow the scaling of the difficulties on the maze to simulate every possible scenario to gather information for validation.

Figure 1 – Overview of Arena

On the left console of shown in Fig 1, is the editable 15 x 20 grid arena whereby it shows the start position in pink and the goal position in orange. Furthermore, the grid of the arena can be clicked to add in obstacles to simulate the maze for simulation and validation.

On the right console of Fig 1, the virtual robot(coloured in blue) is modeled in such a way that it replicates the actual size of the real robot(3×3 grid) with the yellow grid as its head.
Finally each grid will be coloured in 3 different type of colour which is to represent the obstacle, uncovered grid and clear grid using red, black and white respectively.

## Exploration

Figure 2  – Exploration Arena (click onto the image to view the gif)

The purpose of performing exploration is to map out as much details as possible of the given scenario which will be used later for the computation of the shortest path. This is achieve through the integration of multiple implementations to assure completeness.

## Wall Hugging (Left)

Figure 3 – Wall Hugging & Unforeseen circumstances

Exploration’s algorithm was implemented through the use of  the “wall hugging” method in the initial phase. As depicted in Fig 3, one side of the robot will try to remain in a predefined threshold value from the wall or obstacle while traversing. Thus, it will “hug” the outer perimeter of the maze and seek to achieve a full exploration with the aid of the sensor readings. However, in the event whereby certain area of the maze, shown in Fig 3, cannot be map out during the initial phase, clean up exploration will be done, which will be discuss under its own chapter later on.



## Algorithm for left hugging

## Grid’s Attribute

During exploration the details of the environment which the robot is expose to will be plot onto a map. Each cell will then be assigned to a condition and step cost based on the data receive from the robot.
There are 3 different pair of combination of status that could be assign to a cell which are (Not_Visited , No_Obstacle) as initial , (Visited, No_Obstacle) as clear and (Visited, Obstacle) as blocked grid. In addition, step cost are also assigned to each cell depending on the number of action steps that are performed by the robot on the particular grid.
For example, moving to a grid will allocate a value of 1 step cost to the grid and if on the same grid cell a turning occur an additional value of 1 step cost will be added to the previous value allocated.

## Clean Up Exploration

Figure 4 – Cleaning Up Exploration (2 rounds) (click onto the image to view the gif)

To provide coverage for uncovered areas from “wall hugging” algorithm due to unforeseen situations, illustrated in Fig 3, is the use of Dijkstra algorithm to explore the rest of the unvisited grid cell to complete the full exploration.

The heuristic function used for Dijkstra algorithm is based on the computation of the step cost assigned to each visited grid cell during the initial exploration. First, the implementation of the heuristic function computation will read in the 15 x 20 grid and check for each cell if it is accessible.
Then for each accessible cell it will retrieve the step cost assigned to it and add them up whereby the value will then be reassigned to each of the cell to act as the heuristic function for Dijkstra algorithm.
Finally, Dijkstra algorithm will apply the best first search to select the path to the unexplored area to complete the full exploration with the minimal time. A snipped code of the heuristic function computation is shown in Figure 6 later on.



### Sensor Data Processing

The algorithm has to implement a function to read the sensor data and plot the map based on the data receive. As both the long range and short range sensors were installed onto the robot, there will be two parts of implementation. The first part of will read up to a maximum of two grids. Thus, for every short range sensor, the implemented function will return the condition of the two grids of its respective side to plot the map. The second part will start from where the short range sensor had left off and stop at a maximum of five grids in total. This implementation is only applicable to the long range right sensor to improve the coverage efficiency with the left “wall hugging” approach taken.

In addition to the basic functionalities of processing the sensor data, an extension was made to overwrite nonsensical reading receive from the robot due to a voltage spike. As the robot traverse through the maze, each time it will return a new reading of the environment and priority had been set for the short range sense data to overwrite what had been plotted previously for the first two grid. This is to increase the assurance of the correctness of the map being plot to avoid any catastrophic circumstances (plot when not supposed to, vice versa) which render a failure in the exploration run.

## Our Initiatives

1. Robot Circling Preemption
As the exploration algorithm requires the robot to stick to wall (preferably left). This allowed the robot to walk around the entire maze. However, there might be incidents where sensors readings isn’t accurate. which result in sudden “ghost” obstacle to be viewed immediately in front of the robot front grids. it is possible for the robot to get into a situation where the right side of the robot do not have obstacles or wall at all. In such cases, the robot might keep turning right, which results in robot circling, a common issue identified.

To solve this problem in the algorithm level, every step the robot will perform checking to make decision for next step. Should the above situation is detected, a preemption will force the robot to first turn right and then keep moving forward until it reached an obstacle(or wall). Then it will turn left and then continue with exploration.

2. Turbo Boost for Exploration


Example of a boost when robot is facing north: 
An additional feature that was implemented in Week 11 was the use of a forward thinking concept. This can be done at each movement whereby the robot will predict if the next 2-4 steps has already been visited previously. If yes, the simulator will forward an additional maximum “greedy” movement step of 6 (a boost) speeding up the exploration. Especially useful when robot has 100% coverage and dashing back to start point, or when robot u-turns out of a deadend, it can dash out instead of grid by grid.


The logic of this concept is to split the robot into 3 zones. The robot will predict the maximum amount of steps it can dash from its front, its left and lastly its right. Once done, the simulator will do a math function to get the minimum greedy steps it is allowed to run. After the result is generated, it will perform a check to see if the greedy move will exceed the arena zone, if yes, a while loop will be done to decrease the “greedy” steps and return the boost value back to its wall hugging algorithm.

3. Time and Coverage (Additional features)

These additional features implemented will assist in providing the assurance of the correctness of the algorithm. For time limit, it is implemented to ensure that the maximum traverse time through the maze is within 6 minutes, else it will be terminated. Whereas, coverage is used to ensure that the function implemented use to keep track, if further exploration is required, is working as intended.

## Shortest Path

Figure 6 Shortest Path Algorithm – Dijkstra Algorithm
After exploration is done, computation of the shortest path based on the information obtained is required. In order to do this the heuristic function used by the informed search strategy must be carefully devised to avoid falling into an endless loop or a selection of an undesirable path.

## Heuristic Function



Figure 7 – Step Cost                                                        

Figure 8 – Heuristic Computation

The heuristic function used by Dijkstra algorithm to identify the shortest path will be based on the addition of the step cost of each accessible grid. The algorithm will then simulate all the possible paths from start to waypoint and from waypoint to goal. An example of how the heuristic function work is shown in Fig 7 and 8. During the simulation of possible paths, if there is a path resulting in moving straight grid to a turning grid it will automatically increase the step cost of the 3×3 grid it is on by 1. This way the algorithm sets moving straight as priority, to reduce time consuming turn commands.
