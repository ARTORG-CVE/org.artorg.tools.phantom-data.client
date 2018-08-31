package org.artorg.tools.phantomData.client.boot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import org.artorg.tools.phantomData.client.Main;

public class BootUtils extends org.artorg.tools.phantomData.server.boot.BootUtils {
	private static String text = "";
	private static JFrame consoleframe;
	private static JFrame startupFrame;
	private static JProgressBar progressBar;
	private static JTextArea textArea;
	private static double progress;
	private static int nLines;
	private static JLabel progressLabel;

	static {
		startupFrame = new JFrame();
		consoleframe = new JFrame();
		textArea = new JTextArea();
		progressBar = new JProgressBar();
		progressLabel = new JLabel();
		text = "";
		progress = 0.0;
	}

	public static boolean launch(int nConsoleLines, Runnable rc) {
		BootUtils.nLines = nConsoleLines - 1;
		try {
			
			if (!isConnected())
				showStartupFrame();
			redirectConsoleOuptut();
			createConsoleFrame();
			
			rc.run();
			startupFrame.setVisible(false);
			startupFrame.dispose();

		} catch (Exception e) {
			e.printStackTrace();
			consoleframe.setVisible(true);
		}

		return false;
	}

	public static void showConsoleFrame() {
		consoleframe.setVisible(true);
	}

	private static void showStartupFrame() {
		startupFrame.setSize(300, 300);
		startupFrame.setTitle("Phantom Database");
		startupFrame.setResizable(false);
		startupFrame.setUndecorated(true);
		startupFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container content = startupFrame.getContentPane();
		content.setLayout(new BorderLayout());

		BufferedImage myPicture = null;
		try {
			myPicture = ImageIO
					.read(Main.class.getClassLoader().getResourceAsStream("img/startup.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		JLabel startupImageLabel = new JLabel(new ImageIcon(myPicture));
		content.add(startupImageLabel, BorderLayout.NORTH);

		JPanel progressPanel = new JPanel();
		progressPanel.setLayout(new BorderLayout());
		
		progressLabel.setText("Launching Application...");
		progressLabel.setFont(new Font("monospaced", Font.PLAIN, 11));
		
		progressBar.setValue(0);
		progressBar.setForeground(Color.green);
		progressPanel.add(progressLabel, BorderLayout.WEST);
		progressPanel.add(progressBar, BorderLayout.PAGE_END);
		content.add(progressPanel, BorderLayout.PAGE_END);

		alignFrame(startupFrame);
		startupFrame.pack();
		startupFrame.setVisible(true);

	}

	private static void redirectConsoleOuptut() {
		System.setOut(addPrintStream(System.out));
		System.setErr(addPrintStream(System.err));
	}
	
	private static PrintStream addPrintStream(PrintStream defaultPrintStream) {
		Pattern pattern = Pattern.compile("[^:]: (.*)");
		
		return new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				char c = (char) b;
				text = text + String.valueOf(c);
				if (text.endsWith("\n")) {
					List<String> consoleLines = lineSplitter(text);	
					while (consoleLines.size() > 2000 )
						consoleLines.remove(0);
					text = consoleLines.stream()
							.collect(Collectors.joining("\n")) +"\n";
					
					progress = progress + 100.0 / nLines;
					progressBar.setValue((int) progress);

					String newLine = consoleLines.get(consoleLines.size()-1);
					if (consoleLines.size() > 0) {
						if (consoleLines.size() < 9)
							progressLabel.setText("Launching Spring Boot...");
						
						Matcher m = pattern.matcher(newLine);
						if (m.find())
							progressLabel.setText(m.group(1));
					}
					
					if (consoleframe.isVisible())
						textArea.setText(text);
					
					defaultPrintStream.println(newLine);
				}
			}
		});
	}

	private static void createConsoleFrame() {
		consoleframe.setSize(1200, 440);
		consoleframe.setTitle("Phantom Data Server");
		textArea.setEditable(false);
		textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollV = new JScrollPane(textArea);
		scrollV.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollV.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		consoleframe.add(scrollV);
		alignFrame(consoleframe);
	}

	private static List<String> lineSplitter(String s) {
		List<String> list = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new StringReader(s));
		reader.lines().forEach(line -> list.add(line));

		return list;
	}

	private static void alignFrame(JFrame frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}

}
