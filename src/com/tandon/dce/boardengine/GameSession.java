package com.tandon.dce.boardengine;

import java.util.ArrayList;



public class GameSession {
	GameComponent gameBoard = new GameComponent("", this, null);
	ArrayList<GameAction> executionQueue = new ArrayList<GameAction>();
	ArrayList<GameEvent> eventQueue = new ArrayList<GameEvent>();
	
	/**
	 * Creates a new game component using the prototype passed in.
	 * If the stack identified doesn't exist, creates a new stack
	 * If the interim parent components don't exist, creates them all as generic components
	 * 
	 * For example, if the following call is made
	 *   session.addToComponentStack("mission.nighttime.attack", zaneRunner, 2)
	 * The following component structure will be created
	 *   gameboard
	 *    |-mission
	 *     |-nighttime
	 *      |-attack
	 *  Where mission and nighttime are GameComponent objects and attack is a GameComponentStack object
	 *  Then, a new GameComponent will be created using zaneRunner as the prototype component.  This 
	 *  new component is added to the attack GameComponentStack  
	 * 
	 * @param locationOnBoard
	 * @param prototypeComponent
	 * @param quantity
	 */
	public void addNewToComponentStack(String locationOnBoard, GameComponent prototypeComponent,
			int quantity) {
		
		String[] resolvedAddress;
		
		GameComponent parent = gameBoard;
		
		do {
			resolvedAddress = AddressResolver.getNextTag(locationOnBoard);
			String componentName = resolvedAddress[0];
			if (resolvedAddress[1]==null) {
				// We have created our found all the parent components
				// create the stack and add to parent component
				
				GameComponentStack stack = (GameComponentStack)parent.getChild(componentName);
				if (stack == null) {
					stack = new GameComponentStack(componentName, this, parent);
					parent.add(stack);
				}
				for (int i = 0; i < quantity; i++) {
					GameComponent gameComponent = new GameComponent(prototypeComponent.name, this, parent, prototypeComponent);
					stack.add(gameComponent);
				}
			} else {
				// we are still breaking up the address, make sure the parent exists
				GameComponent gameComponent = parent.getChild(componentName);
				if (gameComponent == null) {
					// we need to create the interim parent
					gameComponent = new GameComponent(componentName, this, parent);
					parent.add(gameComponent);
				}
				parent = gameComponent;
				locationOnBoard = resolvedAddress[1];
			}		
		} while (resolvedAddress[1]!=null);
	}

	public void addToComponentStack(String locationOnBoard, GameComponent componentToAdd) {	
		GameComponentStack stack = (GameComponentStack)getComponent(locationOnBoard, false);
		if (stack != null) {
			stack.add(componentToAdd);
		}	
	}

	public Object getProperty(String propertyLocation) {
		String[] resolvedAddress = AddressResolver.getPropertyName(propertyLocation);
		String propertyName = resolvedAddress[0];
		GameComponent gameComponent = gameBoard;
		if (resolvedAddress[1] != null) {
			gameComponent = getComponent(resolvedAddress[1], false);
		} 
		
		if (gameComponent == null) return null;
		
		return gameComponent.getProperty(propertyName);
	}

	public void setProperty(String propertyLocation, Object propertyValue) {
		String[] resolvedAddress = AddressResolver.getPropertyName(propertyLocation);
		String propertyName = resolvedAddress[0];
		GameComponent gameComponent = gameBoard;
		if (resolvedAddress[1] != null) {
			gameComponent = getComponent(resolvedAddress[1], true);
		} 
		
		gameComponent.setProperty(propertyName, propertyValue);
	}

	public GameComponent getComponent(String locationOnBoard) {
		return getComponent(locationOnBoard, false);
	}
	
	public GameComponent getComponent(String locationOnBoard, boolean createIfMissing) {
		String[] resolvedAddress;
		
		GameComponent parent = gameBoard;
		GameComponent gameComponent = null;
		
		do {
			resolvedAddress = AddressResolver.getNextTag(locationOnBoard);
			String componentName = resolvedAddress[0];
			if (resolvedAddress[1]==null) {
				// We have created our found all the parent components
				// return componentName, create if missing, if we are asked to do that
				gameComponent = parent.getChild(componentName);
				if (gameComponent == null && createIfMissing) {
					gameComponent = new GameComponent(componentName, this, parent);
					parent.add(gameComponent);
				}
			} else {
				// we are still breaking up the address, make sure the parent exists
				gameComponent = parent.getChild(componentName);
				if (gameComponent == null) {
					// we need to create the interim parent
					gameComponent = new GameComponent(componentName, this, parent);
					parent.add(gameComponent);
				}
				parent = gameComponent;
				locationOnBoard = resolvedAddress[1];
			}		
		} while (resolvedAddress[1]!=null);
		
		return gameComponent;
	}

	/*
	public void setComponent(String locationOnBoard, GameComponent gameComponent) {
		String[] resolvedAddress = AddressResolver.getPropertyName(locationOnBoard);	
		if (resolvedAddress[1]==null) {
			// no parent, goes right on the game board
			gameBoard.
			
		}
	}
	*/
	
	public GameAction popAction() {
		if (executionQueue.isEmpty()) return null;
		return executionQueue.remove(0);
	}

	public void pushAction(GameAction action) {
		executionQueue.add(0, action);
	}
	
	public void queueAction(GameAction action) {
		executionQueue.add(action);
	}
	
	public void queueActionList(ArrayList<GameAction> actionList) {
		executionQueue.addAll(actionList);
	}
	
	public void pushActionList(ArrayList<GameAction> actionList) {
		executionQueue.addAll(0, actionList);
	}
	
	public ArrayList<GameEvent> doNextAction()  {
		eventQueue = new ArrayList<GameEvent>();
		
		while (!executionQueue.isEmpty() && eventQueue.isEmpty()) {
			GameAction action = executionQueue.remove(0);
			action.execute(this, null);
		}
		
		return eventQueue;
	}
	
	public boolean isGameOver() {
		return executionQueue.isEmpty();
	}
	
}
