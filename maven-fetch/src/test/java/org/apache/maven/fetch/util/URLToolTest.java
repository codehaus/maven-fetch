/*
 * Created on 18/05/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.apache.maven.fetch.util;

import junit.framework.TestCase;

/**
 * @author <a href="bwalding@jakarta.org">Ben Walding</a>
 * @version $Id$
 */
public class URLToolTest extends TestCase
{

    /**
     * Test the function that will parse an url which contains
     * a username and password into its constituent parts.
     */
    public void testParseUrl1() throws Exception
    {
        String url = "http://username:password@repository.mycompany.com";
        String[] up = URLTool.parseUrl(url);

        assertEquals("username", up[0]);
        assertEquals("password", up[1]);
        assertEquals("http://repository.mycompany.com", up[2]);
    }

    public void testParseUrl2() throws Exception
    {
        String url = "http://repository.mycompany.com";
        String[] up = URLTool.parseUrl(url);

        assertNull(up[0]);
        assertNull(up[1]);
        assertEquals("http://repository.mycompany.com", up[2]);
    }
}
