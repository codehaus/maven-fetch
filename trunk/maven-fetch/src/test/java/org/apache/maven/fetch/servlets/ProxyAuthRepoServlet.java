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
 * In the place of a real proxy, we slam out some proxy authentication headers
 * to simulate the client hitting a proxy.
 * 
 * XXX: This is insufficient testing, but will do for the time being.
 * 
 * @author <a href="bwalding@jakarta.org">Ben Walding</a>
 * @version $Id$
 */
public class ProxyAuthRepoServlet extends FlawlessRepoServlet
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ProxyAuthRepoServlet.class);

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Proxy-Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic "))
        {
            LOGGER.info("Requiring proxy authentication...");
            response.addHeader("Proxy-Authenticate", "Basic realm=JettyTestEngine");
            response.sendError(HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED);
            return;
        }

        String decoded = new String(org.apache.maven.fetch.util.Base64.decode(authHeader.substring(6)));
        LOGGER.info("Decoded Proxy-Authorization header:" + decoded);

        if (!decoded.equals("proxy:proxy"))
        {
            response.addHeader("Proxy-Authenticate", "Basic realm=JettyTestEngine");
            response.sendError(HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED);
            return;
        }

        super.doGet(request, response);
    }

}
