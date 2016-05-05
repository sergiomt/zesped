/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zesped.action;

import com.knowgate.storage.StorageException;
import com.zesped.idl.data.Tpv;
import com.zesped.model.CaptureService;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Order;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.sec.authentication.AtrilSession;
import java.math.BigDecimal;
import java.util.Collection;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import org.openide.util.Exceptions;

/**
 *
 * @author Sergio
 */
public class HistorialEngageCredit extends BaseActionBean{

    @Override
    public CaptureService getCaptureService() {
        return null;
    }
    
    private static final String FORM = "/WEB-INF/jsp/historicalengagecredit.jsp";
    
    @DefaultHandler
    public Resolution form() {
        return new ForwardResolution(FORM);
    }
    
    public Collection<Order> getOrders() throws StorageException, InstantiationException, IllegalStateException, IllegalStateException, IllegalAccessException  {
            connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
            Dms oDms = getSession().getDms();
            CustomerAccount oCacc = new CustomerAccount(oDms, getSessionAttribute("customer_account_docid"));
            return (Collection<Order>) oCacc.orders(getSession()).list(getSession());

    }
    
    public Resolution deleteOrder(){
       try {
			String order_id = getContext().getRequest().getParameter("order_id");
			connect();
			AtrilSession oSes = getSession();
			Dms oDms = oSes.getDms();
			Order oOrder = new Order(oDms, order_id);
			BigDecimal oStatus = oOrder.getBigDecimal("status_number");
			if (oStatus != null && (oStatus.compareTo(Tpv.PAGADO) != 0 && oStatus.compareTo(Tpv.PENDIENTE_CONFIRMACION) != 0)) {
				oOrder.getDocument().deleteWithChildren();
				oSes.commit();
			}
			disconnect();
			return new ForwardResolution("");
		} catch (StorageException ex) {
			Exceptions.printStackTrace(ex);
		}
        return new ForwardResolution("");
    }
    
}
