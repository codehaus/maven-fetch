package org.apache.maven.fetch.fetchers;

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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.maven.fetch.FetchRequest;
import org.apache.maven.fetch.exceptions.FetchException;
import org.apache.maven.fetch.exceptions.NotAuthorizedFetchException;
import org.apache.maven.fetch.exceptions.NotModifiedFetchException;
import org.apache.maven.fetch.exceptions.ProxyNotAuthorizedFetchException;
import org.apache.maven.fetch.exceptions.ResourceNotFoundFetchException;
import org.apache.maven.fetch.util.*;
import org.apache.maven.fetch.util.Base64;

/**
 * @author <a href="bwalding@jakarta.org">Ben Walding</a>
 * @version $Id$
 */
public class HttpFetcher implements Fetcher
{
    private static class State
    {
        String user;
        String pass;
        String url;

        String proxyUser;
        String proxyPass;
    }

    /* (non-Javadoc)
     * @see org.apache.maven.fetch.fetchers.Fetcher#fetchUrl(java.lang.String, java.io.OutputStream)
     */
    public void fetchUrl(FetchRequest request) throws FetchException
    {
        State state = new State();
        processUrl(request, state);
        processUser(request, state);
        processProxy(request, state);

        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(state.url);
            connection = (HttpURLConnection) url.openConnection();

            if (state.user != null)
            {
                connection.addRequestProperty("Authorization", getAuthorization(state));
            }

            if (state.proxyUser != null)
            {
                connection.addRequestProperty("Proxy-Authorization", getProxyAuthorization(state));
            }

            if (request.getHeaderUserAgent() != null)
            {
                connection.addRequestProperty("User-Agent", request.getHeaderUserAgent());
            }

            if (request.getOnlyIfModifiedSinceDate() != null)
            {
                connection.setIfModifiedSince(request.getOnlyIfModifiedSinceDate().getTime());
            }

            connection.connect();

            if (connection.getResponseCode() == 304)
            {
                throw new NotModifiedFetchException("Not Modified");
            }

            if (connection.getResponseCode() == 401)
            {
                throw new NotAuthorizedFetchException(connection.getResponseMessage());
            }

            if (connection.getResponseCode() == 404)
            {
                throw new ResourceNotFoundFetchException(connection.getResponseMessage());
            }

            if (connection.getResponseCode() == 407)
            {
                throw new ProxyNotAuthorizedFetchException(connection.getResponseMessage());
            }

            OutputStream os = request.getFinalOutputStream();
            IOUtility.transferStream(connection.getInputStream(), os);
            os.flush();
            os.close();
        }
        catch (FetchException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new FetchException(e.getLocalizedMessage(), e);
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }

    private String getAuthorization(State state)
    {
        return getAuth(state.user, state.pass);
    }

    private String getProxyAuthorization(State state)
    {
        return getAuth(state.proxyUser, state.proxyPass);
    }

    private String getAuth(String user, String pass)
    {
        return "Basic " + Base64.encodeBytes((user + ":" + pass).getBytes());
    }

    /**
     * @param request
     * @param state
     */
    private void processUrl(FetchRequest request, State state)
    {
        state.url = request.getUrl();
    }

    private void processUser(FetchRequest request, State state)
    {
        if (request.getUser() != null)
        {
            state.user = request.getUser();
            state.pass = request.getPass();
        }

        String[] pu = URLTool.parseUrl(state.url);
        if (pu[0] != null)
        {
            state.user = pu[0];
            state.pass = pu[1];
            state.url = pu[2];
        }
    }

    private void processProxy(FetchRequest request, State state)
    {
        if (request.isProxied() && request.getProxyUser() != null)
        {
            state.proxyUser = request.getProxyUser();
            state.proxyPass = request.getProxyPass();
        }
        else
        {
            state.proxyUser = null;
            state.proxyPass = null;
        }

        //XXX need to check proxy exclusions
        if (request.isProxied())
        {
            System.getProperties().put("proxySet", "true");
            System.getProperties().put("proxyHost", request.getProxyHost());
            System.getProperties().put("proxyPort", "" + request.getProxyPort());
        }
        else
        {
            System.getProperties().put("proxySet", "false");
        }

    }

}