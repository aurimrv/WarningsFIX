/*
 * Created on May 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hammurapi.inspectors.metrics;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * @author MUCBJ0
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JspDescriptor {
	public CodeMetric codeMetric = null;
	public int numberOfJavaScriptSnippets = 0;
	public int numberOfJavaScriptFunctions = 0;
	
	public int numberOfPopUpCalls = 0;
	public int numberOfOutWriteOps = 0;
	public int numberOfOutPrintOps = 0;
	public int numberOfJavaScriptSubmits = 0;
	public Vector listOfJavaScriptCalledJsp = new Vector();
	public Vector listOfInvokedJsp = new Vector();
	public Vector listOfSqlStrings = new Vector();
	//	 <form name=\"frmTresuryUpd\" action=\"eap_treasury_update_list.jsp\" >
	public void analyseHtmlForm(String jsStream) {
		try {
			int actionIndex = jsStream.indexOf("action=");
			int jspSuffixIndex = jsStream.indexOf(".jsp");
			int closeIndex = jsStream.indexOf(">");
			if (actionIndex > -1 && jspSuffixIndex > -1 && closeIndex > -1
					&& closeIndex > jspSuffixIndex) {
				String referenceStr = jsStream.substring(actionIndex + 8,
						jspSuffixIndex + 3);
				JspXref jXref = new JspXref(this, "HTML form action",
						referenceStr);
				this.listOfInvokedJsp.add(jXref);
			}
		} catch (Exception e) {
			System.out.println(jsStream);
			e.printStackTrace();
		}
	}
	public void analyseJavaScriptPortion(String jsStream) {
		
		String tmpJSStream = jsStream;
		int subCount = tmpJSStream.toCharArray().length;

		while ((subCount = tmpJSStream.lastIndexOf("function ", subCount-1)) != -1) {
			numberOfJavaScriptFunctions++;
			tmpJSStream = tmpJSStream.substring(0, subCount);
		}
		
		tmpJSStream = jsStream;
		subCount = tmpJSStream.toCharArray().length;
		
		while ((subCount = tmpJSStream.lastIndexOf(".submit()", subCount-1)) != -1) {
			numberOfJavaScriptSubmits++;
			tmpJSStream = tmpJSStream.substring(0, subCount);
		}
		
/*		if (jsStream.indexOf("function ") > -1) {
			System.out.println("amit-Script function - " + jsStream);
			numberOfJavaScriptFunctions++;
			System.out.println("amit-numberOfJavaScriptFunctions - " + numberOfJavaScriptFunctions);
		}
		
		if (jsStream.indexOf(".submit()") > -1) {
			numberOfJavaScriptSubmits++;
		}
*/		
		
		int startWinOpenerIndex = jsStream.indexOf("window.opener");
		int startWinOpenIndex = jsStream.indexOf("window.open");
		int startJspIndex = jsStream.indexOf(".jsp");
		int endWinOpenIndex = jsStream.indexOf(")");
		if (startWinOpenIndex > -1 && startWinOpenerIndex == -1) {
			numberOfPopUpCalls++;
		}
		if (startWinOpenIndex > -1 && startWinOpenerIndex == -1
				&& startJspIndex > -1 && startJspIndex < endWinOpenIndex) {
			findJspXref(startWinOpenIndex, jsStream);
		}
		int startActionIndex = jsStream.indexOf(".action");
		if (startActionIndex > -1) {
			findJspXref(startActionIndex, jsStream);
		}
	}
	
	private void findJspXref(int startIndex, String jsStream) {
		try {
			int startDoubleQuote = jsStream.indexOf("\"", startIndex);
			int endDoubleQuote = jsStream.indexOf("\"", startDoubleQuote + 1);
			String referenceStr = jsStream.substring(startDoubleQuote,
					endDoubleQuote);
			// System.out.println("* " + startActionIndex+ ", " +
			// startDoubleQuote+ ", "
			// +endDoubleQuote+ ", " + referenceStr);
			if (referenceStr.indexOf(".jsp") > -1) {
				listOfJavaScriptCalledJsp.add(new JspXref(this,
						"Java Script Action", referenceStr));
				System.out.println(referenceStr);
			}
		} catch (Exception e) {
			System.out.println(jsStream);
			e.printStackTrace();
		}
	}
	public Element toDom(Document document) {
		
		Element ret = document.createElement("JspDescriptor");
		ret.appendChild(this.codeMetric.toDom(document));
		ret.setAttribute("numberOfJavaScriptSnippets", String
				.valueOf(numberOfJavaScriptSnippets));
		ret.setAttribute("numberOfJavaScriptFunctions", String
				.valueOf(numberOfJavaScriptFunctions));
		ret.setAttribute("numberOfPopUpCalls", String
				.valueOf(numberOfPopUpCalls));
		ret.setAttribute("numberOfOutWriteOps", String
				.valueOf(numberOfOutWriteOps));
		ret.setAttribute("numberOfOutPrintOps", String
				.valueOf(numberOfOutPrintOps));
		ret.setAttribute("numberOfJavaScriptSubmits", String
				.valueOf(numberOfJavaScriptSubmits));
		Iterator it = listOfInvokedJsp.iterator();
		while (it.hasNext()) {
			//!! use a reference Descriptor Obj
			JspXref jspX = (JspXref) it.next();
			ret.appendChild(jspX.toDom(document));
		}
		it = listOfJavaScriptCalledJsp.iterator();
		while (it.hasNext()) {
			//!! use a reference Descriptor Obj
			JspXref jspX = (JspXref) it.next();
			ret.appendChild(jspX.toDom(document));
		}
		it = listOfSqlStrings.iterator();
		while (it.hasNext()) {
			//!! use a reference Descriptor Obj
			Element jspRef = document.createElement("SqlString");
			jspRef.setAttribute("name", (String) it.next());
			ret.appendChild(jspRef);
		}
		return ret;
	}
}
