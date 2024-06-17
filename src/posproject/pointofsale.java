/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package posproject;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.swing.table.TableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import java.time.LocalDateTime;
import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;




/**
 *
 * @author LENOVO
 */
public class pointofsale extends javax.swing.JFrame {
    private static pointofsale instance;
    public String currentUserEmail; 
    
String emailToUse = this.currentUserEmail;
// Now you can use emailToUse as a regular string in your code



    public void setCurrentUserEmail(String email) {
    this.currentUserEmail = email;
    System.out.println("Current User Email set in pointofsale: " + this.currentUserEmail);
   
}
  
private double getTotalFromLabel(JLabel label) {
    // Parse the total sale amount from the given JLabel
    try {
        return Double.parseDouble(label.getText().replace("₦", "").trim());
    } catch (NumberFormatException e) {
        e.printStackTrace();
        return 0;
    }
}

   
    public class SessionManager {
    private static SessionManager instance;
    private String currentStaffID;

    private SessionManager() {}

    public SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentStaffID(String staffID) {
        this.currentStaffID = staffID;
    }

    public String getCurrentStaffID() {
        return currentStaffID;
    }
}

    
    public pointofsale() {
    initComponents();
    String[] columnNames = { "S/N", "Item Name", "Quantity", "Price" };

    // Create a table model
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);

    // Set the model to the table
    jTable1.setModel(model);
    
}
    public static pointofsale getInstance() {
        if (instance == null) {
            instance = new pointofsale();
        }
        return instance;
    }
    

    public class NotificationScheduler {

    public void startScheduledTask() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            checkAndNotifyLowStock();
            checkAndNotifyExpirySoon();
        };

        // Schedule the task to run once a day
        executorService.scheduleAtFixedRate(task, 0, 1, TimeUnit.DAYS);
    }

   private void sendNotification(String message) {
    
    JOptionPane.showMessageDialog(null, message, "Notification", JOptionPane.INFORMATION_MESSAGE);
}


    private void checkAndNotifyLowStock() {
    // Database query to find low stock items
    String sql = "SELECT name, stockquantity FROM inventory WHERE stockquantity <= lowstock";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos", "root", "password");
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            String itemName = rs.getString("name");
            int stockQuantity = rs.getInt("stockquantity");
            
            // Send notification
            sendNotification("Low stock alert for " + itemName + ". Current stock: " + stockQuantity);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

   private void checkAndNotifyExpirySoon() {
    // Database query to find items within a month of expiry
    String sql = "SELECT name, expirydate FROM inventory WHERE STR_TO_DATE(expirydate, '%d%m%Y') <= DATE_ADD(CURDATE(), INTERVAL 1 MONTH)";
    
    // Define SimpleDateFormat to parse the expirydate string
    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos", "root", "password");
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            String itemName = rs.getString("name");
            String expiryDateString = rs.getString("expirydate");

            try {
                // Parse the expiry date string to java.util.Date
                Date expiryDate = sdf.parse(expiryDateString);

                // Format date for notification message
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formattedExpiryDate = outputFormat.format(expiryDate);

                // Send notification
                sendNotification("Expiry alert for " + itemName + ". Expiry date: " + formattedExpiryDate);

            } catch (ParseException e) {
                System.err.println("Error parsing the expiry date for item: " + itemName);
                e.printStackTrace();
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.err.println("SQL Error: " + e.getMessage());
    }
}


}

    private void displayPassportPhotoByEmail() {
    if (currentUserEmail == null || currentUserEmail.isEmpty()) return; // Check if currentUserEmail is set
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "password");
        String sql = "SELECT passportphoto FROM salesperson WHERE email = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, currentUserEmail); // Use currentUserEmail directly
        rs = pstmt.executeQuery();

        if (rs.next()) {
            byte[] imgBytes = rs.getBytes("passportphoto");
            if (imgBytes != null) {
                ImageIcon imgIcon = new ImageIcon(imgBytes);
                Image img = imgIcon.getImage().getScaledInstance(jLabel2.getWidth(), jLabel2.getHeight(), Image.SCALE_SMOOTH);
                jLabel2.setIcon(new ImageIcon(img));
            } else {
                jLabel2.setIcon(null); // No image found
            }
        } else {
            jLabel2.setIcon(null); // No record found
        }
    } catch (SQLException e) {
        e.printStackTrace();
        jLabel2.setIcon(null); // Error occurred
    } finally {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

private void displayStaffIDByEmail() {
    if (currentUserEmail == null || currentUserEmail.isEmpty()) return; // Check if currentUserEmail is set
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "password");
        String sql = "SELECT staffid FROM salesperson WHERE email = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, currentUserEmail); // Use currentUserEmail directly
        rs = pstmt.executeQuery();

        if (rs.next()) {
            String staffID = rs.getString("staffid");
            jLabel3.setText(staffID);
        } else {
            jLabel3.setText(""); // No staff ID found
        }
    } catch (SQLException e) {
        e.printStackTrace();
        jLabel3.setText(""); // Error occurred
    } finally {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

private void displayStaffNameByEmail() {
    if (currentUserEmail == null || currentUserEmail.isEmpty()) return; // Check if currentUserEmail is set
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "password");
        String sql = "SELECT staffname FROM salesperson WHERE email = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, currentUserEmail); // Use currentUserEmail directly
        rs = pstmt.executeQuery();

        if (rs.next()) {
            String staffName = rs.getString("staffname");
            jLabel4.setText(staffName);
        } else {
            jLabel4.setText(""); // No staff name found
        }
    } catch (SQLException e) {
        e.printStackTrace();
        jLabel4.setText(""); // Error occurred
    } finally {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

    private List<String> checkLowStockItems() {
    List<String> lowStockItems = new ArrayList<>();
    String sql = "SELECT name, stockquantity FROM inventory WHERE stockquantity <= lowstock";

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos", "root", "password");
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            String itemName = rs.getString("name");
            int stockQuantity = rs.getInt("stockquantity");
            lowStockItems.add(itemName + " (Qty: " + stockQuantity + ")");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lowStockItems;
}

    private void displayLowStockAlert() {
    List<String> lowStockItems = checkLowStockItems();
    if (!lowStockItems.isEmpty()) {
        String message = "Low stock items:\n" + String.join("\n", lowStockItems);
        JOptionPane.showMessageDialog(null, message, "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
    }
}
private void sendLowStockEmail(List<String> lowStockItems, String toEmail) {
    // Setup mail server properties
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    // Assuming you have an application-specific password for your Gmail account
    String myAccountEmail = "oyinkanogunlabi@gmail.com";
    String password = "oefc ahng mlsm xulr";

    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(myAccountEmail, password);
        }
    });
}

    // Other methods in your class...

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setPreferredSize(new java.awt.Dimension(106, 88));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 106, Short.MAX_VALUE)
        );

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setText("staff ID");

        jLabel4.setText("staff name");

        jButton1.setText("New Cart");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setForeground(new java.awt.Color(255, 51, 51));
        jButton2.setText("Clock Out");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton2)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton1)))))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addGap(73, 73, 73)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI Semilight", 1, 36)); // NOI18N
        jLabel1.setText("Point of Sale");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "S/N", "Item Name", "Quantity", "Price"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton3.setText("Confirm Sale");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Print Reciept");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI Semilight", 1, 14)); // NOI18N
        jLabel5.setText("Total:");

        jLabel6.setFont(new java.awt.Font("Segoe UI Semilight", 1, 18)); // NOI18N
        jLabel6.setText("₦");

        jButton5.setText("Search");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(114, 114, 114)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton4)
                                .addGap(29, 29, 29)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addGap(241, 241, 241))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jTextField1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton5))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton5)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton3)
                                .addContainerGap(64, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel7)
                                        .addComponent(jLabel5))
                                    .addComponent(jButton4))
                                .addGap(0, 0, Short.MAX_VALUE))))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

    StringBuilder receiptText = new StringBuilder("RECEIPT\nItem\tQuantity\tPrice\n----------------------------------\n");
    double total = 0;
    for (int i = 0; i < jTable1.getRowCount(); i++) {
        String item = (String) jTable1.getValueAt(i, 1); // Adjust column index as per your table
        int quantity = (Integer) jTable1.getValueAt(i, 2); // Adjust column index as per your table
        double price = (Double) jTable1.getValueAt(i, 3); // Adjust column index as per your table
        double lineTotal = quantity * price;
        total += lineTotal;
        receiptText.append(String.format("%s\t%d\t%.2f\n", item, quantity, price));
    }
    receiptText.append("----------------------------------\n");
    receiptText.append(String.format("Total: %.2f\n", total));

    // Check for connected printers
    PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
    if (printServices.length > 0) {
        // Attempt to print to the printer
        try {
            DocPrintJob job = printServices[0].createPrintJob();
            byte[] bytes = receiptText.toString().getBytes();
            Doc doc = new SimpleDoc(bytes, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
            job.print(doc, null);
            JOptionPane.showMessageDialog(null, "Sending receipt to the printer...");
            return; // Exit method successfully after sending to printer
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to print receipt, attempting to open in Notepad...");
            // Don't return; continue to attempt to open in Notepad
        }
    }

    // Fallback to Notepad if printing fails or no printers are connected
   // Fallback to Notepad if printing fails or no printers are connected
try {
    File tempFile = File.createTempFile("receipt", ".txt");
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
        writer.write(receiptText.toString());
    }

    // Prioritize Desktop.getDesktop().open(tempFile)
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().open(tempFile);
    } else {
        // If Desktop is not supported, try alternatives with Runtime.getRuntime().exec()
        try {
            // Option 1: Using "start notepad" (adjust path if needed)
            Runtime.getRuntime().exec("start notepad " + tempFile.getAbsolutePath());
        } catch (IOException e) {
            // Option 2: Using full path to notepad.exe (adjust path if needed)
            Runtime.getRuntime().exec("C:\\Windows\\System32\\notepad.exe " + tempFile.getAbsolutePath());
        }
    }
} catch (IOException e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(null, "Failed to open the file in Notepad.");
}




    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
    String searchText = jTextField1.getText().trim();
if (searchText.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Please enter an item to search for.", "Info", JOptionPane.INFORMATION_MESSAGE);
    return;
}

try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos", "root", "password");
     PreparedStatement pstmt = conn.prepareStatement("SELECT name, price, expirydate, stockquantity FROM inventory WHERE name LIKE ?")) {
    
    pstmt.setString(1, "%" + searchText + "%");
    try (ResultSet rs = pstmt.executeQuery()) {
        boolean itemFound = false;
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date today = new Date();

        while (rs.next()) {
            String expiryDateString = rs.getString("expirydate");
            Date expiryDate = sdf.parse(expiryDateString);
            int stockQuantity = rs.getInt("stockquantity");

            if (expiryDate.compareTo(today) < 0) {
                JOptionPane.showMessageDialog(this, "Item '" + rs.getString("name") + "' is expired.", "Info", JOptionPane.INFORMATION_MESSAGE);
                continue;
            }

            if (stockQuantity <= 0) {
                JOptionPane.showMessageDialog(this, "Item '" + rs.getString("name") + "' is out of stock.", "Info", JOptionPane.INFORMATION_MESSAGE);
                continue;
            }

            itemFound = true;
            String itemName = rs.getString("name");
            double price = rs.getDouble("price");

            String quantityString = JOptionPane.showInputDialog(this, "Enter quantity for " + itemName + ":", "Select Quantity", JOptionPane.PLAIN_MESSAGE);
            if (quantityString != null && !quantityString.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityString);
                    if (quantity <= 0 || quantity > stockQuantity) {
                        JOptionPane.showMessageDialog(this, "Invalid quantity entered for '" + itemName + "'.", "Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    
                    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                    int rowCount = model.getRowCount() + 1; // For S/N
                    model.addRow(new Object[]{rowCount, itemName, quantity, price * quantity}); // Add row with calculated total for this item
                    updateTotal(); // Update total in jLabel7
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for quantity for '" + itemName + "'.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        if (!itemFound) {
            JOptionPane.showMessageDialog(this, "No item found matching '" + searchText + "'.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
} catch (SQLException e) {
    JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
} catch (ParseException e) {
    JOptionPane.showMessageDialog(this, "Error parsing expiry date.", "Error", JOptionPane.ERROR_MESSAGE);   
}}

private void updateTotal() {
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    double total = 0;
    for (int i = 0; i < model.getRowCount(); i++) {
        int quantity = (Integer) model.getValueAt(i, 2); // Assuming quantity is at column index 2
        double price = (Double) model.getValueAt(i, 3); // Assuming price is at column index 3
        total += price; // Multiply quantity by price for each item
    }
    jLabel7.setText(String.format("%.2f", total)); // Update the total price in jLabel7


        
     // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    Connection conn = null;
    PreparedStatement pstmt = null;
    PreparedStatement salesPstmt = null; // PreparedStatement for inserting into salesperformance table
    

    try {
        // Establish a connection
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos", "root", "password");

        // Loop through each row in the jTable1 to update inventory and record the sale
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            String itemName = (String) jTable1.getValueAt(i, 1); // Item name
            int quantitySold = (Integer) jTable1.getValueAt(i, 2); // Quantity sold

            // Update inventory stock quantity
            String inventorySql = "UPDATE inventory SET stockquantity = stockquantity - ? WHERE name = ?";
            pstmt = conn.prepareStatement(inventorySql);
            pstmt.setInt(1, quantitySold);
            pstmt.setString(2, itemName);
            pstmt.executeUpdate();
            
            
            

        // Capture the output
            String emailToUse = this.currentUserEmail;

            // Insert sale record into salesperformance table
            String salesSql = "INSERT INTO salesperformance (salesperson, itemsold, quantitysold, totalsold, date) VALUES (?, ?, ?, ?, NOW())";
            salesPstmt = conn.prepareStatement(salesSql);
            salesPstmt.setString(1, emailToUse);
            salesPstmt.setString(2, itemName);
            salesPstmt.setInt(3, quantitySold);
            salesPstmt.setDouble(4, getTotalFromLabel(jLabel7)); // Assuming a method to parse total from jLabel7
            salesPstmt.executeUpdate();
            
        }

        JOptionPane.showMessageDialog(null, "Sale recorded successfully.");
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
    } finally {
        // Clean up
        try {
            if (pstmt != null) pstmt.close();
            if (salesPstmt != null) salesPstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }}
    

    // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    this.dispose();

    // Create a new instance of salespersondash and make it visible
    salespersondash salespersonDash = new salespersondash();
    salespersonDash.setVisible(true);          // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
      DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.setRowCount(0);
    jLabel7.setText("");// This will clear all the rows      // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(pointofsale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(pointofsale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(pointofsale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(pointofsale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new pointofsale().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
