/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import client.control.ClientCtr;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import model.ObjectWrapper;
import model.Player;

/**
 *
 * @author Administrator
 */
public class SignUpFrm extends JFrame implements ActionListener{
    private JLabel nameLabel, usernameLabel, passwordLabel;
    private JTextField txtName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnSubmit;
    private ClientCtr myControl;
    private LoginFrm loginFrm;
    public SignUpFrm(ClientCtr clientCtr, LoginFrm lgF){
        loginFrm = lgF;
        myControl = clientCtr;
        myControl.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_ADD_USER, this));
        
        this.setVisible(true);  
        this.setSize(400, 250);  
        setLayout(null);  
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  
        setTitle("Dang ki tai khoan");  
        
        nameLabel = new JLabel("Name:");  
        nameLabel.setBounds(20, 20, 70, 25);
        txtName = new JTextField();  
        txtName.setBounds(100, 20, 200, 25);
        this.add(nameLabel);
        this.add(txtName);  

        usernameLabel = new JLabel("Username:");  
        usernameLabel.setBounds(20, 50, 200, 25);
        txtUsername = new JTextField();
        txtUsername.setBounds(100, 50, 200, 25);
        this.add(usernameLabel);
        this.add(txtUsername);

        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 80, 200, 25);
        txtPassword = new JPasswordField();  
        txtPassword.setBounds(100, 80, 200, 25);
        this.add(passwordLabel);
        this.add(txtPassword);
        
        btnSubmit = new JButton("Submit");  
        btnSubmit.setBounds(150, 150, 100, 30);    
        this.add(btnSubmit);          
        btnSubmit.addActionListener(this);  

        this.addWindowListener( new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                loginFrm.setVisible(true);
            }
        });
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JButton){           
            Player player = new Player();
            player.setName(txtName.getText());
            player.setUsername(txtUsername.getText());
            player.setPassword(txtPassword.getText());
            myControl.sendData(new ObjectWrapper(ObjectWrapper.ADD_USER,player));            
        }
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getData().equals("ok")) {
            JOptionPane.showMessageDialog(this, "Register succesfully!");
            loginFrm.setVisible(true);
            this.dispose();
        }
        else {
            JOptionPane.showMessageDialog(this, "Failed");
        }
    }
}
