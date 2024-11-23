package server.ws;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	
	/*TODO: logic to check for proper bid will go here*/
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		String target_forward = "/FORWARD.jsp";
		String target_dutch = "/DUTCH.jsp";

		//Decoding the string
		String checkedName = request.getParameter("checked");
		System.out.println(checkedName);
		
		//decoding checkedName
		String[] fchecked_arr = checkedName.split(":");
			session.setAttribute("auction_type", fchecked_arr[0]);
			session.setAttribute("starting_bid", fchecked_arr[1]);
			session.setAttribute("item_name", fchecked_arr[2]);
			session.setAttribute("item_id", fchecked_arr[3]);
			
			//TODO: replace session_id with the username 
			session.setAttribute("session_id", session.getId());
			if(fchecked_arr[0].contains("forward")) {
				request.getRequestDispatcher(target_forward).forward(request, response);
			}
			else { 
				request.getRequestDispatcher(target_dutch).forward(request, response); 
			}
		} 
		
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
