package br.unicap.compilador.main;

import br.unicap.compilador.exceptions.LexicalException;
import br.unicap.compilador.lexico.Scanner;
import br.unicap.compilador.lexico.Token;

public class AnalisadorLexico {

    public static void main(String[] args) {
        try {
            // Inicializa Scanner.java para ler o arquivo.
            Scanner sc = new Scanner("input.txt");
            Token token = null;
            do {
                // Pega o token
                token = sc.nextToken();
                // Se token for diferente de NULO
                if (token != null) {
                    System.out.println(token);
                }
            } while (token != null);
            // Erro Lexico
        } catch (LexicalException ex) {
            System.out.println("Lexical ERROR " + ex.getMessage());

        } catch (Exception ex) {
            System.out.println("Passou!");
        }
    }
}