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

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;


/**
 * Insert the type's description here.
 * Server DataElementManager intialzed in init().
 * Session's DataElementManager instantiated and initialized in findManager.
 * Creation date: (10/8/2001 11:12:16 AM)
 * @author: Administrator
 */
public interface INodeManager extends XCFContext {
	
	public static String NT_GENERIC = "ntg";

	public INodeElement newElement(String type, String name) throws XCFException;

	/**
	 * Insert the method's description here.
	 * Creation date: (10/12/2001 1:50:10 PM)
	 * @return com.dataskill.xcf.data.DataElement
	 * @param context com.dataskill.xcf.data.DataElement
	 * @param dimensionName java.lang.String
	 */
	public INodeElement addDimension(INodeElement context, String type, String dimensionName) throws XCFException;
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/12/2001 1:50:10 PM)
	 * @return com.dataskill.xcf.data.DataElement
	 * @param context com.dataskill.xcf.data.DataElement
	 * @param dimensionName java.lang.String
	 */
	public INodeElement addElement(INodeElement context, INodeElement element) throws XCFException;
	
	/**
	 * Returns the dimension specified by the context and address.   Returns null there is
	 * no dimension at the address specified.  If makeDimension is set to true, then, rather than
	 * return NULL, it creates a generic dimension at that address and returns it.
	 * <br>
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return com.dataskill.xcf.data.DataElement the dimension specified
	 * @param context DataElement the dimension to use as the address base.  If null, assume
	 * the context is the manager's root.
	 * @param address java.lang.String the address of the dimension to return
	 * @param makeDimension if set to true, create a generic dimension if one does not
	 * exist at the address.
	 */
	public INodeElement bind(INodeElement context, String address, boolean makeDimensions, String type) throws XCFException;
	
	/**
	 * Assigns src's value to dest. This method will create
	 * an address at dest if one doesn't already exists.<br>
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return com.dataskill.xcf.data.DataElement
	 * @param address java.lang.String
	 */
	public void copyValue(String dest, String src) throws XCFException;
	
	/**
	 * Returns the nth child dimension of el.<br>
	 * Creation date: (4/30/2002 1:02:42 PM)
	 * @return com.dataskill.xcf.data.DataElement
	 * @param address java.lang.String
	 * @param index int
	 */
	public INodeElement elementAt(INodeElement el, int index) throws XCFException;
	
	/**
	 * Returns the nth child dimension of the element specified by address.<br>
	 * Creation date: (4/30/2002 1:02:42 PM)
	 * @return com.dataskill.xcf.data.DataElement
	 * @param address java.lang.String
	 * @param index int
	 */
	public INodeElement elementAt(String address, int index) throws XCFException;
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/22/2001 2:47:14 PM)
	 * @return com.dataskill.xcf.data.DataElement
	 * @param address java.lang.String
	 */
	public INodeElement findElement(String address) throws XCFException;
	
	/**
	 * Returns the dimension specified by address and context.<br>
	 * 
	 * Creation date: (10/12/2001 1:50:10 PM)
	 * @return com.dataskill.xcf.data.DataElement
	 * @param context com.dataskill.xcf.data.DataElement
	 * @param dimensionName java.lang.String
	 */
	public INodeElement getDimension(INodeElement context, String address) throws XCFException;
	
	/**
	 * Returns an iterator of the children of the element specified by address.
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return com.dataskill.xcf.data.DataElement
	 * @param address java.lang.String
	 */
	public INodeIterator getElements(String address) throws XCFException;
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/8/2001 2:41:40 PM)
	 * @return com.dataskill.xcf.data.DataElement
	 * @depreciated
	 */
	public INodeElement getRoot();
	
	/**
	 * Returns the value of the element specified by context and address.
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return com.dataskill.xcf.data.DataElement
	 * @param address java.lang.String
	 */
	public Object getValue(INodeElement context, String address) throws XCFException;
	
	/**
	 * Returns the value stored in the dimension referenced by address.
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return a value stored in a dimension.
	 * @param address java.lang.String an address of a dimension.
	 */
	public Object getValue(String address) throws XCFException;
	
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
	 * @return com.dataskill.xcf.data.DataElement
	 * @param address java.lang.String
	 */
	public int numberOf(INodeElement el, String dimName, int genNum) throws XCFException;
	
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
	 * @return com.dataskill.xcf.data.DataElement
	 * @param address java.lang.String
	 */
	public int numberOf(String address, String dimName, int genNum) throws XCFException;
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/22/2001 2:45:28 PM)
	 * @return com.dataskill.xcf.data.DataElement
	 */
	public INodeElement popContext();
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/22/2001 2:45:08 PM)
	 * @param context com.dataskill.xcf.data.DataElement
	 */
	public void pushContext(INodeElement context);
	
	/**
	 * Puts dimension as a child dimension of element.  If there is another
	 * child in element with the same name, it is replaced.<br>
	 * Creation date: (10/18/2001 2:45:28 PM)
	 * @param element com.dataskill.xcf.data.DataElement
	 * @param dimension com.dataskill.xcf.data.DataElement
	 */
	public void putDimension(INodeElement element, INodeElement dimension) throws XCFException;
	
	/**
	 * Creates (and overwrites any existing) dimension of type "generic"
	 * at the address.
	 * <br>Creation date: (10/18/2001 2:45:28 PM)
	 * @param address the address of the new dimension.
	 */
	public void putDimension(String address) throws XCFException;
	
	/**
	 * Creates (and overwrites any existing) dimension of the type passed in
	 * at the address.<br>
	 * <br>Creation date: (10/18/2001 2:45:28 PM)
	 * @param address the address of the new dimension.
	 */
	public void putDimension(String type, String address) throws XCFException;
	
	
	/**
	 * Puts a value in the dimension at the address specified.  This method will overrite
	 * any value already in the dimension.
	 * <br>Creation date: (10/12/2001 1:36:26 PM)
	 * @param address identifies a dimension
	 * @param value an object to store in the address
	 */
	public void putValue(INodeElement context, String address, Object value) throws XCFException;
	
	/**
	 * Puts a value in the dimension at the address specified.  This method will overrite
	 * any value already in the dimension.
	 * <br>Creation date: (10/12/2001 1:36:26 PM)
	 * @param address identifies a dimension
	 * @param value an object to store in the address
	 */
	public void putValue(String address, Object value) throws XCFException;
	
	/**
	 * Removes the dimension identifyied by context and address.<br>
	 * Creation date: (4/23/2002 4:04:50 PM)
	 * @param address java.lang.String
	 */
	public void removeDimension(INodeElement context, String address) throws XCFException;
	
	/**
	 * Removes the dimension identifyied by address from
	 * the context.<br>
	 * Creation date: (4/23/2002 4:04:50 PM)
	 * @param address java.lang.String
	 */
	public void removeDimension(String address) throws XCFException;
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/25/2001 9:50:26 AM)
	 */
	public void resetScope();
	
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
	 * @return com.dataskill.xcf.data.DataElement
	 * @param address java.lang.String
	 */
	public int size(INodeElement el, int genNum) throws XCFException;
	
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
	 * @return com.dataskill.xcf.data.DataElement
	 * @param address java.lang.String
	 */
	public int size(String address) throws XCFException;
	
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
	 * @return com.dataskill.xcf.data.DataElement
	 * @param address java.lang.String
	 */
	public int size(String address, int genNum) throws XCFException;
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 9:55:55 AM)
	 * @param context com.dataskill.xcf.data.DataElement
	 * @param selectors java.lang.String[]
	 * @param visitor com.dataskill.xcf.data.DataElementVisitor
	 */
	public void visitAll(INodeElement context, String[] selectors, INodeElementVisitor visitor) throws XCFException;
	
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
	 * @return com.dataskill.xcf.data.DataElement
	 * @param address java.lang.String
	 */
	public void visitGeneration(INodeElement el, int genNum, INodeElementVisitor visitor) throws XCFException;
	
	public void setContextManager(SERVICE_NodeContextManager cmgr) throws XCFException;
	
	public void setName(String name) throws XCFException;
}
