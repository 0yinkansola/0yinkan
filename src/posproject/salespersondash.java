/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package posproject;

import java.awt.Desktop;
import java.awt.Image;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author LENOVO
 */
public class salespersondash extends javax.swing.JFrame {
    private String currentUserEmail;
    public void setCurrentUserEmail(String email) {
    this.currentUserEmail = email;
    System.out.println("Email set in salespersondash: " + this.currentUserEmail);
}
    
    private void navigateToPointOfSale(String email) {
    pointofsale posFrame = new pointofsale();
    posFrame.setCurrentUserEmail(email); // Pass the email directly
    System.out.println("Passing Email to pointofsale: " + email);
    posFrame.setVisible(true);
    this.setVisible(false); // Or this.dispose();
}







    // Optionally, a setter for password if needed
    public void setCurrentPassword(String password) {
        //this.currentPassword = password; // Use with caution
    }


    public salespersondash() {
        initComponents();
    checkBirthdays();
    


    NotificationScheduler scheduler = new NotificationScheduler();
    scheduler.startScheduledTask();

    
        
    }
    
    public void verifySalespersonLogin(String username, String password) {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos", "root", "password");
        String sql = "SELECT email FROM salesperson WHERE email = ? AND password = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            String salespersonEmail = rs.getString("email");
            pointofsale posFrame = new pointofsale();
            posFrame.setCurrentUserEmail(salespersonEmail);
            posFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
    public class NotificationScheduler {

    private ScheduledExecutorService scheduler;

    public NotificationScheduler() {
        // Initialize the scheduler with 1 thread
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startScheduledTask() {
// Define times when you want the task to run
        LocalTime[] notificationTimes = {
            LocalTime.of(9, 0), // 9:00 AM
            LocalTime.of(12, 0), // 12:00 PM
            LocalTime.of(15, 0)  // 3:00 PM
        };

        // Define the task to be scheduled
        Runnable task = () -> {
            System.out.println("Notification sent at: " + LocalTime.now());
            // Your notification logic here
        };

        // Schedule the task for each specified time
        for (LocalTime notificationTime : notificationTimes) {
            // Calculate the delay until the next occurrence of the specified time
            long initialDelay = Duration.between(LocalTime.now(), notificationTime).toMillis();

            // If the time is in the past, add 24 hours to schedule it for the next day
            if (initialDelay < 0) {
                initialDelay += TimeUnit.DAYS.toMillis(1);
            }

            // Schedule the task to run daily at the specified time
            scheduler.scheduleAtFixedRate(task, initialDelay, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
        }
    }

    
    public void sendnotification(String message, String type) {
    // Print the message to the console for now
    System.out.println(message);

    // Setup mail server properties
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    // Email credentials
    String senderEmail = "oyinkanogunlabi@gmail.com";
    String senderPassword = "oefc ahng mlsm xulr";

    // Create a session with the SMTP server
    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(senderEmail, senderPassword);
        }
    });

    try {
        // Create a new email message
        Message emailMessage = new MimeMessage(session);
        emailMessage.setFrom(new InternetAddress(senderEmail));

        // Set the recipient email address
        // TODO: Replace "recipientEmailAddress" with the actual recipient's email address
        String recipientEmailAddress = currentUserEmail;
        emailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmailAddress));

        // Customize the subject and content based on the notification type
        if ("lowStock".equals(type)) {
            emailMessage.setSubject("Low Stock Alert");
            emailMessage.setText("Low Stock Notification: " + message);
            JOptionPane.showMessageDialog(null, message, "Notification", JOptionPane.INFORMATION_MESSAGE);
    
        } else if ("expired".equals(type)) {
            emailMessage.setSubject("Expired Goods Alert");
            emailMessage.setText("Expiry Notification: " + message);
            JOptionPane.showMessageDialog(null, message, "Notification", JOptionPane.INFORMATION_MESSAGE);
    
        }

        // Send the email
        Transport.send(emailMessage);
        System.out.println("Email notification sent successfully.");
        
    } catch (MessagingException e) {
        e.printStackTrace();
        System.out.println("Failed to send email notification.");
    }
}



public void checkAndNotifyLowStock() {
    // List to hold names of low stock items
    List<String> lowStockItems = new ArrayList<>();

    // Database query to find low stock items
    String sql = "SELECT name, stockquantity FROM inventory WHERE stockquantity <= lowstock";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos", "root", "password");
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            String itemName = rs.getString("name");
            int stockQuantity = rs.getInt("stockquantity");
            
            // Add item name and stock quantity to the list
            lowStockItems.add(itemName + " (Qty: " + stockQuantity + ")");
        }

        // If there are any low stock items, show a message using JOptionPane
        if (!lowStockItems.isEmpty()) {
            String message = "The following items are low in stock:\n" + String.join("\n", lowStockItems);
            // Display the message in a JOptionPane dialog
            JOptionPane.showMessageDialog(null, message, "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}




    public void checkAndNotifyExpirySoon() {
    // List to hold names of items expiring soon
    List<String> expiringSoonItems = new ArrayList<>();

    // Database query to retrieve all items
    String sql = "SELECT name, expirydate FROM inventory";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos", "root", "password");
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy"); // Format of your expirydate string
        Calendar oneMonthAhead = Calendar.getInstance();
        oneMonthAhead.add(Calendar.MONTH, 1); // Set to one month ahead of the current date

        while (rs.next()) {
            String itemName = rs.getString("name");
            String expiryDateString = rs.getString("expirydate");
            
            try {
                Date expiryDate = sdf.parse(expiryDateString); // Parse the expirydate string into a Date object
                if (expiryDate.before(oneMonthAhead.getTime())) {
                    // If the expiry date is before one month from now, add it to the list
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy"); // Format for displaying expiry dates
                    expiringSoonItems.add(itemName + " (Expiry Date: " + outputFormat.format(expiryDate) + ")");
                }
            } catch (ParseException e) {
                System.err.println("Error parsing expiry date for item " + itemName);
            }
        }

        // If there are any items expiring soon, send a consolidated notification
        if (!expiringSoonItems.isEmpty()) {
    String message = "The following items are expiring soon:\n" + String.join("\n", expiringSoonItems);
    // Display the message in a JOptionPane dialog
    JOptionPane.showMessageDialog(null, message, "Expiry Alert", JOptionPane.WARNING_MESSAGE);
}


    } catch (SQLException e) {
        e.printStackTrace();
    }
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
    
   
public List<String> checkLowStockItems() {
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
public void sendLowStockEmail(List<String> lowStockItems, String toEmail) {
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

    try {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(myAccountEmail));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        message.setSubject("Low Stock Alert");
        String emailText = "The following items are low in stock:\n" + String.join("\n", lowStockItems);
        message.setText(emailText);
        Transport.send(message);
    } catch (MessagingException e) {
        e.printStackTrace();
    }
}

    
    

 
    

    public void checkBirthdays() {
    String dbURL = "jdbc:mysql://localhost:3306/pos";
    String username = "root";
    String password = "password";
    // SQL query that unions the dob and firstname from all four tables
    String query = "SELECT firstname, dob FROM salesperson " +
                   "UNION SELECT firstname, dob FROM invmanager " +
                   "UNION SELECT firstname, dob FROM itadmin " +
                   "UNION SELECT firstname, dob FROM salesmanager";

    try (Connection con = DriverManager.getConnection(dbURL, username, password);
         PreparedStatement ps = con.prepareStatement(query)) {

        ResultSet rs = ps.executeQuery();

        // Get today's month and day for comparison
        Calendar today = Calendar.getInstance();
        int todayMonth = today.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        int todayDay = today.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

        while (rs.next()) {
            String name = rs.getString("firstname");
            String dobStr = rs.getString("dob"); // Birthdate in 'ddMMyyyy' format

            try {
                Date dob = sdf.parse(dobStr);
                Calendar dobCal = Calendar.getInstance();
                dobCal.setTime(dob);

                int dobMonth = dobCal.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
                int dobDay = dobCal.get(Calendar.DAY_OF_MONTH);

                // Check if today is the person's birthday
                if (todayMonth == dobMonth && todayDay == dobDay) {
                    // If it's someone's birthday, print a greeting
                    JOptionPane.showMessageDialog(null, "Happy Birthday to " + name + "!", "Birthday Greeting", JOptionPane.INFORMATION_MESSAGE);

                }

            } catch (ParseException e) {
                System.err.println("Error parsing date for " + name + " with DOB " + dobStr);
            }
        }
    } catch (SQLException e) {
        System.err.println("Database error: " + e.getMessage());
    
    }
    }
    public void setStaffEmailAndDisplay(String email) {
        displayStaffNameByEmail(email);
    }

    public void setStaffEmailAndDisplayID(String email) {
        displayStaffIDByEmail(email);
    }

    public void setStaffEmailAndDisplayPhoto(String email) throws SQLException {
        displayPassportPhotoByEmail(email);
    }

    private void displayPassportPhotoByEmail(String email) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Replace with your actual database connection details
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "password");
            String sql = "SELECT passportphoto FROM salesperson WHERE email = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                byte[] imgBytes = rs.getBytes("passportphoto");
                if (imgBytes != null) {
                    // Convert byte array into ImageIcon and set it to jLabel2
                    ImageIcon imgIcon = new ImageIcon(imgBytes);
                    // Resize the ImageIcon to fit jLabel2 if necessary
                    Image img = imgIcon.getImage().getScaledInstance(jLabel2.getWidth(), jLabel2.getHeight(), Image.SCALE_SMOOTH);
                    jLabel2.setIcon(new ImageIcon(img)); // Set the ImageIcon to jLabel2
                } else {
                    jLabel2.setIcon(null); // Clear jLabel2 if no image is found
                }
            } else {
                jLabel2.setIcon(null); // Clear jLabel2 if no record is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            jLabel2.setIcon(null); // Clear jLabel2 if there's an error
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void displayStaffIDByEmail(String email) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Replace with your actual database connection details
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "password");
            String sql = "SELECT staffid FROM salesperson WHERE email = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String staffID = rs.getString("staffid"); 
                jLabel14.setText(staffID); 
            } else {
                jLabel14.setText(""); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            jLabel14.setText(""); 
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // And then the displayStaffNameByEmail method after that
    private void displayStaffNameByEmail(String email) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Assume "YourDBURL", "YourUsername", "YourPassword" are your actual database details
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "password");
            String sql = "SELECT staffname FROM salesperson WHERE email = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String staffName = rs.getString("staffname");
                jLabel13.setText(staffName); // Directly using jLabel4 as it's the name of your JLabel
            } else {
                jLabel13.setText(""); // Leave blank if no staff name is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            jLabel13.setText(""); // Leave blank if there's an error
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel8 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 121, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 127, Short.MAX_VALUE)
        );

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        jLabel13.setText("Name");

        jButton11.setBackground(new java.awt.Color(242, 242, 242));
        jButton11.setFont(new java.awt.Font("Gabriola", 3, 18)); // NOI18N
        jButton11.setText("Need help with your account? Contact Admin");
        jButton11.setBorder(null);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jLabel14.setText("StaffID");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton1.setText("Clock In");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton11)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(297, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(104, 104, 104)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14)
                        .addGap(57, 57, 57))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)))
                .addComponent(jButton11)
                .addGap(47, 47, 47)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("My Profile", jPanel8);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jLabel4.setFont(new java.awt.Font("Segoe UI Semilight", 1, 36)); // NOI18N
        jLabel4.setText("Salesperson Dashboard");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 82, 82)
                        .addComponent(jLabel4))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        navigateToPointOfSale(this.currentUserEmail);
        // Call the method when the button is clicked
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        try {
            // Define the subject
            String subject = "Help needed with POS System account";

            // Construct the mailto URI with the recipient and the URL-encoded subject
            String mailto = "mailto:oyinkanogunlabi@gmail.com?subject=" +
            URLEncoder.encode(subject, StandardCharsets.UTF_8.toString()).replace("+", "%20");

            // Open the default mail client with the prepared mailto URI
            Desktop desktop = Desktop.getDesktop();
            if (desktop != null && desktop.isSupported(Desktop.Action.MAIL)) {
                URI mailURI = new URI(mailto);
                desktop.mail(mailURI);
            } else {
                // Handle the case where no mail client is available or the action is not supported
                JOptionPane.showMessageDialog(null, "No default mail client is configured or the action is not supported.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while opening the mail client: " + e.getMessage());
        }         // TODO add your handling code here:
    }//GEN-LAST:event_jButton11ActionPerformed

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
            java.util.logging.Logger.getLogger(salespersondash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(salespersondash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(salespersondash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(salespersondash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new salespersondash().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
