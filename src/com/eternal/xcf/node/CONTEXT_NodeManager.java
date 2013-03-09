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

import java.util.StringTokenizer;
import java.util.Stack;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;

/**
 * Insert the type's description here.
 * Server INodeElementManager intialzed in init().
 * Session's INodeElementManager instantiated and initialized in findManager.
 * Creation date: (10/8/2001 11:12:16 AM)
 * @author: Administrator
 */
public class CONTEXT_NodeManager implements INodeManager {

	INodeElement root;
	private XCFFacade facade;
	SERVICE_NodeContextManager cmgr;

	private Stack<INodeElement> scope = new Stack<INodeElement>();
	private VISITOR_ElementCounter elCounter = new VISITOR_ElementCounter();
	private VISITOR_DimensionNameCounter dnCounter = new VISITOR_DimensionNameCounter();
	private String name;

	/**
	 * INodeElementManager constructor comment.
	 */
	public CONTEXT_NodeManager() {
	}
	
	/**
	 * INodeElementManager constructor comment.
	 */
	public CONTEXT_NodeManager(boolean p) {
	}
	
	public void setContextManager(SERVICE_NodeContextManager cmgr) throws XCFException {
		this.cmgr = cmgr;
		
		root = newElement(NT_GENERIC, "");
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/12/2001 1:50:10 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param context com.dataskill.xcf.data.INodeElement
	 * @param dimensionName java.lang.String
	 */
	public INodeElement addDimension(INodeElement context, String type, String dimensionName) throws XCFException {
		if (context == null) {
			context = root;
		}
	
		INodeElement dimension = newElement(type, dimensionName);
		return addElement(context, dimension);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/12/2001 1:50:10 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param context com.dataskill.xcf.data.INodeElement
	 * @param dimensionName java.lang.String
	 */
	public INodeElement addElement(INodeElement context, INodeElement element) throws XCFException {
		if (context == null) {
			context = root;
		}
	
		return context.addElement(element)	;
	}
	
	/**
	 * Returns the dimension specified by the context and address.   Returns null there is
	 * no dimension at the address specified.  If makeDimension is set to true, then, rather than
	 * return NULL, it creates a generic dimension at that address and returns it.
	 * <br>
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return com.dataskill.xcf.data.INodeElement the dimension specified
	 * @param context INodeElement the dimension to use as the address base.  If null, assume
	 * the context is the manager's root.
	 * @param address java.lang.String the address of the dimension to return
	 * @param makeDimension if set to true, create a generic dimension if one does not
	 * exist at the address.
	 */
	public INodeElement bind(INodeElement context, String address, boolean makeDimensions, String type) throws XCFException {
		StringTokenizer tokenizer = new StringTokenizer(address, ".");
		INodeElement element;
		if (context == null) {
			element = root;
		} else {
			element = context;
		}
	
		if (address.startsWith("mgr.")) {
			element = root;
			tokenizer.nextToken();
		}
			
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			boolean lastToken = !tokenizer.hasMoreTokens();
			INodeElement nextDim = element.getDimension(token);
			if ((nextDim == null) && makeDimensions) {
				if (lastToken) {
					nextDim = addDimension(element, type, token);
				} else {
					nextDim = addDimension(element, NT_GENERIC, token);
				}
			} else if ((nextDim == null) && !makeDimensions) {
				// the dimension doesn't exist and we are not supposed to
				// make it.
				return null;
			}
	
			element = nextDim;
		}
		
		return element;
	}
	
	/**
	 * Assigns src's value to dest. This method will create
	 * an address at dest if one doesn't already exists.<br>
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param address java.lang.String
	 */
	public void copyValue(String dest, String src) throws XCFException {
		INodeElement s = bind(null, src, false, null);
	
		if (s != null) {
			putValue(dest, s.getValue());	
		}
	}
	
	/**
	 * Returns the nth child dimension of el.<br>
	 * Creation date: (4/30/2002 1:02:42 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param address java.lang.String
	 * @param index int
	 */
	public INodeElement elementAt(INodeElement el, int index) throws XCFException {
		return el.elementAt(index);
	}
	/**
	 * Returns the nth child dimension of the element specified by address.<br>
	 * Creation date: (4/30/2002 1:02:42 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param address java.lang.String
	 * @param index int
	 */
	public INodeElement elementAt(String address, int index) throws XCFException {
		INodeElement el = bind(null, address, false, null);
		if (el == null) return null;
		return elementAt(el, index);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/22/2001 2:47:14 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param address java.lang.String
	 */
	public INodeElement findElement(String address) throws XCFException {
		INodeElement element = null;
		int scopeIndex = scope.size() - 1;
		while (element == null && scopeIndex >= 0) {
			INodeElement context = (INodeElement)scope.get(scopeIndex);
			element = bind(context, address, false, null);
			scopeIndex--;
		}
		
		if (element == null) {
			element = bind(null, address, false, null);
		}
		
		return element;
	}
	/**
	 * Returns the dimension specified by address and context.<br>
	 * 
	 * Creation date: (10/12/2001 1:50:10 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param context com.dataskill.xcf.data.INodeElement
	 * @param dimensionName java.lang.String
	 */
	public INodeElement getDimension(INodeElement context, String address) throws XCFException {
		if (context == null) {
			context = root;
		}
		return bind(context, address, false, null);
	}
	/**
	 * Returns an iterator of the children of the element specified by address.
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param address java.lang.String
	 */
	public INodeIterator getElements(String address) throws XCFException {
		INodeElement el = bind(null, address, false, null);
		if (el == null) {
			return NITERATOR_Empty.s();
		}
		
		return el.getElements();
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/8/2001 2:41:40 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @depreciated
	 */
	public INodeElement getRoot() {
		return root;
	}
	/**
	 * Returns the value of the element specified by context and address.
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param address java.lang.String
	 */
	public Object getValue(INodeElement context, String address) throws XCFException {
		INodeElement el = bind(context, address, false, null);
		if (el == null) {
			return null;
		}
		
		return el.getValue();
	}
	/**
	 * Returns the value stored in the dimension referenced by address.
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return a value stored in a dimension.
	 * @param address java.lang.String an address of a dimension.
	 */
	public Object getValue(String address) throws XCFException {
		INodeElement el = bind(null, address, false, null);
		if (el == null) {
			return null;
		}
		
		return el.getValue();
	}
	
	/**
	 * Creates an element of the type specified and names it with the name
	 * passed in.<br>
	 * Creation date: (10/8/2001 11:26:03 AM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param type java.lang.String
	 * @param name java.lang.String
	 */
	public INodeElement newElement(String type, String name) throws XCFException {
		INodeElement node = cmgr.newElement(type);
		node.setMgr(this);
		node.setName(name);
		return node;
	}
	
	/**
	 * Returns the number of times dimName has a value
	 * in the generation specified.<br>
	 * FOR EXAMPLE:<br>
	 * If the dimensional space lookes like:<br>
	 * orders.101.798.should-delete<br>
	 * orders.101.798.compliant<br>
	 * orders.101.799.compliant<br>
	 * orders.102.798.compliant<br>
	 * orders.102.799.should-delete<br>
	 * orders.102.799.compliant<br>
	 * then the call numberOf(orders, "should-delete", 2) will return 2.<br>
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param address java.lang.String
	 */
	public int numberOf(INodeElement el, String dimName, int genNum) throws XCFException {
		dnCounter.dimensionName = dimName;
		dnCounter.numElements = 0;
		visitGeneration(el, genNum, dnCounter);
		return dnCounter.numElements;
	}
	/**
	 * Returns the number of times dimName has a value
	 * in the generation specified.<br>
	 * FOR EXAMPLE:<br>
	 * If the dimensional space lookes like:<br>
	 * orders.101.798.should-delete<br>
	 * orders.101.798.compliant<br>
	 * orders.101.799.compliant<br>
	 * orders.102.798.compliant<br>
	 * orders.102.799.should-delete<br>
	 * orders.102.799.compliant<br>
	 * then the call numberOf("orders", "should-delete", 2) will return 2.<br>
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param address java.lang.String
	 */
	public int numberOf(String address, String dimName, int genNum) throws XCFException {
		INodeElement el = bind(null, address, false, null);
		if (el == null) return 0;
		return numberOf(el, dimName, genNum);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/22/2001 2:45:28 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 */
	public INodeElement popContext() {
		return (INodeElement)scope.pop();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/22/2001 2:45:08 PM)
	 * @param context com.dataskill.xcf.data.INodeElement
	 */
	public void pushContext(INodeElement context) {
		scope.push(context);
	}
	/**
	 * Puts dimension as a child dimension of element.  If there is another
	 * child in element with the same name, it is replaced.<br>
	 * Creation date: (10/18/2001 2:45:28 PM)
	 * @param element com.dataskill.xcf.data.INodeElement
	 * @param dimension com.dataskill.xcf.data.INodeElement
	 */
	public void putDimension(INodeElement element, INodeElement dimension) throws XCFException {
		if (element == null) {
			element = root;
		}
		element.removeDimension(dimension.getName());
		addElement(element, dimension);
	}
	/**
	 * Creates (and overwrites any existing) dimension of type "generic"
	 * at the address.
	 * <br>Creation date: (10/18/2001 2:45:28 PM)
	 * @param address the address of the new dimension.
	 */
	public void putDimension(String address) throws XCFException {
		putDimension(NT_GENERIC, address);
	}
	/**
	 * Creates (and overwrites any existing) dimension of the type passed in
	 * at the address.<br>
	 * <br>Creation date: (10/18/2001 2:45:28 PM)
	 * @param address the address of the new dimension.
	 */
	public void putDimension(String type, String address) throws XCFException {
		bind(null, address, true, type);
	}
	
	/**
	 * Puts a value in the dimension at the address specified.  This method will overrite
	 * any value already in the dimension.
	 * <br>Creation date: (10/12/2001 1:36:26 PM)
	 * @param address identifies a dimension
	 * @param value an object to store in the address
	 */
	public void putValue(INodeElement context, String address, Object value) throws XCFException  {
		INodeElement element = bind(context, address, true, NT_GENERIC);
		try {
			element.setValue(value);
		} catch (NullPointerException e) {
			throw new XCFException(address + " is an invalid dimension.");
		}
	}
	/**
	 * Puts a value in the dimension at the address specified.  This method will overrite
	 * any value already in the dimension.
	 * <br>Creation date: (10/12/2001 1:36:26 PM)
	 * @param address identifies a dimension
	 * @param value an object to store in the address
	 */
	public void putValue(String address, Object value) throws XCFException  {
		INodeElement element = bind(null, address, true, NT_GENERIC);
		try {
			element.setValue(value);
		} catch (NullPointerException e) {
			throw new XCFException(address + " is an invalid dimension.");
		}
	}
	
	
	/**
	 * Removes the dimension identifyied by context and address.<br>
	 * Creation date: (4/23/2002 4:04:50 PM)
	 * @param address java.lang.String
	 */
	public void removeDimension(INodeElement context, String address) throws XCFException {
		if ((address == null) || (address.trim().length() == 0)) {
			throw new XCFException("Attempted to remove a null address");
		}
		
		int index = address.lastIndexOf(".");
	
		if (index == 0) {
			throw new XCFException("Poorly formed address: " + address);
		}
		
		INodeElement srcDim = context;
		
		if (srcDim == null) {
			srcDim = getRoot();
		}
		
		String delDim = address.substring(index+1);
	
		if (delDim.trim().length() == 0) {
			throw new XCFException("Poorly formed address: " + address);
		}
		
		if (index > 0) {
			String srcDimAddress = address.substring(0, index);
	
			srcDim = getDimension(srcDim, srcDimAddress);
	
			if (srcDim == null) {
				// there is nothing to remove - the dimension doesn't exists.
				return;
			}
		}
	
		srcDim.removeDimension(delDim);
	}
	/**
	 * Removes the dimension identifyied by address from
	 * the context.<br>
	 * Creation date: (4/23/2002 4:04:50 PM)
	 * @param address java.lang.String
	 */
	public void removeDimension(String address) throws XCFException {
		removeDimension(getRoot(), address);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/25/2001 9:50:26 AM)
	 */
	public void resetScope() {
		scope.removeAllElements();
	}
	
	
	
	/**
	 * Returns the number of elements at the generation 
	 * in the generation specified.<br>
	 * FOR EXAMPLE:<br>
	 * If the dimensional space lookes like:<br>
	 * orders.101.798.should-delete<br>
	 * orders.101.798.compliant<br>
	 * orders.101.799.compliant<br>
	 * orders.102.798.compliant<br>
	 * orders.102.799.should-delete<br>
	 * orders.102.799.compliant<br>
	 * then the call size(orders, 0) will return 2.<br>
	 *      the call size(orders, 1) will return 4.<br>
	 *      the call size(orders, 2) will return 6.<br>
	 *      the call size(orders, 3) will return 0.<br>
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param address java.lang.String
	 */
	public int size(INodeElement el, int genNum) throws XCFException {
		elCounter.numElements = 0;
		visitGeneration(el, genNum, elCounter);
		return elCounter.numElements;
	}
	/**
	 * Returns the number of child dimensions at the address.<br>
	 * FOR EXAMPLE:<br>
	 * If the dimensional space lookes like:<br>
	 * orders.101.798.should-delete<br>
	 * orders.101.798.compliant<br>
	 * orders.101.799.compliant<br>
	 * orders.102.798.compliant<br>
	 * orders.102.799.should-delete<br>
	 * orders.102.799.compliant<br>
	 * then size("orders") will return 2 (101 and 102 are the child dimensions).<br>
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param address java.lang.String
	 */
	public int size(String address) throws XCFException {
		INodeElement el = bind(null, address, false, null);
		if (el == null) {
			return 0;
		}
		
		return el.size();
	}
	/**
	 * Returns the number of elements at the generation 
	 * in the generation specified.<br>
	 * FOR EXAMPLE:<br>
	 * If the dimensional space lookes like:<br>
	 * orders.101.798.should-delete<br>
	 * orders.101.798.compliant<br>
	 * orders.101.799.compliant<br>
	 * orders.102.798.compliant<br>
	 * orders.102.799.should-delete<br>
	 * orders.102.799.compliant<br>
	 * then the call size("orders", 0) will return 2.<br>
	 *      the call size("orders", 1) will return 4.<br>
	 *      the call size("orders", 2) will return 6.<br>
	 *      the call size("orders", 3) will return 0.<br>
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param address java.lang.String
	 */
	public int size(String address, int genNum) throws XCFException {
		INodeElement el = bind(null, address, false, null);
		if (el == null) return 0;
		return size(el, genNum);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 9:55:55 AM)
	 * @param context com.dataskill.xcf.data.INodeElement
	 * @param selectors java.lang.String[]
	 * @param visitor com.dataskill.xcf.data.INodeElementVisitor
	 */
	public void visitAll(INodeElement context, String[] selectors, INodeElementVisitor visitor) throws XCFException {
		if (context == null) return;
		
		INodeIterator iter = context.getElements();
		while (iter.hasMoreElements()) {
			INodeElement element = iter.nextElement();
			boolean shouldVisit = true;
			if (selectors != null) {
				shouldVisit = false;
				for (int i=0; i < selectors.length; i++) {
					if (element.getType().equals(selectors[i])) {
						shouldVisit = true;
						break;
					}
				}
			}
	
			if (shouldVisit) {
				visitor.startVisit(element);
			}
	
			if (!element.getType().equals("reference")) {
				visitAll(element, selectors, visitor);
			}
				
			if (shouldVisit) {
				visitor.endVisit(element);
			}
		}
	}
	/**
	 * Iterates through all the elements at a specific generation.
	 * FOR EXAMPLE:<br>
	 * If the dimensional space lookes like:<br>
	 * orders.101.798.should-delete<br>
	 * orders.101.798.compliant<br>
	 * orders.101.799.compliant<br>
	 * orders.102.798.compliant<br>
	 * orders.102.799.should-delete<br>
	 * orders.102.799.compliant<br>
	 * then the call visitGeneration(orders, 2, visitor) will result in the following
	 * visitor calls:.<br>
	 *      visitor.startVisit(orders.101.798).<br>
	 *      visitor.startVisit(orders.101.799).<br>
	 *      visitor.startVisit(orders.102.798).<br>
	 *      visitor.startVisit(orders.101.799).<br>
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return com.dataskill.xcf.data.INodeElement
	 * @param address java.lang.String
	 */
	public void visitGeneration(INodeElement el, int genNum, INodeElementVisitor visitor) throws XCFException {
		if (el == null) return;
		
		if (genNum < 0) {
			throw new XCFException("genNum MUST be >= 0 in visitGeneration method call");
		}
		
		if (genNum == 0) {
			visitor.startVisit(el);
			return;
		}
		
		INodeIterator iter = el.getElements();
		while (iter.hasMoreElements()) {
			el = iter.nextElement();
			visitGeneration(el, genNum - 1, visitor);
		}
	}
	/**
	 * @see XCFComponent#setServer(XCFServer)
	 */
	public void setFacade(XCFFacade facade) {
		this.facade = facade;
	}


	/**
	 * @see XCFContext#getServer()
	 */
	public XCFFacade getFacade() throws XCFException {
		return facade;
	}

	/**
	 * @see XCFService#getName()
	 */
	public String getName() {
		return name;
	}


	/**
	 * @see XCFService#setName(String)
	 */
	public void setName(String name) {
		this.name = name;
	}

}
