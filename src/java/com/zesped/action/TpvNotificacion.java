package com.zesped.action;

import com.zesped.model.CaptureService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

public class TpvNotificacion extends BaseActionBean {

    private String peticion;

    @DefaultHandler
    public Resolution notificacion() {
        return new ForwardResolution("/WEB-INF/jsp/tpvnotificacion.jsp");
    }

    public String getPeticion() {
        return peticion;
    }

    public void setPeticion(String peticion) {
        this.peticion = peticion;
    }

	@Override
	public CaptureService getCaptureService() {
		return null;
	}
    
    
}
