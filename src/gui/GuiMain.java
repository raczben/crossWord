package gui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GuiMain {
	
	int xDim = 3;
	int yDim = 3;
	public GuiMain()
    {
		
		
		 try {
	            // Set cross-platform Java L&F (also called "Metal")
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
		 
		 
		 
        JFrame guiFrame = new JFrame();
        guiFrame.setPreferredSize(new Dimension(500, 500));
        
        //make sure the program exits when the frame closes
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle("Example GUI");
        guiFrame.setSize(582,344);
      
        //This will center the JFrame in the middle of the screen
        guiFrame.setLocationRelativeTo(null);
//        cnvEditor.setSize(new Dimension(100, 100));
        guiFrame.getContentPane().setLayout(new BorderLayout(0, 0));
        
        JPanel panel_2 = new JPanel();
        guiFrame.getContentPane().add(panel_2, BorderLayout.SOUTH);
        
        JPanel wrapper = new JPanel();
        wrapper.setAutoscrolls(true);
        
        CanvasEditor cnvEditor = new CanvasEditor(xDim, yDim);
        cnvEditor.setMinimumSize(new Dimension(120, 120));
        cnvEditor.setMaximumSize(new Dimension(120, 120));
        cnvEditor.setSize(new Dimension(120, 120));
        
        Settings settings = new Settings(xDim, yDim, cnvEditor);
        JScrollPane scrollPane = new JScrollPane(wrapper);
        GroupLayout gl_wrapper = new GroupLayout(wrapper);
        gl_wrapper.setHorizontalGroup(
        	gl_wrapper.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_wrapper.createSequentialGroup()
        			.addGap(7)
        			.addComponent(cnvEditor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        gl_wrapper.setVerticalGroup(
        	gl_wrapper.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_wrapper.createSequentialGroup()
        			.addGap(7)
        			.addComponent(cnvEditor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        wrapper.setLayout(gl_wrapper);
        guiFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
//        guiFrame.getContentPane().add(cnvEditor);
//        scrollPane.add(cnvEditor);
        
        JPanel panel_1 = new JPanel();
        guiFrame.getContentPane().add(panel_1, BorderLayout.NORTH);
        settings.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        settings.setMaximumSize(new Dimension(200, 200));
        guiFrame.getContentPane().add(settings, BorderLayout.EAST);
        
        
        
        JMenuBar menuBar = new JMenuBar();
        guiFrame.setJMenuBar(menuBar);
        
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        
        JMenuItem mntmQuit = new JMenuItem("Quit");
        mnFile.add(mntmQuit);
        
        
        //make sure the JFrame is visible
        guiFrame.setVisible(true);
    }
    
}