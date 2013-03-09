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

/**
 * Insert the type's description here.
 * Creation date: (10/2/2001 2:56:50 PM)
 * @author: Administrator
 */
class NITERATOR_Empty implements INodeIterator {
	static NITERATOR_Empty _singleton = new NITERATOR_Empty();
/**
 * EmptyIterator constructor comment.
 */
public NITERATOR_Empty() {
	super();
}
/**
 * firstElement method comment.
 */
public void first() {
}
/**
 * hasMoreElements method comment.
 */
public boolean hasMoreElements() {
	return false;
}
/**
 * nextElement method comment.
 */
public INodeElement nextElement() {
	return null;
}
	static NITERATOR_Empty s() {
		return _singleton;
	}
}
