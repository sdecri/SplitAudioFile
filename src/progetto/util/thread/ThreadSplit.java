package progetto.util.thread;

import java.io.File;

import javax.swing.JOptionPane;

import progetto.Elaboration;
import progetto.WindowSplit;

import javazoom.jl.decoder.JavaLayerException;

import suoni.*;
import progetto.util.Support;

public class ThreadSplit extends Thread{
	//ATTRIBUTI
	WindowSplit split;
	Elaboration elaboration;

	//COSTRUTTORI
	public ThreadSplit(WindowSplit s,Elaboration el) {
		super();
		split=s;
		elaboration=el;
	}
	public ThreadSplit(WindowSplit s) {
		this(s, null);
	}
	public ThreadSplit() {
		this(null,null);
	}
	
	//METODI
	public void run() {
		boolean splitCompleted=false;
		//Split a seconda delle opzioni selezionate
		if(split.getOutputFormat()==0) {//output format = input format
			if(split.getFirstNumberInName()==-1 && 
					(  (split.getSplitType()==1 && split.getFixedLengthSeconds()==split.getAudioFileDuration()) ||  (split.getSplitType()==2 && split.getPartsNumber()==1))  ) {
				JOptionPane.showMessageDialog(null, "Output file is input file!!", "ATTENTION", JOptionPane.WARNING_MESSAGE);
			}else if(split.getInputFormat()==1) {//mp3
				try {
					switch (split.getSplitType()) {
					case 1://fixed length
						Utils.splitMp3FixedLength(split.getOriginPath(),split.getFixedLengthSeconds(),split.getDestinationPath(),split.getOutputFile(),split.getNumberofcharacter(),split.getFirstNumberInName());
						break;
					case 2://in parts
						Utils.splitMp3InPart(split.getOriginPath(),split.getPartsNumber(),split.getDestinationPath(),split.getOutputFile(),split.getNumberofcharacter(),split.getFirstNumberInName());
						break;
					}
					splitCompleted=true;
				}catch (ThreadDeath e) {
					e.printStackTrace();
				}catch (Throwable e) {
					e.printStackTrace();
					System.out.println(e);
					Support.throwableHandler();
					System.exit(0);
				}
			}else if (split.getInputFormat()==2) {//wav
				try {
					switch (split.getSplitType()) {
					case 1://fixed length
						Utils.splitWavFixedLength(split.getOriginPath(),split.getFixedLengthSeconds(),split.getDestinationPath(),split.getOutputFile(),split.getNumberofcharacter(),split.getFirstNumberInName());
						break;
					case 2://in parts
						Utils.splitWavInPart(split.getOriginPath(),split.getPartsNumber(),split.getDestinationPath(),split.getOutputFile(),split.getNumberofcharacter(),split.getFirstNumberInName());
						break;
					}
					splitCompleted=true;
				}catch (ThreadDeath e) {
					e.printStackTrace();
				} catch (Throwable e) {
					e.printStackTrace();
					System.out.println(e);
					Support.throwableHandler();
					System.exit(0);
				}
			}
		}else if(split.getOutputFormat()==1) {//inputFormat = wav; outputFormat = mp3
			if(split.getFirstNumberInName()==-1 &&
					(  (split.getSplitType()==1 && split.getFixedLengthSeconds()==split.getAudioFileDuration()) ||  (split.getSplitType()==2 && split.getPartsNumber()==1))  ) {
				try {
					Utils.convertAudioFile(split.getOriginPath(), split.getDestinationPath()+"\\\\"+split.getOutputFile()+".mp3");
				} catch (JavaLayerException e) {
					e.printStackTrace();
				}
				splitCompleted=true;
			}else {
			//prima splitto il wav e poi i singoli wav vengono convertiti in mp3
				String []wavParts=null;
				try {
					switch (split.getSplitType()) {
					case 1://fixed length
						wavParts = Utils.splitWavFixedLength(split.getOriginPath(),split.getFixedLengthSeconds(),split.getDestinationPath(),split.getOutputFile(),split.getNumberofcharacter(),split.getFirstNumberInName());
						break;
					case 2://in parts
						wavParts =Utils.splitWavInPart(split.getOriginPath(),split.getPartsNumber(),split.getDestinationPath(),split.getOutputFile(),split.getNumberofcharacter(),split.getFirstNumberInName());
						break;
					}
					//converto i singoli wav
					for(int i=0;i<wavParts.length;i++) {
						String wavFile = wavParts[i];
						String mp3File = wavFile.substring(0, wavFile.length()-4)+".mp3";
						File waveToDelete=new File(wavFile);
						Utils.convertAudioFile(wavFile, mp3File);
						//cancello il wavParts[i]
						waveToDelete.delete();
					}
					splitCompleted=true;
				}catch (ThreadDeath e) {
					e.printStackTrace();
				} catch (Throwable e) {
					e.printStackTrace();
					System.out.println(e);
					Support.throwableHandler();
					System.exit(0);
				}
			}
		}else if(split.getOutputFormat()==2) {//inputFormat = mp3; outputFormat = wav
			if(split.getFirstNumberInName()==-1 && 
					(  (split.getSplitType()==1 && split.getFixedLengthSeconds()==split.getAudioFileDuration()) ||  (split.getSplitType()==2 && split.getPartsNumber()==1))  ) {
				try {
					Utils.convertAudioFile(split.getOriginPath(), split.getDestinationPath()+"\\\\"+split.getOutputFile()+".wav");
				} catch (JavaLayerException e) {
					e.printStackTrace();
				}
				splitCompleted=true;
			}else {
				//prima converto l'mp3 in wav e poi splitto il wav
				String wavFile=null;
				String mp3File=null;
				try {
					mp3File=split.getOriginPath();
					wavFile=mp3File.substring(0, mp3File.length()-4)+".wav";
					Utils.convertAudioFile(mp3File, wavFile);//devo ricordarmi di cancellare il wavFile
					split.setOriginPath(wavFile);
					switch (split.getSplitType()) {
					case 1://fixed length
						Utils.splitWavFixedLength(split.getOriginPath(),split.getFixedLengthSeconds(),split.getDestinationPath(),split.getOutputFile(),split.getNumberofcharacter(),split.getFirstNumberInName());
						break;
					case 2://in parts
						Utils.splitWavInPart(split.getOriginPath(),split.getPartsNumber(),split.getDestinationPath(),split.getOutputFile(),split.getNumberofcharacter(),split.getFirstNumberInName());
						break;
					}
					splitCompleted=true;
				}catch (ThreadDeath e) {
					e.printStackTrace();
				} catch (Throwable e) {
					e.printStackTrace();
					System.out.println(e);
					Support.throwableHandler();
					System.exit(0);
				}finally {
					//Cancello il wav
					File f = new File(wavFile);
					f.delete();
				}
			}
		}
		//ho effettuato lo split saluto e me ne vado
		if(splitCompleted)JOptionPane.showMessageDialog(null, "Split completed", "INFORMATION", JOptionPane.PLAIN_MESSAGE);
		elaboration.dispose();
		split.setVisible(true);
	}

	//GETTERS and SETTERS
	public WindowSplit getSplit() {
		return split;
	}

	public void setSplit(WindowSplit split) {
		this.split = split;
	}
	public Elaboration getElaboration() {
		return elaboration;
	}
	public void setElaboration(Elaboration elaboration) {
		this.elaboration = elaboration;
	}

}
