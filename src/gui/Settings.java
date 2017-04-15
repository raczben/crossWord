package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Canvas;
import main.Generator;


public class Settings extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -599560588978919215L;

	CanvasEditor cnvEditor;
	JSpinner spinnerX;
	JSpinner spinnerY;
	GuiMain mainGui; 

	public Settings(int xDim, int yDim, GuiMain mainGui) {
		this.mainGui = mainGui;
		setMinimumSize(new Dimension(200, 100));
		setMaximumSize(new Dimension(200, 100));
		this.cnvEditor = mainGui.cnvEditor;
		setPreferredSize(new Dimension(200, 100));
		setLayout(null);

		JLabel lblCol = new JLabel("col");
		lblCol.setBounds(0, 0, 100, 25);
		add(lblCol);

		spinnerX = new JSpinner();
		spinnerX.setBounds(100, 0, 100, 25);
		spinnerX.setValue(xDim);
		add(spinnerX);

		JLabel lblRows = new JLabel("rows");
		lblRows.setBounds(0, 25, 100, 25);
		add(lblRows);

		spinnerY = new JSpinner();
		spinnerY.setBounds(100, 25, 100, 25);
		spinnerY.setValue(yDim);
		add(spinnerY);

		JButton btnNewButton = new JButton("Generate");
		btnNewButton.setBounds(0, 50, 100, 25);
		add(btnNewButton);
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Canvas cnv = cnvEditor.toCanvas();
				Generator generator =  new Generator(cnv);
				mainGui.generator = generator;
				generator.generateGui(mainGui, 100);
				
			}
		});
		
		spinnerY.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				resizeCanvas();
			}
		});
		spinnerX.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				resizeCanvas();
			}
		});
	}

	private void resizeCanvas() {
		int x = (Integer)spinnerX.getValue();
		int y = (Integer)spinnerY.getValue();
		if(x>15){
			JOptionPane.showMessageDialog(null, "The width must be smaller than 16", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(x<2){
			JOptionPane.showMessageDialog(null, "The width must be grater than 1", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(y>15){
			JOptionPane.showMessageDialog(null, "The height must be smaller than 16", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(y<2){
			JOptionPane.showMessageDialog(null, "The height must be grater than 1", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
//		System.out.println("x:" +x + " y : " + y);
		cnvEditor.init(x, y);
	}
}
