package com.eternal.xcf.common.request.processor;

import java.util.HashMap;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFModule;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.request.processor.XCFProcessingInstruction;

/**
 * Implements module handling by associating a proccessing instruction to 
 * each operation (usually a composite instruction) and invoking that
 * instruction whenever a request is issued for that operation.
 * @author sonjaya
 *
 */
public class MODULE_Instructions implements XCFModule {

	String name;
	String defaultOperation;
	XCFFacade facade;
	HashMap<String, XCFProcessingInstruction> operations = new HashMap<String, XCFProcessingInstruction>();

	//////////////////////////////////////////////////////
	// Define the getters and setters
	public void setFacade(XCFFacade facade) {
		this.facade = facade;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDefaultOperation(String defaultOperation) {
		this.defaultOperation = defaultOperation;
	}

	public String getDefaultOperation() {
		return defaultOperation;
	}

	//////////////////////////////////////////////////////
	// Define operation handling
	/**
	 * Associate the operation name with the processing instruction
	 * that handles that operation
	 * @param operation
	 * @param handler
	 */
	public void addOperationHandler(String operation, XCFProcessingInstruction handler) {
		operations.put(operation, handler);
	}

	/**
	 * Process the request looking up the operation handler and invoking
	 * the process method on that handler.
	 */
	public void process(XCFRequest req) throws XCFException {
		XCFProcessingInstruction handler = operations.get(req.getOperation());
	   	if (handler == null)
	   	{
	   	   throw new XCFException("operation '" + req.getModule() + "." + req.getOperation() + "' not found");
	  	}
		
		handler.process(req);
	}
}
