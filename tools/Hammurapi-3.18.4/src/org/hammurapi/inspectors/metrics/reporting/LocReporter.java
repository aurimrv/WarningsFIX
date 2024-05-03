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
 * e-Mail: Johannes.Bellert@ercgroup.com
 */
package org.hammurapi.inspectors.metrics.reporting;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.hammurapi.inspectors.metrics.CodeMetric;
import org.hammurapi.inspectors.metrics.statistics.IntVector;
import org.hammurapi.results.AnnotationContext;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

public class LocReporter {

	private String projectBaseDir = "";
	private JFreeChart jFreeChartClasses;
	private JFreeChart jFreeChartFunctions;
	private AnnotationContext.FileEntry fileEntry;
	private File outFile;
	private Integer classMaxLoc;
	private Integer functionMaxLoc;
	private Integer ncssReport;
	private Integer chartDebugWindow;

	private AnnotationContext context;
	private AnnotationContext.FileEntry jpgClassFileEntry;
	private AnnotationContext.FileEntry jpgFunctionFileEntry;


	/**
     * A sorted distribution list of class NCSS
     *
     */
	private IntVector ncssClassList = new IntVector();

	/**
     * A sorted distribution list of function NCSS
     *
     */
	private IntVector ncssFunctionList = new IntVector();



	public LocReporter(AnnotationContext _context, Integer _classMaxLoc, Integer _functionMaxLoc, Integer _ncssReport, Integer _chartDebugWindow  ) {
		super();
		context = _context;
//		projectBaseDir=context.;
//		outFile = _outFile ;
		classMaxLoc = _classMaxLoc;
		functionMaxLoc = _functionMaxLoc;
		ncssReport = _ncssReport;
		chartDebugWindow = _chartDebugWindow;
	}


	public void doIt(CodeMetric projectMetric) {
		//!! job: refactor to Visitor traversing
		//		 packages
		if (ncssReport.intValue() > 0) {
			LocCharts locClassCharts = new LocCharts("NCSS: Classes", classMaxLoc.intValue(), this.ncssClassList,
					chartDebugWindow);
			locClassCharts.setGraphicDimX(350);
			locClassCharts.setGraphicDimY(250);
			this.jFreeChartClasses = locClassCharts.generateChart();
			LocCharts locFunctionCharts = new LocCharts("NCSS: Functions", functionMaxLoc.intValue(),
					this.ncssFunctionList, chartDebugWindow);
			locFunctionCharts.setGraphicDimX(350);
			locFunctionCharts.setGraphicDimY(250);
			this.jFreeChartFunctions = locFunctionCharts.generateChart();
		//	writeToXml(projectMetric);

			try {

				jpgClassFileEntry = context.getNextFile("Class.jpg");
				jpgFunctionFileEntry = context.getNextFile("Function.jpg");
				FileOutputStream out = new FileOutputStream(jpgClassFileEntry.getFile());
				ChartUtilities.writeChartAsJPEG((OutputStream) out, this.jFreeChartClasses, 500, 300);
				// outChartFile = new File(projectBaseDir, File.separatorChar +
				// "FunctionCodeMetric" + ".jpg");
				out = new FileOutputStream(jpgFunctionFileEntry.getFile());
				ChartUtilities.writeChartAsJPEG((OutputStream) out, this.jFreeChartFunctions, 500, 300);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * @return Returns the jpgClassFileEntry.
	 */
	public AnnotationContext.FileEntry getJpgClassFileEntry() {
		return jpgClassFileEntry;
	}
	/**
	 * @return Returns the jpgFunctionFileEntry.
	 */
	public AnnotationContext.FileEntry getJpgFunctionFileEntry() {
		return jpgFunctionFileEntry;
	}
	/**
	 * @return Returns the ncssClassList.
	 */
	public IntVector getNcssClassList() {
		return ncssClassList;
	}
	/**
	 * @param ncssClassList The ncssClassList to set.
	 */
	public void setNcssClassList(IntVector ncssClassList) {
		this.ncssClassList = ncssClassList;
	}
	/**
	 * @return Returns the ncssFunctionList.
	 */
	public IntVector getNcssFunctionList() {
		return ncssFunctionList;
	}
	/**
	 * @param ncssFunctionList The ncssFunctionList to set.
	 */
	public void setNcssFunctionList(IntVector ncssFunctionList) {
		this.ncssFunctionList = ncssFunctionList;
	}
}
