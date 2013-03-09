/*
 * Copyright 2003 Dataskill, Inc.
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
 * @author Sonjaya Tandon
 */
/*
 * Created on Nov 7, 2004
 *
 */
package com.eternal.xcf.sax;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

////////////////////////////////////
// SAX IMPORTS for XML INTERPRETER
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

////////////////////////////////////
// XCF IMPORTS
import com.eternal.xcf.common.service.SERVICE_FlyweightFactory;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;


/**
 * A set of static convinience methods for SAX parsing.
 * @author standon
 *
 */
public class UTIL_Helper {
	
	/**
	 * Checks to see that the flyweight factory exists.  If it doesn't, it creates one and adds it to the facade
	 * @param facade
	 * @throws XCFException
	 */
	private static void assertFlyweightFactoryExists(XCFFacade facade) throws XCFException {
		SERVICE_FlyweightFactory flyweightFactory = (SERVICE_FlyweightFactory)facade.getService(SERVICE_FlyweightFactory.XCF_TAG);
		
		if (flyweightFactory == null) {
			flyweightFactory = new SERVICE_FlyweightFactory();
			facade.putService(SERVICE_FlyweightFactory.XCF_TAG, flyweightFactory);
		}
	}
	
	/**
	 * Interprets an XML input source using the handler passed in.<br>
	 * 
	 * @param InputSource xmlInput
	 * @param InterpreterHandler handler
	 */
	private static void interpretInputSource(InputSource xmlInput, SAX_Handler handler) throws XCFException {
		try {
			assertFlyweightFactoryExists(handler.getFacade());
	        SAXParserFactory factory = SAXParserFactory.newInstance();
	        SAXParser parser = factory.newSAXParser();
		    parser.parse(xmlInput, handler);
		} catch (SAXException e1) {
			throw new XCFException(e1, "Error parsing: " + e1.getLocalizedMessage());
		} catch (IOException e3) {
			throw new XCFException(e3, "Problem accessing: " + e3.getLocalizedMessage());
		} catch (Exception e4) {
			throw new XCFException(e4, "Problem accessing: " + e4.getLocalizedMessage());
		}
	}

	/**
	 * Interpret an XML file 
	 * @param refObj  the object used to locate the file.  
	 * @param fileName the name of the file.  It is assumed the path name of the file is relative to the location of refObj 
	 * @param server the xcf server
	 * @param rootTag the name of the root tag in the xml file
	 * @throws Exception
	 */
	public static final void interpretLocalFile(Object refObj, String fileName, XCFFacade facade, String rootTag) throws XCFException {
    	InputStream stream = null;

    	// always create a new flyweight factory and replace the one 
    	// that is there
    	SERVICE_FlyweightFactory flyweightFactory = new SERVICE_FlyweightFactory();
		facade.putService(SERVICE_FlyweightFactory.XCF_TAG, flyweightFactory);    	
		
    	try {
    	   	// open a stream to the xml file
    		stream = refObj.getClass().getResourceAsStream(fileName);
    		if (stream == null) throw new XCFException("Unable to locate: " + fileName);
    		InputSource input = new InputSource(stream);
    		
    		// Set up the interpreter
    		SAX_Element root = new SAXEL_Root();
    		root.setTag(rootTag);
    		SAX_Handler handler = new SAX_Handler(root, facade);

    		// Interpret the xml file
    		UTIL_Helper.interpretInputSource(input, handler);
    	} finally {
    		// clean up
    	   	if (stream != null) {
       			try {
    				stream.close();
    			} catch (IOException e) {
    				throw new XCFException("Unable to close stream for " + fileName + ": " + e.getMessage());
    			}
        	}
    	} 
	}
	
	/**
	 * Interpret an XML file 
	 * @param refObj  the object used to locate the file.  
	 * @param fileName the name of the file.  It is assumed the path name of the file is relative to the location of refObj 
	 * @param server the xcf server
	 * @param rootTag the name of the root tag in the xml file
	 * @throws Exception
	 */
	public static final void interpretFile(String fileName, XCFFacade facade, String rootTag) throws XCFException {
    	InputStream stream = null;

    	// always create a new flyweight factory and replace the one 
    	// that is there
    	SERVICE_FlyweightFactory flyweightFactory = new SERVICE_FlyweightFactory();
		facade.putService(SERVICE_FlyweightFactory.XCF_TAG, flyweightFactory);    	
		
    	try {
    	   	// open a stream to the xml file
    		stream = new FileInputStream(fileName);
    		if (stream == null) throw new XCFException("Unable to locate: " + fileName);
    		InputSource input = new InputSource(stream);
    		
    		// Set up the interpreter
    		SAX_Element root = new SAXEL_Root();
    		root.setTag(rootTag);
    		SAX_Handler handler = new SAX_Handler(root, facade);

    		// Interpret the xml file
    		UTIL_Helper.interpretInputSource(input, handler);
    	} catch (FileNotFoundException e) {
    		facade.logError(e);
    		throw new XCFException(e, e.getMessage());
    	} finally {
    		// clean up
    	   	if (stream != null) {
       			try {
    				stream.close();
    			} catch (IOException e) {
    				throw new XCFException("Unable to close stream for " + fileName + ": " + e.getMessage());
    			}
        	}
    	} 
	}
	
}
