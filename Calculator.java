package eecs40;
import java.util.ArrayList; //import ArrayList
import java.lang.*;

public class Calculator implements eecs40.CalculatorInterface {
    private String display = "0";
    private ArrayList<String> operands = new ArrayList<>();
    private ArrayList<String> operators = new ArrayList<>();
    private ArrayList<String> allInputs = new ArrayList<>();
    public int negFlag = 0;
    public String strTemp = "";
    public boolean keep = false;


    public void acceptInput(String s) {

        if (s.equals("C")) { //clear
            this.display = "0";
            operands.clear();
            operators.clear();
            allInputs.clear();
        } //fi

        else if (allInputs.contains("NaN") && !s.equals("C")){ //user must stay in NaN until Cleared
            this.display = "NaN";
        } //fi

        else if (allInputs.contains("Error") && !s.equals("C")){
            this.display = "Error";
        }

        else if(s.equals("-") && this.display.equals("0")){ //display still remains 0 if - is inputted
            strTemp = s;
            negFlag = 1;
            this.display = "0";
        }

        else if (strTemp.equals("-") && negFlag == 1){
            this.display = strTemp + s;
            strTemp = "";
            negFlag = 0;
            allInputs.add(this.display);
        }

        else if (s.equals("=") && this.display.equals("0") && operators.isEmpty()){ //stay 0 if the original display is already 0
            this.display = "0";
        }
        else if (keep == true && s.equals(".") && this.display.equals("0")){ //Prevent .99 or .(any number) from happening
            keep = false;
            this.display = this.display + s;
        }
        else if (keep == true && (!s.equals("+")&& !s.equals("-") && !s.equals("*") && !s.equals("/") )
                && !s.equals(".")){
            operands.clear();
            this.display = s;
            allInputs.add(s);
            keep = false;
        }

        else if (allInputs.isEmpty()){
            allInputs.add(this.display);
            if (s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/")){
                if (!checkForOperator()) {
                    operands.add(this.display);
                    operators.add(s);
                    allInputs.add(this.display);
                    allInputs.add(s);
                } // fi
            }
            else{
                this.display = s;
            }
        }

        else if(this.display.equals("0") && s.equals(".")){
            this.display = this.display + s;
        }


        else if (s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/")) { //operations
            //case if user inputs two operators in a row
            if (!checkForOperator()) {
                operands.add(this.display);
                operators.add(s);
                allInputs.add(this.display);
                allInputs.add(s);
                keep = false;
            } // fi

            else { //if this is a second operator in a row, print error
                this.display = "Error";
                allInputs.add(this.display);
            } //fi
        }

        else if (this.display.equals("0") && s.equals("0")){ // user inputs 0 when 0 is already in
            this.display = "0";
        }

        else if (s.equals("=") && this.display =="0" && !operators.contains("/")){
            this.display = "0";
        }

        else if (s.equals("=")) { //equal
            keep = true;
            operands.add(this.display);
            allInputs.add(s);
            this.display = equalInput();

        } //fi

        else if (checkForDecimal(s)){ //check if user is inputting a second decimal
            this.display = this.display + "";
        }

        else if (checkForOperator() || this.display.equals("0")){ //check if operator was the last element
            this.display = s;
        }

        else if (s.equals(".")){
            this.display = this.display +s;
        }

        else{
            this.display = this.display + s; //add numbers to string until operator
        }
    }

    public String getDisplayString() {
        return this.display;
    }

    public boolean checkForOperator(){
        int index = 0;
        if(allInputs.size()>0){
            index = allInputs.size() - 1;
        }
        else {
            System.exit(1);
        }
        String lastIndex = allInputs.get(index);
        if (lastIndex.equals("+") || lastIndex.equals("-") || lastIndex.equals("*") || lastIndex.equals("/")){
            allInputs.remove(lastIndex);
            return true;
        }
        else{
            return false;
        }
    }

    public boolean checkForDecimal(String s){
        if (this.display.contains(".") && s.equals(".")){
            return true;
        }
        else{
            return false;
        }
    }


    public String equalInput() {
        String strResult = "";
        strResult = computeExpression();
        this.display = strResult;
        return this.display;
    }

    public String computeExpression() {
        double result;
        int temp;
        int operatorIndex;
        int operator1;
        int operator2;
        String operand1;
        String operand2;
        double a;
        double b;
        int counter = 0;
        String strResult = "";

       while (operators.size() >= counter) {
            //PEMDAS
            if (((operators.indexOf("*") < operators.indexOf("/")) || !operators.contains("/"))
                    && operators.contains("*")) {
                temp = operators.indexOf("*");
                operator1 = temp - 1; //get values of the left and right indicies
                operator2 = temp + 1;

                if(operator1 == -1){
                    operator1 -= operator1;
                }

                if(operators.size()>1){
                    operator1 = temp;
                }

                operand1 = operands.get(operator1);
                operand2 = operands.get(operator2);
                a = Double.parseDouble(operand1);
                b = Double.parseDouble(operand2);

                result = a * b; // compute
                strResult = Double.toString(result);
                String lastTwoDigits = strResult.substring(strResult.length()-2);
                if (lastTwoDigits.equals(".0")){
                    strResult = strResult.substring(0, strResult.length() - 2);
                }
                operatorIndex = operators.indexOf("*");

                operators.remove(operatorIndex);
                operands.remove(operator1);
                operands.remove(operand1);
                operands.remove(operand2);

                operands.add(operatorIndex, strResult); //insert the product back where operand was
                counter++;

            } //fi
            else if (((operators.indexOf("/") < operators.indexOf("*")) || !operators.contains("*"))
                    && operators.contains("/")) {

                temp = operators.indexOf("/");
                operator1 = temp - 1; //get values of the left and right indicies
                operator2 = temp + 1;

                if(operator1 == -1){
                    operator1 -= operator1;
                }

                if(operators.size()>1){
                    operator1 = temp;
                }

                operand1 = operands.get(operator1);
                operand2 = operands.get(operator2);
                a = Double.parseDouble(operand1);
                b = Double.parseDouble(operand2);

                if (b==0){ //if a is getting divided by 0
                    return strResult = "NaN";
                }

                result = a / b; // compute
                strResult = Double.toString(result);
                String lastTwoDigits = strResult.substring(strResult.length()-2);
                if (lastTwoDigits.equals(".0")){
                    strResult = strResult.substring(0, strResult.length() - 2);
                }
                operatorIndex = operators.indexOf("/");

                operators.remove(operatorIndex);
                operands.remove(operator1);
                operands.remove(operand1);
                operands.remove(operand2);

                operands.add(strResult); //insert the product back where operand was
                counter++;
            }
            else if (((operators.indexOf("+") < operators.indexOf("-")) || !operators.contains("-"))
                    && operators.contains("+")) { //check if addition is before subtraction

                temp = operators.indexOf("+");
                operator1 = temp - 1; //get values of the left and right indicies
                operator2 = temp + 1;

                if(operator1 < 0){
                    operator1 -= operator1;
                }

                operand1 = operands.get(operator1);
                operand2 = operands.get(operator2);

                a = Double.parseDouble(operand1);
                b = Double.parseDouble(operand2);

                result = a + b; // compute
                strResult = Double.toString(result);
                String lastTwoDigits = strResult.substring(strResult.length()-2);
                if (lastTwoDigits.equals(".0")){
                    strResult = strResult.substring(0, strResult.length() - 2);
                }
                operatorIndex = operators.indexOf("+");

                operators.remove(operatorIndex);
                operands.remove(operator1);
                operands.remove(operand1);
                operands.remove(operand2);

                if( a != 0 && b != 0){ //don't need to add it back in if the operand was the same as output
                    //(strResult); //insert the product back where operand was
                }
                counter++;
            } //fi
            else if (((operators.indexOf("-") < operators.indexOf("+")) || !operators.contains("+"))
                    && operators.contains("-")) {
                temp = operators.indexOf("-");
                operator1 = temp - 1; //get values of the left and right indicies
                operator2 = temp + 1;

                if(operator1 == -1){
                    operator1 -= operator1;
                }

                operand1 = operands.get(operator1);
                operand2 = operands.get(operator2);


                a = Double.parseDouble(operand1);
                b = Double.parseDouble(operand2);

                result = a - b; // compute
                strResult = Double.toString(result);
                String lastTwoDigits = strResult.substring(strResult.length()-2);
                if (lastTwoDigits.equals(".0")){
                    strResult = strResult.substring(0, strResult.length() - 2);
                }
                operatorIndex = operators.indexOf("-");

                operators.remove(operatorIndex);
                operands.remove(operator1);
                operands.remove(operand1);
                operands.remove(operand2);

                operands.add(strResult); //insert the product back where operand was
                counter++;
            } //if
        } // elihw
        return strResult;
    }
}