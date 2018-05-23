package com.forumdeitroll.persistence.sql.mysql;

public class Utf8Mb4Conv {
	// soluzione al baco:
	// http://stackoverflow.com/questions/10957238/incorrect-string-value-when-trying-to-insert-utf-8-into-mysql-via-jdbc
	// mysql con column encoding utf8 non supporta caratteri multibyte da piu' di 3 bytes,
	// questo metodo rileva questo tipo di caratteri e li converte automaticamente in entities html
	// per far postare PILE-OF-POO a sarrusofono

	public static String mb4safe(String s) {
		StringBuilder out = new StringBuilder();
		char[] C = s.toCharArray();
		for (int i = 0; i < C.length; i++) {
			if (Character.isHighSurrogate(C[i])) {
				int codePoint = Character.codePointAt(C, i);
				out.append(String.format("&#%d;", codePoint));
				i++; // skip 2nd char
			} else {
				out.append(C[i]);
			}
		}
		return out.toString();
	}
}
