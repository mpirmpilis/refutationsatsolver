package webapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * @author Georgios Mpirmpilis (csd3296)
 * <p> This class illustrates the problem solver </p>
 * <br><br><b>Solver</b> : Takes the input typed from user in index.html
 * and returns the solution in the same window.
 */


public class Solver implements Cloneable {
	String mainSet;
	List<String> finalOutput = new ArrayList<String>();
	
	List<ArrayList<Statement>> subsets = new LinkedList<ArrayList<Statement>>();
	Map<Operator, ArrayList<Statement>> symtable = new LinkedHashMap<Operator, ArrayList<Statement>>();

	
	Statement selectedStatement = null, copy, left, right;
	int subsetNumber = 0, indexDeleted = 0, minCounter = 0;       // deleted_index is used to write the new premise at the correct place in miniset
	int iteration = 0, whichSet = 0;
	boolean delRuleUsed = false;
	

	
	
	public Solver(String input) throws IOException {
		solveProblem(input);
	}
	
	/* Empty everything so to be ready for the next problem to solve */
	public void emptyEverything() {
		mainSet = null;
		subsets.clear();
		symtable.clear();
		selectedStatement = copy = left = right = null;
		subsetNumber = indexDeleted = minCounter = iteration = 0;
		delRuleUsed = false;
	}
	
	public static void main(String[] args) throws IOException {
		/* Nothing here. Construction calls immediately solveProblem() with the input */
	}
	
	
	/* this is where the actual program starts */
	public void solveProblem(String inputProblem) throws IOException {
		Operator op = null, key = null, key2 = null;
		Statement copyLeft = null, copyRight = null;		// used for IF_ONLY_IF and NOT_IF_ONLY_IF
		boolean newSpawnSetOperator = false, result = false;
		int spareMinisetNumber = -1;  // used when there is no element so assign this number
		
		
		readInput(inputProblem);
		makeSymbolTable();
		addToMiniSets();   
	
		while (!(result = allSubsetsEmpty())) {
			if (delUsed()) {
				delRuleUsed = true;
				iteration++;
			} else {
				delRuleUsed = false;
				if ((checkForSingleLetters()) != -1)
					 break;
				selectedStatement = selecteHighestStatement();
				for (int h = 0; h < subsets.size(); h++) {
					if (subsets.get(h).contains(selectedStatement)) {
						subsetNumber = h;
						break;
					}
				}
				indexDeleted = subsets.get(subsetNumber).indexOf(selectedStatement);   // catch the position of the selected statement in miniset (this is where new statements will be written)
				op = selectedStatement.getOperator();
				iteration++;

				
				if (op == Operator.AND || op == Operator.NOT_OR || op == Operator.NOT_IF_THEN) {
					newSpawnSetOperator = false;
					if (op == Operator.NOT_OR) {
						left = new Statement(reformPart(selectedStatement.getLeftPart()));
						right = new Statement(reformPart(selectedStatement.getRightPart()));
					} else if (op == Operator.NOT_IF_THEN) {
						left  = new Statement(selectedStatement.getLeftPart());
						right = new Statement(reformPart(selectedStatement.getRightPart()));
					} else {
						left  = new Statement(selectedStatement.getLeftPart());
						right = new Statement(selectedStatement.getRightPart());
					}
					
					if (subsets.get(subsetNumber).size() == 0) {
						left.setMinisetNumber(spareMinisetNumber);
						right.setMinisetNumber(spareMinisetNumber);
						spareMinisetNumber--;
					} else {
						left.setMinisetNumber(subsets.get(subsetNumber).get(0).getMinisetNumber());
						right.setMinisetNumber(subsets.get(subsetNumber).get(0).getMinisetNumber());
					}

					subsets.get(subsetNumber).add(indexDeleted, left);
					subsets.get(subsetNumber).add(indexDeleted + 1, right);
					subsets.get(subsetNumber).remove(selectedStatement);
				} else if (op == Operator.OR || op == Operator.IF_THEN || op == Operator.IF_ONLY_IF || op == Operator.NOT_AND || op == Operator.NOT_IF_ONLY_IF) {
					newSpawnSetOperator = true;
					if (op == Operator.IF_THEN) {
						left = new Statement(reformPart(selectedStatement.getLeftPart()));
						right = new Statement(selectedStatement.getRightPart());
					} else if (op == Operator.IF_ONLY_IF || op == Operator.NOT_IF_ONLY_IF) {
						left  = new Statement(selectedStatement.getLeftPart());
						right = new Statement(selectedStatement.getRightPart());
						
						copyLeft = new Statement(reformPart(selectedStatement.getLeftPart()));
						copyRight = new Statement(reformPart(selectedStatement.getRightPart()));
					} else if (op == Operator.NOT_AND) {
						left = new Statement(reformPart(selectedStatement.getLeftPart()));
						right = new Statement(reformPart(selectedStatement.getRightPart()));
					} else {
						left  = new Statement(selectedStatement.getLeftPart());
						right = new Statement(selectedStatement.getRightPart());
					}
					

					subsets.get(subsetNumber).remove(selectedStatement);
					subsets.add(new ArrayList<Statement>());
					for(Statement s : subsets.get(subsetNumber)) {
						subsets.get(subsets.size()-1).add((Statement) s.clone());
					}

					if (op == Operator.IF_ONLY_IF) {
						if (subsets.get(subsetNumber).size() == 0) {	// cannot retrieve minisetNumber from first element (it's empty). Assign spareMinisetNumber instead
							left.setMinisetNumber(spareMinisetNumber);
							right.setMinisetNumber(spareMinisetNumber);
							spareMinisetNumber--;
						} else {
							left.setMinisetNumber(subsets.get(subsetNumber).get(0).getMinisetNumber());
							right.setMinisetNumber(subsets.get(subsetNumber).get(0).getMinisetNumber());
						}
						
						copyLeft.setMinisetNumber(minCounter);
						copyRight.setMinisetNumber(minCounter);
						subsets.get(subsetNumber).add(indexDeleted,left);
						subsets.get(subsetNumber).add(indexDeleted + 1,right);
						subsets.get(subsets.size() - 1).add(indexDeleted,copyLeft);
						subsets.get(subsets.size() - 1).add(indexDeleted + 1,copyRight);
					} else if (op == Operator.NOT_IF_ONLY_IF) {
						if (subsets.get(subsetNumber).size() == 0) {
							left.setMinisetNumber(spareMinisetNumber);
							copyRight.setMinisetNumber(spareMinisetNumber);
							spareMinisetNumber--;
						} else {
							left.setMinisetNumber(subsets.get(subsetNumber).get(0).getMinisetNumber());
							copyRight.setMinisetNumber(subsets.get(subsetNumber).get(0).getMinisetNumber());
						}
						
						right.setMinisetNumber(minCounter);
						copyLeft.setMinisetNumber(minCounter);
						
						subsets.get(subsetNumber).add(indexDeleted,left);
						subsets.get(subsetNumber).add(indexDeleted + 1,copyRight);
						subsets.get(subsets.size()-1).add(indexDeleted,copyLeft);
						subsets.get(subsets.size()-1).add(indexDeleted + 1,right);
					} else {
						if (subsets.get(subsetNumber).size() == 0) {
							left.setMinisetNumber(spareMinisetNumber);
							spareMinisetNumber--;
						} else {
							left.setMinisetNumber(subsets.get(subsetNumber).get(0).getMinisetNumber());
						}
						
						right.setMinisetNumber(minCounter);
						subsets.get(subsetNumber).add(indexDeleted,left);
						subsets.get(subsets.size()-1).add(indexDeleted,right);
					}
					for (Statement s : subsets.get(subsets.size()-1)) {
						s.setMinisetNumber(minCounter);
					}
					
					minCounter++;
					whichSet = left.getMinisetNumber();
				} else if (op == Operator.DOUBLE_NOT) {							
					if (selectedStatement.getPremise().charAt(2) == '(') {
						left = new Statement(selectedStatement.getPremise().substring(3, selectedStatement.getPremise().length()-1));
					} else {
						left = new Statement(selectedStatement.getPremise().substring(2));
					}
					left.setMinisetNumber(subsets.get(subsetNumber).get(0).getMinisetNumber());
					subsets.get(subsetNumber).add(indexDeleted, left);
					subsets.get(subsetNumber).remove(selectedStatement);
				}
				
				
				
				
		/* ADD statements to SymTable */
				if (op == Operator.OR || op == Operator.IF_THEN || op == Operator.NOT_AND || op == Operator.IF_ONLY_IF || op == Operator.NOT_IF_ONLY_IF) {
					if (op == Operator.OR || op == Operator.IF_THEN || op == Operator.NOT_AND) {
						key = left.getOperator();
						if (symtable.get(key) == null)   // check if rule in symtable has an active arraylist with premises
							symtable.put(key, new ArrayList<Statement>());
						symtable.get(key).add(left);
					} else if (op == Operator.IF_ONLY_IF) {
						key = left.getOperator();
						if (symtable.get(key) == null)   // check if rule in symtable has an active arraylist with premises
							symtable.put(key, new ArrayList<Statement>());
						symtable.get(key).add(left);
						
						key = right.getOperator();
						if (symtable.get(key) == null)   // check if rule in symtable has an active arraylist with premises
							symtable.put(key, new ArrayList<Statement>());
						symtable.get(key).add(right);
					} else {
						key = left.getOperator();
						if (symtable.get(key) == null)   // check if rule in symtable has an active arraylist with premises
							symtable.put(key, new ArrayList<Statement>());
						symtable.get(key).add(left);
						
						key = copyRight.getOperator();
						if (symtable.get(key) == null)   // check if rule in symtable has an active arraylist with premises
							symtable.put(key, new ArrayList<Statement>());
						symtable.get(key).add(copyRight);
					}

					
					// add all premises to symtable from new miniset (must register them also)
					for (Statement s : subsets.get(subsets.size()-1)) {
						key = s.getOperator();
						if (symtable.get(key) == null)   // check if rule in symtable has an active arraylist with premises
							symtable.put(key, new ArrayList<Statement>());
						
						symtable.get(key).add(s);
					}	
				} else {
					key = left.getOperator();
					if (symtable.get(key) == null)   // check if rule in symtable has an active arraylist with premises
						symtable.put(key, new ArrayList<Statement>());
					symtable.get(key).add(left);
					if (op != Operator.DOUBLE_NOT && op != Operator.NO_OPERATOR) {							
						key2 = right.getOperator();
						if (symtable.get(key2) == null)   // check if rule in symtable has an active arraylist with premises
							symtable.put(key2, new ArrayList<Statement>());
						symtable.get(key2).add(right);
					}
				}
			}
			

			// update the output
			// Move the new spawn set to next of the original 
			if (newSpawnSetOperator && subsets.size() > 1 && subsetNumber < subsets.size()-1) {
				ArrayList<Statement> removed = subsets.remove(subsets.size()-1);
				subsets.add(subsetNumber + 1, removed);
			}

		
			String temp = "C" + "<sub>"+Integer.toString(iteration)+"</sub>" + " = {{";
			if (!subsets.isEmpty()) {
				for (ArrayList<Statement> set : subsets) {
					for (Statement s : set) {
						temp += s.getPremise() + ",";
					}
					temp = temp.substring(0, temp.length()-1) + "},{";
				}
			} else {
				temp += "}";
			}
			
			temp = temp.substring(0,temp.length()-2) + "}";
			
			if (!delRuleUsed) {		// if operator is not del, color the selected operator
				String regex = regexCreate(encodeSymbols(selectedStatement.getPremise()));
				
				String coloured;
				if (finalOutput.get(finalOutput.size()-1).indexOf(regex) == -1) {
					regex = regex.substring(0, regex.length()-1);
					coloured = finalOutput.get(finalOutput.size()-1).replaceFirst(regex,  "<span style=\"color: #ff0000\">"+ encodeSymbols(selectedStatement.getPremise()) +"</span>" + "");
				} else {
					coloured = finalOutput.get(finalOutput.size()-1).replaceFirst(regex,  "<span style=\"color: #ff0000\">"+ encodeSymbols(selectedStatement.getPremise()) +"</span>" + ",");
				}

				finalOutput.set(finalOutput.size()-1, coloured);
			} else {	// if it is del, color the whole set that's going to be deleted (that is, miniset_num)			
				int start, indexOfDeletedSet, countedMiniSets = 0;

				start = indexOfDeletedSet = finalOutput.get(finalOutput.size()-1).indexOf('{')+1;
				String lastString = finalOutput.get(finalOutput.size()-1);
				int i;
				for (i = start; i < lastString.length(); i++) {
					if (countedMiniSets == subsetNumber+1)
						break;
					
					if (lastString.charAt(i) == '{') {
						countedMiniSets++;
						indexOfDeletedSet = i;
					}
				}
				
				while (lastString.charAt(i) != '}') {
					i++;
				}
				String rest = lastString.substring(i+1);
				String highlighted = lastString.substring(0,indexOfDeletedSet) + "<span style=\"color: #00000;background-color:Tomato;\">" + "{"+lastString.substring(indexOfDeletedSet+1,i+1) + "</span>" + rest;
				finalOutput.set(finalOutput.size()-1, highlighted);
			}
			
			
			
			temp = encodeSymbols(temp);			
			if (iteration != 0) {
				if (delRuleUsed) {
					temp += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[del on set " + (subsetNumber+1) +"]<br>";
				} else {
					switch(op) {
					case AND:
						temp += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[&#8743;]<br>";
						break;
					case DOUBLE_NOT:
						temp += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[&#172;&#172;]<br>";
						break;
					case IF_ONLY_IF:
						temp += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[&#8596;]<br>";
						break;
					case IF_THEN:
						temp += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[&#8594;]<br>";
						break;
					case NOT_AND:
						temp += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[&#172;&#8743;]<br>";
						break;
					case NOT_IF_ONLY_IF:
						temp += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[&#172;&#8596;]<br>";
						break;
					case NOT_IF_THEN:
						temp += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[&#172;&#8594;]<br>";
						break;
					case NOT_OR:
						temp += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[&#172;&#8744;]<br>";
						break;
					case OR:
						temp += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[&#8744;]<br>";
						break;
					default:
						break;
					}
				}
			}
			finalOutput.add(temp);
		}
		
		if (result) {
			finalOutput.add("<br><br> S is empty which makes it <b>unsatisfiable</b>. Thus, the original Input is <b>satisfiable.</b>");
		}
		
		subsets.clear();
		iteration = 0;
		for (Entry<Operator, ArrayList<Statement>> entry : symtable.entrySet()) {
			if (entry.getValue() != null)
				entry.getValue().clear();
		}	
	}

	
	
	
	// Creates a regex to escape parentheses for coloring in HTML
	private String regexCreate(String premise) {
		StringBuilder regex = new StringBuilder();
		
		for (int i = 0; i < premise.length(); i++) {
			if (premise.charAt(i) == '(') {
				regex.append("\\(");
			} else if (premise.charAt(i) == ')') {
				regex.append("\\)");
			} else {
				regex.append(premise.charAt(i));
			}
		}
		regex.append(',');
		return regex.toString();
	}

	
	
	/* Encodes logic symbols for HTML printout */
	private String encodeSymbols(String output) {
		output = output.replace("&", "&and;");
		output = output.replace("<->", "&LeftRightArrow;");
		output = output.replace("->", "&#8594;");
		output = output.replace("|", "&or;");
		output = output.replace("~", "&not;");
		
		return output;
	}
	
	
	// for html solution display
	public String getResult() {
		return String.join("<br>", finalOutput);
	}
	
	/* Checks if a subset has only letters (NO_OPERATOR)
	 * Returns the number of subset in subsets that contains only letters and cannot perform other rule
	 * Returns -1 if every subset has at least one operator different from NO_OPERATOR
	 */
	private int checkForSingleLetters() {
		int lettersOnlyMiniset = -1;
		
		for (int i = 0; i < subsets.size(); i++) {
			for (Statement s : subsets.get(i)) {
				if (s.getOperator() == Operator.NO_OPERATOR) {			
					lettersOnlyMiniset = i;
				} else {
					lettersOnlyMiniset = -1;
					break;
				}
			}
			if (lettersOnlyMiniset != -1) {
				String lettersSet = "<br><br> S is <b>satisfiable</b> and can be satisfied by <b>{";
				for (Statement s : subsets.get(i)) {
					lettersSet += s.getPremise() + ",";
				}
				lettersSet = lettersSet.substring(0,lettersSet.length()-1) + "}</b>. Thus, original Ιnput is <b>unsatisfiable</b>.";
				finalOutput.add(encodeSymbols(lettersSet));
				return lettersOnlyMiniset;
			}
		}
		return -1;
	}
	
	
	



	/* Checks if a key (from high to low) has an available premise to use */
	private Statement selecteHighestStatement() {		
		Statement s = null;
		
		for (Entry<Operator, ArrayList<Statement>> entry : symtable.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
            	s = entry.getValue().get(0);	// retrieve and then delete it
            	entry.getValue().remove(0);
            	break;
            }
		}
		
		return s;
	}

	private void makeSymbolTable() {
		symtable.put(Operator.DOUBLE_NOT,   	null);		/* 1 */
		symtable.put(Operator.AND, 	 			null);		/* 2 */
		symtable.put(Operator.NOT_OR, 	 		null);		/* 3 */
		symtable.put(Operator.NOT_IF_THEN,  	null);		/* 4 */
		symtable.put(Operator.OR, 	 			null);		/* 5 */
		symtable.put(Operator.IF_THEN, 	 		null);		/* 6 */
		symtable.put(Operator.NOT_AND, 	 		null);		/* 7 */
		symtable.put(Operator.IF_ONLY_IF,  		null);		/* 8 */
		symtable.put(Operator.NOT_IF_ONLY_IF, 	null);		/* 9 */
		symtable.put(Operator.NO_OPERATOR,		null);		/* 10 */
		/* [del] is omitted due to instant use of it's rule */
	}


	/* Checks if whole input obeys the input rules (no random symbols) and initializes minisets arraylist */
	private void addToMiniSets() {
		// this is the original set (without rules used so far)
		String[] seperatedPremises = mainSet.split(",");
		Operator key = null;
		
		
		int len = seperatedPremises.length;
		seperatedPremises[0] = seperatedPremises[0].substring(1);   // trim first curly bracket {
		seperatedPremises[len-1] = seperatedPremises[len-1].substring(0, seperatedPremises[len-1].length()-1); // trim last }
		
		subsets.add(new ArrayList<Statement>());  // initialize miniset by adding the Statements of main_set
		int i = 0;
		for (String s : seperatedPremises) {
			subsets.get(0).add(new Statement(s));
			subsets.get(0).get(i).setMinisetNumber(0);
			
			key = subsets.get(0).get(i).getOperator();
			
			if (symtable.get(key) == null) {    // check if rule in symtable has an active arraylist with premises
				symtable.put(key, new ArrayList<Statement>());
			}
			symtable.get(key).add(subsets.get(0).get(i));
			i++;
		}
		minCounter++;
	}

	
	
	
	
	
	

	/* ---------- 1. Read the input ---------- */
	
	public void readInput(String input) {
		input = input.replaceAll("\\s+","");
		
		if (input.charAt(0) == '{' && input.charAt(input.length()-1) == '}') { // directly given as {....}
			finalOutput.add("Input = " + input + "<br><br><br><br>");
			mainSet = input;
		}
		else if (input.contains("/")) {	// directly given as {....}/
			finalOutput.add("Input = " + input + "<br><br><br><br>");
			reformClause(input, input.substring(input.lastIndexOf('/')+1));
		}
		
		finalOutput.add(encodeSymbols("S = " + mainSet + "<br><br>"));
		finalOutput.add(encodeSymbols("C<sub>0</sub> = {" + mainSet + "}<br>"));
	}
	
	


	
	
	/**
	 * Determines if 'not' operator should be placed around whole clause with a set or parenthesis or just stick to the other 'not' already being there
	 * If 'not' encloses whole premise or part of it. Example below: 
	 * ~(P&Q)->(G&S)  _____ ~(~(P&Q)->(G&S)) [NOT_IF_THEN]			~(P<->~(D&E))  _____ ~~(P<->~(D&E)) [DOUBLE_NOT]
	 */
	private void reformClause(String line, String catchClause) {		
		
		String clauseCopy = catchClause;
		int i = 0, openPar = 0;
		boolean fullNegation = false;   // full_negation is when negation encloses whole premise and not part of it
		
		
		
		if (clauseCopy.length() <= 2) {		// Letter or Negative letter  ===>  P and ~Q become ~P and ~~Q respectively
			mainSet = line.substring(0,line.lastIndexOf('}')) + ",~" + catchClause + "}";
			return;
		}

		
		if (clauseCopy.charAt(0) == '~') {
			fullNegation = true;       // true if negation catches whole premise and false if it's part of it
			
			for (i = 1; i < clauseCopy.length(); i++) {
				if (clauseCopy.charAt(i) == '(') {
					openPar++;
				} else if (clauseCopy.charAt(i) == ')') {
					openPar--;
				} else if (openPar == 0 && clauseCopy.charAt(i) != '(' && clauseCopy.charAt(i) != ')') {   // negation is part of letter __ ~P
					fullNegation = false;
					break;
				}
					
				
				if (openPar == 0 && i != clauseCopy.length()-1) {   // negation is part of smaller parenthesis and not at whole premise
					fullNegation = false;
					break;
				} else
					fullNegation = true;
			}
		}			
		String prem = (line.charAt(0) == '/') ? "{" : line.substring(0,line.lastIndexOf('}')) + ",";
		
		
		if (fullNegation) {
			mainSet = prem + "~" + catchClause + "}";
		} else
			mainSet = prem + "~(" + catchClause + ")}";
	}
	
	
	
	
	
	
	/*
	 * Returns true if all minisets are empty or false if at least one has statements in it
	 */
	private boolean allSubsetsEmpty() {
		if (subsets.isEmpty())
			return true;
		for (ArrayList<Statement> miniset : subsets) {
			if (!miniset.isEmpty())
				return false;
		}
		return true;
	}
	
	
	
	
	
	/*
	 * Searches for set with NO_OPERATOR statements and checks if [del] can be used.
	 * Returns TRUE if it finds two letters that can be deleted, it deletes them (updates current miniset) and returns true and continues to next iteration
	 * Returns FALSE if there are is at least one operator different from NO_OPERATOR or if delete cannot be performed
	 */
	private boolean delUsed() {
		int h;
		String currentLetter, target;
		if (symtable.get(Operator.NO_OPERATOR) != null) {
			for (Statement s : symtable.get(Operator.NO_OPERATOR)) {
				currentLetter = s.getPremise();	// current letter
				target = (currentLetter.charAt(0) == '~') ? currentLetter.substring(1) : ("~" + currentLetter);	// if negative, search the positve and vice-versa
				
				// DELETE EVERYTHING THAT REFERS TO THE DELETED SET (SET AND SYMTABLE STATEMENTS)
				for (Statement s2 : symtable.get(Operator.NO_OPERATOR)) {
					if (s2.getPremise().equals(target) && s.getMinisetNumber() == s2.getMinisetNumber()) {
						for (h = 0; h < subsets.size(); h++) {
							if (subsets.get(h).contains(s2) && subsets.get(h).get(0).getMinisetNumber() == s.getMinisetNumber()) {
								subsetNumber = h;	// catch what is the set to be deleted
								break;
							}
						}
						int whatToDel = subsets.get(h).get(0).getMinisetNumber();
						subsets.remove(h);
						
						
						Iterator<Statement> i;
						for (Entry<Operator, ArrayList<Statement>> entry : symtable.entrySet()) {
				            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
				            	i = symtable.get(entry.getKey()).iterator();
				            	Statement currentStatement;
				            	while (i.hasNext()) {
				            		currentStatement = (Statement) i.next();
				            		if (currentStatement.getMinisetNumber() == whatToDel)
				            			i.remove();
				            	}
				            }
						}						
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	
	private String reformPart(String s) {
		int openPar = 0, i;
		boolean foundPar = false;	// determines if not should be placed around set of parenthesis
		String reformedInput;
		
		if (s.length() < 3)
			return ("~" + s);
		
		for (i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '(') {
				foundPar = true;
				openPar++;
			}
			else if (s.charAt(i) == ')')
				openPar--;
			
			if (foundPar && openPar == 0)
				break;
		}
		
		if (foundPar) {
			if (i != s.length()-1) {
				reformedInput= ("~(" + s + ")");
			} else {
				if (s.charAt(0) == '~')
					reformedInput= ("~" + s);
				else
					reformedInput= ("~(" + s + ")");
			}
		} else
			reformedInput= ("~(" + s + ")");
		
		return reformedInput;
	}
}
