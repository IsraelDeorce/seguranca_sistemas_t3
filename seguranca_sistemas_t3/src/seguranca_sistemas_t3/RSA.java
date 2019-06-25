package seguranca_sistemas_t3;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Random;

/**
 * Este arquivo java eh uma alternativa de implementação para o exercício
 * proposto no terceiro trabalho da disciplina de Segurança de Sistemas.
 * 
 * Enunciado:
 * 
 * "Gerar uma chave pública e uma chave privada para usar com RSA. Estas chave
 * deve ter um número de bits superior a 1000. Imprimir a chave pública e chave
 * privada. Dada uma mensagem M, cifre esta mensagem usando RSA com a chave
 * pública gerada no item 1. Imprimir a mensagem cifrada C. Dada uma mensagem C,
 * decifre esta mensagem usando RSA com a chave privada gerada no item 1.
 * Imprimir a mensagem decifrada.
 * 
 * O programa deve gerar, usando o pequeno teorema de Fermat, os números primos
 * que comporão o módulo utilizado no RSA. Para cálculo do inverso de um número
 * pode usar o algoritmo extendido de Euclides ou uma biblioteca pronta."
 * 
 * Fontes consultadas:
 * http://doc.sagemath.org/html/en/thematic_tutorials/numtheory_rsa.html
 * 
 * Data: 2019/1
 * 
 * @author Israel Deorce Vieira Junior
 * @professor Avelino Zorzo
 */

public class RSA {
	/*
	 * Sumario de variaveis:
	 * 
	 * textoClaro = Texto a ser incriptado
	 * p = um numero primo qualquer de tamanho 1024 bits; 
	 * q = um numero primo qualquer de tamanho 1024; 
	 * N = p * q;
	 * eulerN = (p-1) * (q-1)
	 * e = Chave publica;
	 * d = Chave privada 
	 * textoCifrado = Texto claro em formato numerico apos cifragem
	 * textoDecifrado = Texto claro em formato numerico apos decifragem
	 */
	private static Random rnd = new Random();
	private static String textoClaro = "O Israel vai se sair bem no trabalho do Avelino!";
	private static BigInteger p, q, N, eulerN, e, d, textoCifrado, textoDecifrado;

	public static void main(String[] args) {
		if (args.length > 0)
			textoClaro = args[0];

		// ### Gerando numeros primos... ###
		//
		// Para encriptar uma mensagem RSA, precisaremos de dois numeros primos (p e q).
		// Para gerar os numeros primos de 1024 bits, utiliza-se a biblioteca BigInteger
		// que utiliza Fermat, passando valor randomico e o tamanho do numero.
		//
		// A chance de um valor gerado nao ser primo, eh de 2^(-100)
		// Fonte: https:\\www.tutorialspoint.com/java/math/biginteger_probableprime.htm
		do {
			p = BigInteger.probablePrime(1024, rnd);
			q = BigInteger.probablePrime(1024, rnd);
		} while (p == q);
		System.out.println("Primo1 p: " + p + "\nPrimo2 q: " + q);

		// ### Descobrir o N (modulo), fazendo N = p * q ###
		N = p.multiply(q);
		System.out.println("N: " + N);

		
		// ### Descobrir o e (chave publica) ###
		//
		// Descobrir Euler(N), fazendo Euler(N) = p-1 * q-1;
		//
		// "Euler’s phi function" (Gerar Zn* (ZnEuler) para n = eulerN):
		// Considere todos os inteiros de 1 a 'N' inclusivo. Liste todos os inteiros que
		// são coprimos de N. Em outras palavras, queremos encontrar os inteiros n, onde
		// 1<='e'<='N', tal que o máximo divisor comum entre 'e' e 'eulerN' dara 1
		// ( gcd(e,eulerN) = 1 ).
		eulerN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		System.out.println("Euler(N): " + eulerN);
		
		do {
			e = new BigInteger(1024, rnd);
		} while (!(e.gcd(eulerN)).equals(BigInteger.ONE) || e.longValue() < 1);
		System.out.println("Chave Publica e: " + e);

		
		// ### Descobrir o d (chave privada) ###
		//
		// Utiliza-se o algoritmo extendido de Euclides para encontrar a chave privada
		// 'd'. O 'd' deve ser o inverso de 'e' no mod de 'eulerN' para isso, multiplico 'e'
		// pelos valores de Zn ate encontrar 1, e esse 'n' sera o inverso de 'e', n == d.
		// BigInteger realiza essa operacao utlizando a funcao 'e.modInverse(eulerN)'
		//
		d = e.modInverse(eulerN);
		System.out.println("Chave Privada d: " + d);

		// ### Cifrando a mensagem... ###
		//
		// Para cifrar, pegar a mensagem de texto claro (Ex: "8"), e elevar essa
		// mensagem na chave privada 'd' em mod N.
		
		// Para que a encriptacao e decriptacao funciona, eh preciso trabalhar com 
		//numeros, portanto converteu-se o texto claro em bytes conforme tabela ASCII
		try {
			System.out.println("Texto Claro: " + textoClaro);
			byte[] bytes = textoClaro.getBytes("US-ASCII");
			BigInteger ascMsg = new BigInteger(bytes);

			textoCifrado = ascMsg.modPow(d, N);
			System.out.println("Texto Cifrado: " + textoCifrado);

		} catch (UnsupportedEncodingException exception) {
			System.out.println("try another");
		}

		// ### Decifrando Mensagem ###
		// Pegar mensagem cifrada e elevar na chave publica 'e' em mod 'N'
		textoDecifrado = textoCifrado.modPow(e, N);
		byte[] bytes = textoDecifrado.toByteArray();
		String novaMsg = new String(bytes);
		System.out.println("Texto Decifrado: " + novaMsg);
	}
}
