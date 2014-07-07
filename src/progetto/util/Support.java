package progetto.util;

import javax.swing.*;

import progetto.*;


public class Support {
	private static final String newline="\n";
	
	//scrive nella textArea
	public static void scrivi(JTextArea log,String originPath,String destinationPath,String outputFile,
			int splitType,int outputFormat,int fixedLengthSeconds,int partsNumber,int duration,int firstNumberInName) {
		String splitTypeString="";
		if(splitType==1) {
			if(fixedLengthSeconds!=0)partsNumber=(int)Math.ceil((double)duration/fixedLengthSeconds);
			else partsNumber=0;
			splitTypeString="Split in parts of "+fixedLengthSeconds+" seconds = "+ (double)fixedLengthSeconds/60 + " minutes"+newline+"Number of parts: "+partsNumber;
		}else {
			if(partsNumber!=0)fixedLengthSeconds=duration/partsNumber;
			else fixedLengthSeconds=0;
			splitTypeString= "Split in "+partsNumber+" parts"+newline+"Duration of single part: "+fixedLengthSeconds+" seconds = "+ (double)fixedLengthSeconds/60 + " minutes";
		}
		String format="";
		if(outputFormat==0) {
			format=originPath.substring(originPath.length()-3,originPath.length());
		}else if(outputFormat==1) format="mp3";
		else format="wav";
		log.setText("Origin folder: "+originPath+newline+"Destination folder: "+destinationPath+newline+
				"Output file: "+outputFile+newline+splitTypeString+newline+"Output format: "+format+
				newline+"Duration of the track: "+duration+" seconds = "+ (double)duration/60 + " minutes"+newline+"First number in files name: "+firstNumberInName);
	}

	public static void throwableHandler() {
		JOptionPane.showMessageDialog(null, "Some error occurs!", "ERROR", JOptionPane.ERROR_MESSAGE);
		//System.exit(0);
	}

	//determino il massimo primo numero nel nome
	public static int getMaxFirstNumberInTheName(int splitType,int numberCharacter,int audioFileDuration,int fixedLengthSeconds,int partsNumber) {
		String tempString="";
		for(int i=0;i<numberCharacter;i++)
			tempString+="9";
		if(splitType==1)
			return Integer.parseInt(tempString)-(audioFileDuration/fixedLengthSeconds-1);
		else
			return Integer.parseInt(tempString)-partsNumber;
	}
}
