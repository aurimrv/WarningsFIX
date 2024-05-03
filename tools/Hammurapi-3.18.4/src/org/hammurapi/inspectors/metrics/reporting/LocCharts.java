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

import java.awt.Color;
import java.awt.GradientPaint;

import org.hammurapi.inspectors.metrics.statistics.DescriptiveStatistic;
import org.hammurapi.inspectors.metrics.statistics.IntVector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


//!! job variablize criteria and job Path program and for XSL
public class LocCharts extends ApplicationFrame {

	private IntVector locList = new IntVector();
	IntVector distinctValues = new IntVector();
	IntVector frequencies = new IntVector();
	String chartName = "NA";
	private int defectCriteria = 120;
	private int graphicDimX = 500;
	private int graphicDimY = 300;
	private Integer chartDebugWindow;

	public LocCharts( String _chartName, int _defectCriteria, IntVector _locList, Integer _chartDebugWindow) {
		//!! job: Wrong super type .
		super(_chartName);
		chartName = _chartName;

		defectCriteria = _defectCriteria;
		locList = _locList;
		chartDebugWindow = _chartDebugWindow;
	}

	public JFreeChart generateChart() {
		JFreeChart chart;

		new DescriptiveStatistic().frequencies(locList, distinctValues, frequencies);
		//!! job: wrong method name and tailoring
		chart = this.copyDeepXYSeries(distinctValues, frequencies);
		// System.out.println( distinctValues );
		// System.out.println( frequencies );
		customizeChartBars(chart);
		this.pack();

		if ( chartDebugWindow.intValue() > 0){
			RefineryUtilities.centerFrameOnScreen(this);
			this.setVisible(true);
		}
		return chart;
	}
	private void customizeChartBars(JFreeChart chart) {
		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		// set the background color for the chart...
		chart.setBackgroundPaint(new Color(0xBBBBDD));
		// get a reference to the plot for further customisation...
		XYPlot plot = chart.getXYPlot();
		// set the range axis to display integers only...
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// disable bar outlines...
		XYItemRenderer renderer = (XYItemRenderer) plot.getRenderer();
		// renderer.ssetDrawBarOutline(false);
		GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green, 0.0f, 0.0f, Color.lightGray);
		GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f, 0.0f, Color.lightGray);
		renderer.setSeriesPaint(0, gp1);
		renderer.setSeriesPaint(1, gp2);
		ValueAxis domainAxis = plot.getDomainAxis();
		//domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		//domainAxis.setMaxCategoryLabelWidthRatio(5.0f);
		// OPTIONAL CUSTOMISATION COMPLETED.
	}



	private JFreeChart copyDeepXYSeries(IntVector distinctValues, IntVector frequencies) {
		IntervalXYDataset dataset = new LocIntervalXYDataset();
		// create the chart...
		JFreeChart chart = ChartFactory.createXYBarChart(chartName, // chart
																	// title
				"NCSS", // domain axis label
				"Occurance", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, false);
		// get a reference to the plot for further customisation...
		XYPlot plot = chart.getXYPlot();
		plot.setDomainAxis(new NumberAxis("Not Commented Source Statements"));
		// add the chart to a panel...
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(graphicDimX, graphicDimY));
		setContentPane(chartPanel);
		return chart;
	}


	class LocIntervalXYDataset extends AbstractDataset implements IntervalXYDataset {
		private int barWidth = -1;
		/**
		 * Creates a new dataset.
		 */
		public LocIntervalXYDataset() {
			super();
		}
		/**
		 * Returns the number of series in the dataset.
		 *
		 * @return the number of series in the dataset.
		 */
		public int getSeriesCount() {
			return 2;
		}
		/**
		 * Returns the name of a series.
		 *
		 * @param series
		 *            the series (zero-based index).
		 *
		 * @return the series name.
		 */
		public String getSeriesName(int series) {
			if (series == 0) {
				return "Good";
			} else if (series == 1) {
				return "Defect";
			}
			return "N/A";
		}
		/**
		 * Returns the number of items in a series.
		 *
		 * @param series
		 *            the series (zero-based index).
		 *
		 * @return the number of items within a series.
		 */
		public int getItemCount(int series) {
			return distinctValues.size();
		}
		/**
		 * Returns the x-value for an item within a series.
		 * <P>
		 * The implementation is responsible for ensuring that the x-values are
		 * presented in ascending order.
		 *
		 * @param series
		 *            the series (zero-based index).
		 * @param item
		 *            the item (zero-based index).
		 *
		 * @return the x-value for an item within a series.
		 */
		public double getXValue(int series, int item) {
			if (series == 0 && distinctValues.elementAt(item) < defectCriteria) {
				//  "Good";
				return distinctValues.elementAt(item);
			} else if (series == 1 && distinctValues.elementAt(item) > defectCriteria) {
				// "Defect";
				return distinctValues.elementAt(item);
			}
			return 0;
		}
		/**
		 * Returns the y-value for an item within a series.
		 *
		 * @param series
		 *            the series (zero-based index).
		 * @param item
		 *            the item (zero-based index).
		 *
		 * @return the y-value for an item within a series.
		 */
		public double getYValue(int series, int item) {
			// return new Integer( frequencies.elementAt(item) );
			if (series == 0 && distinctValues.elementAt(item) < defectCriteria) {
				//  "Good";
				return frequencies.elementAt(item);
			} else if (series == 1 && distinctValues.elementAt(item) >= defectCriteria) {
				// "Defect";
				return frequencies.elementAt(item);
			}
			return 0;
		}
		/**
		 * Returns the starting X value for the specified series and item.
		 *
		 * @param series
		 *            the series (zero-based index).
		 * @param item
		 *            the item within a series (zero-based index).
		 *
		 * @return the start x value.
		 */
		public double getStartXValue(int series, int item) {
			//return new Integer( distinctValues.elementAt(item) ) ;
			if (series == 0 && distinctValues.elementAt(item) < defectCriteria) {
				//  "Good";
				return distinctValues.elementAt(item);
			} else if (series == 1 && distinctValues.elementAt(item) > defectCriteria) {
				// "Defect";
				return distinctValues.elementAt(item);
			}
			return 0;
		}
		/**
		 * Returns the ending X value for the specified series and item.
		 *
		 * @param series
		 *            the series (zero-based index).
		 * @param item
		 *            the item within a series (zero-based index).
		 *
		 * @return the end x value.
		 */
		public double getEndXValue(int series, int item) {
			barWidth = computeBarWidth();

			//!! job: compute bar width
			if (series == 0 && distinctValues.elementAt(item) < defectCriteria) {
				//  "Good";
				return distinctValues.elementAt(item) + barWidth;
			} else if (series == 1 && distinctValues.elementAt(item) > defectCriteria) {
				// "Defect";
				return distinctValues.elementAt(item) + barWidth;
			}
			return 0;
		}
		/*
		 * barWidth = 1 if MAX of distinctValues > 300 barWidth = 2 if MAX of
		 * distinctValues < 300 > 200 barWidth = 3 if MAX of distinctValues <
		 * 100
		 */
		public int computeBarWidth() {
			// lazy init
/*			if (barWidth < 0) {
				int max = distinctValues.elementAt(distinctValues.size() - 1);
				if (max > 350) {
					barWidth = 1;
				};
				if (max > 50 && max <= 350) {
					barWidth = 2;
				};
				if (max <= 50) {
					barWidth = 3;
				};
				// System.out.println(max + " barWidth " + barWidth);
				 *
				 */

			return 1;
		}
		/**
		 * Returns the starting Y value for the specified series and item.
		 *
		 * @param series
		 *            the series (zero-based index).
		 * @param item
		 *            the item within a series (zero-based index).
		 *
		 * @return the start y value.
		 */
		public double getStartYValue(int series, int item) {
			// return new Integer( frequencies.elementAt(item) );
			if (series == 0 && distinctValues.elementAt(item) < defectCriteria) {
				//  "Good";
				return frequencies.elementAt(item);
			} else if (series == 1 && distinctValues.elementAt(item) > defectCriteria) {
				// "Defect";
				return frequencies.elementAt(item);
			}
			return 0;
		}
		/**
		 * Returns the ending Y value for the specified series and item.
		 *
		 * @param series
		 *            the series (zero-based index).
		 * @param item
		 *            the item within a series (zero-based index).
		 *
		 * @return the end y value.
		 */
		public double getEndYValue(int series, int item) {
			//  return new Integer( frequencies.elementAt(item) );
			if (series == 0 && distinctValues.elementAt(item) < defectCriteria) {
				//  "Good";
				return frequencies.elementAt(item);
			} else if (series == 1 && distinctValues.elementAt(item) > defectCriteria) {
				// "Defect";
				return frequencies.elementAt(item);
			}
			return 0;
		}
		/**
		 * Registers an object for notification of changes to the dataset.
		 *
		 * @param listener
		 *            the object to register.
		 */
		public void addChangeListener(DatasetChangeListener listener) {
		}
		/**
		 * Deregisters an object for notification of changes to the dataset.
		 *
		 * @param listener
		 *            the object to deregister.
		 */
		public void removeChangeListener(DatasetChangeListener listener) {
		    
		}
		
        public Number getStartX(int series, int item) {
            return new Double(getStartXValue(series, item));
        }
        
        public Number getEndX(int series, int item) {
            return new Double(getEndXValue(series, item));
        }
        
        public Number getStartY(int series, int item) {
            return new Double(getStartYValue(series, item));
        }
        
        public Number getEndY(int series, int item) {
            return new Double(getEndYValue(series, item));
        }
        
        public DomainOrder getDomainOrder() {
            return DomainOrder.ASCENDING;
        }
        
        public Number getX(int series, int item) {
            return new Double(getXValue(series, item));
        }
        
        public Number getY(int series, int item) {
            return new Double(getYValue(series, item));
        }
        
	}
	/**
	 * @return Returns the graphicDimX.
	 */
	public int getGraphicDimX() {
		return graphicDimX;
	}
	/**
	 * @param graphicDimX The graphicDimX to set.
	 */
	public void setGraphicDimX(int graphicDimX) {
		this.graphicDimX = graphicDimX;
	}
	/**
	 * @return Returns the graphicDimY.
	 */
	public int getGraphicDimY() {
		return graphicDimY;
	}
	/**
	 * @param graphicDimY The graphicDimY to set.
	 */
	public void setGraphicDimY(int graphicDimY) {
		this.graphicDimY = graphicDimY;
	}
	/**
	 * @return Returns the chartName.
	 */
	public String getChartName() {
		return chartName;
	}
	/**
	 * @return Returns the defectCriteria.
	 */
	public int getDefectCriteria() {
		return defectCriteria;
	}
	/**
	 * @return Returns the distinctValues.
	 */
	public IntVector getDistinctValues() {
		return distinctValues;
	}
	/**
	 * @return Returns the frequencies.
	 */
	public IntVector getFrequencies() {
		return frequencies;
	}
	/**
	 * @return Returns the locList.
	 */
	public IntVector getLocList() {
		return locList;
	}

}
