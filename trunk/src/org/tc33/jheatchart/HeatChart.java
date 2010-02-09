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
	
	private double[][] zValues;
	private double xOffset;
	private double yOffset;
	private double xInterval;
	private double yInterval;
	
	private int chartWidth;
	private int chartHeight;
	private int chartMargin;
	
	private Color backgroundColour;
	
	private String title;
	private Font titleFont;
	private Color titleColour;
	private int titleWidth;
	private int titleHeight;
	
	private int axisWidth;
	private Color axisColour;
	
	private String xAxisLabel;
	private String yAxisLabel;
	private Font axisLabelsFont;
	private Color axisLabelColour;
	private int xAxisLabelHeight;
	private int xAxisLabelWidth;
	private int yAxisLabelHeight;
	private int yAxisLabelWidth;
	
	private Font axisValuesFont; // The font size will be considered the maximum font size - it may be smaller if needed to fit in.
	private int axisValuesMinFontSize;
	private Color axisValuesColour;
	private int xAxisValuesInterval;
	private int xAxisValuesHeight;
	private int yAxisValuesInterval;
	private int yAxisValuesWidth;
	private boolean showXAxisValues;
	private boolean showYAxisValues;
	private int xAxisValuesPrecision;
	private int yAxisValuesPrecision;
	
	// How many RGB steps there are between the high and low colours.
	private int colourValueDistance;
	private Color highValueColour;
	private Color lowValueColour;
	
	private int heatMapWidth;
	private int heatMapHeight;
	
	// Shrink the chart size to fit if cell dimensions don't add up nicely to fill space.
	private boolean flexibleChartSize;
	
	/**
	 * Creates a heatmap for x-values from 0 to zValues[0].length-1 and
	 * y-values from 0 to zValues.length-1.
	 * 
	 * @param zValues The z-value zValues, where each element is a row of z-values
	 * in the resultant heat chart.
	 */
	public HeatChart(double[][] zValues) {
		this(zValues, 0.0, 0.0, 1.0, 1.0);
	}
	
	/**
	 * Creates a heatmap for x-values ranging from xOffset to (xInterval * zValues[0].length-1)
	 * and y-values ranging from yOffset to (yInterval * zValues.length-1).
	 * 
	 * @param zValues
	 * @param xOffset
	 * @param yOffset
	 * @param xInterval
	 * @param yInterval
	 */
	public HeatChart(double[][] zValues, double xOffset, double yOffset, double xInterval, double yInterval) {
		this.zValues = zValues;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xInterval = xInterval;
		this.yInterval = yInterval;
	
		this.chartWidth = 800;
		this.chartHeight = 400;
		this.chartMargin = 20;		
		
		this.title = null;
		this.titleFont = new Font("Sans-Serif", Font.BOLD, 16);
		this.titleColour = Color.BLACK;
		
		this.xAxisLabel = null;
		this.yAxisLabel = null;
		this.axisWidth = 2;
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
		
		this.backgroundColour = Color.WHITE;
		this.highValueColour = Color.BLACK;
		this.lowValueColour = Color.WHITE;
		
		this.flexibleChartSize = true;
		
		updateColourDistance();
	}
	
	public void setData(double[][] data) {
		this.zValues = data;
	}
	
	/**
	 * 
	 * @return
	 */
	public double[][] getData() {
		return zValues;
	}
	
	/**
	 * @return the xOffset
	 */
	public double getXOffset() {
		return xOffset;
	}

	/**
	 * @param xOffset the xOffset to set
	 */
	public void setXOffset(double xOffset) {
		this.xOffset = xOffset;
	}

	/**
	 * @return the yOffset
	 */
	public double getYOffset() {
		return yOffset;
	}

	/**
	 * @param yOffset the yOffset to set
	 */
	public void setYOffset(double yOffset) {
		this.yOffset = yOffset;
	}

	/**
	 * @return the xInterval
	 */
	public double getXInterval() {
		return xInterval;
	}

	/**
	 * @param xInterval the xInterval to set
	 */
	public void setXInterval(double xInterval) {
		this.xInterval = xInterval;
	}

	/**
	 * @return the yInterval
	 */
	public double getYInterval() {
		return yInterval;
	}

	/**
	 * @param yInterval the yInterval to set
	 */
	public void setYInterval(double yInterval) {
		this.yInterval = yInterval;
	}

	/**
	 * @return the width
	 */
	public int getChartWidth() {
		return chartWidth;
	}

	/**
	 * @param width the width to set
	 */
	public void setChartWidth(int width) {
		this.chartWidth = width;
	}

	/**
	 * @return the height
	 */
	public int getChartHeight() {
		return chartHeight;
	}

	/**
	 * @param height the height to set
	 */
	public void setChartHeight(int height) {
		this.chartHeight = height;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the xAxisLabel
	 */
	public String getXAxisLabel() {
		return xAxisLabel;
	}

	/**
	 * @param xAxisLabel the xAxisLabel to set
	 */
	public void setXAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	/**
	 * @return the yAxisLabel
	 */
	public String getYAxisLabel() {
		return yAxisLabel;
	}

	/**
	 * @param yAxisLabel the yAxisLabel to set
	 */
	public void setYAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}

	/**
	 * @return the margin
	 */
	public int getChartMargin() {
		return chartMargin;
	}

	/**
	 * @param margin the margin to set
	 */
	public void setChartMargin(int margin) {
		this.chartMargin = margin;
	}

	public Color getBackgroundColour() {
		return backgroundColour;
	}

	public void setBackgroundColour(Color backgroundColour) {
		if (backgroundColour == null) {
			backgroundColour = Color.WHITE;
		}
		
		this.backgroundColour = backgroundColour;
	}

	public Font getTitleFont() {
		return titleFont;
	}

	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
	}

	public Color getTitleColour() {
		return titleColour;
	}

	public void setTitleColour(Color titleColour) {
		this.titleColour = titleColour;
	}

	public int getAxisWidth() {
		return axisWidth;
	}

	public void setAxisWidth(int axisWidth) {
		this.axisWidth = axisWidth;
	}

	public Color getAxisColour() {
		return axisColour;
	}

	public void setAxisColour(Color axisColour) {
		this.axisColour = axisColour;
	}

	public Font getAxisLabelsFont() {
		return axisLabelsFont;
	}

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
		drawAxis(chartGraphics);
		
		// Draw axis values.
		drawXValues(chartGraphics);
		drawYValues(chartGraphics);
		
		// We might not have used the full chart width/height so crop.
		chartImage = cropToSize(chartImage);
		
		return chartImage;
	}
	
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
	
	private void drawAxis(Graphics2D chartGraphics) {
		if (axisWidth > 0) {
			chartGraphics.setColor(axisColour);
			
			// Draw x-axis.
			int x = chartMargin + yAxisLabelWidth + yAxisValuesWidth;
			int y = chartMargin + titleHeight + heatMapHeight;
			int width = heatMapWidth + axisWidth;
			int height = axisWidth;
			chartGraphics.fillRect(x, y, width, height);
			
			// Draw y-axis.
			x = chartMargin + yAxisLabelWidth + yAxisValuesWidth;
			y = chartMargin + titleHeight;
			width = axisWidth;
			height = heatMapHeight;
			chartGraphics.fillRect(x, y, width, height);
		}
	}
	
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
	
	private void drawXLabel(Graphics2D chartGraphics) {
		if (xAxisLabel != null) {
			FontMetrics metrics = chartGraphics.getFontMetrics();
			int axisLabelAscent = metrics.getAscent();
			
			// Strings are drawn from the baseline position of the leftmost char.
			int yPosXAxisLabel = chartMargin + titleHeight + heatMapHeight + axisWidth + axisLabelAscent + xAxisValuesHeight;
			int xPosXAxisLabel = (chartMargin + yAxisLabelWidth + (heatMapWidth / 2)) - (xAxisLabelWidth / 2) + yAxisLabelWidth;
			
			chartGraphics.setFont(axisLabelsFont);
			chartGraphics.setColor(axisLabelColour);
			chartGraphics.drawString(xAxisLabel, xPosXAxisLabel, yPosXAxisLabel);
		}
	}
	
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
			valueXPos += (chartMargin + yAxisLabelWidth + axisWidth + yAxisValuesWidth);
			int valueYPos = (chartMargin + titleHeight + heatMapHeight + metrics.getAscent() + 1);
			
			chartGraphics.drawString(xValueStr, valueXPos, valueYPos);
		}
	}
	
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
		
		//TODO We might lose a few pixels here through truncating, so perhaps add some width/height adjustment.
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
		int xHeatMap = chartMargin + axisWidth + yAxisLabelWidth + yAxisValuesWidth;
		int yHeatMap = titleHeight + chartMargin;
		
		// Draw the heat map onto the chart.
		chartGraphics.drawImage(heatMapImage, xHeatMap, yHeatMap, heatMapWidth, heatMapHeight, null);
	}
	
	private Color getCellColour(double data, double min, double max) {		
		double range = max - min;
		double position = data - min;

		// What proportion of the way through the possible values is that.
		double percentPosition = position / range;
		
		// Which colour group does that put us in.
		int colourPosition = (int) Math.floor(percentPosition * colourValueDistance);
		
		int r = lowValueColour.getRed();
		int g = lowValueColour.getGreen();
		int b = lowValueColour.getBlue();
		
		// Make i shifts of the colour, where i is the colourPosition.
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
			chartWidth = (chartMargin*2) + yAxisLabelWidth + yAxisValuesWidth + axisWidth + heatMapWidth;
			chartHeight = (chartMargin*2) + titleHeight + axisWidth + xAxisLabelHeight + xAxisValuesHeight + heatMapHeight;
			
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

	public static void main(String[] args) {
		double[][] data = new double[][]{
				{5,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6},
				{4,3,4,5,6,7,6,5,4,5,6,5,4,3,4,5,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6},
				{3,4,5,6,7,6,5,4,5,6,5,4,3,2,3,4,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6},
				{4,5,6,7,6,5,4,5,6,7,6,5,4,3,5,6,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6},
				{5,6,7,6,5,4,5,6,5,6,7,6,5,4,6,7,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6},
				{6,7,8,7,6,5,4,5,4,5,6,7,6,5,4,5,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6,4,3,4,5,6,7,6,5,6,7,6,5,4,5,6}
		};
		
		
		
		HeatChart chart = new HeatChart(data);
		chart.setTitle("This is my chart title");
		chart.setAxisWidth(2);
		chart.setXAxisLabel("X Axis");
		chart.setYAxisLabel("Y Axis");
		chart.setChartMargin(20);
		chart.setXAxisValuesInterval(10);
		chart.setXInterval(3.323442452223);
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
	}
}