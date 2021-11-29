package client.view;

import client.control.ClientCtr;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.ObjectWrapper;
import model.Player;

public class Friend_SearchFrm extends JFrame implements ActionListener{
    private ArrayList<Player> listPlayer;
    private JTextField txtKey;
    private JButton btnSearch;
    private JTable tblResult;
    private ClientCtr myControl;
    
    public Friend_SearchFrm(ClientCtr clientCtr){
        myControl = clientCtr;
        myControl.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_SEARCH_PLAYER, this));
        listPlayer = new ArrayList<Player>();
        
        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width-5, this.getSize().height-20);       
        pnMain.setLayout(new BoxLayout(pnMain,BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));
         
        JLabel lblHome = new JLabel("Tìm kiếm người chơi");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);  
        lblHome.setFont (lblHome.getFont ().deriveFont (20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0,20)));
         
        JPanel pn1 = new JPanel();
        pn1.setLayout(new BoxLayout(pn1,BoxLayout.X_AXIS));
        pn1.setSize(this.getSize().width-5, 20);
        pn1.add(new JLabel("Tên người chơi: "));
        txtKey = new JTextField();
        pn1.add(txtKey);
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(this);
        pn1.add(btnSearch);
        pnMain.add(pn1);
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));
 
        JPanel pn2 = new JPanel();
        pn2.setLayout(new BoxLayout(pn2,BoxLayout.Y_AXIS));     
        tblResult = new JTable();
        JScrollPane scrollPane= new  JScrollPane(tblResult);
        tblResult.setFillsViewportHeight(false); 
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 250));
        
        tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblResult.getColumnModel().getColumnIndexAtX(e.getX()); 
                int row = e.getY() / tblResult.getRowHeight();
                
                if (row < tblResult.getRowCount() && row >= 0 && column < tblResult.getColumnCount() && column >= 0) {
                    ObjectWrapper existed = null;
                    for(ObjectWrapper func: myControl.getActiveFunction())
                        if(func.getData() instanceof Friend_DetailFrm) {
                            ((Friend_DetailFrm)func.getData()).dispose();
                            existed = func;
                        }
                    if(existed != null)
                        myControl.getActiveFunction().remove(existed);
                     
                    (new Friend_DetailFrm(myControl, listPlayer.get(row))).setVisible(true);
                }
            }
        });
        
        pn2.add(scrollPane);
        pnMain.add(pn2);    
        this.add(pnMain);
        this.setSize(600,300);              
        this.setLocation(200,10);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        JButton btnClicked = (JButton)e.getSource();
        if(btnClicked.equals(btnSearch)){
            if((txtKey.getText() == null)||(txtKey.getText().length() == 0))
                return;
            //send data to the server
            myControl.sendData(new ObjectWrapper(ObjectWrapper.SEARCH_PLAYER, txtKey.getText().trim()));
            
        }
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getData() instanceof ArrayList<?>) {
            listPlayer = (ArrayList<Player>)data.getData();
            listPlayer.remove(myControl.getPlayer());
            String[] columnNames = {"Id", "Tên"};
            String[][] value = new String[listPlayer.size()][columnNames.length];
            for(int i=0; i<listPlayer.size(); i++){
                value[i][0] = listPlayer.get(i).getId() +"";
                value[i][1] = listPlayer.get(i).getName();
            }
            DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                   //unable to edit cells
                   return false;
                }
            };
            tblResult.setModel(tableModel);
        }else {
            tblResult.setModel(null);
        }
    }
}
