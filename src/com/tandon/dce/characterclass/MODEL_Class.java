/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.characterclass;

import java.util.ArrayList;
import java.util.HashMap;

import com.eternal.xcf.core.XCFFacade;
import com.tandon.dce.IDCEItem;

public class MODEL_Class implements IDCEItem {
	XCFFacade facade;
	final Integer id;
	final String name;
	String description;

	ArrayList<String> attributeIds = new ArrayList<String>();
	static HashMap<String, String> attributeNames = new HashMap<String, String>();
	HashMap<String, Integer> attributeValues = new HashMap<String, Integer>();

	ArrayList<MODEL_Template> templates = new ArrayList<MODEL_Template>();

	public MODEL_Class(XCFFacade facade, Integer id, String name) {
		this.facade = facade;
		this.id = id;
		this.name = name;
	}

	public final Integer getID() {
		return id;
	}

	public final String getName() {
		return name;
	}

	public void addAttribute(String attributeId, String attributeName, Integer value) {
		attributeIds.add(attributeId);
		attributeNames.put(attributeId, attributeName);
		attributeValues.put(attributeId, value);
	}

	public void addTemplate(MODEL_Template template) {
		templates.add(template);
	}

	public ArrayList<String> getAttributeIDS() {
		return attributeIds;
	}

	public String getAttributeName(String attributeId) {
		return attributeNames.get(attributeId);
	}

	public Integer getAttributeValue(String attributeId) {
		return attributeValues.get(attributeId);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public MODEL_Template getTemplate(int templateId) {
		return templates.get(templateId-1);
	}
}
