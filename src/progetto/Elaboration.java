package progetto;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Elaboration extends JFrame implements ActionListener{
	//ATTRIBUTI
	private JLabel message;
	private JButton button_cancel;
	private JFrame frame;
	private Thread threadToInterrupt;
	
	
	//COSTRUTTORI
	public Elaboration(JFrame f,Thread t) {
		super("Spliting");
		frame=f;
		threadToInterrupt=t;
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);// associamo l'evento di chiusura al solito bottone di chiusura
		setResizable(false);
		setLocation(400, 275);//la finestra è posta al centro delo schermo
		setSize(300, 100);//dimensioni della finestra: LARGHEZZA, ALTEZZA in pixel
		setVisible(true);//rendo non visibile la finestra
		//istanzio elementi
		//inserire metodo setMessage
		button_cancel=new JButton("Cancel");
		button_cancel.addActionListener(this);
		JPanel panel = new JPanel();
		panel.add(button_cancel);
		add(panel,BorderLayout.SOUTH);
		//message=new JLabel("Converting file: "+ConvertingFile+"/"+totFileToConvert);
		message=new JLabel("Spliting file...");
		add(message,BorderLayout.CENTER);
		//pack();
	}
	
	public static void main(String []args) {
		//new Elaboration();
	}
	
	//METODI	
	public void actionPerformed(ActionEvent e){
		JOptionPane.showMessageDialog(null, "The process was interrupted!", "ATTENTION", JOptionPane.WARNING_MESSAGE);
		threadToInterrupt.stop();
		frame.setVisible(true);
		dispose();
		//System.exit(0);
	}

}
