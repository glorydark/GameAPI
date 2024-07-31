package gameapi.tools;

import java.util.Stack;

/**
 * StringFormulaCalculator converts infix expressions to postfix expressions,
 * thereby improving computational efficiency
 */
public class StringFormulaCalculator {

    public static Double evaluate(String expression) {
        return evaluatePostfix(infixToPostfix(expression));
    }

    private static String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> operators = new Stack<>();
        boolean wasOperator = true;  // Keeps track of whether the last token was an operator or not

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);

            // Handle numbers and decimals
            if (Character.isDigit(c) || c == '.') {
                while (i < infix.length() && (Character.isDigit(infix.charAt(i)) || infix.charAt(i) == '.')) {
                    postfix.append(infix.charAt(i++));
                }
                postfix.append(' ');
                i--;
                wasOperator = false;
            } else if (c == '(') {
                operators.push(c);
                wasOperator = true;
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    postfix.append(operators.pop()).append(' ');
                }
                operators.pop(); // Remove the '(' from the stack
                wasOperator = false;
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                // Handle the case where '-' is a negative sign
                if (c == '-' && wasOperator) {
                    postfix.append("0 ");  // Treat as unary negative sign, so we add '0'
                }
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    postfix.append(operators.pop()).append(' ');
                }
                operators.push(c);
                wasOperator = true;
            }
        }

        while (!operators.isEmpty()) {
            postfix.append(operators.pop()).append(' ');
        }

        return postfix.toString().trim();
    }

    private static int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return -1;
        }
    }

    private static double evaluatePostfix(String postfix) {
        Stack<Double> stack = new Stack<>();
        String[] tokens = postfix.split(" ");
        for (String token : tokens) {
            if (token.isEmpty()) continue;
            try {
                stack.push(Double.parseDouble(token));
            } catch (NumberFormatException e) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid postfix expression: insufficient operands.");
                }
                double right = stack.pop();
                double left = stack.pop();
                switch (token.charAt(0)) {
                    case '+':
                        stack.push(left + right);
                        break;
                    case '-':
                        stack.push(left - right);
                        break;
                    case '*':
                        stack.push(left * right);
                        break;
                    case '/':
                        if (right == 0) {
                            throw new ArithmeticException("Division by zero.");
                        }
                        stack.push(left / right);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported operator: " + token);
                }
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid postfix expression: incorrect number of operands.");
        }
        return stack.pop();
    }

    public static void main(String[] args) {
        long startMills = System.currentTimeMillis();
        System.out.println("Start Millis: " + startMills);
        String str = "-3.5*50"; // -175.0

        System.out.println(evaluate(str));
        System.out.println("Cost: " + (System.currentTimeMillis() - startMills) + "ms"); // avg: 1ms
    }
}
