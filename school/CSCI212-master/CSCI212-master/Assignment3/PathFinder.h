#ifndef PATHFINDER_H
#define PATHFINDER_H

#include "SubmitMazeSoln.h"
// #include "Path.h"
// #include "Maze.h"
// #include "ProgramLog.h"
// #include "Assignm3_Utils.h"
#include "Assign3.h"

// #include <cstdlib>
// #include <string>
#include <ctime>
// #include <pthread.h>
#include <algorithm>
#include <unistd.h>
#include <signal.h>
#include <streambuf>

using namespace std;
using namespace Assign3;

const string fileName = "mazedata.txt";
const string studentName = "Lindi Lamduan Wong";
const string stuentID = "4872721";

Maze * mazeSolution;
VectorOfPointStructType barriers;
VectorOfVectorOfPointStructType allPaths;

time_t startTime, endTime;

bool areThreadsActive[MAX_NO_OF_THREADS];

int activeThreads;
int submittedPaths;
int numSoln;
int maxNoOfSolutions;

int start();	// start the process
int create();	// create threads
void explore();	// display paths
int end();		// end the process


#endif
