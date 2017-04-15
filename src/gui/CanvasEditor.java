package gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import main.Canvas;

public class CanvasEditor extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6993662748106493317L;
	private JTextField textFields[][];

	public CanvasEditor(int col, int row) {
		init(col, row);
	}

	public void init(int col, int row) {
		removeAll();
		JTextField[][] textFieldsNew = new JTextField[col][row];
		setPreferredSize(new Dimension(col*40, row*40));
		setLayout(new GridLayout(row, col));
		for(int y = 0 ; y < row ; y++){
			for(int x = 0 ; x < col ; x++){
				JTextField texField = new LetterField(x, y, this);
				add(texField);
				textFieldsNew[x][y] = texField;
				texField.setColumns(1);
				try{
					String text = textFields[x][y].getText();
					texField.setText(text);
				} catch(NullPointerException npex){
					// The textFieldsOther is null.
				} catch(ArrayIndexOutOfBoundsException aob){
					// The textFieldsOther is smaller.
				}
			}
		}
		this.textFields = textFieldsNew;
		setVisible(true);
		revalidate();
		repaint();
	}
	
	

	public void setLetterFocus(int x, int y) {
		x = (x+textFields.length)%textFields.length;
		y = (y+textFields[x].length)%textFields[x].length;
		textFields[x][y].requestFocus();
	}


	Canvas toCanvas(){
		Canvas ret = new Canvas(textFields.length, textFields[0].length);

		for(int x = 0; x<textFields.length; x++){
			for(int y = 0; y<textFields[0].length; y++){
				Character ch;
				try{
					ch = new Character(textFields[x][y].getText().charAt(0));
				} catch(StringIndexOutOfBoundsException strex){	// Empty cell
					ch = '.';
				}
				ret.setCharAt(ch, x, y);
			}
		}

		return ret;
	}
	
	void load(Canvas canvas){
		removeAll();
		int col = canvas.getWidth();
		int row = canvas.getHeight();
		textFields = new JTextField[col][row];
		setPreferredSize(new Dimension(col*40, row*40));
		setLayout(new GridLayout(row, col));
		for(int y = 0 ; y < row ; y++){
			for(int x = 0 ; x < col ; x++){
				JTextField texField = new LetterField(x, y, this);
				add(texField);
				textFields[x][y] = texField;
				texField.setColumns(1);
				try{
//					String text = textFields[x][y].getText();
					texField.setText(canvas.getCharAt(x, y).toString());
				} catch(NullPointerException npex){
					// The textFieldsOther is null.
				} catch(ArrayIndexOutOfBoundsException aob){
					// The textFieldsOther is smaller.
				}
			}
		}
//		this.textFields = textFieldsNew;
		setVisible(true);
		revalidate();
		repaint();
	}


}
