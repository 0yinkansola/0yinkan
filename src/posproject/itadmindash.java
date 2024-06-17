/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package posproject;

import com.sun.mail.imap.protocol.ID;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.security.SecureRandom;
import java.io.File;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
/**
 *
 * @author LENOVO
 */
public class itadmindash extends javax.swing.JFrame {
     static byte photo[]=null;
     private String currentUserEmail;
     public void setCurrentUserEmail(String email) {
        this.currentUserEmail = email; // Set the email passed from itadminlogin
    }
     
     

     
private void backupDatabase() {
    try {
        String dbName = "pos";
        String dbUser = "root";
        String dbPass = "password";

        // Define the backup path. Change the path as needed.
        String backupPath = "C:\\Users\\LENOVO\\OneDrive\\Documents\\dbbackup";

        // MySQL dump command
        String command = "mysqldump -u" + dbUser + " -p" + dbPass + " " + dbName + " -r " + backupPath;

        // Execute the command
        Process runtimeProcess = Runtime.getRuntime().exec(command);

        int processComplete = runtimeProcess.waitFor();

        // Check the process execution status
        if (processComplete == 0) {
            JOptionPane.showMessageDialog(null, "Backup created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Could not create the backup", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (IOException | InterruptedException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error executing backup: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}




    public itadmindash() {
        initComponents();
        cameraInstance = new Camera();
        checkBirthdays();
        
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
                    JOptionPane.showMessageDialog(rootPane, "Happy Birthday to " + name + "!");
                    
                }

            } catch (ParseException e) {
                System.err.println("Error parsing date for " + name + " with DOB " + dobStr);
            }
        }
    } catch (SQLException e) {
        System.err.println("Database error: " + e.getMessage());
    }
}
    

    public String generateStaffID() {
        // Simple example to generate a staffID. Consider a more robust method for real applications.
        long timePart = System.currentTimeMillis();
        int randomPart = (int) (Math.random() * 10000);
        return "STF" + timePart + randomPart; // Prefix with STF for staff
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
            String sql = "SELECT passportphoto FROM itadmin WHERE email = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                byte[] imgBytes = rs.getBytes("passportphoto");
                if (imgBytes != null) {
                    // Convert byte array into ImageIcon and set it to jLabel2
                    ImageIcon imgIcon = new ImageIcon(imgBytes);
                    // Resize the ImageIcon to fit jLabel2 if necessary
                    Image img = imgIcon.getImage().getScaledInstance(jLabel1.getWidth(), jLabel1.getHeight(), Image.SCALE_SMOOTH);
                    jLabel1.setIcon(new ImageIcon(img)); // Set the ImageIcon to jLabel2
                } else {
                    jLabel1.setIcon(null); // Clear jLabel2 if no image is found
                }
            } else {
                jLabel1.setIcon(null); // Clear jLabel2 if no record is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            jLabel1.setIcon(null); // Clear jLabel2 if there's an error
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
    String password = generateRandomPassword(7);
    private Camera cameraInstance;

    public class Camera extends JFrame {

        public Camera() {
            super("Camera Window"); // Set window title
            setSize(300, 200); // Set window size
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Set default close operation
            // Initialize your camera components here
        }

        // Method to capture or retrieve a photo
        public BufferedImage capturePhoto() {
            // Your logic to capture a photo goes here
            BufferedImage image = null;
            // Capture and return the BufferedImage
            return image;
        }
    }

    public String generateRandomPassword(int length) {
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charSet.length());
            password.append(charSet.charAt(randomIndex));
        }

        return password.toString();
    }

    String staffID = generateStaffID();

    public void sendmailafteregistration() {
        String firstname = jTextField3.getText();
        String lastname = jTextField2.getText();
        String email = jTextField4.getText();
        String phone = jTextField1.getText();
        String dob = jTextField5.getText();
        try {
            String senderEmail = "oyinkanogunlabi@gmail.com";
            String senderPassword = "oefc ahng mlsm xulr";
            Properties props = new Properties();
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            }
            );
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Welcome to Our Team!");
            message.setText(
                    "        Hello " + firstname + "!\n"
                    + "        Here are your login details. \n"
                    + "          Your email:  " + email + "\n "
                    + "        Your password: " + password + "\n"
                    + "        Your Staff ID is: " + staffID + "\n\n"
                    + "        This is an auto-generated email. Do not reply.\n"
            );
            Transport.send(message);
            JOptionPane.showMessageDialog(rootPane, "Email sent");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }

    }

    public void sendmailafterchange() {
        String firstname = jTextField14.getText();
        String email = jTextField15.getText();

        try {
            String senderEmail = "oyinkanogunlabi@gmail.com";
            String senderPassword = "oefc ahng mlsm xulr";
            Properties props = new Properties();
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            }
            );
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Password Reset");
            message.setText(
                    "        Hello " + firstname + "!\n"
                    + "        Here are your login details. \n"
                    + "        Your password: " + password + "\n"
                    + "        This is an auto-generated email. Do not reply.\n"
            );
            Transport.send(message);
            JOptionPane.showMessageDialog(rootPane, "Email sent");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }

    }

    public BufferedImage capturePhoto() {
        // Logic to capture photo from the camera
        // This is just a placeholder; actual implementation will depend on how you're interfacing with the camera
        BufferedImage image = null;
        // Capture and return the BufferedImage
        return image;
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
        jLabel11 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jTextField9 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel12 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jPanel8 = new javax.swing.JPanel();
        jButton12 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jTextField14 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel13.setText("Name");

        jLabel14.setText("StaffID");

        jButton10.setBackground(new java.awt.Color(242, 242, 242));
        jButton10.setFont(new java.awt.Font("Gabriola", 3, 18)); // NOI18N
        jButton10.setText("Need help with your account? Contact Admin");
        jButton10.setBorder(null);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(230, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(128, 128, 128)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(167, 167, 167)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14)))
                .addGap(32, 32, 32)
                .addComponent(jButton10)
                .addContainerGap(108, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("My Profile", jPanel5);

        jButton4.setText("Search");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton7.setText("Web Cam");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel15.setText("Last Name");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton5.setText("Clear");

        jButton8.setBackground(new java.awt.Color(242, 242, 242));
        jButton8.setText("Browse");
        jButton8.setToolTipText("");
        jButton8.setBorder(null);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel17.setText("Phone");

        jLabel18.setText("First Name");

        jButton9.setText("Insert");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jLabel20.setText("Passport Photograph");

        jLabel21.setText("E-mail");

        jTextField9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField9ActionPerformed(evt);
            }
        });

        jLabel22.setText("Date of Birth");

        jLabel25.setText("Insert StaffID");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel25)
                .addGap(44, 44, 44)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(jButton5))
                    .addComponent(jTextField11))
                .addGap(18, 18, 18)
                .addComponent(jButton4)
                .addGap(145, 145, 145))
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(26, 26, 26)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jLabel18)
                            .addGap(461, 461, 461))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel17)
                                .addComponent(jLabel15)
                                .addComponent(jLabel21)
                                .addComponent(jLabel22))
                            .addGap(43, 43, 43)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel6Layout.createSequentialGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                            .addComponent(jButton7)
                                            .addGap(17, 17, 17))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jSeparator2)
                                                .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
                                            .addGap(35, 35, 35))))
                                .addGroup(jPanel6Layout.createSequentialGroup()
                                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel6Layout.createSequentialGroup()
                                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(jTextField7)
                                                .addComponent(jTextField9)
                                                .addComponent(jTextField6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(jPanel6Layout.createSequentialGroup()
                                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jTextField8, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jTextField10))
                                            .addGap(93, 93, 93)))
                                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel6Layout.createSequentialGroup()
                                            .addComponent(jLabel20)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGap(27, 27, 27)))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(286, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton9))
                .addGap(31, 31, 31)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4))
                .addGap(45, 45, 45))
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(29, 29, 29)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel18)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel20))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel15)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel17)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(15, 15, 15)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel21)
                                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel22)
                                .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGap(46, 46, 46)
                    .addComponent(jButton7)
                    .addGap(18, 18, 18)
                    .addComponent(jButton8)
                    .addGap(7, 7, 7)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(69, 69, 69)))
        );

        jTabbedPane1.addTab("Manage Staff", jPanel6);

        jLabel2.setText("Last Name");

        jButton3.setText("Clear");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel3.setText("Phone");

        jLabel4.setText("Department");

        jLabel5.setText("E-mail");

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jLabel6.setText("Sex");

        jButton6.setText("Web Cam");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "IT Department", "Salesperson", "Sales Manager", "Inventory Manager" }));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton1.setBackground(new java.awt.Color(242, 242, 242));
        jButton1.setText("Browse");
        jButton1.setToolTipText("");
        jButton1.setBorder(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel8.setText("First Name");

        jButton2.setText("Insert");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel9.setText("Passport Photograph");

        jLabel12.setText("Date of Birth");

        jRadioButton1.setText("Male");

        jRadioButton2.setText("Female");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addContainerGap(469, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5)
                            .addComponent(jLabel12))
                        .addGap(43, 43, 43)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jButton2)
                                        .addGap(29, 29, 29)
                                        .addComponent(jButton3))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jRadioButton2)
                                        .addGap(49, 49, 49)
                                        .addComponent(jRadioButton1)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                        .addComponent(jButton6)
                                        .addGap(52, 52, 52))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jSeparator1)
                                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
                                        .addGap(70, 70, 70))))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jTextField1)
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.Alignment.LEADING, 0, 173, Short.MAX_VALUE)
                                    .addComponent(jTextField2)
                                    .addComponent(jTextField3)
                                    .addComponent(jTextField4, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(35, 35, 35))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addContainerGap())))))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jButton6))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jRadioButton1)
                                .addComponent(jRadioButton2))
                            .addComponent(jLabel6))))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(7, 7, 7)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2)
                            .addComponent(jButton3))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab("Register Staff", jPanel7);

        jPanel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jButton12.setText("Backup Database");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(215, 215, 215)
                .addComponent(jButton12)
                .addContainerGap(207, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(150, 150, 150)
                .addComponent(jButton12)
                .addContainerGap(233, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Backup Database", jPanel8);

        jLabel24.setText("Last Name");

        jButton11.setText("Reset Password");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jLabel27.setText("First Name");

        jTextField14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField14ActionPerformed(evt);
            }
        });

        jLabel26.setText("Staff ID");

        jLabel28.setText("Email");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(153, 153, 153)
                .addComponent(jButton11)
                .addGap(0, 279, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(jLabel26)
                            .addComponent(jLabel28))
                        .addGap(54, 54, 54)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .addComponent(jTextField14, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField13)
                            .addComponent(jTextField15))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addComponent(jButton11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Reset Staff Password", jPanel3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jLabel10.setFont(new java.awt.Font("Segoe UI Semilight", 1, 36)); // NOI18N
        jLabel10.setText("IT Administrator Dashboard");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jLabel10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)))
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        WebCamera newCapture = new WebCamera();
        newCapture.show();
        newCapture.setVisible(true);
        System.out.println("i am webcam issue");
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

// Inside the actionPerformed method of jButton1
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home"))); // Start at the user's home directory
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes())); // Filter to only show image files

        int result = fileChooser.showOpenDialog(null); // Show the dialog; wait until it is closed

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                ImageIcon imageIcon = new ImageIcon(ImageIO.read(selectedFile)); // Load the image as an ImageIcon

                // Scale the image to fit jLabel7
                Image image = imageIcon.getImage().getScaledInstance(jLabel7.getWidth(), jLabel7.getHeight(), Image.SCALE_SMOOTH);
                jLabel7.setIcon(new ImageIcon(image)); // Set the scaled image as the icon of jLabel7
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.out.println("i am browse issue");
                // Handle the exception, e.g., by showing an error dialog
            }
        }

//        jFileChooser1.showOpenDialog(null);
//        File f=jFileChooser1.getSelectedFile();
//        String path=f.getAbsolutePath();
//        Image im=Toolkit.getDefaultToolkit().createImage(path);
//        im=im.getScaledInstance(jLabel7.getWidth(), jLabel7.getHeight(), Image.SCALE_SMOOTH);
//        ImageIcon ic=new ImageIcon(im);
//        jLabel7.setIcon(ic);// TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String firstname = jTextField3.getText();
        String lastname = jTextField2.getText();
        String email = jTextField4.getText();
        String phone = jTextField1.getText();
        String dob = jTextField5.getText();
        String sex = null;
        if (jRadioButton1.isSelected()) {
            sex = "Male";
        } else if (jRadioButton2.isSelected()) {
            sex = "Female";
        }

        // Assume jLabel7 has an ImageIcon from which we extract the image
    ImageIcon passportPhotoIcon = (ImageIcon) jLabel7.getIcon();
    Image image = passportPhotoIcon.getImage();
    BufferedImage bImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
    Graphics2D bImageGraphics = bImage.createGraphics();
    bImageGraphics.drawImage(image, 0, 0, null);
    bImageGraphics.dispose();

    // Convert BufferedImage to byte array
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
        ImageIO.write(bImage, "jpg", baos);
    } catch (IOException e) {
        JOptionPane.showMessageDialog(rootPane, "Error in image processing: " + e.getMessage());
        return;
    }
    byte[] photoBytes = baos.toByteArray();
        try {
               System.out.println("Password before hashing: " + password);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            String hashedpassword = sb.toString();

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos", "root", "password");
            String department = jComboBox2.getSelectedItem().toString();
        String sql = "";
        switch (department) {
            case "Salesperson":
                sql = "INSERT INTO salesperson (staffID, phoneno, firstname, lastname, email, sex, password, dob, passportphoto) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                break;
            case "IT Department":
                sql = "INSERT INTO itadmin (staffID, phoneno, firstname, lastname, email, sex, password, dob, passportphoto) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                break;
            case "Sales Manager":
                sql = "INSERT INTO salesmanager (staffID, phoneno, firstname, lastname, email, sex, password, dob, passportphoto) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                break;
            case "Inventory Manager":
                sql = "INSERT INTO invmanager (staffID, phoneno, firstname, lastname, email, sex, password, dob, passportphoto) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                break;
            default:
                JOptionPane.showMessageDialog(rootPane, "Invalid Department Selection");
                return;
        }

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, staffID);
        ps.setString(2, phone);
        ps.setString(3, firstname);
        ps.setString(4, lastname);
        ps.setString(5, email);
        ps.setString(6, sex);
        ps.setString(7, hashedpassword);
        ps.setString(8, dob);
        ps.setBytes(9, photoBytes);

            // Convert the BufferedImage to a byte array
            int rs = ps.executeUpdate();
            sendmailafteregistration();

            JOptionPane.showMessageDialog(rootPane, firstname + " records have been successfully inserted");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }


    }//GEN-LAST:event_jButton2ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // Get staffID from jTextField11
        String staffIdValue = jTextField11.getText();

        // Database connection details
        String dbURL = "jdbc:mysql://localhost:3306/pos";
        String username = "root";
        String password = "password";

        // SQL query to search for staffID in all tables
        String query = "SELECT 'salesperson' AS tableName, firstname, lastname, email, dob, phoneno, passportphoto FROM salesperson WHERE staffID = ? "
                + "UNION "
                + "SELECT 'salesmanager' AS tableName, firstname, lastname, email, dob, phoneno, passportphoto FROM salesmanager WHERE staffID = ? "
                + "UNION "
                + "SELECT 'itadmin' AS tableName, firstname, lastname, email, dob, phoneno, passportphoto FROM itadmin WHERE staffID = ? "
                + "UNION "
                + "SELECT 'invmanager' AS tableName, firstname, lastname, email, dob, phoneno, passportphoto FROM invmanager WHERE staffID = ?";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos", "root", "password"); PreparedStatement ps = con.prepareStatement(query)) {

            // Set the staffID value for each part of the UNION query
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, staffIdValue);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String tableName = rs.getString("tableName");

                    // Now, you can insert data into the appropriate table
                    String insertQuery = "";

                    switch (tableName) {
                        case "salesperson":
                            insertQuery = "INSERT INTO salesperson (firstname, lastname, email, dob, phoneno, passportphoto) VALUES (?, ?, ?, ?, ?, ?)";
                            break;
                        case "salesmanager":
                            insertQuery = "INSERT INTO salesmanager (firstname, lastname, email, dob, phoneno, passportphoto) VALUES (?, ?, ?, ?, ?, ?)";
                            break;
                        case "itadmin":
                            insertQuery = "INSERT INTO itadmin (firstname, lastname, email, dob, phoneno, passportphoto) VALUES (?, ?, ?, ?, ?, ?)";
                            break;
                        case "invmanager":
                            insertQuery = "INSERT INTO invmanager (firstname, lastname, email, dob, phoneno, passportphoto) VALUES (?, ?, ?, ?, ?, ?)";
                            break;
                    }

                    
                    PreparedStatement insertPs = con.prepareStatement(insertQuery);

                    
                    jTextField9.setText(rs.getString("firstname"));
                    jTextField7.setText(rs.getString("lastname"));
                    jTextField8.setText(rs.getString("email"));
                    jTextField10.setText(rs.getString("dob"));
                    jTextField6.setText(rs.getString("phoneno"));

                    
                    byte[] imgBytes = rs.getBytes("passportphoto");
                    if (imgBytes != null) {
                        
                        ImageIcon imageIcon = new ImageIcon(imgBytes);
                        
                        Image img = imageIcon.getImage().getScaledInstance(jLabel16.getWidth(), jLabel16.getHeight(), Image.SCALE_SMOOTH);
                        ImageIcon resizedIcon = new ImageIcon(img);

                        
                        jLabel16.setIcon(resizedIcon);
                    } else {
                        jLabel16.setText("No Image");
                    }

                    int result = insertPs.executeUpdate();
                } else {
                    JOptionPane.showMessageDialog(null, "Staff ID not found in any table.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while accessing the database: " + e.getMessage());
        }


    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        WebCamera newCapture = new WebCamera();
        newCapture.show();
        newCapture.setVisible(true);
        System.out.println("i am webcam issue");
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
    String staffID = jTextField11.getText();
    String firstname = jTextField9.getText();
    String lastname = jTextField7.getText();
    String email = jTextField8.getText();
    String phone = jTextField6.getText();
    String dob = jTextField10.getText();

    // Array of table names to update
    String[] tables = {"invmanager", "salesperson", "salesmanager", "itadmin"};

    Connection conn = null;
    PreparedStatement pstmt = null;
    int updateCount = 0;

    try {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos", "root", "password");

        for (String table : tables) {
            String sql = "UPDATE " + table + " SET firstname = ?, lastname = ?, email = ?, phoneno = ?, dob = ? WHERE staffid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, firstname);
            pstmt.setString(2, lastname);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setString(5, dob);
            pstmt.setString(6, staffID);

            updateCount += pstmt.executeUpdate();
        }

        if (updateCount > 0) {
            JOptionPane.showMessageDialog(null, "Staff information updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "No staff found with the given ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }        // TODO add your handling code here:
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField9ActionPerformed

    private void jTextField14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField14ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jTextField3.setText("");
        jTextField2.setText("");
        jTextField4.setText("");
        jTextField1.setText("");
        jTextField5.setText("");
        jRadioButton1.setSelected(false);
        jRadioButton2.setSelected(false);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        String firstname = jTextField14.getText();
        String lastname = jTextField12.getText();
        String staffid = jTextField13.getText();

        // Get staffID from jTextField11
        String staffIdValue = jTextField13.getText();

        // Database connection details
        String dbURL = "jdbc:mysql://localhost:3306/pos";
        String username = "root";
        String password = "password";

        // SQL query to search for staffID in all tables
        String query = "SELECT 'salesperson' AS tableName, firstname, lastname, email, dob, phoneno, passportphoto FROM salesperson WHERE staffID = ? "
                + "UNION "
                + "SELECT 'salesmanager' AS tableName, firstname, lastname, email, dob, phoneno, passportphoto FROM salesmanager WHERE staffID = ? "
                + "UNION "
                + "SELECT 'itadmin' AS tableName, firstname, lastname, email, dob, phoneno, passportphoto FROM itadmin WHERE staffID = ? "
                + "UNION "
                + "SELECT 'invmanager' AS tableName, firstname, lastname, email, dob, phoneno, passportphoto FROM invmanager WHERE staffID = ?";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos", "root", "password"); PreparedStatement ps = con.prepareStatement(query)) {

            // Set the staffID value for each part of the UNION query
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, staffIdValue);
            }

            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(password.getBytes());
                byte[] bytes = md.digest();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < bytes.length; i++) {
                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                }

                sendmailafterchange();

                JOptionPane.showMessageDialog(rootPane, firstname + " records have been successfully inserted");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(rootPane, e);
            }
            StringBuilder sb = new StringBuilder();
            String hashedpassword = sb.toString();

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String tableName = rs.getString("tableName");

                    // Now, you can insert the hashed password into the appropriate table
                    String insertQuery = "";

                    switch (tableName) {
                        case "salesperson":
                            insertQuery = "UPDATE salesperson SET password = ? WHERE staffID = ?";
                            break;
                        case "salesmanager":
                            insertQuery = "UPDATE salesmanager SET password = ? WHERE staffID = ?";
                            break;
                        case "itadmin":
                            insertQuery = "UPDATE itadmin SET password = ? WHERE staffID = ?";
                            break;
                        case "invmanager":
                            insertQuery = "UPDATE invmanager SET password = ? WHERE staffID = ?";
                            break;
                    }

                    // Create the PreparedStatement for updating the password
                    PreparedStatement updatePs = con.prepareStatement(insertQuery);

                    // Set the hashed password and staffID as parameters
                    updatePs.setString(1, hashedpassword);
                    updatePs.setString(2, staffIdValue);

                    // Execute the update query to set the hashed password
                    int result = updatePs.executeUpdate();

                    if (result > 0) {
                        JOptionPane.showMessageDialog(rootPane, firstname + " password has been updated successfully.");
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Error updating password for " + tableName + ".");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Staff ID not found in any table.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while accessing the database: " + e.getMessage());
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
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
    }        // TODO add your handling code here:
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
    backupDatabase();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton12ActionPerformed

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
            java.util.logging.Logger.getLogger(itadmindash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(itadmindash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(itadmindash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(itadmindash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new itadmindash().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    public static javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables

}
