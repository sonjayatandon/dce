package com.tandon.dce.boardengine;

public class PropertyDecorator {
	String propertyName;
	Number amount;
	OperatorType operator;
	
	public PropertyDecorator(String propertyName, Number amount, OperatorType operator) {
		this.propertyName = propertyName;
		this.amount = amount;
		this.operator = operator;
	}

	public Number getValue(String propertyName, Number value) {
		if (this.propertyName.equals(propertyName)) {
			switch(operator) {
				case add:
					value = value.intValue() + amount.intValue();
					break;
				case divide:
					value = value.floatValue() / amount.floatValue();
					break;
				case multiply:
					value = value.floatValue() * amount.floatValue();
					break;
				case subtract:
					value = value.intValue() - amount.intValue();
					break;
			}
		}
		
		return value;
	}
}
