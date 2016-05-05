package com.zesped.action;

import com.knowgate.storage.StorageException;
import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.idl.data.Tpv;
import com.zesped.model.CaptureService;
import com.zesped.model.Countries;
import com.zesped.model.Country;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Order;
import com.zesped.model.OrderLine;
import com.zesped.model.Product;
import com.zesped.model.Products;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.sec.authentication.AtrilSession;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.ajax.JavaScriptResolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import org.openide.util.Exceptions;


public class EngageCredit extends BaseDatableBean {

    private static final String FORM = "/WEB-INF/jsp/engagecredit.jsp";
    
    @Validate(required = true, on = "save", converter = Products.class,label="El producto a contratar")
    public Product selectedProduct;
 
    @Validate(required = true, on = "save", label="Pais")
    public String country;
    
    @Validate(required = true, on = "save", label="Provincia")
    public String state;
     
    @Validate(required = true, on = "save", label="Razón social o titular")
    public String name;
    
    @Validate(required = true, on = "save", label="CIF o NIF")
    public String cif;
    
    @Validate(required = true, on = "save", label="Teléfono de contacto")
    public String phone;
    
    @Validate(required = true, on = "save", label="Email")
    public String mail;
    
    @Validate(required = true, on = "save", label="Dirección")
    public String address;
        
    @Validate(required = true, on = "save", label="Código Postal")
    public String postcode;
          
    //@Validate(required = true, on = "save", label="Número Tarjeta")
    public String cardnumber;
    
    //@Validate(required = true, on = "save", label="Nombre Titular")
    public String cardholder;
    
    @Validate(required = true, on = "save", label="Fecha Caducidad")
    public String expiration_month_card;
    
    @Validate(required = true, on = "save", label="Fecha Caducidad")
    public String expiration_year_card;
        
    //@Validate(required = true, on = "save", label="Código Seguridad (CVV2)")
    public String cvv2;
    
    @Validate(required = true, on = "save", label="Ciudad")
    public String city;
    
    
    public String promo_code;
    
    public Order order;
        
    @DefaultHandler
    public Resolution form() {
        String order_id = getParam("order_id","");
        if(order_id.length()>0){
            try {
                connect();
                AtrilSession oSes = getSession();
                Dms oDms = oSes.getDms();
                order = new Order(oDms,order_id);
                disconnect();
                return new ForwardResolution("/WEB-INF/jsp/engagecreditok.jsp");
            } catch (StorageException ex) {
                Log.out.error("EngageCredit.form() "+ex.getClass().getName()+" "+ex.getMessage(), ex);
            }
        }else{           
            try {
                connect();
                AtrilSession oSes = getSession();
                Dms oDms = oSes.getDms();
                CustomerAccount cacc = new CustomerAccount(oDms, getSessionAttribute("customer_account_docid"));
                setName(cacc.getString("name_billing"));
                setCif(cacc.getString("cif_billing"));
                setPhone(cacc.getString("phone_billing"));
                setMail(cacc.getString("mail_billing"));
                setAddress(cacc.getString("address_billing"));
                setPostcode(cacc.getString("postcode_billing"));
                setCountry(cacc.getString("country_billing"));
                setState(cacc.getString("state_billing"));
                setCity(cacc.getString("city_billing"));
                disconnect();
                return new ForwardResolution(FORM);
            } catch (StorageException ex) {
                 Log.out.error("EngageCredit.form() "+ex.getClass().getName()+" "+ex.getMessage(), ex);
            }
        }
        return null;
    }
    
    @Override
    public CaptureService getCaptureService() {
        return null;
    }
    
        
    @ValidationMethod(on = "save")
    public void validatePayInformation() {
        ValidationErrors errors= getContext().getValidationErrors();
        if(selectedProduct.getPrice().compareTo(BigDecimal.ZERO)>0)
        {
            if(getCvv2()==null){
                errors.add("cvv2", new LocalizableError("com.zesped.action.EngageCredit.EmptyCvv2"));
            }
            if(getCardnumber()==null){
                errors.add("cardnumber", new LocalizableError("com.zesped.action.EngageCredit.EmptyCardnumber"));
            }
            if(getCardholder()==null){
                errors.add("cardholder", new LocalizableError("com.zesped.action.EngageCredit.EmptyCardholder"));
            }
        }
        
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCardnumber() {
        return cardnumber;
    }

    public void setCardnumber(String cardnumber) {
        this.cardnumber = cardnumber;
    }

    public String getCardholder() {
        return cardholder;
    }

    public void setCardholder(String cardholder) {
        this.cardholder = cardholder;
    }

    public String getExpiration_month_card() {
        return expiration_month_card;
    }

    public void setExpiration_month_card(String expiration_month_card) {
        this.expiration_month_card = expiration_month_card;
    }

    public String getExpiration_year_card() {
        return expiration_year_card;
    }

    public void setExpiration_year_card(String expiration_year_card) {
        this.expiration_year_card = expiration_year_card;
    }

    public String getPromo_code() {
        return promo_code;
    }

    public void setPromo_code(String promo_code) {
        this.promo_code = promo_code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
   
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    } 
    
    public Resolution states() {        
        return new ForwardResolution("/WEB-INF/jsp/partial_states.jsp");
    }
    
    public Resolution cities() {        
        return new ForwardResolution("/WEB-INF/jsp/partial_cities.jsp");
    }
    
    public Collection<Country> getCountries() {
        return Countries.list();
    }

    public Collection<Product> getProducts() {
        return Products.list();
    }

    public String getCvv2() {
        return cvv2;
    }

    public void setCvv2(String cvv2) {
        this.cvv2 = cvv2;
    }      
    
    public Resolution save() {
        return guardar("/WEB-INF/jsp/engagecreditok.jsp");
    }
    
    public Resolution updatePendigState(){
        try {
            String order_id = getContext().getRequest().getParameter("order_id");
						
            connect();
            AtrilSession oSes = getSession();
            Dms oDms = oSes.getDms();
            order = new Order(oDms,order_id);
            order.put("status_number",Tpv.PENDIENTE_CONFIRMACION);
            Date dtNow = new Date();
            order.put("pay_date", dtNow);
			order.newTransaction();
			order.put("cardnumber", cardnumber);
			order.put("cardholder", cardholder);
			order.put("expiration_month_card", expiration_month_card);
			order.put("expiration_year_card", expiration_year_card);
			order.put("cvv2", cvv2);
            order.save(oSes);
            oSes.commit();
			HttpServletRequest request = getContext().getRequest();
			String peticion = "<tpv><oppago>"
					+ "<modelopago>"+Tpv.getModelopago()+"</modelopago>"					
                   + "<idterminal>"+Tpv.getIdterminal()+"</idterminal>"
                   + "<idcomercio>"+Tpv.getIdcomercio()+"</idcomercio>"
                   + "<idtransaccion>"+order.getTransaction()+"</idtransaccion>"
                   + "<mediopago>"+Tpv.getMediopago()+"</mediopago>"
                   + "<soporte>"+Tpv.getSoporte()+"</soporte>"
                   + "<canal>"+Tpv.getCanal()+"</canal>"
                   + "<moneda>"+Tpv.getMoneda()+"</moneda>"
                   + "<importe>"+order.getTotal_price()+"</importe>"
                   + "<numtarjeta>"+order.getCardnumber()+"</numtarjeta>"
                   + "<fechacaducidad>"+order.getExpiration_year_card()+order.getExpiration_month_card()+"</fechacaducidad>"
                   + "<cvv2>"+order.getCvv2()+"</cvv2>"
                   + "<urlcomercio>"+Tpv.getUrlcomercio(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath())+"</urlcomercio>"
                   + "<idioma>"+Tpv.getIdioma()+"</idioma>"
                   + "<pais>"+Tpv.getPais()+"</pais>"
                   + "<urlredir>"+Tpv.getUrlredir(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath())+"?engage=true&order_id="+order.getId()+"</urlredir>"
                   + "<localizador>"+order.getOrderId()+"</localizador>"
                   + "<firma>"+Tpv.getFirma(order.getTransaction(), order.getTotal_price(), order.getOrderId())+"</firma>"
                   + "</oppago></tpv>";
            return new JavaScriptResolution(peticion);
        } catch (StorageException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
    public Resolution guardar(String path) {
        try {

            connect();
            AtrilSession oSes = getSession();
            Dms oDms = oSes.getDms();
            CustomerAccount cacc = new CustomerAccount(oDms, getSessionAttribute("customer_account_docid"));
			oSes.commit();
			
            cacc.put("name_billing",getName());
            cacc.put("cif_billing",getCif());
            cacc.put("phone_billing",getPhone());
            cacc.put("mail_billing",getMail());
            cacc.put("address_billing",getAddress());
            cacc.put("postcode_billing",getPostcode());
            cacc.put("city_billing",getCity());
            cacc.put("state_billing",getState());
            cacc.put("country_billing",getCountry());
            cacc.save(oSes);
            oSes.commit();
			
            order = cacc.createOrder(oSes);
            order.put("user_id", getSessionAttribute("user_docid"));
            order.put("credits_bought", selectedProduct.getCredits());
            //order.save(oSes);

            Log.out.info("Created order " + order.getString("order_id") + " for customer " + getSessionAttribute("customer_acount"));

            order.put("promo_code",promo_code);
            order.put("name",name);
            order.put("cif",cif);
            order.put("phone",phone);
            order.put("mail",mail);
            order.put("address",address);
            order.put("city",city);
            order.put("state",state);
            order.put("country",country);
            order.put("postcode",postcode);
            order.put("cardnumber",cardnumber);
            order.put("cardholder",cardholder);
            order.put("expiration_month_card",expiration_month_card);
            order.put("expiration_year_card",expiration_year_card);
            order.put("cvv2",cvv2);

            order.save(oSes);
            commit();
            
            OrderLine line = order.addLine(oSes, selectedProduct);
			commit();
			
            Log.out.info("Added order line for product " + selectedProduct.getString("product_name"));

            order.put("base_price", line.getString("base_price"));
            order.put("taxes", line.getString("taxes"));
            order.put("taxespct", line.getString("taxespct"));
            order.put("total_price", line.getString("subtotal_price"));
            order.put("currency", line.getString("currency"));

            if (selectedProduct.getPrice().compareTo(BigDecimal.ZERO)==0) {
                order.put("status_number", Tpv.PAGADO);
                Long oCreditsLeft = new Long(cacc.getBigDecimal("credits_left").longValue());
                if (null == oCreditsLeft) {
                    oCreditsLeft = new Long(0l);
                }
                cacc.put("credits_left", new Long(oCreditsLeft.longValue() + selectedProduct.getCredits().longValue()));
                cacc.save(oSes);
				commit();
				
                Log.out.info("Added " + selectedProduct.getCredits().toString() + " credits to customer " + getSessionAttribute("customer_acount"));
                DAO.log(oSes, cacc.getDocument(), CustomerAccount.class, "UPDATE CREDITS", AtrilEvent.Level.INFO, cacc.getDocument().id() + ";" + cacc.get("credits_left"));

            } else {
                order.put("status_number",Tpv.PENDIENTE_PAGO);
            }
            
            order.save(oSes);
            commit();

            DAO.log(oSes, order.getDocument(), Order.class, "CREATE ORDER", AtrilEvent.Level.INFO, order.getDocument().id() + ";" + getSessionAttribute("customer_acount") + ";" + selectedProduct.getString("product_name"));

            commit();
            disconnect();
            return new ForwardResolution(path);
        } catch (Exception e) {
            Log.out.error("EngageCredit.save() "+e.getClass().getName()+" "+e.getMessage(), e);
            getContext().getMessages().add(new SimpleMessage("ERROR " + e.getMessage(), order));
            return new ForwardResolution("/WEB-INF/jsp/engagecredit.jsp");
        } finally {
            close();
        }
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
    
    /**
     * Obtener el número de créditos libres
     * @return Número de créditos libres
     */
    public String getCreditsleft() {
        try {
            connect();
            AtrilSession oSes = getSession();
            if(oSes!=null){
            Dms oDms = oSes.getDms();
            CustomerAccount cacc = new CustomerAccount(oDms, getSessionAttribute("customer_account_docid"));
            disconnect();
            return cacc.getCreditsLeft().toString().substring(0, cacc.getCreditsLeft().toString().indexOf("."));
            }
        } catch (StorageException ex) {
            Log.out.error("BaseActionBean.getCreditsleft() "+ex.getClass().getName()+" "+ex.getMessage(), ex);
        }
        return null;
    }
    
    
    /**
     * Obtiene el número de créditos usados
     * @return Número de créditos usados
     */
    public String getCreditsused() {
        try {
            connect();
            AtrilSession oSes = getSession();
            if(oSes!=null){
            Dms oDms = oSes.getDms();
            CustomerAccount cacc = new CustomerAccount(oDms, getSessionAttribute("customer_account_docid"));
            disconnect();
            return cacc.getCreditsUsed().toString().substring(0, cacc.getCreditsUsed().toString().indexOf("."));
            }
        } catch (StorageException ex) {
            Log.out.error("BaseActionBean.getCreditsused() "+ex.getClass().getName()+" "+ex.getMessage(), ex);
        }
        return null;
    }    
}
