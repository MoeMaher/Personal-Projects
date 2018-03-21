package System;

import GUI.App;

import javax.swing.*;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Random;

public class Client extends javax.swing.JFrame{

    String Username;
    Socket clientSocket;

    private javax.swing.JTextArea InputText;
    private javax.swing.JList<String> MsgViewArea;
    DefaultListModel<String> msgModel;


    private javax.swing.JButton SendBtn;
    private javax.swing.JTextField TTLArea;
    private javax.swing.JList<String> UsersPanel;
    javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton refreshBtn;
    DefaultListModel<String> model;
    private String msgID;
    private Random rand = new Random();



    public String getUsername(){

        return Username;
    }

    public Client(int PortNum,String U) throws Exception
    {
        String sentence;
        String modifiedSentence;


        int  n = rand.nextInt(100000) + 1;
        msgID = n + "";
        System.out.println("ClientID" + msgID);
        Username = U;
        clientSocket = new Socket("MahersPC", PortNum);
        System.out.println("connection established from client " + clientSocket);

        try{

               DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
               outToServer.writeBytes("ok#"+ Username + '\n');
               System.out.println("sent the username to the server");

           }
           catch(Exception e ){
               System.out.println("4 "+ e.getMessage());
           }

                ClientReciever rec = new ClientReciever(clientSocket, this, Username);
                ClientSender sender = new ClientSender(clientSocket, Username, rec);

        sender.start();
        rec.start();
        System.out.println("sender and reciever started" +
                "with the socket formed");
        initComponents();
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        this.setVisible(true);
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new App().setVisible(true);
            }
        });
    }



    public void sendMsg(){
        try{

            String sentence = InputText.getText();
            msgModel.addElement(Username + " : " + sentence);
            String ttlS = TTLArea.getText();
            int ttl = 0;
            try{
                ttl = Integer.parseInt(ttlS);
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null, "Please enter a TTL number", "Error", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("5 "+ e.getMessage());
                return;
            }
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());


            //String[] s = sentence.split("#");

            Object [] UsersToSend = UsersPanel.getSelectedValues();
            for (Object user: UsersToSend) {
                String sendTo = (String) user;
                int  n = rand.nextInt(1000000000) + 1;
                msgID = n + "";
                System.out.println("ClientID" + msgID);
                String outstr =  sendTo +"#" + ttl + "%" + Username + "@" + msgID +"!"+ sentence;
                if(sentence.equals("bye")){
                    System.out.println("Close Request");
                    outToServer.writeBytes("close"+ "#" + Username + '\n');
                    clientSocket.close();
                    this.dispose();
                    return;
                }

                outToServer.writeBytes(outstr + '\n');



            }
        }
        catch(Exception e ){
            System.out.println(e.getMessage());
        }
    }

    private void initComponents() {

        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        InputText = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        MsgViewArea = new javax.swing.JList<String>();
        msgModel = new DefaultListModel<String>();
        MsgViewArea.setModel(msgModel);
        UsersPanel = new javax.swing.JList<String>();
        model = new DefaultListModel<String>();
        UsersPanel.setModel(model);
        UsersPanel.setFont(new java.awt.Font("Ubuntu", 0, 36));
        jLabel3 = new javax.swing.JLabel();
        SendBtn = new javax.swing.JButton();
        TTLArea = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        refreshBtn = new javax.swing.JButton();

        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("jRadioButtonMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(230, 251, 254));

        jPanel2.setBackground(new java.awt.Color(230, 251, 254));
        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        InputText.setColumns(20);
        InputText.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N
        InputText.setRows(5);
        jScrollPane3.setViewportView(InputText);

        MsgViewArea.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N
        jScrollPane4.setViewportView(MsgViewArea);

        javax.swing.GroupLayout UsersPanelLayout = new javax.swing.GroupLayout(UsersPanel);
        UsersPanel.setLayout(UsersPanelLayout);
        UsersPanelLayout.setHorizontalGroup(
                UsersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );
        UsersPanelLayout.setVerticalGroup(
                UsersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 1118, Short.MAX_VALUE)
        );

        jLabel3.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N
        jLabel3.setText("Active Users :");

        SendBtn.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N
        SendBtn.setText("Send");
        SendBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    sendMsg();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TTLArea.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Ubuntu", 0, 27)); // NOI18N
        jLabel1.setText("TTL :");

        refreshBtn.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N
        refreshBtn.setText("Refresh Users");
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
                 public void actionPerformed(java.awt.event.ActionEvent evt) {
                     String ttlS = TTLArea.getText();
                     int ttl = 0;
                     try {
                         ttl =10;
                         DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                         outToServer.writeBytes("getMembers" + '\n');
                     } catch (Exception e) {
                         JOptionPane.showMessageDialog(null, "Please enter a TTL number", "Error", JOptionPane.INFORMATION_MESSAGE);
                         System.out.println(e.getMessage());
                     }


                 }
             });


        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 736, Short.MAX_VALUE)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(18, 18, 18)
                                                .addComponent(TTLArea, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(SendBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(UsersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                                        .addComponent(refreshBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(18, 18, 18)
                                                .addComponent(UsersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                .addComponent(jScrollPane4)
                                                .addGap(18, 18, 18)
                                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(TTLArea)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(SendBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(refreshBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

}

class ma {
    public  static void main(String[] args) throws Exception {
        Client client = new Client(1111, "Maher");

    }
}
