package client.view;

import client.control.ClientCtr;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import model.Group;
import model.ObjectWrapper;
import model.Player;

public class Group_CreateFrm extends JFrame implements ActionListener{
    private JLabel nameLabel, descLabel;
    private JTextField txtName, txtDesc;
    private JButton btnSubmit;
    private ClientCtr myControl;
    
    public Group_CreateFrm(ClientCtr clientCtr){
        myControl = clientCtr;
        myControl.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_CREATE_GROUP, this));

        this.setVisible(true);  
        this.setSize(400, 250);  
        setLayout(null);  
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  
        setTitle("Tạo hội nhóm");  
        
        nameLabel = new JLabel("Tên nhóm:");  
        nameLabel.setBounds(20, 20, 70, 25);
        txtName = new JTextField();  
        txtName.setBounds(100, 20, 200, 25);
        this.add(nameLabel);
        this.add(txtName);  

        descLabel = new JLabel("Mô tả:");  
        descLabel.setBounds(20, 50, 70, 25);
        txtDesc = new JTextField();  
        txtDesc.setBounds(100, 50, 200, 25);
        this.add(descLabel);
        this.add(txtDesc); 
        
        btnSubmit = new JButton("Tạo nhóm");  
        btnSubmit.setBounds(150, 100, 100, 30);    
        this.add(btnSubmit);          
        btnSubmit.addActionListener(this);  

    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JButton){           
            Group group = new Group();
            group.setName(txtName.getText());
            group.setDesc(txtDesc.getText());
            myControl.sendData(new ObjectWrapper(ObjectWrapper.CREATE_GROUP,group, myControl.getPlayer())); 

        }
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getData() instanceof Group) {
            JOptionPane.showMessageDialog(this, "Tạo nhóm thành công");
            myControl.getMyGroups().add((Group) data.getData());
            myControl.getHomeView().updateListGroups();
            this.dispose();
        }
        else {
            JOptionPane.showMessageDialog(this, "Tạo nhóm thất bại! Vui lòng thử lại");
        }
    }
}
