import java.math.RoundingMode;
import java.util.Vector;
import java.math.BigDecimal;

public class Calcolatrice {
    protected Vector<String> termini; //vector per contenere le operazioni e i termini
    protected String espressione; //stringa data per calcolare il risultato
    protected char[] hop; //High Operator Precedence
    protected char[] lop; //Low Operator Precedence

    public Calcolatrice(String esp) {
        termini = new Vector<>();
        espressione = esp;
        hop = new char[] {'*', '/', '%', '^'};
        lop = new char[] {'+', '-'};
    }

    protected boolean isHop(char carattere) {
        for (char operatore : hop) {
            if (carattere == operatore) {
                return true;
            }
        }
        return false;
    }

    protected boolean isLop(char carattere) {
        for (char operatore : lop) {
            if (carattere == operatore) {
                return true;
            }
        }
        return false;
    }

    protected boolean isOperatore(char carattere) {
        return isHop(carattere) || isLop(carattere);
    }

    protected boolean haOperatori() {
        for (char carattere : espressione.toCharArray()) {
            if (isOperatore(carattere)) {
                return true;
            }
        }
        return false;
    }

    protected boolean sonoOperatoriValidi() {
        //prima controllo se la stringa ha operatori
        if (haOperatori()) {
            char primoChar = espressione.charAt(0);
            char ultimoChar = espressione.charAt(espressione.length()-1);

            if (!isOperatore(primoChar) && !isOperatore(ultimoChar)) {
                for (int i=1; i < espressione.length()-1; i++) {
                    char charPrec = espressione.charAt(i-1);
                    char charAttuale = espressione.charAt(i);
                    char charSucc = espressione.charAt(i+1);

                    //controllo per ogni operatore se alla sua dx e sx ha dei numeri
                    if (isOperatore(charAttuale)) {
                        if (!Character.isDigit(charPrec) || !Character.isDigit(charSucc)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    protected boolean haDecimali() {
        return espressione.contains(".");
    }

    protected boolean sonoDecimaliValidi() {
        //prima controllo se la stringa ha operatori
        if (haDecimali()) {
            char primoChar = espressione.charAt(0);
            char ultimoChar = espressione.charAt(espressione.length()-1);

            if (primoChar != '.' && ultimoChar != '.') {
                for (int i=1; i < espressione.length()-1; i++) {
                    char charPrec = espressione.charAt(i-1);
                    char charAttuale = espressione.charAt(i);
                    char charSucc = espressione.charAt(i+1);

                    //controllo per ogni punto se alla sua dx e sx ha dei numeri
                    //non sono validi i formati .5 .25 ecc.
                    if (charAttuale == '.') {
                        if (!Character.isDigit(charPrec) || !Character.isDigit(charSucc)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    //metodo che aggiunge al vector termini i termini numerici e gli operatori
    //es. se la stringa di partenza è "3.5*2+1"
    //il vector diventerà [3.5, *, 2, +, 1]
    protected void popolaTermini() {
        int i;
        boolean aggiuntoOperatore = false;

        if (sonoOperatoriValidi() && sonoDecimaliValidi()) {
            for (i=0; i < espressione.length(); i++) {
                char carattere = espressione.charAt(i);

                //se il carattere corrente è un operatore:
                //      lo aggiungo al vector
                //      imposto aggiuntoOperatore su true
                //altrimenti se termini è ancora vuoto
                //oppure ho aggiunto un operatore
                //      devo aggiungere un carattere numerico
                //se entrambe le condizioni sono false allora
                //il numero è composto da più di una cifra perciò
                //      aggiungo il carattere nella corretta posizione
                if (isOperatore(carattere)) {
                    termini.add(carattere + "");
                    aggiuntoOperatore = true;
                } else {
                    if (termini.isEmpty() || aggiuntoOperatore) {
                        termini.add(carattere + "");
                        aggiuntoOperatore = false;
                    } else {
                        termini.set(termini.size()-1, termini.getLast() + carattere);
                    }
                }
            }
        }
    }

    public String risultato() {
        popolaTermini();

        String op;
        int pos;
        BigDecimal ris = new BigDecimal(0), n1, n2;
        final int round = 20;

        //operazioni hop
        for (char charOpPro : hop) {
            op = charOpPro + "";

            //continuo finché non ho calcolato e rimosso l'operazione in termini
            //per tutte le operazioni prioritarie
            while (termini.contains(op)) {
                pos = termini.indexOf(op);
                n1 = new BigDecimal(termini.get(pos-1));
                n2 = new BigDecimal(termini.get(pos+1));

                //RoundingMode.HALF_UP serve per arrotondare per eccesso in caso di numeri periodici o infiniti
                //imposto l'arrotondamento con round
                switch (op) {
                    case "*": ris = n1.multiply(n2); break;
                    case "/": ris = n1.divide(n2, round, RoundingMode.HALF_UP); break;
                    case "^": ris = BigDecimal.valueOf(Math.pow(n1.doubleValue(), n2.doubleValue())).setScale(round, RoundingMode.HALF_UP); break;
                    case "%": ris = n1.remainder(n2); break;
                }

                //rimozione dopo il calcolo
                //rimuovo prima l'elemento di posizione successiva all'operatore (il secondo termine)
                //per evitare problemi di posizione in termini
                //in questo modo al posto ad es. della moltiplicazione [3, *, 2] si troverà [6]
                termini.set(pos, ris.toPlainString());
                termini.remove(pos+1);
                termini.remove(pos-1);
            }
        }

        //operazioni lop
        for (char charOpSec : lop) {
            op = charOpSec + "";

            //continuo finché non ho calcolato e rimosso l'operazione in termini
            //per tutte le operazioni secondarie
            while (termini.contains(op)) {
                pos = termini.indexOf(op);
                n1 = new BigDecimal(termini.get(pos-1));
                n2 = new BigDecimal(termini.get(pos+1));

                switch (op) {
                    case "+": ris = n1.add(n2); break;
                    case "-": ris = n1.subtract(n2); break;
                }

                termini.set(pos, ris.toPlainString());
                termini.remove(pos+1);
                termini.remove(pos-1);
            }
        }

        ris = new BigDecimal(termini.getFirst());
        ris = ris.stripTrailingZeros();

        return ris.toPlainString();
    }

} //fine classe
