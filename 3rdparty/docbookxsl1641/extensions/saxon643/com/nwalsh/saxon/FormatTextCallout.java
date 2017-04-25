package com.nwalsh.saxon;

import org.xml.sax.SAXException;
import org.w3c.dom.*;

import javax.xml.transform.TransformerException;

import com.icl.saxon.om.NamePool;
import com.icl.saxon.output.Emitter;

import com.nwalsh.saxon.Callout;

/**
 * <p>Utility class for the Verbatim extension (ignore this).</p>
 *
 * <p>$Id: //depot/dev/bt-open-source/3rdparty/docbookxsl1641/extensions/saxon643/com/nwalsh/saxon/FormatTextCallout.java#1 $</p>
 *
 * <p>Copyright (C) 2000, 2001 Norman Walsh.</p>
 *
 * <p><b>Change Log:</b></p>
 * <dl>
 * <dt>1.0</dt>
 * <dd><p>Initial release.</p></dd>
 * </dl>
 *
 * @author Norman Walsh
 * <a href="mailto:ndw@nwalsh.com">ndw@nwalsh.com</a>
 *
 * @see Verbatim
 *
 * @version $Id: //depot/dev/bt-open-source/3rdparty/docbookxsl1641/extensions/saxon643/com/nwalsh/saxon/FormatTextCallout.java#1 $
 **/

public class FormatTextCallout extends FormatCallout {
  public FormatTextCallout(NamePool nPool, boolean fo) {
    super(nPool, fo);
  }

  public void formatCallout(Emitter rtfEmitter,
			    Callout callout) {
    formatTextCallout(rtfEmitter, callout);
  }
}
