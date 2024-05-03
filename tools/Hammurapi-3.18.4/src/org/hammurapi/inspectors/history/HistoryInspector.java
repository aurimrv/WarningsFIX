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
package org.hammurapi.inspectors.history;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.hammurapi.HammurapiException;
import org.hammurapi.HammurapiRuntimeException;
import org.hammurapi.PersistingInspectorBase;
import org.hammurapi.results.AnnotationContext;
import org.hammurapi.results.LinkedAnnotation;
import org.hammurapi.results.NamedResults;
import org.hammurapi.results.ResultsFactory;
import org.hammurapi.results.AnnotationContext.FileEntry;
import org.hammurapi.results.persistent.jdbc.sql.AggregatedResultsMetricData;
import org.hammurapi.results.persistent.jdbc.sql.BasicResultTotal;
import org.hammurapi.results.persistent.jdbc.sql.Report;
import org.hammurapi.results.persistent.jdbc.sql.ResultsEngine;
import org.hammurapi.results.simple.SimpleAggregatedResults;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.XmlSource;
import com.pavelvlasov.convert.CompositeConverter;
import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.sql.DataAccessObject;
import com.pavelvlasov.sql.SQLProcessor;
import com.pavelvlasov.xml.dom.AbstractDomObject;
import com.pavelvlasov.xml.dom.DOMUtils;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.8 $
 */
public class HistoryInspector extends PersistingInspectorBase {
    
    private XmlSource styleConfig;

    public HistoryInspector() {
        styleConfig=new XmlSource("style", this.getClass(), ".xsl");
        addConfigurator(styleConfig);
    }
    
    public static class JoinedHistoryImplEx extends JoinedHistoryImpl implements DataAccessObject {
        private SQLProcessor processor;

        public void toDom(Element holder) {
            super.toDom(holder);
            AbstractDomObject.addTextElement(holder, "DPMO", getDPMO());
            AbstractDomObject.addTextElement(holder, "Sigma", getSigma());
            
            try {
	            String description=new HistoryEngine(processor).getLastDescription(getReportDate());
	            if (description!=null) {
	                AbstractDomObject.addTextElement(holder, "Description", description);
	            }
            } catch (SQLException e) {
            	throw new HammurapiRuntimeException(e);
            }
        }
        
    	public String getDPMO() {
    		if (getReviews()==0) {
    			return "Not available, no reviews";
    		}
    		
			return String.valueOf((int) (1000000*getViolationLevel()/getReviews())) + (getHasWarnings()>0 ? "*" : "");
    	}

    	public String getSigma() {
    		double p=1.0-getViolationLevel()/getReviews();
    		if (getReviews()==0) {
    			return "No results";
    		} else if (p<=0) {
    			return "Full incompliance";
    		} else if (p>=1) {
    			return "Full compliance";
    		} else {
    			return MessageFormat.format("{0,number,#.###}", new Object[] {new Double(SimpleAggregatedResults.normsinv(p)+1.5)}) + (getHasWarnings()>0 ? "*" : "");
    		}
    	}
        
        public JoinedHistoryImplEx() {
            super();
        }
        
        public JoinedHistoryImplEx(ResultSet rs) throws SQLException {
            super(rs);
        }

        public void setSQLProcessor(SQLProcessor sqlProcessor) {
            this.processor=sqlProcessor;
        }
    }
    
    private static abstract class TimeChartGenerator {
        
        abstract Number getValue(JoinedHistoryImplEx jhie);
                
        TimeSeries createTimeSeries(Collection series, String title) {
            TimeSeries timeSeries = new TimeSeries(title);
            Iterator sit=series.iterator();
            while (sit.hasNext()) {
                JoinedHistoryImplEx jhie=(JoinedHistoryImplEx) sit.next();
                timeSeries.add(new Day(new Date(jhie.getReportDate().getTime())), getValue(jhie));
            }
            return timeSeries;
        }
                
        void createPngChart(String title, TimeSeries timeSeries, File out) throws IOException {
            TimeSeriesCollection timeDataset = new TimeSeriesCollection(timeSeries);

            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    title,  // Title
                    "Time",           // X-Axis label
                    timeSeries.getName(),           // Y-Axis label
                    timeDataset,          // Dataset
                    true,                // Show legend
                    true,
                    true);            
            
            ChartUtilities.saveChartAsPNG(out, chart, 500, 300 );
        }
        
        void createPngBarChart(String title, TimeSeries timeSeries, File out) throws IOException {
            TimeSeriesCollection timeDataset = new TimeSeriesCollection(timeSeries);

            JFreeChart chart = ChartFactory.createXYBarChart(
                    title,  // Title
	                "Time",           // X-Axis label
	                true,
	                timeSeries.getName(),           // Y-Axis label
	                timeDataset,          // Dataset
	                PlotOrientation.VERTICAL,
	                true,                // Show legend
	                true,
	                true
	               );
            
            //System.out.println(out);
            ChartUtilities.saveChartAsPNG(out, chart, 500, 300 );
        }
        
    }
    
    public void leave(Repository repository) {
        getContext().annotate(new LinkedAnnotation() {            
            NamedResults summary=ResultsFactory.getThreadResults();
            FileEntry rootFile;

            public String getName() {
                return "History";
            }

            public void render(AnnotationContext context) throws HammurapiException {
                if (ResultsFactory.getInstance() instanceof org.hammurapi.results.persistent.jdbc.ResultsFactory) {
                    try {
                        SQLProcessor processor=getContext().getSession().getProcessor();
		                HistoryEngine historyEngine=new HistoryEngine(processor);		                
		                ResultsEngine resultsEngine = new ResultsEngine(processor);
                        Iterator it=historyEngine.getReportWithoutHistory(summary.getName(), ((org.hammurapi.results.persistent.jdbc.ResultsFactory) ResultsFactory.getInstance()).getReportId()).iterator();
                        while (it.hasNext()) {
                            Report report=(Report) it.next();
                            BasicResultTotal result = resultsEngine.getBasicResultTotal(report.getResultId().intValue());
    		                AggregatedResultsMetricData cr = resultsEngine.getAggregatedResultsMetricData(report.getResultId().intValue(), "Change ratio");
    		                
                            historyEngine.insertHistory(
    		                        report.getId(),
    		                        result.getCodebase(),
    		                        (Integer) CompositeConverter.getDefaultConverter().convert(result.getMaxSeverity(), Integer.class, false),
    		                        result.getReviews(),
    		                        result.getViolationLevel(),
    		                        result.getViolations(),
    		                        result.getWaivedViolations(),
    		                        (int) result.getHasWarnings(),
    		                        cr==null ? -1 : cr.getTotalValue()/ cr.getMeasurements(),
    		                        result.getCompilationUnits(),
									result.getResultDate(),
									report.getName(),
									report.getDescription(),
									new Long(report.getExecutionTime()==null ? 0 : report.getExecutionTime().longValue()));		                                            
                        }                                                
                        
                        Document doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                        Element root = doc.createElement("history");
                        root.setAttribute("title", summary.getName());
                        doc.appendChild(root);
                        Collection series=new ArrayList();
                        DOMUtils.toDom(historyEngine.getJoinedHistory(summary.getName(), series, JoinedHistoryImplEx.class), root);
                        
                        // Generate charts here
                        if (series.size()>1) {
	                        generateNodesChart(context, root, series);
	                        generateFilesChart(context, root, series);
	                        generateActivityChart(context, root, series);
	                        generateSigmaChart(context, root, series);
	                        generateDpmoChart(context, root, series);
	                        generateMaxSeverityChart(context, root, series);
	                        generateViolationsChart(context, root, series);
	                        generateReviewsChart(context, root, series);
	                        //generatePerformanceChart(context, root, series);
                        }
                        
		                rootFile=context.getNextFile(context.getExtension());
		                
                        if (context.getExtension().equalsIgnoreCase(".html")) {
                            TransformerFactory tFactory = TransformerFactory.newInstance();
                            InputStream styleStream = styleConfig.getStream();
                            if (styleStream==null) {
                                throw new HammurapiException("Stylesheet cannot be loaded");
                            }
                            
							Transformer transformer =  tFactory.newTransformer(new StreamSource(styleStream));

							DOMSource domSource = new DOMSource(doc);
							transformer.transform(domSource, new StreamResult(rootFile.getFile()));
                        } else {
                            DOMUtils.serialize(doc, rootFile.getFile());
                        }		                
	                } catch (ConfigurationException e) {
	                    throw new HammurapiException("Cannot render history annotation, "+e.getMessage(), e);
	                } catch (IOException e) {
	                    throw new HammurapiException("Cannot render history annotation, "+e.getMessage(), e);
	                } catch (SQLException e) {
	                    throw new HammurapiException("Cannot process history annotation, "+e.getMessage(), e);
                    } catch (ParserConfigurationException e) {
	                    throw new HammurapiException("Cannot process history annotation, "+e.getMessage(), e);
                    } catch (FactoryConfigurationError e) {
	                    throw new HammurapiException("Cannot process history annotation, "+e.getMessage(), e);
                    } catch (TransformerConfigurationException e) {
	                    throw new HammurapiException("Cannot process history annotation, "+e.getMessage(), e);
                    } catch (TransformerException e) {
	                    throw new HammurapiException("Cannot process history annotation, "+e.getMessage(), e);
                    }
                }
            }

            /**
             * @param context
             * @param root
             * @param series
             * @throws HammurapiException
             * @throws IOException
             */
            private void generateNodesChart(AnnotationContext context, Element root, Collection series) throws HammurapiException, IOException {
                TimeChartGenerator codeBaseChartGenerator=new TimeChartGenerator() {
                    Number getValue(JoinedHistoryImplEx jhie) {
                        return new Long(jhie.getCodebase());
                    }
                };
                
                FileEntry codeBaseNodesChart=context.getNextFile(".png");
                TimeSeries cbtn = codeBaseChartGenerator.createTimeSeries(series, "Nodes");
                codeBaseChartGenerator.createPngChart("Codebase history (nodes)", cbtn, codeBaseNodesChart.getFile());
                root.setAttribute("nodes-chart", codeBaseNodesChart.getPath());
            }

            /**
             * @param context
             * @param root
             * @param series
             * @throws HammurapiException
             * @throws IOException
             */
            private void generateFilesChart(AnnotationContext context, Element root, Collection series) throws HammurapiException, IOException {
                TimeChartGenerator codeBaseChartGenerator=new TimeChartGenerator() {
                    Number getValue(JoinedHistoryImplEx jhie) {
                        return new Long(jhie.getCompilationUnits());
                    }
                };
                
                FileEntry codeBaseChart=context.getNextFile(".png");
                TimeSeries cbtn = codeBaseChartGenerator.createTimeSeries(series, "Files");
                codeBaseChartGenerator.createPngChart("Codebase history (files)", cbtn, codeBaseChart.getFile());
                root.setAttribute("files-chart", codeBaseChart.getPath());
            }
            
            /**
             * @param context
             * @param root
             * @param series
             * @throws HammurapiException
             * @throws IOException
             */
            private void generateActivityChart(AnnotationContext context, Element root, Collection series) throws HammurapiException, IOException {
                TimeChartGenerator chartGenerator=new TimeChartGenerator() {
                    Number getValue(JoinedHistoryImplEx jhie) {
                        return new Integer((int) (jhie.getChangeRatio() * 100));
                    }
                };
                
                FileEntry fileEntry=context.getNextFile(".png");
                TimeSeries cbtn = chartGenerator.createTimeSeries(series, "Activity (%)");
                chartGenerator.createPngBarChart("Activity history", cbtn, fileEntry.getFile());
                root.setAttribute("activity-chart", fileEntry.getPath());
            }
            
            /**
             * @param context
             * @param root
             * @param series
             * @throws HammurapiException
             * @throws IOException
             */
            private void generateSigmaChart(AnnotationContext context, Element root, Collection series) throws HammurapiException, IOException {
                TimeChartGenerator chartGenerator=new TimeChartGenerator() {
                    Number getValue(JoinedHistoryImplEx jhie) {                        
                        String sigma=jhie.getSigma();
                        int idx=sigma.indexOf(' ');
                        try {
                            return new Double(idx==-1 ? sigma : sigma.substring(0, idx));
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    }
                };
                
                FileEntry fileEntry=context.getNextFile(".png");
                TimeSeries cbtn = chartGenerator.createTimeSeries(series, "Sigma");
                chartGenerator.createPngChart("Sigma history", cbtn, fileEntry.getFile());
                root.setAttribute("sigma-chart", fileEntry.getPath());
            }
            
            /**
             * @param context
             * @param root
             * @param series
             * @throws HammurapiException
             * @throws IOException
             */
            private void generateDpmoChart(AnnotationContext context, Element root, Collection series) throws HammurapiException, IOException {
                TimeChartGenerator chartGenerator=new TimeChartGenerator() {
                    Number getValue(JoinedHistoryImplEx jhie) {                        
                        String dpmo=jhie.getDPMO();
                        int idx=dpmo.indexOf(' ');
                        try {
                            return new Long(idx==-1 ? dpmo : dpmo.substring(0, idx));
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    }
                };
                
                FileEntry fileEntry=context.getNextFile(".png");
                TimeSeries cbtn = chartGenerator.createTimeSeries(series, "DPMO");
                chartGenerator.createPngChart("DPMO history", cbtn, fileEntry.getFile());
                root.setAttribute("dpmo-chart", fileEntry.getPath());
            }
            
            /**
             * @param context
             * @param root
             * @param series
             * @throws HammurapiException
             * @throws IOException
             */
            private void generateViolationsChart(AnnotationContext context, Element root, Collection series) throws HammurapiException, IOException {
                TimeChartGenerator chartGenerator=new TimeChartGenerator() {
                    Number getValue(JoinedHistoryImplEx jhie) {
                        return new Long(jhie.getViolations());
                    }
                };
                
                FileEntry fileEntry=context.getNextFile(".png");
                TimeSeries cbtn = chartGenerator.createTimeSeries(series, "Violations");
                chartGenerator.createPngChart("Violations history", cbtn, fileEntry.getFile());
                root.setAttribute("violations-chart", fileEntry.getPath());
            }
            
            /**
             * @param context
             * @param root
             * @param series
             * @throws HammurapiException
             * @throws IOException
             */
            private void generateMaxSeverityChart(AnnotationContext context, Element root, Collection series) throws HammurapiException, IOException {
                TimeChartGenerator chartGenerator=new TimeChartGenerator() {
                    Number getValue(JoinedHistoryImplEx jhie) {
                        return new Integer(jhie.getMaxSeverity());
                    }
                };
                
                FileEntry fileEntry=context.getNextFile(".png");
                TimeSeries cbtn = chartGenerator.createTimeSeries(series, "Max severity");
                chartGenerator.createPngChart("Max severity history", cbtn, fileEntry.getFile());
                root.setAttribute("max-severity-chart", fileEntry.getPath());
            }
            
            /**
             * @param context
             * @param root
             * @param series
             * @throws HammurapiException
             * @throws IOException
             */
            private void generateReviewsChart(AnnotationContext context, Element root, Collection series) throws HammurapiException, IOException {
                TimeChartGenerator chartGenerator=new TimeChartGenerator() {
                    Number getValue(JoinedHistoryImplEx jhie) {
                        return new Long(jhie.getReviews());
                    }
                };
                
                FileEntry fileEntry=context.getNextFile(".png");
                TimeSeries cbtn = chartGenerator.createTimeSeries(series, "Reviews");
                chartGenerator.createPngChart("Reviews history", cbtn, fileEntry.getFile());
                root.setAttribute("reviews-chart", fileEntry.getPath());
            }
            
            public Properties getProperties() {
                return null;
            }

            public String getPath() {
                return rootFile.getPath();
            }
            
        });
    }    
}
