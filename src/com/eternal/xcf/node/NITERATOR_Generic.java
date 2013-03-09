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

import java.util.Iterator;
/**
 * Insert the type's description here.
 * Creation date: (10/4/2001 3:53:22 PM)
 * @author: Administrator
 */
class NITERATOR_Generic implements INodeIterator {
	NODE_GenericElement element;
	Iterator iterator;
/**
 * GenericIterator constructor comment.
 */
NITERATOR_Generic(NODE_GenericElement element) {
	super();
	this.element = element;
	first();
}
/**
 * firstElement method comment.
 */
public void first() {
	iterator = element.nodeElements.iterator();
}
/**
 * hasMoreElements method comment.
 */
public boolean hasMoreElements() {
	return iterator.hasNext();
}
/**
 * nextElement method comment.
 */
public INodeElement nextElement() {
	return (INodeElement)iterator.next();
}
}
