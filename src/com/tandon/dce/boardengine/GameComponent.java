package com.tandon.dce.boardengine;

import java.util.ArrayList;
import java.util.HashMap;

public class GameComponent {
	protected GameSession session;
	protected GameComponent parent;
	protected GameComponent prototype;
	protected String name;
	protected HashMap<String, Object> properties = new HashMap<String, Object>();
	protected HashMap<String, ArrayList<GameAction>> actions = new HashMap<String, ArrayList<GameAction>>();
	protected HashMap<String, GameComponent> children = new HashMap<String, GameComponent>();

	public GameComponent(String name, GameSession engine, GameComponent parent) {
		this.session = engine;
		this.name = name;
		this.parent = parent;
	}
	
	public GameComponent(String name, GameSession engine, GameComponent parent, GameComponent prototype) {
		this.session = engine;
		this.name = name;
		this.parent = parent;
		this.prototype = prototype;
		
		// TODO, if prototype is a composite object
		// do a deep clone -- clone should be a mirror of prototype
		// where each subcomponent points to the sub-prototype component it mirrors
	}
	
	public void execute(String trigger) {
		ArrayList<GameAction> actionList = actions.get(trigger);
		if (actionList == null) return;
		
		for (GameAction action: actionList) {
			action.execute(session, this);
		}
	}
	
	public Object getProperty(String propertyName) {
		Object value = properties.get(propertyName);
		if (value == null && prototype != null) {
			value = prototype.getProperty(propertyName);
		}
		return value;
	}
	
	public void setProperty(String propertyName, Object propertyValue) {
		properties.put(propertyName, propertyValue);
	}

	public void addAction(String trigger, GameAction action) {
		action.setContainer(this);
		ArrayList<GameAction> actionList = actions.get(trigger);
		
		if (actionList == null) {
			actionList = new ArrayList<GameAction>();
			actions.put(trigger, actionList);
		}
		
		actionList.add(action);
	}

	public GameComponent getChild(String componentName) {
		return children.get(componentName);
	}

	public void add(GameComponent child) {
		children.put(child.name, child);
	}

	
}
