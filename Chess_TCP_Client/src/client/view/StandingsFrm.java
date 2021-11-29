/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import client.control.ClientCtr;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.ObjectWrapper;
import model.Player;
import model.StandingsDetail;

/**
 *
 * @author Administrator
 */
public class StandingsFrm extends JFrame{
    private JTable tblStandings;
    private ClientCtr myControl;
    private ArrayList<StandingsDetail> standings;

    public StandingsFrm(ClientCtr clientCtr){
        super("Bảng xếp hạng");
        myControl = clientCtr;
        myControl.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_GET_STANDINGS, this));
        
        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width-5, this.getSize().height-20);       
        pnMain.setLayout(new BoxLayout(pnMain,BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));
         
        JLabel lblHome = new JLabel("Bảng xếp hạng");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);  
        lblHome.setFont (lblHome.getFont ().deriveFont (20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0,20)));
        
        JPanel pn2 = new JPanel();
        pn2.setLayout(new BoxLayout(pn2,BoxLayout.Y_AXIS));     
        tblStandings = new JTable();
        JScrollPane scrollPane= new  JScrollPane(tblStandings);
        tblStandings.setFillsViewportHeight(false); 
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 250));
        
        pn2.add(scrollPane);
        pnMain.add(pn2);    
        this.add(pnMain);
        this.setSize(600,300);              
        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        tblStandings.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblStandings.getColumnModel().getColumnIndexAtX(e.getX()); 
                int row = e.getY() / tblStandings.getRowHeight();
                
                if (row < tblStandings.getRowCount() && row >= 0 && column < tblStandings.getColumnCount() && column >= 0) {
                    ObjectWrapper existed = null;
                    for(ObjectWrapper func: myControl.getActiveFunction())
                        if(func.getData() instanceof Friend_DetailFrm) {
                            ((Friend_DetailFrm)func.getData()).dispose();
                            existed = func;
                        }
                    if(existed != null)
                        myControl.getActiveFunction().remove(existed);
                     
                    (new Friend_DetailFrm(myControl, standings.get(row).getPlayer())).setVisible(true);
                }
            }
        });
        
        myControl.sendData(new ObjectWrapper(ObjectWrapper.GET_STANDINGS, null));        
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getPerformative() == ObjectWrapper.REPLY_GET_STANDINGS) {
            standings = ((ArrayList<StandingsDetail>) data.getData());
            
            Collections.sort(standings, new Comparator<StandingsDetail>() {
            @Override
            public int compare(StandingsDetail sd1, StandingsDetail sd2) {
                if (sd1.getPlayed() == 0 || sd2.getPlayed() == 0){
                    return 0;
                }else{
                    return ((float) sd1.getWon()/sd1.getPlayed()) < ((float) sd2.getWon()/sd2.getPlayed()) ? 1 :-1;
                }
            }
        });
        String[] columnNames = {"Id", "Tên", "Tỷ lệ thắng"};
        String[][] value = new String[standings.size()][columnNames.length];
        for(int i=0; i<standings.size(); i++){
            StandingsDetail standingsDetail = standings.get(i);
            value[i][0] = standingsDetail.getPlayer().getId() +"";
            value[i][1] = standingsDetail.getPlayer().getName();
            if (standingsDetail.getPlayed() == 0){
                value[i][2] = "--/--";
            }else{
                Float winrate = (float)standingsDetail.getWon()/standingsDetail.getPlayed()*100;
                value[i][2] = String.format("%.02f%%" , winrate);
            }
            
        }
        DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               //unable to edit cells
               return false;
            }
        };
        tblStandings.setModel(tableModel);
        tblStandings.getColumnModel().getColumn(0).setMaxWidth(100);
        tblStandings.getColumnModel().getColumn(0).setResizable(false);
        
//        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
//        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
//        tblStandings.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );
        }
    }

}
