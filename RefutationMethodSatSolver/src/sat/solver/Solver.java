package sat.solver;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import javax.swing.JOptionPane;





/**
 * @author Georgios Mpirmpilis
 * <p> This class illustrates the problem solver </p>
 * <br><br><b>Solver</b> : Takes the input typed from user in index.html
 * and returns the solution in the same window.
 */



public class Solver {
	static View view = null;
	String mainSet = "", output = "";
	List<ArrayList<Statement>> subsets = new LinkedList<>();
	Map<Operator, ArrayList<Statement>> symtable = new LinkedHashMap<>();
	
	Statement selectedStatement = null, copy = null, left = null, right = null;
	int subsetNumber = 0, indexDeleted = 0, minCounter = 0;       // indexDeleted is used to write the new premise at the correct place in subset
	int iteration = 0, whichSet = 0;
	boolean delRuleUsed = false;

	
	
	
	public static void main(String[] args) throws IOException {
		view = new View();
	}
	
	
	
	
	

	/* Empty everything so to be ready for the next problem to solve */
	public void emptyEverything() {
		mainSet = output = null;
		subsets.clear();
		symtable.clear();
		selectedStatement = copy = left = right = null;
		subsetNumber = indexDeleted = minCounter = iteration = 0;
		delRuleUsed = false;
	}
	
	
	/* this is where the actual program starts */
	public void solveProblem(String file_path) throws IOException {
		Operator op = null, key, key2;
		Statement copyLeft = null, copyRight = null;		// used for IF_ONLY_IF and NOT_IF_ONLY_IF
		boolean result, newSpawnSetOperator = false;
		int number = 0, spareMinisetNumber = -1;  // used when there is no element so assign this number
		
		readFile(file_path);
		makeSymbolTable();
		addToMiniSets();   
	
		while (!(result = allSubsetsEmpty())) {			
			if (delUsed()) {
				delRuleUsed = true;
				iteration++;
			} else {
				delRuleUsed = false;
				if ((number = checkForSingleLetters()) != -1)
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
					subsets.add(new ArrayList<>());
					for(Statement s : subsets.get(subsetNumber)) {
						subsets.get(subsets.size() - 1).add((Statement) s.clone());
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
						subsets.get(subsets.size() - 1).add(indexDeleted,copyLeft);
						subsets.get(subsets.size() - 1).add(indexDeleted + 1,right);
					} else {
						if (subsets.get(subsetNumber).size() == 0) {
							left.setMinisetNumber(spareMinisetNumber);
							spareMinisetNumber--;
						} else {
							left.setMinisetNumber(subsets.get(subsetNumber).get(0).getMinisetNumber());
						}
						
						right.setMinisetNumber(minCounter);
						subsets.get(subsetNumber).add(indexDeleted,left);
						subsets.get(subsets.size() - 1).add(indexDeleted,right);
					}
					for (Statement s : subsets.get(subsets.size() - 1)) {
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
						// check if rule in symtable has an active arraylist with premises
						symtable.computeIfAbsent(key, k -> new ArrayList<>());
						symtable.get(key).add(left);
					} else if (op == Operator.IF_ONLY_IF) {
						key = left.getOperator();
						// check if rule in symtable has an active arraylist with premises
						symtable.computeIfAbsent(key, k -> new ArrayList<>());
						symtable.get(key).add(left);
						
						key = right.getOperator();
						// check if rule in symtable has an active arraylist with premises
						symtable.computeIfAbsent(key, k -> new ArrayList<>());
						symtable.get(key).add(right);
					} else {
						key = left.getOperator();
						// check if rule in symtable has an active arraylist with premises
						symtable.computeIfAbsent(key, k -> new ArrayList<>());
						symtable.get(key).add(left);
						
						key = copyRight.getOperator();
						// check if rule in symtable has an active arraylist with premises
						symtable.computeIfAbsent(key, k -> new ArrayList<>());
						symtable.get(key).add(copyRight);
					}

					
					// add all premises to symtable from new miniset (must register them also)
					for (Statement s : subsets.get(subsets.size() - 1)) {
						key = s.getOperator();
						// check if rule in symtable has an active arraylist with premises
						symtable.computeIfAbsent(key, k -> new ArrayList<>());
						
						symtable.get(key).add(s);
					}	
				} else {
					key = left.getOperator();
					// check if rule in symtable has an active arraylist with premises
					symtable.computeIfAbsent(key, k -> new ArrayList<>());
					symtable.get(key).add(left);
					if (op != Operator.DOUBLE_NOT && op != Operator.NO_OPERATOR) {							
						key2 = right.getOperator();
						// check if rule in symtable has an active arraylist with premises
						symtable.computeIfAbsent(key2, k -> new ArrayList<>());
						symtable.get(key2).add(right);
					}
				}
			}
			
			// update the output
			output = output + "C" + iteration + " = {{";
			if (newSpawnSetOperator && subsets.size() > 1 && subsetNumber < subsets.size() - 1) {
				ArrayList<Statement> removed = subsets.remove(subsets.size() - 1);
				subsets.add(subsetNumber + 1, removed);
			}
		
			for (ArrayList<Statement> set : subsets) {
				for (Statement s : set) {
					output += s.getPremise() + ",";
				}
				output = output.substring(0, output.length() - 1) + "},{";
			}
			if (newSpawnSetOperator && subsets.size() > 1 && subsetNumber < subsets.size() -1)
				Collections.swap(subsets, subsetNumber + 1, subsets.size() - 1);
			
			
			output = output.substring(0,output.length() - 2) + "}";
			if (iteration != 0) {
				if (delRuleUsed) {
					output += "\t\t[del]\n";
				} else {
					switch (Objects.requireNonNull(op)) {
						case AND -> output += "\t\t[\u2227]\n";
						case DOUBLE_NOT -> output += "\t\t[\u00AC\u00AC]\n";
						case IF_ONLY_IF -> output += "\t\t[\u2194]\n";
						case IF_THEN -> output += "\t\t[\u2192]\n";
						case NOT_AND -> output += "\t\t[\u00AC\u2227]\n";
						case NOT_IF_ONLY_IF -> output += "\t\t[\u00AC\u2194]\n";
						case NOT_IF_THEN -> output += "\t\t[\u00AC\u2192]\n";
						case NOT_OR -> output += "\t\t[\u00AC\u2228]\n";
						case OR -> output += "\t\t[\u2228]\n";
						default -> {
						}
					}
				}
			}
		}

		if (result)
			output = output.substring(0,output.length()-9) + "{}\t[del]\n\n\nS is empty which makes it unsatisfiable. Thus, the original Input is satisfiable.";
		else {
			output += "\n\nS is satisfiable and can be satisfied by {";
			for (Statement s : subsets.get(number)) {
					output += s.getPremise() + ",";
			}
			output = output.substring(0,output.length() - 1) + "}. Thus, original Ιnput is unsatisfiable.";
		}
		
		char[] f = output.toCharArray();
		for (int x = 0; x < f.length; x++) {
			if (f[x] == '~')
				f[x] = '\u00AC';
			else if (f[x] == '|')
				f[x] = '\u2228';
			else if (f[x] == '&')
				f[x] = '\u2227';
			else if (f[x] == '-' && f[x+1] == '>') {
				f[x] = '\u2192';
				f[x+1] = '_';	// will be replaced
			} else if (f[x] == '<' && f[x+1] == '-' && f[x+2] == '>') {
				f[x] = '\u2194';
				f[x+1] = f[x+2] = '_';	// will be replaced later
			}
		}
		output = String.valueOf(f);
		output = output.replaceAll("_","");
		

		

		// checks if filename exists and if not, it adds a number to name
		String solutionFileName = "solved.txt";
        File file = new File(solutionFileName);
        int fileNumber = 1;
        while (file.exists()) {
        	solutionFileName = "solved_" + fileNumber + ".txt";
        	file = new File(solutionFileName);
        	fileNumber++;
        }

		try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
			out.write(output);
		} finally {
			String path = new File(solutionFileName).getAbsolutePath();
			JOptionPane.showMessageDialog(null, "Solution has been successfully written to file " + solutionFileName + "      in path \n" + path, "Success!", JOptionPane.INFORMATION_MESSAGE, view.getSuccessIcon());
		}
        
        
        
		subsets.clear();
		for (Entry<Operator, ArrayList<Statement>> entry : symtable.entrySet()) {
			if (entry.getValue() != null)
				entry.getValue().clear();
		}
		emptyEverything();
	}


	
	/* Checks if a miniset has only letters (NO_OPERATOR)
	 * Returns the number of miniset in minisets that contains only letters and cannot perform other rule
	 * Returns -1 if every miniset has at least one operator different from NO_OPERATOR
	 */
	private int checkForSingleLetters() {
		int lettersOnlySubset = -1;
		
		for (int i = 0; i < subsets.size(); i++) {
			for (Statement s : subsets.get(i)) {
				if (s.getOperator() == Operator.NO_OPERATOR) {
					lettersOnlySubset = i;
				} else {
					lettersOnlySubset = -1;
					break;
				}
			}
			if (lettersOnlySubset != -1)
				return lettersOnlySubset;
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


	private void addToMiniSets() {
		String[] seperatedPremises = mainSet.split(",");
		Operator key;
		int len = seperatedPremises.length;
		
		seperatedPremises[0] = seperatedPremises[0].substring(1);   // trim first curly bracket {
		seperatedPremises[len - 1] = seperatedPremises[len - 1].substring(0, seperatedPremises[len-1].length() - 1); // trim last }
		subsets.add(new ArrayList<>());  // initialize subset main arraylist by adding the Statements of main set
		int i = 0;
		for (String s : seperatedPremises) {
			subsets.get(0).add(new Statement(s));
			subsets.get(0).get(i).setMinisetNumber(0);
			
			key = subsets.get(0).get(i).getOperator();

			// check if rule in symtable has an active arraylist with premises
			symtable.computeIfAbsent(key, k -> new ArrayList<>());
			symtable.get(key).add(subsets.get(0).get(i));
			i++;
		}
		minCounter++;
	}

	
	
	
	
	
	

	/* ---------- 1. Read the file ---------- */
	
	public void readFile(String path) {
		if (path.length() == 0) {
			JOptionPane.showMessageDialog(null, "Error : Empty file.\nProgram will now terminate.", "Failure", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} else if (path.charAt(0) == '{' && path.charAt(path.length()-1) == '}') { // directly given as {....}
			output = "Input = " + path + "\n\n";
			mainSet = path.replaceAll("\\s+","");
		} else if (path.contains("/")) {	// directly given as {....}/
			output = "Input = " + path + "\n\n";
			reformClause(path, path.substring(path.lastIndexOf('/')+1));
			mainSet = mainSet.replaceAll("\\s+","");
		} else {		// given as file
			try {
				FileReader fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr);
				String line;
				line = br.readLine();
					line = line.replaceAll("\\s+","");
					reformClause(line, line.substring(line.lastIndexOf('/')+1));
					output = "Input = " + line + "\n\n";
				fr.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error : Cannot open file.\nProgram will now terminate.", "Failure", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			} 
		}
		
		checkInputForm(mainSet);
		output += "S = " + mainSet + "\n\n";
		output += "C0 = {" + mainSet + "}\n";
	}
	
	
	
	
	
	
	/**
	 * Determines if 'not' operator should be placed around whole clause with a set or parenthesis or just stick to the other 'not' already being there
	 * If 'not' encloses whole premise or part of it. Example below: 
	 * ~(P&Q)->(G&S)  _____ ~(~(P&Q)->(G&S)) [NOT_IF_THEN]			~(P<->~(D&E))  _____ ~~(P<->~(D&E)) [DOUBLE_NOT]
	 */
	private void reformClause(String line, String catchClause) {

		int i, openPar = 0;
		boolean fullNegation = false;   // full_negation is when negation encloses whole premise and not part of it
		
		
		
		if (catchClause.length() <= 2) {		// Letter or Negative letter  ===>  P and ~Q become ~P and ~~Q respectively
			mainSet = line.substring(0,line.lastIndexOf('}')) + ",~" + catchClause + "}";
			return;
		}

		
		if (catchClause.charAt(0) == '~') {
			fullNegation = true;       // true if negation catches whole premise and false if it's part of it
			
			for (i = 1; i < catchClause.length(); i++) {
				if (catchClause.charAt(i) == '(') {
					openPar++;
				} else if (catchClause.charAt(i) == ')') {
					openPar--;
				} else if (openPar == 0 && catchClause.charAt(i) != '(' && catchClause.charAt(i) != ')') {   // negation is part of letter __ ~P
					fullNegation = false;
					break;
				}
					
				
				if (openPar == 0 && i != catchClause.length()-1) {   // negation is part of smaller parenthesis and not at whole premise
					fullNegation = false;
					break;
				}
			}
		}
		String prem = (line.charAt(0) == '/') ? "{" : line.substring(0,line.lastIndexOf('}')) + ",";
		
		if (fullNegation)
			mainSet = prem + "~" + catchClause + "}";
		else
			mainSet = prem + "~(" + catchClause + ")}";
	}
	
	
	
	
	
	
	/* Returns true if all minisets are empty or false if at least one has statements in it */
	private boolean allSubsetsEmpty() {
		if (subsets.isEmpty())
			return true;
		for (ArrayList<Statement> subset : subsets) {
			if (!subset.isEmpty())
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
				currentLetter = s.getPremise();
				target = (currentLetter.charAt(0) == '~') ? currentLetter.substring(1) : ("~" + currentLetter);	// if negative, search the positve and vice-versa
				
				// DELETE EVERYTHING THAT REFERS TO THE DELETED SET (SET AND SYMTABLE STATEMENTS)
				for (Statement s2 : symtable.get(Operator.NO_OPERATOR)) {
					if (s2.getPremise().equals(target) && s.getMinisetNumber() == s2.getMinisetNumber()) {
						for (h = 0; h < subsets.size(); h++) {
							if (subsets.get(h).contains(s2) && subsets.get(h).get(0).getMinisetNumber() == s.getMinisetNumber()) {
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
				            		currentStatement = i.next();
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
		String newPart;
		
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
				newPart = ("~(" + s + ")");
			} else {
				if (s.charAt(0) == '~')
					newPart = ("~" + s);
				else
					newPart = ("~(" + s + ")");
			}
		} else
			newPart = ("~(" + s + ")");
		
		return newPart;
	}
	
	
	/* Checks if given string uses legal grammar rules or has parenthesis issues.
	 * If found any problem that doesn't meet the requirements, displays an error message and exits program */
	private void checkInputForm(String s) {
		if (Objects.equals(s, "") || s == null) {
			JOptionPane.showMessageDialog(null, "Empty input. Please type a problem in correct form to solve.\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} else if (s.charAt(0) != '{' && s.charAt(0) != '/') {
			JOptionPane.showMessageDialog(null, "Missing left bracket { or slash /..\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} else if (s.charAt(0) == '{' && s.indexOf('}') == -1) {
			JOptionPane.showMessageDialog(null, "Missing right bracket } bracket..\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} else if (s.charAt(0) == '/' && (s.indexOf('{') > -1 || s.indexOf('}') > -1)) {
			JOptionPane.showMessageDialog(null, "Error : Cannot insert brackets when input starts with slash.\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} else {
			int i, openPar = 0;
			
			for (i = 0; i < s.length(); i++) {
				if (s.charAt(i) == '(')
					openPar++;
				else if (s.charAt(i) == ')')
					openPar--;
			}
			if (openPar != 0) {
			JOptionPane.showMessageDialog(null, "Parenthesis pairs not matching..\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
			} else {
				String validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ,/<-&|~(){}>";
				int rightBracketCounter = 0;
				
				for (i = 1; i < s.length(); i++) {
					if (s.charAt(i) == ',' && s.charAt(i+1) == ',') {
						JOptionPane.showMessageDialog(null, "Error : Cannot have two or more consecutive commas ','.\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
		     		System.exit(1);
					} else if (s.charAt(i) == '-' && s.charAt(i+1) != '>') {
						JOptionPane.showMessageDialog(null, "Error : - not followed by >   Maybe trying to type -> ?.\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					} else if (s.charAt(i) == '{') {
						JOptionPane.showMessageDialog(null, "Error : Cannot insert second left curly bracket {.\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					} else if (s.charAt(i) == '}' && rightBracketCounter == 0) {
						rightBracketCounter++;
					} else if (s.charAt(i) == '}' && rightBracketCounter == 1) {
						JOptionPane.showMessageDialog(null, "Error : Cannot insert second right curly bracket }.\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					} else if (s.charAt(i) == '>' && s.charAt(i-1) != '-') {
						JOptionPane.showMessageDialog(null, "Error : Illegal character '>'. Probably forgot - before > ?.\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					} else if (s.charAt(i) == '<' && (s.charAt(i+1) != '-' || s.charAt(i+2) != '>' && i+2 < s.length())) {
						JOptionPane.showMessageDialog(null, "Error : < not followed by - and >   Maybe trying to type <-> ?.\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					} else if ((s.charAt(i) == '|' || s.charAt(i) == '&' || s.charAt(i) == '>') && ((i+1 == s.length()) || ((s.charAt(i+1) == ',' || s.charAt(i+1) == ')' || s.charAt(i+1) == '}') && i+1 < s.length()))) {
						JOptionPane.showMessageDialog(null, "Error : Missing RIGHT part from operator.\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					} else if (((s.charAt(i) == ',' || s.charAt(i) == '{' || s.charAt(i) == '~' || s.charAt(i) == '(') && (s.charAt(i+1) == '|' || s.charAt(i+1) == '&' || s.charAt(i+1) == '<' || s.charAt(i+1) == '-' || s.charAt(i+1) == '}' || s.charAt(i+1) == ',') && i+1 < s.length())) {
						JOptionPane.showMessageDialog(null, "Error : Missing LEFT part from operator.\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					} else if ((validCharacters.indexOf(s.charAt(i)) < 52 && s.charAt(i+1) == '(') || ((s.charAt(i) == ')' && validCharacters.indexOf(s.charAt(i+1)) < 52)) && i+1 < s.length()) {
						JOptionPane.showMessageDialog(null, "Error : Cannot have letter next to parenthesis.\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					} else if (validCharacters.indexOf(s.charAt(i)) < 52 && validCharacters.indexOf(s.charAt(i+1)) < 52 && i+1 < s.length()) {
						JOptionPane.showMessageDialog(null, "Error : Cannot have two ore more consecutive letters.\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					} else if (validCharacters.indexOf(s.charAt(i)) == -1) {		// might be <-> or ->  								
						JOptionPane.showMessageDialog(null, "Error : Illegal character '" + s.charAt(i) + "'.\n\nProgram will be terminated.", "Failure", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					}
				}
			}
		}
	}
}
