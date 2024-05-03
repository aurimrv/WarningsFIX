/*
  * Hammurapi
 * Automated Java code review system. 
 * Copyright (C) 2004  Hammurapi Group
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * URL: http://www.hammurapi.org
 * e-Mail: support@hammurapi.biz

  */
package org.hammurapi;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Buffers source stream.
 * @author Pavel Vlasov
 *
 * @version $Revision: 1.2 $
 */
public abstract class BufferedReviewRequest extends ReviewRequestBase {
	private String source;
	private String name;
	
	/**
	 * Reads input into buffer. 
	 * @throws IOException
	 */
	public BufferedReviewRequest(ClassLoader classLoader, Reader in, String name) throws IOException {
		super(classLoader);
		this.name=name;
		StringWriter sw=new StringWriter();
		char[] buf=new char[4096];
		int l;
		while ((l=in.read(buf))!=-1) {
			sw.write(buf, 0, l);
		}
		in.close();
		sw.close();
		source=sw.toString();
	}

	public Reader getSource() {
		return new StringReader(source);
	}

	public String getName() {
		return name;
	}
}
