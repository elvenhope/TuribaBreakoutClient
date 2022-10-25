package game;

import Packets.AddConnectionPacket;
import sockets.Client;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Breakout extends JFrame {

    public JPanel MainMenu;
    private JTextField ipInput;
    private JTextField portInput;
    private JLabel ipText = new JLabel("Enter IP:");
    private JLabel portText = new JLabel("Enter Port:");
    private JLabel ErrorText = new JLabel("");
    private JLabel WaitingText = new JLabel("Connected and Waiting for Others....");
    protected String IP = "10.2.3.147";
    protected int Port = 3001;
    public static Client client;
    public static Board board;
    public static Scoreboard scoreboard;

    public static Breakout game;
    public Breakout() {
        initUI();
    }

    private void initUI() {
        //SET TITLE AND FRAME SIZE
        setTitle("Breakout");

        //START INITIALIZING THE MAIN MENU
        MainMenu = new JPanel();
        MainMenu.setBackground(Color.ORANGE);
        MainMenu.setPreferredSize(new Dimension(Commons.WIDTH, Commons.HEIGHT));
        MainMenu.setVisible(true);

        //Define the Layout
        GridBagLayout layout = new GridBagLayout();
        MainMenu.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //DEFINE THE IP INPUT TEXT FIELD
        ipInput = new JTextField();

        //DEFINE THE PORT INPUT TEXT FIELD
        portInput = new JTextField();

        //DEFINE THE JOIN BUTTON
        JButton Join = new JButton("Join");
        Join.addActionListener(e -> changePanels());

        //Add ErrorText
        gbc.fill = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        ErrorText.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        MainMenu.add(ErrorText, gbc);

        //Add ipText
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        MainMenu.add(ipText, gbc);

        //Add ipInput
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 2;
        MainMenu.add(ipInput, gbc);

        //Add portText
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 3;
        MainMenu.add(portText, gbc);

        //Add portInput
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 4;
        MainMenu.add(portInput, gbc);

        //add Join Button
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 5;
        MainMenu.add(Join, gbc);

        //add WaitingText
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 6;
        WaitingText.setVisible(false);
        MainMenu.add(WaitingText, gbc);

        //Add the JPanel to the JFrame
        add(MainMenu);

        //DEFAULT CONFIGURATIONS
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        pack();
    }

    private static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    private static final Pattern IPv4_PATTERN = Pattern.compile(IPV4_REGEX);

    public static boolean isValidInet4Address(String ip) {
        if (ip == null) {
            return false;
        }

        Matcher matcher = IPv4_PATTERN.matcher(ip);

        return matcher.matches();
    }

    protected boolean CheckInput(String Input) {
        if(!Input.isEmpty()) {
            if (Input.matches("^[0-9]*$")) {
                if(Integer.parseInt(Input) > 3000 && Integer.parseInt(Input) < 65535) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected void Connect() {
        client = new Client(IP,Port);
        client.connect();

        AddConnectionPacket packet = new AddConnectionPacket();
        client.sendObject(packet);
        WaitingText.setVisible(true);

//        board = new Board();
//        scoreboard = new Scoreboard();
//        add(board, BorderLayout.CENTER);
//        add(scoreboard, BorderLayout.EAST);
//        MainMenu.setVisible(false);
//        board.updateState();
    }

    private void changePanels() {
//        add(new Board());
//        MainMenu.setVisible(false);
//        ipText.setText(ipInput.getText());
        if(IP != null && Port != 0) {
            Connect();
        } else {
            if(CheckInput(portInput.getText())) {
                Port = Integer.parseInt(portInput.getText());
                if(isValidInet4Address(ipInput.getText())) {
                    IP = ipInput.getText();
                    Connect();
                } else {
                    ErrorText.setText("IP Number is Not Valid");
                }
            } else {
                ErrorText.setText("<html><body>Port Number is Incorrect<br>Enter an Integer Between 3000 and 65535</body></html>");
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            game = new Breakout();
            game.setVisible(true);
        });
    }
}
