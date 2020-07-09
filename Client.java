import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.Scanner;

public class Client extends Thread {

    private static BufferedWriter bufferedWriter;

    private static final int MAX_FILE_SIZE = 1000000;

    public static File folder;
    public static File NotepadHist;

    private static Socket socket;

    private static String directory = System.getProperty("user.dir");

    private static String MyComputerName = getComputerName();
    public static String DialogComputerName = "";
    private static String DialogIP = "";

    private static JFrame FileView = new JFrame("Choose a file");

    public static JTextPane chatMSG = new JTextPane();
    private static JScrollPane JPChatMSG = new JScrollPane(chatMSG);

    private static final int LOGIN_WIDTH = 400;
    private static final int LOGIN_HEIGHT = 200;

    private static final int CHAT_WIDTH = 800;
    private static final int CHAT_HEIGHT = 700;

    private static final int FILE_WIDTH = 700;
    private static final int FILE_HEIGHT = 600;

    private static String Name = "";

    public static String msgHist;

    private static ArrayList<String> ComputerName = new ArrayList<>();

    public static void main(String[] args) {
        new Client();
        new Thread(new NetChatConnection()).start();
    }

    public Client() {
        getComputerName();
        CheckLocalNetwork();
        new LogWindow();
    }

    class LogWindow {
        private JFrame LoginFrame = new JFrame("Login");

        public LogWindow() {
            LoginFrame.setLayout(new GridBagLayout());
            LoginFrame.setSize(LOGIN_WIDTH, LOGIN_HEIGHT);
            LoginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            LoginFrame.setVisible(true);
            LoginFrame.setLocationRelativeTo(null);

            JLabel InvitationText = new JLabel("Welcome to the local Network chat");
            LoginFrame.add(InvitationText, new GridBagConstraints(0, 0, 1, 1, 2.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

            JTextField userName = new JTextField("Enter your name here");
            LoginFrame.add(userName, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            userName.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (userName.getText().equals("Enter your name here"))
                        userName.setText("");
                }
            });

            JComboBox<String> ComputerList = new JComboBox<>();
            ComputerList.addItem("Choose PC to connect");
            for (String s : ComputerName) {
                ComputerList.addItem(s);
            }
            LoginFrame.add(ComputerList, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

            JButton LogButton = new JButton("Join chat");
            LoginFrame.add(LogButton, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

            LogButton.addActionListener(buttonClick -> {
                if (!Objects.equals(ComputerList.getSelectedItem(), "Choose PC to connect") && !Objects.equals(ComputerList.getSelectedItem(), "")) {
                    DialogComputerName = (String) ComputerList.getSelectedItem();
                    getIPFromName();
                    Name = userName.getText();
                    if (userName.getText().isBlank()) {
                        Name = Integer.toString((int) (Math.random() * 1000000));
                    }
                    try {
                        socket = new Socket(DialogIP, 8000);
                        folder = new File(directory + "\\" + DialogComputerName);
                        NotepadHist = new File(directory + "\\" + DialogComputerName + "\\ChatMesseges.txt");
                        if (!folder.exists()) {
                            folder.mkdir();
                        }
                        if (!NotepadHist.exists()) {
                            NotepadHist.createNewFile();
                        }
                        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        new ChatWindow();
                        LoginFrame.setVisible(false);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Couldn't connect to the computer you want. Probably it's not using tha app at the moment.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You are not connecting to the computer!", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            });
        }
    }

    class ChatWindow {

        private JFrame ChatFrame = new JFrame("Chat");

        public ChatWindow() {
            ChatFrame.setLayout(new GridBagLayout());
            ChatFrame.setSize(CHAT_WIDTH, CHAT_HEIGHT);
            ChatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ChatFrame.setVisible(true);
            ChatFrame.setLocationRelativeTo(null);

            JLabel SomeText = new JLabel("You are now logged in as: " + Name + ". You're connected to " + DialogComputerName);
            ChatFrame.add(SomeText, new GridBagConstraints(0, 0, 1, 1, 3, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

            JButton Logout = new JButton("Change chat");
            ChatFrame.add(Logout, new GridBagConstraints(1, 0, 1, 1, .25, .25, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            Logout.addActionListener(buttonClick -> {
                new LogWindow();
                try {
                    bufferedWriter.write("SUPERPUPERSECRETCODE\n");
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    socket.close();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Couldn't close current socket", "Error", JOptionPane.ERROR_MESSAGE);
                }
                ChatFrame.setVisible(false);
            });

            JButton refreshHistory = new JButton("Refresh history");
            ChatFrame.add(refreshHistory, new GridBagConstraints(2, 0, 1, 1, .25, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            refreshHistory.addActionListener(buttonClick -> UploadHistory());

            chatMSG.setEditable(false);
            ChatFrame.add(JPChatMSG, new GridBagConstraints(0, 1, 3, 1, 1.0, 100.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

            JTextField MSG = new JTextField(20);
            ChatFrame.add(MSG, new GridBagConstraints(0, 2, 1, 1, .5, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

            JButton SendButton = new JButton("Send");
            ChatFrame.add(SendButton, new GridBagConstraints(1, 2, 1, 1, .25, .25, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            SendButton.addActionListener(sendButtonClick -> {
                String msg = MSG.getText();
                MSG.setText(null);
                try {
                    GregorianCalendar gc = new GregorianCalendar();
                    String mssg = Name + "(" + DialogComputerName + ") [" + gc.getTime() + "] : " + msg;
                    if (!MyComputerName.equals(DialogComputerName)) {
                        updateMSGArea(mssg);
                    }
                    bufferedWriter.write(mssg + "\n");
                    bufferedWriter.flush();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Couldn't send messege", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JButton SendFile = new JButton("Send File");
            ChatFrame.add(SendFile, new GridBagConstraints(2, 2, 1, 1, .25, .25, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            SendFile.addActionListener(sendFileButtonClick -> new FileViewer());
        }
    }

    static class FileViewer {
        public FileViewer() {
            FileView.setSize(FILE_WIDTH, FILE_HEIGHT);
            FileView.setLayout(new GridBagLayout());
            FileView.setVisible(true);

            JFileChooser fileSelector = new JFileChooser();
            FileView.add(fileSelector, new GridBagConstraints(1, 0, 1, 1, .25, .25, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            fileSelector.addActionListener(buttonClick -> {
                File fileToSend = fileSelector.getSelectedFile();
                FileView.dispatchEvent(new WindowEvent(FileView, WindowEvent.WINDOW_CLOSING));
                char[] c = new char[MAX_FILE_SIZE];
                byte[] b = new byte[MAX_FILE_SIZE];
                try {
                    bufferedWriter.write("FILEISSENDINGWITHTHISNAME:" + fileToSend.getName() + "\n");
                    bufferedWriter.flush();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Couldn't send the selected file message", "Error", JOptionPane.ERROR_MESSAGE);
                }
                try {
                    FileInputStream fis = new FileInputStream(fileToSend.getPath());
                    try {
                        fis.read(b, 0, b.length);
                        //OutputStream os = socket.getOutputStream();
                        int i = 0;
                        while (i < b.length) {
                            c[i] = (char) b[i];
                            i++;
                        }
                        bufferedWriter.write(c);
                        bufferedWriter.flush();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Couldn't read the selected file", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(null, "Couldn't deal with the selected file", "Error", JOptionPane.ERROR_MESSAGE);
                }
                updateMSGArea("You have sent the file " + fileToSend.getName() + " to " + DialogComputerName);
            });
        }
    }

    static class NetChatConnection extends Thread {

        public NetChatConnection() {
            try (ServerSocket server = new ServerSocket(8000)) {
                while (true) {
                    Socket workSocket = server.accept();
                    while (true) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(workSocket.getInputStream()));
                        String msg = bufferedReader.readLine();
                        if (msg.startsWith("FILEISSENDINGWITHTHISNAME:")) {
                            String name = msg.substring(26);
                            byte[] b = new byte[MAX_FILE_SIZE];
                            char[] c = new char[MAX_FILE_SIZE];
                            try {
                                FileOutputStream fos = new FileOutputStream(Client.folder + "\\" + name);
                                bufferedReader.read(c);
                                int i = 0;
                                while (i < c.length) {
                                    b[i] = (byte) c[i];
                                    i++;
                                }
                                fos.write(b);
                                fos.flush();
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(null, "Problems with stream", "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                            msg = "\tYou have received a file " + name + " from " + Client.DialogComputerName;
                        }
                        if (msg.equals("SUPERPUPERSECRETCODE")) {
                            break;
                        }
                        Client.updateMSGArea(msg);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Couldn't make server", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void CheckLocalNetwork() {
        try {
            Process proc = Runtime.getRuntime().exec("net view");
            InputStream stdout = proc.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout, Charset.forName("cp866")));
            String line;
            while ((line = reader.readLine()) != null) {
                String name = "";
                if (!line.isBlank() && line.charAt(0) == '\\' && line.charAt(1) == '\\') {
                    int j = 2;
                    while (j < line.length() && line.charAt(j) != ' ') {
                        j++;
                    }
                    name = line.substring(2, j);
                }
                if (ComputerName.size() == 0 || !ComputerName.contains(name)) {
                    ComputerName.add(name);
                }
            }
            proc.destroy();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Couldn't get the list of computers in local network", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String getComputerName() {
        String MyName = "";
        try {
            MyName = InetAddress.getLocalHost().getHostName();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Couldn't get name of your computer", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        return MyName;
    }

    private static void getIPFromName() {
        try {
            Process proc = Runtime.getRuntime().exec("ping -n 1 " + DialogComputerName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream(), Charset.forName("cp866")));
            String Buffer;
            reader.readLine();
            Buffer = reader.readLine();
            proc.destroy();
            int i = 0;
            while (Buffer.charAt(i) != '[') {
                i++;
            }
            int j = i;
            while (Buffer.charAt(j) != ']') {
                j++;
            }
            DialogIP = Buffer.substring(i + 1, j);
            System.out.println(DialogIP);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Couldn't get IP from opponents computer. Make sure you can ping", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void updateMSGArea(String msg) {
        msgHist = msgHist + "\n" + msg;
        chatMSG.setText(msgHist);
        try (FileWriter writer = new FileWriter(NotepadHist, true)) {
            writer.write(msg + "\n");
            writer.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Problems with renewing the notepad file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void UploadHistory() {
        msgHist = "";
        try (FileReader reader = new FileReader(NotepadHist.getAbsolutePath())) {
            Scanner scanner = new Scanner(reader);
            while (scanner.hasNext()) {
                msgHist = msgHist.concat(scanner.nextLine());
                if (scanner.hasNext()) {
                    msgHist = msgHist.concat("\n");
                }
            }
            chatMSG.setText(msgHist);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Couldn't open the chat message history", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}




