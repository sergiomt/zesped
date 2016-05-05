package com.zesped.model;

import java.math.BigDecimal;
import java.nio.channels.NotYetConnectedException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.zesped.DAO;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class Order extends BaseModelObject {

	public static final double TAX_RATE = 0.18d;

	private static final long serialVersionUID = 1L;

	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("order_id",DataType.STRING,true,true,null),
    	new Attr("customer_acount",DataType.STRING,false,true,new ForeignKey(CustomerAccount.class,"account_id")),
    	new Attr("user_id",DataType.STRING,false,true,new ForeignKey(User.class,"user_id")),
    	new Attr("promo_code",DataType.STRING,false,false,null),
    	new Attr("creation_date",DataType.DATE_TIME,false,true,null),
    	new Attr("credits_bought",DataType.NUMBER,false,true,null),
    	new Attr("payment_mean",DataType.STRING,false,false,null),
    	new Attr("service_type",DataType.STRING,false,false,null),
    	new Attr("base_price",DataType.STRING,false,true,null),
    	new Attr("taxes",DataType.STRING,false,true,null),
    	new Attr("taxespct",DataType.STRING,false,true,null),
    	new Attr("total_price",DataType.STRING,false,true,null),
    	new Attr("currency",DataType.STRING,false,true,null),
    	new Attr("comments",DataType.STRING,false,false,null),
    	// SICUBO 201212 {
    	new Attr("name", DataType.STRING, false, false, null),
    	new Attr("cif", DataType.STRING, false, false, null),
    	new Attr("phone", DataType.STRING, false, false, null),
    	new Attr("mail", DataType.STRING, false, false, null),
    	new Attr("address", DataType.STRING, false, false, null),
    	new Attr("city", DataType.STRING, false, false, null),
    	new Attr("state", DataType.STRING, false, false, null),
    	new Attr("country", DataType.STRING, false, false, null),
    	new Attr("postcode", DataType.STRING, false, false, null),
    	new Attr("cardnumber", DataType.STRING, false, false, null),
    	new Attr("cardholder", DataType.STRING, false, false, null),
    	new Attr("expiration_month_card", DataType.STRING, false, false, null),
    	new Attr("expiration_year_card", DataType.STRING, false, false, null),
    	new Attr("status_number", DataType.NUMBER, false, false, null),
    	new Attr("transaction", DataType.STRING, false, false, null),
    	new Attr("cvv2", DataType.STRING, false, false, null),
    	new Attr("pay_date", DataType.DATE_TIME, false, false, null)
    	//SICUBO 201212 }    	
    };

	public Order() {
		super("Order");	
	}

	public Order(AtrilSession oSes, Orders oOrs) {
		super(oSes.getDms().newDocument(oSes.getDms().getDocumentType("Order"), oOrs.getDocument()));
		Date dtNow = new Date();
	    Random oRnd = new Random();
		SimpleDateFormat oFmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		put("order_id", oFmt.format(dtNow)+String.valueOf(oRnd.nextInt(10)+10*oRnd.nextInt(10)+100*oRnd.nextInt(10)));
		put("creation_date", dtNow);
	}

	public Order(Dms oDms, String sDocId) throws ElementNotFoundException {
		super(oDms, sDocId);
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public String getOrderId() {
		return getString("order_id");
	}

	public String getUserUuid() {
		return getString("user_id");
	}

	public Date getOrderDate() {
		return this.getDate("creation_date");
	}

	public BigDecimal getCreditsBought() {
		return getBigDecimal("credits_bought");
	}

	public String getBasePrice() {
		return getString("base_price");
	}

	public String getTaxes() {
		return getString("taxes");
	}

	public String getTaxesPct() {
		return getString("taxespct");
	}

	public String getTotalPrice() {
		return getString("total_price");
	}
	
	public ArrayList<OrderLine> lines() {
		ArrayList<OrderLine> aLines = new ArrayList<OrderLine>();
		AtrilSession oSes = null;
		try {
			oSes = DAO.getAdminSession("Order.lines");
			Dms oDma = oSes.getDms();
			for (Document d : oDma.getDocument(id()).children())
				aLines.add(new OrderLine(oDma.getDocument(d.id())));
			oSes.disconnect();
			oSes.close();
			oSes = null;
		} finally {
			if (oSes != null) {
				if (oSes.isConnected()) {
					oSes.disconnect();
				}
				if (oSes.isOpen()) {
					oSes.close();
				}
			}
		}
		return aLines;
	}

	public OrderLine addLine(AtrilSession oSes, Product oProd) {
		long lNextLine = 0l;
		Dms oDms = oSes.getDms();
		ArrayList<OrderLine> aLines = new ArrayList<OrderLine>();
		for (Document d : getDocument().children())
			aLines.add(new OrderLine(oDms.getDocument(d.id())));
		for (OrderLine ol : aLines)
			if (ol.getLong("line_num")>lNextLine)
				lNextLine = ol.getLong("line_num");
		lNextLine++;
		OrderLine oOrdLn = new OrderLine(oSes, this);
		double dBase = Double.parseDouble(oProd.getString("price"));
		double dTaxes = dBase*TAX_RATE;
		oOrdLn.put("line_num", new Long(lNextLine));
		oOrdLn.put("product_id", oProd.get("product_id"));
		oOrdLn.put("product_name", oProd.get("product_name"));
		oOrdLn.put("base_price", oProd.get("price"));
		oOrdLn.put("currency", oProd.get("currency"));
		oOrdLn.put("taxespct", String.valueOf(TAX_RATE));
		oOrdLn.put("taxes", String.valueOf(dTaxes));
		oOrdLn.put("subtotal_price", String.valueOf(dBase+dTaxes));
		oOrdLn.save(oSes);
		return oOrdLn;
	}
	
	@Override
	public void save(AtrilSession oSes)
		throws ClassCastException, RuntimeException, IllegalStateException, NullPointerException, NotYetConnectedException, DmsException {
		
		Date dtNow = new Date();
		if (isNull("creation_date")) put("creation_date", dtNow);
		
		if (isNull("currency")) put("currency", "EUR");
		if (isNull("base_price")) put("base_price", "0");
		if (isNull("total_price")) put("total_price", "0");
		if (isNull("credits_bought")) put("credits_bought", new Long(0l));
		if (isNull("taxes")) put("taxes", "0");
		if (isNull("taxespct")) put("taxespct", String.valueOf(TAX_RATE));
		
		super.save(oSes);
	}

	// SICUBO 201212 {
	public String getTransaction() {
		Object o = get("transaction");
		return o == null ? "" : o.toString();
	}

	public String getTotal_price() {
		Object o = get("total_price");
		if (o == null) {
			return "";
		} else {
			BigDecimal oTotal = new BigDecimal(o.toString());
			String sTotal = new DecimalFormat("0.00").format(oTotal);
			return sTotal.replace(".", ",");
		}
	}

	public String getExpiration_month_card() {
		Object o = get("expiration_month_card");
		Long oMonth = new Long(o.toString());
		return new DecimalFormat("00").format(oMonth);
	}

	public String getExpiration_year_card() {
		Object o = get("expiration_year_card");
		Long oYear = new Long(o.toString());
		if (oYear<100) oYear+=2000;
		return new DecimalFormat("0000").format(oYear);
	}

	private static Integer rnd = 0;
	
	public void newTransaction() {
		Date dtNow = new Date();
		setTransaction(dtNow);
	}
	
	private void setTransaction(Date dtNow) {
		Calendar oCalendar = Calendar.getInstance();
		oCalendar.setTime(dtNow);
		long year = (oCalendar.get(Calendar.YEAR) - 1900) % 99;
		long day = oCalendar.get(Calendar.DAY_OF_YEAR);
		long hour = oCalendar.get(Calendar.HOUR_OF_DAY);
		long min = oCalendar.get(Calendar.MINUTE);
		long sec = oCalendar.get(Calendar.SECOND);
		long msec = oCalendar.get(Calendar.MILLISECOND) / 100;		
		long transaction;
		synchronized (rnd) {
			transaction =
					year * 366 * 24 * 60 * 60 * 10 * 10
					+ day * 24 * 60 * 60 * 10 * 10
					+ hour * 60 * 60 * 10 * 10
					+ min * 60 * 10 * 10
					+ sec * 10 * 10
					+ msec * 10
					+ rnd;
			rnd++;
		}
		String sTransaction = new DecimalFormat("000000000000").format(transaction);
		put("transaction", sTransaction);
	}

	public String getCardnumber() {
		return getStringNull("cardnumber");
	}

	public String getCvv2() {
		return getStringNull("cvv2");
	}
	
	// SICUBO 201212 }
}
