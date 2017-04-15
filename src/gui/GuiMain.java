package gui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import main.Canvas;
import main.Generator;
import config.Config;
import config.ConfigLoader;
import config.ConfigWriter;

public class GuiMain {

	int xDim = 3;
	int yDim = 3;
	JFrame guiFrame;
	Config cfg;
	CanvasEditor cnvEditor;
	ControlGenerator ctrlGen;
	private ArrayList<Canvas> goodCanvasList = null;
	Generator generator;
	Settings settings;
	private int activeIndex;

	public GuiMain()
	{
		cfg = new Config();

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



		guiFrame = new JFrame();
		guiFrame.setMinimumSize(new Dimension(480, 300));
		guiFrame.setPreferredSize(new Dimension(500, 500));

		//make sure the program exits when the frame closes
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setTitle("Example GUI");
		guiFrame.setSize(582,344);

		guiFrame.setLocationRelativeTo(null);
		guiFrame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel wrapper = new JPanel();
		wrapper.setAutoscrolls(true);

		cnvEditor = new CanvasEditor(xDim, yDim);
		cnvEditor.setMinimumSize(new Dimension(120, 120));
		cnvEditor.setMaximumSize(new Dimension(120, 120));
		cnvEditor.setSize(new Dimension(120, 120));

		settings = new Settings(xDim, yDim, this);
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
		settings.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		settings.setMaximumSize(new Dimension(200, 200));
		guiFrame.getContentPane().add(settings, BorderLayout.EAST);

		JPanel panel_1 = new JPanel();
		guiFrame.getContentPane().add(panel_1, BorderLayout.NORTH);

		ctrlGen = new ControlGenerator(this);
		guiFrame.getContentPane().add(ctrlGen, BorderLayout.SOUTH);



		JMenuBar menuBar = setupMenu();
		guiFrame.setJMenuBar(menuBar);


		//make sure the JFrame is visible
		guiFrame.setVisible(true);
	}

	/**
	 * @return
	 */
	private JMenuBar setupMenu() {
		JMenuBar menuBar = new JMenuBar();

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpenload = new JMenuItem("Open (Load)...");
		mntmOpenload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				final JFileChooser fc = new JFileChooser();
				//        		fc.setFileFilter(filter);
				int returnVal = fc.showOpenDialog(guiFrame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					System.out.println("Opening: " + file.getName() + ".");
					ConfigLoader cfgloader = new ConfigLoader();
					try {
						cfg = cfgloader.readConfig(new FileInputStream(file));
						applyConfig();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

		});
		mnFile.add(mntmOpenload);

		JMenuItem mntmSaveAs = new JMenuItem("Save As..");
		mnFile.add(mntmSaveAs);
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				final JFileChooser fc = new JFileChooser();
				//        		fc.setFileFilter(filter);
				int returnVal = fc.showSaveDialog(guiFrame);
				System.out.println(returnVal);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					System.out.println("Opening: " + file.getName() + ".");
					ConfigWriter cfgwriter = new ConfigWriter();
					cfgwriter.setFile(file.getAbsolutePath());
					try {
						cfg.cnv = cnvEditor.toCanvas();
						cfgwriter.saveConfig(cfg);
						//						applyConfig();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

		});

		JMenuItem mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mnFile.add(mntmQuit);

		JMenuItem mntmNewMenuItem = new JMenuItem("New menu item");
		mnFile.add(mntmNewMenuItem);
		return menuBar;
	}


	private void applyConfig() {
		this.cnvEditor.load(cfg.cnv);
	}

	public void update(int all, int done, int success){
		ctrlGen.updateStatus(all, done, success);
	}

	public void setGoodCanvasList(ArrayList<Canvas> goodCanvasList) {
		this.goodCanvasList  = goodCanvasList;
		setActiveCanvas(0);
	}

	public Generator getGenerator() {
		return generator;
	}

	public Canvas getActiveCanvas() {
		return goodCanvasList.get(activeIndex);
	}

	public int setActiveCanvas(int idx) {
		activeIndex = idx;
		Canvas activeCanvas;
		
		if(activeIndex<0){
			activeIndex = 0;
		} else if(activeIndex >= goodCanvasList.size()){
			activeIndex = goodCanvasList.size()-1;
		}
		activeCanvas = goodCanvasList.get(activeIndex);
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				cnvEditor.load(activeCanvas);
				settings.showResult();
			}
		});
		return activeIndex;
	}

}