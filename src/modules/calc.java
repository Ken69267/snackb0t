/**
 * Copyright (C) 2009-2010 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package modules;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

enum Operator {
    ADD, SUBTRACT, MULTIPLY, DIVIDE, POWER
}

enum Func {
    SQRT
}

enum Type {
    NUMBER, OPERATOR, FUNC
}

class CalcParseException extends Exception {
    public CalcParseException() {
        super();
    }
}

class CalcRecallException extends Exception {
    public CalcRecallException() {
        super();
    }
}

/*
 * This really didn't turn out how I had planned. Perhaps it will be more useful
 * later on.
 */
class CalcObject {
    BigDecimal n;
    Operator o;
    Func f;
    Type type;

    public CalcObject(Type type, String num) {
        this.type = type;
        n = new BigDecimal(num);
    }

    public CalcObject(Type type, Operator op) {
        this.type = type;
        o = op;
    }

    public CalcObject(Type type, Func f) {
        this.type = type;
        this.f = f;
    }
}

public class calc {
    private BigDecimal result = BigDecimal.ZERO;
    private List<CalcObject> stack = new ArrayList<CalcObject>();
    private static MathContext PREC = MathContext.DECIMAL128;
    private String input;
    private String user;
    private Map<String, String> prev = new HashMap<String, String>();

    public String getCalculation(String msg, String user) {
        this.user = user;
        stack.clear();
        input = msg;
        result = BigDecimal.ZERO;
        // strip !calc and spaces/commas
        input = input.substring(5).trim().replaceAll(" ", "")
            .replaceAll(",", "");

        if (input.length() == 0) {
            return "Invalid Input";
        }

        try {
            parse(input, stack);
            return calculate(stack);
        } catch (CalcRecallException e) {
            return "Cannot recall previous answer";
        } catch (CalcParseException e) {
            return "Invalid Input";
        }
    }

    /**
     * Parse the input
     */
    private void parse(String input, List<CalcObject> stack)
        throws CalcRecallException, CalcParseException {
        /*
         * Check to see if the user wants to recall a previous answer to use in
         * his query.
         */
        if (input.matches(".*@.*")) {
            if (prev.containsKey(user)) {
                input = input.replaceAll("@", prev.get(user));
            } else {
                throw new CalcRecallException();
            }
        }

        int pos = 0;
        int size = input.length();
        Type last = null;

        while (pos < size) {
            char c = input.charAt(pos);
            if (last == Type.FUNC) {
                if (c == '(' || Character.isDigit(c)) {
                    // pass
                } else {
                    throw new CalcParseException();
                }
            }

            if ((Character.isDigit(c) || (c == '.'))) {
                // We have the beginning of a number. We need to retrieve the
                // whole number. It could include a decimal.
                int end = pos;
                int i = 0;
                for (i = pos; i < size; i++) {
                    char tmp = input.charAt(i);
                    if (tmp == '.' || Character.isDigit(tmp)) {
                        continue;
                    } else {
                        // end of the number
                        break;
                    }
                }
                end = i;
                String tnum = input.substring(pos, end);
                stack.add(new CalcObject(Type.NUMBER, tnum));
                pos = end;
                last = Type.NUMBER;
                continue;
            } else if (Character.isLetter(c)) {
                // Lets check if its an operator keyword like sqrt
                int end = pos;
                int i = 0;
                for (i = pos; i < size; i++) {
                    char tmp = input.charAt(i);
                    if (Character.isLetter(tmp)) {
                        continue;
                    } else {
                        // end of the number
                        break;
                    }
                }
                end = i;
                String word = input.substring(pos, end).toLowerCase();

                Func f = null;

                if (word.equals("sqrt")) {
                    f = Func.SQRT;
                } else {
                    throw new CalcParseException();
                }

                stack.add(new CalcObject(Type.FUNC, f));
                pos = end;
                last = Type.FUNC;
                continue;
            } else if (c == '+' || c == '-' || c == '/' || c == '*' || c == '^') {
                // we have an operator
                Operator op = null;

                // If this is the first thing it better be a negative or it
                // won't make sense!
                if ((pos == 0) && !(c == '-')) {
                    throw new CalcParseException();
                }

                // make sure we didn't just check an operator previously!!
                if (last == Type.OPERATOR || last == Type.FUNC) {
                    throw new CalcParseException();
                }

                if (c == '+') {
                    op = Operator.ADD;
                } else if (c == '-') {
                    op = Operator.SUBTRACT;
                    // negative number, so lets just push a zero in front of it.
                    // FIFO
                    if (pos == 0) {
                        stack.add(new CalcObject(Type.NUMBER, "0"));
                    }
                } else if (c == '/') {
                    op = Operator.DIVIDE;
                } else if (c == '*') {
                    op = Operator.MULTIPLY;
                } else if (c == '^') {
                    op = Operator.POWER;
                }

                stack.add(new CalcObject(Type.OPERATOR, op));
                last = Type.OPERATOR;
                pos++;
                continue;
            } else if (c == '(') {
                if (last == Type.NUMBER) {
                    // or we could enable multiplication right here
                    // and mind FUNC
                    throw new CalcParseException();
                }
                // were only gonna do single level for sanity
                int end = ++pos;
                for (int i = pos; i < size; i++) {
                    if (input.charAt(i) == ')') {
                        end = i;
                        break;
                    }
                    if (input.charAt(i) == '(') {
                        // google
                        throw new CalcParseException();
                    }
                }
                // assuming we have the end
                String paren = input.substring(pos, end);
                List<CalcObject> substack = new ArrayList<CalcObject>();
                parse(paren, substack);
                stack.add(new CalcObject(Type.NUMBER, calculate(substack)));
                last = Type.NUMBER;
                pos = end;
                pos++;
                continue;
            } else {
                throw new CalcParseException();
            }
        }
        if ((pos == size) && (last == Type.OPERATOR || last == Type.FUNC)) {
            throw new CalcParseException();
        }
    }

    private String calculate(List<CalcObject> stack) throws CalcParseException {
        // Pretend Queue time with a list.

        Iterator<CalcObject> iter = stack.listIterator();

        while (iter.hasNext()) {
            CalcObject cur = iter.next();
            CalcObject n = null;

            switch (cur.type) {
                case FUNC:
                    // the beginning
                    result = getFUNCResult(cur, iter).n;
                    break;
                case NUMBER:
                    // should only happen at the beginning
                    result = cur.n;
                    break;
                case OPERATOR:
                    n = iter.next(); // should be a number
                    if (n.type == Type.FUNC) {
                        n = getFUNCResult(n, iter);
                    }
                    switch (cur.o) {
                        case ADD:
                            result = result.add(n.n, PREC);
                            break;
                        case SUBTRACT:
                            result = result.subtract(n.n, PREC);
                            break;
                        case DIVIDE:
                            if (n.n.compareTo(BigDecimal.ZERO) == 0) {
                                return "Mortals shall not divide by zero";
                            }
                            result = result.divide(n.n, PREC);
                            break;
                        case MULTIPLY:
                            result = result.multiply(n.n, PREC);
                            break;
                        case POWER:
                            try {
                                result = result.pow(n.n.intValue(), PREC);
                            } catch (ArithmeticException e) {
                                return "ITS OVER 9000 ... okay maybe larger.";
                            }
                            break;
                    }
                    break;
            }
        }
        // System.out.println(result.toPlainString());
        prev.put(user, result.toPlainString());
        return result.toEngineeringString();
    }

    // Handle Functions
    private CalcObject getFUNCResult(CalcObject o, Iterator<CalcObject> iter)
        throws CalcParseException {
        CalcObject newc = null;

        switch (o.f) {
            case SQRT:
                newc = new CalcObject(Type.NUMBER, getNRSQRT(iter.next())
                        .toPlainString());
                break;
            default:
                throw new CalcParseException();
        }
        return newc;
    }

    /*
     * newton-raphson
     */
    private BigDecimal getNRSQRT(CalcObject o) {
        double x = o.n.doubleValue();
        double guess = 1.0;
        double lastguess = -1.0;
        double diff = 1;
        double epsilon = .0000000001;

        while (Math.abs(diff) > epsilon) {
            diff = Math.pow(guess, 2) - x;
            guess = guess - diff / (2.0 * guess);
            if (guess == lastguess) {
                /*
                 * Slide the scale smaller for precision loss if we can't figure
                 * it out
                 */
                epsilon = epsilon * 10;
                // System.out.println("Precision lost: " + epsilon);
                // System.out.println(guess + " vs " + diff);
            }
            lastguess = guess;
        }
        o.n = new BigDecimal(guess);
        return o.n;
    }

    public static void main(String[] arg) {
        calc c = new calc();
        System.out.println(c.getCalculation("!calc 1.2x1.3", "ken"));
    }
}
