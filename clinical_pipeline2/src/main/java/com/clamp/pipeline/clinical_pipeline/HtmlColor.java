package com.clamp.pipeline.clinical_pipeline;

import java.util.Random;

public class HtmlColor {
	//static String[] colorList = { "#0066FF", "#00FF00", "#FF0000", "#FFFF00", "#00FFFF", "#FF00FF", "#C0C0C0" }; 
	static String[] colorList =  {
		"#00FF00", "#FFFF00", "#00FFFF", "#FF00FF", "#C0C0C0", "#0099FF",
		"#00CC00", "#00CC66", "#00CC99", "#00CCCC", "#00CCFF", "#3399CC",      
		"#00FF00", "#00FF33", "#00FF66", "#00FF99", "#00FFCC", "#00FFFF",
		"#3399FF", "#33CC99", "#33CCCC", "#33CCFF", "#33FF00", "#33FF33",      
		"#33FF66", "#33FF99", "#33FFCC", "#33FFFF", "#66CC66", "#66CC99",      
		"#66CCCC", "#66CCFF", "#66FF00", "#66FF33", "#66FF66", "#66FF99",      
		"#66FFCC", "#66FFFF", "#9966FF", "#9999CC", "#9999FF", "#99CC00",      
		"#99CC33", "#99CC66", "#99CC99", "#99CCCC", "#99CCFF", "#99FF00",      
		"#99FF33", "#99FF66", "#99FF99", "#99FFCC", "#99FFFF", "#CC6699",      
		"#CC66FF", "#CC9999", "#CC99CC", "#CC99FF", "#CCCC00", "#CCCC33",      
		"#CCCC66", "#CCCC99", "#CCCCCC", "#CCCCFF", "#CCFF00", "#CCFF33",      
		"#CCFF66", "#CCFF99", "#CCFFCC", "#CCFFFF", "#FF00CC", "#FF6600",      
		"#FF6633", "#FF6699", "#FF9900", "#FF9933", "#FF9966", "#FF9999",      
		"#FF99CC", "#FF99FF", "#FFCC00", "#FFCC33", "#FFCC66", "#FFCC99",      
		"#FFCCCC", "#FFCCFF", "#FFFF00", "#FFFF33", "#FFFF66", "#FFFF99",      
		"#FFFFCC"};
	
	static Random r = new Random( System.currentTimeMillis() );
	
	public static String getCorlor( int index ) {
		return colorList[ index % colorList.length ];
	}
	
	public static String getRandomCorlor() {
		return getCorlor( r.nextInt( colorList.length ) );
	}
}
