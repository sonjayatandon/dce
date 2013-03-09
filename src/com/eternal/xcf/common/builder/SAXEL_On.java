package com.eternal.xcf.common.builder;

import org.xml.sax.Attributes;

import com.eternal.xcf.request.processor.XCFProcessingInstruction;
import com.eternal.xcf.request.processor.instructions.INSTRUCTION_On;
import com.eternal.xcf.request.processor.instructions.INSTRUCTION_Operation;
import com.eternal.xcf.sax.SAXEL_Composite;

/**
 * This SAXEL configures an "On" instruction.
 * 
 * This SAXEL assumes an INSTRUCTION_Composite exists in handler under the tag XCFProcessingInstruction.XCF_TAG
 * The sub tags under each result are intended to create instruction objects.
 * 
 * The syntax is of the form:
 *  <on>
 *    <[result]>..sub tags...</[result]>
 *    <[result]>..sub tags...</[result]>
 *    ...
 *  </on>
 *  
 * @author sonjaya
 *
 */
public class SAXEL_On extends SAXEL_Composite {
	public final static String XCF_TAG = "on";
	
	public SAXEL_On() {
		super(XCF_TAG);
	}

	/**
	 * Create an On instruction and add it to the current container.
	 * Maike the On instruction the container for all child tags
	 */
	@Override
	public void startInterpreting(String uri, String name, String rawName, Attributes attributes) {
		// get the instruction container
		INSTRUCTION_Operation container = null;
		try {
			container = (INSTRUCTION_Operation)getHandler().peekValue(XCFProcessingInstruction.XCF_TAG);
			if (container == null) {
				handleError("<on> failed because there was no container.");
				return;
			}
		} catch (ClassCastException e) {
			handleError("<on> failed because the container was not an INSTRUCTION_Operation.");
			return;
		}
		
		// create an On instruction, add it to the container
		INSTRUCTION_On instruction = new INSTRUCTION_On();
		container.addInstruction(instruction, true);
		getHandler().pushValue(XCFProcessingInstruction.XCF_TAG, instruction);
	}

	/**
	 * The first child tag will actually identify a result.  All child tags of that
	 * result are intended to build a composite instruction that is supposed to
	 * be executed when that result is set.
	 * 
	 * To do this, inject a SAXEL_Composite into the stack to represent the result.
	 * Set the current result for the On instruction.  This insures all instructions
	 * created by the child tags of the result will be bound to the result.
	 */
	@Override
	public void handleStartChildElement(String uri, String name, String rawName, Attributes attributes) {
		try {
			INSTRUCTION_On instruction = (INSTRUCTION_On)getHandler().peekValue(XCFProcessingInstruction.XCF_TAG);
			instruction.setCurrentResult(name);
			SAXEL_Composite saxel = new SAXEL_Composite(name);
			saxel.setHandler(getHandler());
			getHandler().push(saxel);
		} catch (ClassCastException e) {
			// we might get here if there were errors earlier -- since those error were reported,
			// the only thing to do is return.
			return;
		}		
	}
	
	/**
	 * Cleaning up popping the On instruction off of the container stack.
	 */
	@Override
	public void endInterpreting(String uri, String name, String qName) {
		XCFProcessingInstruction instruction = (XCFProcessingInstruction)getHandler().popValue(XCFProcessingInstruction.XCF_TAG);
		if (instruction == null) return;
	}
}
