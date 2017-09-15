package cz3004MDP.views;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.Timer;



public class View1 implements ItemListener {
	JPanel controlPanel, cardPanel, comPanel, mapPanel, arenaPanel;
	JTextField spTextField1, spTextField2, eTextField1, eTextField2, comTextField;
	JButton spButton, spButton2, eButton, eButton2;
	JLabel comLabel, comLabel2, spLabel2, eLabel4;
	Timer timer;
    long startTime = -1;
    long duration = 360000;

     
    public void addComponentToPane(Container panel) {
    	
		//Editable Map Panel (Left)
   		mapPanel = new JPanel(new FlowLayout());
   		mapPanel.setBackground(Color.LIGHT_GRAY);
        mapPanel.setPreferredSize(new Dimension(205, 180));
   		JPanel map = new JPanel();
   		map.setBackground(Color.GRAY);
   		map.setLayout(new GridLayout(200, 150));
   		map.setPreferredSize(new Dimension(200, 150));
   		
   		mapPanel.add(map);
		panel.add(mapPanel, BorderLayout.WEST);
		JButton loadMap = new JButton("Load map");
		loadMap.setFont(new Font("Arial", Font.BOLD, 11));
		mapPanel.add(loadMap);
		JButton clearMap = new JButton("Clear map");
		clearMap.setFont(new Font("Arial", Font.BOLD, 11));
		mapPanel.add(clearMap);
		
		JButton btnAddObstacle = new JButton("Add Obstacle");
		btnAddObstacle.setHorizontalAlignment(SwingConstants.LEFT);
		btnAddObstacle.setFont(new Font("Arial", Font.BOLD, 11));
		mapPanel.add(btnAddObstacle);
		
		//Navigation Map Panel (Right)
		arenaPanel = new JPanel(new FlowLayout());
		arenaPanel.setBackground(Color.LIGHT_GRAY);
        arenaPanel.setPreferredSize(new Dimension(205, 170));
		JPanel arena = new JPanel();
		arena.setBackground(Color.GRAY);
		arena.setLayout(new GridLayout(200, 150));
        arena.setPreferredSize(new Dimension(200, 150));
        arenaPanel.add(arena);
		panel.add(arenaPanel, BorderLayout.EAST);
    	
		//Command Panel (Center)
        controlPanel = new JPanel(new BorderLayout()); 
        
		//Dropdown List
        String comboBoxItems[] = { "Exploration", "Shortest Path" };
        JComboBox cbSwitch = new JComboBox(comboBoxItems);
        cbSwitch.setEditable(false);
        cbSwitch.addItemListener(this);
        controlPanel.add(cbSwitch, BorderLayout.NORTH);
        panel.add(controlPanel, BorderLayout.CENTER);
        
        //Exploration Panel
        
		eButton = new JButton("Explore arena");
		eButton.setFont(new Font("Arial", Font.BOLD, 11));
		 timer = new Timer(10, new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 if (startTime < 0) {
                     startTime = System.currentTimeMillis();
                 }
                 long now = System.currentTimeMillis();
                 long clockTime = now - startTime;
                 if (clockTime >= duration) {
                     clockTime = duration;
                     timer.stop();
                 }
                 SimpleDateFormat df = new SimpleDateFormat("mm:ss");
                 df.setTimeZone(TimeZone.getTimeZone("UTC"));
                 eLabel4.setText(df.format(duration - clockTime));
                 spLabel2.setText(df.format(duration - clockTime));
             }
         });
         timer.setInitialDelay(0);
		eButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				updateCoverageAndTime();
				 if (!timer.isRunning()) {
                     startTime = -1;
                     timer.start();
                 }
			}
		});
		
		eButton2 = new JButton("Stop Timer");
		eButton2.setFont(new Font("Arial", Font.BOLD, 11));
		eButton2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			timer.stop();
			}
		});
		
		JPanel eInputPanel = new JPanel(new GridLayout(4, 2));
		eInputPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		
		eTextField1 =new JTextField(10) ;
		eTextField1.setFont(new Font("Arial", Font.PLAIN, 11));
		eTextField2 =new JTextField(10) ;
		eTextField2.setFont(new Font("Arial", Font.PLAIN, 11));

		eLabel4 = new JLabel("6:00");

		JLabel elabel1 = new JLabel("Initial position: ");
		elabel1.setFont(new Font("Arial", Font.PLAIN, 11));
		elabel1.setForeground(Color.BLACK);
		elabel1.setBackground(Color.LIGHT_GRAY);
		eInputPanel.add(elabel1);
		eInputPanel.add(eTextField1);
		
		JLabel elabel2 = new JLabel("Speed: ");
		elabel2.setFont(new Font("Arial", Font.PLAIN, 11));
		elabel2.setForeground(Color.BLACK);
		elabel2.setBackground(Color.LIGHT_GRAY);
		eInputPanel.add(elabel2);
		eInputPanel.add(eTextField2);
		
		JLabel elabel3 = new JLabel("Time limit in sec: ");
		elabel3.setFont(new Font("Arial", Font.PLAIN, 11));
		elabel3.setForeground(Color.BLACK);
		elabel3.setBackground(Color.LIGHT_GRAY);
		eInputPanel.add(elabel3);
		eInputPanel.add(eLabel4);
		
		JPanel eButtonPanel = new JPanel();
		eButtonPanel.setBackground(Color.LIGHT_GRAY);
		eButtonPanel.add(eButton);
		eButtonPanel.add(eButton2);
		
		JPanel eMainPanel = new JPanel();
		eMainPanel.setBackground(Color.LIGHT_GRAY);
		eMainPanel.add(eInputPanel);
		eMainPanel.add(eButtonPanel);
		eMainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		// Shortest Path Panel
        spButton = new JButton("Look for Shortest Path");
		spButton.setFont(new Font("Arial", Font.BOLD, 11));
        spButton.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
				 if (!timer.isRunning()) {
                     startTime = -1;
                     timer.start();
                 }

           
        	}
        });
        
        spButton2 = new JButton("Stop Timer");
		spButton2.setFont(new Font("Arial", Font.BOLD, 11));
		spButton2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			timer.stop();
			}
		});

        
        JPanel spInputPanel = new JPanel(new GridLayout(2, 2));
        spInputPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
        
        spTextField1 =new JTextField(10) ;
        spTextField1.setFont(new Font("Arial", Font.PLAIN, 11));
//		sp2TextField =new JTextField(10) ;
//		sp2TextField.setFont(new Font("Arial", Font.PLAIN, 11));
        spLabel2 = new JLabel("6:00");

        JLabel slabel = new JLabel("Speed: ");
        slabel.setFont(new Font("Arial", Font.PLAIN, 11));
        slabel.setForeground(Color.BLACK);
        slabel.setBackground(Color.LIGHT_GRAY);
        spInputPanel.add(slabel);
        spInputPanel.add(spTextField1);
        		
        JLabel slabel2 = new JLabel("Time limit in sec:");
        slabel2.setFont(new Font("Arial", Font.PLAIN, 11));
        slabel2.setBackground(Color.LIGHT_GRAY);
        spInputPanel.add(slabel2);
        spInputPanel.add(spLabel2);
        		
        JPanel spButtonPanel = new JPanel();
        spButtonPanel.setBackground(Color.LIGHT_GRAY);
        spButtonPanel.add(spButton);
        spButtonPanel.add(spButton2);
        		
        JPanel spMainPanel = new JPanel();
        spMainPanel.setBackground(Color.LIGHT_GRAY);
        spMainPanel.add(spInputPanel);
        spMainPanel.add(spButtonPanel);
        spMainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        		
        		
        //Panel for containing Exploration and Shortest Path Panel (Center)
        cardPanel = new JPanel(new CardLayout());
        controlPanel.add(cardPanel, BorderLayout.CENTER);
        cardPanel.add(eMainPanel, "Exploration");
        cardPanel.add(spMainPanel, "Shortest Path");
        cardPanel.setPreferredSize(new Dimension(200, 150));
        
		//Communication Panel 
        comPanel = new JPanel(new BorderLayout());
        controlPanel.add(comPanel, BorderLayout.SOUTH);
		comLabel = new JLabel("Communication Panel");
		comLabel.setHorizontalAlignment(SwingConstants.CENTER);
		comLabel.setFont(new Font("Arial", Font.PLAIN, 11));
		comPanel.add(comLabel, BorderLayout.NORTH);
		JPanel comConsole = new JPanel(new GridLayout(1, 1));
		
		comLabel2 = new JLabel("Sending String...");

		comLabel2.setFont(new Font("Arial", Font.PLAIN, 11));
		comLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		comConsole.add(comLabel2);
		comPanel.add(comConsole, BorderLayout.SOUTH);
		

    }
    
    
	//DropDown List Action event
    public void itemStateChanged(ItemEvent evt) {
        CardLayout cl = (CardLayout)(cardPanel.getLayout());
        cl.show(cardPanel, (String)evt.getItem());
    }
    
    
     
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("MDP Simulator");
        frame.setPreferredSize(new Dimension(650, 255));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        //Create and set up the content pane.
        View1 demo = new View1();
        demo.addComponentToPane(frame.getContentPane());
         
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    

	public void updateCoverageAndTime(){
		double result = calculateExploredPercentage();
		comLabel.setText("Coverage: "+result+"%");
	}
	
	public double calculateExploredPercentage(){
		return 0;

	}
	
	
	
    
    public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
         
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }


}