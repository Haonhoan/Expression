package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import java.util.StringTokenizer;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
	
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	expr.replaceAll("\\s","");
    	StringTokenizer strTkn = new StringTokenizer(expr, delims, true);
    	
		Stack<String> tokens = new Stack<String>();
    	String identifier = "";
		
		while(strTkn.hasMoreTokens()) {
			identifier = strTkn.nextToken();
			if(Character.isLetter(identifier.charAt(0))) {
				tokens.push(identifier);
			}
			if(identifier.charAt(0) == '[') {
				tokens.push(identifier);
			}
		}
		
    	identifier = "";
    	
    	while(!tokens.isEmpty()) {
    		identifier = tokens.pop();
    		
    		if(identifier.equals("[")) {
    			identifier = tokens.pop();
    			Array finA = new Array(identifier);
    			
    			if(!arrays.contains(finA)) {
    				arrays.add(finA);
    			}
    		}
    		else {
    			Variable finV = new Variable(identifier);
    			
    			if(!vars.contains(finV)) {
    				vars.add(finV);
    			}
    		}
    	}
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    private static String eval(String temp) {
    	int j = 1;
    	int q = 1;
		for(int i = 0; i < 2; i++) {
			for(int k = 0; k < temp.length(); k++) {
				if(i == 0) {
					if(temp.charAt(k) == '*') {
						String xFull = "";
						String yFull = "";
						while((k-j >= 0 && Character.isDigit(temp.charAt(k-j))) || (k-j >= 0 && temp.charAt(k-j) == '-')) {
							if((temp.charAt(k-j) == '-' && k-j == 0) ||(temp.charAt(k-j) == '-' && temp.substring(k-j-1).matches("[0-9]+"))) {
								xFull = temp.charAt(k-j) + xFull;
							}
							else if(k-j >= 0 && Character.isDigit(temp.charAt(k-j))) {
								xFull = temp.charAt(k-j) + xFull;
							}
							else {
								break;
							}
							j++;
						}
						int x = Integer.parseInt(xFull);
						while((k+q < temp.length() && Character.isDigit(temp.charAt(k+q))) || (k+q < temp.length() && temp.charAt(k+q) == '-')) {
							if(temp.charAt(k+q) == '-' && temp.substring(k+q+1).matches("[0-9]+")){
								yFull = yFull + temp.charAt(k+q);
							}
							else if(k+q < temp.length() && Character.isDigit(temp.charAt(k+q))){
								yFull = yFull + temp.charAt(k+q);
							}
							else {
								break;
							}
							q++;
						}
						int y = Integer.parseInt(yFull);
						int product = x*y;
						temp = temp.replace(temp.substring(k-j+1,k+q),String.valueOf(product));
						j = 1;
						q = 1;
						k = 0;
					}
					if(temp.charAt(k) == '/') {
						String xFull = "";
						String yFull = "";
						while((k-j >= 0 && Character.isDigit(temp.charAt(k-j))) || (k-j >= 0 && temp.charAt(k-j) == '-')) {
							if((temp.charAt(k-j) == '-' && k-j == 0) ||(temp.charAt(k-j) == '-' && temp.substring(k-j-1).matches("[0-9]+"))) {
								xFull = temp.charAt(k-j) + xFull;
							}
							else if(k-j >= 0 && Character.isDigit(temp.charAt(k-j))) {
								xFull = temp.charAt(k-j) + xFull;
							}
							else {
								break;
							}
							j++;
						}
						int x = Integer.parseInt(xFull);
						while((k+q < temp.length() && Character.isDigit(temp.charAt(k+q))) || (k+q < temp.length() && temp.charAt(k+q) == '-')) {
							if(temp.charAt(k+q) == '-' && temp.substring(k+q+1).matches("[0-9]+")){
								yFull = yFull + temp.charAt(k+q);
							}
							else if(k+q < temp.length() && Character.isDigit(temp.charAt(k+q))){
								yFull = yFull + temp.charAt(k+q);
							}
							else {
								break;
							}
							q++;
						}
						int y = Integer.parseInt(yFull);
						int quotient = x/y;
						temp = temp.replace(temp.substring(k-j+1,k+q),String.valueOf(quotient));
						j = 1;
						q = 1;
						k = 0;
					}
				}
				if(i == 1) {
					if(temp.charAt(k) == '+') {
						String xFull = "";
						String yFull = "";
						while((k-j >= 0 && Character.isDigit(temp.charAt(k-j))) || (k-j >= 0 && temp.charAt(k-j) == '-')) {
							if((temp.charAt(k-j) == '-' && k-j == 0) ||(temp.charAt(k-j) == '-' && temp.substring(k-j-1).matches("[0-9]+"))) {
								xFull = temp.charAt(k-j) + xFull;
							}
							else if(k-j >= 0 && Character.isDigit(temp.charAt(k-j))) {
								xFull = temp.charAt(k-j) + xFull;
							}
							else {
								break;
							}
							j++;
						}
						int x = Integer.parseInt(xFull);
						while((k+q < temp.length() && Character.isDigit(temp.charAt(k+q))) || (k+q < temp.length() && temp.charAt(k+q) == '-')) {
							if(temp.charAt(k+q) == '-' && temp.substring(k+q+1).matches("[0-9]+")){
								yFull = yFull + temp.charAt(k+q);
							}
							else if(k+q < temp.length() && Character.isDigit(temp.charAt(k+q))){
								yFull = yFull + temp.charAt(k+q);
							}
							else {
								break;
							}
							q++;
						}
						int y = Integer.parseInt(yFull);
						int sum = x+y;
						temp = temp.replace(temp.substring(k-j+1,k+q),String.valueOf(sum));
						j = 1;
						q = 1;
						k = 0;
					}
					if(temp.charAt(k) == '-' && temp.charAt(0) != '-') {
						String xFull = "";
						String yFull = "";
						while((k-j >= 0 && Character.isDigit(temp.charAt(k-j))) || (k-j >= 0 && temp.charAt(k-j) == '-')) {
							if((temp.charAt(k-j) == '-' && k-j == 0) ||(temp.charAt(k-j) == '-' && temp.substring(k-j-1).matches("[0-9]+"))) {
								xFull = temp.charAt(k-j) + xFull;
							}
							else if(k-j >= 0 && Character.isDigit(temp.charAt(k-j))) {
								xFull = temp.charAt(k-j) + xFull;
							}
							else {
								break;
							}
							j++;
						}
						int x = Integer.parseInt(xFull);
						while((k+q < temp.length() && Character.isDigit(temp.charAt(k+q))) || (k+q < temp.length() && temp.charAt(k+q) == '-')) {
							if(temp.charAt(k+q) == '-' && temp.substring(k+q+1).matches("[0-9]+")){
								yFull = yFull + temp.charAt(k+q);
							}
							else if(k+q < temp.length() && Character.isDigit(temp.charAt(k+q))){
								yFull = yFull + temp.charAt(k+q);
							}
							else {
								break;
							}
							q++;
						}
						int y = Integer.parseInt(yFull);
						int sum = x-y;
						temp = temp.replace(temp.substring(k-j+1,k+q),String.valueOf(sum));
						j = 1;
						q = 1;
						k = 0;
					}
				}
			}
		}
		return temp;
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	expr = expr.replaceAll("\\s",""); //removes spaces
    	int open = 0;//used to find innermost closed parenthesis and innermost open parenthesis
    	int closed = 0; //used to find innermost closed parenthesis and innermost open parenthesis
    	
    	if(expr == "" || expr == null || vars == null || arrays == null) {
    		float result = 0;
    		return result;
    	}
    	//recursion end  
    	if((!expr.contains("*") && !expr.contains("/") && !expr.contains("+") && !expr.contains("-") && expr.matches("[0-9]+")) || (expr.charAt(0) == '-' && !expr.contains("*") && !expr.contains("/") && !expr.contains("+"))) {
    		float result = 0;
    		result = Float.parseFloat(expr);
    		return result;
    	}
    	else {
    		//finds innermost closed parenthesis and innermost open parenthesis
    		for(int i = 0; i < expr.length(); i++) {
    			if(expr.charAt(i) == '(' || expr.charAt(i) == '[') {
    				open = i;
    			}
    			if(expr.charAt(i) == ')' || expr.charAt(i) == ']') {
    				closed = i;
    				break;
    			}
    		}
    		
    		String temp = "";
    		boolean hasVarArr = true;
    		
    		if(expr.length() < 3) {
    			temp = expr;
    		}
    		else if(closed != 0 && expr.substring(open,closed+1).matches(".*[a-zA-Z]+.*")){
    			temp = expr.substring(open+1,closed); //sets temp as innermost area of equation
    		}
    		else if(closed == 0 && expr.substring(open,closed+1).matches(".*[a-zA-Z]+.*")){
    			temp = expr; //sets temp as innermost area of equation
    		}
    		else if(closed != 0 && !expr.substring(open,closed+1).matches(".*[a-zA-Z]+.*")){
    			temp = expr.substring(open+1,closed);
    			hasVarArr = false;
    		}
    		else {
    			temp = expr;
    			hasVarArr = false;
    		}
    		if(expr.charAt(open) != '[' && hasVarArr == true && closed == 0) {
    			StringTokenizer strVarsTkn = new StringTokenizer(temp, delims);
    			String tempVarsToken = "";
    			Variable tempTokenVar = new Variable("");
    			
    			do{
    				tempVarsToken = strVarsTkn.nextToken();
    				if(Character.isLetter(tempVarsToken.charAt(0))) {
    					tempTokenVar = new Variable(tempVarsToken);
    			
    					Variable tempVar = vars.get(vars.indexOf(tempTokenVar));
    					int varInt = tempVar.value;
    					temp = temp.replace(tempVarsToken, String.valueOf(varInt));
    				}
    			}while(strVarsTkn.hasMoreElements());
    			
    			expr = expr.replace(expr, eval(temp));
    		}
    		else if(expr.charAt(open) != '[' && hasVarArr == true) {
    			StringTokenizer strVarsTkn = new StringTokenizer(temp, delims);
    			String tempVarsToken = "";
    			Variable tempTokenVar = new Variable("");
    			
    			do{
    				tempVarsToken = strVarsTkn.nextToken();
    				if(Character.isLetter(tempVarsToken.charAt(0))) {
    					tempTokenVar = new Variable(tempVarsToken);
    			
    					Variable tempVar = vars.get(vars.indexOf(tempTokenVar));
    					int varInt = tempVar.value;
    					temp = temp.replace(tempVarsToken, String.valueOf(varInt));
    				}
    			}while(strVarsTkn.hasMoreElements());
    			expr = expr.replace(expr.substring(open, closed+1), eval(temp));
    		}
    		else if(expr.charAt(open) != '[' && hasVarArr == false && closed != 0) {
    			expr = expr.replace(expr.substring(open,closed+1), eval(temp));
    		}
    		else if(expr.charAt(open) == '[' && hasVarArr == false) {
    			int k = open;
    			int q = 1;
    			String full = "";
    			
    			while(k-q >= 0 && Character.isLetter(expr.charAt(k-q))) {
					full = expr.charAt(k-q) + full;
					q++;
				}
				Array tempStringArr = new Array(full);
    			
    			Array tempArr = arrays.get(arrays.indexOf(tempStringArr));
    			int arrInt[] = tempArr.values;
    			expr = expr.replace(expr.substring(open-q+1, closed+1), String.valueOf(arrInt[Integer.parseInt(eval(temp))]));
    		}
    		else if(expr.charAt(open) == '[' && hasVarArr == true) {
    			StringTokenizer strVarsTkn = new StringTokenizer(temp, delims);
    			String tempVarsToken = "";
    			Variable tempTokenVar = new Variable("");
    			
    			do{
    				tempVarsToken = strVarsTkn.nextToken();
    				if(Character.isLetter(tempVarsToken.charAt(0))) {
    					tempTokenVar = new Variable(tempVarsToken);
    			
    					Variable tempVar = vars.get(vars.indexOf(tempTokenVar));
    					int varInt = tempVar.value;
    					temp = temp.replace(tempVarsToken, String.valueOf(varInt));
    				}
    			}while(strVarsTkn.hasMoreElements());
    			
    			int k = open;
    			int q = 1;
    			String full = "";
    			
    			while(k-q >= 0 && Character.isLetter(expr.charAt(k-q))) {
					full = expr.charAt(k-q) + full;
					q++;
				}
				Array tempStringArr = new Array(full);
    			
    			Array tempArr = arrays.get(arrays.indexOf(tempStringArr));
    			int arrInt[] = tempArr.values;
    			expr = expr.replace(expr.substring(open-q+1, closed+1), String.valueOf(arrInt[Integer.parseInt(eval(temp))]));
    		}
    		else {
    			expr = expr.replace(expr, eval(temp));
    		}
    		return evaluate(expr,vars,arrays);
    	}
    }
}
