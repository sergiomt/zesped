package com.zesped.idl.data;

import com.zesped.util.TpvbbvaUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Properties;
import org.openide.util.Exceptions;

public class Tpv {

	private static String modelopago; //Modelo de pago 3D Secure
	private static String mediopago; //Identificador medio de pago 3D Secure
	private static String canal; //Identificador del canal Internet
	private static String idterminal;
	private static String idcomercio;
	private static String moneda; //978 para euro
	private static String urlcomercio;
	private static String idioma; //es para español
	private static String pais; //es para España
	private static String urlredir;
	private static String pal_sec_ofuscada;
	private static String clave_xor;
	private static String soporte;
	private static String tpvUrl;
	public static final BigDecimal PENDIENTE_PAGO = BigDecimal.valueOf(1);
	public static final BigDecimal PENDIENTE_CONFIRMACION = BigDecimal.valueOf(2);
	public static final BigDecimal PAGADO = BigDecimal.valueOf(3);
	public static final	BigDecimal RECHAZADO = BigDecimal.valueOf(4);

	static {
		try {
			Properties oProperties = new Properties();
			oProperties.load(Tpv.class.getResourceAsStream("tpv.properties"));
			modelopago = oProperties.getProperty("modelopago");
			mediopago = oProperties.getProperty("mediopago");
			canal = oProperties.getProperty("canal");
			idterminal = oProperties.getProperty("idterminal");
			idcomercio = oProperties.getProperty("idcomercio");
			moneda = oProperties.getProperty("moneda");
			urlcomercio = oProperties.getProperty("urlcomercio");
			idioma = oProperties.getProperty("idioma");
			pais = oProperties.getProperty("pais");
			urlredir = oProperties.getProperty("urlredir");
			pal_sec_ofuscada = oProperties.getProperty("pal_sec_ofuscada");
			clave_xor = oProperties.getProperty("clave_xor");
			soporte = oProperties.getProperty("soporte");
			tpvUrl = oProperties.getProperty("tpvUrl");
		} catch (IOException ex) {
			com.zesped.Log.out.error("Error loading tpv.properties");
		}
	}

	public static String getIdioma() {
		return idioma;
	}

	public static String getUrlredir(String contextPath) {
		return contextPath + urlredir;
	}

	public static String getPais() {
		return pais;
	}

	public static String getUrlcomercio(String contextPath) {
		return contextPath + urlcomercio;
	}

	public static String getIdterminal() {
		return idterminal;
	}

	public static String getIdcomercio() {
		return idcomercio;
	}

	public static String getMoneda() {
		return moneda;
	}

	public static String getMediopago() {
		return mediopago;
	}

	public static String getModelopago() {
		return modelopago;
	}

	public static String getCanal() {
		return canal;
	}

	public static String getFirma(String idtransaccion, String importe, String localizador) {
		try {
			String palabra_secreta = TpvbbvaUtils.desofuscarPalabraSecreta(pal_sec_ofuscada, clave_xor);
			String firma = TpvbbvaUtils.getSHA1(idterminal + idcomercio + idtransaccion + TpvbbvaUtils.replaceNoNum(importe) + moneda + localizador + palabra_secreta);
			return firma;
		} catch (UnsupportedEncodingException ex) {
			Exceptions.printStackTrace(ex);
		}
		return null;
	}

	public static String getPal_sec_ofuscada() {
		return pal_sec_ofuscada;
	}

	public static String getClave_xor() {
		return clave_xor;
	}

	public static String getSoporte() {
		return soporte;
	}

	public static String getTpvUrl() {
		return tpvUrl;
	}
}
