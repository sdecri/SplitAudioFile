package progetto.util.thread;

import javax.swing.JFrame;

import progetto.Elaboration;

import progetto.util.Support;

public class ThreadInterruption extends Thread{
	//ATTRIBUTI
	JFrame frame;
	Thread threadToInterrupt;
	
	//COSTRUTTORI
	public ThreadInterruption(JFrame f,Thread t) {
		super();
		frame=f;
		threadToInterrupt=t;
	}
	public ThreadInterruption(JFrame f) {
		this(f,null);
	}
	public ThreadInterruption(Thread t) {
		this(null,t);
	}
	public ThreadInterruption() {
		this(null,null);
	}

	
	//METODI
	public void run() {
			new Elaboration(frame,threadToInterrupt);
	}

	//GETTERS and SETTERS
	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public Thread getThreadToInterrupt() {
		return threadToInterrupt;
	}

	public void setThreadToInterrupt(Thread threadToInterrupt) {
		this.threadToInterrupt = threadToInterrupt;
	}

	
}
