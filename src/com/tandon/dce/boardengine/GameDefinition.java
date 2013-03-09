package com.tandon.dce.boardengine;

public interface GameDefinition {
	
	void createGamePieces();
	
	void setupGameBoard(GameSession session);
	
	void addPlayer(GameSession session, GameComponent player);
	
	void initializeTurnStructure(GameSession session);
	
	GameComponent getGamePiece(String id);

}
