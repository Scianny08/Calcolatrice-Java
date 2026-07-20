// A C ^ ÷
// 7 8 9 +
// 4 5 6 -
// 1 2 3 x
// , 0 % =

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.BorderFactory;

public class GUICalcolatrice implements ActionListener {

    //Attributi
    JFrame f = new JFrame();
    Container c = f.getContentPane();

    //nome calcolatrice
    JPanel pnlCalc = new JPanel();
    JLabel lblCalc = new JLabel("Calcolatrice");

    //schermo su cui verrà visualizzato il calcolo
    JPanel pnlSchermo = new JPanel();
    JTextField txtSchermo = new JTextField(17);

    //pulsanti calcolatrice
    JPanel pnlPulsanti = new JPanel(new GridLayout(5, 4, 5, 5));

    JButton btnA = new JButton("A");
    JButton btnC = new JButton("C");
    JButton btnPow = new JButton("^");
    JButton btnDiviso = new JButton("÷");

    JButton btn7 = new JButton("7");
    JButton btn8 = new JButton("8");
    JButton btn9 = new JButton("9");
    JButton btnPiu = new JButton("+");

    JButton btn4 = new JButton("4");
    JButton btn5 = new JButton("5");
    JButton btn6 = new JButton("6");
    JButton btnMeno = new JButton("-");

    JButton btn1 = new JButton("1");
    JButton btn2 = new JButton("2");
    JButton btn3 = new JButton("3");
    JButton btnPer = new JButton("x");

    JButton btnVirgola = new JButton(",");
    JButton btn0 = new JButton("0");
    JButton btnMod = new JButton("%");
    JButton btnUguale = new JButton("=");

    //Costruttore
    public GUICalcolatrice(){
        // aggiungo gli elementi atomici
        txtSchermo.setOpaque(true);
        txtSchermo.setEditable(true);
        txtSchermo.setBackground(new Color(0, 225, 127));
        txtSchermo.setFont(new Font("Consolas", Font.BOLD, 16));
        txtSchermo.setHorizontalAlignment(JTextField.RIGHT); //scrittura da destra
        txtSchermo.setPreferredSize(new Dimension(0, 35));

        pnlSchermo.setLayout(new BorderLayout());
        pnlSchermo.add(txtSchermo, BorderLayout.NORTH);

        lblCalc.setOpaque(true);
        pnlCalc.add(lblCalc);

        // Aggiungi i bottoni direttamente al pannello (l'ordine crea la griglia automatica)
        pnlPulsanti.add(btnA);
        pnlPulsanti.add(btnC);
        pnlPulsanti.add(btnPow);
        pnlPulsanti.add(btnDiviso);

        pnlPulsanti.add(btn7);
        pnlPulsanti.add(btn8);
        pnlPulsanti.add(btn9);
        pnlPulsanti.add(btnPiu);

        pnlPulsanti.add(btn4);
        pnlPulsanti.add(btn5);
        pnlPulsanti.add(btn6);
        pnlPulsanti.add(btnMeno);

        pnlPulsanti.add(btn1);
        pnlPulsanti.add(btn2);
        pnlPulsanti.add(btn3);
        pnlPulsanti.add(btnPer);

        pnlPulsanti.add(btnVirgola);
        pnlPulsanti.add(btn0);
        pnlPulsanti.add(btnMod);
        pnlPulsanti.add(btnUguale);

        // Distribuisco i macro-pannelli nella finestra
        c.setLayout(new BorderLayout());
        ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        c.add(pnlCalc, BorderLayout.NORTH);
        c.add(pnlSchermo, BorderLayout.CENTER);
        c.add(pnlPulsanti, BorderLayout.SOUTH);

        // Collego l'action listener
        txtSchermo.addActionListener(this); //per pressione invio
        for (Component comp : pnlPulsanti.getComponents()) {
            if (comp instanceof JButton) {
                ((JButton) comp).addActionListener(this);
            }
        }

        f.setMinimumSize(new Dimension(300, 325));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);
        f.pack();
        f.setVisible(true);
    } // fine costruttore

    /*
     * L'unico metodo astratto dell'interfaccia
     * ActionListener è actionPerformed(ActionEvent e)
     */
    public void actionPerformed(ActionEvent e) {
        Object sorgente = e.getSource();
        String testo = txtSchermo.getText();

        // 1. GESTIONE TASTO "A" (Reset)
        if (sorgente.equals(btnA)) {
            txtSchermo.setText("");
        }

        // 2. GESTIONE TASTO "C" (Cancella ultimo)
        else if (sorgente.equals(btnC)) {
            if (testo != null && !testo.isEmpty() && !testo.equals("Errore")) {
                txtSchermo.setText(testo.substring(0, testo.length() - 1));
            }
        }

        // 3. GESTIONE TASTO "UGUALE" (Elaborazione del calcolo)
        else if (sorgente.equals(btnUguale) || sorgente.equals(txtSchermo)) {
            // Esegue il calcolo solo se lo schermo non è vuoto o in stato di errore
            if (!testo.isEmpty() && !testo.equals("Errore")) {

                try {
                    //replacement degli operatori
                    testo = testo.replace(",", ".");
                    testo = testo.replace("x", "*");
                    testo = testo.replace("÷", "/");

                    Calcolatrice calc = new Calcolatrice(testo);

                    String output = calc.risultato();

                    if (output.equals("Errore")) {
                        txtSchermo.setText("Errore");
                    } else {
                        // Rimozione del .0 per i numeri interi
                        if (output.endsWith(".0")) {
                            output = output.substring(0, output.length() - 2);
                        }
                    }
                    output = output.replace(".", ",");

                    txtSchermo.setText(output);

                } catch (Exception ex) {
                    txtSchermo.setText("Errore");
                }
            }
        }

        // 4. SCRITTURA STANDARD (Tasti numerici e operatori)
        else {
            // Se sul display c'è un errore, pulisce prima di scrivere il nuovo carattere
            if (testo.equals("Errore")) {
                testo = "";
            }

            JButton bottonePremuto = (JButton) sorgente;
            txtSchermo.setText(testo + bottonePremuto.getText());
        }
    } // fine metodo

} // fine classe
