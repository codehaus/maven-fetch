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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class FetchRequest
{
    private OutputStream outputStream;
    private String url;
    private File outputDir;
    private File outputFile;

    /*
     * Resource access user / pass
     */
    private String user;
    private String pass;
    //    private boolean resumeDownload = false;
    private String headerUserAgent = "Maven-Fetch-" + FetchTool.VERSION;

    /*
     * Proxy settings.  If proxyHost is not null, settings will be used. 
     */
    private String proxyHost = null;
    private String proxyUser = null;
    private String proxyPass = null;
    private int proxyPort = -1;

    public FetchRequest(String url)
    {
        this.url = url;
    }

    private void clearOutput()
    {
        this.outputFile = null;
        this.outputDir = null;
        this.outputStream = null;
    }

    public void setOutputFile(File outputFile)
    {
        clearOutput();
        this.outputFile = outputFile;
    }

    public void setOutputDir(File outputDir)
    {
        clearOutput();
        this.outputDir = outputDir;
    }

    public void setOutputStream(OutputStream outputStream)
    {
        clearOutput();
        this.outputStream = outputStream;
    }

    /**
     * @return
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @return
     */
    public File getOutputDir()
    {
        return outputDir;
    }

    /**
     * @return
     */
    public File getOutputFile()
    {
        return outputFile;
    }

    /**
     * @return
     */
    public OutputStream getOutputStream()
    {
        return outputStream;
    }

    /**
     * @return
     */
    public String getProxyHost()
    {
        return proxyHost;
    }

    /**
     * @return
     */
    public String getProxyPass()
    {
        return proxyPass;
    }

    /**
     * @return
     */
    public int getProxyPort()
    {
        return proxyPort;
    }

    /**
     * @return
     */
    public String getProxyUser()
    {
        return proxyUser;
    }

    /**
     * @param string
     */
    public void setProxyHost(String string)
    {
        proxyHost = string;
    }

    /**
     * @param string
     */
    public void setProxyPass(String string)
    {
        proxyPass = string;
    }

    /**
     * @param i
     */
    public void setProxyPort(int i)
    {
        proxyPort = i;
    }

    /**
     * @param string
     */
    public void setProxyUser(String string)
    {
        proxyUser = string;
    }

    public boolean isProxied()
    {
        return proxyHost != null;
    }

    /**
     * @return
     */
    public String getPass()
    {
        return pass;
    }

    /**
     * @return
     */
    public String getUser()
    {
        return user;
    }

    /**
     * @param string
     */
    public void setPass(String string)
    {
        pass = string;
    }

    /**
     * @param string
     */
    public void setUser(String string)
    {
        user = string;
    }

    public OutputStream getFinalOutputStream() throws IOException
    {
        if (outputStream != null)
        {
            return outputStream;
        }

        if (outputFile != null)
        {
            return new FileOutputStream(outputFile);
        }

        if (outputDir != null)
        {
            File f = File.createTempFile("fetch", ".tmp", outputDir);
            return new FileOutputStream(f);
        }

        throw new IllegalStateException("No appropriate output method specified in request");
    }

    /**
     * @return
     */
    public String getHeaderUserAgent()
    {
        return headerUserAgent;
    }

    /**
     * @param string
     */
    public void setHeaderUserAgent(String string)
    {
        headerUserAgent = string;
    }

    /**
     * 
     */
    private Date onlyIfModifiedSinceDate = null;

    public Date getOnlyIfModifiedSinceDate()
    {
        return onlyIfModifiedSinceDate;

    }

    /**
     * @param date
     */
    public void setOnlyIfModifiedSinceDate(Date date)
    {
        onlyIfModifiedSinceDate = date;
    }

}
