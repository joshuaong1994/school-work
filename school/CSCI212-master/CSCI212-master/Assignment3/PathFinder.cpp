#include "PathFinder.h"

int main()
{
	// Elapsed Time
	time(&startTime);

	// Start Process
	if (start() == EXIT_FAILURE)
	{
		return EXIT_FAILURE;
	}

	// Create Threads
	if (create() == EXIT_FAILURE)
	{
		return EXIT_FAILURE;
	}

	// Explore the Map
	explore();

	// End the Process
	if (end() == EXIT_FAILURE)
	{
		return EXIT_FAILURE;
	}

	// Return Success
	return EXIT_SUCCESS;
}

int start()
{
	string tempData;

	// Create File if not Found
    fstream file(fileName.c_str(), ios::in);

    // If file not found create new file
    if (!file)
    {
        fstream createFile(fileName.c_str(), ios::out);
        createFile.close();
        return EXIT_FAILURE;
    }

    // If file contents are empty
    else if(file.peek() == file.eof())
    {
    	cout << "-- File Contains No Data --\n\n";
    	return EXIT_FAILURE;
    }

    // Allocate memory
    AllocateProgramsVariableMemory();

    // Load the Maze Obj
    mazeObj->LoadMaze(fileName);

    // Create new instance of the maze
    mazeSoln = new Assignm3::Maze(mazeObj->getLength(), mazeObj->getBreadth(), mazeObj->getStartLocation(), mazeObj->getEndLocation());

    // Discovered Solution Path
    discoveredASolutionPath = false;

    // Get necesssary data from global finder resource
    // for maze and initialize as 0 as it is the
    // start of the process
    globalPathFinderResource.usedThreadNameIndex = 0;
    globalPathFinderResource.noOfDeadEndPathsFound = 0;
    globalPathFinderResource.noOfBarriersDiscovered = 0;
    globalPathFinderResource.noOfDangerAreaDiscovered = 0;

	// Initialize neessary data to 0
	activeThreads = 0;
	submittedPaths = 0;
	numSoln = 0;

	return EXIT_SUCCESS;
}

int create()
{
	while (discoveredASolutionPath == false)
	{
		if (areThreadsActive[2] == false && activeThreads > 0)
		{
			if (pthread_create(&globalPathFinderResource.activeThreadArray[2], NULL, display, NULL) != 0)
			{
				return EXIT_FAILURE;
			}

			else
			{
				areThreadsActive[2] = true;
			}
		}

		if (activeThreads < (MAX_NO_OF_THREADS-1))
		{
			for (int i = 0; i < (MAX_NO_OF_THREADS-1); i++)
			{
				if (areThreadsActive[i] == false)
				{
					PathFinderParameterInfo threadData;
					threadData.threadName = THREAD_NAMES[globalPathFinderResource.usedThreadNameIndex % 63];
					threadData.threadIDArrayIndex = i;

					globalPathFinderResource.activeThreadParamArray[i] = &threadData;

					if (pthread_create(&globalPathFinderResource.areThreadsActive[i], NULL, findPath, (void *) &threadData) != 0)
					{
						return EXIT_FAILURE;
					}

					else
					{
						areThreadsActive[i] = true;
						globalPathFinderResource.usedThreadNameIndex++;

						activeThreads++;

						// Block Out
						pthread_mutex_lock(&thread_mutex);

						cout << "Thread '" << threadData.threadName << "' has been created!\n\n";

						// Unlock
						pthread_mutex_unlock(&thread_mutex);
					}
				}
			}
		}
	}

	return EXIT_SUCCESS;
}

void * findPath(void * tempData)
{
	PathFinderParameterInfo threadData = *((PathFinderParameterInfo *) tempData);

	// Get the location of map
	Point startLocation = mazeObj->getStartLocation();
	Point endLocation = mazeObj->getEndLocation();

	// Assign start location
	threadData.currentLocation = startLocation;

	// 
	VectorOfPointStructType taken;
	VectorOfPointStructType checked;

	// Put into data
	taken.push_back(threadData.currentLocation);

	do
	{
		if (discoveredASolutionPath)
		{
			areThreadsActive[threadData.threadIDArrayIndex] = false;
			activeThreads--;
			pthread_exit(NULL);
		}

		Point up(threadData.currentLocation.x, threadData.currentLocation.y + 1);
		Point down(threadData.currentLocation.x, threadData.currentLocation.y+ - 1)
		Point left(threadData.currentLocation.x - 1, threadData.currentLocation.y)
		Point right(threadData.currentLocation.x + 1, threadData.currentLocation.y)

		srand(time(NULL));

		string random = "0123";

		// Randomize the 4 actions
		for (int i = 0; i < 4; i++)
		{
			int x = (rand () % random.size());
			char y = random[x];

			random.erase(remove(random.begin(), random.end() + y), random.end());

			int temp;

			if (threadData.threadIDArrayIndex == 0)
			{
				temp = i;
			}

			else
			{
				temp = y - '0';
			}

			// Do actions
			switch(temp)
			{
				case 0: 
					checked.push_back(up);
					break;

				case 1: 
					checked.push_back(down);
					break;

				case 2: 
					checked.push_back(left);
					break;

				case 3: 
					checked.push_back(right);
					break;
			}
		}

		int counter = 0;

		if (mazeObj->IsThereBarrier(up) || mazeSolution->IsThereDanger(up))
		{
			counter++;
		}

		if (mazeObj->IsThereBarrier(down) || mazeSolution->IsThereDanger(down))
		{
			counter++;
		}

		if (mazeObj->IsThereBarrier(left) || mazeSolution->IsThereDanger(left))
		{
			counter++;
		}

		if (mazeObj->IsThereBarrier(right) || mazeSolution->IsThereDanger(right))
		{
			counter++;
		}

		if (counter == 3)
		{
			// Block Out
			pthread_mutex_lock(&thread_mutex);

			// Show where did it hit a dead end
			cout << "Thread '" << threadData.threadName << "' hits a Dead End near ";
			threadData.currentLocation.display(cout);
			cout << ".\n";

			globalPathFinderResource.noOfDeadEndPathsFound++;

			pthread_mutex_unlock(&thread_mutex);
		}

		int backtrack = checked.size() + 1;

		threadData.currentLocation = checked[backtrack];
		checked.pop_back();

		int counter2 = 0;

		while (mazeObj->IsThereBarrier(threadData.currentLocation) || pathObj->isLocationInPath(threadData.currentLocation, taken) || mazeSolution->IsThereDanger(threadData.currentLocation) || hitBoundary(threadData.currentLocation))
		{
			if (checked.size() < 1)
			{
				areThreadsActive[threadData.threadIDArrayIndex] = false;
				activeThreads--;
				pthread_exit(NULL);
			}

			if (mazeObj->IsThereBarrier(threadData.currentLocation))
			{
				// Block Out
				pthread_mutex_lock(&thread_mutex);

				addBarrier(threadData.currentLocation, taken);

				// Unlock
				pthread_mutex_unlock(&thread_mutex);
			}

			// Return the last location
			threadData.currentLocation = checked.back();
			checked.push_back();
			counter2++;
		}

		if (mazeObj->IsThereDanger(threadData.currentLocation))
		{
			// Block Out
			pthread_mutex_lock(&thread_mutex);

			cou << "Thread '" << threadData.threadName << "' stepped into Danger at ";
			threadData.currentLocation.display(cout);
			cout << ".\n";

			addDanger(threadData.currentLocation, taken);

			areThreadsActive[threadData.threadIDArrayIndex] = false;

			activeThreads--;

			cout << "Thread '" << threadData.threadName << "is dead. His sacrifice will not be made in vain!\n";

			pthread_mutex_unlock(&thread_mutex);
			pthread_exit(NULL);
		}

		if (counter2 > 3)
		{
			// Return the last point
			Point tempP = taken.back();

			while (!tempP.isConnected(threadData.currentLocation))
			{
				taken.pop_back();
				tempP = taken.back();
			}

			taken.push_back(threadData.currentLocation);
		}

		if (counter2 < 4)
		{
			taken.push_back(threadData.currentLocation);
		}

		if (threadData.currentLocation == endLocation)
		{
			if (!discoveredASolutionPath)
			{
				cout << "Thread '" << threadData.threadName << "' found a solution! Well done!\n";

				// Block Out
				pthread_mutex_lock(&thread_mutex);

				addSolution(threadData.currentLocation, taken);

				// Unlock
				pthread_mutex_unlock(&thread_mutex);

				areThreadsActive[threadData.threadIDArrayIndex] = false;
				activeThreads--;

				pthread_exit(NULL);
			}
		}
	} while (!discoveredASolutionPath);

	return EXIT_SUCCESS;
}

bool hitBoundary(Point tempCurrLoc)
{
	if ((tempCurrLoc.x < 0) || (tempCurrLoc.x >= mazeObj->getLength()))
	{
		return true;
	}

	else if ((tempCurrLoc.y < 0) || (tempCurrLoc.y >= mazeObj->getBreadth()))
	{
		return true;
	}

	return false;
}

void addBarrier(Point tempCurrLoc, VectorOfPointStructType tempTaken)
{
	if (find(barries.begin(), barries.end(), tempCurrLoc) == barries.end())
	{
		barries.push_back(tempCurrLoc);
		globalPathFinderResource.noOfBarriersDiscovered++;
	}

	submittedPaths++;

	tempTaken.push_back(tempCurrLoc);
	submitMazeSolnObj->submitPathToBarrier(pthread_self(), taken);

	mazeSolution->updateMaze(tempCurrLoc, BARRIER_INT);
}

void addDanger(Point tempCurrLoc, VectorOfPointStructType tempTaken)
{
	globalPathFinderResource.noOfDangerAreaDiscovered++;
	globalPathFinderResource.discoveredDangerAreas.push_back(tempCurrLoc);

	submittedPaths++;

	taken.push_back(tempCurrLoc);
	submitMazeSolnObj->submitPathToDangerArea(pthread_self(), taken);

	mazeSolution->updateMaze(tempCurrLoc, DANGER_INT);
}

void addSolutionPoint tempCurrLoc, VectorOfPointStructType tempTaken)
{
	globalPathFinderResource.solutionPath = taken;
	numSoln++;
	submittedPaths++;

	allPaths.push_back(taken);

	submitMazeSolnObj->submitSolutionPath(pthread_self(), taken);

	mazeSolutions->AddNewPath(taken);

	if (numSoln == maxNoOfSolutions)
	{
		discoveredASolutionPath = true;
	}
}

void * display(void * empty)
{
	// Block Out
	pthread_mutex_lock(&thread_mutex);

	// Suspend execution for 4 seconds
	usleep(4000000);
	time(&endTime);

	cout << "=======================================================\n";
	cout << "Elasped Time: " << difftime(endTime, startTime) << "\n";
	cout << "Latest Update: \n";
	cout << "=======================================================\n";
	cout << "\n";
	cout << "Dead End Paths Found  : " << globalPathFinderResource.noOfDeadEndPathsFound << "\n";
	cout << "Barriers Discovered   : " << globalPathFinderResource.noOfBarriersDiscovered << "\n";
	cout << "Danger Area Discovered: " << globalPathFinderResource.noOfDangerAreaDiscovered << "\n";
	cout << "\n";

	areThreadsActive[2] = false;

	// Unlock
	pthread_mutex_unlock(&thread_mutex);

	return EXIT_SUCCESS;
}

void explore()
{
	// Suspend execution for 4 seconds
	usleep(4000000);

	// Block Out
	pthread_mutex_lock(&thread_mutex);

	cout << "Finished Finding a SAFE PATH!\n";
	cout << "Printing Submitted maze solution:\n\n";

	// Print and save accordingly
	submitMazeSolnObj->printSubmittedSolution(studentName, studentID);
	submitMazeSolnObj->saveSubmittedSolution(studentName, studentID);

	statistics();

	// Unlock
	pthread_mutex_unlock(&thread_mutex);
}

void statistics()
{
	// Create an output buffer to store
	streambuf * buffer;
	ostream output(buffer);

	// Capture all display
	output << "Information:\n"
	output << "Threads Active     : " << globalPathFinderResource.usedThreadNameIndex << "\n";
	output << "Paths Submitted    : " << submittedPaths << "\n";
	output << "Solutions Submitted: " << numSoln << "\n";
	output << "\n";

	output << "Discoveries:\n";
	output << "Dead Ends Found   : " << globalPathFinderResource.noOfDeadEndPathsFound << "\n";
	output << "Barriers Found    : " << globalPathFinderResource.noOfBarriersDiscovered << "\n";
	output << "Danger Areas Found: " << globalPathFinderResource.noOfDangerAreaDiscovered << "\n";
	output << "\n";

	// Get the shortest path
	VectorOfPointStructType final = getShortestPath();

	output << "Shortest Path Found: ";

	final.pop_back();
	mazeObj->ShowPathGraphically(final, output);
}

VectorOfPointStructType getShortestPath()
{
	int min = 0;
	for(int i=1; i < allPaths.size(); i++)
	{
		if(allPaths[i].size() < allPaths[min].size())
			min = i;
	}

	return allPaths[min];
}

int end()
{
	// If there are any threads still active
	// return a failure
	for (int i = 0; i < MAX_NO_OF_THREADS; i++)
	{
		if (pthread_join(globalPathFinderResource.activeThreadArray[i], NULL) != 0)
		{
			return EXIT_FAILURE;
		}
	}

	DeallocateProgramsVariableMemory();

	return EXIT_SUCCESS;
}


