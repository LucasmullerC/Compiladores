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
            txtConteudo = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            System.out.println("DEBUG --------");
            System.out.println(txtConteudo);
            System.out.println("--------------");
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
        while (true) {
            currentChar = nextChar();

            switch (estado) {
                case 0:
                    if (isLetra(currentChar) || currentChar == '_') {
                        term += currentChar;
                        estado = 1;
                    } else if (isDigit(currentChar)) {
                        estado = 3;
                        term += currentChar;
                    } else if (currentChar == '\'') {
                        estado = 6;
                        term += currentChar;
                    } else if (isSpace(currentChar)) {
                        estado = 0;
                    } else if (isOperator(currentChar)) {
                        estado = 5;
                        term += currentChar;
                        wardrobe = currentChar;
                    } else if (isCarac_Especial(currentChar)) {
                        estado = 7;
                        term += currentChar;
                        wardrobe = currentChar;
                    } else {
                        throw new LexicalException("Unrecognized SYMBOL: "+ currentChar);
                    }
                    break;
                case 1:
                    if (isLetra(currentChar) || isDigit(currentChar) || currentChar == '_') {
                    		estado = 1;
                            term += currentChar;
                    } else if (isSpace(currentChar)) {
                        if(term.compareTo("main") == 0 || term.compareTo("if") == 0 || term.compareTo("else") == 0 || term.compareTo("while") == 0 || term.compareTo("do") == 0 || term.compareTo("for") == 0 || term.compareTo("int") == 0 || term.compareTo("float") == 0 || term.compareTo("char") == 0){
                            estado = 11;
                        } else{
                            estado = 2;
                        }
                    } else {
                        throw new LexicalException("Malformed Identifier: "+currentChar);
                    }
                    break;
                case 2:
                    back();
                    token = new Token();
                    token.setType(Token.TK_IDENTIFIER);
                    token.setText(term);
                    return token;
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
                        throw new LexicalException("Unrecognized Number: "+currentChar);
                    }
                    break;
                case 4:
                	if (!isEOF(currentChar))
                	back();
                    token = new Token();
                    token.setType(Token.TK_INTEIRO);
                    token.setText(term);
                    return token;
                case 5:
                    token = new Token();
                    if(wardrobe == '>'){
                        if(currentChar == '='){
                            term += currentChar;
                            token.setType(Token.TK_OPERATOR_relacional_maior_igual);
                        } else{
                            token.setType(Token.TK_OPERATOR_relacional_maior);
                            back();
                        }
                    } else if(wardrobe == '<'){
                        if(currentChar == '='){
                            term += currentChar;
                            token.setType(Token.TK_OPERATOR_relacional_menor_igual);
                        } else{
                            token.setType(Token.TK_OPERATOR_relacional_menor);
                            back();
                        }
                    } else if(wardrobe == '!'){
                        if(currentChar == '='){
                        	term += currentChar;
                            token.setType(Token.TK_OPERATOR_relacional_diferenca);
                        } else{
                            throw new LexicalException("Unrecognized SYMBOL: "+currentChar);
                        }
                    } else if(wardrobe == '+'){
                        token.setType(Token.TK_OPERATOR_aritmetrico_mais);
                    } else if(wardrobe == '-'){
                        token.setType(Token.TK_OPERATOR_aritmetrico_menos);
                    } else if(wardrobe == '*'){
                        token.setType(Token.TK_OPERATOR_aritmetrico_multiplicacao);
                    } else if(wardrobe == '/'){
                        token.setType(Token.TK_OPERATOR_aritmetrico_divisao);
                    } else if(wardrobe == '='){
                        if(currentChar == '='){
                            term += currentChar;
                            token.setType(Token.TK_OPERATOR_igual);
                        } else{
                            token.setType(Token.TK_OPERATOR_atribuidor);
                            back();
                        }
                    }
                    token.setText(term);
                    return token;
                case 6:
                	if (isLetra(currentChar) || isDigit(currentChar)) {
                		estado = 6;
                		term += currentChar;
                		cont++;
                	} else if(currentChar == '\'' && cont == 1) {
                		term += currentChar;
                		estado = 10;
                	} else {
                		throw new LexicalException("Malformed Identifier: "+currentChar);
                	}
                	break;
                case 7:
                    term += currentChar;
                    token = new Token();
                    if(wardrobe == '{'){
                        token.setType(Token.TK_CARACTER_especial_abre_chave);
                    } else if(wardrobe == '}'){
                        token.setType(Token.TK_CARACTER_especial_fecha_chave);
                    } else if(wardrobe == '('){
                        token.setType(Token.TK_CARACTER_especial_abre_parenteses);
                    } else if(wardrobe == ')'){
                        token.setType(Token.TK_CARACTER_especial_fecha_parenteses);
                    } else if(wardrobe == ';'){
                        token.setType(Token.TK_CARACTER_especial_pontovirgula);
                    } else if(wardrobe == ','){
                        token.setType(Token.TK_CARACTER_especial_virgula);
                    }
                    token.setText(term);
                    return token;
                case 8:
                    token = new Token();
                    token.setType(Token.TK_FLOAT);
                    token.setText(term);
                    back();
                    return token;
                case 10:
                    back();
                    token = new Token();
                    token.setType(Token.TK_CHAR);
                    token.setText(term);
                    return token;
                case 11:
                    back();
                    token = new Token();
                    token.setType(Token.TK_PALAVRA_reservada);
                    token.setText(term);
                    return token;
                case 12:
                    if (isLetra(currentChar) || isDigit(currentChar) || currentChar == '_') {
                        estado = 12;
                        term += currentChar;
                    } else if (isSpace(currentChar)) {
                        estado = 2;
                    } else {
                        throw new LexicalException("Malformed Identifier: "+currentChar);
                    }
                    break;
            }
        }
    }
    
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

    private char nextChar() {
		if (isEOF()) {
			return '\0';
		}
		return content[pos++];
	}

    private boolean isEOF() {
        return pos >= content.length;
    }

    private void back() {
        pos--;
    }
    
    private boolean isEOF(char c) {
    	return c == '\0';
    }
}