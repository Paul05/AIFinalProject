%file: maze.pl
%author: Ian Beer
%version 0.4 4/15/2013

%This file is a beta version game engine for the final project for CST496 AI Spring 2013.

%NOTE: All older implementation of this program have been kept for posterity including searchtree testing predicates 
%	and these game operating techniques: 
%		DFS which does not really work (takes a long time) for current game configuration see version 0.1 for working maze. 
%		BFS works, but monster is really dumb

%This program involves to indepentant AI agents: Explorer and Monster
%The explorer is trying to find the the treasure(gold), then find the exit(goal) of the maze.
%The monster can "smell" the explorer and is hungry.

%Either the explorer wins by getting to the treasure room and then escape to the goal room
%Or the monster wins by getting to the same room as the explorer.

%The output of this engine is two lists: explorer moves and monster moves with the intent of these lists
%being processed by jpl and shown on a java GUI.

%The agents implement seperate algrothims to traverse their decision tree.

%Operation:
%the predicate playGame/2 is called to start the game.
%	this sets up the initial state of the game places the pieces:Explorer, Monster, Treasure, Goal
%	NOTE: the exit can move but will probably be static in the final game.
%Then gamePlay/6 is called. This predicate serves as the Knowledge Base of the game and produces 2 lists:
%	One for explorer and the monster, the indicies of these list correspond to each game move.
%	game play tests for explorer win, monster win and explorer gets gold states. else continues 
%	The man and monster then calculate thier next move by using predicates: explorerMove/3 and monsterMove/4
%	which seperate algrothims for search, and returns the next room to move into.
%	then recursively loop till a base case is true. 

%Assumptions:
%The entire state space is known to the explorer, He has a map.
%The explorer does not initially know there is a monster in the cave.
%The monster always knows where the explorer is, he can hear and smell the Explorer.
%The explorer can see the monster 1 room away.
%The explorer can smell the monster when its two rooms away.

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
treasure( (2,1) ).
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

%============================A*+===================================================================
%monster search
%1 Tree Node: (CurrentRoom, PathToGetToCurrentRoom, FvalueofNode)
%2 Goal: goal state of search
%3 Soln: returns the next room for the monster to move into		 
searchTree( [(Goal, PathSoFar, _F)|_Fringe], Goal, NextMove):- reverse(PathSoFar, [NextMove|_Soln]).%base case found goal
searchTree( [Node|Fringe] , Goal,  Soln):-expandAS(Node, Children, Goal),
					insert(Fringe, Children , NewFringe),
					searchTree(NewFringe, Goal, Soln).

%Explorer Search
%1 Tree Node: (CurrentRoom, PathToGetToCurrentRoom, FvalueofNode)
%2 Goal: goal state of search
%3 Soln: returns the next room for the explorer to move into
%4 N: depth you want the forward checking to check, currently set at 1 only forwards checks the children of the root node
%5 RoomMonsterIn: needed for forward checking if the monster is 1 room away, note presence of this variable does not mean the explorer knows where the monster is
%6 MonStinkRooms: list of rooms that have monster stink.
expSearchTree( [(Goal, PathSoFar, _F)|_Fringe], Goal, NextMove, _N, _Mon, _MonStR):- reverse(PathSoFar, [NextMove|_Soln]).%base case found goal
expSearchTree( [Node|Fringe] , Goal,  Soln, N, RoomMonsterIn, MonStinkRooms):- N > 0,
					expandAS(Node, Children, Goal),
					checkForMonster(RoomMonsterIn, MonStinkRooms ,Children, NewChildren),
					insert(Fringe, NewChildren , NewFringe),
					N1 is N-1,
					expSearchTree(NewFringe, Goal, Soln, N1, RoomMonsterIn,  MonStinkRooms).
expSearchTree( [Node|Fringe] , Goal,  Soln, N, RoomMonsterIn, MonStR):- N < 1,
					expandAS(Node, Children, Goal),
					insert(Fringe, Children , NewFringe),
					expSearchTree(NewFringe, Goal, Soln, N, RoomMonsterIn, MonStR).
					
%expand/3 is true if expanding Node results in a list of valid Children Nodes 
%1 Node: node to expand
%2 Returns: Children: a list of tuples...[ (Node1) , ([Node2),...]
%3 Goal: room
expandAS(Node, Children, Goal):- findall(Child,nextASNode(Node, Child, Goal),Children).	

%nextASNode/3 finds and builds the chilren nodes of a expanded tree node
%1 Node: node to expand
%2 Returns: NextNode: a valid child node
%3 Goal: Goal room
nextASNode(Node, NextNode, Goal):- Node = (InRoom, Visited, _Fvalue),
			 conn(InRoom, NxtRoom),
			 \+ member(NxtRoom, Visited),
			 NextNode = (NxtRoom, [NxtRoom|Visited], Fval),
			 calcf(NextNode, Fval, Goal).
	
%insert/3 puts each child node of list Children in value order to remake Fringe
%1 Fringe: of search tree so far
%2 Children: list of children from an expanded node
%3 Returns: NewFringe
insert(Fringe, [], Fringe).			 
insert(Fringe, [Child|Children] , NewFringe):-insertNode(Child, Fringe, NextFringe),
						insert(NextFringe, Children, NewFringe).

%insertNode/3 sorts the Child node into the fringe	
%1 Child: tree node to sort into fringe
%2 Fringe: current frings
%3 Returns: NewFringe with child sorted into fringe									
%need to iterate Fringe based on value and insert child into Frige based on value
insertNode(Child, [], [Child]).
insertNode(Child, [Element|Fringe], [Child,Element|Fringe]):-checkFval(Child, Element), !.
insertNode(Child, [Element|Fringe], [Element|NewFringe]):- insertNode(Child, Fringe, NewFringe).
	
%checkForMonster/4 implements the forward checking for the explorer search
%1 RoomMonsterIn: Single room
%2 MonsterStinkRooms: list of rooms
%3 Children: list of expanded children nodes to test for monster
%4 NewChildren: the children list with updated fvals
%Takes the expanded children of a node and checks if the monster is in one of them, if so add 200 to the fval.
%Then checks if there is stink in the expanded children if so adds 100 to the fval.
%implements: The explorer can see the monster 1 room away.
%	     The explorer can smell the monster when its two rooms away.
checkForMonster(_RoomMonsterIn, _MonStinkRooms ,[], []).
checkForMonster(RoomMonsterIn, MonStinkRooms ,[Child|Children], [NewChild|NewChildren]):-Child = (Room, Path , Fval),
							Room \= RoomMonsterIn,
							member(Room, MonStinkRooms),
							NewFval is Fval + 100,
							NewChild = (Room, Path, NewFval),
							checkForMonster(RoomMonsterIn, MonStinkRooms, Children, NewChildren).
checkForMonster(RoomMonsterIn, MonStinkRooms ,[Child|Children], [Child|NewChildren]):-Child = (Room, _Path , _F),
							Room \= RoomMonsterIn,
							\+ member(Room, MonStinkRooms),
							checkForMonster(RoomMonsterIn, MonStinkRooms, Children, NewChildren).							
checkForMonster(RoomMonsterIn, MonStinkRooms ,[Child|Children], [NewChild|NewChildren]):-Child = (Room, Path , Fval),
							Room = RoomMonsterIn,
							NewFval is Fval + 200,
							NewChild = (Room, Path, NewFval),
							checkForMonster(RoomMonsterIn, MonStinkRooms, Children, NewChildren).
	
%checkFval returns true if the firsts node fval is less than the second nodes fval
checkFval(Node1, Node2):-Node1 = (_,_, Fval1), Node2 = (_,_, Fval2), Fval1 =< Fval2.

%not currently valid
%predicate to test searchTree/3		 			
%solve(Soln):-start(Start),
%	     	goal(Goal),
%	     	Node = (Start, [Start], Fval),
%	     	calcf(Node, Fval), 
%	    	searchTree([Node], Goal, Soln).

%f(n) = g(n) + h(n); g is the cost so far let g = 1n, n is number of moves taken so far
%		     h is the estimated cost left using one of the hueristics below: 
%						h0:  each node is worth g + 1, for testing
%						hval: implements the distance formula between to points
%calcf/3
%1 Node to calculate Fval
%2 Returns Fval for node
%3 Goal room for hval
calcf(Node, Fval, Goal):-Node = (_State, PathSoFar, _Fval),
		length(PathSoFar, Gval),
		hval(Node,Goal, Hval),
		Fval is Gval+Hval.
%for testing
h0(_Node,_Goal, 1).

%hval/3
%1 Node node to calculate the hval
%2 Goal room
%3 Returns Hval of Node
%calculates distance formula from current room to goal room
hval(Node, Goal, Hval):-Node = (Room, _PathSoFar, _Fval),
		Room = (X1,Y1),
		Goal = (X2,Y2),
		X = (X2-X1),Y = (Y2-Y1),  
		sqrt( ( (X*X) +   (Y*Y)), Hval ).
			
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

%playGame/5 main predicate to play game with A*/forward checking
%1 ManStart room X-coord
%2 ManStart room Y-coord
%3 Monster Start room X-coord
%4 Monster Start room Y-coord
%5 Room Gold is in X-coord
%6 Room Gold is in Y-coord
%7 Returns a list of explorer Moves
%8 Returns a list of monster Moves
playGame(ExpX, ExpY, MonX, MonY, GoldX, GoldY, ExplorerMoves, MonsterMoves):-ExpStart = (ExpX, ExpY),
		MonStart = (MonX, MonY), Gold = (GoldX, GoldY), goal(Exit),
		gamePlay((ExpStart, Gold),(MonStart), Exit, Gold, GameExplorerMoves, GameMonsterMoves),
		ExplorerMoves = [ExpStart|GameExplorerMoves], MonsterMoves = [MonStart|GameMonsterMoves].

%gamePlay/6 is the main predicate of the game serves as the game engine and also contains the Knowledge Base of the Game
%1 ManState
%2 MonsterState
%3 Exit room
%4 Treasure room
%5 Returns: ListOfManMoves
%6 Returns: ListOfMonsterMoves
%Additional Knowledge stored in this predicate:
%	The room the monster is in for explorer tree search forward checking
%	The room the explorer is in for the monster tree search
%	The rooms that have monster stink
%Explorer state= (InRoom, GoalRoom)
%Monster state= (InRoom) %use to be a larger tuple :)
%Basecases: man at exit, monster at man. both end the game
gamePlay((Exit,Exit),(_), Exit, _Gold, [],[]).%Explorer Wins
gamePlay((Same,_),(Same), _Exit, _Gold, [],[]).%Monster Wins
%explorer finds gold now head for the exit!!!
gamePlay((Gold,Gold), MonsterState, Exit, Gold,  ExplorerMoves, MonsterMoves):-NextExplorerState = (Gold,Exit),
				gamePlay(NextExplorerState, MonsterState, Exit, Gold, ExplorerMoves, MonsterMoves).
%else game play
gamePlay(ExplorerState, MonsterState, Exit, Gold, [ExMove |ExplorerMoves], [MonMove|MonsterMoves]):-MonsterState= (RoomMonIn),
					calcMonsterStink(RoomMonIn, StinkyRooms),
					explorerMove(ExplorerState, NextExplorerState, ExMove, RoomMonIn, StinkyRooms),
					NextExplorerState = (RoomManIn, _Goal),%monster knows the room exploer is in
					monsterMove(MonsterState, NextMonsterState, RoomManIn, MonMove),
					gamePlay(NextExplorerState, NextMonsterState, Exit, Gold, ExplorerMoves, MonsterMoves).

%explorerMove/5 calculates the next move for the explorer using knowledge from the KB
%1 Current Explorer State
%2 Returns: Next Man State
%3 Returns: Next Explorer Room !!may not be used/needed!!!!
%4 Room Monster is in
%5 List of monster stink rooms
explorerMove(ManState,NextManState, NextRoom, RoomMonIn, MonStinkRooms):-ManState = (RoomIn, Goal),
				expSearchTree([(RoomIn, [], 0)],  Goal, NextRoom, 1 , RoomMonIn, MonStinkRooms), 
				NextManState = (NextRoom, Goal).

%monsterMove/4 calculates the next move for the monster using knowledge from the KB
%1 Current Monster State
%2 Returns: Next Monster State
%3 Room Explorer is in
%4 Returns: Next Monster Room !!may not be needed!!!
monsterMove((RoomIn), NextMonsterState, RoomIn, NextRoom):-NextRoom = RoomIn, NextMonsterState = (RoomIn).%Explorer moved into room monster is in, Oops!
monsterMove(MonsterState, NextMonsterState, RoomManIn, NextRoom):-MonsterState = (RoomIn),
				searchTree([(RoomIn, [], 0)], RoomManIn, NextRoom),
				NextMonsterState = (NextRoom).

%calcMonsterStink/2 
%1 Room monster is in
%2 Returns: a list of rooms with monster stink in them
calcMonsterStink(RoomMonIn, StinkyRooms):- findall(StinkyRoom,conn(RoomMonIn, StinkyRoom),StinkyRooms).	
%===========================end A*===================================================================

%====================BUGS===========================
%explorer can go thru monster room and not get eaten, should fix this with A* forward checking---fixed