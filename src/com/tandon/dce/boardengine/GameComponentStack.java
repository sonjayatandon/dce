package com.tandon.dce.boardengine;

import java.util.ArrayList;

public class GameComponentStack extends GameComponent {
	protected ArrayList<GameComponent> stack = new ArrayList<GameComponent>();
	
	public GameComponentStack(String name, GameSession session, GameComponent parent) {
		super(name, session, parent);
		this.session = session;
		this.name = name;
		this.parent = parent;
	}
	
	public int size() {
		return stack.size();
	}

	public void shuffle() {
		
	}

	public GameComponent pop() {
		if (stack.size() > 0) return stack.remove(0);
		
		return null;
	}
	
	public GameComponent getChild(String strIndex) {
		int i = Integer.parseInt(strIndex);
		if (i < stack.size()) return stack.get(i);
		return null;
	}
	
	public void add(GameComponent child) {
		stack.add(child);
	}
}
