package org.artorg.tools.phantomData.client.boot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.text.DefaultCaret;

import org.artorg.tools.phantomData.client.Main;

public class BootUtils extends org.artorg.tools.phantomData.server.boot.BootUtils{
	private static String text = "";
	private static JFrame consoleframe;
	private static JFrame startupFrame;
	private static PrintStream defaultOut;
	private static PrintStream defaultErr;
	private static boolean errorOccured; 
	private static JProgressBar progressBar;
	private static JTextArea textArea;
	
	static {
		
		textArea = new JTextArea();
	}
	
	public static boolean runWithConsoleFrame(Runnable rc) {
		consoleframe = new JFrame();
		
		
		
		if (!isConnected())
			showConsoleFrame();
		
		try {
			rc.run();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	public static void closeConsoleFrame() {
//		if (!errorOccured && consoleframe != null) {
//			consoleframe.setVisible(false);
//			consoleframe.dispose();
//			System.setOut(defaultOut);
//			System.setErr(defaultErr);
//		}
	}
	
	public static void showStartupFrame() throws IOException {
		startupFrame = new JFrame();
		startupFrame.setSize(300, 300);
		startupFrame.setTitle("Phantom Database");
		startupFrame.setResizable(false);
		startupFrame.setUndecorated(true);
		startupFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	    Container content = startupFrame.getContentPane();
	    content.setLayout(new BorderLayout());
	    
	    BufferedImage myPicture = ImageIO.read(Main.class.getClassLoader().getResourceAsStream("img/startup.png"));
		JLabel startupImageLabel = new JLabel(new ImageIcon(myPicture));
	    content.add(startupImageLabel, BorderLayout.NORTH);
		
		JPanel test = new JPanel();
		test.setLayout(new BorderLayout());
	    JLabel label = new JLabel();
	    label.setText("Test");
	    progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setForeground(Color.green);
		test.add(label, BorderLayout.WEST);
		test.add(progressBar, BorderLayout.PAGE_END);
		content.add(test, BorderLayout.PAGE_END);
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - startupFrame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - startupFrame.getHeight()) / 2);
	    startupFrame.setLocation(x, y);
	    startupFrame.pack();
	    startupFrame.setVisible(true);
	    redirectConsoleOuptut();
	}
	
	private static void redirectConsoleOuptut() {
		defaultOut = System.out;
		defaultErr = System.err;
		
		System.setOut(new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			  text = text +String.valueOf((char) b);
			  if (text.endsWith("\n") || text.endsWith("\r")) {
				  List<String> lines = lineSplitter(text);
				  
				  progressBar.setValue(progressBar.getValue()+1);
				  int start = lines.size() - 2000;
						if (start < 0)
							start = 0;

						final String output2 = lines.subList(start, lines.size()).stream()
								.collect(Collectors.joining("\n"));
						textArea.setText(output2);
					}
				}
			}));
		
			System.setErr(new PrintStream(new OutputStream() {
				@Override
				public void write(int b) throws IOException {
				  text = text +String.valueOf((char) b);
				  if (text.endsWith("\n") || text.endsWith("\r")) {
					  List<String> lines = lineSplitter(text);
					  errorOccured = true;
					  int start = lines.size() - 2000;
							if (start < 0)
								start = 0;
		
							final String output2 = lines.subList(start, lines.size()).stream()
									.collect(Collectors.joining("\n"));
							textArea.setText(output2);
						}
					}
				}));
	}
	
	
    private static void showConsoleFrame() {
		consoleframe.setSize(1200, 440);
		consoleframe.setTitle("Phantom Data Server");
//		frame.setResizable(false);
//		frame.setUndecorated(true);			
		
		
		textArea.setEditable(false);
		textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollV = new JScrollPane(textArea);
		scrollV.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollV.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		consoleframe.add(scrollV);
//		frame.add(textArea);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - consoleframe.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - consoleframe.getHeight()) / 2);
	    consoleframe.setLocation(x, y);
    	consoleframe.setVisible(true);
		
    	
		
    }
    
    private static List<String> lineSplitter(String s) {
		List<String> list = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new StringReader(s));
		reader.lines().forEach(line -> list.add(line));
		if (s.endsWith("\r") || s.endsWith("\n")) list.add("");
		
		return list;
    }

}
