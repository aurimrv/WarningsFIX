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
 * URL: http://www.pavelvlasov.com/pv/content/menu.show?id=products.jtaste
 * e-Mail: Johannes.Bellert@gmail.com
 */
package org.hammurapi.inspectors.metrics;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.hammurapi.HammurapiException;
import org.hammurapi.InspectorBase;
import org.hammurapi.results.AnnotationContext;
import org.hammurapi.results.LinkedAnnotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.Interface;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.Parameter;
import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.jsel.TypeBody;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.jsel.TypeSpecification;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.impl.ClassTypeSpecificationImpl;
import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.render.dom.AbstractRenderer;
import com.pavelvlasov.review.SourceMarker;

public class ArchitecturalLayerInspector 
	extends InspectorBase 
	implements ArchitecturalLayerConstants, Parameterizable {

	private static final String CONFIG_ERROR_MESSAGE = "'mapping-configuration-file', " +
			"'mapping-configuration-resource', and 'mapping-configuration-url' " +
			"parameters are mutually exclusive";
	
	private ArchitecturalLayerMappingTable architecturalLayerMappingTable = null;
	private ArchitecturalLayerExtensionsMap extensionMap =  new ArchitecturalLayerExtensionsMap();
	private Hashtable allCategorizedPackages = new Hashtable();
	private Hashtable allCategorizedClasses = new Hashtable();
	private Hashtable allGetterSetterCounterForClasses = new Hashtable();
	private ListOfLayers listOfLayers = null;
	private Hashtable allTechStackEntitiesTable = new Hashtable();
	private ArchitecturalComplexityMappingTable complexityMappingTable = null;
	private HashSet unknownVariableList = new HashSet();

	private Coupling coupling = new Coupling();
	private CodeMetric projectMetric = new CodeMetric();
	String projectBaseDir = "";
	String xmlResourceName=this.getClass().getName().replace('.','/')+  "XmlPretty.xsl";
	Repository repository = null;
	
	private String mappingConfigurationPath;
	private String mappingConfigurationResource;
	private String mappingConfigurationUrl;
				
	public void init() throws HammurapiException {
		
		try {
			InputStream inspectorStream;
			if (mappingConfigurationPath!=null) {
				inspectorStream=new FileInputStream(this.mappingConfigurationPath);
			} else if (mappingConfigurationUrl!=null) {
				inspectorStream=new URL(mappingConfigurationUrl).openStream();
			} else if (mappingConfigurationResource!=null) {
				inspectorStream=getClass().getClassLoader().getResourceAsStream(mappingConfigurationResource);
				if (inspectorStream==null) {
					throw new HammurapiException("Resource not found: "+mappingConfigurationResource);
				}
			} else {
				String className = getClass().getName();
				int idx=className.lastIndexOf('.');
				String rName = (idx==-1 ? className : className.substring(idx+1))+".xml";				
				inspectorStream=getClass().getResourceAsStream(rName);
				if (inspectorStream==null) {
					throw new HammurapiException("Default mappig not found: "+rName);
				}
			}
	
			DomArchitecturalMappingSource source=new DomArchitecturalMappingSource(inspectorStream);
			this.architecturalLayerMappingTable = source.loadLayerMappings();
			this.complexityMappingTable = source.loadComplexityMapping();
			Enumeration enumAlm =  architecturalLayerMappingTable.elements();
			
				while( enumAlm.hasMoreElements()){
					ArchitecturalLayerMapping alm = (ArchitecturalLayerMapping)enumAlm.nextElement();
					
						Enumeration stackEnum =  alm.getTechStackEntityList().elements();
			
				while( stackEnum.hasMoreElements()){
					TechStackEntity te = (TechStackEntity)stackEnum.nextElement();
					for( int i=0; i< te.getExtensionMapping().size(); i++){ 
						// System.out.println( (String)te.getExtensionMapping().elementAt(i) + " >-> "+ te.getName());
						this.extensionMap.put( (String)te.getExtensionMapping().elementAt(i), te.getName());
					}
				}
				}
				Collection almList = architecturalLayerMappingTable.values();
				Iterator it = almList.iterator();
				
				
				 while(it.hasNext()){
				//for( int i=0; i<almList.size(); i++){
					 // ArchitecturalLayerMapping  alm = (ArchitecturalLayerMapping)architecturalLayerMappingTable.get((String)it.next());
					ArchitecturalLayerMapping  alm = (ArchitecturalLayerMapping)it.next();
					
					// allTechStackEntitiesSet.addAll(alm.getTechStackEntityListValues());
					Collection clte = alm.getTechStackEntityListValues();
					Iterator itClte = clte.iterator();
					while(itClte.hasNext()){
						TechStackEntity te = (TechStackEntity)itClte.next();
						allTechStackEntitiesTable.put( te.getName(), te);
					}
				 }
				
		} catch (IOException e) {
			throw new HammurapiException("Cannot load mapping configuration: "+e, e);
		}
	}
	
	public void visit(com.pavelvlasov.jsel.Package p) {
		if ( "".equals( p.getName()) ){
			allCategorizedPackages.put( "<default>", new ListOfPackageCategories( p.getName()  ));
		}else {
			allCategorizedPackages.put(p.getName(), new ListOfPackageCategories( p.getName()  ));
			
		}
	}

	public void visit(TypeDefinition p)  throws JselException, ClassNotFoundException {
		
		this.coupling.classCouplingMetric.put( p.getFcn(), new CouplingMetricOfClass(p));
		allCategorizedClasses.put(p.getFcn(), new ListOfCategories( p.getFcn(), ((SourceMarker)p).getSourceURL(), ((SourceMarker)p).getLine(), ((SourceMarker)p).getColumn() ));
		this.identifyCategories(p);

	}

	public void visit(Interface p)  throws JselException, ClassNotFoundException {
		this.coupling.classCouplingMetric.put( p.getFcn(), new CouplingMetricOfClass(p));
		allCategorizedClasses.put(p.getFcn(),  new ListOfCategories( p.getFcn(), ((SourceMarker)p).getSourceURL(),((SourceMarker)p).getLine(), ((SourceMarker)p).getColumn() ));
		addCategory("Interface", p.getName(), p);
	}

	
	//!! job: read from XML descriptor
	public void checkExtentType( TypeDefinition type )throws JselException, ClassNotFoundException {

		Enumeration keys = extensionMap.keys();
		while ( keys.hasMoreElements() ){
			String key = (String)keys.nextElement();
			String category = extensionMap.getProperty(key);
			checkExtendTypeHelper( key, category, type);	
		}
	}

	//!! job: remember tokens which raise a ClassNotFounds and remove from list, for performance optimization
	private void checkExtendTypeHelper	(String _loadClass, String category, TypeDefinition type) {
		try {
			if ( type.isKindOf(_loadClass) ) {
				addCategory(category, _loadClass, type);
			}
		} catch (JselException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/*
	 *
	 * Constant Identifier missing
	 * 23public class Constants {
24    //~ Static fields/initializers ---------------------------------------------
25
26    public static final int PROCESS_STATUS_NOT_SUBMITTED = 1;
27    public static final int PROCESS_STATUS_OPEN = 2;

	 *
	 *
	 */
	public void checkVariableType(TypeSpecification typDef, TypeDefinition declarationType ) throws JselException, ClassNotFoundException {
		boolean foundAnyCategory = false;
		Iterator it = this.architecturalLayerMappingTable.values().iterator();
		
		while (it.hasNext() && !foundAnyCategory){
			
			ArchitecturalLayerMapping alm = (ArchitecturalLayerMapping)it.next();
			Iterator stackEnum = alm.getTechStackEntityListValues().iterator();
			while (stackEnum.hasNext() && !foundAnyCategory){	
				TechStackEntity tse = (TechStackEntity)stackEnum.next();
				foundAnyCategory = iterateThroughVarDefSearchStrings(tse.getVariableMapping(), typDef, tse.getName(), declarationType);
			}
		}
		if ( foundAnyCategory ){
			// I have a dam clue about this var
		} else {
			if ( nonTrivialTypeFilter( typDef ) ){
			unknownVariableList.add( typDef.getName() );
		} 
		} 

	}
	public void checkVariableType(TypeSpecification typDef, TypeBody declarationType ) throws JselException, ClassNotFoundException {
		boolean foundAnyCategory = false;
		Iterator it = this.architecturalLayerMappingTable.values().iterator();
	
		while (it.hasNext() && !foundAnyCategory){
			
			ArchitecturalLayerMapping alm = (ArchitecturalLayerMapping)it.next();
			Iterator stackEnum = alm.getTechStackEntityListValues().iterator();
			while (stackEnum.hasNext() && !foundAnyCategory){	
				TechStackEntity tse = (TechStackEntity)stackEnum.next();
				foundAnyCategory = iterateThroughVarDefSearchStrings(tse.getVariableMapping(), typDef, tse.getName(), declarationType);
		}
		}
		if ( foundAnyCategory ){
			// THIS means, I have a dam clue about this var
		} else {
			if ( nonTrivialTypeFilter( typDef ) ) {
			unknownVariableList.add( typDef.getName() );
		} 
		} 

	}

	public boolean iterateThroughVarDefSearchStrings( Vector searchList, TypeSpecification typeDef, String category, TypeDefinition declarationType)
	throws JselException {
	
		boolean foundAnyCategory = false;
		for ( int i = 0; i  < searchList.size(); i++){
		String searchStr = (String)searchList.elementAt(i);

		if("*".equals(searchStr.substring(searchStr.length()-1) ) ){
			String packageStr = searchStr.substring(0, searchStr.length()-1);

			if ( typeDef.getName().startsWith(packageStr  ) ){
				addCategory(category, typeDef.getName(), typeDef,   declarationType);
				foundAnyCategory = true;
			}
		}else	if (searchStr.equals(typeDef.getName())) {
			addCategory(category, searchStr, typeDef,   declarationType);
			foundAnyCategory = true;
		}
		}
	return foundAnyCategory;
	}
	
	public boolean iterateThroughVarDefSearchStrings( Vector searchList, TypeSpecification typeDef, String category, TypeBody declarationType)
	throws JselException {
	
		boolean foundAnyCategory = false;

	for ( int i = 0; i  < searchList.size(); i++){
		String searchStr = (String)searchList.elementAt(i);
		if("*".equals(searchStr.substring(searchStr.length()-1) ) ){
			String packageStr = searchStr.substring(0,searchStr.length()-1);

			if ( typeDef.getName().startsWith(packageStr  ) ){
				addCategory(category, typeDef.getName(), typeDef,   declarationType);
				foundAnyCategory = true;
			}
		}else	if (searchStr.equals(typeDef.getName())) {
			addCategory(category, searchStr, typeDef,   declarationType);
			foundAnyCategory = true;
		}
		}
	return foundAnyCategory;
	}

	public boolean nonTrivialTypeFilter(  TypeSpecification typeDef )throws JselException{
			//!! should be xml configured
		if ( 	typeDef.getName().startsWith("java."  )  ||
				typeDef.getName().startsWith("com.ge."  )  ||
				typeDef.getName().startsWith("ge.geia."  )  ||
				typeDef.getName().startsWith("com.erc."  )  ||
				typeDef.getName().startsWith("com.gefra."  )  ||
				typeDef.getName().startsWith("com.geerc."  )  ||
				typeDef.getName().startsWith("com.gecapital."  )  ||
				typeDef.getName().startsWith("erc."  ) ||
				typeDef.getName().startsWith("ercrio.rio."  ) ||
				typeDef.getName().startsWith("com.iri."  )||
				typeDef.getName().startsWith("com.igate."  )
				){
			return false;
		}
		return true;
	}
	
	public void visit(VariableDefinition variableDefinition) {
		
		// JDepend 
		this.coupling.incrementVariableCoupling(variableDefinition) ;
		
		// Repository initialization
		if (repository == null ){
			repository = variableDefinition.getCompilationUnit().getPackage().getRepository();
		}
		try {
			if (variableDefinition.getTypeSpecification() instanceof ClassTypeSpecificationImpl ) {
				try {
					
					TypeDefinition typeDef = variableDefinition.getRootEnclosingType();
					checkVariableType(variableDefinition.getTypeSpecification(), typeDef);
				} catch (JselException e) {
					context.warn(variableDefinition, e);
				}
			} else {
				// System.out.println( " OO "  + variableDefinition.getTypeSpecification().getClass().toString() );
			}
		} catch (ClassNotFoundException e) {
			context.warn(variableDefinition, e);
		}
	}

	public void visit(Parameter parameter) {

		// JDepend 
		this.coupling.incrementVariableCoupling(parameter) ;

		if (repository == null ){
			repository = parameter.getCompilationUnit().getPackage().getRepository();
		}
		
		try {
			if (parameter.getTypeSpecification() instanceof ClassTypeSpecificationImpl ) {
				try {
					TypeSpecification ts = parameter.getTypeSpecification();
					checkVariableType(ts, parameter.getParent().getEnclosingType());
				} catch (JselException e) {
					context.warn(parameter, e);
				}
			}
		} catch (ClassNotFoundException e) {
			context.warn(parameter, e);
		}
			
	}
	
	/*
	public void addCategory(String category, String variableIdent, VariableDefinition element,  TypeDefinition declarationType) {
		ListOfCategories typeLoc = null;
		typeLoc = (ListOfCategories) allCategorizedClasses.get(element.getEnclosingType().getFcn());
		typeLoc.add(new ArchitecturalCategory(
				((SourceMarker)element).getSourceURL(),
				((SourceMarker)element).getLine(), 
				((SourceMarker)element).getColumn(), 
				 variableIdent, category));
	}
	*/
	/*
	public void addCategory(String category, String variableIdent, VariableDefinition element,  TypeBody declarationType) {
		ListOfCategories typeLoc = null;
		typeLoc = (ListOfCategories) allCategorizedClasses.get(element.getEnclosingType().getFcn());
		typeLoc.add(new ArchitecturalCategory(
				((SourceMarker)element).getSourceURL(),
				((SourceMarker)element).getLine(), 
				((SourceMarker)element).getColumn(), 
				 variableIdent, category));
	}
	*/
/*
	public void addCategory(String category, String variableIdent, Interface element,  TypeDefinition declarationType) {
		ListOfCategories typeLoc = null;
		typeLoc = (ListOfCategories) allCategorizedClasses.get(element.getFcn());
		typeLoc.add(new ArchitecturalCategory(
				((SourceMarker)element).getSourceURL(),
				((SourceMarker)element).getLine(), 
				((SourceMarker)element).getColumn(), 
				 variableIdent, category));
	}
*/
	public void addCategory(String category, String variableIdent, TypeSpecification element,  TypeDefinition declarationType) throws JselException {
		ListOfCategories typeLoc = null;
		if(declarationType != null ){
			typeLoc = (ListOfCategories) allCategorizedClasses.get(declarationType.getFcn());
		
		//!! job: what happens with missing categories??
		if( typeLoc == null){
	//		typeLoc = new ListOfCategories( declarationType.getFcn(  );
	//		allCategorizedClasses.put( declarationType.getFcn(), typeLoc );
			new Exception().printStackTrace();
		}else{
			typeLoc.add(new ArchitecturalCategory(
					((SourceMarker)element).getSourceURL(),
					((SourceMarker)element).getLine(), 
					((SourceMarker)element).getColumn(), 
					 variableIdent, category));
			}
		}
	}
	
	public void addCategory(String category, String variableIdent, TypeSpecification element,  TypeBody declarationType) throws JselException {
		
		
		ListOfCategories typeLoc = null;
		
		if(declarationType != null ){
			typeLoc = (ListOfCategories) allCategorizedClasses.get(declarationType.getFcn());
		
		//!! job: what happens with missing categories??
		if( typeLoc == null){
	//		typeLoc = new ListOfCategories( declarationType.getFcn(  );
	//		allCategorizedClasses.put( declarationType.getFcn(), typeLoc );
		}else{
			
			//!! put in here (and all category constructors a complexity helper method
			typeLoc.add(new ArchitecturalCategory(
					((SourceMarker)element).getSourceURL(),
					((SourceMarker)element).getLine(), 
					((SourceMarker)element).getColumn(), 
					 variableIdent, category));
			}
		}
	}

	public void addCategory(String category, String variableIdent, TypeDefinition element) throws JselException {
		ListOfCategories typeLoc = null;
		typeLoc = (ListOfCategories) allCategorizedClasses.get(element.getFcn());

		typeLoc.add(new ArchitecturalCategory(
				((SourceMarker)element).getSourceURL(),
				((SourceMarker)element).getLine(), 
				((SourceMarker)element).getColumn(), 
				variableIdent, category));
	}
/*
	public void addCategory(ListOfCategories typeLoc, String category, String variableIdent, LanguageElement element) {
		try {
			typeLoc.add(new ArchitecturalCategory(
					((SourceMarker)element).getSourceURL(),
					((SourceMarker)element).getLine(), 
					((SourceMarker)element).getColumn(), 
					 variableIdent, category));
		} catch (Exception e) {
			context.warn(element, e);
		}
	}
*/
	public void addCategory(ListOfCategories typeLoc, String category, String variableIdent, String fcn) {
			typeLoc.add(new ArchitecturalCategory(fcn, 0,0, variableIdent, category));
			}
	
	public void visit(Method p) {
		//!! detect Connection >> prepareCall( )

		int start = p.getAst().getFirstToken().getLine();
		int end = p.getAst().getLastToken().getLine();
		TypeBody enclosingType = p.getEnclosingType();
		final String fcn = enclosingType.getFcn();

		if (((end - start) < 3)
				&& (p.getName().toString().startsWith("get") || p.getName()
						.toString().startsWith("set"))) {
			try {
				String keyGetterSetter = fcn + GETTERSETTER;
				Integer getterCounter = (Integer) allGetterSetterCounterForClasses
						.get(keyGetterSetter);
				if (getterCounter == null) {
					allGetterSetterCounterForClasses.put(keyGetterSetter, new Integer(1));
					ListOfCategories typeLoc = (ListOfCategories) allCategorizedClasses.get(fcn);
					if (typeLoc!=null) {
						typeLoc.setType(fcn);
					}
				} else {
					allGetterSetterCounterForClasses.put(keyGetterSetter, new Integer(getterCounter.intValue() + 1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			try {
				Integer getterCounter = (Integer) allGetterSetterCounterForClasses.get(fcn);
				if (getterCounter == null) {
					allGetterSetterCounterForClasses.put(fcn, new Integer(1));
					ListOfCategories typeLoc = (ListOfCategories) allCategorizedClasses.get(fcn);
					if (typeLoc!=null) {
						typeLoc.setType(fcn);
					}
				} else {
					allGetterSetterCounterForClasses.put(fcn, new Integer(getterCounter.intValue() + 1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void identifyCategories(TypeDefinition c)
		throws JselException, ClassNotFoundException {
		this.checkExtentType(c);
		return;
	}


	public void aggregate(){
		// create Getter-Setter Categories
		checkGetterSetter();
		
		//-- compress to package level
		compressClassesToPackageLevel();

		determineLayerPerPackage();
		extractLayer2PackageRelations();
	}


	private void extractLayer2PackageRelations(){
	 listOfLayers = 	new ListOfLayers( this.allCategorizedPackages );
	}

	private void compressClassesToPackageLevel(){

	Enumeration classesEnum = this.allCategorizedClasses.keys();
	while (classesEnum.hasMoreElements()){
		String key = (String)classesEnum.nextElement();
		ListOfCategories loc =(ListOfCategories)allCategorizedClasses.get(key);

		ListOfPackageCategories lopc = (ListOfPackageCategories)this.allCategorizedPackages.get( ListOfPackageCategories.getWithClassFcn(key));
		
		if (loc.size() == 0) {
			ArchitecturalCategoryPackage pnac = (ArchitecturalCategoryPackage) lopc.get("NOT CATEGORIZED");
			if (pnac != null) {
				pnac.occurance++;
			} else {
				// logger.error(" No ArchitecturalCategory for <NOT CATEGORIZED> initialized. ");
			}
		}

			for (int i = 0; i < loc.size(); i++) {
				try {
					ArchitecturalCategory ac = (ArchitecturalCategory) loc.elementAt(i);
					// System.out.println ("*** " +ac.categoryType );
					ArchitecturalCategoryPackage pac = lopc.get(ac.categoryType);
					if (pac == null) {
						lopc.put( ac.categoryType, new ArchitecturalCategoryPackage( key ,ac.categoryType,ac.source_url ));
					} else {
						pac.occurance++;
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
				}
			}
	}
}

	private void checkGetterSetter(){
	//!!
		double ratio = 0;
		Enumeration propertyEnum = allGetterSetterCounterForClasses.keys();
		while (propertyEnum.hasMoreElements()){
			String key = (String)propertyEnum.nextElement();
			Integer numGetterSetter = (Integer)allGetterSetterCounterForClasses.get( key+GETTERSETTER );
			Integer num = (Integer)allGetterSetterCounterForClasses.get( key );
			if( numGetterSetter != null && num  != null){
				 ratio = new Double(num.intValue()).doubleValue() / new Double(numGetterSetter.intValue()).doubleValue();
				} else if ( numGetterSetter != null && num  == null){
					ratio = -1 ;
				} else if ( numGetterSetter == null && num  != null){
					ratio = 1;
				}
				//System.out.println( "§§ " + num +" / "+ numGetterSetter + " = "+  ratio );
			if( ratio < 0.3){
				ListOfCategories typeLoc = (ListOfCategories) allCategorizedClasses.get(key);
				if (typeLoc!=null) {
					BigDecimal bg =new BigDecimal( ratio);							
					addCategory( typeLoc, VALUE_OBJECT, "Getter/Setter Count = " + numGetterSetter
							+ " Ratio: " + bg.divide(new BigDecimal("1.0"),2,BigDecimal.ROUND_HALF_UP).toString(), 
							typeLoc.getSourceURL());
				}				
			}
		}
	}

	private void determineLayerPerPackage(){

		Enumeration enumKey = this.allCategorizedPackages.keys();
		while(enumKey.hasMoreElements()){
			String key = (String)enumKey.nextElement() ;
			ListOfPackageCategories loc = (ListOfPackageCategories)this.allCategorizedPackages.get( key );
			loc.setLayers(  this.determineLayerInListOfCategories(loc) );
		}

		return;
	}

	private HashSet determineLayerInListOfCategories(ListOfPackageCategories loc) {
		HashSet layers = new HashSet();
		// Enumeration keys = architecturalLayerMapping.getMappings().keys();
		Enumeration keys = architecturalLayerMappingTable.keys();
		
		while (keys.hasMoreElements()) {
			String layer = (String) keys.nextElement();
			 // System.out.print ( "layer " + layer );
			 ArchitecturalLayerMapping alMapping  = architecturalLayerMappingTable.get(layer);
Iterator itAlTe = alMapping.getTechStackEntityListValues().iterator();
			 while ( itAlTe.hasNext() ){
			 	
			 	TechStackEntity tse =	(TechStackEntity)itAlTe.next();
						
			if (loc.contains(tse.getName())) {
				// layers.add((String) architecturalLayerMapping.getMappings().get(key));
				layers.add( layer );
				
			     // layers.add((String) architecturalLayerMappingTable.get(key));
				// System.out.println ( " ..added" );
			} else {
				 // System.out.println ( " ..failed" );
			}
			 }
		}
		return layers;
	}

	public void leave(Repository repository) {
		aggregate();
	
		context.annotate( new LinkedAnnotation() {
			private String path;

			public String getName() {
				return getContext().getDescriptor().getName();
			}

			public String getPath() {
				return path;
			}

			public Properties getProperties() {
				Properties ret=new Properties();
				ret.setProperty("left-panel", "yes");
				ret.setProperty("target", "Architectual Layers");
				return ret;
			}

				public void render(AnnotationContext context) throws HammurapiException {
				String errorCausingFile = "";
				projectMetric.setName ( "Project" );
				projectMetric.setDescriptonEntity ( projectMetric.getName() );

				// Output images here. See AnnotationTest for details.

				class ArchitecturalLayerInspectorRenderer 
					extends AbstractRenderer {
					
				    ArchitecturalLayerInspectorRenderer() {
				        
						super(new RenderRequest(ArchitecturalLayerInspector.this));
					}

					public Element render(Document document) throws RenderingException {
						HashSet hs = new HashSet();

						ArchitecturalLayerInspector myInspector=(ArchitecturalLayerInspector) request.getRenderee(); ;
						Element entities=document.createElement("Entities");
						Element inspectorElement=document.createElement("ArchitecturalLayers");

						Enumeration enumC = allCategorizedClasses.elements();
						while(enumC.hasMoreElements()){
							ListOfCategories loc = (ListOfCategories)enumC.nextElement();
							inspectorElement.appendChild( loc.toDom(document)  );
							for ( int i=0; i<loc.size(); i++){
							hs.add( ((ArchitecturalCategory)loc.get(i)).categoryType);
							}
						}
						
						Element tsel=document.createElement("TechStackEntityList");
						inspectorElement.appendChild( tsel  );
						
						Collection almList = architecturalLayerMappingTable.values();
						Iterator it = almList.iterator();
					
						 while(it.hasNext()){
							ArchitecturalLayerMapping  alm = (ArchitecturalLayerMapping)it.next();
							
							Collection clte = alm.getTechStackEntityListValues();
							Iterator itClte = clte.iterator();
							 tsel.appendChild( alm.toDom(document)  );
						 }
							Element tselU=document.createElement("UsedTechStackEntityList");
							inspectorElement.appendChild( tselU  );

						Vector usedTechStackEntityList = new Vector();
						 Iterator itHs = hs.iterator();
						 while(itHs.hasNext()){
						 	TechStackEntity tse = (TechStackEntity)allTechStackEntitiesTable.get((String)itHs.next());
						 	if (tse != null){
								 tselU.appendChild( tse.toDom(document)  );
								 usedTechStackEntityList.add(tse);
								}
						 }
						 //!! markUsedJarFiles
						JarFileList jarFileList = new JarFileLookup().parseClasspath();
						jarFileList.markUsedJarFiles(usedTechStackEntityList, allTechStackEntitiesTable);
						inspectorElement.appendChild(  jarFileList.toDom(document) );
						
						//-------------------------------------------------------------------------
						
						
						Element projSumE=document.createElement("ProjectSummary");
						projSumE.setAttribute("number", String.valueOf( "" ) );
						inspectorElement.appendChild(projSumE);
						inspectorElement.appendChild(listOfLayers.toDom(document));
						Enumeration enump = allCategorizedPackages.elements();
						while(enump.hasMoreElements()){
							ListOfPackageCategories loc = (ListOfPackageCategories)enump.nextElement();
							inspectorElement.appendChild(loc.toDom(document));
						}
						Element tselr=document.createElement("TechStackEntityRating");
						inspectorElement.appendChild( tselr  );
						Enumeration enumalmt = architecturalLayerMappingTable.elements();
						while(enumalmt.hasMoreElements()){
							ArchitecturalLayerMapping almx = (ArchitecturalLayerMapping)enumalmt.nextElement();
							Enumeration enumte = almx.getTechStackEntityList().elements(); 
							while(enumte.hasMoreElements()){
								TechStackEntity tse = (TechStackEntity)enumte.nextElement();
								tselr.appendChild( tse.toDom(document)  );
							}
						}
						inspectorElement.appendChild(new ArchitecturalLayerMapping().toDom(document));
						inspectorElement.appendChild(extensionMap.toDom(document) );
						inspectorElement.appendChild(complexityMappingTable.toDom(document) );
						
						Element urantvlst=document.createElement("UnResolvedAndNonTrivialVariableList");
						Iterator ituvl = unknownVariableList.iterator();
						while( ituvl.hasNext()){
							Element urantv=document.createElement("UnResolvedAndNonTrivialVariable");	
							urantv.setAttribute("name", (String)ituvl.next() );		
							urantvlst.appendChild( urantv );
						}
						inspectorElement.appendChild( urantvlst );
						inspectorElement.appendChild( coupling.toDom(document) );
						return inspectorElement;
					}
				} //-- end class Renderer
				
				AnnotationContext.FileEntry fileEntry=context.getNextFile(context.getExtension());
				path=fileEntry.getPath();
				AnnotationContext.FileEntry fileEntryXml=context.getNextFile(".xml");
				try {
					ArchitecturalLayerInspectorRenderer renderer=new ArchitecturalLayerInspectorRenderer();
					FileOutputStream out=new FileOutputStream(fileEntry.getFile());

					renderer.setEmbeddedStyle(context.isEmbeddedStyle());
					try {
						errorCausingFile = fileEntry.getFile().getAbsolutePath();
					renderer.render(
						        context.getParameter("style")==null 
						        	? null 
						            : new FileInputStream(context.getParameter("style").toString()), out);

					} finally {
						out.close();
					}
					FileOutputStream outXml=new FileOutputStream(fileEntryXml.getFile());
					try {
						errorCausingFile = fileEntryXml.getFile().getAbsolutePath();
						//-- write a XML file for other XSL usage
						renderer.setEmbeddedStyle(false);
						renderer.render(outXml);
//						renderer.setPrettyPrint( true );
//						InputStream inStream=getClass().getClassLoader().getResourceAsStream(xmlResourceName);
//						renderer.render(inStream, outXml);

					} finally {
						outXml.close();
					}
				} catch (Exception e) {
					throw new HammurapiException("Can't save "+ errorCausingFile +". " +e.getMessage()  );
				}
			}
		});

	}


	   /**
     * Configures the rule. Reads in the values of the parameters XXX and
     * class-max-complexity.
     *
     * @param name the name of the parameter being loaded from Hammurapi configuration
     * @param value the value of the parameter being loaded from Hammurapi configuration
     * @exception ConfigurationException in case of a not supported parameter
     */
	public boolean setParameter(String name, Object value)  
		throws ConfigurationException {
    
    	 if ("mapping-configuration-file".equals(name)) {
    	 	if (mappingConfigurationResource!=null) {
    	 		throw new ConfigurationException(CONFIG_ERROR_MESSAGE);
    	 	}
    	 	if (mappingConfigurationUrl!=null) {
    	 		throw new ConfigurationException(CONFIG_ERROR_MESSAGE);
    	 	}
    	 	mappingConfigurationPath=value.toString();
    	 } else if ("mapping-configuration-resource".equals(name)) {
    	 	if (mappingConfigurationPath!=null) {
    	 		throw new ConfigurationException(CONFIG_ERROR_MESSAGE);
    	 	}
    	 	if (mappingConfigurationUrl!=null) {
    	 		throw new ConfigurationException(CONFIG_ERROR_MESSAGE);
    	 	}
    	 	mappingConfigurationResource=value.toString();
    	 } else if ("mapping-configuration-url".equals(name)) {
    	 	if (mappingConfigurationPath!=null) {
    	 		throw new ConfigurationException(CONFIG_ERROR_MESSAGE);
    	 	}
    	 	if (mappingConfigurationResource!=null) {
    	 		throw new ConfigurationException(CONFIG_ERROR_MESSAGE);
    	 	}
    	 	mappingConfigurationUrl=value.toString();
        } else {
            throw new ConfigurationException("Parameter '"+name+"' is not supported");
        }
			return true;
    }

}
