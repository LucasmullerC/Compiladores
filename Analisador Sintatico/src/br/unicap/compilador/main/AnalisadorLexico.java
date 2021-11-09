package br.unicap.compilador.main;

import br.unicap.compilador.exceptions.LexicalException;
import br.unicap.compilador.lexico.Scanner;
import br.unicap.compilador.lexico.Token;

public class AnalisadorLexico {

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner("input.txt");
            Token token = null;
            do {
                token = sc.nextToken();
                if (token != null) {
                    System.out.println(token);
                }
            } while (token != null);
        } catch (LexicalException ex) {
            System.out.println("Lexical ERROR " + ex.getMessage());

        } catch (Exception ex) {
            System.out.println("Generic Error!");
        }
    }
}