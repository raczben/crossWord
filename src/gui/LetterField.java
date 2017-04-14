package gui;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import main.Canvas;

public class LetterField extends JTextField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5695269407827337274L;
	CanvasEditor canvas;
	int x;
	int y;
	
	public LetterField(int x, int y, CanvasEditor canvas) {
		super(1);
		this.x = x;
		this.y = y;
		this.canvas = canvas;
		setFont(new Font("Tahoma", Font.PLAIN, 24));
		setHorizontalAlignment(SwingConstants.CENTER);
		getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setTextHost();
			}
			public void removeUpdate(DocumentEvent e) {
				setTextHost();
			}
			public void insertUpdate(DocumentEvent e) {
				setTextHost();
			}
		});
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
//				System.out.println("HELLO");
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
//				System.out.println("HELLO2");
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					canvas.setLetterFocus(x, y-1);
					break;
				case KeyEvent.VK_DOWN:
					canvas.setLetterFocus(x, y+1);
					break;
				case KeyEvent.VK_RIGHT:
					canvas.setLetterFocus(x+1, y);
					break;
				case KeyEvent.VK_LEFT:
					canvas.setLetterFocus(x-1, y);
					break;

				default:
					break;
				}
				
			}
		});

	}
	 class TextSetter implements Runnable {
		 
	      String newText;
	      
	      TextSetter(String txt){
	    	  newText = txt;
	      }

		public void run() {
	        try {
	          setText(newText);
	        } catch (Exception x) {
	          x.printStackTrace();
	        }
	      }
	    };
	    
	void setTextHost(){
		String text = getText();
		if (text.length()>1){
			text = text.substring(text.length()-1);
			TextSetter setTextRun = new TextSetter(text);
			SwingUtilities.invokeLater(setTextRun);
		}
		
	  }
	
}
