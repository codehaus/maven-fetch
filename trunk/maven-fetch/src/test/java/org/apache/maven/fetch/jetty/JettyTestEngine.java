package org.apache.maven.fetch.jetty;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Maven" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Maven", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ====================================================================
 */
 
import org.apache.maven.fetch.servlets.AuthRepoServlet;
import org.apache.maven.fetch.servlets.FlawlessRepoServlet;
import org.apache.maven.fetch.servlets.ProxyAuthRepoServlet;
import org.apache.maven.fetch.servlets.SlowRepoServlet;
import org.apache.maven.fetch.servlets.UserAgentRepoServlet;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SecurityConstraint;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.util.MultiException;

/**
 * Fully encapsulated servlet engine for simulating a variety of scenarios for fetching
 * resources.
 * 
 * @author  Ben Walding
 * @version $Id$
 */
public class JettyTestEngine
{
    public static void main(String args[]) throws Exception
    {
        JettyTestEngine jte = new JettyTestEngine();
        jte.startJetty();
        Thread.sleep(100000);
        jte.stopServer();
    }

    private HttpServer server;

    private static final int PORT = 8000;
    private static final String CONTEXT = "repositories";

    public void startJetty() throws MultiException
    {
        // Create the server
        server = new HttpServer();

        // Create a port listener
        SocketListener listener = new SocketListener();
        listener.setPort(PORT);
        server.addListener(listener);

        // Create a context 
        HttpContext context = new HttpContext();
        context.setContextPath("/" + CONTEXT + "/*");
        server.addContext(context);

        // Create a servlet container
        ServletHandler servlets = new ServletHandler();

        // Map a servlet onto the container
        servlets.addServlet("/flawless-repo/*", FlawlessRepoServlet.class.getName());
        servlets.addServlet("/slow-repo/*", SlowRepoServlet.class.getName());
        servlets.addServlet("/auth-repo/*", AuthRepoServlet.class.getName());
        servlets.addServlet("/proxyauth-repo/*", ProxyAuthRepoServlet.class.getName());
        servlets.addServlet("/useragent-repo/*", UserAgentRepoServlet.class.getName());
        context.addHandler(servlets);

        SecurityConstraint sc = new SecurityConstraint();
        sc.addRole("ANYUSER");
        sc.setAuthenticate(true);
        sc.setDataConstraint(SecurityConstraint.DC_NONE);
        context.addSecurityConstraint("/auth-repo/*", sc);

        server.start();
    }

    public void stopServer() throws InterruptedException
    {
        server.stop();
    }

    public int getPort()
    {
        return PORT;
    }

    public String getContext()
    {
        return CONTEXT;
    }

    public void getServerBase()
    {
    }

    public String getUrl(String relativeUrl)
    {
        return "http://localhost:" + getPort() + "/" + CONTEXT + relativeUrl;
    }
}
