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



import com.eternal.xcf.core.XCFException;

/**
 * Insert the type's description here.
 * Creation date: (10/1/2001 1:27:48 PM)
 * @author: Administrator
 */
public interface INodeElement {
/**
 * Insert the method's description here.
 * Creation date: (10/1/2001 2:17:38 PM)
 * @return com.dataskill.smw.data.DataElement
 * @param dimensionName java.lang.String
 * @param value com.dataskill.smw.data.DataElement
 */
INodeElement addElement(INodeElement value) throws XCFException;
/**
 * Insert the method's description here.
 * Creation date: (10/17/2001 4:47:10 PM)
 * @return com.dataskill.xcf.data.DataElement
 * @param mgr com.dataskill.xcf.data.DataElementManager
 * @param name java.lang.String
 */
INodeElement copy(INodeManager mgr, String name) throws XCFException;
/**
 * Insert the method's description here.
 * Creation date: (10/25/2001 5:08:27 PM)
 * @return java.lang.String
 */
String getAddress();
/**
 * Insert the method's description here.
 * Creation date: (10/2/2001 2:02:58 PM)
 * @return com.dataskill.xcf.data.DataElement
 * @param dimension java.lang.String
 */
INodeElement getDimension(String dimension) throws XCFException;
/**
 * Insert the method's description here.
 * Creation date: (10/2/2001 2:01:34 PM)
 * @return com.dataskill.xcf.data.DataIterator
 */
INodeIterator getElements() throws XCFException;
/**
 * Insert the method's description here.
 * Creation date: (10/17/2001 3:42:36 PM)
 * @return com.dataskill.xcf.data.DataElementHandler
 */
INodeManager getMgr();
/**
 * Insert the method's description here.
 * Creation date: (10/1/2001 2:28:28 PM)
 * @return java.lang.String
 */
String getName();
void setName(String name) throws XCFException;
/**
 * Insert the method's description here.
 * Creation date: (10/17/2001 4:49:44 PM)
 * @return java.lang.String
 */
String getType();
/**
 * Insert the method's description here.
 * Creation date: (10/1/2001 2:28:28 PM)
 * @return java.lang.String
 */
Object getValue() throws XCFException;
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 2:45:52 PM)
 * @param name java.lang.String
 */
void removeDimension(String name) throws XCFException;
/**
 * Insert the method's description here.
 * Creation date: (10/25/2001 5:13:22 PM)
 * @param address java.lang.String
 */
void setAddress(String address) throws XCFException;
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 10:14:05 AM)
 * @param newMgr com.dataskill.xcf.data.DataElementManager
 */
void setMgr(INodeManager newMgr) throws XCFException;
/**
 * Insert the method's description here.
 * Creation date: (10/17/2001 4:50:00 PM)
 * @param type java.lang.String
 */
void setType(String type);
/**
 * Insert the method's description here.
 * Creation date: (10/1/2001 2:28:28 PM)
 * @return java.lang.String
 */
void setValue(Object value) throws XCFException;
/**
 * Insert the method's description here.
 * Creation date: (4/15/2002 2:12:27 PM)
 * @return int
 */
int size() throws XCFException;

/**
 * Insert the method's description here.
 * Creation date: (4/30/2002 1:03:17 PM)
 * @return com.dataskill.xcf.data.DataElement
 * @param index int
 */
INodeElement elementAt(int index) throws XCFException;
}
