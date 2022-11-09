package webapp;

/**
 * @author Georgios Mpirmpilis
 * <p> This class models the operators that joins the two premises </p>
 * <br><br><b>Operator</b> : Enumerates all possible operator combinations used
 * for solving the problem.
 */

public enum Operator {
	AND,				/* &    */
	OR,					/* |    */
	IF_THEN,			/* ->   */
	IF_ONLY_IF,			/* <->  */
	NOT_AND,			/* ~&   */
	NOT_OR,				/* ~|   */
	NOT_IF_THEN,		/* ~->  */
	NOT_IF_ONLY_IF,		/* ~<-> */
	DOUBLE_NOT,			/* ~~   */
	NO_OPERATOR;		/* used when having letter or with negation: P, ~S */
}