package cz3004MDP.views;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.geom.Area;

import cz3004MDP.models.*;

import javax.swing.*;

public class MainFrameUI extends JFrame{
	
	private JFrame frame;
	private Arena arena;
	
	public static void main(String[] args) {	
	
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrameUI window = new MainFrameUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainFrameUI() {
		initialize();
		arena = new Arena();
		arena.printArena();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("MDP Group 18");
		frame.setSize(1200, 600);
		frame.setLayout(new BorderLayout(5,10));
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	

}
