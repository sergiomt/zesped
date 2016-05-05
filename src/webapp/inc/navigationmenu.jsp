<%@ page import="com.zesped.model.CaptureService" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@ include file="atril.jspf" %><%

final CaptureService oCapSrv = oActionBean.getCaptureService();
String sAppBase;
if (oCapSrv!=null)
  if (oCapSrv.equals(CaptureService.INVOICES))
	  sAppBase="Invoice";
  else if (oCapSrv.equals(CaptureService.BILLNOTES))
    sAppBase="BillNote";
  else
	  sAppBase="Invoice";
else
	sAppBase="Invoice";

%><table width="100%" summary="Workflow">
  <tr>
    <td id="workflow_capture"><a id="wf_capture" class="workflow_caption" href="<%="Capture"+sAppBase+".action"%>"><% out.write (StripesResources.getString("workflow.capture")); %></a></td>
    <td id="workflow_enterdata"><a id="workflow_caption wf_enterdata" class="workflow_caption" href="<%="ListNew"+sAppBase+"s.action"%>"><% out.write (StripesResources.getString("workflow.enterdata")); %></a></td>
    <td id="workflow_approve"><a id="wf_approve" class="workflow_caption" href="<%="ListPending"+sAppBase+"s.action"%>">
    <% out.write (sAppBase.equals("Invoice") ? StripesResources.getString("workflow.approve") : StripesResources.getString("workflow.settle")); %></a></td>
    <td id="workflow_cloud"><div id="appname"><%
      if (oCapSrv!=null)
        if (oCapSrv.equals(CaptureService.INVOICES))
          out.write(StripesResources.getString("workflow.invoices"));
        else if (oCapSrv.equals(CaptureService.BILLNOTES))
          out.write(StripesResources.getString("workflow.billnotes"));
    %></div></td>
    <td id="workflow_manage"><a id="wf_manage" class="workflow_caption" href="<%="ListApproved"+sAppBase+"s.action"%>"><% out.write (StripesResources.getString("workflow.manage")); %></a></td>
  </tr>
</table>
