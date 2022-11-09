package webapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet implementation class Solve
 * 
 * @author Georgios Mpirmpilis
 * <p> This class sets up the communication between HTML and the actual java code </p>
 * <br><br><b>Solve</b> : Creates a new instance of the solver (giving the input) and gets
 * the result formatted suitably for the HTML display.
 */
public class Solve extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Solver solver = new Solver(request.getParameter("inputproblem"));

		String output = "<!DOCTYPE html>\r\n"
				+ "<html>\r\n"
				+ "<head>\r\n"
				+ "<meta charset=\"UTF-8\">\r\n"
				+ "<title>SOLVED! - [CS180 Logic]</title>\r\n"
				+ "<script type=\"text/javascript\" src='js/checkInput.js' async></script>"
				+ "</head>\r\n"
				+ "<body style=\"background-color:#d4fffc;font-size:13pt;\">"
				+ getTextboxAndButton()
				+ "<p style= font-family:Verdana;>";

		
		output += solver.getResult() + "</p></body>\r\n"
				+ "</html>";
				
		PrintWriter out = response.getWriter();
		out.println(output);
	}

	
	private String getTextboxAndButton() {
		String s = "<div style=\"text-align:center\">\r\n"
				+ "	<form method=get action=Solve name=\"problemForm\" onsubmit=\"return checkInputForm()\">\r\n"
				+ "		Enter problem to solve: <input style=\"width:500px;font-size:14pt;\" type=text id=\"txt1\" name=inputproblem>\r\n"
				+ "		<br><br>\r\n"
				+ "		<input type=submit id=\"sbmt\" value=\"Check Satisfiability\">\r\n"
				+ "	</form>\r\n"
				+ "</div>\r\n";
		
		return s;
	}
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);

		
	}

}
