package system;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.util.Date;
import java.util.Vector;
import javax.swing.JTabbedPane;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class Server extends JFrame {
	private JPanel contentPane;
	private JTextField TFClientLim;
	private JTextField TFPort;
	static JTextArea TAServerLog;
	static Vector<ClientManager> clientVector = new Vector<>();
	static int activeClientCount = 0;
	static int Port, Limit;
	static ServerBackend SBProgram;
	static Thread t;
	static Format f = new SimpleDateFormat("[dd-mm-yyyy hh:mm:ss]");
	static String timestamp;
	JLabel AdminName;
	static String MyName = "AdminName";
	static JTextArea TAServerLogHistory;
	JButton BtnConnect;
	static Connection con;
	static Statement stmt;
	
	public static void main(String[] args) throws Exception {
		
		SBProgram = new ServerBackend();
		t = new Thread(SBProgram);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server frame = new Server(args[0]);
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
	 * @throws SQLException 
	 */
	
	static void displayServerLog(String s) throws Exception {
		
		timestamp = f.format(new Date());
		TAServerLog.append(timestamp + " " + s + "\n");
		TAServerLog.setCaretPosition(TAServerLog.getDocument().getLength() );
		String query = "insert into JPServerLog (timestamp,log, sid) values ( to_char(sysdate,'dd mm yyyy HH12:MI:SS') ,'"  + s + "', '" + MyName + "')";
		database("execute", query);
	}
	
	
	static void database(String action, String query) throws ClassNotFoundException, SQLException {
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		con= DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCL","system","system");
		stmt = con.createStatement();
		
		if(action.equals("execute")) {
			stmt.executeUpdate(query);
		}
		
		if(action.equals("getdata")) {
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				displayServerLogHistory("[" + rs.getString(1) + "] " + rs.getString(2)); 
			}
		}
		
		stmt.close();
		con.close();
	}
	static void displayServerLogHistory(String s) {
		TAServerLogHistory.append(s + "\n");
		TAServerLogHistory.setCaretPosition( TAServerLog.getDocument().getLength() );
	}
	
	public Server(String Name) {
		Server.MyName = Name;
		
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setBounds(100, 100, 745, 548);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Welcome Server Administrator,");
		lblNewLabel.setBounds(150, 10, 287, 36);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		contentPane.add(lblNewLabel);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 58, 711, 443);
		contentPane.add(tabbedPane);
		
		JLayeredPane layeredPane = new JLayeredPane();
		tabbedPane.addTab("Connection", null, layeredPane, null);
		layeredPane.setLayout(null);
		
		BtnConnect = new JButton("Start");
		BtnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				
				try {
					displayServerLog("Server Started!");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				//Collecting the Data
				Port=Integer.parseInt(TFPort.getText());
				Limit=Integer.parseInt(TFClientLim.getText());
				
				SBProgram.insertPort(Port, Limit);
				t.start();
				
				BtnConnect.setEnabled(false);
			}
		});
		BtnConnect.setBounds(435, 10, 114, 30);
		layeredPane.add(BtnConnect);
		
		JLabel lblNewLabel_1 = new JLabel("Client Limit:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_1.setBounds(20, 15, 114, 25);
		layeredPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Port:");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_1_1.setBounds(250, 9, 51, 31);
		layeredPane.add(lblNewLabel_1_1);
		
		TFClientLim = new JTextField();
		TFClientLim.setText("23");
		TFClientLim.setBounds(144, 11, 96, 30);
		layeredPane.add(TFClientLim);
		TFClientLim.setColumns(10);
		
		TFPort = new JTextField();
		TFPort.setText("2323");
		TFPort.setColumns(10);
		TFPort.setBounds(311, 11, 114, 30);
		layeredPane.add(TFPort);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(23, 50, 673, 356);
		layeredPane.add(scrollPane);
		
		TAServerLog = new JTextArea();
		TAServerLog.setEditable(false);
		scrollPane.setViewportView(TAServerLog);
		
		JLayeredPane layeredPane_2 = new JLayeredPane();
		tabbedPane.addTab("Log History", null, layeredPane_2, null);
		layeredPane_2.setLayout(null);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 44, 686, 362);
		layeredPane_2.add(scrollPane_1);
		
		TAServerLogHistory = new JTextArea();
		TAServerLogHistory.setEditable(false);
		scrollPane_1.setViewportView(TAServerLogHistory);
		
		JButton BtnGetLog = new JButton("Get Log");
		BtnGetLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					TAServerLogHistory.setText("");
					String query = "select timestamp, log from JPServerLog where sid = '" + MyName + "'";
					database("getdata", query);
					
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				
			}
		});
		BtnGetLog.setFont(new Font("Tahoma", Font.BOLD, 13));
		BtnGetLog.setBounds(591, 13, 85, 21);
		layeredPane_2.add(BtnGetLog);
		
		AdminName = new JLabel(MyName);
		AdminName.setFont(new Font("Tahoma", Font.PLAIN, 20));
		AdminName.setBounds(439, 10, 219, 36);
		contentPane.add(AdminName);
	}
	
	public void windowClosing(WindowEvent e) throws Exception {
		t.stop();
		displayServerLog("Server Stopped!");
	    System.exit(0);
	}
}
