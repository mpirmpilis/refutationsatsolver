package webapp;

/**
 * @author Georgios Mpirmpilis
 * <p> This class illustrates the main statement along with it's attributes </p>
 * <br><br><b>Statement</b> : Takes the given premise and analyzes it to extract the
 * main operator along with the left and right parts (the parts that are joined by the operator).
 * Also, it specifies the miniset number this premise currently belongs to. 
 */

public class Statement implements Cloneable {
	private String premise, leftPart, rightPart;
	private Operator operator;
	private int subsetNumber;
	
	public Statement(String premise) {
		this.premise = premise;
		this.operator = null;
		this.subsetNumber = 0;
		getRuleAndParts();
	}
	
	public Object clone(){  
	    try{  
	        return super.clone();  
	    }catch(Exception e){ 
	        return null; 
	    }
	}
	
	
	
	
	public void setLeftPart(String left_part) { this.leftPart = left_part; }
	public void setRightPart(String right_part) { this.rightPart = right_part;	}
	public void setOperator(Operator operator) { this.operator = operator; }
	public void setMinisetNumber(int minisetNumber) { this.subsetNumber = minisetNumber; }
	
	public String getPremise() { return this.premise; }
	public String getLeftPart() { return this.leftPart; }
	public String getRightPart() { return this.rightPart; }
	public Operator getOperator() { return this.operator; }
	public int getMinisetNumber() { return this.subsetNumber; }
	
	
	
	
	
	
	
	
	
	public void getRuleAndParts () {
			int open_par = 0, i = 0, j = 0;
			char ch;
			boolean negation = false;
			String copy = this.premise;
			
						
			if (copy.length() == 2 && copy.charAt(0) == '~' && copy.charAt(1) == '~') {    // this is double not ___ ~~(.......
				this.leftPart = this.rightPart = null;
				this.operator = Operator.DOUBLE_NOT;
			} else if (copy.length() == 1 || (copy.length() == 2 && copy.charAt(0) == '~' && copy.charAt(1) != '(')) {  // letter or negative letter
				this.leftPart = this.rightPart = this.premise;
				this.operator = Operator.NO_OPERATOR;
			}
			
			
		if (this.operator == null) {
			if (copy.charAt(0) == '~') {
				negation = true;       // true if negation catches whole premise and false if it's part of it
				
				for (i = 1; i < copy.length(); i++) {
					if (copy.charAt(i) == '(') {
						open_par++;
					} else if (copy.charAt(i) == ')') {
						open_par--;
					} else if (open_par == 0 && copy.charAt(i) != '(' && copy.charAt(i) != ')') {   // negation is part of letter __ ~P
						negation = false;
						break;
					}
						
					
					if (open_par == 0 && i != copy.length()-1) {   // negation is part of smaller parenthesis and not at whole premise
						negation = false;
						break;
					} else
						negation = true;
				}
			}
			
			if (negation) {
				i = 2;
				j = copy.length() - 1;
			} else {
				i = 0;
				j = copy.length();
			}
			
				open_par = 0;
				for (; i < j; i++) {
					ch = copy.charAt(i);
					if (ch == '(') {
						open_par++;
					} else if (ch == ')') {
						open_par--;
					} else if (open_par == 0) {
						if (ch == '-' && copy.charAt(i+1) == '>') {
							this.operator = Operator.IF_THEN;
						} else if (ch == '<' && copy.charAt(i+1) == '-' && copy.charAt(i+2) == '>') {
							this.operator = Operator.IF_ONLY_IF;
						} else if (ch == '&') {
							this.operator = Operator.AND;
						} else if (ch == '|') {
							this.operator = Operator.OR;
						}
					}
					
					if (this.operator != null)  // a rule was found, no need to analyze further. BREAK!
						break;
				}
			

			if (copy.charAt(0) == '~' && copy.charAt(1) == '~') {
				this.operator = Operator.DOUBLE_NOT;
			} else if (negation) {			// check if rule is combined with a NOT	
				switch (this.operator) {
					case AND:
						this.operator = Operator.NOT_AND;
						break;
					case OR:
						this.operator = Operator.NOT_OR;
						break;
					case IF_THEN:
						this.operator = Operator.NOT_IF_THEN;
						break;
					case IF_ONLY_IF:
						this.operator = Operator.NOT_IF_ONLY_IF;
						break;
					default:
						this.operator = Operator.DOUBLE_NOT;
				}
			}
			
			
			
			/* EXTRACT the two parts of the operator */
			// check if there is an extra set of parenthesis and trim it (if so...)
			if (this.operator == Operator.AND || this.operator == Operator.OR) {
				this.leftPart = copy.substring(0,i);
				this.rightPart = copy.substring(i+1,copy.length());
			} else if (this.operator == Operator.NOT_AND || this.operator == Operator.NOT_OR) {
				this.leftPart = copy.substring(2,i);
				this.rightPart = copy.substring(i+1,copy.length()-1);
			} else if (this.operator == Operator.IF_THEN) {
				this.leftPart = copy.substring(0,i);
				this.rightPart = copy.substring(i+2,copy.length());
			} else if (this.operator == Operator.IF_ONLY_IF) {
				this.leftPart = copy.substring(0,i);
				this.rightPart = copy.substring(i+3,copy.length());
			} else if (this.operator == Operator.NOT_IF_THEN) {
				this.leftPart = copy.substring(2,i);
				this.rightPart = copy.substring(i+2,copy.length()-1);
			} else if (this.operator == Operator.NOT_IF_ONLY_IF) {
				this.leftPart = copy.substring(2,i);
				this.rightPart = copy.substring(i+3,copy.length()-1);
			}
			if (this.leftPart != null && this.leftPart.length() > 2 && (this.operator != Operator.NO_OPERATOR || this.operator != Operator.DOUBLE_NOT)) {  // can extract big premise and more than two chars
				if (this.leftPart.charAt(0) != '~') {
					this.leftPart = this.leftPart.substring(1,this.leftPart.length()-1);
				}
		}			
			
			// if there is "not" after operator (right_par), catch it whole. If there is not, trim the outside set of parenthesis
			if (this.rightPart != null && this.rightPart.length() > 2 && (this.operator != Operator.NO_OPERATOR || this.operator != Operator.DOUBLE_NOT)) {  // can extract big premise and more than two chars
				if (this.rightPart.charAt(0) != '~') {
					this.rightPart = this.rightPart.substring(1,this.rightPart.length()-1);
				}
			}
		}
	}
}
