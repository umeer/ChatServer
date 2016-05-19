package Client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;

public class GUI_Client extends JFrame {

    private Comunication_Adapter adapter;
    private Component pop_upPanel = null;
    private JPanel disp = new JPanel(new BorderLayout());
    private JTextArea chatDisplay;
 
    GUI_Client() {
        drawGui();
    }

    private void drawGui() {
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Pannello Cliente");
        this.setBounds(900, 100, 400, 270);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                adapter.close_connection();
            }
        });

        this.add(disp, BorderLayout.CENTER);
        disp.add(fistPanel(), BorderLayout.CENTER);
    }

    private JPanel fistPanel() {
        JPanel display = new JPanel(new BorderLayout());

        JPanel nord = new JPanel(new GridLayout(0, 2, 20, 20));
        nord.add(new JLabel("IP Server:", SwingConstants.CENTER));
        final JTextField ip_server = new JTextField();
        ip_server.setText("localhost");
        nord.add(ip_server);

        nord.add(new JLabel("Port:", SwingConstants.CENTER));
        final JTextField port_server = new JTextField();
        port_server.setText("8888");
        nord.add(port_server);

        display.add(nord, BorderLayout.NORTH);

        JButton connect = new JButton("Start Connection");
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                adapter = new Comunication_Adapter(ip_server.getText(), Integer.parseInt(port_server.getText()));

                if (adapter.start_connection()) {
                    System.out.println("Connessione riuscita");
                    reDraw(loginPanel());
                } else {
                    System.out.println("Connessione fallita");
                    JOptionPane.showMessageDialog(pop_upPanel,
                            "Immpossibile collegarsi al server, provare a cambiare i dati.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        display.add(connect, BorderLayout.CENTER);

        return display;
    }

    private JPanel loginPanel() {

        JPanel display = new JPanel(new GridLayout(0, 2, 20, 20));
        display.add(new JLabel("User:", SwingConstants.CENTER));
        final JTextField user = new JTextField();
        display.add(user);

        display.add(new JLabel("Pass:", SwingConstants.CENTER));
        final JTextField pass = new JTextField();
        display.add(pass);

        JButton sign_up = new JButton("Sign Up");
        display.add(sign_up);
        JButton login = new JButton("Login");
        display.add(login);

        sign_up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                reDraw(sign_upPanel());
            }
        });

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (adapter.login(user.getText(), pass.getText())) {
                    reDraw(chatPanel());
                } else {
                    reDraw(loginPanel());
                    JOptionPane.showMessageDialog(pop_upPanel,
                            "Dati sbagliati.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return display;
    }

    private JPanel sign_upPanel() {

        JPanel display = new JPanel(new BorderLayout());

        JPanel nord = new JPanel(new GridLayout(0, 2, 20, 20));
        nord.add(new JLabel("User:", SwingConstants.CENTER));
        final JTextField user = new JTextField();
        nord.add(user);

        nord.add(new JLabel("Pass:", SwingConstants.CENTER));
        final JTextField pass = new JTextField();
        nord.add(pass);

        nord.add(new JLabel("Email:", SwingConstants.CENTER));
        final JTextField email = new JTextField();
        nord.add(email);

        display.add(nord, BorderLayout.NORTH);

        JButton register = new JButton("Sign Up");
        display.add(register, BorderLayout.SOUTH);

        register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (adapter.sign_up(user.getText(), pass.getText(), email.getText())) {
                    reDraw(loginPanel());
                    JOptionPane.showMessageDialog(pop_upPanel,
                            "Registrazione avvenuta con successo",
                            "Messaggio Successo",
                            JOptionPane.PLAIN_MESSAGE);
//                    adapter.close_connection();
                } else {
                    System.out.println("Connessione fallita");
                    JOptionPane.showMessageDialog(pop_upPanel,
                            "Immpossibile registrarsi.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return display;
    }

    private JPanel chatPanel() {
        JPanel display = new JPanel(new GridLayout(0, 1, 30, 30));

        chatDisplay = new JTextArea("Benvenuto nella chat", 5, 1);
        chatDisplay.setLineWrap(true);

        DefaultCaret caret = (DefaultCaret) chatDisplay.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scroll = new JScrollPane(chatDisplay);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        display.add(scroll);

        final JTextField message = new JTextField();
        display.add(message);

        JButton send = new JButton("Send Message");
        display.add(send);

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (adapter.send(message.getText())) {
                } else {
                    JOptionPane.showMessageDialog(pop_upPanel,
                            "Immpossibile inviare messaggio.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updaterThread().start();

        return display; 
    }

    private Thread updaterThread() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        chatDisplay.append(adapter.read());
                    } catch (IOException ex) {
                    }

                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException ex) {
                    }
                }

            }
        }
        );
        return t;
    }

    private void reDraw(final JPanel displayPanel) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                disp.removeAll();
                disp.add(displayPanel, BorderLayout.CENTER);
                disp.revalidate();
                disp.repaint();
            }
        });
    }

}
