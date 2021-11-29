package client.view;

import client.control.ClientCtr;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
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
import model.Invitation;
import model.ObjectWrapper;
import model.Player;

public class HomeFrm extends JFrame implements ActionListener{
    private JButton btnTimTran, btnThemBan, btnTaoNhom, btnGiaiDau, btnBXH ;
    private JTable tblFriends, tblGroups, tblInvitations, tblChallenges;
    
    private ClientCtr myControl;
    
    private ArrayList<Player> listFriends;
    private ArrayList<Group> listGroups;
    private ArrayList<Invitation> listInvitations;
    private ArrayList<Player> listChallenges = new ArrayList<>();
    
    public HomeFrm(ClientCtr ctr){
        super("Chess");
        myControl = ctr;
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        
        this.setContentPane(mainPanel);
        this.pack();        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(700, 420));
        this.setResizable(false);
        
        JLabel lblGreeting = new JLabel("Xin chào, " + myControl.getPlayer().getName());
        lblGreeting.setBounds(new Rectangle(20, 20, 150, 30));
        mainPanel.add(lblGreeting, null);
        
        btnTimTran = new JButton("Tìm trận");
        btnTimTran.setBounds(20, 50, 150, 35); 
        btnTimTran.addActionListener(this);
        this.add(btnTimTran);
        
        btnThemBan = new JButton("Thêm bạn");
        btnThemBan.setBounds(20, 105, 150, 35);  
        btnThemBan.addActionListener(this);
        this.add(btnThemBan);
        
        btnTaoNhom = new JButton("Tạo nhóm");
        btnTaoNhom.setBounds(20, 160, 150, 35);   
        btnTaoNhom.addActionListener(this);
        this.add(btnTaoNhom);
        
        btnGiaiDau = new JButton("Tham dự giải đấu");
        btnGiaiDau.setBounds(20, 215, 150, 35);    
        this.add(btnGiaiDau);
        
        btnBXH = new JButton("Bảng xếp hạng");
        btnBXH.setBounds(20, 270, 150, 35);  
        btnBXH.addActionListener(this);
        this.add(btnBXH);
        
        JLabel lblInvitations = new JLabel("Danh sách lời mời");
        lblInvitations.setBounds(new Rectangle(200, 20, 150, 30));
        mainPanel.add(lblInvitations, null);
        
        JLabel lblGroups = new JLabel("Danh sách nhóm");
        lblGroups.setBounds(new Rectangle(200, 200, 150, 30));
        mainPanel.add(lblGroups, null);
        
        tblInvitations = new JTable();
        JScrollPane scrollPane1 = new  JScrollPane(tblInvitations);
        tblInvitations.setFillsViewportHeight(false); 
        scrollPane1.setPreferredSize(new Dimension(scrollPane1.getPreferredSize().width, 150));
        scrollPane1.setBounds(200, 50, 250, 150); 
        this.add(scrollPane1);
        
        tblGroups = new JTable();
        JScrollPane scrollPane2 = new  JScrollPane(tblGroups);
        tblGroups.setFillsViewportHeight(false); 
        scrollPane2.setPreferredSize(new Dimension(scrollPane2.getPreferredSize().width, 150));
        scrollPane2.setBounds(200, 230, 250, 150); 
        this.add(scrollPane2);
        
        JLabel lblFriends = new JLabel("Danh sách bạn bè");
        lblFriends.setBounds(new Rectangle(470, 20, 150, 30));
        mainPanel.add(lblFriends, null);
        
        tblFriends = new JTable();
        JScrollPane scrollPane3 = new  JScrollPane(tblFriends);
        tblFriends.setFillsViewportHeight(false); 
        scrollPane3.setPreferredSize(new Dimension(scrollPane1.getPreferredSize().width, 150));
        scrollPane3.setBounds(470, 50, 200, 150); 
        this.add(scrollPane3);
        
        JLabel lblChallenge = new JLabel("Lời mời thách đấu");
        lblChallenge.setBounds(new Rectangle(470, 200, 150, 30));
        mainPanel.add(lblChallenge, null);
        
        tblChallenges = new JTable();
        JScrollPane scrollPane4 = new  JScrollPane(tblChallenges);
        tblChallenges.setFillsViewportHeight(false); 
        scrollPane4.setPreferredSize(new Dimension(scrollPane1.getPreferredSize().width, 150));
        scrollPane4.setBounds(470, 230, 200, 150); 
        this.add(scrollPane4);
        
        myControl.setHomeView(this);
        //tblFriends
        tblFriends.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblFriends.getColumnModel().getColumnIndexAtX(e.getX()); 
                int row = e.getY() / tblFriends.getRowHeight();
                
                if (row < tblFriends.getRowCount() && row >= 0 && column < tblFriends.getColumnCount() && column >= 0) {
                    ObjectWrapper existed = null;
                    for(ObjectWrapper func: myControl.getActiveFunction())
                        if(func.getData() instanceof Friend_DetailFrm) {
                            ((Friend_DetailFrm)func.getData()).dispose();
                            existed = func;
                        }
                    if(existed != null)
                        myControl.getActiveFunction().remove(existed);
                     
                    (new Friend_DetailFrm(myControl, myControl.getMyFriends().get(row))).setVisible(true);
                }
            }
        });
        tblChallenges.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblChallenges.getColumnModel().getColumnIndexAtX(e.getX()); 
                int row = e.getY() / tblChallenges.getRowHeight();
                if (row < tblChallenges.getRowCount() && row >= 0 && column < tblChallenges.getColumnCount() && column >= 0) {
                    Player opponent = listChallenges.get(row);
                    if (JOptionPane.showConfirmDialog(null, "Chấp nhận thách đấu?", null,
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        // yes option
                        listChallenges.remove(opponent);
                        updateListChallenges();
                        myControl.sendData(new ObjectWrapper(ObjectWrapper.ACCEPT_CHALLENGE, opponent));
                    } else {
                        // no option
                        listChallenges.remove(opponent);
                        updateListChallenges();
                        myControl.sendData(new ObjectWrapper(ObjectWrapper.DECLINE_CHALLENGE, opponent));
                    }
                }
            }
        });
//        //tblGroups
        tblGroups.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblGroups.getColumnModel().getColumnIndexAtX(e.getX()); 
                int row = e.getY() / tblGroups.getRowHeight();
                
                if (row < tblGroups.getRowCount() && row >= 0 && column < tblGroups.getColumnCount() && column >= 0) {
                    ObjectWrapper existed = null;
                    for(ObjectWrapper func: myControl.getActiveFunction())
                        if(func.getData() instanceof Group_DetailFrm) {
                            ((Group_DetailFrm)func.getData()).dispose();
                            existed = func;
                        }
                    if(existed != null)
                        myControl.getActiveFunction().remove(existed);
                     
                    (new Group_DetailFrm(myControl, myControl.getMyGroups().get(row))).setVisible(true);
                }
            }
        });
//        //tblInvitations
        tblInvitations.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblInvitations.getColumnModel().getColumnIndexAtX(e.getX()); 
                int row = e.getY() / tblInvitations.getRowHeight();
                
                if (row < tblInvitations.getRowCount() && row >= 0 && column < tblInvitations.getColumnCount() && column >= 0) {
                    ObjectWrapper existed = null;
                    Invitation inv = myControl.getMyInvitations().get(row);
                    Object target = inv.getTarget();
                    if (inv.getType().equals(Invitation.FRIEND_REQUEST)){
                        for(ObjectWrapper func: myControl.getActiveFunction())
                        if(func.getData() instanceof Friend_DetailFrm) {
                            ((Friend_DetailFrm)func.getData()).dispose();
                            existed = func;
                        }
                        if(existed != null)
                            myControl.getActiveFunction().remove(existed);

                        (new Friend_DetailFrm(myControl, myControl.getMyInvitations().get(row).getInviter())).setVisible(true);
                    }else if (inv.getType().equals(Invitation.GROUP_INVITATION)){
                        for(ObjectWrapper func: myControl.getActiveFunction())
                        if(func.getData() instanceof Group_DetailFrm) {
                            ((Group_DetailFrm)func.getData()).dispose();
                            existed = func;
                        }
                        if(existed != null)
                            myControl.getActiveFunction().remove(existed);

                        (new Group_DetailFrm(myControl, (Group) target)).setVisible(true);
                    }
                    //
                    
                }
            }
        });
//        get list friends
        myControl.sendData(new ObjectWrapper(ObjectWrapper.GET_LIST_FRIEND, myControl.getPlayer()));
//        //get list groups
        myControl.sendData(new ObjectWrapper(ObjectWrapper.GET_LIST_GROUP, myControl.getPlayer()));
//        //get list invitations
        myControl.sendData(new ObjectWrapper(ObjectWrapper.GET_ALL_INVITATION, myControl.getPlayer())); 
        this.updateListChallenges();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JButton){
            if (((JButton)e.getSource()).equals(btnTimTran)){
//                btnTimTran.setEnabled(false);
//                Player player = new Player();
//                player.setId(2);
//                player.setName("opponent");
//                GameFrm gameFrm = new GameFrm(myControl, player, player);
//                gameFrm.setVisible(true);
            }
            if (((JButton)e.getSource()).equals(btnThemBan)){
                //btnTimBan
                for(ObjectWrapper func: myControl.getActiveFunction())
                    if(func.getData() instanceof Friend_SearchFrm) {
                        ((Friend_SearchFrm)func.getData()).setVisible(true);
                        return;
                    }
                Friend_SearchFrm scv = new Friend_SearchFrm(myControl);
                scv.setVisible(true);
            }else if (((JButton)e.getSource()).equals(btnTaoNhom)){
                //btnTaoHoiNhom
                for(ObjectWrapper func: myControl.getActiveFunction())
                    if(func.getData() instanceof Group_CreateFrm) {
                        ((Group_CreateFrm)func.getData()).setVisible(true);
                        return;
                    }
                Group_CreateFrm cgv = new Group_CreateFrm(myControl);
                cgv.setVisible(true);
            
            }else if (((JButton)e.getSource()).equals(btnBXH)){
                //btnBXH
                for(ObjectWrapper func: myControl.getActiveFunction())
                    if(func.getData() instanceof StandingsFrm) {
                        ((StandingsFrm)func.getData()).setVisible(true);
                        return;
                    }
                StandingsFrm cgv = new StandingsFrm(myControl);
                cgv.setVisible(true);
            }
        }
    }
    
    public void updateListFriends(){
        
        listFriends = myControl.getMyFriends();

        String[] columnNames = {"Id", "Name", "Status"};
        String[][] value = new String[listFriends.size()][columnNames.length];
        for(int i=0; i<listFriends.size(); i++){
            Player friend = listFriends.get(i);
            value[i][0] = friend.getId() +"";
            value[i][1] = friend.getName();

            if (myControl.getOnlinePlayers().contains(friend)){
                value[i][2] = "online";
            }else{
                value[i][2] = "offline";
            }
            if (myControl.getIngamePlayers().contains(friend)){
                    value[i][2] = "ingame";
                }
        }
        DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               //unable to edit cells
               return false;
            }
        };
        tblFriends.setModel(tableModel);
        tblFriends.getColumnModel().getColumn(0).setMaxWidth(50);
        tblFriends.getColumnModel().getColumn(0).setResizable(false);
    }
    
    public void updateListGroups(){
        listGroups = myControl.getMyGroups();
                
        String[] columnNames = {"Id", "Tên", "Mô tả"};
        String[][] value = new String[listGroups.size()][columnNames.length];
        for(int i=0; i<listGroups.size(); i++){
            value[i][0] = listGroups.get(i).getId() +"";
            value[i][1] = listGroups.get(i).getName();
            value[i][2] = listGroups.get(i).getDesc();

        }
        DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               //unable to edit cells
               return false;
            }
        };
        tblGroups.setModel(tableModel);
    }
    
    public void updateListInvitations(){        
        listInvitations = myControl.getMyInvitations();                
        String[] columnNames = {"Loại lời mời", "Người mời", "Tên nhóm/giải"};
        String[][] value = new String[listInvitations.size()][columnNames.length];
        for(int i=0; i<listInvitations.size(); i++){
            value[i][0] = listInvitations.get(i).getType();
            value[i][1] = listInvitations.get(i).getInviter().getName();
            Object target = listInvitations.get(i).getTarget();
            if(target instanceof Group){
                value[i][2] = ((Group) target).getName();
            }else{
//                value[i][2] = ((Tournament) target).getName();
            }

        }
        DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               //unable to edit cells
               return false;
            }
        };
        tblInvitations.setModel(tableModel);
    }
    
    public void updateListChallenges(){
        String[] columnNames = {"STT", "Người mời"};
        listChallenges.removeIf(player -> (myControl.getIngamePlayers().contains(player)));
        listChallenges.removeIf(player -> (!myControl.getOnlinePlayers().contains(player)));

        String[][] value = new String[listChallenges.size()][columnNames.length];
        for(int i=0; i<listChallenges.size(); i++){
            value[i][0] = (i+1) + "";
            value[i][1] = listChallenges.get(i).getName();
        }
        DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               //unable to edit cells
               return false;
            }
        };
        tblChallenges.setModel(tableModel);
    }

    public void openGameFrm(Player opponent, Player first){
        ObjectWrapper existed = null;
        for(ObjectWrapper func: myControl.getActiveFunction())
            if(func.getData() instanceof GameFrm){
                ((GameFrm)func.getData()).dispose();
                existed = func;
            }else if (func.getData() instanceof JFrame)
                ((JFrame)func.getData()).dispose();
        if(existed != null)
            myControl.getActiveFunction().remove(existed);
        
        (new GameFrm(myControl, opponent, first)).setVisible(true);
        this.setVisible(false);
    }
    
    public ArrayList<Player> getListChallenges() {
        return listChallenges;
    }
    
    
}
