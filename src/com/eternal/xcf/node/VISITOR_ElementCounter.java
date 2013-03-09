/*
 * Copyright � 2004 Eternal Adventures, Inc.
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
 * Creation date: (4/30/2002 12:19:20 PM)
 * @author: Administrator
 */
class VISITOR_ElementCounter implements INodeElementVisitor {
	int numElements = 0;
/**
 * ElementCounter constructor comment.
 */
public VISITOR_ElementCounter() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2002 12:19:20 PM)
 * @param element com.dataskill.xcf.data.DataElement
 */
public void endVisit(INodeElement element) throws XCFException {}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2002 12:19:20 PM)
 * @param element com.dataskill.xcf.data.DataElement
 */
public void startVisit(INodeElement element) throws XCFException {
	numElements += element.size();
}
}
