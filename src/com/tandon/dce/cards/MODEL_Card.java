/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.cards;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.request.processor.instructions.INSTRUCTION_Operation;
import com.tandon.dce.IDCEItem;

public class MODEL_Card implements IDCEItem {
	public static final String XCF_TAG = "Card";

	String name;
	INSTRUCTION_Operation action;
	ArrayList<String> keywords;
	int cost = -1;
	Integer id;

	public MODEL_Card(Integer id, String name) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setAction(INSTRUCTION_Operation action) {
		this.action = action;
	}

	public void execute(XCFRequest req) throws XCFException {
		action.process(req);
	}

	public ArrayList<String> getKeywords()  {
		if (keywords != null) return keywords;
		keywords = new ArrayList<String>();

		String keywordlist = (String)action.getProperty("keywords");
		if (keywordlist == null) return keywords;

		StringTokenizer tokenizer = new StringTokenizer(keywordlist, ",");
		while (tokenizer.hasMoreTokens()) {
			keywords.add(tokenizer.nextToken());
		}
		return keywords;
	}

	public Integer getCost() {
		if (cost > -1) return cost;

		String sCost = (String)action.getProperty("cost");
		if (sCost == null) {
			cost = 0;
		} else try {
			cost = Integer.parseInt(sCost);
		} catch (NumberFormatException e) {
			cost = 0;
		}

		return cost;
	}

	public String getProperty(String propertyName) {
		return (String)action.getFirstProperty(propertyName);
	}

	static final String WILDCARD = "*";
	static final String ALL = "@";
	static final String ANY = "|";
	static final String DELIM = "#";

	public boolean matches(String keywordList) {
		if (keywordList == null || keywordList.trim().length() == 0) return true;
		if (keywordList.equals(WILDCARD)) return true;


		StringTokenizer tokenizer = new StringTokenizer(keywordList, DELIM);
		while (tokenizer.hasMoreTokens()) {
			String word = tokenizer.nextToken();
			if (!matchList(word)) return false;
		}

		return true;
	}

	private boolean matchList(String keywordList) {
		if (keywordList.trim().startsWith(ALL)) {
			keywordList = keywordList.substring(1);
			StringTokenizer tokenizer = new StringTokenizer(keywordList, ",");
			while (tokenizer.hasMoreTokens()) {
				String word=tokenizer.nextToken();
				if (!hasKeyWord(word)) return false;
			}
			return true;
		} else if (keywordList.trim().startsWith(ANY)) {
			keywordList = keywordList.substring(1);
			StringTokenizer tokenizer = new StringTokenizer(keywordList, ",");
			while (tokenizer.hasMoreTokens()) {
				String word=tokenizer.nextToken();
				if (hasKeyWord(word)) return true;
			}
			return false;
		} else {
			return hasKeyWord(keywordList);
		}
	}

	private boolean hasKeyWord(String word) {
		for (String keyword: keywords) {
			if (keyword.trim().toUpperCase().equals(word.trim().toUpperCase())) return true;
		}
		return false;
	}

	public Integer getID() {
		return id;
	}
}
