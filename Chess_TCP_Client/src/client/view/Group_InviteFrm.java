/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import client.control.ClientCtr;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.Group;
import model.GroupMember;
import model.ObjectWrapper;
import model.Player;

public class Group_InviteFrm extends JFrame implements ActionListener{
    private JButton btnInvite;
    private JTable tblFriends;
    private Group group;
    private ArrayList<Player> members;
    private ArrayList <Player> listFriends;
    private ClientCtr myControl;
    
    public Group_InviteFrm(ClientCtr clientCtr, Group gr, ArrayList<Player> mems){
        myControl = clientCtr;
        group = gr;
        members = mems;
        myControl.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_SENT_GROUP_INVITATION, this));
        this.setVisible(true);  
        this.setSize(600, 450);  
        this.setLayout(null);  
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  
        this.setTitle("Mời bạn bè vào nhóm");
        
        btnInvite = new JButton("Mời vào nhóm");
        btnInvite.setBounds(30, 20, 250, 35);   
        btnInvite.addActionListener(this);  
        this.add(btnInvite);
        
        JLabel friendLabel = new JLabel("Danh sách bạn bè");
        friendLabel.setBounds(30, 60, 200, 25);
        this.add(friendLabel);
        
        JPanel friendPanel = new JPanel();
        friendPanel.setSize(this.getSize().width-5, 150);       
        friendPanel.setLayout(new BoxLayout(friendPanel,BoxLayout.Y_AXIS));     
        tblFriends = new JTable();
        JScrollPane scrollPane= new  JScrollPane(tblFriends);
        tblFriends.setFillsViewportHeight(false); 
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 150));
        friendPanel.add(scrollPane);
        friendPanel.setBounds(30, 100, 500, 150); 
        this.add(friendPanel); 
        
        
        listFriends = new ArrayList<>();
        listFriends.addAll(myControl.getMyFriends());
        listFriends.removeIf(player -> (members.contains(player)));
        String[] columnNames = {"Id", "Tên"};
        String[][] value = new String[listFriends.size()][columnNames.length];
        for(int i=0; i<listFriends.size(); i++){
            Player friend = listFriends.get(i);
                value[i][0] = friend.getId() +"";
                value[i][1] = friend.getName();
        }
        DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               //unable to edit cells
               return false;
            }
        };
        tblFriends.setModel(tableModel);

        
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JButton){
            if (((JButton)e.getSource()).equals(btnInvite)){
                int row = tblFriends.getSelectedRow();  
                System.out.println(row);
                if (row < tblFriends.getRowCount() && row >= 0) {
                    myControl.sendData(new ObjectWrapper(ObjectWrapper.SENT_GROUP_INVITATION,group, listFriends.get(row)));
                }
            }
        }
    }
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getPerformative() == ObjectWrapper.REPLY_GET_LIST_GROUP_MEMBER) {
            JOptionPane.showMessageDialog(this, "Đã gửi lời mời");
        }
    }

}
