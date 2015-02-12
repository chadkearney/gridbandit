package it.gridband.campaigner.score;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import java.util.Map;
import java.util.Stack;

public class PostfixFormula {

	ImmutableList<String> operations;

	public PostfixFormula(String formula) {
		operations = ImmutableList.copyOf(Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().split(formula));
	}

	public double evaluate(Map<String, Double> arguments) {
		Stack<Double> s = new Stack<>();

		for (String operation : operations) {
			if (operation.equals("+")) {
				s.push(s.pop() + s.pop());
			} else if (operation.equals("-")) {
				s.push(-s.pop() + s.pop());
			} else if (operation.equals("*")) {
				s.push(s.pop() * s.pop());
			} else if (operation.equals("/")) {
				s.push((1 / s.pop()) * s.pop());
			} else if (operation.equals("_ln_")) {
				s.push(Math.log(s.pop()));
			} else if (operation.equals("_exp_")) {
				s.push(Math.exp(s.pop()));
			} else if (CharMatcher.JAVA_LETTER.matchesAllOf(operation)) {
				s.push(arguments.containsKey(operation) ? arguments.get(operation) : 0d);
			} else {
				s.push(Double.valueOf(operation));
			}
		}

		Double result = s.pop();
		if (s.empty()) {
			return result;
		} else {
			throw new IllegalStateException("Malformed formula: final result stack included multiple values.");
		}
	}

}
