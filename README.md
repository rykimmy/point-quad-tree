# Point Quad Tree
Provides an iterative implementation of the recursive ALGOL structure described in Finkel and Bentley's seminal work.

## Overview
This program builds a point quadtree. The main idea is that the root splits the board into four quadrants, each of which poses a possible child. These children split their sections of the board into another four quadrants and so forth. The program utilizes a BST-like structure to represent these points and recursion to check, add, and remove points.
