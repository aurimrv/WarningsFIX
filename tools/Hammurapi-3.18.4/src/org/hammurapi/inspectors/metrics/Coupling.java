/*
 * Hammurapi
 * Automated Java code review system.
 * Copyright (C) 2004  Johannes Bellert
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
 * URL: http://www.hammurapi.com
 * e-Mail: Johannes.Bellert@ercgroup.com
 *
 *  * Created on Apr 11, 2004
 *
 */
package org.hammurapi.inspectors.metrics;

import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.jsel.Parameter;
import com.pavelvlasov.jsel.Type;
import com.pavelvlasov.jsel.TypeBody;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.jsel.TypeSpecification;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.impl.ClassImpl;
import com.pavelvlasov.jsel.impl.ClassTypeSpecificationImpl;

/**
 * @author mucbj0
 *
 *	JDepend
 *
 *	Afferent Couplings (Ca)
 *		The number of other packages that depend upon classes within the package
 *		is an indicator of the package's responsibility.
 *
 *	Efferent Couplings (Ce)
 *		The number of other packages that the classes in the package depend upon
 *		is an indicator of the package's independence.
 *
 **/
public class Coupling {

	public Hashtable classCouplingMetric = new Hashtable();
	public Hashtable packageCouplingMetric = new Hashtable();
	public Hashtable allClasses = null;
	public Hashtable allMethodsImplemented = new Hashtable();
	public Hashtable allMethodsInvoked =  new Hashtable();

	public Hashtable allVariables = new Hashtable();
	

	//!! tbd: Set an meaningful name to the CouplingMetricOfPackage or use a different class
	private CouplingMetricOfPackage totalCouplingMetricOfProject = new CouplingMetricOfPackage("Project");

/*	public Coupling(ClassMap _classes,   File _projectBaseDir){
		super();
		allClasses =  _classes;
		projectBaseDir = _projectBaseDir;
	}

	public  void init(){
		logger.debug( "-> init "  );
		Enumeration enum = allClasses.elements();
		while (enum.hasMoreElements()){
			Type c =(Type)enum.nextElement();
			logger.debug( "Class " + c.toKey()  );
			CouplingMetricOfClass cmc = new CouplingMetricOfClass(c);
			this.classCouplingMetric.put( c.toKey(), cmc);
		}
		// logger.debug( "this.couplingMetric " + this.couplingMetric  );
		logger.debug( "<- init "  );
		return;
	};
*/
	
	
	//-------------------------------------------------------------------------

	public void incrementVariableCoupling( Parameter parameter){

		try {
		if (parameter.getTypeSpecification() instanceof ClassTypeSpecificationImpl ) {
				TypeSpecification ts = parameter.getTypeSpecification();
				CouplingMetricOfClass cmcParent = 
					(CouplingMetricOfClass)classCouplingMetric.get(	parameter.getParent().getEnclosingType().getFcn() );
				CouplingMetricOfClass cmcVar = (CouplingMetricOfClass)classCouplingMetric.get(	ts.getName() );
				if( cmcVar == null){
					classCouplingMetric.put( ts.getName(), new CouplingMetricOfClass( ts ) );
				}else{
					cmcVar.afferentVariableCounter++;
				}
				if( cmcParent == null){
					classCouplingMetric.put( parameter.getParent().getEnclosingType().getFcn(), 
							new CouplingMetricOfClass( parameter.getParent().getEnclosingType() ) );
				}else{
					cmcParent.efferentVariableCounter++;
				}
			//	System.out.println( cmcVar );
			//	System.out.println( cmcParent );
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	
	public String determineKey( VariableDefinition var){
		String str = "<undefined>";
		if( var != null){
			if( var.getParent() instanceof ClassImpl) {
				return ((ClassImpl)var.getParent()).getFcn();
			} else {
				// System.out.println(	((ClassImpl)var.getCompilationUnit().getTypes().get(0)).getFcn() );
			}
		}
		
		return str;
	}
	
	public void incrementVariableCoupling( VariableDefinition var){
		try {
			// System.out.println( ">> " + var.getTypeSpecification().getName() );
			// System.out.println( ">> " + var.getParent().getEnclosingType().getFcn());
			if( var != null){
		
				String key = determineKey( var) ; 
			CouplingMetricOfClass cmcParent = (CouplingMetricOfClass)classCouplingMetric.get(	key );
			CouplingMetricOfClass cmcVar = (CouplingMetricOfClass)classCouplingMetric.get(	var.getTypeSpecification().getName() );
			if( cmcVar == null){
				classCouplingMetric.put( var.getTypeSpecification().getName(), new CouplingMetricOfClass( var.getTypeSpecification() ) );
			}else{
				cmcVar.afferentVariableCounter++;
			}
			if( cmcParent == null){
				TypeBody parentEnclosingType = var.getParent().getEnclosingType();
				if (parentEnclosingType!=null) {
					classCouplingMetric.put( parentEnclosingType.getFcn(), new CouplingMetricOfClass( parentEnclosingType ) );
				}
			}else{
				cmcParent.efferentVariableCounter++;
			}
			// System.out.println( cmcVar );
			// System.out.println( cmcParent );
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	
	
//-------------------------------------------------------------------------	
	
	public void doIt(){
//		init();

		Enumeration classEnum = allClasses.elements();
		while (classEnum.hasMoreElements()){
			TypeDefinition c =(TypeDefinition)classEnum.nextElement();
			CouplingMetricOfClass thisCmc =  (CouplingMetricOfClass)this.classCouplingMetric.get( c.getFcn() );

//!! putAll reinitialize allMethodsImplemented !!
			// allMethodsImplemented.addAll(c.getMethodMap());
		
			//determineMethodMetricForClass( c, thisCmc);
			determineVariableMetricForClass( c, thisCmc);

		}
		this.aggregateToPackageMetric();
		this.findDefects();
		this.reporting();
		return;
	}


	public void determineVariableMetricForClass(
			TypeDefinition c,
		CouplingMetricOfClass thisCmc) {
//!! job		
/*		
		Enumeration enumMet = c.getMethodMap().elements();

		// -- all local variables
		while (enumMet.hasMoreElements()) {
			MethodImplemented mi = (MethodImplemented) enumMet.nextElement();

			Enumeration enumslvm = mi.getLocalVarMap().elements();
			while (enumslvm.hasMoreElements()) {
				Variable v = (Variable) enumslvm.nextElement();
				assignVariableCountersTo(v, thisCmc);
			}
		}

		Enumeration enumsivm = c.getInstanceVarMap().elements();
		while (enumsivm.hasMoreElements()) {
			Variable v = (Variable) enumsivm.nextElement();
			assignVariableCountersTo(v, thisCmc);
		}
*/		
	}

public void assignVariableCountersTo(VariableDefinition v, CouplingMetricOfClass thisCmc) {
	//!!
		CouplingMetricOfClass cmc = (CouplingMetricOfClass) this.classCouplingMetric.get(v.getName());
		// lazy intialization

		if (cmc == null) {
	//		cmc = initializeCouplingMetricOfClassFor(v.getName());
		}

		//!! not correcth
	//	cmc.listAfferentTypes.add(  (Type)v.getType() );
		cmc.afferentVariableCounter++;
	//	thisCmc.listEfferentTypes.add(thisCmc.type);
		thisCmc.efferentVariableCounter++;
	}

public void determineMethodMetricForClass(Type c, CouplingMetricOfClass thisCmc) {
	/*
	Enumeration enumMet = c.getMethodMap().elements();
	while (enumMet.hasMoreElements()) {
		MethodImplemented mi = (MethodImplemented) enumMet.nextElement();
		// logger.debug( "\t" +mi.toString() );

		Enumeration enumMk = mi.getInvokedMethods().elements();
		while (enumMk.hasMoreElements()) {
			MethodInvoked mk = (MethodInvoked) enumMk.nextElement();
			logger.debug("getSignature: " + mk.getSignature());
			logger.debug("getImplementorType: " + mk.getImplementorType());

			if (mk.getServiceVariable() != null && mk.getServiceVariable().getType() != null) {
				logger.debug("getServiceVariable: " + mk.getServiceVariable());
				logger.debug("mk.getServiceVariable().getType(): " + mk.getServiceVariable().getType());

				if (mk.getServiceVariable().getType().equals(c)) {
					thisCmc.reflectiveMethodCounter++;
				} else {
					CouplingMetricOfClass cmc =
						(CouplingMetricOfClass) this.classCouplingMetric.get(mk.getServiceVariable().getType().toKey());
					// lazy intialization

					if (cmc == null) {
						cmc = initializeCouplingMetricOfClassFor(mk.getServiceVariable().getType());
					}
		//!! not correct
					cmc.listAfferentTypes.add(c);
					cmc.afferentMethodCounter++;
					thisCmc.listEfferentTypes.add(mk.getServiceVariable().getType());
					thisCmc.efferentMethodCounter += mk.occurranceCounter;

					//-- thisCmc is per se in the project scope: no check needed
					//!! tbd: parameterize root package check
					if (cmc.type.getRootPackage() != null
						&& cmc.type.getRootPackage().getRootPackageName() != null
						&& (cmc.type.getRootPackage().getRootPackageName().startsWith("com.")
							|| cmc.type.getRootPackage().getRootPackageName().startsWith("org.apache.jsp"))) {
						cmc.afferentMethodCounterProjectInternal++;
						thisCmc.efferentMethodCounterProjectInternal += mk.occurranceCounter;
					}

					logger.debug("cmc.efferentMethodUnresolvedCounter++ " + cmc.type.getSignature());
				}
			} else {
				logger.debug("thisCmc.efferentMethodUnresolvedCounter++ " + thisCmc.type.getSignature());
				thisCmc.efferentMethodUnresolvedCounter++;
			}
		}
	}
	*/
}

	public void aggregateToPackageMetric() {
		/*
		String rootPack = "";
		Enumeration enum = classCouplingMetric.elements();
		while (enum.hasMoreElements()) {
			CouplingMetricOfClass cCmc =
				(CouplingMetricOfClass) enum.nextElement();
			try {
				if (cCmc.type != null
					&& cCmc.type.getRootPackage() != null
					&& cCmc.type.getRootPackage().getRootPackageName() != null) {

				rootPack = cCmc.type.getRootPackage().getRootPackageName();
					}
				if (rootPack != null && !"".equals(rootPack)) {

					logger.fatal("rootPack is: " + rootPack);
					CouplingMetricOfPackage pCmc =	(CouplingMetricOfPackage) this.packageCouplingMetric.get(rootPack);

					// lazy init
					if (pCmc == null) {
						logger.debug("Insert Entry: " + rootPack);
						pCmc = new CouplingMetricOfPackage(rootPack);
						this.packageCouplingMetric.put(rootPack, pCmc);
					}
					pCmc.aggregate(cCmc);
					this.totalCouplingMetricOfProject.aggregate(pCmc);
				}

			} catch (Exception e) {
				logger.fatal(
					"Issues in this.packageCouplingMetric.get(cmc.type.getRootPackage());",
					e);
			}
		}
		*/
	}

	public void findDefects(){
		Enumeration metricEnum = classCouplingMetric.elements();
		while (metricEnum.hasMoreElements()){
			CouplingMetricOfClass cmc =  (CouplingMetricOfClass)metricEnum.nextElement();
			// logger.debug(cmc.toXml());
		}
		Enumeration enump = packageCouplingMetric.elements();
		while (enump.hasMoreElements()){
			CouplingMetricOfPackage cmc =  (CouplingMetricOfPackage)enump.nextElement();
		}

	}
	public CouplingMetricOfClass initializeCouplingMetricOfClassFor(Type c){
		/*
		CouplingMetricOfClass cmc = new CouplingMetricOfClass(c);
		this.classCouplingMetric.put(c.toKey(), cmc);
		logger.debug( "<- initializeCouplingMetricOfClassFor");
		return cmc;
		*/
		return null;
	}

	public void reporting() {
		/*
		String extension = ".xml";
		String dir = ".";
		File outFile =
			new File(
				projectBaseDir,
				File.separatorChar
					+ "results"
					+ File.separatorChar
					+ "CouplingMetricOfPackage"
					+ extension);
		try {

			FileWriter fw = new FileWriter(outFile);
			fw.write("<Coupling>");
			fw.write("<ListCouplingMetricOfPackage>");
			fw.write("<ProjectSummary>");
			fw.write(this.totalCouplingMetricOfProject.toXml().toString());
			fw.write("</ProjectSummary>");
			fw.flush();
			Enumeration enump = packageCouplingMetric.elements();
			while (enump.hasMoreElements()) {
				CouplingMetricOfPackage cmc =
					(CouplingMetricOfPackage) enump.nextElement();
				fw.write(cmc.toXml().toString());
				fw.flush();
			}
			fw.write("</ListCouplingMetricOfPackage>");

			fw.write("<ListCouplingMetricOfClasses>");
			Enumeration enumc = classCouplingMetric.elements();
			while (enumc.hasMoreElements()) {
				CouplingMetricOfClass cmc =
					(CouplingMetricOfClass) enumc.nextElement();

				fw.write(cmc.toXml().toString());
				fw.flush();

			}
			fw.write("</ListCouplingMetricOfClasses>");
			fw.write("</Coupling>");
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

//
//				try {
//					outFile =
//										new File(
//											projectBaseDir,
//											File.separatorChar
//												+ "results"
//												+ File.separatorChar
//												+ "CouplingMetricOfClasses"
//
//												+ extension);
//
//					FileWriter fw = new FileWriter(outFile);
//					fw.write("<ListCouplingMetricOfClasses>");
//					Enumeration enump = classCouplingMetric.elements();
//					while (enump.hasMoreElements()) {
//						CouplingMetricOfClass cmc =
//							(CouplingMetricOfClass) enump.nextElement();
//
//						fw.write(cmc.toXml().toString());
//						fw.flush();
//
//					}
//					fw.write("</ListCouplingMetricOfClasses>");
//					fw.close();
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
		logger.debug("<- reporting ");
	*/
	}
	
	
	public Element toDom(Document document){

		Element root=document.createElement("Coupling");
		Element ret=document.createElement("ListCouplingMetricOfClasses");
		root.appendChild(ret);
		
		
		ret.setAttribute("size", String.valueOf(this.classCouplingMetric.size()));
		Enumeration enumc = this.classCouplingMetric.elements();
		while (enumc.hasMoreElements()) {
			CouplingMetricOfClass cmc =
				(CouplingMetricOfClass) enumc.nextElement();
			ret.appendChild(cmc.toDom(document));
		}
		return ret;
		
	}
	
}

