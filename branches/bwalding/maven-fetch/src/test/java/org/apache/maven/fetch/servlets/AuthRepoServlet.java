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
 * @author <a href="bwalding@jakarta.org">Ben Walding</a>
 * @version $Id$
 */
public class AuthRepoServlet extends FlawlessRepoServlet
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(AuthRepoServlet.class);

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic "))
        {
            LOGGER.info("Requiring authentication...");
            response.addHeader("WWW-Authenticate", "Basic realm=JettyTestEngine");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String decoded = new String(org.apache.maven.fetch.util.Base64.decode(authHeader.substring(6)));
        LOGGER.info("Decoded authentication header:" + decoded);

        if (!decoded.equals("test:test"))
        {
            response.addHeader("WWW-Authenticate", "Basic realm=JettyTestEngine");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        super.doGet(request, response);
    }

}
