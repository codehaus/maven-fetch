package org.apache.maven.fetch;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;

import org.apache.maven.fetch.exceptions.FetchException;
import org.apache.maven.fetch.exceptions.NotAuthorizedFetchException;
import org.apache.maven.fetch.exceptions.NotModifiedFetchException;
import org.apache.maven.fetch.exceptions.ProxyNotAuthorizedFetchException;
import org.apache.maven.fetch.exceptions.ResourceNotFoundFetchException;
import org.apache.maven.fetch.exceptions.UnsupportedProtocolFetchException;
import org.apache.maven.fetch.jetty.JettyTestEngine;
import org.mortbay.util.MultiException;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class HTTPFetchTest extends TestCase
{
    JettyTestEngine testEngine;

    public void setUp() throws MultiException
    {
        testEngine = new JettyTestEngine();
        testEngine.startJetty();
    }

    public void tearDown() throws InterruptedException
    {
        testEngine.stopServer();
        testEngine = null;
    }

    public void testFlawlessStream() throws InterruptedException, FetchException
    {
        FetchTool bean = new FetchTool();

        FetchRequest dreq = new FetchRequest(testEngine.getUrl("/flawless-repo/test-data-1.txt"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dreq.setOutputStream(baos);

        FetchResponse dresp = bean.performDownload(dreq);
        assertNotNull(dresp);
        assertEquals("THIS IS TEST-DATA-1.TXT", baos.toString());
    }

    public void testFlawlessStreamModifiedCheck() throws InterruptedException, FetchException, ParseException
    {
        FetchTool bean = new FetchTool();

        FetchRequest dreq = new FetchRequest(testEngine.getUrl("/flawless-repo/test-data-1.txt"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dreq.setOutputStream(baos);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        dreq.setOnlyIfModifiedSinceDate(sdf.parse("19980101"));
        FetchResponse dresp = bean.performDownload(dreq);
        assertNotNull(dresp);
        assertEquals("THIS IS TEST-DATA-1.TXT", baos.toString());
    }

    public void testFlawlessStreamNotModifiedCheck() throws InterruptedException, FetchException, ParseException
    {
        FetchTool bean = new FetchTool();

        FetchRequest dreq = new FetchRequest(testEngine.getUrl("/flawless-repo/test-data-1.txt"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dreq.setOutputStream(baos);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        dreq.setOnlyIfModifiedSinceDate(sdf.parse("20000101"));
        try
        {
            bean.performDownload(dreq);
            fail("The resource was not modified and should have thrown NotModified exception");
        }
        catch (NotModifiedFetchException e)
        {
            //Success!         
        }
    }

    public void testFlawlessFileNotModified() throws InterruptedException, FetchException
    {
        FetchTool bean = new FetchTool();

        File f = new File("target/test1.file");
        f.delete();

        FetchRequest dreq = new FetchRequest(testEngine.getUrl("/flawless-repo/test-data-1.txt"));
        dreq.setOutputFile(f);

        FetchResponse dresp = bean.performDownload(dreq);
        assertNotNull(dresp);

        assertEquals("THIS IS TEST-DATA-1.TXT".length(), f.length());
    }

    public void testSlow() throws InterruptedException, FetchException
    {
        FetchTool bean = new FetchTool();

        FetchRequest dreq = new FetchRequest(testEngine.getUrl("/slow-repo/test-data-2.txt"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dreq.setOutputStream(baos);

        FetchResponse dresp = bean.performDownload(dreq);
        assertNotNull(dresp);
        assertEquals("THIS IS TEST-DATA-2.TXT", baos.toString());
    }

    public void testNotAuthed() throws InterruptedException, FetchException
    {
        try
        {

            FetchTool bean = new FetchTool();

            FetchRequest dreq = new FetchRequest(testEngine.getUrl("/auth-repo/test-data-1.txt"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            dreq.setOutputStream(baos);

            bean.performDownload(dreq);
            fail("Should have thrown " + NotAuthorizedFetchException.class.getName());
        }
        catch (NotAuthorizedFetchException e)
        {
            //Success

        }
    }

    public void testAuthed1() throws InterruptedException, FetchException
    {

        FetchTool bean = new FetchTool();

        FetchRequest dreq = new FetchRequest(testEngine.getUrl("/auth-repo/test-data-1.txt"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dreq.setOutputStream(baos);
        dreq.setUser("test");
        dreq.setPass("test");

        FetchResponse dresp = bean.performDownload(dreq);
        assertNotNull(dresp);
        assertEquals("THIS IS TEST-DATA-1.TXT", baos.toString());

    }

    public void testAuthed2() throws InterruptedException, FetchException
    {

        FetchTool bean = new FetchTool();
        String url =
            "http://test:test@localhost:"
                + testEngine.getPort()
                + "/"
                + testEngine.getContext()
                + "/auth-repo/test-data-1.txt";
        FetchRequest dreq = new FetchRequest(url);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dreq.setOutputStream(baos);

        FetchResponse dresp = bean.performDownload(dreq);
        assertNotNull(dresp);
        assertEquals("THIS IS TEST-DATA-1.TXT", baos.toString());

    }

    public void testProxyNotAuthed() throws InterruptedException, FetchException
    {
        try
        {

            FetchTool bean = new FetchTool();

            FetchRequest dreq = new FetchRequest(testEngine.getUrl("/proxyauth-repo/test-data-1.txt"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            dreq.setOutputStream(baos);
            dreq.setProxyHost("localhost");
            dreq.setProxyPort(80);
            bean.performDownload(dreq);
            fail("Should have thrown " + ProxyNotAuthorizedFetchException.class.getName());
        }
        catch (ProxyNotAuthorizedFetchException e)
        {
            //Success

        }
    }

    public void testProxyAuthed() throws InterruptedException, FetchException
    {
        FetchTool bean = new FetchTool();

        FetchRequest dreq = new FetchRequest(testEngine.getUrl("/proxyauth-repo/test-data-1.txt"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dreq.setOutputStream(baos);
        dreq.setProxyHost("127.0.0.1");
        dreq.setProxyPort(80);
        dreq.setProxyUser("proxy");
        dreq.setProxyPass("proxy");

        FetchResponse dresp = bean.performDownload(dreq);
        assertNotNull(dresp);
        assertEquals("THIS IS TEST-DATA-1.TXT", baos.toString());
    }

    public void testUnknownProtocol() throws FetchException
    {
        try
        {
            FetchTool bean = new FetchTool();

            FetchRequest dreq = new FetchRequest("shrub://test/fred.html");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            dreq.setOutputStream(baos);
            bean.performDownload(dreq);
            fail("shrub is not a valid protocol. FetchTool should have thrown an exception,");
        }
        catch (UnsupportedProtocolFetchException e)
        {
            //success
        }
    }

    /**
     * This tests the end-to-end handling of both authorisation (based on user agent)
     * and the transfer of user-agent amongst components.
     * @throws InterruptedException
     * @throws FetchException
     */
    public void testUserAgentFred() throws InterruptedException, FetchException
    {
        try
        {
            FetchTool bean = new FetchTool();

            FetchRequest dreq = new FetchRequest(testEngine.getUrl("/useragent-repo/test-data-1.txt"));
            dreq.setUser("Fred");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            dreq.setOutputStream(baos);

            bean.performDownload(dreq);
            fail("Should have thrown " + NotAuthorizedFetchException.class.getName());
        }
        catch (NotAuthorizedFetchException e)
        {
            //Success
        }
    }

    public void testUserAgentBonza() throws InterruptedException, FetchException
    {
        FetchTool bean = new FetchTool();

        FetchRequest dreq = new FetchRequest(testEngine.getUrl("/useragent-repo/test-data-1.txt"));
        dreq.setHeaderUserAgent("Bonza");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dreq.setOutputStream(baos);

        bean.performDownload(dreq);
        assertEquals("THIS IS TEST-DATA-1.TXT", baos.toString());
    }

    public void testFlawlessStreamMissingResource() throws InterruptedException, FetchException
    {
        try
        {
            FetchTool bean = new FetchTool();

            FetchRequest dreq = new FetchRequest(testEngine.getUrl("/flawless-repo/shrub"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            dreq.setOutputStream(baos);

            bean.performDownload(dreq);
            fail("Should have thrown " + ResourceNotFoundFetchException.class.getName());
        }
        catch (ResourceNotFoundFetchException e)
        {
            //success
        }
    }
}