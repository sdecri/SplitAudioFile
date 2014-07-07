package progetto;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import suoni.*;
import progetto.util.Support;
import progetto.util.filter.supportedFilter;
import progetto.util.thread.ThreadInterruption;
import progetto.util.thread.ThreadSplit;

public class WindowSplit extends JFrame implements ActionListener, TextListener,ChangeListener{
	private final static String currentDirectoryPath="C:\\Users\\Simone\\Desktop\\";
	//ATTRIBUTI
	private final int numberOfCharacter=3;
	private JButton button_origin;
	private JButton button_destination;
	private JButton button_split;
	private JButton button_close;
	private TextField field_nameFile;
	private JSpinner spinner_firstNumberInName;
	private SpinnerModel spinnerModel_firstNumberInName;
	private JFileChooser chooser;
	private JRadioButton radioButton_splitFixedLength;
	private JRadioButton radioButton_splitInParts;
	private JLabel label_partsNumber;
	private JSpinner spinner_partsNumber;
	private SpinnerModel spinnerModel_partsNumber;
	private JLabel label_fixedLengthSeconds;
	private JSpinner spinner_fixedLengthSeconds;
	private SpinnerModel spinnerModel_fixedLengthSeconds;
	private JRadioButton radioButton_formatAsInput;
	private JRadioButton radioButton_formatMp3;
	private JRadioButton radioButton_formatWav;
	private JTextArea log;

	private String originPath;
	private String destinationPath;
	private String outputFile;
	private int splitType;//1 --> split in fixed length; 2 --> split in parts
	private int inputFormat;//1 --> mp3; 2 --> wav
	private int outputFormat;//0 = same as input; 1 --> mp3; 2 --> wav
	private int partsNumber;
	private int fixedLengthSeconds;
	private int audioFileDuration;//durata del brano in secondi
	private final int numberCharacter=3;
	private int firstNumberInName;
	private int maxfirstNumberInTheName;
	//DEFAULT
	private int partsNumberDefault;
	private int fixedLengthSecondsDefault;
	private int firstNumberInNameDefault;

	private boolean clickOrigin;//indica se è stata cliccata almeno una volta il button_origin
	private boolean clickDestination;//indica se è stata cliccata almeno una volta il button_destination
		
	//COSTRUTTORI
	public WindowSplit() {
		super("Audio split");
		setOriginPath("");
		setDestinationPath("");
		setOutputFile("");
		setSplitType(1);
		setOutputFormat(0);
		setPartsNumber(0);
		firstNumberInNameDefault=1;
		firstNumberInName=firstNumberInNameDefault;
		setFixedLengthSeconds(0);
		audioFileDuration=0;
		
		clickOrigin=false;
		clickDestination=false;
		
		
		setLayout(new GridLayout(5,0));
		setDefaultCloseOperation(EXIT_ON_CLOSE);// associamo l'evento di chiusura al solito bottone di chiusura
		setResizable(false);
		setSize(350,510);
		//setLocationRelativeTo(null);//la finestra è posta al centro delo schermo
		setLocation(300, 100);
        log = new JTextArea(5,1);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);

        //Origin, destination
		button_origin = new JButton("Origin folder");
		button_origin.addActionListener(this);
		button_destination = new JButton("Destination folder");
		button_destination.setEnabled(false);
		button_destination.addActionListener(this);
		field_nameFile = new TextField(8);
		field_nameFile.addTextListener(this);
		field_nameFile.setEnabled(false);
		spinner_firstNumberInName = new JSpinner();
		spinner_firstNumberInName.addChangeListener(this);
		//spinner_firstNumberInName.setText("1");
		spinner_firstNumberInName.setEnabled(false);

		
		//radioButton per tipo di split
		radioButton_splitFixedLength=new JRadioButton("Split in fixed length parts",true);
		radioButton_splitFixedLength.addActionListener(this);
		radioButton_splitFixedLength.setEnabled(false);
		radioButton_splitInParts=new JRadioButton("Split in parts");
		radioButton_splitInParts.addActionListener(this);
		radioButton_splitInParts.setEnabled(false);

		// Crea il ButtonGroup e registra i RadioButton
		ButtonGroup group1 = new ButtonGroup();
		group1.add(radioButton_splitFixedLength);
		group1.add(radioButton_splitInParts);
		
		//Label e text field
		label_fixedLengthSeconds = new JLabel("Seconds");
		spinner_fixedLengthSeconds= new JSpinner();
		spinner_fixedLengthSeconds.addChangeListener(this);
		label_fixedLengthSeconds.setEnabled(false);
		spinner_fixedLengthSeconds.setEnabled(false);
		label_partsNumber = new JLabel("Number of parts");
		spinner_partsNumber=new JSpinner();
		spinner_partsNumber.addChangeListener(this);
		label_partsNumber.setEnabled(false);
		spinner_partsNumber.setEnabled(false);
						
		//radioButton per output format
		radioButton_formatAsInput=new JRadioButton("Input file format",true);
		radioButton_formatAsInput.addActionListener(this);
		radioButton_formatAsInput.setEnabled(false);
		radioButton_formatMp3=new JRadioButton("mp3");
		radioButton_formatMp3.addActionListener(this);
		radioButton_formatMp3.setEnabled(false);
		radioButton_formatWav=new JRadioButton("wav");
		radioButton_formatWav.addActionListener(this);
		radioButton_formatWav.setEnabled(false);

		// Crea il ButtonGroup e registra i RadioButton
		ButtonGroup group2 = new ButtonGroup();
		group2.add(radioButton_formatAsInput);
		group2.add(radioButton_formatMp3);
		group2.add(radioButton_formatWav);

		//split, cancel
		button_split = new JButton("Split");
		button_split.addActionListener(this);
		button_split.setEnabled(false);
		button_close= new JButton("Close");
		button_close.addActionListener(this);
		
		//DEFINIZIONE dei panel
		//PanelA - origin, Destination, name
		JPanel panelA=new JPanel(new GridLayout(2,1));
        JPanel panel1 = new JPanel(new FlowLayout());
        panel1.add(button_origin);
        panel1.add(button_destination);
        JPanel panel2 = new JPanel(new FlowLayout());
        panel2.add(new JLabel("Parts name: "));
        panel2.add(field_nameFile);
        panel2.add(new JLabel("_"));
        panel2.add(spinner_firstNumberInName);
        panelA.add(panel1);
        panelA.add(panel2);
        
        //PanelB - split type
		JPanel panelB=new JPanel(new GridLayout(4,1));
		panelB.add(radioButton_splitFixedLength);
		JPanel panel3 = new JPanel(new FlowLayout());
		
		panel3.add(label_fixedLengthSeconds);
		panel3.add(spinner_fixedLengthSeconds);
		panelB.add(panel3);
		panelB.add(radioButton_splitInParts);
		JPanel panel4 = new JPanel(new FlowLayout());
		panel4.add(label_partsNumber);
		panel4.add(spinner_partsNumber);
		panelB.add(panel4);
		
		//PanelC - output format
		JPanel panelC = new JPanel(new GridLayout(4,0));
		panelC.add(new JLabel("Output file format"));
		panelC.add(radioButton_formatAsInput);
		panelC.add(radioButton_formatMp3);
		panelC.add(radioButton_formatWav);
		
		//PanelD - cancel, split
		JPanel panelD = new JPanel(new FlowLayout());
		panelD.add(new JLabel("Support wav and mp3 files"));
		JPanel panel5 = new JPanel(new FlowLayout());
		panel5.add(button_close);
		panel5.add(button_split);
		panelD.add(panel5);
		
		//AGGIUNGO i pannelli al frame
		add(panelA);
		add(panelB);
		add(panelC);
		add(logScrollPane);
		add(panelD);
 
//		SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 3, 1);
//		JSpinner spinner = new JSpinner(spinnerModel);
//		add(spinner);
		
        //Add the buttons and the log to this panel.
		setVisible(true);
		//pack();

    }

	//METODI	
	@Override
	public void actionPerformed(ActionEvent e) {
		//Handle origin button action.
		if (e.getSource() == button_origin) {
			chooser = new JFileChooser(currentDirectoryPath); 
			//chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("Origin directory");
			chooser.setFileFilter(new supportedFilter());
			//chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);  
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				//riabilito tutti gli elementi
				if(!clickOrigin) {
					clickOrigin=true;
					button_destination.setEnabled(true);
					field_nameFile.setEnabled(true);
					spinner_firstNumberInName.setEnabled(true);
					radioButton_splitFixedLength.setEnabled(true);
					label_fixedLengthSeconds.setEnabled(true);
					spinner_fixedLengthSeconds.setEnabled(true);
					radioButton_splitInParts.setEnabled(true);
					radioButton_formatAsInput.setEnabled(true);
					button_split.setEnabled(true);
				}
				originPath=chooser.getSelectedFile().getPath();
				String format=originPath.substring(originPath.length()-3,originPath.length());
				if(format.equalsIgnoreCase("mp3")) {
					inputFormat=1;
					radioButton_formatMp3.setEnabled(false);
					radioButton_formatWav.setEnabled(true);
				}else if(format.equalsIgnoreCase("wav")) {
					inputFormat=2;
					radioButton_formatMp3.setEnabled(true);
					radioButton_formatWav.setEnabled(false);
				}
				radioButton_formatAsInput.doClick();
				if(!clickDestination) {
					destinationPath=chooser.getSelectedFile().getParent();
				}
				try {
					audioFileDuration=suoni.Support.getDurationInSecond(originPath);
					fixedLengthSecondsDefault=audioFileDuration/2+1;
					fixedLengthSeconds=fixedLengthSecondsDefault;
					//definisco lo spinner model
					spinnerModel_fixedLengthSeconds=new SpinnerNumberModel(fixedLengthSecondsDefault, 1, audioFileDuration, 1);
					spinner_fixedLengthSeconds.setModel(spinnerModel_fixedLengthSeconds);
					//field_fixedLengthSeconds.setText(""+fixedLengthSeconds);
					partsNumberDefault=2;
					partsNumber=partsNumberDefault;
					spinnerModel_partsNumber=new SpinnerNumberModel(partsNumberDefault, 1, audioFileDuration, 1);
					spinner_partsNumber.setModel(spinnerModel_partsNumber);
					//field_partsNumber.setText(""+partsNumber);
				} catch (UnsupportedAudioFileException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, e1.getMessage(), "WARNING", JOptionPane.WARNING_MESSAGE);
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null,e1.getMessage(), "WARNING", JOptionPane.WARNING_MESSAGE);
				}
				//determino il massimo primo numero nel nome
				maxfirstNumberInTheName=Support.getMaxFirstNumberInTheName(splitType, numberCharacter, audioFileDuration, fixedLengthSeconds, partsNumber);
				//System.out.println(maxfirstNumberInTheName);
				spinnerModel_firstNumberInName=new SpinnerNumberModel(1, -1, maxfirstNumberInTheName, 1);
				spinner_firstNumberInName.setModel(spinnerModel_firstNumberInName);
				firstNumberInName=1;
				
				String[]temp=originPath.split("\\\\");
				outputFile=temp[temp.length-1].substring(0, temp[temp.length-1].length()-4);
				field_nameFile.setText(outputFile);
				Support.scrivi(log,originPath,destinationPath,outputFile,splitType,outputFormat,fixedLengthSeconds,partsNumber,audioFileDuration,firstNumberInName);
			}	
		}//Handle destination button action.
		else if (e.getSource() == button_destination) {
			chooser = new JFileChooser(); 
			//chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("Destination directory");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				clickDestination=true;
				destinationPath=chooser.getSelectedFile().getPath();
				Support.scrivi(log,originPath,destinationPath,outputFile,splitType,outputFormat,fixedLengthSeconds,partsNumber,audioFileDuration,firstNumberInName);
			}
		}//Handle  fixed length radio button action.
		else if (e.getSource() == radioButton_splitFixedLength) {
			splitType=1;
			label_fixedLengthSeconds.setEnabled(true);
			spinner_fixedLengthSeconds.setEnabled(true);
			label_partsNumber.setEnabled(false);
			spinner_partsNumber.setEnabled(false);
			Support.scrivi(log,originPath,destinationPath,outputFile,splitType,outputFormat,fixedLengthSeconds,partsNumber,audioFileDuration,firstNumberInName);
		}//Handle  part radio button action.
		else if (e.getSource() == radioButton_splitInParts) {
			splitType=2;
			label_fixedLengthSeconds.setEnabled(false);
			spinner_fixedLengthSeconds.setEnabled(false);
			label_partsNumber.setEnabled(true);
			spinner_partsNumber.setEnabled(true);
			Support.scrivi(log,originPath,destinationPath,outputFile,splitType,outputFormat,fixedLengthSeconds,partsNumber,audioFileDuration,firstNumberInName);
		}//Handle input format radio button action.
		else if (e.getSource() == radioButton_formatAsInput) {
			outputFormat=0;
			Support.scrivi(log,originPath,destinationPath,outputFile,splitType,outputFormat,fixedLengthSeconds,partsNumber,audioFileDuration,firstNumberInName);
		}//Handle mp3 format radio button action.
		else if (e.getSource() == radioButton_formatMp3) {
			outputFormat=1;
			Support.scrivi(log,originPath,destinationPath,outputFile,splitType,outputFormat,fixedLengthSeconds,partsNumber,audioFileDuration,firstNumberInName);
		}//Handle wav format radio button action.
		else if (e.getSource() == radioButton_formatWav) {
			outputFormat=2;
			Support.scrivi(log,originPath,destinationPath,outputFile,splitType,outputFormat,fixedLengthSeconds,partsNumber,audioFileDuration,firstNumberInName);
		}//Handle Split button action.
		else if (e.getSource() == button_split) {
			if(originPath.equals("") || destinationPath.equals("") || splitType==0 || (splitType==1 && fixedLengthSeconds==0) || (splitType==2 && partsNumber==0))
				JOptionPane.showMessageDialog(null, "Input data not correctly inserted", "WARNING", JOptionPane.WARNING_MESSAGE);
			else {//lancio il programma
				destinationPath+="\\\\";
				
				setVisible(false);//faccio scomparire la finestra
				
				//Lancio 2 Thread
				//Thread 1 --> Finistra elaborazione
				//Thread 2 --> processo di split
				//in questo modo posso interrompere il processo di split dal primo thread
				
				ThreadSplit splitThread = new ThreadSplit(this);
				Elaboration elaboration=new Elaboration(this,splitThread);
				splitThread.setElaboration(elaboration);
				splitThread.start();
				
			}
		}//Handle Cancel button action.
		else if (e.getSource() == button_close) {
			System.exit(0);
		}
			
	}

	@Override
	public void textValueChanged(TextEvent e) {
		//Handle text name change event
		if(e.getSource()==field_nameFile) {
			setOutputFile(field_nameFile.getText());
			Support.scrivi(log,originPath,destinationPath,outputFile,splitType,outputFormat,fixedLengthSeconds,partsNumber,audioFileDuration,firstNumberInName);
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		//Handle first number in name change event
		if (e.getSource() == spinner_firstNumberInName) {
			try {
				if(spinner_firstNumberInName.getValue().toString().equals("-1")) {//non è stato specificato un numero
					firstNumberInName=-1;
				}else {
					firstNumberInName=Integer.parseInt(spinner_firstNumberInName.getValue().toString());
					if(firstNumberInName<-1) {
						JOptionPane.showMessageDialog(null, "Insert an integer greater than -1", "WARNING", JOptionPane.WARNING_MESSAGE);
						firstNumberInName=firstNumberInNameDefault;
						spinner_firstNumberInName.setValue(firstNumberInNameDefault);
					}
					//Controllo la correttezza del dato inserito
					else if(firstNumberInName>=maxfirstNumberInTheName) {
						JOptionPane.showMessageDialog(null, "The firt number in the name must be minor than "+maxfirstNumberInTheName, "WARNING", JOptionPane.WARNING_MESSAGE);
						firstNumberInName=firstNumberInNameDefault;
						spinner_firstNumberInName.setValue(firstNumberInNameDefault);
					}
				}
				Support.scrivi(log,originPath,destinationPath,outputFile,splitType,outputFormat,fixedLengthSeconds,partsNumber,audioFileDuration,firstNumberInName);
			
			}catch(NumberFormatException ex){
				JOptionPane.showMessageDialog(null, "Insert an integer greater than -1", "WARNING", JOptionPane.WARNING_MESSAGE);
				firstNumberInName=firstNumberInNameDefault;
				spinner_firstNumberInName.setValue(firstNumberInNameDefault);
				Support.scrivi(log,originPath,destinationPath,outputFile,splitType,outputFormat,fixedLengthSeconds,partsNumber,audioFileDuration,firstNumberInName);
			}
		}//Handle text seconds change event
		else if (e.getSource() == spinner_fixedLengthSeconds) {
			try {
				fixedLengthSeconds=Integer.parseInt(spinner_fixedLengthSeconds.getValue().toString());
				if(fixedLengthSeconds<=0) {
					JOptionPane.showMessageDialog(null, "Insert a positive non null integer", "WARNING", JOptionPane.WARNING_MESSAGE);
					fixedLengthSeconds=fixedLengthSecondsDefault;
					spinner_fixedLengthSeconds.setValue(fixedLengthSeconds);
				}
				//VERIFICO che la durata in secondi inserita sia corretta
				if (fixedLengthSeconds<=0) {
					JOptionPane.showMessageDialog(null, "Second length part must be greater than 0", "WARNING", JOptionPane.WARNING_MESSAGE);
					fixedLengthSeconds=fixedLengthSecondsDefault;
					spinner_fixedLengthSeconds.setValue(fixedLengthSeconds);
				}else if (fixedLengthSeconds>audioFileDuration) {
					JOptionPane.showMessageDialog(null, "Second length part must be minor than total sencond length ("+audioFileDuration+") of the file selected to be converted", "WARNING", JOptionPane.WARNING_MESSAGE);
					fixedLengthSeconds=fixedLengthSecondsDefault;
					spinner_fixedLengthSeconds.setValue(fixedLengthSeconds);
				}
				
				//verifico che il primo numero del nome sia corretto
				maxfirstNumberInTheName=Support.getMaxFirstNumberInTheName(splitType, numberCharacter, audioFileDuration, fixedLengthSeconds, partsNumber);
				spinnerModel_firstNumberInName=new SpinnerNumberModel(1, -1, maxfirstNumberInTheName, 1);
				spinner_firstNumberInName.setModel(spinnerModel_firstNumberInName);
				
				Support.scrivi(log,originPath,destinationPath,outputFile,splitType,outputFormat,fixedLengthSeconds,partsNumber,audioFileDuration,firstNumberInName);
			}catch(NumberFormatException ex){
				if(!(spinner_fixedLengthSeconds.getValue().toString().equals(""))) {
					JOptionPane.showMessageDialog(null, "Insert a positive non null integer", "WARNING", JOptionPane.WARNING_MESSAGE);
					spinner_fixedLengthSeconds.setValue("");
				}
				fixedLengthSeconds=0;
				Support.scrivi(log,originPath,destinationPath,outputFile,splitType,outputFormat,fixedLengthSeconds,partsNumber,audioFileDuration,firstNumberInName);
			}
		}//Handle text number parts change event
		else if (e.getSource() == spinner_partsNumber) {
			try {
				partsNumber=Integer.parseInt(spinner_partsNumber.getValue().toString());
				if(partsNumber<=0) {
					JOptionPane.showMessageDialog(null, "Insert a positive non null integer", "WARNING", JOptionPane.WARNING_MESSAGE);
					partsNumber=partsNumberDefault;
					spinner_partsNumber.setValue(partsNumber);
				}
				
				//VERIFICO che il numero di parti inserito sia corretto
				if (partsNumber<1) {
					JOptionPane.showMessageDialog(null, "The number of parts must be greater or equal than 1", "WARNING", JOptionPane.WARNING_MESSAGE);
					partsNumber=partsNumberDefault;
					spinner_partsNumber.setValue(partsNumber);
				}else if (partsNumber>audioFileDuration) {
					JOptionPane.showMessageDialog(null, "The number of part must be minor than the number of seconds ("+audioFileDuration+") of the audio file selected", "WARNING", JOptionPane.WARNING_MESSAGE);
					partsNumber=partsNumberDefault;
					spinner_partsNumber.setValue(partsNumber);
				}
				
				//verifico che il primo numero del nome sia corretto
				maxfirstNumberInTheName=Support.getMaxFirstNumberInTheName(splitType, numberCharacter, audioFileDuration, fixedLengthSeconds, partsNumber);
				spinnerModel_firstNumberInName=new SpinnerNumberModel(1, -1, maxfirstNumberInTheName, 1);
				spinner_firstNumberInName.setModel(spinnerModel_firstNumberInName);
	
				Support.scrivi(log,originPath,destinationPath,outputFile,splitType,outputFormat,fixedLengthSeconds,partsNumber,audioFileDuration,firstNumberInName);
			}catch(NumberFormatException ex){
				if(!(spinner_partsNumber.getValue().toString().equals(""))) {
					JOptionPane.showMessageDialog(null, "Insert a positive non null integer", "WARNING", JOptionPane.WARNING_MESSAGE);
					spinner_partsNumber.setValue("");
				}
				partsNumber=0;
				Support.scrivi(log,originPath,destinationPath,outputFile,splitType,outputFormat,fixedLengthSeconds,partsNumber,audioFileDuration,firstNumberInName);
			}
		}
	}


	//Getter and setter
	public int getOutputFormat() {
		return outputFormat;
	}

	
	public void setOutputFormat(int outputFormat) {
		this.outputFormat = outputFormat;
	}

	public int getInputFormat() {
		return inputFormat;
	}

	public void setInputFormat(int inputFormat) {
		this.inputFormat = inputFormat;
	}

	
	public int getSplitType() {
		return splitType;
	}

	
	public void setSplitType(int splitType) {
		this.splitType = splitType;
	}

	public String getOriginPath() {
		return originPath;
	}

	public void setOriginPath(String originPath) {
		this.originPath = originPath;
	}

	public int getFixedLengthSeconds() {
		return fixedLengthSeconds;
	}

	public void setFixedLengthSeconds(int fixedLengthSeconds) {
		this.fixedLengthSeconds = fixedLengthSeconds;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public int getNumberofcharacter() {
		return numberOfCharacter;
	}

	public int getPartsNumber() {
		return partsNumber;
	}

	public void setPartsNumber(int partsNumber) {
		this.partsNumber = partsNumber;
	}

	public int getFirstNumberInName() {
		return firstNumberInName;
	}

	public void setFirstNumberInName(int firstNumberInName) {
		this.firstNumberInName = firstNumberInName;
	}
	
	public int getAudioFileDuration() {
		return audioFileDuration;
	}

	public void setAudioFileDuration(int audioFileDuration) {
		this.audioFileDuration = audioFileDuration;
	}

	public JSpinner getSpinner_firstNumberInName() {
		return spinner_firstNumberInName;
	}

	public void setSpinner_firstNumberInName(JSpinner spinner_firstNumberInName) {
		this.spinner_firstNumberInName = spinner_firstNumberInName;
	}

	public JSpinner getSpinner_partsNumber() {
		return spinner_partsNumber;
	}

	public void setSpinner_partsNumber(JSpinner spinner_partsNumber) {
		this.spinner_partsNumber = spinner_partsNumber;
	}

	public JSpinner getSpinner_fixedLengthSeconds() {
		return spinner_fixedLengthSeconds;
	}

	public void setSpinner_fixedLengthSeconds(JSpinner spinner_fixedLengthSeconds) {
		this.spinner_fixedLengthSeconds = spinner_fixedLengthSeconds;
	}


}
