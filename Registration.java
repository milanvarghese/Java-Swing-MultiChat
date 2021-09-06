package system;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;

public class Registration {

	private JFrame frame;
	private JTextField TFEmail;
	private JPasswordField TFPassword1;
	private JPasswordField TFPassword2;
	private JTextField TFID;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Registration window = new Registration();
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
	public Registration() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.getContentPane().setFont(new Font("Calibri", Font.PLAIN, 20));
		frame.setBounds(100, 100, 578, 490);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel l1Heading = new JLabel("Registration Form");
		l1Heading.setFont(new Font("Calibri", Font.BOLD, 20));
		l1Heading.setBounds(211, 23, 153, 32);
		frame.getContentPane().add(l1Heading);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setFont(new Font("Calibri", Font.PLAIN, 20));
		lblEmail.setBounds(55, 130, 95, 32);
		frame.getContentPane().add(lblEmail);
		
		JLabel lblNewPassword = new JLabel("New Password");
		lblNewPassword.setFont(new Font("Calibri", Font.PLAIN, 20));
		lblNewPassword.setBounds(55, 187, 153, 32);
		frame.getContentPane().add(lblNewPassword);
		
		JLabel lblRepeatPassword = new JLabel("Repeat Password");
		lblRepeatPassword.setFont(new Font("Calibri", Font.PLAIN, 20));
		lblRepeatPassword.setBounds(55, 248, 153, 32);
		frame.getContentPane().add(lblRepeatPassword);
		
		TFEmail = new JTextField();
		TFEmail.setColumns(10);
		TFEmail.setBounds(238, 130, 267, 26);
		frame.getContentPane().add(TFEmail);
		
		ButtonGroup bg=new ButtonGroup();
		JRadioButton rb1 = new JRadioButton("Client"); bg.add(rb1);
		rb1.setSelected(true);
		rb1.setFont(new Font("Calibri", Font.PLAIN, 20));
		rb1.setBounds(238, 312, 103, 21);
		frame.getContentPane().add(rb1);
		
		JRadioButton rb2 = new JRadioButton("Server");
		rb2.setFont(new Font("Calibri", Font.PLAIN, 20));
		rb2.setBounds(358, 312, 103, 21);
		frame.getContentPane().add(rb2); bg.add(rb2);
		JButton b1 = new JButton("Register");
		
		b1.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				String ID, emailString, Password1, Password2, UserType;
				boolean done=false;
				ID = TFID.getText();
				emailString = TFEmail.getText();
				Password1 = TFPassword1.getText();
				Password2 = TFPassword2.getText();
				if(rb1.isSelected()) {
					UserType="Client";
				}else {
					UserType="Server";
				}
				try{
					Class.forName("oracle.jdbc.driver.OracleDriver");
					Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl","system","system");
					Statement stmt=con.createStatement();
					
					ResultSet rs =stmt.executeQuery("select USERID from JPUser");
					Boolean found = false;
					while(rs.next()) {
						if(ID.equals(rs.getString(1))) {
							found =true;
						}
					}
					if(found.equals(false)) {
						
						if(Password1.equals(Password2) && ID!=null && emailString!=null && UserType!=null) {
							String query="insert into JPUser values ('" + ID + "','" + Password1 + "','" + emailString + "','" + UserType + "')";
							stmt.executeUpdate(query);
							con.close();
							done=true;
						}else {
							JOptionPane.showMessageDialog(frame, "Invalid Input");
						}
						if(done) {
							JOptionPane.showMessageDialog(frame, "Registration Successful!");
							Login.main(null);
							frame.setVisible(false);
							frame.dispose();
						}
						
					}else {
						JOptionPane.showMessageDialog(frame, "UserID Already Exists!");
					}
					
				}catch(Exception e3) {System.out.println(e3);}
			}
		});
		b1.setFont(new Font("Calibri", Font.PLAIN, 20));
		b1.setBounds(56, 366, 171, 45);
		frame.getContentPane().add(b1);
		JButton b2 = new JButton("Cancel");
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Login.main(null);
				frame.setVisible(false);
				frame.dispose();
			}
		});
		b2.setFont(new Font("Calibri", Font.PLAIN, 20));
		b2.setBounds(319, 366, 171, 45);
		frame.getContentPane().add(b2);
		
		TFPassword1 = new JPasswordField();
		TFPassword1.setBounds(238, 187, 267, 32);
		frame.getContentPane().add(TFPassword1);
		
		TFPassword2 = new JPasswordField();
		TFPassword2.setBounds(238, 248, 267, 32);
		frame.getContentPane().add(TFPassword2);
		
		
		
		JLabel lblUserType = new JLabel("User Type");
		lblUserType.setFont(new Font("Calibri", Font.PLAIN, 20));
		lblUserType.setBounds(73, 306, 153, 32);
		frame.getContentPane().add(lblUserType);
		
		TFID = new JTextField();
		TFID.setColumns(10);
		TFID.setBounds(238, 79, 267, 26);
		frame.getContentPane().add(TFID);
		
		JLabel l2_1 = new JLabel("UserID");
		l2_1.setFont(new Font("Calibri", Font.PLAIN, 20));
		l2_1.setBounds(55, 76, 95, 32);
		frame.getContentPane().add(l2_1);
	}
}
