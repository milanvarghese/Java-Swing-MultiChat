package system;
import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class Client extends JFrame {

	private JPanel contentPane;
	private JTextField TFAdress;
	private JTextField TFPort;
	private JTextField TFPrivateSend;
	private JTextField TFGlobalSend;
	static JTextArea TAPrivate;
	static JTextArea TAGlobal;
	String ServerAddress;
	int ServerPort;
	Socket ClientSocket;
	DataOutputStream dos;
    DataInputStream dis;
    Thread readMessage;
    String MyID = "ClientID";
    String MyName = "ClientName";
    static DefaultListModel<Object> listModelActiveUsers;
    static JList<Object> list;
    JButton BtnConnect;
    JButton BtnDisconnect;
    static Format f = new SimpleDateFormat("[mm-dd-yyyy hh:mm:ss]");
	static String timestamp;
	private static JTable table;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = new Client(args[0]);
					frame.setResizable(false);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	static void displayPrivate(String s) {
		timestamp = f.format(new Date());
		TAPrivate.append(timestamp +" " + s + "\n");
		TAPrivate.setCaretPosition(TAPrivate.getDocument().getLength());
	}
	
	static void displayGlobal(String s) {
		TAGlobal.append(timestamp +" " + s + "\n");
		TAGlobal.setCaretPosition( TAGlobal.getDocument().getLength() );
	}
	
	static void displayBoth(String s) {
		displayPrivate(s);
		displayGlobal(s);
	}
	static void addUser(String s) {
		listModelActiveUsers.addElement(s);
		list.setSelectedIndex(listModelActiveUsers.indexOf(s));
		displayBoth(s +" joined the chat.");
	}
	static void RemoveUser(String s) {
		listModelActiveUsers.removeElement(s);
		displayBoth(s +" left the chat.");
	}
	
	//static void displayHistory(String s) {
	//	TAHistory.append(timestamp +" " + s + "\n");
	//	TAHistory.setCaretPosition( TAHistory.getDocument().getLength() );
	//}
	
	static void database(String action, String query) throws ClassNotFoundException, SQLException {
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection co= DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCL","system","system");
		Statement stm = co.createStatement();
		
		if(action.equals("execute")) {
			stm.executeUpdate(query);
		}
		
		if(action.equals("getdata")) {
			ResultSet rs =stm.executeQuery(query);
			while(rs.next()){
				String no = String.valueOf(rs.getInt("HNO"));
				String type = rs.getString("TYPE");
				String recepient = rs.getString("RECEPIENTID");
				String Message = rs.getString("MESSAGE");
				String TimeStamp = rs.getString("TIMESTAMP");
				String tbData[] = { no , type, recepient, Message, TimeStamp};
				DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
				tableModel.addRow(tbData);
				
			}
		}
		
		stm.close();
		co.close();
	}
	
	public Client(String Name) {
		this.MyName = Name;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 670, 578);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Welcome, ");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel.setBounds(34, 10, 80, 32);
		contentPane.add(lblNewLabel);
		
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblStatus.setBounds(34, 52, 80, 32);
		contentPane.add(lblStatus);
		
		JLabel LabelName = new JLabel(MyName);
		LabelName.setFont(new Font("Tahoma", Font.BOLD, 15));
		LabelName.setBounds(116, 10, 145, 32);
		contentPane.add(LabelName);
		
		JLabel LabelStatus = new JLabel("Not Connected");
		LabelStatus.setFont(new Font("Tahoma", Font.PLAIN, 15));
		LabelStatus.setBounds(105, 52, 145, 32);
		contentPane.add(LabelStatus);
		
		JLabel lblAddress = new JLabel("Address: ");
		lblAddress.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblAddress.setBounds(249, 10, 71, 32);
		contentPane.add(lblAddress);
		
		JLabel lblStatus_1_1 = new JLabel("Port: ");
		lblStatus_1_1.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblStatus_1_1.setBounds(249, 52, 71, 32);
		contentPane.add(lblStatus_1_1);
		
		TFAdress = new JTextField();
		TFAdress.setText("localhost");
		TFAdress.setBounds(330, 14, 156, 30);
		contentPane.add(TFAdress);
		TFAdress.setColumns(10);
		
		TFPort = new JTextField();
		TFPort.setText("2323");
		TFPort.setColumns(10);
		TFPort.setBounds(330, 56, 156, 30);
		contentPane.add(TFPort);
		
		//Thread to receive Messages
		readMessage = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                    	String msg = dis.readUTF();
                    	
                    	System.out.print(msg);
                    	
                    	if ( msg.indexOf("#") != -1 ) {
                    		StringTokenizer st = new StringTokenizer(msg,"#");
                    		String recipient = st.nextToken();
                    		String message = st.nextToken();
                    		
    						if(recipient.equals("Global")) {
                        		displayGlobal(message);
                        		continue;
                        	}
    						
    						if(recipient.equals("AddUser")) {
    							addUser(message);
    							continue;
    						}
    						
    						if(recipient.equals("RemoveUser")) {
    							 RemoveUser(message);
    							 continue;
    						}
    						
                    	}else {
                    		
                    		if(msg.equals("CloseConn")) {
                            	displayBoth("Connection Closed By Server!");
                            	ClientSocket.close();
        						break;
        					}
                    		
                    		displayPrivate(msg);
                    	}
                        	
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
		
		BtnConnect = new JButton("Connect");
		BtnConnect.setFont(new Font("Tahoma", Font.BOLD, 13));
		BtnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				ServerAddress = TFAdress.getText();
				ServerPort = Integer.parseInt(TFPort.getText());
				
				try {
					
					displayBoth("Connecting to " + ServerAddress + " on port " + ServerPort);
					ClientSocket= new Socket(ServerAddress, ServerPort);
					
				    dis = new DataInputStream(ClientSocket.getInputStream());
				    dos = new DataOutputStream(ClientSocket.getOutputStream());
				    
				    if (dis.available() == 0 ) {
				    	
				    	LabelStatus.setText("Connected");
				    	displayBoth("Connected Established at Address " + ClientSocket.getRemoteSocketAddress());
				    	
				    	//Sending the Name of the Client Server
				    	dos.writeUTF(MyName);
				    	
				    	//Starting Thread to receive Messages
				    	readMessage.start();
				    	
				    	//Adding the clients name to active list and sending avilability to other clients 
				    	dos.writeUTF("AddClient#" + MyName);
				    	
				    } else {
				    	displayBoth("Could not connect to Server: " + ClientSocket.getRemoteSocketAddress());
				    }
				    
				    BtnConnect.setEnabled(false);
					BtnDisconnect.setEnabled(true);
				    
				}catch(Exception e2) {
					System.out.println(e2);
					displayBoth("Cannot Connect");
				}
				
			}
		});
		BtnConnect.setBounds(496, 11, 150, 32);
		contentPane.add(BtnConnect);
		
		BtnDisconnect = new JButton("Disconnect");
		BtnDisconnect.setEnabled(false);
		BtnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

		    	try {
										
					listModelActiveUsers.removeAllElements();
					displayBoth("Disconnected from Server.");
					BtnDisconnect.setEnabled(false);
					dos.writeUTF("RemoveClient#" + MyName);
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		    	
			}
		});
		BtnDisconnect.setFont(new Font("Tahoma", Font.BOLD, 13));
		BtnDisconnect.setBounds(496, 53, 150, 32);
		contentPane.add(BtnDisconnect);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 94, 636, 437);
		contentPane.add(tabbedPane);
		
		JLayeredPane layeredPane = new JLayeredPane();
		tabbedPane.addTab("Private Chat", null, layeredPane, null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 43, 151, 357);
		layeredPane.add(scrollPane);
		
		listModelActiveUsers = new DefaultListModel<Object>();
		list = new JList<Object>(listModelActiveUsers);
		scrollPane.setViewportView(list);
		
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(171, 10, 450, 338);
		layeredPane.add(scrollPane_1);
		
		TAPrivate = new JTextArea();
		TAPrivate.setEditable(false);
		scrollPane_1.setViewportView(TAPrivate);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(171, 358, 343, 42);
		layeredPane.add(scrollPane_2);
		
		TFPrivateSend = new JTextField();
		scrollPane_2.setViewportView(TFPrivateSend);
		TFPrivateSend.setColumns(10);
		
		JButton BtnPrivateSend = new JButton("Send");
		BtnPrivateSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
		        try {
		        	//Add Recipient here
		        	if(!list.isSelectionEmpty()) {
		        		Class.forName("oracle.jdbc.driver.OracleDriver");
		        		Connection conn= DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCL","system","system");
		        		Statement stmtt = conn.createStatement();
		        		
		        		String Name = list.getSelectedValue().toString();
		        		String Message=TFPrivateSend.getText();
						dos.writeUTF(Name + "#" +Message);
						displayPrivate("> "+Message);
						
						String query = "insert into JPHistory (timestamp,senderid, recepientid, type, message) values (to_char(sysdate,'dd mm yyyy HH12:MI:SS'), '" + MyName+ "', '" + Name + "','Private', '" + Message + "')";
						System.out.println(query);
						stmtt.execute(query);
						stmtt.close();
						conn.close();
		        	}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
		        
				
			}
		});
		BtnPrivateSend.setFont(new Font("Tahoma", Font.BOLD, 13));
		BtnPrivateSend.setBounds(524, 358, 93, 42);
		layeredPane.add(BtnPrivateSend);
		
		JLabel lblNewLabel_2 = new JLabel("Active Connections");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(10, 5, 151, 35);
		layeredPane.add(lblNewLabel_2);
		
		JLayeredPane layeredPane_1 = new JLayeredPane();
		tabbedPane.addTab("Global Chat", null, layeredPane_1, null);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(10, 10, 611, 332);
		layeredPane_1.add(scrollPane_3);
		
		TAGlobal = new JTextArea();
		TAGlobal.setEditable(false);
		scrollPane_3.setViewportView(TAGlobal);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(10, 352, 503, 48);
		layeredPane_1.add(scrollPane_4);
		
		TFGlobalSend = new JTextField();
		scrollPane_4.setViewportView(TFGlobalSend);
		TFGlobalSend.setColumns(10);
		
		JButton BtnGlobalSend = new JButton("Send");
		BtnGlobalSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					Class.forName("oracle.jdbc.driver.OracleDriver");
	        		Connection connn= DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCL","system","system");
	        		Statement stmttt = connn.createStatement();
	        		
		        	String Message=TFGlobalSend.getText();
					dos.writeUTF("Global#" + Message);
					
					String query = "insert into JPHistory (timestamp,senderid, type, message) values (to_char(sysdate,'dd mm yyyy HH12:MI:SS'), '" + MyName+ "', 'Global','" + Message + "')";
					System.out.println(query);
					stmttt.execute(query);
					stmttt.close();
					connn.close();
					
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		BtnGlobalSend.setFont(new Font("Tahoma", Font.BOLD, 13));
		BtnGlobalSend.setBounds(523, 352, 100, 48);
		layeredPane_1.add(BtnGlobalSend);
		
		JLayeredPane layeredPane_2 = new JLayeredPane();
		tabbedPane.addTab("Chat History", null, layeredPane_2, null);
		layeredPane_2.setLayout(null);
		
		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(10, 51, 611, 349);
		layeredPane_2.add(scrollPane_5);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"No", "TYPE", "RECEPIENT", "Message", "Time Stamp"
			}
		));
		scrollPane_5.setViewportView(table);
		
		JButton btnNewButton = new JButton("Get History");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					//TAHistory.setText("");
					String query = "select * from JPHistory where senderid = '" + MyName + "'";
					database("getdata", query);
					
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnNewButton.setBounds(501, 10, 120, 31);
		layeredPane_2.add(btnNewButton);
	}
}
