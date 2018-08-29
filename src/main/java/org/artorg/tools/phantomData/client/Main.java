package org.artorg.tools.phantomData.client;

import static org.artorg.tools.phantomData.server.boot.BootUtils.isConnected;
import static org.artorg.tools.phantomData.server.boot.BootUtils.logInfos;
import static org.artorg.tools.phantomData.server.boot.BootUtils.prepareFileStructure;
import static org.artorg.tools.phantomData.server.boot.BootUtils.startingServer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.artorg.tools.phantomData.client.control.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
	static String text = "";
	
    public static void main( String[] args ) {
    	
		

    	launch(args);
    	
    	
    }
    
    public static List<String> lineSplitter(String s) {
		List<String> list = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new StringReader(s));
		reader.lines().forEach(line -> list.add(line));
		if (s.endsWith("\r") || s.endsWith("\n")) list.add("");
		
		return list;
    }
    
    @Override
	public void start(Stage stage) throws Exception {
    	JFrame frame;
    	frame = new JFrame();
    	
    	if (!isConnected()) {
			frame.setSize(1800, 440);
//			frame.setResizable(false);
//			frame.setUndecorated(true);
			
			
			JTextArea textArea = new JTextArea();
			textArea.setEditable(false);
			textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
			JScrollPane scrollV = new JScrollPane(textArea);
			scrollV.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			frame.add(scrollV);
			Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		    frame.setLocation(x, y);
	    	frame.setVisible(true);
			
			System.setOut(new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			  text = text +String.valueOf((char) b);
			  if (text.endsWith("\n") || text.endsWith("\r")) {
				  List<String> lines = lineSplitter(text);
				  
				  int start = lines.size() - 200;
						if (start < 0)
							start = 0;
	
						final String output2 = lines.subList(start, lines.size()).stream()
								.collect(Collectors.joining("\n"));
						textArea.setText(output2);
					}
				}
			}));
    	}
    	
    	try {
			startingServer(new String[] {});
			
			while(!isConnected()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	
			prepareFileStructure();
			logInfos();
			
	    	FXMLLoader loader = new FXMLLoader(org.artorg.tools.phantomData.client.Main.class.getResource("Table.fxml"));
	    	
			MainController controller = new MainController(stage);
			loader.setController(controller);
			
			AnchorPane pane = null;
			try {
				pane = loader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	Scene scene = new Scene(pane);
			scene.getStylesheets().add(org.artorg.tools.phantomData.client.Main.class.getResource("application.css").toExternalForm());
			
			stage.setScene(scene);
			stage.setTitle("Phantom Database");
			stage.setWidth(800);
			stage.setHeight(500);
			
	//		frame.setVisible(false);
	//		frame.dispose();
			stage.show();
			stage.requestFocus();
			stage.toFront();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
		
	}
    
}
