package eecs40;
import java.util.*;

/*
Test Case 1: clear
Test Case 2: clear
Test Case 3: clear
Test Case 4:
Test Case 5:
Test Case 6:
Test Case 7:
Test Case 8:
Test Case 9:
Test Case 10:

 */

public class ExprCalculator implements eecs40.CalculatorInterface {
    private String expression = "";
    private String displayError = "";

    private void eval() {
        char[] tokens = expression.toCharArray();
        Stack<String> expressionStack = new Stack<String>();
        Stack<String> operators = new Stack<String>();
        ArrayList<String> postfix = new ArrayList<String>();
        Stack<String> tempPostfix = new Stack<String>();
        Stack<String> operands = new Stack<String>();
        ArrayList<Character> parenCheck = new ArrayList<>();
        int i;

        for (i = 0; i < tokens.length; i++) {
            if (checkOperator(tokens[i]) && checkOperator(tokens[i+1])) { //check for double operator
                displayError = "Error";
                return;
            }
        }
        for (i = 0; i < tokens.length; i++) {
            if (tokens[i] == '.' && tokens[i+1] == '.') { // check for double decimal
                displayError = "Error";
                return;
            }
        }
        for (i = 0; i < tokens.length; i++) {
            if (tokens[i] == '(' || tokens[i] == ')') { // add parentheses to list
                parenCheck.add(tokens[i]);
            }
        }
        if (!(parenCheck.size() % 2 == 0)) { // checking for parentheses error
            displayError = "Error:Parentheses";
            return;
        } //fi
        else {
            parenCheck.clear();
        } //esle


        for (i = 0; i<tokens.length; i++) {
            //skip spaces in the expression
            if (tokens[i] ==' '){
                continue;
            }
            //check if character is a number
            if ((tokens[i]>='0' && tokens[i]<='9') || (i == 0 && tokens[0] == '-') || (tokens[i] == '.')){
                StringBuffer buffer = new StringBuffer();

                while ((i < tokens.length && tokens[i] >= '0'  && tokens[i] <= '9') || (i == 0 && tokens[0] == '-')
                || (tokens[i] == '.')){
                    buffer.append(tokens[i++]);
                    if (i == tokens.length){
                        break;
                    }
                }
                expressionStack.push(buffer.toString());

                //revert back the incremented i since it is looking at a operator right now
                i--;
                buffer.delete(0, buffer.length());

            } //fi
            //special operations
            else if((tokens[i] == 's') || (tokens[i] == 'c') || (tokens[i] == 't')
            || tokens[i] == 'l' || tokens[i] == 'f' || tokens[i] == 'm'){
                StringBuffer buffer2 = new StringBuffer();

                while (i < tokens.length && Character.isLetter(tokens[i])){
                    buffer2.append(tokens[i++]);
                }
                expressionStack.push(buffer2.toString());
                i--;
            }
            //operators
            else {
                StringBuffer tempString = new StringBuffer();
                tempString.append(tokens[i]);
                expressionStack.push(tempString.toString());
                operators.push(tempString.toString());
            }
        } //rof

        // infix to postfix
        for (String s:expressionStack){
            //expression stack has a number
            if (checkNumeric(s)){
                postfix.add(s);
            }

            else if (s.equals("(")){
                tempPostfix.push(s);
            }
            else if (s.equals(")")){
                while (!tempPostfix.isEmpty() && !tempPostfix.peek().equals("(")){
                    postfix.add(tempPostfix.pop());
                }
                tempPostfix.pop();
            }

            //operator check
            else{
                while (!tempPostfix.isEmpty() && orderOfOps(s) <= orderOfOps(tempPostfix.peek())){
                    postfix.add(tempPostfix.pop());
                }
                tempPostfix.push(s);
            }
        } //rof

        if (tempPostfix.contains("(") || tempPostfix.contains(")")){
            tempPostfix.remove("(");
            tempPostfix.remove(")");
        }

        //take out all operators from stack
        while (!tempPostfix.isEmpty()){
            if(tempPostfix.peek().equals("(") || tempPostfix.peek().equals(")")){
                displayError = "Error: Parentheses";
                return;
            }
            postfix.add(tempPostfix.pop());
        }

        //postfix to calculating the result
        for(String s : postfix){
            //store numbers in a separate stack
            if (checkNumeric(s)){
                operands.push(s);
            }

            else if (s.equals("sin") || s.equals("cos") || s.equals("tan") || s.equals("log")
                    || s.equals("ln") || s.equals("sqrt") || s.equals("fac")){
                    double num2 = Double.parseDouble(operands.pop());
                    String resultString = String.valueOf(useSpecialOperator(s, num2));
                    String lastTwoDigits = resultString.substring(resultString.length()-2);
                    if (lastTwoDigits.equals(".0")){
                        resultString = resultString.substring(0, resultString.length() - 2);
                    }
                    operands.push(resultString);
            }

            //if anything other than number is detected
            else {
                int num2Index = 0;
                int num1Index = 0;
                double num2 = Double.parseDouble(operands.pop());
                num2Index = operands.indexOf(num2);
                double num1 = Double.parseDouble(operands.pop());
                num1Index = operands.indexOf(num1);
                   // numFlag = 1;

                if (s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/") || s.equals("^")
                        || s.equals("mod")) {

                        String resultString = String.valueOf(useOperator(s, num2, num1));
                        if(resultString.equals("null")){
                            displayError = "NaN";
                            return;
                        }

                        String lastTwoDigits = resultString.substring(resultString.length()-2);
                        if (lastTwoDigits.equals(".0")){
                            resultString = resultString.substring(0, resultString.length() - 2);
                        }

                        operands.push(resultString);
                }
            } //esle
        } //rof
        this.expression = operands.pop();

    } //end of eval

    //check if string is numeric
    public static boolean checkNumeric(String strNum){
        if (strNum == null) {
            return false;
        }
        try {
            double testNum = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe){
            return false;
        }
        return true;
    }

    public static boolean checkLetter(String strLetter){
        int len = strLetter.length();
        for (int i = 0; i <len ; i++){
            if ((Character.isLetter(strLetter.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkOperator(char c) {
        if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == 'm' || c == 'd'){
            return true;
        }
        else{
            return false;
        }
    }

    public static int orderOfOps(String ops){
        switch(ops) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
            case "sin":
            case "cos":
            case "tan":
            case "log":
            case "ln":
            case "sqrt":
            case "fac":
            case "mod":
                return 4;
        }
        return -1;
    } // end of orderOfOps

    public static Double useOperator(String operator, double b, double a){
        {
            switch(operator){
                case "+":
                    return a+b;
                case "-":
                    return a-b;
                case "*":
                    return a*b;
                case "/":
                    if (b == 0 ) {
                        return null; // NaN
                    }
                    return a/b;
                case "^":
                    return Math.pow(a,b);
                case "mod":
                    return a%b;
            }
            return 0.0;
        }
    }

    public static Double useSpecialOperator(String operator, double b){
        switch(operator){
            case "sin":
                return Math.sin(b);
            case "cos":
                return Math.cos(b);
            case "tan":
                return Math.tan(b);
            case "log":
                return Math.log10(b);
            case "ln":
                return Math.log(b);
            case "fac":
                long fact = 1;
                for (int i = 2; i <= b; i++){
                    fact = fact * i;
                }
                double c = (double) fact;
                return c;
            case "sqrt":
                return Math.sqrt(b);
        }
        return 0.0;
    }


    @Override
    public void acceptInput(String s) {
        if (s.equalsIgnoreCase("=")) {
            eval();
        } else if (s.equalsIgnoreCase("Backspace")) {
            expression = expression.substring(0, expression.length() - 1);
            displayError = "";
        } else if (s.equalsIgnoreCase("C")) {
            expression = ""; // clear!
            displayError = "";
        } else { // accumulate input String
            expression = expression + s;
            displayError = "";
        }
    }
    @Override
    public String getDisplayString() {
        if (displayError.equalsIgnoreCase("NaN") || displayError.equalsIgnoreCase("Error")
                || displayError.equalsIgnoreCase("Error:Parentheses")){
            return displayError;
        }
        else {
            return expression;
        }
    }
}