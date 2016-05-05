<%@page import="es.ipsa.atril.doc.user.Document"%>
<%@page import="javax.xml.parsers.DocumentBuilder"%>
<%@page import="javax.xml.parsers.DocumentBuilderFactory"%>
<%@page import="es.ipsa.atril.eventLogger.AtrilEvent"%>
<%@page import="com.zesped.model.CustomerAccount"%>
<%@page import="com.zesped.model.Order"%>
<%@page import="es.ipsa.atril.doc.user.Dms"%>
<%@page import="com.zesped.DAO"%>
<%@page import="es.ipsa.atril.sec.authentication.AtrilSession"%>
<%@page import="com.zesped.Log"%>
<%@page import="com.zesped.idl.data.Tpv"%>
<%@page import="com.zesped.util.TpvbbvaUtils"%>
<%@page import="com.zesped.action.TpvNotificacion"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.text.SimpleDateFormat,java.util.*,java.net.*,java.io.*"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
	<%!
		Map getParametros(String peticion) {
			Map hash = new HashMap();
			try {
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
				InputStream is = new ByteArrayInputStream(peticion.getBytes());
				org.w3c.dom.Document doc = docBuilder.parse(is);
				LinkedList<org.w3c.dom.Element> elements = new LinkedList<org.w3c.dom.Element>();
				elements.add(doc.getDocumentElement());
				do {
					org.w3c.dom.Element element = elements.remove();
					org.w3c.dom.NodeList children = element.getChildNodes();
					for (int i = 0; i < children.getLength(); i++) {
						org.w3c.dom.Node node = children.item(i);
						hash.put(node.getNodeName(), node.getTextContent());
						if (node instanceof org.w3c.dom.Element) {
							elements.add((org.w3c.dom.Element) node);
						}
						//hash.put(children.item(i).getNodeName(),
					}
				} while (!elements.isEmpty());
				return hash;
			} catch (Exception ex) {
			}
			String total = peticion.replaceAll(">[ ]*<", "><");
			String clave = null;
			String valor = null;
			int index1 = 0;
			while ((index1 = total.indexOf("><")) != -1) {
				try {
					clave = total.substring(1, total.indexOf(">"));
					valor = total.substring(total.indexOf(">") + 1, total.indexOf("</"));
					hash.put((String) clave, (String) valor);
				} catch (Exception ex) {
				}
				total = total.substring(index1 + 1, total.length());
			}
			return hash;
		}
	%>
	<%
		//PARÁMETRO DE LA CONSULTA
		String ERROR = "Su operación no se puede realizar actualmente. Perdone las molestias.";
		String OK = "Su operación se ha procesado correctamente.";
		TpvNotificacion oAbn = (TpvNotificacion) request.getAttribute("actionBean");
		String peticion = oAbn.getPeticion();
		String idtransaccion = "-";
		String nombrecomercio = "";
		String estado = "";
		String codautorizacion = "";
		String fechahora = "";
		String importe = "";
		String moneda = "";
		String coderror = null;
		String deserror = null;
		String localizador = null;
		String firma_resp_tpv = null;
		String firma_calculada = null;
		String xmlRespuesta = "";

		String mensajePago = ERROR;
		boolean repudiable = false;
		com.zesped.Log.out.info("Tpvnotificacion: Parametro peticion -> " + peticion);
		//if ((peticion=request.getParameter("peticion"))!=null){
		if (peticion != null) {
			try {
				//      xmlRespuesta = request.getParameter("peticion");
				xmlRespuesta = peticion;
				Map hash = getParametros(xmlRespuesta);
				nombrecomercio = (String) hash.get("nombrecomercio");
				idtransaccion = (String) hash.get("idtransaccion");
				moneda = (String) hash.get("moneda");
				fechahora = (String) hash.get("fechahora");
				importe = (String) hash.get("importe");
				String importe_formateado = TpvbbvaUtils.replaceNoNum(importe);
				estado = (String) hash.get("estado");
				codautorizacion = (String) hash.get("codautorizacion");
				coderror = (String) hash.get("coderror");
				deserror = (String) hash.get("deserror");
				localizador = (String) hash.get("localizador");

				if (estado != null) {
					firma_resp_tpv = (String) hash.get("firma");
					String palabra_sec = TpvbbvaUtils.desofuscarPalabraSecreta(Tpv.getPal_sec_ofuscada(), Tpv.getClave_xor());
					firma_calculada = TpvbbvaUtils.getSHA1(Tpv.getIdterminal() + Tpv.getIdcomercio() + idtransaccion + importe_formateado + moneda + estado + coderror + codautorizacion + palabra_sec);

					///COMPARAMOS LA FIRMA CALCULADA A PARTIR DE LOS DATOS DE RESPUESTA DEL TPV
					///CON EL VALOR DEL CAMPO FIRMA QUE NOS LLEGA TAMBIÉN DEL TPV
					if (!firma_calculada.equals(firma_resp_tpv)) {
						com.zesped.Log.out.error("Tpvnotification: Las firmas no coinciden: ");
					} else {
						com.zesped.Log.out.info("Tpvnotification: Las firmas coinciden: ");


						com.zesped.Log.out.info("Tpvnotification: firma TPV --> " + firma_resp_tpv);
						com.zesped.Log.out.info("Tpvnotification: firma Calculada --> " + firma_calculada);

						if (estado.equals("2")) {
							/* if (coderror!=null && coderror.equals("099"))
							 {
							 log ("El Pago efectuado es repudiable.");
							 repudiable =true;
							 } */
							mensajePago = OK;
							coderror = null;

							Order oOrder;
							AtrilSession oSes = null;
							oSes = DAO.getAdminSession("Order.lines");
							Dms oDma = oSes.getDms();
							List<Document> oLst = oDma.query("Order$transaction='" + idtransaccion + "'");
							oOrder = new Order(oDma, oLst.get(0).id());
							if (oOrder.getBigDecimal("status_number").compareTo(Tpv.PAGADO) != 0) {
								oOrder.put("status_number", Tpv.PAGADO);
								oOrder.save(oSes);

								oLst = oDma.query("CustomerAccount$account_id='" + oOrder.get("customer_acount").toString() + "'");
								CustomerAccount cacc = new CustomerAccount(oDma, oLst.get(0).id());
								Long olong = cacc.getBigDecimal("credits_left").longValue();
								Long oCredits = oOrder.getBigDecimal("credits_bought").longValue();
								cacc.put("credits_left", olong + oCredits);
								cacc.save(oSes);
								oSes.commit();

								Log.out.info("Added " + oCredits.toString() + " credits to customer " + oOrder.get("customer_acount").toString());
								DAO.log(oSes, cacc.getDocument(), CustomerAccount.class, "UPDATE CREDITS", AtrilEvent.Level.INFO, cacc.getDocument().id() + ";" + cacc.get("credits_left"));

								com.zesped.Log.out.info("Tpvnotification: Pago realizado con éxito. Idtransacción:" + idtransaccion + ". Importe:" + importe + ".");
							} else {
								com.zesped.Log.out.info("Tpvnotification: Pago ya realizado anteriormente. Idtransacción:" + idtransaccion + ". Importe:" + importe + ".");
							}
						} else {
							if (!estado.equals("1")) {
								Order oOrder;
								AtrilSession oSes = null;
								oSes = DAO.getAdminSession("Order.lines");
								Dms oDma = oSes.getDms();
								List<Document> oLst = oDma.query("Order$transaction='" + idtransaccion + "'");
								oOrder = new Order(oDma, oLst.get(0).id());
								oOrder.put("status_number", Tpv.RECHAZADO);
								oOrder.save(oSes);
								oSes.commit();
								com.zesped.Log.out.info("Tpvnotification: Pago rechazado. Idtransacción:" + idtransaccion + ". Importe:" + importe + ".");
							}
						}
					}
				} else {
					com.zesped.Log.out.error("Tpvnotification: La peticion no ha sido procesada. No se valida la firma.");
					com.zesped.Log.out.error("Tpvnotification: Error. " + coderror + " - " + deserror);
				}
			} catch (Exception e) {
				com.zesped.Log.out.error("Tpvnotification: Error en la página de notificacion. " + e.getMessage());
				out.println("Error en la página de notificacion\n" + e);
			}
		}
	%>
	<body>
		<table width="629" border="0" cellpadding="1" cellspacing="0">
			<tr> 
				<td valign="top">
					<table width="95%" border="0" align="center" cellpadding="0" cellspacing="0" bgcolor="#990066">
						<tr>
							<td width="62%" class="Titulo"><font class="TituloSección">&nbsp;&nbsp;Resultado
								de su Compra</font></td>
						</tr>
					</table>
					<table width="95%" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor="#990099">
						<tr>
							<td height="19" valign="top"> <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor="#FFFFFF">
									<tr>

										<td width="100%"> <table width="100%" border="0" cellspacing="0" cellpadding="0">
												<tr align="center">
													<td width="100%" nowrap bgcolor="#F7DBEE" class="Cabecera-Datos">

														<p><br>
															<%=mensajePago%>
														<p><br>
															Los datos de su pedido son:
														<p><br>
														<table>
															<tr>
																<td class="Titulo" colspan="2">
																	<div align="center"><span class="TituloSección">&nbsp;&nbsp;Datos
																			del pedido</span></div>
																</td>
															</tr>
															<tr>
																<td class="Cabecera-Datos" nowrap>N&ordm; de Pedido:</td>
																<td width="60%"><%=idtransaccion%></td>
															</tr>
															<tr>
																<td class="Cabecera-Datos" nowrap>Importe de la Compra</td>
																<td width="60%"><%=importe%> </td>
															</tr>
															<%if (coderror != null) {%>
															<tr>
																<td class="Cabecera-Datos" nowrap>Error: </td>
																<td width="75%"><%=coderror%> - <%if (deserror != null) {%><%=deserror%><%}%></td>
															</tr>
															<%}%>				
															<%if (repudiable) {%>
															<tr>
																<td class="Cabecera-Datos" nowrap>Datos del
																	Pago </td>
																<td width="60%">Pago Repudiable</td>
															</tr>
															<%}%>
														</table>
														<p><br>
													</td>
												</tr>
											</table></td>
									</tr>
								</table></td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellspacing="5" align="center">
						<tr>
							<td align="center"> <a href="index.jsp">Volver</a> </td>
						</tr>
					</table>
					<br>
					<br>
				</td>
			</tr>
		</table>
	</body></html>
