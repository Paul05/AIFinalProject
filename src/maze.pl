%file: maze.pl
%author: Ian Beer
%version 0.3 4/15/2013

%This file is a beta version game engine for the final project for CST496 AI Spring 2013.

%NOTE: All older implementation of this program have been kept for posterity including searchtree testing predicates 
%	and these game operating techniques: 
%		DFS which does not really work (takes a long time) for current game configuration see version 0.1 for working maze. 

%This program involves to indepentant AI agents: Explorer and Monster
%The explorer is trying to find the the treasure(gold), then find the exit(goal) of the maze.
%The monster can "smell" the explorer and is hungry.

%Either the explorer wins by getting to the treasure room and then escape to the goal room
%Or the monster wins by getting to the same room as the explorer.

%The output of this engine is two lists: explorer moves and monster moves with the intent of these lists
%being processed by jpl and shown on a java GUI.

%Both agents implement the same BFS algrothim to traverse their decision tree.

%Operation:
%the predicate playGameBFS/2 is called to start the game.
%	this sets up the initial state of the game places the pieces:Explorer, Monster, Treasure, Goal
%	NOTE: the goal can move but will probably be static in the final game.
%Then gamePlayBFS/6 is called. This predicate serves as the Knowledge Base of the game and produces 2 lists:
%	One for explorer and the monster, the indicies of these list correspond to each game move.
%	game play tests for explorer win, monster win and explorer gets gold states. else continues 
%	The man and monster then calculate thier next move by using predicates: explorerMoveBFS/3 and monsterMoveBFS/4
%	which uses a BFS algrothim from searchTreeBFS/3 that takes the first room from the fringe, then discards the rest of the search.
%	then recursively loop till a base case is true. 

%Assumptions:
%The entire state space is known to the explorer, He has a map.
%The explorer does not know there is a monster in the cave.
%The monster always knows where the explorer is, he can here and smell the Explorer.

%Additional predicates needed for program:
%standard predicate to check if a element is a member of a list
member(X, [X|_]).
member(X, [_|Y]) :- member(X, Y).
 
%standard predicate to reverse a list
reverse([ ], [ ]).
reverse([H|T], R) :- reverse(T, R1), append(R1, [H], R).

%remove removes a partial list from a larger list
%remove(ListToCut, ListToCutFrom, Result).
remove([],Result,Result).
remove([H|T],List,Result):-delete(H,List,NewList),remove(T,NewList,Result).

%delete(X,L,R) is true if R is the result of deleting the 1st X element from L
%returns R even if X was not in list.
delete(_,[],[]).
delete(X,[X|T],T).
delete(X,[H|T],[H|R]):-X\=H, delete(X,T,R).

%State Space of game board:
%edge(A,B) are valid room A to room B moves and the rooms have a format of (X,Y) on a loose grid format
edge((1,1),(1,2)).
edge((1,1),(2,1)).
edge((1,2),(1,3)).
edge((1,2),(2,2)).
edge((2,1),(3,1)).
edge((2,1),(2,2)).
edge((2,2),(3,2)).
edge((2,2),(2,3)).
edge((3,1),(3,2)).
edge((3,1),(4,1)).
edge((4,1),(4,2)).
edge((4,1),(5,1)).
edge((3,2),(3,3)).
edge((3,2),(4,2)).
edge((4,2),(4,3)).
edge((1,3),(1,4)).
edge((1,3),(2,3)).
edge((2,3),(2,4)).
edge((2,3),(3,3)).
edge((3,3),(3,4)).
edge((3,3),(4,3)).
edge((4,3),(5,3)).
edge((1,4),(1,5)).
edge((1,4),(2,4)).
edge((2,4),(3,4)).
edge((3,4),(4,4)).
edge((4,4),(4,3)).
edge((1,5),(1,6)).

%convience predicate to make true the reverse of all the edge/2 predicates
conn(X,Y):-edge(X,Y).
conn(X,Y):-edge(Y,X).

%for testing
start( (1,6) ).
manStart( (1,6) ).
monsterStart( (5,3) ).
goal( (5,1) ).
treasure( (1,1) ).

%==================================DFS search=======================================================================
%does not really work only for VERY simple solutions
% path/x: 
%1 Start: current state of search
%2 Goal: goal state of search
%3 PathSoFar: list to save the rooms visited so far
%4 State: The reverse of PathSoFar will need to get the next state of the gamePlay
%5 
%6 
searchTreeDFS(Start, Goal, PathSoFar, State):-conn(Start, Goal), \+ member(Goal,PathSoFar), reverse([Goal|PathSoFar], State).
searchTreeDFS(Start, Goal, PathSoFar, State):-conn(Start, NextRoom), \+ member(NextRoom,PathSoFar), 
					searchTreeDFS(NextRoom,Goal,[NextRoom|PathSoFar], State).

solveDFS(States):-start(Start),
	     goal(Goal),
	     searchTreeDFS(Start, Goal, [Start], States).

%simple algorthim for gameplay:
%playgame:-playermoves call path, get next move from Path variable then move player there
%	 if: player at goal(exit)
%		 playerwins win game
%	 else: 	monstermoves call path with goal as player 
%	 if: monster at same room as player(exit)
%		player looses game
%	 else: playgame
playGameDFS(ExplorerMoves, MonsterMoves):-manStart(Start),monsterStart(MonStart),goal(Goal),
	gamePlayDFS((Start, Goal,[Start]),(MonStart,Start,[MonStart]),ExplorerMoves, MonsterMoves).
	
%gamePlay(ManState, MonsterState, List of Gamemoves)
%base cases: man at goal, monster at man, both end the game
%player state= (InRoom, GoalRoom, PathSoFar)
gamePlayDFS((Goal,Goal,_),(_,_,_),[],[]).%passed test
gamePlayDFS((Same,_,_),(Same,_,_),[],[]).
gamePlayDFS(ExplorerState, MonsterState,[ExMove |ExplorerMoves],[MonMove|MonsterMoves]):-
					ExplorerState = (RoomManIn, _Goal, _PathSoFar),%monster knows the room exploer is in
					explorerMoveDFS(ExplorerState, NextExplorerState, ExMove),
					monsterMoveDFS(MonsterState, NextMonsterState, RoomManIn, MonMove),
					gamePlayDFS(NextExplorerState, NextMonsterState, ExplorerMoves, MonsterMoves).

explorerMoveDFS(ManState,NextManState, NextRoom):-ManState = (RoomIn, Goal, PathSoFar),
				searchTreeDFS(RoomIn, Goal, PathSoFar, SearchResult), 
				remove(PathSoFar, SearchResult, [NextRoom|_Result]),%have to remove rooms visisted so far for DFS
				NextManState = (NextRoom, Goal, [NextRoom|PathSoFar]).

monsterMoveDFS(MonsterState, NextMonsterState, RoomManIn, NextRoom):-MonsterState = (RoomIn, _Goal, PathSoFar),
				searchTreeDFS(RoomIn, RoomManIn, PathSoFar, SearchResult),
				remove(PathSoFar, SearchResult, [NextRoom|_Result]),%have to remove rooms visisted so far for DFS
				NextMonsterState = (NextRoom, RoomManIn, [NextRoom|PathSoFar]).
	
%===================End DFS==========================================================================================

%====================BFS==============================================================================================
%1 Tree Node: (CurrentRoom, PathToGetToRoom)
%2 Goal: goal state of search
%3 Soln: The reverse of PathSoFar will need to get the next state of the gamePlay			 
searchTreeBFS( [(Goal, PathSoFar)|_Fringe], Goal,  Soln):- reverse(PathSoFar, Soln).%base case found goal
searchTreeBFS( [Node|Fringe] , Goal,  Soln):-expand(Node, Children),
					append(Fringe, Children , NewFringe),
					searchTreeBFS(NewFringe, Goal, Soln).
											
%expand is true if expanding Node results in a list of valid Children Nodes 
%Children will be a list of tuples...[ (Node1) , ([Node2),...]
expand(Node, Children):- findall(Child,nextNode(Node, Child),Children).

nextNode(Node, NextNode):- Node = (InRoom, Visited),
			 conn(InRoom, NxtRoom),
			 \+ member(NxtRoom, Visited),
			 NextNode = (NxtRoom, [NxtRoom|Visited]).

%predicate to test searchTreeBFS/3			 			
solveBFS(Soln):-start(Start),
	     	goal(Goal),
	     	Node = (Start, [Start]),
	    	searchTreeBFS([Node], Goal, Soln).

%simple algorthim for gameplay:
%playgame:-explorermoves call path, get next move from Path variable then move player there
%	 if: explorer at goal(exit)
%		 explorer wins win game
%	 if: explorer at treasure(gold)
%		explorer changes goal to exit
%	 else: 	monstermoves call path with goal as explorer 
%	 if: monster at same room as explorer(exit)
%		explorer looses game and gets eaten
%	 else: playgame
playGameBFS(ExplorerMoves, MonsterMoves):-manStart(Start),monsterStart(MonStart),goal(Exit),treasure(Gold),
	gamePlayBFS((Start, Gold, [Start]),(MonStart, [MonStart]), Exit, Gold, ExplorerMoves, MonsterMoves).

%gamePlay(ManState, MonsterState, ExitRoom, GoldRoom, ListOfManMoves, ListOfMonsterMoves)
%gamePlay also serves as the Knowledge Base of the Game contains:
%Exit room
%Treasure room
%The room the explorer is in for the monster
%Moves the explorer has made
%Move the monster has made

%base cases: man at exit, monster at man. both end the game

%Explorer state= (InRoom, GoalRoom, PathSoFar)
%Monster state= (InRoom, PathSoFar)

%basecases:
gamePlayBFS((Exit,Exit,_),(_,_), Exit, _Gold, [],[]).%Explorer Wins
gamePlayBFS((Same,_,_),(Same,_), _Exit, _Gold, [],[]).%Monster Wins
%explorer finds gold
gamePlayBFS((Gold,Gold,PathSoFar), MonsterState, Exit, Gold,  ExplorerMoves, MonsterMoves):-NextExplorerState = (Gold,Exit,PathSoFar),
				gamePlayBFS(NextExplorerState, MonsterState, Exit, Gold, ExplorerMoves, MonsterMoves).
%else game play
gamePlayBFS(ExplorerState, MonsterState, Exit, Gold, [ExMove |ExplorerMoves], [MonMove|MonsterMoves]):-
					explorerMoveBFS(ExplorerState, NextExplorerState, ExMove),
					NextExplorerState = (RoomManIn, _Goal, _PathSoFar),%monster knows the room exploer is in
					monsterMoveBFS(MonsterState, NextMonsterState, RoomManIn, MonMove),
					gamePlayBFS(NextExplorerState, NextMonsterState, Exit, Gold, ExplorerMoves, MonsterMoves).


explorerMoveBFS(ManState,NextManState, NextRoom):-ManState = (RoomIn, Goal, PathSoFar),
				searchTreeBFS([(RoomIn, PathSoFar)],  Goal, SearchResult), 
				remove(PathSoFar, SearchResult, [NextRoom|_Result]),%have to remove rooms visited so far 
				NextManState = (NextRoom, Goal, [NextRoom|PathSoFar]).

monsterMoveBFS(MonsterState, NextMonsterState, RoomManIn, NextRoom):-MonsterState = (RoomIn, PathSoFar),
				searchTreeBFS([(RoomIn, PathSoFar)], RoomManIn, SearchResult),
				remove(PathSoFar, SearchResult, [NextRoom|_Result]),%have to remove rooms visited so far 
				NextMonsterState = (NextRoom, [NextRoom|PathSoFar]).
				
%===========================end BFS===============================================================================