package br.unicap.compilador.sintatico;

import br.unicap.compilador.exceptions.SyntaxException;
import br.unicap.compilador.lexico.*;
import java.util.LinkedList;

public class Parser {
	private Scanner scanner;
	private Token token;
	
	LinkedList<String> tabela = new LinkedList<String>();
	LinkedList<String> tabelaVariavel = new LinkedList<String>();
	LinkedList<String> tabelaTipo = new LinkedList<String>();
	
	String textoId;
	int index;

	public Parser(Scanner scanner) {
		this.scanner = scanner;
	}
	
	public void programa() {
		token = scanner.nextToken();
		if(token.getText().compareTo("int") != 0) {
			throw new SyntaxException("int Expected!");
		}
		token = scanner.nextToken();
		if(token.getText().compareTo("main") != 0) {
			throw new SyntaxException("main Expected!");
		}
		token = scanner.nextToken();
		if(token.getType() != Token.TK_CARACTER_especial_abre_parenteses) {
			throw new SyntaxException("Abre parentese Expected!");
		}
		token = scanner.nextToken();
		if(token.getType() != Token.TK_CARACTER_especial_fecha_parenteses) {
			throw new SyntaxException("Fecha parenteses Expected!");
		}
		token = scanner.nextToken();
		bloco();
	}
	
	public void bloco() {
		if(token.getType() != Token.TK_CARACTER_especial_abre_chave) {
			throw new SyntaxException("Abre chave Expected!");
		}
		token = scanner.nextToken();
		do {
			decl_var();
			comando();
			token = scanner.nextToken();
		} while(token.getType() != Token.TK_CARACTER_especial_fecha_chave);
	}
	
	public void decl_var() {
		tipo();
		id();
		token = scanner.nextToken();
		if(token.getType() != Token.TK_CARACTER_especial_pontovirgula) {
			throw new SyntaxException("Ponto e virgula Expected!");
		}
	}
	
	public void tipo() {
		if(token.getText().compareTo("int") != 0 && token.getText().compareTo("float") != 0 && token.getText().compareTo("char") != 0) {
			throw new SyntaxException("Type Expected!");
		}
		
		tabela.add(token.getText());
	}
	
	public void id() {
		token = scanner.nextToken();
		if(token.getType() != Token.TK_IDENTIFIER) {
			throw new SyntaxException("Identifier Expected!");
		}
		
		if(tabelaVariavel.contains(token.getText()) == true) {
			throw new SyntaxException("Variavel '" + token.getText() + "' repetida!");
		}
		
		tabelaVariavel.add(token.getText());
	}
	
	public void comando() {
		token = scanner.nextToken();
		if(token.getText().compareTo("while") == 0 || token.getText().compareTo("do") == 0 || token.getText().compareTo("for") == 0) {
			iteracao();
		}
		else if(token.getText().compareTo("if") == 0) {
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_abre_parenteses) {
				throw new SyntaxException("Abre parentese Expected!");
			}
			expr_relacional();
			if(token.getType() != Token.TK_CARACTER_especial_fecha_parenteses) {
				throw new SyntaxException("Fecha parenteses Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_abre_chave) {
				throw new SyntaxException("Abre chave Expected!");
			}
			comando();
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_fecha_chave) {
				throw new SyntaxException("Fecha chave Expected!");
			}
			token = scanner.nextToken();
			if(token.getText().compareTo("else") != 0) {
				throw new SyntaxException("Command else Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_abre_chave) {
				throw new SyntaxException("Abre chave Expected!");
			}
			comando();
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_fecha_chave) {
				throw new SyntaxException("Fecha chave Expected!");
			}
		}
		else {
			comando_basico();
		}
	}
	
	public void comando_basico() {
		if(token.getType() == Token.TK_CARACTER_especial_abre_chave) {
			bloco();
		}
		else {
			atribuicao();
		}
	}
	
	public void atribuicao() {
		if(token.getType() != Token.TK_IDENTIFIER) {
			throw new SyntaxException("Identifier Expected!");
		}
		textoId = token.getText();
		if(tabelaVariavel.contains(token.getText()) == false) {
			throw new SyntaxException("Variavel '" + token.getText() + "' nao declarada!");
		}
		token = scanner.nextToken();
		if(token.getType() != Token.TK_OPERATOR_atribuidor) {
			throw new SyntaxException("Atribuidor Expected!");
		}
		expr_arit();
		if(tabelaTipo.contains(Token.TK_CHAR) && tabelaTipo.contains(Token.TK_FLOAT)) {
			throw new SyntaxException("Tipo 'char' incompativel com tipo 'float'!");
		}
		else if(tabelaTipo.contains(Token.TK_CHAR) && tabelaTipo.contains(Token.TK_INTEIRO)) {
			throw new SyntaxException("Tipo 'char' incompativel com tipo 'inteiro'!");
		}
		else if(tabelaTipo.contains(Token.TK_FLOAT) && tabelaTipo.contains(Token.TK_INTEIRO) && tabelaTipo.contains(Token.TK_CHAR) == false) {
			index = tabelaVariavel.indexOf(textoId);
			if(tabela.get(index).compareTo("float") != 0) {
				throw new SyntaxException("Invalid type identifier: float expected!");
			}
		}
		else if(tabelaTipo.contains(Token.TK_FLOAT) && tabelaTipo.contains(Token.TK_CHAR) == false) {
			index = tabelaVariavel.indexOf(textoId);
			if(tabela.get(index).compareTo("float") != 0) {
				throw new SyntaxException("Invalid type identifier: float expected!");
			}
		}
		else if(tabelaTipo.contains(Token.TK_INTEIRO) && tabelaTipo.contains(Token.TK_CHAR) == false) {
			index = tabelaVariavel.indexOf(textoId);
			if(tabela.get(index).compareTo("char") == 0) {
				throw new SyntaxException("Invalid type identifier: int expected or float expected!");
			}
		}
		else {
			index = tabelaTipo.indexOf(textoId);
			if(tabelaVariavel.get(index).compareTo("char") != 0) {
				throw new SyntaxException("Invalid type identifier: char expected!");
			}
		}
		tabelaTipo.clear();
		if(token.getType() != Token.TK_CARACTER_especial_pontovirgula) {
			throw new SyntaxException("Ponto e virgula Expected!");
		}
	}
	
	public void iteracao() {
		if(token.getText().compareTo("while") == 0) {
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_abre_parenteses) {
				throw new SyntaxException("Abre parentese Expected!");
			}
			expr_relacional();
			if(token.getType() != Token.TK_CARACTER_especial_fecha_parenteses) {
				throw new SyntaxException("Fecha parenteses Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_abre_chave) {
				throw new SyntaxException("Abre chave Expected!");
			}
			comando();
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_fecha_chave) {
				throw new SyntaxException("Fecha chave Expected!");
			}
		}
		else if(token.getText().compareTo("do") == 0) {
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_abre_chave) {
				throw new SyntaxException("Abre chave Expected!");
			}
			comando();
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_fecha_chave) {
				throw new SyntaxException("Fecha chave Expected!");
			}
			token = scanner.nextToken();
			if(token.getText().compareTo("while") != 0) {
				throw new SyntaxException("Command while Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_abre_parenteses) {
				throw new SyntaxException("Abre parentese Expected!");
			}
			expr_relacional();
			if(token.getType() != Token.TK_CARACTER_especial_fecha_parenteses) {
				throw new SyntaxException("Fecha parenteses Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_pontovirgula) {
				throw new SyntaxException("Ponto e virgula Expected!");
			}
		}
		else {
			String idAnterior;
			
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_abre_parenteses) {
				throw new SyntaxException("Abre parentese Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_IDENTIFIER) {
				throw new SyntaxException("Identifier Expected!");
			}
			if(tabelaVariavel.contains(token.getText()) == false) {
				throw new SyntaxException("Variavel '" + token.getText() + "' nao declarada!");
			}
			textoId = token.getText();
			token = scanner.nextToken();
			if(token.getType() != Token.TK_OPERATOR_atribuidor) {
				throw new SyntaxException("Atribuidor Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_FLOAT && token.getType() != Token.TK_INTEIRO && token.getType() != Token.TK_CHAR) {
				throw new SyntaxException("Terminal Expected!");
			}
			index = tabelaVariavel.indexOf(textoId);
			if(token.getType() != tabela.get(index)) {
				if(token.getType().compareToIgnoreCase("character") == 0 && tabela.get(index).compareTo("float") == 0) {
					throw new SyntaxException("Tipo 'float' incompativel com tipo 'char'!");
				}
				else if(token.getType().compareToIgnoreCase("character") == 0 && tabela.get(index).compareTo("int") == 0) {
					throw new SyntaxException("Tipo 'int' incompativel com tipo 'char'!");
				}
				else if(token.getType().compareToIgnoreCase("inteiro") == 0 && tabela.get(index).compareTo("char") == 0) {
					throw new SyntaxException("Tipo 'char' incompativel com tipo 'int'!");
				}
				else if(token.getType().compareToIgnoreCase("float") == 0 && tabela.get(index).compareTo("int") == 0) {
					throw new SyntaxException("Tipo 'int' incompativel com tipo 'float'!");
				}
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_pontovirgula) {
				throw new SyntaxException("Ponto e virgula Expected!");
			}
			expr_relacional();
			if(token.getType() != Token.TK_CARACTER_especial_pontovirgula) {
				throw new SyntaxException("Ponto e virgula Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_IDENTIFIER) {
				throw new SyntaxException("Identifier Expected!");
			}
			if(tabelaVariavel.contains(token.getText()) == false) {
				throw new SyntaxException("Variavel '" + token.getText() + "' nao declarada!");
			}
			idAnterior = token.getText();
			index = tabelaVariavel.indexOf(idAnterior);
			if(tabela.get(index).compareTo("char") == 0) {
				throw new SyntaxException("Tipo 'char' incompativel com tipo 'int'!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_OPERATOR_atribuidor) {
				throw new SyntaxException("Atribuidor Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_IDENTIFIER) {
				throw new SyntaxException("Identifier Expected!");
			}
			if(idAnterior.compareTo(token.getText()) != 0) {
				throw new SyntaxException("Previous different identifier!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_OPERATOR_aritmetrico_divisao && token.getType() != Token.TK_OPERATOR_aritmetrico_mais && token.getType() != Token.TK_OPERATOR_aritmetrico_menos && token.getType() != Token.TK_OPERATOR_aritmetrico_multiplicacao) {
				throw new SyntaxException("Operador aritmetrico Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_INTEIRO) {
				throw new SyntaxException("Inteiro Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_fecha_parenteses) {
				throw new SyntaxException("Fecha parenteses Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_abre_chave) {
				throw new SyntaxException("Abre chave Expected!");
			}
			comando();
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_fecha_chave) {
				throw new SyntaxException("Fecha chave Expected!");
			}
		}
	}
	
	public void expr_relacional() {
		expr_arit();
		if(token.getType() != Token.TK_OPERATOR_relacional_diferenca && token.getType() != Token.TK_OPERATOR_relacional_maior && token.getType() != Token.TK_OPERATOR_relacional_maior_igual && token.getType() != Token.TK_OPERATOR_relacional_menor && token.getType() != Token.TK_OPERATOR_relacional_menor_igual && token.getType() != Token.TK_OPERATOR_igual) {
			throw new SyntaxException("Operador relacional Expected!");
		}
		expr_arit();
	}
	
	public void expr_arit() {
		termo();
		expr_aritL();
	}
	
	public void expr_aritL() {
		if (token != null) {
			if(token.getType() != Token.TK_CARACTER_especial_pontovirgula && token.getType() != Token.TK_OPERATOR_relacional_diferenca && token.getType() != Token.TK_OPERATOR_relacional_maior && token.getType() != Token.TK_OPERATOR_relacional_maior_igual && token.getType() != Token.TK_OPERATOR_relacional_menor && token.getType() != Token.TK_OPERATOR_relacional_menor_igual && token.getType() != Token.TK_OPERATOR_igual && token.getType() != Token.TK_CARACTER_especial_fecha_parenteses) {
				if(token.getType() != Token.TK_OPERATOR_aritmetrico_mais && token.getType() != Token.TK_OPERATOR_aritmetrico_menos) {
					throw new SyntaxException("Operador aritmetrico Expected!");
				}
				termo();
				expr_aritL();
			}
		}
	}
	
	public void termo() {
		fator();
		termoL();
	}
	
	public void termoL() {
		token = scanner.nextToken();
		if (token != null) {
			if(token.getType() == Token.TK_OPERATOR_aritmetrico_multiplicacao || token.getType() == Token.TK_OPERATOR_aritmetrico_divisao) {
				if(token.getType() != Token.TK_OPERATOR_aritmetrico_multiplicacao && token.getType() != Token.TK_OPERATOR_aritmetrico_divisao) {
					throw new SyntaxException("Operador aritmetrico Expected!");
				}
				fator();
				termoL();
			}
		}
	}
	
	public void fator() {
		token = scanner.nextToken();
		if(token.getType() == Token.TK_CARACTER_especial_abre_parenteses) {
			if(token.getType() != Token.TK_CARACTER_especial_abre_parenteses) {
				throw new SyntaxException("Abre parentese Expected!");
			}
			expr_arit();
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_fecha_parenteses) {
				throw new SyntaxException("Fecha parenteses Expected!");
			}
		}
		else {
			if(token.getType() != Token.TK_IDENTIFIER && token.getType() != Token.TK_FLOAT && token.getType() != Token.TK_INTEIRO && token.getType() != Token.TK_CHAR) {
				throw new SyntaxException("Terminal Expected!");
			}
			
			if(token.getType() == Token.TK_IDENTIFIER && tabelaVariavel.contains(token.getText()) == false) {
				throw new SyntaxException("Variavel '" + token.getText() + "' nao declarada!");
			}
			
			if(token.getType() == Token.TK_FLOAT || token.getType() == Token.TK_INTEIRO || token.getType() == Token.TK_CHAR) {
				tabelaTipo.add(token.getType());
			}
		}
	}
}