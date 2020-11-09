package com.tsystems.javaschool.tasks.calculator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.Locale;
import java.util.Stack;
import java.util.StringTokenizer;

public class Calculator {

  // list of available operators
  private static final String OPERATORS = "+-*/";
  // decimal format
  private static final DecimalFormat FORMAT =
      new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.ROOT));

  // stack for holding expression converted to reversed polish notation
  private final Stack<String> stackRPN = new Stack<>();

  private final Stack<String> stackOperations = new Stack<>();
  private final Stack<String> stackAnswer = new Stack<>();

  /**
   * Evaluate statement represented as string.
   *
   * @param statement mathematical statement containing digits, '.' (dot) as decimal mark,
   *                  parentheses, operations signs '+', '-', '*', '/'<br> Example: <code>(1 + 38) *
   *                  4.5 - 1 / 2.</code>
   * @return string value containing result of evaluation or null if statement is invalid
   */
  public String evaluate(String statement) {
    if (!(statement == null)) {
      statement = statement.replaceAll("\\s+", "");
    }
    if (statement == null || statement.isEmpty() ||
        statement.replaceAll("[0-9+\\-*/().]", "").length() > 0 ||
        statement.replaceAll("[.+\\-*/]{2,}", "NULL").contains("NULL") ||
        statement.contains(",")) {
      return null;
    }

    // little trick
    if (statement.charAt(0) == '-') {
      statement = "0" + statement;
    }

    stackRPN.clear();
    stackAnswer.clear();
    stackOperations.clear();

    // splitting input string into tokens
    StringTokenizer stringTokenizer = new StringTokenizer(statement,
        OPERATORS + "()", true);

    // it is like sort station algorithm
    String token;

    while (stringTokenizer.hasMoreTokens()) {
      token = stringTokenizer.nextToken();
      if (isOpenBracket(token)) {
        stackOperations.push(token);      // pushing all open brackets
      } else if (isCloseBracket(token)) { // and if token is close bracket,
        while (!stackOperations.empty()   // we will push all operators what we have inside
            && !isOpenBracket(stackOperations.lastElement())) {
          stackRPN.push(stackOperations.pop()); // of current frame
        }                                 // before new open bracket
        stackOperations.pop();            // and delete this one open bracket
        if (!stackOperations.empty()) {   // so, we don't need to save close brackets
          stackRPN.push(stackOperations.pop());
        }
      } else if (isNumber(token)) {
        stackRPN.push(token); //<- push all numbers
      } else if (isOperator(token)) {
        while (!stackOperations.empty()
            && isOperator(stackOperations.lastElement())
            && getPrecedence(token) <= getPrecedence(stackOperations.lastElement())) {
          stackRPN.push(stackOperations.pop()) ;
        }
        stackOperations.push(token);
      }
    }

    // push all to stackRPN
    while (!stackOperations.empty()) {
      stackRPN.push(stackOperations.pop());
    }
    if (stackRPN.contains("(") || stackRPN.contains(")")) {
      return null;
    }

    // and calculate!
    Collections.reverse(stackRPN);
    return RPNStringToAnswer();
  }

  private String RPNStringToAnswer() {
    double first;
    double second;
    String token;

    while (!stackRPN.isEmpty()) {
      token= stackRPN.pop();
      if (isNumber(token)) {
        stackAnswer.add(token);
      } else if (!stackAnswer.isEmpty()) {
        second = Double.parseDouble(stackAnswer.pop());
        first = Double.parseDouble(stackAnswer.pop());

        switch (token) {
          case ("+"):
            stackAnswer.add(Double.toString(first + second));
            break;
          case ("-"):
            stackAnswer.add(Double.toString(first - second));
            break;
          case ("*"):
            stackAnswer.add(Double.toString(first * second));
            break;
          case ("/"):
            if (second == 0) {
              return null;
            }
            stackAnswer.add(Double.toString(first / second));
            break;
        }
      }
    }

    return FORMAT.format(Double.parseDouble(stackAnswer.pop()));
  }

  private boolean isNumber(String token) {
    try {
      Double.parseDouble(token);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  private boolean isOpenBracket(String token) {
    return token.equals("(");
  }

  private boolean isCloseBracket(String token) {
    return token.equals(")");
  }

  private boolean isOperator(String token) {
    return OPERATORS.contains(token);
  }

  private byte getPrecedence(String token) {
    if (token.equals("+") || token.equals("-")) {
      return 1;
    }
    return 2;
  }
}
