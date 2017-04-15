package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.JLabel;

public class ControlGenerator extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3285469737823499229L;
	private JProgressBar progressBar;
	JLabel label;
	GuiMain mainGui;

	public ControlGenerator(GuiMain mainGui) {
		this.mainGui = mainGui;
		setMinimumSize(new Dimension(200, 100));
		setMaximumSize(new Dimension(200, 100));
		setPreferredSize(new Dimension(500, 30));
		setLayout(null);
		progressBar = new JProgressBar();
		progressBar.setBounds(10, 8, 180, 14);
		add(progressBar);
		progressBar.setValue(0);
		
		JButton btnStop = new JButton("Stop");
		btnStop.setBounds(202, 8, 55, 14);
		add(btnStop);

		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					mainGui.getGenerator().stop();
				} catch(Exception ex){
					label.setText("Start first.");
				}
			}
		});
		
		label = new JLabel("");
		label.setBounds(272, 8, 218, 14);
		add(label);
	}
	

	
	void updateStatus(int all, int done, int success){
	    javax.swing.SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	int progressVal = done*100/all;
	    		progressBar.setValue(progressVal);
	    		System.out.println("All seeds: " + all + " done: " + done + " success: " + success);
	    		label.setText("All seeds: " + all + " done: " + done + " success: " + success);
//	    		label.
	        }
	    });
	}
}
