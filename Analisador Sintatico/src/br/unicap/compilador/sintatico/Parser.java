package br.unicap.compilador.sintatico;

import br.unicap.compilador.exceptions.SyntaxException;
import br.unicap.compilador.lexico.*;

public class Parser {
	private Scanner scanner;
	private Token token;

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
	}
	
	public void id() {
		token = scanner.nextToken();
		if(token.getType() != Token.TK_IDENTIFIER) {
			throw new SyntaxException("Identificador Expected!");
		}
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
				throw new SyntaxException("Commando else Expected!");
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
			throw new SyntaxException("Identificador Expected!");
		}
		token = scanner.nextToken();
		if(token.getType() != Token.TK_OPERATOR_atribuidor) {
			throw new SyntaxException("Atribuidor Expected!");
		}
		expr_arit();
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
				throw new SyntaxException("Comando while Expected!");
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
			token = scanner.nextToken();
			if(token.getType() != Token.TK_CARACTER_especial_abre_parenteses) {
				throw new SyntaxException("Abre parentese Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_IDENTIFIER) {
				throw new SyntaxException("Identificador Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_OPERATOR_atribuidor) {
				throw new SyntaxException("Atribuidor Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_FLOAT && token.getType() != Token.TK_INTEIRO && token.getType() != Token.TK_CHAR) {
				throw new SyntaxException("Terminal Expected!");
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
				throw new SyntaxException("Identificador Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_OPERATOR_atribuidor) {
				throw new SyntaxException("Atribuidor Expected!");
			}
			token = scanner.nextToken();
			if(token.getType() != Token.TK_IDENTIFIER) {
				throw new SyntaxException("Identificador Expected!");
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
		}
	}
}