package ru.greenbudgie.util;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier.Operation;

public class CustomAttribute {

	private Attribute attribute;
	private double value;
	private Operation operation;
	private AttributeSlot slot;

	public CustomAttribute(Attribute attribute, double value) {
		this(attribute, value, Operation.ADD_NUMBER, AttributeSlot.MAIN_HAND);
	}

	public CustomAttribute(Attribute attribute, double value, AttributeSlot slot) {
		this(attribute, value, Operation.ADD_NUMBER, slot);
	}

	public CustomAttribute(Attribute attribute, double value, Operation operation, AttributeSlot slot) {
		this.attribute = attribute;
		this.value = value;
		this.operation = operation;
		this.slot = slot;
	}

	public String getName() {
		return InventoryHelper.getAttributeName(getAttribute());
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public AttributeSlot getSlot() {
		return slot;
	}

	public void setSlot(AttributeSlot slot) {
		this.slot = slot;
	}

	public enum AttributeSlot {

		MAIN_HAND("mainhand"), OFF_HAND("offhand"), FEET("feet"), LEGS("legs"), CHEST("chest"), HEAD("head");

		private String name;

		private AttributeSlot(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

}
