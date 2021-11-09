package br.unicap.compilador.main;

import br.unicap.compilador.exceptions.LexicalException;
import br.unicap.compilador.exceptions.SyntaxException;
import br.unicap.compilador.lexico.Scanner;
import br.unicap.compilador.sintatico.Parser;

public class Aplicacao {

	public static void main(String[] args) {
        try {
            Scanner sc = new Scanner("Analisador Sintatico/input2.txt");
            Parser  pa = new Parser(sc);

			pa.programa();

			System.out.println("Compilation Successful!");
        } catch (LexicalException ex) {
			System.out.println("Lexical Error "+ex.getMessage());
		}
		catch (SyntaxException ex) {
			System.out.println("Syntax Error "+ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Generic Error!");
        }
    }
}