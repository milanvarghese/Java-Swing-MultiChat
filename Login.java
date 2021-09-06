package system;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import java.awt.Font;
import javax.swing.SwingConstants;

public class Login {

	private JFrame frame;
	private JTextField TFUser;
	private JPasswordField TFPassword;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.frame.setVisible(true);
					window.frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Login() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 427, 272);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel l1 = new JLabel("Username");
		l1.setFont(l1.getFont().deriveFont(16f));
		l1.setBounds(42, 50, 97, 40);
		frame.getContentPane().add(l1);
		
		JLabel l2 = new JLabel("Password");
		l2.setFont(l2.getFont().deriveFont(16f));
		l2.setBounds(42, 100, 97, 40);
		frame.getContentPane().add(l2);
		
		TFUser = new JTextField();
		TFUser.setBounds(149, 59, 234, 27);
		frame.getContentPane().add(TFUser);
		TFUser.setColumns(10);
		
		JButton b1 = new JButton("Login");
		b1.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				String ClientName=TFUser.getText();
				String ClientPass=TFPassword.getText();
				try{
					
					Class.forName("oracle.jdbc.driver.OracleDriver");
					Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl","system","system");
					Statement stmt=con.createStatement();
					ResultSet rs =stmt.executeQuery("select userid, password, trim(role) from JPUser");
					
					while(rs.next()){
						if((ClientName.equals(rs.getString(1))) && (ClientPass.equals(rs.getString(2)))){
							JOptionPane.showMessageDialog(frame, "Login Successful!");
							String[] name = {rs.getString(1)};
							String user = rs.getString(3).toString();
							
							if(user.equals("Client")) {
								Client.main(name);
							}else if(user.equals("Server")) {
								
								Server.main(name);
							}
							
							rs.close();
							stmt.close();
							con.close();
							frame.setVisible(false);
							frame.dispose();
							break;
						}		
					}
					if(rs.isAfterLast()){
						JOptionPane.showMessageDialog(frame, "Invalid Login Credentials!");
					}
				}catch(Exception e3) {System.out.println(e3);}
			}
		});
		b1.setFont(b1.getFont().deriveFont(16f));
		b1.setBounds(29, 170, 110, 34);
		frame.getContentPane().add(b1);
		
		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Registration.main(null);
				frame.setVisible(false);
				frame.dispose();
			}
		});
		btnRegister.setFont(btnRegister.getFont().deriveFont(16f));
		btnRegister.setBounds(149, 170, 114, 34);
		frame.getContentPane().add(btnRegister);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.dispose();
			}
		});
		btnCancel.setFont(btnCancel.getFont().deriveFont(16f));
		btnCancel.setBounds(273, 170, 110, 34);
		frame.getContentPane().add(btnCancel);
		
		TFPassword = new JPasswordField();
		TFPassword.setBounds(149, 109, 234, 27);
		frame.getContentPane().add(TFPassword);
		
		JLabel lblLogin = new JLabel("Login");
		lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogin.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblLogin.setBounds(163, 9, 97, 40);
		frame.getContentPane().add(lblLogin);
	}
}
