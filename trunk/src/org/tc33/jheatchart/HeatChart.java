/*  
 *  Copyright 2010 Tom Castle (www.tc33.org)
 *  Licensed under GNU General Public License
 * 
 *  This file is part of JHeatChart - the heat maps charting api for Java.
 *
 *  JHeatChart is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JHeatChart is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with JHeatChart.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.tc33.jheatchart;


import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.*;

import javax.imageio.ImageIO;

/**
 * The <code>HeatChart</code> class describes a chart which can display 
 * 3-dimensions of zValues. Heat charts are sometimes known as heat maps. 
 * 
 * <p>
 * Use of this chart would typically involve 3 steps:
 * <ol>
 * <li>Construction of a new instance, providing the necessary zValues for the x,y,z values.</li>
 * <li>Configure the visual settings.</li>
 * <li>A call to either <code>getChartImage()</code> or <code>saveToFile(String)</code>.</li>
 * </ol>
 * 
 * <h3>Instantiation</h3>
 * <p>
 * Construction of a new <code>HeatChart</code> instance is through one of two
 * constructors. Both constructors take a 2-dimensional array of <tt>doubles</tt> 
 * which should contain the z-values for the chart. Consider this array to be 
 * the grid of values which will instead be represented as colours in the chart. 
 * One of the constructors then takes an interval and an offset for each of the 
 * x and y axis. These parameters supply the necessary information to describe 
 * the x-values and y-values for the zValues in the array. The quantity of x-values 
 * and y-values is already known from the lengths of the array dimensions. Then 
 * the offset parameters indicate what value the column or row in element 0 
 * represents. The intervals provide the increment from one column or row to the 
 * next.
 * 
 * <p>
 * <strong>Consider an example:</strong>
 * <blockquote><pre>
 * double[][] zValues = new double[][]{
 * 		{1.2, 1.3, 1.5},
 * 		{1.0, 1.1, 1.6},
 * 		{0.7, 0.9, 1.3}
 * };
 * 
 * double xOffset = 1.0;
 * double yOffset = 0.0;
 * double xInterval = 1.0;
 * double yInterval = 2.0;
 * 
 * HeatChart chart = new HeatChart(zValues, xOffset, yOffset, xInterval, yInterval);
 * </pre></blockquote>
 * 
 * In this example, the z-values range from 0.7 to 1.6. The x-values range from 
 * the xOffset value 1.0 to 4.0, which is calculated as the number of x-values 
 * multiplied by the xInterval, shifted by the xOffset of 1.0. The y-values are 
 * calculated in the same way to give a range of values from 0.0 to 6.0. 
 * 
 * <p>The other constructor uses default values of 0 for both offset parameters 
 * and 1 for both interval parameters, therefore keeping it in line with the 
 * indexing of the array. As a result, element [5][4] of the z-values array will 
 * represent an x-value of 4.0 and a y-value of 5.0 (with the contents of that 
 * element being the z-value).
 * 
 * <p>A third constructor is likely to be added in a later version to enable the use 
 * of non-numeric x and y values.
 * 
 * <h3>Configuration</h3>
 * <p>
 * This step is optional. By default the heat chart will be generated without a 
 * title or labels on the axis, and the colouring of the heat map will be in 
 * grayscale. A large range of configuration options are available to customise
 * the chart. All customisations are available through simple accessor methods.
 * See the javadoc of each of the methods for more information.
 * 
 * <h3>Output</h3>
 * <p>
 * The generated heat chart can be obtained in two forms, using the following 
 * methods:
 * <ul>
 * <li><strong>getChartImage()</strong> - The chart will be returned as a 
 * <code>BufferedImage</code> object that can be used in any number of ways, 
 * most notably it can be inserted into a Swing component, for use in a GUI 
 * application.</li>
 * <li><strong>saveToFile(File)</strong> - The chart will be saved to the file 
 * system at the file location specified as a parameter. The image format that  
 * the image will be saved in is derived from the extension of the file name.</li>
 * </ul>
 * 
 * <strong>Note:</strong> The chart image will not actually be created until 
 * either saveToFile(File) or getChartImage() are called, and will be 
 * regenerated on each successive call.
 */
public class HeatChart {
	
	// x, y, z data values.
	private double[][] zValues;
	private double xOffset;
	private double yOffset;
	private double xInterval;
	private double yInterval;
	
	// General chart settings.
	private int chartWidth;
	private int chartHeight;
	private int chartMargin;
	private boolean flexibleChartSize;
	private Color backgroundColour;
	
	// Title settings.
	private String title;
	private Font titleFont;
	private Color titleColour;
	private int titleWidth;
	private int titleHeight;
	
	// Axis settings.
	private int axisThickness;
	private Color axisColour;
	private Font axisLabelsFont;
	private Color axisLabelColour;
	private String xAxisLabel;
	private String yAxisLabel;
	private Color axisValuesColour;
	private Font axisValuesFont; // The font size will be considered the maximum font size - it may be smaller if needed to fit in.
	private int axisValuesMinFontSize;
	private int xAxisValuesInterval;
	private int xAxisValuesHeight;
	private int yAxisValuesInterval;
	private int yAxisValuesWidth;
	private int xAxisValuesPrecision;
	private int yAxisValuesPrecision;
	private boolean showXAxisValues;
	private boolean showYAxisValues;
	
	// Generated axis properties.
	private int xAxisLabelHeight;
	private int xAxisLabelWidth;
	private int yAxisLabelHeight;
	private int yAxisLabelWidth;
	
	// Heat map colour settings.
	private Color highValueColour;
	private Color lowValueColour;
	
	// How many RGB steps there are between the high and low colours.
	private int colourValueDistance;
	
	// Heat map dimensions.
	private int heatMapWidth;
	private int heatMapHeight;
	
	private Scale colourScale;
	
	/**
	 * Incremental scales. These are used in <code>HeatChart</code> to define 
	 * the spread of colours used in the heat map. A linear scale will evenly 
	 * spread the colours throughout the range of possible values. Logarithmic 
	 * scales will provide greater separation of small z-values, and 
	 * exponential scales will provide greater spread of larger z-values.
	 * 
	 * <p>
	 * Logarithmic and scales are not fully supported currently.
	 */
	public enum Scale {
		LOGARITHMIC,
		EXPONENTIAL,
		LINEAR
	}
	
	/**
	 * Creates a heatmap for x-values from 0 to zValues[0].length-1 and
	 * y-values from 0 to zValues.length-1.
	 * 
	 * <p>For a full explanation of the way x/y-values are determined from the 
	 * z-values, see the class JavaDoc above.
	 * 
	 * @param zValues the z-values, where each element is a row of z-values
	 * in the resultant heat chart.
	 */
	public HeatChart(double[][] zValues) {
		this(zValues, 0.0, 0.0, 1.0, 1.0);
	}
	
	/**
	 * Creates a heatmap for x-values ranging from xOffset to (xInterval * zValues[0].length-1)
	 * and y-values ranging from yOffset to (yInterval * zValues.length-1).
	 * 
	 * <p>For a full explanation of the way x/y-values are determined from the 
	 * z-values, see the class JavaDoc above.
	 * 
	 * @param zValues the z-values, where each element is a row of z-values
	 * in the resultant heat chart.
	 * @param xOffset the offset to add to each array index to give the x-value.
	 * @param yOffset the offset to add to each array index to give the y-value.
	 * @param xInterval the x-value spacing between each row index in the data 
	 * array.
	 * @param yInterval the y-value spacing between each column index in the data 
	 * array.
	 */
	public HeatChart(double[][] zValues, double xOffset, double yOffset, double xInterval, double yInterval) {
		this.zValues = zValues;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xInterval = xInterval;
		this.yInterval = yInterval;
	
		// Default chart settings.
		this.chartWidth = 800;
		this.chartHeight = 400;
		this.chartMargin = 20;
		this.backgroundColour = Color.WHITE;
		this.flexibleChartSize = true;
		
		// Default title settings.
		this.title = null;
		this.titleFont = new Font("Sans-Serif", Font.BOLD, 16);
		this.titleColour = Color.BLACK;
		
		// Default axis settings.
		this.xAxisLabel = null;
		this.yAxisLabel = null;
		this.axisThickness = 2;
		this.axisColour = Color.BLACK;
		this.axisLabelsFont = new Font("Sans-Serif", Font.PLAIN, 12);
		this.axisLabelColour = Color.BLACK;
		this.axisValuesColour = Color.BLACK;
		this.axisValuesFont = new Font("Sans-Serif", Font.PLAIN, 10);
		this.axisValuesMinFontSize = 7;
		this.xAxisValuesInterval = 1;
		this.xAxisValuesHeight = 0;
		this.showXAxisValues = true;
		this.showYAxisValues = true;
		this.yAxisValuesInterval = 1;
		this.yAxisValuesWidth = 0;
		this.xAxisValuesPrecision = 4;
		this.yAxisValuesPrecision = 4;
		
		// Default heatmap settings.
		this.highValueColour = Color.BLACK;
		this.lowValueColour = Color.WHITE;
		this.colourScale = Scale.LINEAR;		
		
		updateColourDistance();
	}
	
	/**
	 * Returns the 2-dimensional array of z-values currently in use. Each 
	 * element is a double array which represents one row of the heat map, or  
	 * all the z-values for one y-value.
	 * 
	 * @return an array of the z-values in current use, that is, those values 
	 * which will define the colour of each cell in the resultant heat map.
	 */
	public double[][] getZValues() {
		return zValues;
	}
	
	/**
	 * Replaces the z-values array. The number of elements should match the 
	 * number of y-values, with each element containing a double array with 
	 * an equal number of elements that matches the number of x-values.
	 * 
	 * <blockcode><pre>
	 * new double[][]{
	 *   {1.0,1.2,1.4},
	 *   {1.2,1.3,1.5},
	 *   {0.9,1.3,1.2},
	 *   {0.8,1.6,1.1}
	 * };
	 * </pre></blockcode>
	 * 
	 * The above zValues array is equivalent to:
	 * 
	 * <table border="1">
	 *   <tr>
	 *     <td rowspan="4" width="20"><center><strong>y</strong></center></td>
	 *     <td>1.0</td>
	 *     <td>1.2</td>
	 *     <td>1.4</td>
	 *   </tr>
	 *   <tr>
	 *     <td>1.2</td>
	 *     <td>1.3</td>
	 *     <td>1.5</td>
	 *   </tr>
	 *   <tr>
	 *     <td>0.9</td>
	 *     <td>1.3</td>
	 *     <td>1.2</td>
	 *   </tr>
	 *   <tr>
	 *     <td>0.8</td>
	 *     <td>1.6</td>
	 *     <td>1.1</td>
	 *   </tr>
	 *   <tr>
	 *     <td></td>
	 *     <td colspan="3"><center><strong>x</strong></center></td>
	 *   </tr>
	 * </table>
	 * 
	 * @param zValues the array to replace the current array with. The number 
	 * of elements in each inner array must be identical.
	 */
	public void setZValues(double[][] zValues) {
		this.zValues = zValues;
	}
	
	/**
	 * Returns the offset that will be applied to the index of each element in 
	 * the z-values array to give the x-value, when the interval between each 
	 * x-value is also calculated in. This interval is controlled by the 
	 * xOffset setting.
	 * 
	 * @return the offset that will be added to the index of each z-value 
	 * element to give its x-value.
	 */
	public double getXOffset() {
		return xOffset;
	}

	/**
	 * Sets the offset to apply to each column index in the z-values array to 
	 * give the x-value for that column. The full x-value also includes the 
	 * interval between each x-value set by the x-interval setting.
	 * 
	 * <blockcode><pre>
	 * x-value = x-offset + (column-index * x-interval)
	 * </pre></blockcode>
	 * 
	 * @param xOffset the new offset value to be applied to each column.
	 */
	public void setXOffset(double xOffset) {
		this.xOffset = xOffset;
	}

	/**
	 * Returns the offset that will be applied to the index of each element in 
	 * the z-values array to give the y-value, when the interval between each 
	 * y-value is also calculated in. This interval is controlled by the 
	 * yOffset setting.
	 * 
	 * <blockcode><pre>
	 * y-value = y-offset + (row-index * y-interval)
	 * </pre></blockcode>
	 * 
	 * @return the offset that will be added to the index of each z-value 
	 * element to give its y-value.
	 */
	public double getYOffset() {
		return yOffset;
	}

	/**
	 * Sets the offset to apply to each column index in the z-values array to 
	 * give the y-value for that column. The full y-value also includes the 
	 * interval between each y-value set by the y-interval setting.
	 * 
	 * <blockcode><pre>
	 * y-value = y-offset + (row-index * y-interval)
	 * </pre></blockcode>
	 * 
	 * @param yOffset the new offset value to be applied to each column.
	 */
	public void setYOffset(double yOffset) {
		this.yOffset = yOffset;
	}

	/**
	 * Returns the interval between each x-value. Each x-value is calculated 
	 * from the column index, the x-interval and the x-offset.
	 * 
	 * <blockcode><pre>
	 * x-value = x-offset + (column-index * x-interval)
	 * </pre></blockcode>
	 * 
	 * @return the interval value between each x-value.
	 */
	public double getXInterval() {
		return xInterval;
	}

	/**
	 * Sets the interval between each x-value. Each x-value is calculated 
	 * from the column index, the x-interval and the x-offset.
	 * 
	 * <blockcode><pre>
	 * x-value = x-offset + (column-index * x-interval)
	 * </pre></blockcode>
	 * 
	 * @param xInterval the new interval set between each x-value.
	 */
	public void setXInterval(double xInterval) {
		this.xInterval = xInterval;
	}

	/**
	 * Returns the interval between each y-value. Each y-value is calculated 
	 * from the row index, the y-interval and the y-offset.
	 * 
	 * <blockcode><pre>
	 * y-value = y-offset + (row-index * y-interval)
	 * </pre></blockcode>
	 * 
	 * @return the interval value between each y-value.
	 */
	public double getYInterval() {
		return yInterval;
	}

	/**
	 * Sets the interval between each y-value. Each y-value is calculated 
	 * from the row index, the y-interval and the y-offset.
	 * 
	 * <blockcode><pre>
	 * y-value = y-offset + (row-index * y-interval)
	 * </pre></blockcode>
	 * 
	 * @param yInterval the new interval set between each y-value.
	 */
	public void setYInterval(double yInterval) {
		this.yInterval = yInterval;
	}

	/**
	 * Returns the width of the chart in pixels. If the flexibleChartSize 
	 * setting is set to true then the actual chart image that is generated may 
	 * be smaller than the width returned here. In the case that the chart 
	 * image does get cropped smaller, successful calls to this method will 
	 * return the new cropped width.
	 * 
	 * @return the width in pixels of the chart image to be generated.
	 */
	public int getChartWidth() {
		return chartWidth;
	}

	/**
	 * Sets the width of the chart to be generated in pixels. The actual width 
	 * of the chart image may vary from this if the flexibleChartSize setting 
	 * is set to true.
	 * 
	 * <p>
	 * Defaults to 800 pixels.
	 * 
	 * @param width the width in pixels to use for any successive chart images
	 * that are generated.
	 */
	public void setChartWidth(int width) {
		this.chartWidth = width;
	}

	/**
	 * Returns the height of the chart in pixels. If the flexibleChartSize 
	 * setting is set to true then the actual chart image that is generated may 
	 * be smaller than the height returned here. In the case that the chart 
	 * image does get cropped smaller, successful calls to this method will 
	 * return the new cropped height.
	 * 
	 * @return the height in pixels of the chart image to be generated.
	 */
	public int getChartHeight() {
		return chartHeight;
	}

	/**
	 * Sets the height of the chart to be generated in pixels. The actual 
	 * height of the chart image may vary from this if the flexibleChartSize 
	 * setting is set to true.
	 * 
	 * <p>
	 * Defaults to 400 pixels.
	 * 
	 * @param height the height in pixels to use for any successive chart 
	 * images that are generated.
	 */
	public void setChartHeight(int height) {
		this.chartHeight = height;
	}

	/**
	 * Returns the String that will be used as the title of any successive 
	 * calls to generate a chart.
	 * 
	 * @return the title of the chart.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the String that will be used as the title of any successive 
	 * calls to generate a chart. The title will be displayed centralised 
	 * horizontally at the top of any generated charts.
	 * 
	 * <p>
	 * If the title is set to <tt>null</tt> then no title will be displayed.
	 * 
	 * <p>
	 * Defaults to null.
	 * 
	 * @param title the chart title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the String that will be displayed as a description of the 
	 * x-axis in any generated charts.
	 * 
	 * @return the display label describing the x-axis.
	 */
	public String getXAxisLabel() {
		return xAxisLabel;
	}

	/**
	 * Sets the String that will be displayed as a description of the 
	 * x-axis in any generated charts. The label will be displayed 
	 * horizontally central of the x-axis bar.
	 * 
	 * <p>
	 * If the xAxisLabel is set to <tt>null</tt> then no label will be 
	 * displayed.
	 * 
	 * <p>
	 * Defaults to null.
	 * 
	 * @param xAxisLabel the label to be displayed describing the x-axis.
	 */
	public void setXAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	/**
	 * Returns the String that will be displayed as a description of the 
	 * y-axis in any generated charts.
	 * 
	 * @return the display label describing the y-axis.
	 */
	public String getYAxisLabel() {
		return yAxisLabel;
	}

	/**
	 * Sets the String that will be displayed as a description of the 
	 * y-axis in any generated charts. The label will be displayed 
	 * horizontally central of the y-axis bar.
	 * 
	 * <p>
	 * If the yAxisLabel is set to <tt>null</tt> then no label will be 
	 * displayed.
	 * 
	 * <p>
	 * Defaults to null. 
	 * 
	 * @param yAxisLabel the label to be displayed describing the y-axis.
	 */
	public void setYAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}

	/**
	 * Returns the width of the margin in pixels to be left as empty space 
	 * around the heat map element.
	 * 
	 * @return the size of the margin to be left blank around the edge of the
	 * chart.
	 */
	public int getChartMargin() {
		return chartMargin;
	}

	/**
	 * Sets the width of the margin in pixels to be left as empty space around
	 * the heat map element. If a title is set then half the margin will be 
	 * directly above the title and half directly below it. Where axis labels 
	 * are set then the axis labels may sit partially in the margin.
	 * 
	 * <p>
	 * Defaults to 20 pixels.
	 * 
	 * @param margin the new margin to be left as blank space around the heat 
	 * map.
	 */
	public void setChartMargin(int margin) {
		this.chartMargin = margin;
	}

	/**
	 * Returns an object that represents the colour to be used as the 
	 * background for the whole chart. 
	 * 
	 * @return the colour to be used to fill the chart background.
	 */
	public Color getBackgroundColour() {
		return backgroundColour;
	}

	/**
	 * Sets the colour to be used on the background of the chart. 
	 * 
	 * <p>
	 * Defaults to <code>Color.WHITE</code>.
	 * 
	 * @param backgroundColour the new colour to be set as the background fill.
	 */
	public void setBackgroundColour(Color backgroundColour) {
		if (backgroundColour == null) {
			backgroundColour = Color.WHITE;
		}
		
		this.backgroundColour = backgroundColour;
	}

	/**
	 * Returns the <code>Font</code> that describes the visual style of the 
	 * title.
	 *  
	 * @return the Font that will be used to render the title.
	 */
	public Font getTitleFont() {
		return titleFont;
	}

	/**
	 * Sets a new <code>Font</code> to be used in rendering the chart's title 
	 * String.
	 * 
	 * <p>
	 * Defaults to Sans-Serif, BOLD, 16 pixels.
	 * 
	 * @param titleFont the Font that should be used when rendering the chart 
	 * title.
	 */
	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
	}

	/**
	 * Returns the <code>Color</code> that represents the colour the title text 
	 * should be painted in.
	 * 
	 * @return the currently set colour to be used in painting the chart title.
	 */
	public Color getTitleColour() {
		return titleColour;
	}

	/**
	 * Sets the <code>Color</code> that describes the colour to be used for the 
	 * chart title String.
	 * 
	 * <p>
	 * Defaults to <code>Color.BLACK</code>.
	 * 
	 * @param titleColour the colour to paint the chart's title String.
	 */
	public void setTitleColour(Color titleColour) {
		this.titleColour = titleColour;
	}

	/**
	 * Returns the width of the axis bars in pixels. Both axis bars have the 
	 * same thickness.
	 * 
	 * @return the thickness of the axis bars in pixels.
	 */
	public int getAxisThickness() {
		return axisThickness;
	}

	/**
	 * Sets the width of the axis bars in pixels. Both axis bars use the same 
	 * thickness.
	 * 
	 * <p>
	 * Defaults to 2 pixels.
	 * 
	 * @param axisThickness the thickness to use for the axis bars in any newly
	 * generated charts.
	 */
	public void setAxisThickness(int axisThickness) {
		this.axisThickness = axisThickness;
	}

	/**
	 * Returns the colour that is set to be used for the axis bars. Both axis
	 * bars use the same colour.
	 * 
	 * @return the colour in use for the axis bars.
	 */
	public Color getAxisColour() {
		return axisColour;
	}

	/**
	 * Sets the colour to be used on the axis bars. Both axis bars use the same
	 * colour.
	 * 
	 * <p>
	 * Defaults to <code>Color.BLACK</code>.
	 * 
	 * @param axisColour the colour to be set for use on the axis bars.
	 */
	public void setAxisColour(Color axisColour) {
		this.axisColour = axisColour;
	}

	/**
	 * Returns the font that describes the visual style of the labels of the 
	 * axis. Both axis' labels use the same font.
	 * 
	 * @return the font used to define the visual style of the axis labels.
	 */
	public Font getAxisLabelsFont() {
		return axisLabelsFont;
	}

	/**
	 * Sets the font that describes the visual style of the axis labels. Both 
	 * axis' labels use the same font.
	 * 
	 * @param axisLabelsFont the font to be used to define the visual style of 
	 * the axis labels.
	 */
	public void setAxisLabelsFont(Font axisLabelsFont) {
		this.axisLabelsFont = axisLabelsFont;
	}

	public Color getAxisLabelColour() {
		return axisLabelColour;
	}

	public void setAxisLabelColour(Color axisLabelColour) {
		this.axisLabelColour = axisLabelColour;
	}

	public Font getAxisValuesFont() {
		return axisValuesFont;
	}

	public void setAxisValuesFont(Font axisValuesFont) {
		this.axisValuesFont = axisValuesFont;
	}

	public Color getAxisValuesColour() {
		return axisValuesColour;
	}

	public void setAxisValuesColour(Color axisValuesColour) {
		this.axisValuesColour = axisValuesColour;
	}

	public int getAxisValuesMinFontSize() {
		return axisValuesMinFontSize;
	}

	public void setAxisValuesMinFontSize(int axisValuesMinFontSize) {
		this.axisValuesMinFontSize = axisValuesMinFontSize;
	}

	public int getXAxisValuesInterval() {
		return xAxisValuesInterval;
	}

	public void setXAxisValuesInterval(int axisValuesInterval) {
		xAxisValuesInterval = axisValuesInterval;
	}

	public int getYAxisValuesInterval() {
		return yAxisValuesInterval;
	}

	public void setYAxisValuesInterval(int axisValuesInterval) {
		yAxisValuesInterval = axisValuesInterval;
	}

	public boolean isShowXAxisValues() {
		return showXAxisValues;
	}

	public void setShowXAxisValues(boolean showXAxisValues) {
		this.showXAxisValues = showXAxisValues;
	}

	public boolean isShowYAxisValues() {
		return showYAxisValues;
	}

	public void setShowYAxisValues(boolean showYAxisValues) {
		this.showYAxisValues = showYAxisValues;
	}

	public Color getHighValueColour() {
		return highValueColour;
	}

	public void setHighValueColour(Color highValueColour) {
		this.highValueColour = highValueColour;
		
		updateColourDistance();
	}
	
	public Color getLowValueColour() {
		return lowValueColour;
	}

	public void setLowValueColour(Color lowValueColour) {
		this.lowValueColour = lowValueColour;
		
		updateColourDistance();
	}
	
	/**
	 * @return the colourScale
	 */
	public Scale getColourScale() {
		return colourScale;
	}

	/**
	 * @param colourScale the colourScale to set
	 */
	public void setColourScale(Scale colourScale) {
		this.colourScale = colourScale;
	}

	/*
	 * Calculate and update the field for the distance between the low colour 
	 * and high colour. The distance is the number of steps between one colour 
	 * and the other using an RGB coding with 0-255 values for each of red, 
	 * green and blue. So the maximum colour distance is 255 + 255 + 255.
	 */
	private void updateColourDistance() {
		int r1 = lowValueColour.getRed();
		int g1 = lowValueColour.getGreen();
		int b1 = lowValueColour.getBlue();
		int r2 = highValueColour.getRed();
		int g2 = highValueColour.getGreen();
		int b2 = highValueColour.getBlue();
		
		colourValueDistance = Math.abs(r1 - r2);
		colourValueDistance += Math.abs(g1 - g2);
		colourValueDistance += Math.abs(b1 - b2);
	}

	public boolean isFlexibleChartSize() {
		return flexibleChartSize;
	}

	// Shrink the chart size to fit if cell dimensions don't add up nicely to fill space.
	public void setFlexibleChartSize(boolean flexibleChartSize) {
		this.flexibleChartSize = flexibleChartSize;
	}

	public int getXAxisValuesPrecision() {
		return xAxisValuesPrecision;
	}

	public void setXAxisValuesPrecision(int axisValuesPrecision) {
		xAxisValuesPrecision = axisValuesPrecision;
	}

	public int getYAxisValuesPrecision() {
		return yAxisValuesPrecision;
	}

	public void setYAxisValuesPrecision(int axisValuesPrecision) {
		yAxisValuesPrecision = axisValuesPrecision;
	}

	public void saveToFile(File outputFile) throws IOException {
		BufferedImage chart = (BufferedImage) getChartImage();

		String filename = outputFile.getName();

		int extPoint = filename.lastIndexOf('.');

		if (extPoint < 0) {
			throw new IOException("Illegal filename, no extension used.");
		}

		// Determine the extension of the filename.
		String ext = filename.substring(extPoint + 1);		

		// Save our graphic.
		ImageIO.write(chart, ext, outputFile);
	}
	
	public Image getChartImage() {
		// Create our chart image which we will eventually draw everything on.
		BufferedImage chartImage = new BufferedImage(chartWidth, chartHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D chartGraphics = chartImage.createGraphics();
		
		// Use anti-aliasing where ever possible.
		chartGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
									   RenderingHints.VALUE_ANTIALIAS_ON);

		// Set the background.
		chartGraphics.setColor(backgroundColour);
		chartGraphics.fillRect(0, 0, chartWidth, chartHeight);
		
		// Draw the title.
		drawTitle(chartGraphics);
		
		// Measure axis labels - have to do this before drawing heatmap to know 
		// the allowed size the heatmap can take.
		measureAxisLabels(chartGraphics);
		measureAxisValues(chartGraphics);
		
		// Draw the heatmap image.
		drawHeatMap(chartGraphics, zValues);
		
		// Draw the axis labels.
		drawXLabel(chartGraphics);
		drawYLabel(chartGraphics);
		
		// Draw the axis bars.
		drawAxisBars(chartGraphics);
		
		// Draw axis values.
		drawXValues(chartGraphics);
		drawYValues(chartGraphics);
		
		// We might not have used the full chart width/height so crop.
		chartImage = cropToSize(chartImage);
		
		return chartImage;
	}
	
	/*
	 * Draw the title String on the chart if title is not null.
	 */
	private void drawTitle(Graphics2D chartGraphics) {
		if (title != null) {
			chartGraphics.setFont(titleFont);
			FontMetrics metrics = chartGraphics.getFontMetrics();
			titleWidth = metrics.stringWidth(title);
			int titleAscent = metrics.getAscent();
			titleHeight = metrics.getHeight();
			
			// Strings are drawn from the baseline position of the leftmost char.
			int yTitle = ((chartMargin+titleHeight)/2) + (titleAscent/2);
			int xTitle = (chartWidth/2) - (titleWidth/2);
			
			chartGraphics.setColor(titleColour);
			chartGraphics.drawString(title, xTitle, yTitle);
		}
	}
	
	/*
	 * Draw the bars of the x-axis and y-axis.
	 */
	private void drawAxisBars(Graphics2D chartGraphics) {
		if (axisThickness > 0) {
			chartGraphics.setColor(axisColour);
			
			// Draw x-axis.
			int x = chartMargin + yAxisLabelWidth + yAxisValuesWidth;
			int y = chartMargin + titleHeight + heatMapHeight;
			int width = heatMapWidth + axisThickness;
			int height = axisThickness;
			chartGraphics.fillRect(x, y, width, height);
			
			// Draw y-axis.
			x = chartMargin + yAxisLabelWidth + yAxisValuesWidth;
			y = chartMargin + titleHeight;
			width = axisThickness;
			height = heatMapHeight;
			chartGraphics.fillRect(x, y, width, height);
		}
	}
	
	/*
	 * Measure the dimensions of the axis labels, so we can leave space for 
	 * them when drawing the heat map.
	 */
	private void measureAxisLabels(Graphics2D chartGraphics) {
		if (xAxisLabel != null) {
			chartGraphics.setFont(axisLabelsFont);
			FontMetrics metrics = chartGraphics.getFontMetrics();
			xAxisLabelWidth = metrics.stringWidth(xAxisLabel);
			xAxisLabelHeight = metrics.getHeight();
		}
		if (yAxisLabel != null) {
			chartGraphics.setFont(axisLabelsFont);
			FontMetrics metrics = chartGraphics.getFontMetrics();
			yAxisLabelHeight = metrics.stringWidth(yAxisLabel);
			yAxisLabelWidth = metrics.getHeight();
		}
	}
	
	/*
	 * Measure the dimensions of the axis values, so we can leave space for 
	 * them when drawing the heat map.
	 */
	private void measureAxisValues(Graphics2D chartGraphics) {
		// We don't actually measure the size of font used because it is dynamic
		// based upon the cell width, instead we just use the maximum font size.
		if (showXAxisValues) {
			chartGraphics.setFont(axisValuesFont);
			FontMetrics metrics = chartGraphics.getFontMetrics();
			xAxisValuesHeight = metrics.getHeight();
		}
		if (showYAxisValues) {
			chartGraphics.setFont(axisValuesFont);
			FontMetrics metrics = chartGraphics.getFontMetrics();
			yAxisValuesWidth = metrics.getHeight();
		}
	}
	
	/*
	 * Draw the x-axis label string if it is not null.
	 */
	private void drawXLabel(Graphics2D chartGraphics) {
		if (xAxisLabel != null) {
			FontMetrics metrics = chartGraphics.getFontMetrics();
			int axisLabelAscent = metrics.getAscent();
			
			// Strings are drawn from the baseline position of the leftmost char.
			int yPosXAxisLabel = chartMargin + titleHeight + heatMapHeight + axisThickness + axisLabelAscent + xAxisValuesHeight;
			int xPosXAxisLabel = (chartMargin + yAxisLabelWidth + (heatMapWidth / 2)) - (xAxisLabelWidth / 2) + yAxisLabelWidth;
			
			chartGraphics.setFont(axisLabelsFont);
			chartGraphics.setColor(axisLabelColour);
			chartGraphics.drawString(xAxisLabel, xPosXAxisLabel, yPosXAxisLabel);
		}
	}
	
	/*
	 * Draw the y-axis label string if it is not null.
	 */
	private void drawYLabel(Graphics2D chartGraphics) {
		if (yAxisLabel != null) {
			FontMetrics metrics = chartGraphics.getFontMetrics();
			int axisLabelDescent = metrics.getDescent();
			
			// Strings are drawn from the baseline position of the leftmost char.
			int yPosYAxisLabel = chartMargin + titleHeight + (heatMapHeight / 2) + (yAxisLabelHeight / 2);
			int xPosYAxisLabel = (chartMargin + yAxisLabelWidth) - axisLabelDescent;
			
			chartGraphics.setFont(axisLabelsFont);
			chartGraphics.setColor(axisLabelColour);
			
			// Create 270 degree rotated transform.
			AffineTransform transform = chartGraphics.getTransform();
			AffineTransform originalTransform = (AffineTransform) transform.clone();
			transform.rotate(Math.toRadians(270), xPosYAxisLabel, yPosYAxisLabel);
			chartGraphics.setTransform(transform);
			
			// Draw string.
			chartGraphics.drawString(yAxisLabel, xPosYAxisLabel, yPosYAxisLabel);
			
			// Revert to original transform before rotation.
			chartGraphics.setTransform(originalTransform);
		}
	}
	
	/*
	 * Draw the x-values onto the x-axis if showXAxisValues is set to true.
	 */
	private void drawXValues(Graphics2D chartGraphics) {
		if (!showXAxisValues) {
			return;
		}
		
		int noXCells = zValues[0].length;
		int cellWidth = heatMapWidth/noXCells;
		
		chartGraphics.setColor(axisValuesColour);
		
		for (int i=0; i<noXCells; i+=xAxisValuesInterval) {
			double xValue = (i * xInterval) + xOffset;
			
			// Format to sf.
			MathContext mc = new MathContext(xAxisValuesPrecision, RoundingMode.HALF_UP);
		    BigDecimal bigDecimal = new BigDecimal(xValue, mc);
		    String xValueStr = bigDecimal.toPlainString();
			
			Font font = new Font(axisValuesFont.getName(), 
								 axisValuesFont.getStyle(), 
								 axisValuesFont.getSize());
			
			int valueWidth = Integer.MAX_VALUE;
			
			FontMetrics metrics = null;
			while ((valueWidth > cellWidth) && (font.getSize() > axisValuesMinFontSize)) {
				font = new Font(font.getName(),
								font.getStyle(),
								font.getSize()-1);
				chartGraphics.setFont(font);
				metrics = chartGraphics.getFontMetrics();
				
				valueWidth = metrics.stringWidth(xValueStr);
			}
			
			// Draw the value with whatever font is now set.
			int valueXPos = (i * cellWidth) + ((cellWidth / 2) - (valueWidth / 2));
			valueXPos += (chartMargin + yAxisLabelWidth + axisThickness + yAxisValuesWidth);
			int valueYPos = (chartMargin + titleHeight + heatMapHeight + metrics.getAscent() + 1);
			
			chartGraphics.drawString(xValueStr, valueXPos, valueYPos);
		}
	}
	
	/*
	 * Draw the y-values onto the y-axis if showYAxisValues is set to true.
	 */
	private void drawYValues(Graphics2D chartGraphics) {
		if (!showYAxisValues) {
			return;
		}
		
		int noYCells = zValues.length;
		int cellHeight = heatMapHeight/noYCells;
		
		chartGraphics.setColor(axisValuesColour);

		for (int i=0; i<noYCells; i+=yAxisValuesInterval) {
			double yValue = (i * yInterval) + yOffset;
			
			// Format to sf.
			MathContext mc = new MathContext(yAxisValuesPrecision, RoundingMode.HALF_UP);
		    BigDecimal bigDecimal = new BigDecimal(yValue, mc);
		    String yValueStr = bigDecimal.toPlainString();
			
			Font font = new Font(axisValuesFont.getName(), 
								 axisValuesFont.getStyle(), 
								 axisValuesFont.getSize());
			
			int valueHeight = Integer.MAX_VALUE;
			
			FontMetrics metrics = null;
			while ((valueHeight > cellHeight) && (font.getSize() > axisValuesMinFontSize)) {
				font = new Font(font.getName(),
								font.getStyle(),
								font.getSize()-1);
				chartGraphics.setFont(font);
				metrics = chartGraphics.getFontMetrics();
				
				valueHeight = metrics.stringWidth(yValueStr);
			}
			
			// Draw the value with whatever font is now set.
			int valueXPos = (chartMargin + yAxisLabelWidth + yAxisValuesWidth - 1);
			int valueYPos = (chartMargin + titleHeight + heatMapHeight) - (i * cellHeight);
			valueYPos -= (cellHeight / 2);
			valueYPos += (valueHeight / 2);
			
			// Create 270 degree rotated transform.
			AffineTransform transform = chartGraphics.getTransform();
			AffineTransform originalTransform = (AffineTransform) transform.clone();
			transform.rotate(Math.toRadians(270), valueXPos, valueYPos);
			chartGraphics.setTransform(transform);
			
			// Draw the string.
			chartGraphics.drawString(yValueStr, valueXPos, valueYPos);
			
			// Revert to original transform before rotation.
			chartGraphics.setTransform(originalTransform);
		}
	}
	
	/*
	 * Creates the actual heatmap element as an image, that can then be drawn 
	 * onto a chart.
	 */
	private void drawHeatMap(Graphics2D chartGraphics, double[][] data) {
		// Calculate the available size for the heatmap.
		heatMapWidth = chartWidth - (2 * chartMargin) - yAxisLabelWidth - yAxisValuesWidth;
		heatMapHeight = chartHeight - (2 * chartMargin) - titleHeight - xAxisLabelHeight - xAxisValuesHeight;

		int noYCells = data.length;
		int noXCells = data[0].length;
		
		double dataMin = min(data);
		double dataMax = max(data);
		
		int cellWidth = heatMapWidth/noXCells;
		int cellHeight = heatMapHeight/noYCells;

		// Readjust height and width that we're actually going to draw on.
		heatMapWidth = cellWidth * noXCells;
		heatMapHeight = cellHeight * noYCells;

		BufferedImage heatMapImage = new BufferedImage(heatMapWidth, heatMapHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D heatMapGraphics = heatMapImage.createGraphics();
		
		for (int x=0; x<noXCells; x++) {
			for (int y=0; y<noYCells; y++) {
				// Set colour depending on zValues.
				heatMapGraphics.setColor(getCellColour(data[y][x], dataMin, dataMax));
				
				int cellX = x*cellWidth;
				int cellY = y*cellHeight;
				
				heatMapGraphics.fillRect(cellX, cellY, cellWidth, cellHeight);
			}
		}
		
		// Calculate the position of top right corner of heatmap.
		int xHeatMap = chartMargin + axisThickness + yAxisLabelWidth + yAxisValuesWidth;
		int yHeatMap = titleHeight + chartMargin;
		
		// Draw the heat map onto the chart.
		chartGraphics.drawImage(heatMapImage, xHeatMap, yHeatMap, heatMapWidth, heatMapHeight, null);
	}
	
	/*
	 * Determines what colour a heat map cell should be based upon the cell 
	 * values.
	 */
	private Color getCellColour(double data, double min, double max) {		
		double range = max - min;
		double position = data - min;

		// What proportion of the way through the possible values is that.
		double percentPosition = position / range;
		
		// Which colour group does that put us in.
		int colourPosition = getColourPosition(percentPosition);
		
		int r = lowValueColour.getRed();
		int g = lowValueColour.getGreen();
		int b = lowValueColour.getBlue();
		
		// Make n shifts of the colour, where n is the colourPosition.
		for (int i=0; i<colourPosition; i++) {
			int rDistance = r - highValueColour.getRed();
			int gDistance = g - highValueColour.getGreen();
			int bDistance = b - highValueColour.getBlue();
			
			if ((Math.abs(rDistance) >= Math.abs(gDistance))
						&& (Math.abs(rDistance) >= Math.abs(bDistance))) {
				// Red must be the largest.
				r = changeColourValue(r, rDistance);
			} else if (Math.abs(gDistance) >= Math.abs(bDistance)) {
				// Green must be the largest.
				g = changeColourValue(g, gDistance);
			} else {
				// Blue must be the largest.
				b = changeColourValue(b, bDistance);
			}
		}
		
		return new Color(r, g, b);
	}
	
	/*
	 * Returns how many colour shifts are required from the lowValueColour to 
	 * get to the correct colour position. The result will be different 
	 * depending on the colour scale used: LINEAR, LOGARITHMIC, EXPONENTIAL.
	 */
	private int getColourPosition(double percentPosition) {
		int colourPosition;
		
		// Which colour group does that put us in.
		if (colourScale == Scale.LOGARITHMIC) {
			colourPosition = (int) Math.round((colourValueDistance / Math.log10(2.0)) * Math.log10(percentPosition+1));
		} else if (colourScale == Scale.EXPONENTIAL) {
			colourPosition = (int) Math.round(Math.pow(10.0, (percentPosition * Math.log10(colourValueDistance+1))) - 1);
		} else {
			// Use a linear scale.
			colourPosition = (int) Math.floor(percentPosition * colourValueDistance);
		}
		
		return colourPosition;
	}
	
	private int changeColourValue(int colourValue, int colourDistance) {
		if (colourDistance < 0) {
			return colourValue+1;
		} else if (colourDistance > 0) {
			return colourValue-1;
		} else {
			// This shouldn't actually happen here.
			return colourValue;
		}
	}
	
	private BufferedImage cropToSize(BufferedImage chartImage) {
		if (flexibleChartSize) {
			chartWidth = (chartMargin*2) + yAxisLabelWidth + yAxisValuesWidth + axisThickness + heatMapWidth;
			chartHeight = (chartMargin*2) + titleHeight + axisThickness + xAxisLabelHeight + xAxisValuesHeight + heatMapHeight;
			
			BufferedImage croppedImage = new BufferedImage(chartWidth, chartHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D chartGraphics = croppedImage.createGraphics();
			chartGraphics.drawImage(chartImage,0, 0, null);
			chartImage = croppedImage;
		}
		return chartImage;
	}
	
	private static double max(double[][] values) {
		double max = 0;
		for (int i=0; i<values.length; i++) {
			for (int j=0; j<values[i].length; j++) {
				max = (values[i][j] > max) ? values[i][j] : max;
			}			
		}
		return max;
	}
	
	private static double min(double[][] values) {
		double min = Double.MAX_VALUE;
		for (int i=0; i<values.length; i++) {
			for (int j=0; j<values[i].length; j++) {
				min = (values[i][j] < min) ? values[i][j] : min;
			}
		}
		return min;
	}

	/*public static void main(String[] args) {
		double[][] data = new double[][]{
				{5,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6,7,8,9,8,7,8,9,8,7,6,5,4,3,5,7,6,5,5,4,6,7},
				{4,3,4,5,6,7,6,5,4,5,6,5,4,3,4,5,6,5,4,5,6,5,4,3,2,6,5,6,7,6,5,4,5,6,4,3,4},
				{3,4,5,6,7,6,5,4,5,6,5,4,3,2,3,4,4,5,6,5,7,6,5,4,5,6,5,4,3,2,3,4,5,6,5,4,6},
				{4,5,6,7,6,5,4,5,6,7,6,5,4,3,5,6,7,6,5,4,3,5,6,7,6,5,4,5,4,3,5,7,6,5,4,5,6},
				{5,6,7,6,5,4,5,6,5,6,7,6,5,4,6,7,4,5,6,5,4,5,7,6,5,4,5,6,4,5,6,7,6,5,5,6,7},
				{6,7,8,7,6,5,4,5,4,5,6,7,6,5,4,5,4,3,4,5,6,5,6,7,8,9,8,7,8,9,8,7,6,6,4,2,3}
		};
		
		
		
		HeatChart chart = new HeatChart(data);
		chart.setChartHeight(300);
		chart.setChartWidth(600);
		chart.setTitle("An Example Data Chart");
		chart.setAxisThickness(2);
		chart.setXAxisLabel("X axis data");
		chart.setYAxisLabel("Y axis data");
		chart.setChartMargin(15);
		chart.setXAxisValuesInterval(1);
		chart.setYOffset(0.2);
		chart.setShowYAxisValues(true);
		chart.setShowXAxisValues(true);
		chart.setHighValueColour(new Color(0,255,135));
		chart.setLowValueColour(new Color(126,0,135));
		chart.setBackgroundColour(new Color(230, 230, 250));
		
		try {
			chart.saveToFile(new File("test.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
