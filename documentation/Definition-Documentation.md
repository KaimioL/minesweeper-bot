# Definition Document for Minesweeper bot

Minesweeper bot is algorithimc artifical intelligence which tries to optimize winrate in minesweeper.

Bot uses TiraLabra Minesweeper template 

## Info

Curriculum: MAT

Language: English

## Algorithms

Bot will use two methods combined for solving minesweeper. The first method is using
a set of logical rules and the second method is viewing the game as a constraint satisfaction problem (CSP).

Logical rules are:

1. If a square is explored and the number of unexplored squares around it is equal to the number in the square,
then flag the neighbors.

2. If the number of flags near an explored square is equal to the number in the square, explore the neighbors.

The CSP algorithm views all possible mine arrangements in the inspected area and gives every square a probability based on how often an arrangement
has a mine in the square. Based on this the algorithm can then make guesses if needed.

## Sources

https://dash.harvard.edu/bitstream/handle/1/14398552/BECERRA-SENIORTHESIS-2015.pdf?sequence=1