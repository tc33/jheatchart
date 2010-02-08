package org.tc33.jheatchart;


import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

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
	
	private static final int border = 1;
	private static final int chartAreaMargin = 10;
	private static final int titleAreaHeight = 50;
	
	private double[][] data;
	private double xOffset;
	private double yOffset;
	private double xInterval;
	private double yInterval;
	
	private int chartWidth;
	private int chartHeight;
	private int chartMargin;
	
	private String title;
	private String xAxisLabel;
	private String yAxisLabel;
	
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
		this.title = null;
		this.xAxisLabel = null;
		this.yAxisLabel = null;
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

	public void saveToFile(File outputFile) throws IOException {
		BufferedImage chart = (BufferedImage) getChartImage();
		
		//TODO Determine the image format from the extension.
		
		
		// Save our graphic.
		ImageIO.write(chart, "png", outputFile);
	}
	
	public Image getChartImage() {
		// Calculate the necessary size of the heatmap element.
		int heatMapWidth = chartWidth - (2 * chartMargin);
		int heatMapHeight = chartHeight - (2 * chartMargin);
		
		// Draw the heatmap image.
		Image heatMap = createHeatMap(data, heatMapWidth, heatMapHeight);
				
		return null;
	}
	
	public void createChart(double[][] data, File outputFile, int cellWidth, int cellHeight) {		
		//int totalWidth = 1000;
		//int totalHeight = 3000;

		int totalWidth = (data[0].length * cellWidth) + (chartAreaMargin*2) + (border*2);
		int totalHeight = (data.length * cellHeight) + titleAreaHeight + (chartAreaMargin*2) + (border*2);
		
		int chartAreaHeight = totalHeight - titleAreaHeight;
		
		int chartSquareWidth = totalWidth - (chartAreaMargin * 2);
		int chartSquareHeight = chartAreaHeight - (chartAreaMargin * 2);
		
		String title = "Heat Map";
		
		// Create our image.
		BufferedImage img = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		
		// Set a white background.
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, totalWidth, totalHeight);
		
		// Write title.
		FontMetrics metrics = g.getFontMetrics();
		int titleWidth = metrics.stringWidth(title);
		int titleHeight = metrics.getHeight();
		g.setColor(Color.BLACK);
		g.drawString(title, (totalWidth/2)-(titleWidth/2), 30);
		
		// Create chart square.
		int xChartSquare = chartAreaMargin;
		int yChartSquare = titleAreaHeight + chartAreaMargin;
		Image heatMap = createHeatMap(data, chartSquareWidth, chartSquareHeight);
		g.drawImage(heatMap, xChartSquare, yChartSquare, chartSquareWidth, chartSquareHeight, null);
		

	}
	
	/*
	 * Creates the actual heatmap element as an image, that can then be drawn 
	 * onto a chart.
	 */
	private Image createHeatMap(double[][] data, int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		
		int yCells = data.length;
		int xCells = data[0].length;
		
		double dataMin = min(data);
		double dataMax = max(data);
		
		//TODO We might lose a few pixels here through truncating, so perhaps add some width/height adjustment.
		int cellWidth = (width-border)/xCells;
		int cellHeight = (height-border)/yCells;
		
		// Readjust height and width that we're actually going to draw on.
		width = cellWidth * xCells;
		height = cellHeight * yCells;
		
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width+border, height+border);
		
		for (int x=0; x<xCells; x++) {
			for (int y=0; y<yCells; y++) {
				// Set colour depending on data.
				g.setColor(getCellColour(data[y][x], dataMin, dataMax));
				
				int cellX = x*cellWidth+border;
				int cellY = y*cellHeight+border;
				
				g.fillRect(cellX, cellY, cellWidth, cellHeight);
			}
		}
		
		return img;
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
}
