/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zesped.action;

import com.zesped.model.CaptureService;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 *
 * @author Sergio
 */
public class EngageCreditOK extends BaseActionBean{

    private static final String FORM="/WEB-INF/jsp/engagecreditok.jsp";
    
    @Override
    public CaptureService getCaptureService() {
        return null;
    }
    
    @DefaultHandler
    public Resolution form() {
        return new ForwardResolution(FORM);
    }
    
}
