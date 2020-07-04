import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class ClientWindow extends JFrame{

    JLabel leftLabel = new JLabel();
    JLabel rightLabel = new JLabel();
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(leftLabel), new JScrollPane(rightLabel));


    private static final int WIDTH = 285;
    private static final int HEIGHT = 400;

    private JPanel panelRadio;

    private JButton button = new JButton ("Выбрать чат");

    public static void main(String[] args) {
        NetAppChat nac = new NetAppChat();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }
    private ClientWindow() {
        Container container = this.getContentPane();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
        ArrayList<String> Computers = NetAppChat.getComputerNames();
        panelRadio = new JPanel(new GridLayout(0, 1, 1, 0));
        panelRadio.setBorder(BorderFactory.createTitledBorder("Компьютеры в локальной сети"));
        JScrollPane scrollPane = new JScrollPane();
        ButtonGroup bg1 = new ButtonGroup();
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < Computers.size(); i++) {
                if (!Computers.get(i).isBlank()) {
                    JRadioButton radio = new JRadioButton(Computers.get(i));
                    panelRadio.add(radio, BorderLayout.NORTH);
                    bg1.add(radio);
                }
            }
        }
        JPanel buttonPanel = new JPanel();
        button.addActionListener(new ButtonEventListener());
        buttonPanel.add(button);
        buttonPanel.setBounds(0, -100, WIDTH,50);
        scrollPane.setWheelScrollingEnabled(true);
        container.add(scrollPane.add(panelRadio), BorderLayout.NORTH);
        container.add(scrollPane.add(buttonPanel),BorderLayout.SOUTH);
    }
    class ButtonEventListener extends JFrame implements ActionListener  {

        private static final int MWIDTH = 500;
        private static final int MHEIGHT = 400;

        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame MessegeWindow = new JFrame();
            MessegeWindow.setSize(MWIDTH, MHEIGHT);
        }

    }
}
