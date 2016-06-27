# ABCGrid
ABCGrid stands for Application of Bioinformatics Computing Grid. 

It was designed for biology laboratories to use large amount of heterogeneous computing resources and access many bioinformatics applications from one master node without being aware of where the data and computational resources are located. ABCGrid is very easy to install, maintain and extend at the premise of high performance. Currently, ABCGrid integrates NCBI_BLAST, Hmmpfam and CE, running on a number of computing platforms including UNIX/Linux, Windows and Mac OS X. 

ABCGrid is a server/client application consists of a Master application (ABMaster), a Worker application(ABCWorker) and a User application (ABCuser). ABCGrid is programmed using Java SDK 5.0. Java Runtime Environment (JRE) 5.0 or above(or compatible runtime) is required to run ABCGrid.

To use ABCGrid, the Master Application should be deployed to Master Node while the Worker Application should be installed on multiple Worker (Slave) Nodes.
