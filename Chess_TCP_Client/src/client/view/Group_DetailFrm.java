package client.view;

import client.control.ClientCtr;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.Group;
import model.Invitation;
import model.ObjectWrapper;
import model.Player;

public class Group_DetailFrm extends JFrame implements ActionListener{
   private JLabel idLabel, nameLabel, descLabel, memberTableLabel;
    private JTextField txtId, txtName, txtDesc;
    private JButton btnAdd, btnAccept, btnDecline;
    private JTable tblMember;
    private ArrayList<Player> members;
    private ClientCtr myControl;
    private Group group;
    
    public Group_DetailFrm(ClientCtr clientCtr, Group gr){
        myControl = clientCtr;
        group = gr;
        myControl.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_GROUP_MEMBER, this));
        
        this.setSize(600, 440);  
        this.setLayout(null);  
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        this.setTitle("Chi tiết hội nhóm");
        
        idLabel = new JLabel("ID hội nhóm: ");
        idLabel.setBounds(30, 20, 100, 35);
        txtId = new JTextField();
        txtId.setText(group.getId()+"");
        txtId.setEditable(false);
        txtId.setBounds(150, 20, 100, 35);  
        this.add(idLabel);
        this.add(txtId);

        nameLabel = new JLabel("Tên hội nhóm: ");
        nameLabel.setBounds(270, 20, 100, 35);
        txtName = new JTextField();
        txtName.setText(group.getName());
        txtName.setEditable(false);
        txtName.setBounds(390, 20, 100, 35);
        
        this.add(nameLabel);
        this.add(txtName);
        
        descLabel = new JLabel("Mô tả: ");
        descLabel.setBounds(30, 60, 100, 35);
        txtDesc = new JTextField();
        txtDesc.setText(group.getDesc());
        txtDesc.setEditable(false);
        txtDesc.setBounds(150, 60, 100, 35); 
        this.add(descLabel);
        this.add(txtDesc);
        
        btnAdd = new JButton("Thêm thành viên");
        btnAdd.setBounds(270, 60, 120, 35); 
        btnAdd.addActionListener(this);

        btnAccept = new JButton("Đồng ý");
        btnAccept.setBounds(270, 60, 120, 35);  
        btnAccept.addActionListener(this);
            
        btnDecline = new JButton("Từ chối");
        btnDecline.setBounds(410, 60, 120, 35); 
        btnDecline.addActionListener(this);

        if (myControl.getMyGroups().contains(group)){
            this.add(btnAdd);
        }else{
            this.add(btnAccept);
            this.add(btnDecline);
        }
        
        JLabel friendLabel = new JLabel("Danh sách thành viên");
        friendLabel.setBounds(30, 100, 200, 25);
        this.add(friendLabel);
        
        JPanel memberPanel = new JPanel();
        memberPanel.setSize(this.getSize().width-5, 150);       
        memberPanel.setLayout(new BoxLayout(memberPanel,BoxLayout.Y_AXIS));     
        tblMember = new JTable();
        JScrollPane scrollPane= new  JScrollPane(tblMember);
        tblMember.setFillsViewportHeight(false); 
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 150));
        memberPanel.add(scrollPane);
        memberPanel.setBounds(30, 140, 540, 250); 
        this.add(memberPanel); 
        
        tblMember.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblMember.getColumnModel().getColumnIndexAtX(e.getX()); 
                int row = e.getY() / tblMember.getRowHeight();
                
                if (row < tblMember.getRowCount() && row >= 0 && column < tblMember.getColumnCount() && column >= 0) {
                    ObjectWrapper existed = null;
                    for(ObjectWrapper func: myControl.getActiveFunction())
                        if(func.getData() instanceof Friend_DetailFrm) {
                            ((Friend_DetailFrm)func.getData()).dispose();
                            existed = func;
                        }
                    if(existed != null)
                        myControl.getActiveFunction().remove(existed);
                     
                    (new Friend_DetailFrm(myControl, members.get(row))).setVisible(true);
                }
            }
        });
        
        myControl.sendData(new ObjectWrapper(ObjectWrapper.GET_LIST_GROUP_MEMBER,group)); 
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JButton){
            if (((JButton)e.getSource()).equals(btnAccept)){
                myControl.sendData(new ObjectWrapper(ObjectWrapper.ACCEPT_GROUP_REQUEST,group, myControl.getPlayer()));            
            }else if (((JButton)e.getSource()).equals(btnDecline)){
                myControl.sendData(new ObjectWrapper(ObjectWrapper.DECLINE_GROUP_REQUEST,group, myControl.getPlayer()));            
            }else if (((JButton)e.getSource()).equals(btnAdd)){
                ObjectWrapper existed = null;
                for(ObjectWrapper func: myControl.getActiveFunction())
                    if(func.getData() instanceof Group_InviteFrm) {
                        ((Group_InviteFrm)func.getData()).dispose();
                        existed = func;
                    }
                    if(existed != null)
                        myControl.getActiveFunction().remove(existed);

                    (new Group_InviteFrm(myControl, group, members)).setVisible(true);
            }
        }
    } 
    
    private void updateTable(){
        String[] columnNames = {"Id", "Tên"};
        String[][] value = new String[members.size()][columnNames.length];
        for(int i=0; i<members.size(); i++){
            value[i][0] = members.get(i).getId() +"";
            value[i][1] = members.get(i).getName();
        }
        DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               //unable to edit cells
               return false;
            }
        };
        tblMember.setModel(tableModel);
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {
        switch (data.getPerformative()) {
            case ObjectWrapper.REPLY_GET_LIST_GROUP_MEMBER:
                members = (ArrayList<Player>) data.getData();
                updateTable();
                break;
            case ObjectWrapper.REPLY_ACCEPT_GROUP_REQUEST:{
                this.btnAccept.setVisible(false);
                this.btnDecline.setVisible(false);
                this.add(btnAdd);
                members.add(myControl.getPlayer());
                updateTable();
                myControl.getMyGroups().add(group);
                myControl.getHomeView().updateListGroups();
                myControl.getHomeView().updateListInvitations();
                break;
            }
            case ObjectWrapper.REPLY_DECLINE_GROUP_REQUEST:{
                this.btnAccept.setVisible(false);
                this.btnDecline.setVisible(false);
                myControl.getHomeView().updateListInvitations();
                break;
            }
            default:
                break;
        }
    }
    
}
