package cubesnake;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.Timer;

import javax.swing.JScrollPane;
import java.awt.Font;

public class CubeSnake {
	
	public static enum PIXEL_TYPE {
		PLAYER, TARGET
	};
	
	private final double RANDOM_VAL = 7;
	private final int DELAY = 500;
	private SerialPort port;
	private JFrame frmCubesnake;
	private JTextArea textArea;
	private int[][][] playField;
	private Timer timer;
	
	private ArrayList<ArrayList<Integer>> snake; 
	private int inc_x = 0, inc_y = 0, inc_z = 0;
    private OutputStream outputStream;
    private JComboBox<String> baudBox;
    private JComboBox<String> portBox;
    
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CubeSnake window = new CubeSnake();
					window.frmCubesnake.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CubeSnake() {
		initialize();
	}

	private void initialize() {
		CubeSnake parent = this;
		frmCubesnake = new JFrame();
		frmCubesnake.setResizable(false);
		frmCubesnake.setTitle("CubeSnake");
		frmCubesnake.setBounds(100, 100, 450, 300);
		frmCubesnake.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCubesnake.getContentPane().setLayout(null);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(10, 11, 46, 14);
		frmCubesnake.getContentPane().add(lblPort);
		
		portBox = new JComboBox<String>();
		portBox.setBounds(37, 8, 120, 20);
		portBox.removeAllItems();
		portBox.setModel(new DefaultComboBoxModel<String>(enumeratePorts().toArray(new String[]{})));
		frmCubesnake.getContentPane().add(portBox);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.setBounds(297, 7, 137, 23);
		frmCubesnake.getContentPane().add(btnConnect);
		btnConnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (outputStream == null){
					if (portBox.getSelectedItem() == null) return;
					String input = ((String)portBox.getSelectedItem()).trim();
					if (!input.isEmpty()) {
						if (openSerial(input)) {
							//
							btnConnect.setText("Close");
						}
					}
				} else {
					closeSerial();
					btnConnect.setText("Connect");
				}
				
			}
		});
		
		JButton btnNewButton = new JButton("W");
		btnNewButton.setBounds(61, 62, 46, 46);

		frmCubesnake.getContentPane().add(btnNewButton);
		
		JButton btnS = new JButton("A");
		btnS.setBounds(10, 112, 46, 46);
		frmCubesnake.getContentPane().add(btnS);
		
		JButton btnD = new JButton("D");
		btnD.setBounds(111, 112, 46, 46);
		frmCubesnake.getContentPane().add(btnD);
		
		JButton button = new JButton("S");
		button.setBounds(61, 165, 46, 46);
		
		frmCubesnake.getContentPane().add(button);
		
		JButton btnG = new JButton("G");
		btnG.setBounds(181, 62, 46, 46);
		frmCubesnake.getContentPane().add(btnG);
		
		JButton btnV = new JButton("V");
		btnV.setBounds(181, 112, 46, 46);
		frmCubesnake.getContentPane().add(btnV);
		
		JTextPane textPane = new JTextPane();
		textPane.setFont(new Font("Tahoma", Font.PLAIN, 40));
		textPane.setBounds(61, 112, 46, 46);
		frmCubesnake.getContentPane().add(textPane);
		
		JScrollPane scrollPane = new JScrollPane(this.textArea);
		scrollPane.setBounds(256, 41, 178, 219);
		frmCubesnake.getContentPane().add(scrollPane);
		
		this.textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		this.textArea.setEditable(false);
		baudBox = new JComboBox<String>(new String[]{"9600","19200","38400","57600","115200"});
		baudBox.setBounds(167, 8, 120, 20);
		frmCubesnake.getContentPane().add(baudBox);
		textPane.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				//System.out.println(e.getKeyCode());
				//w == 87
				//s == 83
				//a == 65
				//d == 68
				//g == 71
				//v == 86
				if (inc_x == 0 && inc_z == 0 && inc_y == 0) {
					textArea.setText("");
					timer.start();
				}
				
				switch (e.getKeyCode()) {
					case 87:
						//w pressed
						parent.log("Snake forward");
						if (parent.inc_x == -1) 
							break;
						parent.stopSnake();
						parent.inc_x = 1;
						break;
					case 83:
						//s pressed
						if (parent.inc_x == 1) 
							break;
						parent.log("Snake backward");
						parent.stopSnake();
						parent.inc_x = -1;
						break;
					case 65:
						//a pressed
						if (parent.inc_y == 1) 
							break;
						parent.log("Snake left");
						parent.stopSnake();
						parent.inc_y = -1;
						break;
					case 68:
						//d pressed
						if (parent.inc_y == -1) 
							break;
						parent.log("Snake right");
						parent.stopSnake();
						parent.inc_y = 1;
						break;
					case 71:
						//g pressed
						if (parent.inc_z == -1) 
							break;
						parent.log("Snake up");
						parent.stopSnake();
						parent.inc_z = 1;
						break;
					case 86:
						//v pressed
						if (parent.inc_z == 1) 
							break;
						parent.log("Snake down");
						parent.stopSnake();
						parent.inc_z = -1;
						break;
				}
				textPane.setText("");
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		log("Initialized");
		timer = new Timer(DELAY, new TimerListener());
		reset();
	}
	
	private void stopSnake() {
		this.inc_x = 0;
		this.inc_y = 0;
		this.inc_z = 0;
	}
	
	private int[] randomPosition(){
		int x, y, z;
		x = (int) (Math.random() * RANDOM_VAL);
		y = (int) (Math.random() * RANDOM_VAL);
		z = (int) (Math.random() * RANDOM_VAL);
		return new int[]{x,y,z};
	}
	
	private void fillApple() {
		int apple_x = 0, apple_y = 0, apple_z = 0;
	    do {
	    	apple_x = (int) (Math.random() * RANDOM_VAL);
        	apple_y = (int) (Math.random() * RANDOM_VAL );
        	apple_z = (int) (Math.random() * RANDOM_VAL );
	    } while(this.playField[apple_x][apple_y][apple_z] > -1);
	    if (this.playField[apple_x][apple_y][apple_z] < 0){
    		this.playField[apple_x][apple_y][apple_z] = PIXEL_TYPE.TARGET.ordinal();
    	}
	}
	
	private void move() {
		System.out.println("x: "+inc_x+" y: " + inc_y + " z: " + inc_z);
		ArrayList<Integer> head = new ArrayList<Integer>(), old_head = snake.get(snake.size()-1);
		head.add(old_head.get(0) + inc_x);
		head.add(old_head.get(1) + inc_y);
		head.add(old_head.get(2) + inc_z);
		
		boolean is_moving = (inc_x + inc_y + inc_z) != 0;
		
		if (head.contains(-1) || head.contains(8) || snake.contains(head) && is_moving) {
			timer.stop();
			log("Game Over");
			reset();
			return;
		}
		snake.add(head);
		
		if (playField[head.get(0)][head.get(1)][head.get(2)] == PIXEL_TYPE.TARGET.ordinal()) {
			fillApple();
		} else {
			ArrayList<Integer> tail = snake.remove(0);
			playField[tail.get(0)][tail.get(1)][tail.get(2)] = -1;
		}
		playField[head.get(0)][head.get(1)][head.get(2)] = PIXEL_TYPE.PLAYER.ordinal();
	}
	
	
	private void reset() {
		this.stopSnake();
		this.playField = new int[8][8][8];
		for (int x=0; x<8; x++) {
			for (int y=0; y<8; y++) {
				for (int z=0; z<8; z++){
					this.playField[x][y][z] = -1;
				}
			}
		}
		snake = new ArrayList<ArrayList<Integer>>();
		int[] position = this.randomPosition();
		ArrayList<Integer> init_position = new ArrayList<Integer>();
		init_position.add(position[0]);
		init_position.add(position[1]);
		init_position.add(position[2]);
		snake.add(init_position);
		this.playField[init_position.get(0)][init_position.get(1)][init_position.get(2)] = PIXEL_TYPE.PLAYER.ordinal();
		fillApple();
	}
	
	private void drawFrame(int[][][] fieldState) {
		// send to serial
		CubeMatrix dm = new CubeMatrix();
		for (int x=0; x < 8; x++) {
			for (int y=0; y<8; y++) {
				for (int z=0; z<8; z++){
					boolean val = playField[x][y][z] > -1;
					dm.setDot(x, y, z, val);
				}
			}
		}
		byte[] data = new byte[65];
		data[0] = (byte) 0xf2;
		System.arraycopy(dm.getCache(), 0, data, 1, CubeMatrix.CACHE_LENGTH);
		if (outputStream == null){
			return;
		}
		try {
			outputStream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void log(String message) {
		this.textArea.append('\n' + message);
	}
	
	private class TimerListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            move();
            drawFrame(playField);
        }
    }
	
	public List<String> enumeratePorts() {
		// scan available COM ports
		List<String> ports = new ArrayList<String>();
		System.out.println("enumerate serial ports");
		
		try {
			Enumeration<?> port_list = CommPortIdentifier.getPortIdentifiers();
			
		    while (port_list.hasMoreElements()) {
		    	CommPortIdentifier port_id = (CommPortIdentifier) port_list.nextElement();
		        if (port_id.getPortType() == CommPortIdentifier.PORT_SERIAL) {
		        	ports.add(port_id.getName());
		        }
		    }
		} catch (UnsatisfiedLinkError e) {
			JOptionPane.showMessageDialog(null, "Error initializing serial port:\n" + e.getMessage(), "Serial error", JOptionPane.ERROR_MESSAGE);
		} finally {
			System.out.println(ports.size()+" ports found: " + ports);
			if (ports.isEmpty()) {
				ports.add("No ports found!"); // default
			}
		}
		
		return ports;
	}
	
	public boolean openSerial(String name) {
		try {
			System.out.println("open serial: " + name);
			
		    Enumeration<?> port_list = CommPortIdentifier.getPortIdentifiers();
		    boolean found = false;
		
		    while (port_list.hasMoreElements()) {
		        // Get the list of ports
		        CommPortIdentifier port_id = (CommPortIdentifier) port_list.nextElement();
		        
		        if (port_id.getPortType() == CommPortIdentifier.PORT_SERIAL && port_id.getName().equals(name)) {
		        	found = true;
		        	
		            try {
		            	// attempt to open
		                port = (SerialPort) port_id.open("PortListOpen", 20);
		                if (port == null) {
		                	throw new Exception("Cannot open port: " + name);
		                }
		                
		                System.out.println("serial port opened: " + name);
		  
	                    int baudRate = Integer.parseInt((String)baudBox.getSelectedItem());
	                    port.setSerialPortParams(
	                            baudRate,
	                            SerialPort.DATABITS_8,
	                            SerialPort.STOPBITS_1,
	                            SerialPort.PARITY_NONE);
	                    port.setDTR(true);
	                    
	                    outputStream = port.getOutputStream();
	                    return true;
	                } catch (UnsupportedCommOperationException e) {
	                    JOptionPane.showMessageDialog(null, "Invalid serial parameters:\n" + e.getMessage(), "Serial error", JOptionPane.ERROR_MESSAGE);
	                } catch (PortInUseException e) {
	                	String owner = port_id.getCurrentOwner();
	                	JOptionPane.showMessageDialog(null, "The port is already in use! Owner: " + (owner != null ? owner : "unknown"), 
	                			"Serial error", JOptionPane.ERROR_MESSAGE);
	                } catch (Exception e) {
	                	JOptionPane.showMessageDialog(null, "I/O error:\n" + e.getMessage(), "Serial error", JOptionPane.ERROR_MESSAGE);
	                }
	            }
		    }
		    
		    // not found
		    if (!found) {
		    	JOptionPane.showMessageDialog(null, "Serial port not found: " + name, "Serial error", JOptionPane.ERROR_MESSAGE);
		    }
		} catch (UnsatisfiedLinkError e) {
			JOptionPane.showMessageDialog(null, "Error initializing serial port:\n" + e.getMessage(), "Serial error", JOptionPane.ERROR_MESSAGE);
		}
		
	    return false;
    }
	
	private void closeSerial() {
		if (port != null) {
			System.out.println("closing serial port");
			port.close();
		}
		port = null;
		outputStream = null;
	}
}
