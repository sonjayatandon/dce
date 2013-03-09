package com.eternal.xcf.request.processor.instructions;

import java.util.HashSet;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.core.response.UTIL_Helper;
import com.eternal.xcf.core.response.XCFResponse;
import com.eternal.xcf.request.processor.XCFProcessingInstruction;

public class INSTRUCTION_Operation extends INSTRUCTION_Composite {
	private HashSet<XCFProcessingInstruction> alwaysExecuteInstructions = new HashSet<XCFProcessingInstruction>();

	public INSTRUCTION_Operation(String name) {
		super(name);
	}

	public Object getFirstProperty(String propertyName) {
		Object propertyValue = getProperty(propertyName);
		if (propertyValue !=null) return propertyValue;

		for (XCFProcessingInstruction instruction: instructions) {
			propertyValue = instruction.getProperty(propertyName);
			if (propertyValue !=null) return propertyValue;
		}

		return null;
	}

	@Override
	public boolean process(XCFRequest request) throws XCFException {
		boolean result = true;

		for (XCFProcessingInstruction instruction : instructions) {
			if ((result || alwaysExecuteInstructions.contains(instruction)) && instruction.process(request) == false) {
				// only set response to failure if it is currently at success
				// we don't want to overwrite any other failure code.
				if (UTIL_Helper.getResult(request).equals(XCFResponse.SUCCESS)) {
					UTIL_Helper.setResult(request, XCFResponse.FAILURE);
				}
				result = false;
			}
		}

		return result;
	}

	public void addInstruction(XCFProcessingInstruction instruction, boolean alwaysExecute) {
		addInstruction(instruction);

		if (alwaysExecute) {
			alwaysExecuteInstructions.add(instruction);
		}
	}

}
