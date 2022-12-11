## The Sound Of Music - A assignment by Skedulo for Backend engineer

Hi! Welcome to the my github. This is my work for the Skedulo's Programming Challenge that creates an optimal schedule for Sally Salamander at a music festival, ensuring that she can see her favourite shows!

For all the details of this assignment, you can find from Skedulo github [here](https://github.com/Skedulo/backend-tech-test)

## Solution 
1. Load the performances from json input file. 
2. Sort the list performances 
    * Sort the list performances by start time => **sortedPerformances** list
    * create a new empty **optimalOrderedPerformances** list which will be used to hold the corrected ordered of all the Performances that Sally Salamander can see.
3. Find and populate the first performance
    * Find the first performance in **sortedPerformances** list
        * First performance is the one starts earliest, if there are multiple performances have the same start time, get the one with highest priority.
    * Add the first show to the **optimalOrderedPerformances** list
    * Remove the first show from **sortedPerformances** list
4. Find and populate the rest performances
    * ### Find the next performance
        * Check if the **sortedPerformances** is empty then return, finish the finding.
        * Again find the first performance in **sortedPerformances** list (same rule in the step 3) => calls it as nextCandidate
        * If nextCandidate's priority is lower than the previous performance in the **optimalOrderedPerformances** AND nextCandidate finishes before previous performance, remove the nextCandidate from **sortedPerformances**, find another the next performance
        * If the nextCandidate's priority higher than the previous performance => this is a good one
        * ELSE If there is no betterCandidate then the nextCandidate is a good one, betterCandidates could exist if
            * The nextCandidate's priority is lower than the previous performance AND
            * There are/is other candidates from **sortedPerformances** that have higher priority than the previous performance in **optimalOrderedPerformances** list
        * If there are/is better candidates, get the earliest start one.
        * Handling duplicated start time + with/ w/o priority
        * Removed the unsee performances from **sortedPerformances** that Sally won't want to see base on the nextCandidate (priority, start/finish time).
    * ### Populate the next performance and continue with the rest
        * IF there is no next performance, check in the **optimalOrderedPerformances** to see if any lower priority performance finished after the last one in the list, If have, re-add the highest priority to the end of the list.
        * Add the found next performance into **optimalOrderedPerformances** list
        * Remove the found next performance from **sortedPerformances** list
        * Re-add an exist performance (highest priority if multiple found) in **optimalOrderedPerformances** to the end of the list if 
            * that extended performance has lower priority than the next performance AND has finsh time after the next performance.
            * there is no performance from **sortedPerformances** list that has higher priority than that extended performance AND has start time before the next performance's finish.
        * Continue find and populate the rest until there is no next Candidate.
        
5. Update start and finish time of optimal ordered performances in the **optimalOrderedPerformances** list

6. Write the result to json out put file with extension ‘.optimal.json’.

### Some sides logics
    * Handling time zone on start/finish date during deserializing/serializing.
    * Ingore null property for performance's priority during serializing.
    * For Unit test: I don't have time to provide it (at the time I wrote this readme), but will provide as much as I can.

## Build/Execute Instructions
* I use Java (version 8) to build this scheduler and Maven to manage dependencies, package the jar.
* I provide the build.bat/sh to build this schedular, Maven (mvn) need to install in order to build the project. The result of the build are (on the same folder/directory)
    * dependency-jars folder contains all the dependended jars.
    * the execuated jar file: TheSoundOfMusic-1.0-SNAPSHOT.jar
* I provide the run.bat/sh to run the executed jar file.
    * the run script would be very simple, just need the path to the json input file as a parameter
    ```
    java -jar TheSoundOfMusic-1.0-SNAPSHOT.jar $1
    ```
    * the result of run process is a file same name with input file but with extension ‘.optimal.json’.
* I provide the skedulo-verify.sh to verify the solution (trigger after the build.bat/sh script): This will copy the jars, run.sh file into skedulo-verify folder then execute the original verify-music.sh (copied from [Skedulo/backend-tech-test repo](https://github.com/Skedulo/backend-tech-test/blob/master/backend/music-schedule/verifier/verify-music.sh) )
* I pushed the dependended jars and TheSoundOfMusic-1.0-SNAPSHOT.jar into this repository in case you don't have Maven in your machine.
