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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.fetch.FetchRequest;
import org.apache.maven.fetch.exceptions.FetchException;
import org.apache.maven.fetch.exceptions.NotModifiedFetchException;
import org.apache.maven.fetch.exceptions.ResourceNotFoundFetchException;
import org.apache.maven.fetch.util.IOUtility;

/**
 * @author <a href="bwalding@jakarta.org">Ben Walding</a>
 * @version $Id$
 */
public class FileFetcher implements Fetcher
{

    /**
     * @see org.apache.maven.fetch.fetchers.Fetcher#fetchUrl(java.lang.String, java.io.OutputStream)
     */
    public void fetchUrl(FetchRequest request) throws FetchException
    {

        try
        {
            URL url = new URL(request.getUrl());
            String filename = url.getPath();
            if (filename.length() > 3)
            {
                if (filename.startsWith("/") && filename.charAt(2) == ':' && filename.charAt(3) == '/')
                {
                    filename = filename.substring(1);

                }
            }
            File f = new File(filename);

            if (!f.exists())
            {
                throw new ResourceNotFoundFetchException("Couldn't find " + f);
            }

            if (request.getOnlyIfModifiedSinceDate() != null)
            {
                long ftime = f.lastModified();
                if (ftime > 0 && ftime > request.getOnlyIfModifiedSinceDate().getTime())
                {
                    //Actually get the file
                    OutputStream os = request.getFinalOutputStream();
                    InputStream is = new FileInputStream(f);
                    IOUtility.transferStream(is, os);
                    IOUtility.close(os);
                    IOUtility.close(is);
                }
                else
                {
                    throw new NotModifiedFetchException("Not modified");
                }
            }
            else
            {
                //              Actually get the file
                OutputStream os = request.getFinalOutputStream();
                InputStream is = new FileInputStream(f);
                IOUtility.transferStream(is, os);
                IOUtility.close(os);
                IOUtility.close(is);
            }

        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
