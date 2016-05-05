package com.zesped.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.TypeConverter;

import com.zesped.DAO;
import com.zesped.Log;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class Products extends BaseModelObject implements TypeConverter<Product> {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[] { };

	public Products() {
		super("Products");
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	@Override
	public Product convert(String sProductId, Class<? extends Product> productClass,
						   Collection<ValidationError> conversionErrors) {
		Product oProd = null;
		AtrilSession oSes = null;
		try {
			oSes = DAO.getAdminSession("Products");
			oProd = Products.seek(oSes, sProductId);
			oSes.disconnect();
			oSes.close();
			oSes = null;
		} catch (ElementNotFoundException enf) {
			Log.out.error("No product with id "+sProductId+" was found");
			conversionErrors.add(new SimpleError("No product with id "+sProductId+" was found"));			
		} catch (Exception exc) {
			Log.out.error(exc.getClass().getName()+" Products.convert("+sProductId+") "+exc.getMessage());
			conversionErrors.add(new SimpleError(exc.getMessage()));
		} finally {
			if (oSes!=null) {
				if (oSes.isConnected()) oSes.disconnect();
				if (oSes.isOpen()) oSes.close();
			}
		}
		return oProd;
	}
		
	@Override
	public void setLocale(Locale locale) { }
	
	private class PriceComparator implements Comparator<Product> {
		public int compare(Product p1, Product p2) {
			BigDecimal oDiff = p1.getPrice().subtract(p2.getPrice());
			return oDiff.signum();
		}
	}

	private static PriceComparator oPrcCmp = new Products().new PriceComparator();

	public static Products top(AtrilSession oSes)
		  throws ElementNotFoundException, NotEnoughRightsException {
		  Zesped z = Zesped.top(oSes);
		  Products p = new Products();
		  for (Document d : z.getDocument().children()) {
			  if (d.type().name().equals(p.getTypeName())) {
				  p.setDocument(oSes.getDms().getDocument(d.id()));
				  break;
			  }
		  } // next
		  if (p.getDocument()==null) throw new ElementNotFoundException(p.getTypeName()+" document not found");
		  return p;
    }	

	public static Collection<Product> list() {
		ArrayList<Product> aProds = new ArrayList<Product>();
		AtrilSession oSes = DAO.getAdminSession("Products");
		for (Document d : top(oSes).getDocument().children()) {
			if (d.type().name().equals("Product")) {
				if (!d.attribute("is_active").isEmpty()) {
					if (d.attribute("is_active").toString().equalsIgnoreCase("1")) {
						Product p = new Product();
						p.setDocument(d);
						aProds.add(p);						
					}
				}
			}
		}
		oSes.disconnect();
		oSes.close();		
		Collections.sort(aProds, oPrcCmp);
		return aProds;
	}

	public static Product seek(AtrilSession oSes, String sProductId)
		throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		Product p = new Product();
		if (sProductId==null) throw new ElementNotFoundException("Product may not be null");
		if (sProductId.length()==0) throw new ElementNotFoundException("Product may not be empty");
		for (Document d : top(oSes).getDocument().children()) {
			if (d.type().name().equals("Product")) {
				if (!d.attribute("product_id").isEmpty()) {
					if (sProductId.equals(d.attribute("product_id").toString())) {
						p.setDocument(d);
						return p;
					}
				}
				if (!d.attribute("product_name").isEmpty()) {
					if (sProductId.equalsIgnoreCase(d.attribute("product_name").toString())) {
						p.setDocument(d);
						return p;
					}
				}
			} // fi
		} // next
		throw new ElementNotFoundException("Product "+sProductId+"not found");
	}	
}
