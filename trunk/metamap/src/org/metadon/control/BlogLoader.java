/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.metadon.control;

import java.io.*;
import java.net.*;
import java.util.Vector; //import at.ac.tuwien.tmblog.web.util.MultipartResponse;

import javax.servlet.*;
import javax.servlet.http.*;

import org.metadon.beans.Blog;
import org.metadon.beans.Waypoint;

import com.thoughtworks.xstream.XStream;

/**
 *
 * @author Hannes
 */
@SuppressWarnings("serial")
public class BlogLoader extends HttpServlet
{

	/** 
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 * @param request servlet request
	 * @param response servlet response
	 */
	private SessionBindingListener	sessionListener;

	protected void processRequest(HttpServletRequest request,
	                              HttpServletResponse response) throws ServletException,
	                                                           IOException
	{

		String action = request.getParameter("action");
		response.setContentType("application/xml");
		ServletOutputStream out = response.getOutputStream();
		this.sessionListener = StrutsActionLogin.getSessionListener();
		try
		{
			if (action != null && action.equals("getWaypointList"))
			{
				// create xml document and send it to client
				out.println(this.getXMLWaypointListDoc(this.sessionListener.getWaypointList()));
				out.flush();
				//                // Prepare a multipart response
				//                MultipartResponse multipartResponse = new MultipartResponse(response);
				//                multipartResponse.startResponse("application/xml");
				//                out.println(this.getXMLWaypointDoc(this.sessionListener.getWaypoints()));
				//                multipartResponse.endResponse();

			}
			else if (action != null && action.equals("getWaypointUpdateList"))
			{
				out.println(this.getXMLWaypointListDoc(this.sessionListener.getWaypointUpdateList()));
				out.flush();

			}
			else if (action != null && action.equals("getBlogList"))
			{
				// create xml document and send it to client
				out.println(this.getXMLBlogListDoc(this.sessionListener.getBlogList()));
				out.flush();

			}
			else if (action != null && action.equals("getBlog"))
			{
				String id = request.getParameter("id");
				// create xml document and send it to client
				if (id != null)
				{
					out.println(this.getXMLBlogDoc(this.sessionListener.getBlog(Integer.valueOf(id).intValue())));
					out.flush();
				}
			}
		}
		finally
		{
			out.close();
		}
	}

	private String getXMLWaypointListDoc(Vector<Waypoint> waypointList)
	{
		StringBuffer xmlString = new StringBuffer();

		xmlString.append("<list>");
		if (waypointList != null)
		{
			XStream xstream = new XStream();
			for (int i = 0; i < waypointList.size(); i++)
			{
				xstream.alias("waypoint", Waypoint.class);
				String xml = xstream.toXML(waypointList.elementAt(i));
				xmlString.append(xml);
			}
		}
		xmlString.append("</list>");
		System.out.println("waypoint xml string: " + xmlString.toString());
		return xmlString.toString();
	}

	private String getXMLBlogListDoc(Vector<Blog> blogList)
	{
		StringBuffer xmlString = new StringBuffer();
		xmlString.append("<list>");
		if (blogList != null)
		{
			XStream xstream = new XStream();
			for (int i = 0; i < blogList.size(); i++)
			{
				xstream.alias("blog", Blog.class);
				String xml = xstream.toXML(blogList.elementAt(i));
				xmlString.append(xml);
			}
		}
		xmlString.append("</list>");
		System.out.println("bloglist xml string: " + xmlString.toString());
		return xmlString.toString();
	}

	private String getXMLBlogDoc(Blog blog)
	{
		StringBuffer xmlString = new StringBuffer();
		xmlString.append("<list>");
		if (blog != null)
		{
			XStream xstream = new XStream();
			xstream.alias("blog", Blog.class);
			String xml = xstream.toXML(blog);
			xmlString.append(xml);
		}
		xmlString.append("</list>");
		System.out.println("blog xml string: " + xmlString.toString());
		return xmlString.toString();
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/** 
	 * Handles the HTTP <code>GET</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 */
	protected void doGet(HttpServletRequest request,
	                     HttpServletResponse response) throws ServletException,
	                                                  IOException
	{
		processRequest(request, response);
	}

	/** 
	 * Handles the HTTP <code>POST</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 */
	protected void doPost(HttpServletRequest request,
	                      HttpServletResponse response) throws ServletException,
	                                                   IOException
	{
		processRequest(request, response);
	}

	/** 
	 * Returns a short description of the servlet.
	 */
	public String getServletInfo()
	{
		return "TMBlog Loader";
	}
	// </editor-fold>
}
