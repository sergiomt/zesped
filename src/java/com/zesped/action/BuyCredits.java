package com.zesped.action;

import java.math.BigDecimal;
import java.util.Collection;

import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Order;
import com.zesped.model.OrderLine;
import com.zesped.model.Product;
import com.zesped.model.Products;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.sec.authentication.AtrilSession;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import com.knowgate.debug.StackTraceUtil;

public class BuyCredits extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/buycredits.jsp" ;

	@Validate(required=true, on="buy", converter=Products.class)
	public Product selectedProduct;

	public BuyCredits() { }

	public Collection<Product> getProducts() {
		return Products.list();
	}

	public CaptureService getCaptureService() {
		return CaptureService.NONE;
	}

	@DefaultHandler
	public Resolution form() {
	  return new ForwardResolution(FORM);
	}

	@ValidationMethod(on="buy")
	public void validate(ValidationErrors errors) {
		if (selectedProduct.getCredits()==null) {
			errors.add("selectedProduct", new LocalizableError("com.zesped.action.BuyCredits.noCreditsSpecified"));	
		}
	}

	public Resolution buy() {
		  Order order = new Order();
		  try {
				connect();
				AtrilSession oSes = getSession();
				Dms oDms = oSes.getDms();
				CustomerAccount cacc = new CustomerAccount(oDms, getSessionAttribute("customer_account_docid"));
				order = cacc.createOrder(oSes);
				order.put("user_id", getSessionAttribute("user_docid"));
				order.put("credits_bought", selectedProduct.getCredits());
				order.save(oSes);

				Log.out.info("Created order "+order.getString("order_id")+" for customer "+getSessionAttribute("customer_acount"));
				
				OrderLine line = order.addLine(oSes, selectedProduct);

				Log.out.info("Added order line for product "+selectedProduct.getString("product_name"));

				order.put("base_price", line.getString("base_price"));
				order.put("taxes", line.getString("taxes"));
				order.put("taxespct", line.getString("taxespct"));
				order.put("total_price", line.getString("subtotal_price"));
				order.put("currency", line.getString("currency"));
				order.save(oSes);
				
				DAO.log(oSes, order.getDocument(), Order.class, "CREATE ORDER", AtrilEvent.Level.INFO, order.getDocument().id()+";"+getSessionAttribute("customer_acount")+";"+selectedProduct.getString("product_name"));

				cacc.restoreCredits(oSes, new BigDecimal(selectedProduct.getCredits().longValue()));

				Log.out.info("Added "+selectedProduct.getCredits().toString()+" credits to customer "+getSessionAttribute("customer_acount"));
				DAO.log(oSes, cacc.getDocument(), CustomerAccount.class, "UPDATE CREDITS", AtrilEvent.Level.INFO, cacc.getDocument().id()+";"+cacc.get("credits_left"));
				
				disconnect();
				LoginInterceptor.expire(getSessionAttribute("nickname"), getSessionAttribute("password"));
				return new RedirectResolution(CaptureInvoice.class);
		  } catch (Exception e) {
			    String sStackTrace = "";
			    try { sStackTrace = StackTraceUtil.getStackTrace(e); } catch (Exception ignore) { }
				Log.out.error(e.getClass().getName()+" "+e.getMessage()+"\n"+sStackTrace);
				getContext().getMessages().add(new SimpleMessage("ERROR "+e.getMessage(), order));
				return new RedirectResolution(BuyCredits.class);
	      } finally { close(); }
	}	
}
