package org.apache.maven.fetch.servlets;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This repository works, but it is quite slow to transfer data. 
 * first half, 5 second delay, last half
 * 
 * This will ensure that readers won't fall apart if they stop getting data temporarily
 * 
 * @author <a href="bwalding@jakarta.org">Ben Walding</a>
 * @version $Id$
 */
public class SlowRepoServlet extends HttpServlet
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SlowRepoServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse arg1) throws ServletException, IOException
    {
        LOGGER.info("request.getPathInfo() = " + request.getPathInfo());
        String resource = "data" + request.getPathInfo();
        InputStream is = getClass().getResourceAsStream(resource);

        if (is == null)
        {
            arg1.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        else
        {
            OutputStream os = arg1.getOutputStream();
            final byte[] buffer = new byte[5];
            while (true)
            {
                int bytesRead = is.read(buffer, 0, buffer.length);
                if (bytesRead == -1)
                    break;
                os.write(buffer, 0, bytesRead);
                os.flush();
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }            
        }
    }
}
