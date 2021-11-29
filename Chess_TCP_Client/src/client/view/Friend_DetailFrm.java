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
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.Friendship;
import model.Group;
import model.Invitation;
import model.ObjectWrapper;
import model.Player;
import model.Tournament;

public class Friend_DetailFrm extends JFrame implements ActionListener {
    private JLabel idLabel, nameLabel, lblStatus;
    private JTextField txtId, txtName;
    private JButton btnAdd, btnAccept, btnDecline, btnChallenge;
    private JTable tblTournament;
    
    private ClientCtr myControl;
    
    private Player player;
    private Invitation invitation;
    private ArrayList<Tournament> listTour;
    
    public Friend_DetailFrm(ClientCtr clientCtr, Player p) {
        myControl = clientCtr;
        player = p;
        myControl.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_ADD_FRIEND, this));

        this.setSize(new Dimension(600, 440));
        this.setLayout(null);  
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        this.setTitle("Chi tiết người chơi");

        idLabel = new JLabel("ID người chơi: ");
        idLabel.setBounds(30, 20, 100, 35);
        txtId = new JTextField();
        txtId.setText(player.getId()+"");
        txtId.setEditable(false);
        txtId.setBounds(150, 20, 100, 35);  
        this.add(idLabel);
        this.add(txtId);

        nameLabel = new JLabel("Tên người chơi: ");
        nameLabel.setBounds(280, 20, 100, 35);
        txtName = new JTextField();
        txtName.setText(player.getName());
        txtName.setEditable(false);
        txtName.setBounds(410, 20, 100, 35);  
        this.add(nameLabel);
        this.add(txtName);
        
        lblStatus = new JLabel("Đã gửi lời mời kết bạn");
        lblStatus.setBounds(150, 60, 150, 35); 
//        this.add(lblStatus);
        
        btnAccept = new JButton("Đồng ý");
        btnAccept.setBounds(150, 60, 100, 35);  
        btnAccept.addActionListener(this);

        btnDecline = new JButton("Từ chối");
        btnDecline.setBounds(280, 60, 100, 35); 
        btnDecline.addActionListener(this);
            
        btnAdd = new JButton("Thêm bạn");
        btnAdd.setBounds(150, 60, 150, 35); 
        btnAdd.addActionListener(this);

        btnChallenge = new JButton("Thách đấu");
        btnChallenge.setBounds(30, 60, 100, 35); 
        btnChallenge.addActionListener(this);
        if (!player.equals(myControl.getPlayer())){
            this.add(btnChallenge);
        }

        JLabel lblTour = new JLabel("Danh sách giải đấu đã tham gia");
        lblTour.setBounds(30, 120, 200, 25);
        this.add(lblTour);
        
        JPanel tourPanel = new JPanel();
        tourPanel.setSize(this.getSize().width-5, 150);       
        tourPanel.setLayout(new BoxLayout(tourPanel,BoxLayout.Y_AXIS));     
        tblTournament = new JTable();
        JScrollPane scrollPane= new  JScrollPane(tblTournament);
        tblTournament.setFillsViewportHeight(false); 
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 150));
        tourPanel.add(scrollPane);
        tourPanel.setBounds(30, 140, 530, 250); 
        this.add(tourPanel); 
        
        myControl.sendData(new ObjectWrapper(ObjectWrapper.GET_FRIEND_RELATIONSHIP, player, myControl.getPlayer()));
        
        
        myControl.sendData(new ObjectWrapper(ObjectWrapper.GET_LIST_TOUR, player));
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JButton){
            if (((JButton)e.getSource()).equals(btnAccept)){
                myControl.sendData(new ObjectWrapper(ObjectWrapper.ACCEPT_FRIEND_REQUEST,myControl.getPlayer(), player));
            }else if (((JButton)e.getSource()).equals(btnDecline)){
                myControl.sendData(new ObjectWrapper(ObjectWrapper.DECLINE_FRIEND_REQUEST,myControl.getPlayer(), player));
            }else if (((JButton)e.getSource()).equals(btnAdd)){
                myControl.sendData(new ObjectWrapper(ObjectWrapper.ADD_FRIEND, myControl.getPlayer(), player));            
            }else if (((JButton)e.getSource()).equals(btnChallenge)){
                if (myControl.getOnlinePlayers().contains(player)){
                    myControl.sendData(new ObjectWrapper(ObjectWrapper.CHALLENGE,player));
                }else {
                    JOptionPane.showMessageDialog(this, "Người chơi hiện đang offline!");
                }
            }
        }
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {
        switch (data.getPerformative()) {
            case ObjectWrapper.REPLY_GET_LIST_TOUR:
                listTour = (ArrayList<Tournament>)data.getData();
                String[] columnNames = {"Id", "Name", "Start time", "End time"};
                String[][] value = new String[listTour.size()][columnNames.length];
                for(int i=0; i<listTour.size(); i++){
                    value[i][0] = listTour.get(i).getId() +"";
                    value[i][1] = listTour.get(i).getName();
                    value[i][2] = listTour.get(i).getStarttime().toString();
                    value[i][3] = listTour.get(i).getStarttime().toString();
                    
                }   DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        //unable to edit cells
                        return false;
                    }
                };  tblTournament.setModel(tableModel);
                tblTournament.getColumnModel().getColumn(0).setMaxWidth(50);
                break;
            case ObjectWrapper.REPLY_GET_FRIEND_RELATIONSHIP:
                if (data.getData() instanceof Friendship){
                    Friendship friendship = (Friendship)data.getData();
                    System.out.println(friendship.getUserID() + " " + friendship.getFriendID()+ " "+ friendship.getStatus() );
                    if (friendship.getUserID() == player.getId() && friendship.getStatus() == 0){
                        this.add(btnAccept);
                        this.add(btnDecline);
                    }else if (friendship.getFriendID() == player.getId() && friendship.getStatus() == 0){
                        this.add(lblStatus);
                    }else if (friendship.getFriendID() == player.getId() && friendship.getStatus() == 2){
                        this.add(btnAdd);
                    }else if (friendship.getFriendID() == myControl.getPlayer().getId() && friendship.getStatus() == 2){
                        this.add(btnAdd);
                    }
                }else{
                    if (!player.equals(myControl.getPlayer())){
                        this.add(btnAdd);
                    }
                } 
                break;
            case ObjectWrapper.REPLY_ADD_FRIEND:
                if ((boolean) data.getData()){
                    this.btnAdd.setVisible(false);
                    this.add(lblStatus);
                } 
                break;
            case ObjectWrapper.SERVER_INFORM_NEW_FRIEND:
                if (player.equals((Player) data.getData())){
                    lblStatus.setVisible(false);
                }  
                break;
            case ObjectWrapper.REPLY_DECLINE_FRIEND_REQUEST:
                if ((boolean) data.getData()){
                    this.btnAccept.setVisible(false);
                    this.btnDecline.setVisible(false);
                    this.add(btnAdd);
                    Invitation inv = new Invitation();
                    inv.setInviter(player);
                    inv.setType(Invitation.FRIEND_REQUEST);
                    myControl.getMyInvitations().remove(inv);
                    myControl.getHomeView().updateListInvitations();
                }   
                break;
            case ObjectWrapper.REPLY_ACCEPT_FRIEND_REQUEST:
                if ((boolean) data.getData()){
                    this.btnAccept.setVisible(false);
                    this.btnDecline.setVisible(false);
                    myControl.getMyFriends().add(player);
                    myControl.getHomeView().updateListFriends();
                    Invitation inv = new Invitation();
                    inv.setInviter(player);
                    inv.setType(Invitation.FRIEND_REQUEST);
                    myControl.getMyInvitations().remove(inv);
                    myControl.getHomeView().updateListInvitations();
                }   
                break;
            case ObjectWrapper.REPLY_CHALLENGE:
                JOptionPane.showMessageDialog(this, "Đã gửi lời mời");
                break;
            default:
                break;
        }
    }
}
