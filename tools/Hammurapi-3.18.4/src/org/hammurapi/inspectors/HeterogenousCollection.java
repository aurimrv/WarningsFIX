/*
 * Hammurapi
 * Automated Java code review system.
 * Copyright (C) 2005  Johannes Bellert
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
 * e-Mail: CraftOfObjects@gmail.com

 *	FIX 2: Added additional parameter types of add-methods	
 */

package org.hammurapi.inspectors;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.OperationInfo;
import com.pavelvlasov.jsel.TypeSpecification;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.expressions.Dot;
import com.pavelvlasov.jsel.expressions.Ident;
import com.pavelvlasov.jsel.expressions.IndexOperation;
import com.pavelvlasov.jsel.expressions.IntegerConstant;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.jsel.expressions.NewObject;
import com.pavelvlasov.jsel.expressions.Plus;
import com.pavelvlasov.jsel.expressions.StringConstant;
import com.pavelvlasov.jsel.expressions.TypeCast;
import com.pavelvlasov.jsel.impl.AST;
import com.pavelvlasov.review.SourceMarker;

public class HeterogenousCollection extends InspectorBase {
	
	private Hashtable currentCollectionInstanceVariables = new Hashtable();

	private Hashtable currentCollectionLocalVariables = new Hashtable();

	
	public void init() {
		currentCollectionInstanceVariables = new Hashtable();
		currentCollectionLocalVariables = new Hashtable();
	}

	public void visit(com.pavelvlasov.jsel.Class type) {
		init();
	}

	public void visit(VariableDefinition element) {

		try {
			TypeSpecification ts = element.getTypeSpecification();
			if (element.getTypeSpecification().isKindOf("java.util.Collection")) {
				// Collection: Vector implements List
				// is this a instance or class variable?

				LanguageElement el = element.getParent();
				if (el.getClass().equals(com.pavelvlasov.jsel.impl.ClassImpl.class)) {
					// instance variable
					this.currentCollectionInstanceVariables.put(element.getName(), new ColletionMethodAdd());
				} else {
					this.currentCollectionLocalVariables.put(element.getName(), new ColletionMethodAdd());
				}
			}
		} catch (Exception e) {
			context.warn(element, e);
		}

	}

	public boolean visit(MethodCall methodCall) throws JselException {

		if ("add".equals(methodCall.getMethodName()) || "addAll".equals(methodCall.getMethodName())) {
			String key = fetchProviderVariable(((LanguageElement) methodCall).getAst());
			determineHetergenousViolationFor(key, methodCall);
		}
		return true;
	}

	private void determineHetergenousViolationFor(String key, MethodCall methodCall) {
		List parameterList = methodCall.getParameters();

		Iterator pit = parameterList.iterator();
		while (pit.hasNext()) {
			Object parameter = pit.next();
			String typeDef = checkParameterTypeOf(parameter);
			if ("int".equals(typeDef)) {
				// do nothing
			} else {
				checkVariableRegistryFor(this.currentCollectionLocalVariables, key, typeDef, methodCall);
				checkVariableRegistryFor(this.currentCollectionInstanceVariables, key, typeDef, methodCall);
			}
		}
	}

	public void checkVariableRegistryFor(Hashtable ht, String key, String typeDef, MethodCall methodCall) {

		SourceMarker srcM = (SourceMarker) methodCall;
		LanguageElement parent = ((LanguageElement) methodCall).getParent();
		ColletionMethodAdd value = (ColletionMethodAdd) ht.get(key);
		if (value != null && value.isInitialize()) {
			value.typeDef = typeDef;
			value.callerMethodName = parent.getSignature();
			value.lineNumber = srcM.getLine();
			value.occurance++;

			ht.put(key, value);
		} else if (value != null && value.occurance > 0) {
			value.occurance++;
			context.debug(srcM, parent.getSignature() + " >> " + value.callerMethodName);
			if ((3 > (srcM.getLine() - value.lineNumber))) {
				context.getSession().getContext("ER-215").reportViolation(srcM,
						 "Collection " + key + " got " + value.occurance + " add-messages with parameter type " + value.typeDef );

			}
		}
		if (value != null && !value.isInitialize() && !typeDef.equals(value.typeDef)) {
			// context.reportViolation(srcM);
			context.getSession().getContext("ER-214").reportViolation(srcM,
					typeDef + " is different from  " + value.typeDef);
		} else {
			// ignore .. probably wrong variable registry
		}
	}

	public String fetchProviderVariable(AST myAST) {
		String errorRet = "<cant-determine-service-providing variable>";
		if (myAST.getFirstToken() != null && myAST.getFirstToken().getText() != null) {
			return myAST.getFirstToken().getText();
		} else {
			// System.out.println(errorRet);
			return errorRet;
		}
	}

	public String checkParameterTypeOf(Object parameter) {

		try {
			if (parameter != null && parameter instanceof Ident) {
				Ident p = (Ident) parameter;
				return p.getTypeSpecification().getName();
			} else if (parameter != null && parameter instanceof StringConstant) {
				return "java.lang.String";
			} else if (parameter != null && parameter instanceof MethodCall) {
				MethodCall p = (MethodCall) parameter;
				OperationInfo opi = p.getProvider();
				return opi.getReturnType().getName();
			} else if (parameter != null && parameter instanceof Dot) {
				Dot p = (Dot) parameter;
				return p.getTypeSpecification().getName();
				
			} else if (parameter != null && parameter instanceof IndexOperation) {
				IndexOperation p = (IndexOperation) parameter;
				return p.getTypeSpecification().getName();
				
			} else if (parameter != null && parameter instanceof TypeCast) {
				TypeCast p = (TypeCast) parameter;
				return p.getTypeSpecification().getName();
				
				
			} else if (parameter != null && parameter instanceof Plus) {
				Plus plus = (Plus)parameter;
				/*
				Collection exprList = plus.getOperands();
				Iterator it = exprList.iterator();
				while ( it.hasNext()){
					Object obj = (Object)it.next();
					System.out.println( "++ " +obj.getClass().toString());
				}
				*/
				return "java.lang.String";
				
				
			} else if (parameter != null && parameter instanceof NewObject) {
				NewObject p = (NewObject) parameter;
				return p.getTypeSpecification().getName();
			} else if (parameter != null && parameter instanceof IntegerConstant) {
				return "int";
			}
		} catch (Exception e) {
			context.warn((SourceMarker) parameter, e);
		}
		context.warn((SourceMarker) parameter, "Problem to determine parameters type " + parameter.toString());
		return "<parameter-type-not-determined>";
	}

	class ColletionMethodAdd {
		public int occurance = 0;

		public String typeDef = "";

		public String callerMethodName = "";

		public int lineNumber = 0;

		public boolean isInitialize() {
			return (occurance == 0) && "".equals(callerMethodName) && (lineNumber == 0);
		}

	}

}
