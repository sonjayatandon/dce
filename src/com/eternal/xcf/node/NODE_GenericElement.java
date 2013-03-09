/*
 * Copyright © 2004 Eternal Adventures, Inc.
 *
 *  Licensed under the Eclipse License, Version 1.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 * @author standon
 */
package com.eternal.xcf.node;


import java.util.ArrayList;
import java.util.HashMap;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFStrings;

/**
 * Insert the type's description here.
 * Creation date: (7/24/2001 2:21:43 PM)
 * @author: Administrator
 */
public class NODE_GenericElement implements INodeElement {
	ArrayList<INodeElement> nodeElements = new ArrayList<INodeElement>();
	private HashMap<String, INodeElement> lookup = new HashMap<String, INodeElement>();
	private String name;
	private INodeElement nameElement;
	Object value;
	String type;
	INodeManager mgr;
	String address;
/**
 * ModelContainer constructor comment.
 * @param name java.lang.String
 */
public NODE_GenericElement() {
	super();
	value = this;
}

public void setName(String name) throws XCFException {
	this.name = name;
	this.address = name;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2001 5:01:51 PM)
 * @param element com.dataskill.standon.ccw.model.ModelElement
 */
public INodeElement addElement(INodeElement element) throws XCFException {
	lookup.put(element.getName(), element);
	nodeElements.add(element);
	element.setAddress(address + "." + element.getName());
	return element;
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/2001 4:47:22 PM)
 * @return com.dataskill.xcf.data.INodeElement
 * @param mgr com.dataskill.xcf.data.INodeElementManager
 * @param name java.lang.String
 */
public INodeElement copy(INodeManager mgr, java.lang.String name) throws XCFException {
	if (name == null) {
		name = this.name;
	}
	INodeElement copy = mgr.newElement(type, name);
	if (value != null) {
		///////////////////////////////////////
		// REPLACE
		//  Somehow we have to copy the value
		//////////////////////////////////////
		copy.setValue(value);
	}
	INodeIterator iter = getElements();
	while (iter.hasMoreElements()) {
		INodeElement element = iter.nextElement();
		copy.addElement(element.copy(mgr, element.getName()));
	}
	return copy;
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2001 5:08:41 PM)
 * @return java.lang.String
 */
public java.lang.String getAddress() {
	return address;
}
/**
 * Insert the method's description here.
 * Creation date: (10/4/2001 4:10:43 PM)
 * @return com.dataskill.xcf.data.INodeElement
 * @param dimensionName java.lang.String
 */
public INodeElement getDimension(String dimensionName) throws XCFException {
	INodeElement dimension = (INodeElement)lookup.get(dimensionName);
	if (dimension == null) {
		if (dimensionName.equals(XCFStrings.NAME)) {
			if (nameElement == null) {
				nameElement = new NODE_GenericElement();
				nameElement.setName(dimensionName);
			}
			return nameElement;
		}
	}
	return dimension;
}
/**
 * Insert the method's description here.
 * Creation date: (10/2/2001 3:15:35 PM)
 * @return com.dataskill.xcf.data.DataIterator
 */
public INodeIterator getElements() throws XCFException {
	return new NITERATOR_Generic(this);
}

/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 10:15:45 AM)
 * @return com.dataskill.xcf.data.INodeElementManager
 */
public INodeManager getMgr() {
	return mgr;
}
/**
 * Insert the method's description here.
 * Creation date: (10/4/2001 4:01:22 PM)
 * @return java.lang.String
 */
public String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/2001 4:50:19 PM)
 * @return java.lang.String
 */
public java.lang.String getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (10/4/2001 4:08:03 PM)
 * @return com.dataskill.xcf.data.DataValue
 */
public Object getValue() throws XCFException {
	return value;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 2:46:01 PM)
 * @param name java.lang.String
 */
public void removeDimension(java.lang.String dimensionName) throws XCFException {
	INodeElement element = getDimension(dimensionName);
	if (element != null) {
		nodeElements.remove(element);
		lookup.remove(element.getName());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2001 5:13:28 PM)
 * @param address java.lang.String
 */
public void setAddress(java.lang.String address) throws XCFException {
	this.address = address;
	INodeIterator iter = getElements();
	while (iter.hasMoreElements()) {
		INodeElement element = iter.nextElement();
		element.setAddress(address + "." + element.getName());
	}
}

/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 10:15:45 AM)
 * @param newMgr com.dataskill.xcf.data.INodeElementManager
 */
public void setMgr(INodeManager newMgr) throws XCFException {
	mgr = newMgr;
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/2001 4:50:19 PM)
 * @param newType java.lang.String
 */
public void setType(java.lang.String newType) {
	type = newType;
}
/**
 * Insert the method's description here.
 * Creation date: (10/4/2001 4:08:03 PM)
 * @param newValue com.dataskill.xcf.data.DataValue
 */
public void setValue(Object newValue) throws XCFException {
	value = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (4/15/2002 2:12:49 PM)
 * @return int
 */
public int size() throws XCFException {
	return nodeElements.size();
}
/**
 * Insert the method's description here.
 * Creation date: (10/4/2001 5:24:19 PM)
 * @return java.lang.String
 */
public String toString() {
	if (value == this) {
		return getName();
	}
	
	if (value != null) {
		return value.toString();
	}
	return getName()/* + ":" + super.toString()*/;
}

/**
 * Insert the method's description here.
 * Creation date: (4/30/2002 1:03:40 PM)
 * @return com.dataskill.xcf.data.INodeElement
 * @param index int
 */
public INodeElement elementAt(int index) throws XCFException {
	return (INodeElement)nodeElements.get(index);
}
}
