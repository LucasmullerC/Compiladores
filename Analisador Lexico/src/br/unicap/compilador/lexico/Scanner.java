package br.unicap.compilador.lexico;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import br.unicap.compilador.exceptions.LexicalException;

public class Scanner {

    private char[] content;
    private int estado;
    private int pos;

    public Scanner(String filename) {
        try {
            String txtConteudo;
            // Lê texto do arquivo.
            txtConteudo = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            // Printa conteúdo do texto.
            System.out.println("DEBUG --------");
            System.out.println(txtConteudo);
            System.out.println("--------------");
            // Passa o texto para um Array de chars.
            content = txtConteudo.toCharArray();
            pos = 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Token nextToken() {
        char currentChar;
        char wardrobe = 0;
        int cont = 0;
        int decimal = 0;

        Token token;
        String term = "";

        if (isEOF()) {
            return null;
        }
        estado = 0;
        // Inicia o loop
        while (true) {
            // Coloca o char na variavel
            currentChar = nextChar();

            // Sempre inicia com estado 0
            switch (estado) {
                case 0:
                    // Se for Letra de a até z ou _.
                    if (isLetra(currentChar) || currentChar == '_') {
                        term += currentChar;
                        estado = 1;
                        // Se for Número
                    } else if (isDigit(currentChar)) {
                        estado = 3;
                        term += currentChar;
                        // Se for \
                    } else if (currentChar == '\'') {
                        estado = 6;
                        term += currentChar;
                        // Se tiver espaço
                    } else if (isSpace(currentChar)) {
                        estado = 0; // Ignora colocando estado == "0"
                        // Se tiver algum operador
                    } else if (isOperator(currentChar)) {
                        estado = 5;
                        term += currentChar;
                        wardrobe = currentChar;
                        // Se for um Caracter Especial
                    } else if (isCarac_Especial(currentChar)) {
                        estado = 7;
                        term += currentChar;
                        wardrobe = currentChar;
                        // Se o simbolo não for reconhecido.
                    } else {
                        throw new LexicalException("Unrecognized SYMBOL: " + currentChar);
                    }
                    break;
                // Caso estado seja "1"
                case 1:
                    // Detecta se o char é uma letra, digito ou _.
                    if (isLetra(currentChar) || isDigit(currentChar) || currentChar == '_') {
                        estado = 1;
                        term += currentChar;
                    } else if (isSpace(currentChar)) {
                        // Compara o termo com
                        // "main","if","else","while","do","for","int","float","char"
                        if (term.compareTo("main") == 0 || term.compareTo("if") == 0 || term.compareTo("else") == 0
                                || term.compareTo("while") == 0 || term.compareTo("do") == 0
                                || term.compareTo("for") == 0 || term.compareTo("int") == 0
                                || term.compareTo("float") == 0 || term.compareTo("char") == 0
                                || term.compareTo("lucas") == 0) {
                            estado = 11;
                            // Caso não...
                        } else if (term.compareTo("anderson") == 0) {
                            estado = 4;
                        } else if (term.compareTo("flavio") == 0) {
                            estado = 8;
                        } else if (term.compareTo("raffael") == 0) {
                            estado = 10;
                        } else {
                            estado = 2;
                        }
                        // Não reconhece
                    } else {
                        throw new LexicalException("Malformed Identifier: " + currentChar);
                    }
                    break;
                // Caso o estado seja "2"
                case 2:
                    // Seta como IDENTIFICADOR
                    back();
                    token = new Token();
                    token.setType(Token.TK_IDENTIFIER);
                    token.setText(term);
                    return token;
                // Caso o estado seja "3"
                case 3:
                    if (isDigit(currentChar)) {
                        estado = 3;
                        term += currentChar;
                    } else if (currentChar == '.' && decimal == 0) {
                        estado = 3;
                        term += currentChar;
                        decimal = 1;
                    } else if (!isLetra(currentChar) && decimal != 0) {
                        estado = 8;
                    } else if (!isLetra(currentChar) && currentChar != '.') {
                        estado = 4;
                    } else {
                        throw new LexicalException("Unrecognized Number: " + currentChar);
                    }
                    break;
                // Caso o estado seja "4"
                case 4:
                    token = new Token();
                    token.setType(Token.TK_INTEIRO);
                    token.setText(term);
                    back();
                    return token;
                // Caso o estado seja "5"
                case 5:
                    token = new Token();
                    if (wardrobe == '>') {
                        if (currentChar == '=') {
                            term += currentChar;
                            token.setType(Token.TK_OPERATOR_relacional_maior_igual);
                        } else {
                            token.setType(Token.TK_OPERATOR_relacional_maior);
                            back();
                        }
                    } else if (wardrobe == '<') {
                        if (currentChar == '=') {
                            term += currentChar;
                            token.setType(Token.TK_OPERATOR_relacional_menor_igual);
                        } else {
                            token.setType(Token.TK_OPERATOR_relacional_menor);
                            back();
                        }
                    } else if (wardrobe == '!') {
                        if (currentChar == '=') {
                            term += currentChar;
                            token.setType(Token.TK_OPERATOR_relacional_diferenca);
                        } else {
                            throw new LexicalException("Unrecognized SYMBOL: " + currentChar);
                        }
                    } else if (wardrobe == '+') {
                        token.setType(Token.TK_OPERATOR_aritmetrico_mais);
                    } else if (wardrobe == '-') {
                        token.setType(Token.TK_OPERATOR_aritmetrico_menos);
                    } else if (wardrobe == '*') {
                        token.setType(Token.TK_OPERATOR_aritmetrico_multiplicacao);
                    } else if (wardrobe == '/') {
                        token.setType(Token.TK_OPERATOR_aritmetrico_divisao);
                    } else if (wardrobe == '=') {
                        if (currentChar == '=') {
                            term += currentChar;
                            token.setType(Token.TK_OPERATOR_igual);
                        } else {
                            token.setType(Token.TK_OPERATOR_atribuidor);
                            back();
                        }
                    }
                    token.setText(term);
                    return token;
                // Caso o estado seja "6"
                case 6:
                    if (isLetra(currentChar) || isDigit(currentChar)) {
                        estado = 6;
                        term += currentChar;
                        cont++;
                    } else if (currentChar == '\'' && cont == 1) {
                        term += currentChar;
                        estado = 10;
                    } else {
                        throw new LexicalException("Malformed Identifier: " + currentChar);
                    }
                    break;
                // Caso o estado seja "7"
                case 7:
                    term += currentChar;
                    token = new Token();
                    if (wardrobe == '{') {
                        token.setType(Token.TK_CARACTER_especial_abre_chave);
                    } else if (wardrobe == '}') {
                        token.setType(Token.TK_CARACTER_especial_fecha_chave);
                    } else if (wardrobe == '(') {
                        token.setType(Token.TK_CARACTER_especial_abre_parenteses);
                    } else if (wardrobe == ')') {
                        token.setType(Token.TK_CARACTER_especial_fecha_parenteses);
                    } else if (wardrobe == ';') {
                        token.setType(Token.TK_CARACTER_especial_pontovirgula);
                    } else if (wardrobe == ',') {
                        token.setType(Token.TK_CARACTER_especial_virgula);
                    }
                    token.setText(term);
                    return token;
                // Caso o estado seja "8"
                case 8:
                    token = new Token();
                    token.setType(Token.TK_FLOAT);
                    token.setText(term);
                    back();
                    return token;
                // Caso o estado seja "10"
                case 10:
                    back();
                    token = new Token();
                    token.setType(Token.TK_CHAR);
                    token.setText(term);
                    return token;
                // Caso o estado seja "11"
                case 11:
                    back();
                    token = new Token();
                    token.setType(Token.TK_PALAVRA_reservada);
                    token.setText(term);
                    return token;
                // Caso o estado seja "12"
                case 12:
                    if (isLetra(currentChar) || isDigit(currentChar) || currentChar == '_') {
                        estado = 12;
                        term += currentChar;
                    } else if (isSpace(currentChar)) {
                        estado = 2;
                    } else {
                        throw new LexicalException("Malformed Identifier: " + currentChar);
                    }
                    break;
            }
        }

    }

    // Verificadores

    private boolean isLetra(char c) {
        return c >= 'a' && c <= 'z';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isOperator(char c) {
        return c == '>' || c == '<' || c == '=' || c == '!' || c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean isCarac_Especial(char c) {
        return c == ')' || c == '(' || c == '{' || c == '}' || c == ',' || c == ';';
    }

    private boolean isSpace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    // Proximo Char
    private char nextChar() {
        return content[pos++];
    }

    private boolean isEOF() {
        return pos == content.length;
    }

    private void back() {
        pos--;
    }
}