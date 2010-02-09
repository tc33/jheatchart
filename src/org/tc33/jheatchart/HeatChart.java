package org.tc33.jheatchart;


import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

import sun.font.FontManager;

/**
 * The chart image will not actually be created until either saveToFile() or 
 * getChartImage() are called. 
 */
public class HeatChart {

	private static final Color[] COLOUR_GROUPS = {
		new Color(0,0,0,255),
		new Color(0,0,0,230),
		new Color(0,0,0,205),
		new Color(0,0,0,180),
		new Color(0,0,0,155),
		new Color(0,0,0,130),
		new Color(0,0,0,105),
		new Color(0,0,0,80),
		new Color(0,0,0,55),
		new Color(0,0,0,30)
	};
	
	private double[][] data;
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
	
	private int heatMapWidth;
	private int heatMapHeight;
	
	/**
	 * Creates a heatmap for x values 0..data[0].length-1 and
	 * y values 0..data.length-1.
	 * 
	 * @param data
	 */
	public HeatChart(double[][] data) {
		this(data, 0.0, 0.0, 1.0, 1.0);
	}
	
	/**
	 * Creates a heatmap for x values from xOffset..(xInterval*data[0].length-1)
	 * and y values yOffset..(yInterval*data.length-1).
	 * @param data
	 * @param xOffset
	 * @param yOffset
	 * @param xInterval
	 * @param yInterval
	 */
	public HeatChart(double[][] data, double xOffset, double yOffset, double xInterval, double yInterval) {
		this.data = data;
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
		this.axisValuesMinFontSize = 6;
		this.xAxisValuesInterval = 1;
		this.xAxisValuesHeight = 0;
		this.showXAxisValues = true;
		this.showYAxisValues = true;
		this.yAxisValuesInterval = 1;
		this.yAxisValuesWidth = 0;
		
		this.backgroundColour = Color.WHITE;

	}
	
	public void setData(double[][] data) {
		this.data = data;
	}
	
	/**
	 * 
	 * @return
	 */
	public double[][] getData() {
		return data;
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

	public void saveToFile(File outputFile) throws IOException {
		BufferedImage chart = (BufferedImage) getChartImage();
		
		//TODO Determine the image format from the extension.
		
		
		// Save our graphic.
		ImageIO.write(chart, "png", outputFile);
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
		drawHeatMap(chartGraphics, data);
		
		// Draw the axis labels.
		drawXLabel(chartGraphics);
		drawYLabel(chartGraphics);
		
		// Draw the axis bars.
		drawAxis(chartGraphics);
		
		// Draw axis values.
		drawXValues(chartGraphics);
		drawYValues(chartGraphics);
		
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
		
		int noXCells = data[0].length;
		int cellWidth = heatMapWidth/noXCells;
		
		chartGraphics.setColor(axisValuesColour);
		
		for (int i=0; i<noXCells; i+=xAxisValuesInterval) {
			double xValue = (i * xInterval) + xOffset;
			String xValueStr = Double.toString(xValue);
			
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
		
		int noYCells = data.length;
		int cellHeight = heatMapHeight/noYCells;
		
		chartGraphics.setColor(axisValuesColour);

		for (int i=0; i<noYCells; i+=yAxisValuesInterval) {
			double yValue = (i * yInterval) + yOffset;
			String yValueStr = Double.toString(yValue);
			
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
				// Set colour depending on data.
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
	
	private static Color getCellColour(double data, double min, double max) {
		double range = max - min;
		data = data - min;

		if (data == 0) {
			return new Color(0,0,0,255);
		} else {
			double percentPosition = data / range;
			int group = (int) Math.floor(percentPosition * 155) + 100;
			
			return new Color(0, 0, 0, (255-group));
		}
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
				{3,2,3,4,5,6,7,6},
				{2,3,4,5,6,7,6,5},
				{3,4,5,6,7,6,5,4},
				{4,5,6,7,6,5,4,5},
				{5,6,7,6,5,4,5,6},
				{6,7,8,7,6,5,4,5}
		};
		
		HeatChart chart = new HeatChart(data);
		chart.setTitle("This is my chart title");
		chart.setAxisWidth(2);
		chart.setXAxisLabel("X Axis");
		chart.setYAxisLabel("Y Axis");
		chart.setChartMargin(20);
		chart.setXAxisValuesInterval(1);
		chart.setShowYAxisValues(true);
		chart.setShowXAxisValues(true);
		//chart.setXInterval(1.3211222221233131213212323322222);
		
		try {
			chart.saveToFile(new File("test.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
