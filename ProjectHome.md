**JHeatChart is a one class Java API for generating heat map charts.**

Very few charting APIs seem to contain facility to create heat maps (heat charts). So this is just a one off class for creating simple heat maps. It is not a full featured charting API, and you cannot use it to create line charts, bar charts or pie charts. JHeatChart is released under a GPL license and is free to use and modify.

The charts generated are created as Java Image objects which can be incorporated into a GUI or saved to a file.

## Download ##

Downloads, javadoc and updates are available from the **[JHeatChart web site](http://www.javaheatmap.com)**.

The [source](http://code.google.com/p/jheatchart/source/checkout) for the latest version of JHeatChart is available by SVN.

## Donate ##

Found JHeatChart useful? Support further development with a [small donation](http://sourceforge.net/donate/index.php?group_id=305449).

## Usage example: ##
```
// Create some dummy data.
double[][] data = new double[][]{{3,2,3,4,5,6},
				 {2,3,4,5,6,7},
				 {3,4,5,6,7,6},
				 {4,5,6,7,6,5}};

// Create our heat chart using our data.
HeatChart chart = new HeatChart(data);

// Customise the chart.
chart.setTitle("This is my chart title");
chart.setXAxisLabel("X Axis");
chart.setYAxisLabel("Y Axis");

// Output the chart to a file.
chart.saveToFile(new File("my-chart.png"));
```

## Example chart output: ##

![http://www.tc33.org/jheatchart/demo.png](http://www.tc33.org/jheatchart/demo.png)