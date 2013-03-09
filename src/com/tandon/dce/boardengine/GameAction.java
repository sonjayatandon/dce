package com.tandon.dce.boardengine;

import java.util.HashMap;


public abstract class GameAction {
	protected HashMap<String, Object> properties = new HashMap<String, Object>();
	protected GameComponent container;
	
	public void setContainer(GameComponent container) {
		this.container = container;
	}
	
	public void setProperty(String propertyName, Object propertyValue) {
		properties.put(propertyName, propertyValue);
	}
	
	public Object getProperty(String propertyName) {
		return properties.get(propertyName);
	}
	
	public abstract void execute(GameSession session, GameComponent sourceComponent);
}
