/*
 * Created on 25/05/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.apache.maven.fetch.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Only allows requests from agents that identify themselves as "Bonza"
 * @author <a href="bwalding@jakarta.org">Ben Walding</a>
 * @version $Id$
 */
public class UserAgentRepoServlet extends FlawlessRepoServlet
{
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String userAgentHeader = request.getHeader("User-Agent");

        if (!"Bonza".equals(userAgentHeader))
        {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        super.doGet(request, response);
    }
}
