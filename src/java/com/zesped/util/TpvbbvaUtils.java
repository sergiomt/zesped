package com.zesped.util;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

public class TpvbbvaUtils {

    /**
     * Desofusca la palabra secreta con la clave que introdujo el usuario al
     * solicitarla.
     *
     * @param palabraSecOfuscHex la palabra secreta ofuscada y expresada en
     * hexadecimal
     * @param claveOfuscadoraLong la clave ofuscadora rellena con el idComercio
     * hasta que tenga igual longitud que la palabra secreta expresada en claro.
     * @return La palabra secreta en claro
     */
    public static String desofuscarPalabraSecreta(String palabraSecOfuscHex, String claveOfuscadoraLong) throws UnsupportedEncodingException {
        try {
            //se expresa en bytes las claves de las que se hara el XOR
            byte[] claveOfuscadoraLongBytes = claveOfuscadoraLong.getBytes("ISO-8859-1");
            byte[] palabraSecOfuscBytes = hexStringToByteArray(palabraSecOfuscHex, ";");

            byte[] palabraSecretaBytes = xorArrayBytes(claveOfuscadoraLongBytes, palabraSecOfuscBytes);
            //Se pasa la palabra secreta de bytes a ASCIII
            String palabraSecreta = new String(palabraSecretaBytes, "ISO-8859-1");

            return palabraSecreta;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Permite hacer un XOR entre dos array de bytes.
     *
     * @param operador1 Operador de la izquierda de en el XOR.
     * @param operador2 Operador de la derecha de en el XOR.
     * @exception IllegalArgumentException Si la longitud en bytes del operador
     * de la izquierda es mayor que el de la derecha.
     */
    public static byte[] xorArrayBytes(byte[] operador1, byte[] operador2) throws IllegalArgumentException {
        if (operador1.length > operador2.length) {
            throw new IllegalArgumentException("El operador1 es de longitud mayor que el operador2");
        }
        byte[] resultado = new byte[operador1.length];
        for (int cont = 0; cont < operador1.length; cont++) {
            resultado[cont] = (byte) (operador1[cont] ^ operador2[cont]);
        };
        return resultado;
    }

    /**
     *
     */
    public static byte[] hexStringToByteArray(String lista, String separador)
            throws NumberFormatException {
        StringTokenizer parser = new StringTokenizer(lista, separador);
        byte[] buffer = new byte[parser.countTokens()];
        int index = 0;
        while (parser.hasMoreTokens()) {
            buffer[index] = (byte) Integer.parseInt(parser.nextToken(), 16);
            index++;
        };
        return buffer;
    }

    /**
     * Devuelve el String resultante de eliminar del String pasado como
     * parámetro los caracteres que no son numéricos.
     */
    public static String replaceNoNum(String cadena) {
        StringBuffer buffer = new StringBuffer(cadena);
        int cont = 0;
        while (cont < buffer.length()) {
            if (!Character.isDigit(buffer.charAt(cont))) {
                buffer.deleteCharAt(cont);
            } else {
                cont++;
            }
        }
        return buffer.toString();
    }
    static final String HEX_CHR = "0123456789ABCDEF";
    /*
     * Convierte un numero 32-bit a una cadena hexadecimal con ms-byte primero
     */

    public static String hex(int num) {

        String str = "";
        for (int j = 7; j >= 0; j--) {
            str += HEX_CHR.charAt((num >> (j * 4)) & 0x0F);
        }

        return str;
    }


    /*
     * Añade enteros, "wrapping" a 2^32. Se usan operaciones de 16-bit para evitar problemas con algunos navegadores.
     */
    public static int safe_add(int x, int y) {
        int lsw = (x & 0xFFFF) + (y & 0xFFFF);
        int msw = (x >> 16) + (y >> 16) + (lsw >> 16);
        return (msw << 16) | (lsw & 0xFFFF);
    }


    /*
     * Se rota un numero 32-bit a la izquierda
     */
    public static int rol(int num, int cnt) {
        // System.out.println("Entro en rol con valores: num--> " + num + "y cnt--> " + cnt);
        return (num << cnt) | (num >>> (32 - cnt));
    }

    /*
     * Realiza la apropiada funcion combinacion triple para la iteracion actual
     */
    public static int ft(int t, int b, int c, int d) {
        if (t < 20) {
            return (b & c) | ((~b) & d);
        }
        if (t < 40) {
            return b ^ c ^ d;
        }
        if (t < 60) {
            return (b & c) | (b & d) | (c & d);
        }
        return b ^ c ^ d;
    }


    /*
     * Determina la apropiada constante a añadir para la iteracion actual
     */
    public static int kt(int t) {
        return (t < 20) ? 1518500249 : (t < 40) ? 1859775393
                : (t < 60) ? -1894007588 : -899497514;
    }


    /*
     * Devuelve la representacion hexadecimal del SHA-1 de la cadena pasada como parametro
     */
    public static String getSHA1(String str) {

        Integer[] x = str2blks_SHA1(str);
        Integer[] w = new Integer[80];

        int a = 1732584193;
        int b = -271733879;
        int c = -1732584194;
        int d = 271733878;
        int e = -1009589776;

        for (int i = 0; i < x.length; i += 16) {
            int olda = a;
            int oldb = b;
            int oldc = c;
            int oldd = d;
            int olde = e;

            for (int j = 0; j < 80; j++) {
                if (j < 16) {
                    w[j] = x[i + j];
                } else {
                    w[j] = new Integer(rol(w[j - 3].intValue() ^ w[j - 8].intValue() ^ w[j - 14].intValue() ^ w[j - 16].intValue(), 1));
                }
                int t = safe_add(safe_add(rol(a, 5), ft(j, b, c, d)), safe_add(safe_add(e, w[j].intValue()), kt(j)));
                e = d;
                d = c;
                c = rol(b, 30);
                b = a;
                a = t;
            }

            a = safe_add(a, olda);
            b = safe_add(b, oldb);
            c = safe_add(c, oldc);
            d = safe_add(d, oldd);
            e = safe_add(e, olde);
        }
        return (hex(a) + hex(b) + hex(c) + hex(d) + hex(e));

    }

    /*
     * Convierte una cadena a una secuencia de bloques de 16 palabras, almacenadas como array.
     * Añade bits de relleno y la longitud, como se describe en el SHA1 estandar
     */
    public static Integer[] str2blks_SHA1(String str) {
        int nblk = ((str.length() + 8) >> 6) + 1;
        Integer[] blks = new Integer[nblk * 16];
        for (int i = 0; i < nblk * 16; i++) {
            blks[i] = new Integer(0);
        }

        int i = 0;
        for (i = 0; i < str.length(); i++) {
            blks[i >> 2] = new Integer(blks[i >> 2].intValue() | (new Character(str.charAt(i)).hashCode() << (24 - (i % 4) * 8)));
        }

        blks[i >> 2] = new Integer(blks[i >> 2].intValue() | 0x80 << (24 - (i % 4) * 8));
        blks[nblk * 16 - 1] = new Integer(str.length() * 8);

        return blks;

    }
}
