/*-- 

 $Id: DOMOutputter.java,v 1.43 2007/11/10 05:29:01 jhunter Exp $

 Copyright (C) 2000-2007 Jason Hunter & Brett McLaughlin.
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows 
    these conditions in the documentation and/or other materials 
    provided with the distribution.

 3. The name "JDOM" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact <request_AT_jdom_DOT_org>.
 
 4. Products derived from this software may not be called "JDOM", nor
    may "JDOM" appear in their name, without prior written permission
    from the JDOM Project Management <request_AT_jdom_DOT_org>.
 
 In addition, we request (but do not require) that you include in the 
 end-user documentation provided with the redistribution and/or in the 
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by the
      JDOM Project (http://www.jdom.org/)."
 Alternatively, the acknowledgment may be graphical using the logos 
 available at http://www.jdom.org/images/logos.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE JDOM AUTHORS OR THE PROJECT
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 This software consists of voluntary contributions made by many 
 individuals on behalf of the JDOM Project and was originally 
 created by Jason Hunter <jhunter_AT_jdom_DOT_org> and
 Brett McLaughlin <brett_AT_jdom_DOT_org>.  For more information
 on the JDOM Project, please see <http://www.jdom.org/>.
 
 */


package lsp.test.org.jdom.output;

import java.util.*;

import lsp.test.org.jdom.*;
import lsp.test.org.jdom.adapters.*;



/**
 * Outputs a JDOM {@link lsp.test.org.jdom.Document org.jdom.Document} as a DOM {@link
 * lsp.test.org.jdom.Document lsp.test.org.jdom.Document}.
 *
 * @version $Revision: 1.43 $, $Date: 2007/11/10 05:29:01 $
 * @author  Brett McLaughlin
 * @author  Jason Hunter
 * @author  Matthew Merlo
 * @author  Dan Schaffer
 * @author  Yusuf Goolamabbas
 * @author  Bradley S. Huffman
 */
public class DOMOutputter {

    private static final String CVS_ID = 
      "@(#) $RCSfile: DOMOutputter.java,v $ $Revision: 1.43 $ $Date: 2007/11/10 05:29:01 $ $Name:  $";

    /** Default adapter class */
    private static final String DEFAULT_ADAPTER_CLASS =
        "org.jdom.adapters.XercesDOMAdapter";

    /** Adapter to use for interfacing with the DOM implementation */
    private String adapterClass;

    /** Output a DOM with namespaces but just the empty namespace */
    private boolean forceNamespaceAware;

    /**
     * This creates a new DOMOutputter which will attempt to first locate
     * a DOM implementation to use via JAXP, and if JAXP does not exist or
     * there's a problem, will fall back to the default parser.
     */
    public DOMOutputter() {
        // nothing
    }

    /**
     * This creates a new DOMOutputter using the specified DOMAdapter
     * implementation as a way to choose the underlying parser.
     *
     * @param adapterClass <code>String</code> name of class
     *                     to use for DOM output
     */
    public DOMOutputter(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    /**
     * Controls how NO_NAMESPACE nodes are handeled. If true the outputter
     * always creates a namespace aware DOM.
     * @param flag
     */
    public void setForceNamespaceAware(boolean flag) {
        this.forceNamespaceAware = flag;
    }

    /**
     * Returns whether DOMs will be constructed with namespaces even when
     * the source document has elements all in the empty namespace.
     * @return the forceNamespaceAware flag value
     */
    public boolean getForceNamespaceAware() {
        return forceNamespaceAware;
    }

    /**
     * This converts the JDOM <code>Document</code> parameter to a 
     * DOM Document, returning the DOM version.  The DOM implementation
     * is the one chosen in the constructor.
     *
     * @param document <code>Document</code> to output.
     * @return an <code>lsp.test.org.jdom.Document</code> version
     */
    public lsp.test.org.jdom.Document output(Document document)
                                       throws JDOMException {
        NamespaceStack namespaces = new NamespaceStack();

        lsp.test.org.jdom.Document domDoc = null;
        try {
            // Assign DOCTYPE during construction
            DocType dt = document.getDocType();
            domDoc = createDOMDocument(dt);

            // Add content
            Iterator itr = document.getContent().iterator();
            while (itr.hasNext()) {
                Object node = itr.next();

                if (node instanceof Element) {
                    Element element = (Element) node;
                    org.w3c.dom.Element domElement =
                        output(element, domDoc, namespaces);
                }
                else if (node instanceof Comment) {
                }
                else if (node instanceof ProcessingInstruction) {
                }
                else if (node instanceof DocType) {
                    // We already dealt with the DocType above
                }
                else {
                    throw new JDOMException(
                        "Document contained top-level content with type:" +
                        node.getClass().getName());
                }
            }
        }
        catch (Throwable e) {
            throw new JDOMException("Exception outputting Document", e);
        }

        return domDoc;
    }

    private lsp.test.org.jdom.Document createDOMDocument(DocType dt)
                                       throws JDOMException {
        if (adapterClass != null) {
            // The user knows that they want to use a particular impl
            try {
                DOMAdapter adapter =
                    (DOMAdapter)Class.forName(adapterClass).newInstance();
                // System.out.println("using specific " + adapterClass);
                return adapter.createDocument(dt);
            }
            catch (ClassNotFoundException e) {
                // e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                // e.printStackTrace();
            }
            catch (InstantiationException e) {
                // e.printStackTrace();
            }
        }
        else {
            // Try using JAXP...
            try {
                DOMAdapter adapter =
                    (DOMAdapter)Class.forName(
                    "org.jdom.adapters.JAXPDOMAdapter").newInstance();
                // System.out.println("using JAXP");
                return adapter.createDocument(dt);
            }
            catch (ClassNotFoundException e) {
                // e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                // e.printStackTrace();
            }
            catch (InstantiationException e) {
                // e.printStackTrace();
            }
        }

        // If no DOM doc yet, try to use a hard coded default
        try {
            DOMAdapter adapter = (DOMAdapter)
                Class.forName(DEFAULT_ADAPTER_CLASS).newInstance();
            return adapter.createDocument(dt);
            // System.out.println("Using default " +
            //   DEFAULT_ADAPTER_CLASS);
        }
        catch (ClassNotFoundException e) {
            // e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            // e.printStackTrace();
        }
        catch (InstantiationException e) {
            // e.printStackTrace();
        }

        throw new JDOMException("No JAXP or default parser available");
        
    }

    private org.w3c.dom.Element output(Element element,
                                         lsp.test.org.jdom.Document domDoc,
                                         NamespaceStack namespaces)
                                         throws JDOMException {
    	return null;
    }

    private org.w3c.dom.Attr output(Attribute attribute,
                                      lsp.test.org.jdom.Document domDoc)
                                      throws JDOMException {
    	return null;
    }

    /**
     * This will handle adding any <code>{@link Namespace}</code>
     * attributes to the DOM tree.
     *
     * @param ns <code>Namespace</code> to add definition of
     */
    private static String getXmlnsTagFor(Namespace ns) {
        String attrName = "xmlns";
        if (!ns.getPrefix().equals("")) {
            attrName += ":";
            attrName += ns.getPrefix();
        }
        return attrName;
    }
}
