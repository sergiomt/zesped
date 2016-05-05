<%@ page import="java.util.Date,com.knowgate.misc.Calendar" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
			         <%
			           Date dtNow = new Date();
			           final int iYear = dtNow.getYear()+1900;
			           final int iMonth = dtNow.getMonth()+1;
			           int iYearStart, iYearEnd, iMonthStart,iMonthEnd;
			         %>
			         <stripes:select class="darkcombo" id="period" name="period">
							 <option value="" selected="selected">Cualquiera</option>
			         <%
			             iYearStart=iYearEnd=iYear;
			             iMonthStart=iMonthEnd=iMonth;
			         %><option value="<%=(iMonthStart<10 ? "0" : "")+String.valueOf(iMonthStart)+"/"+String.valueOf(iYearStart)+"-"+(iMonthEnd<10 ? "0" : "")+String.valueOf(iMonthEnd)+"/"+String.valueOf(iYearEnd)%>" ><%=Calendar.MonthName(iMonth-1,"es").toLowerCase()+" "+String.valueOf(iYear)%></option>			         
			         <%  for (int m=1; m<=3; m++) {
			               iYearStart=iYearEnd=iYear;
			               iMonthStart=iMonthEnd=iMonth-m;
			               if (iMonthStart<1) {
			            	   iMonthStart=iMonthEnd=12+iMonthStart;
			            	   iYearStart=iYearEnd=iYear-1;
			               }
			         %><option value="<%=(iMonthStart<10 ? "0" : "")+String.valueOf(iMonthStart)+"/"+String.valueOf(iYearStart)+"-"+(iMonthEnd<10 ? "0" : "")+String.valueOf(iMonthEnd)+"/"+String.valueOf(iYearEnd)%>"><%=Calendar.MonthName(iMonthStart-1,"es").toLowerCase()+" "+String.valueOf(iYearStart)%></option>
			         <% } //next
			         
			            iYearStart=iYearEnd=iYear;
			            if (iMonth<4) {
			            	iMonthStart=1;
			            	iMonthEnd=3;
			            } else if (iMonth<7) {
			            	iMonthStart=4;
			            	iMonthEnd=6;			            	
			            } else if (iMonth<10) {
			            	iMonthStart=7;
			            	iMonthEnd=9;			            	
			            } else {
			            	iMonthStart=10;
			            	iMonthEnd=12;			            	
			            }
			         %><option value="<%=(iMonthStart<10 ? "0" : "")+String.valueOf(iMonthStart)+"/"+String.valueOf(iYearStart)+"-"+(iMonthEnd<10 ? "0" : "")+String.valueOf(iMonthEnd)+"/"+String.valueOf(iYearEnd)%>">Este trimestre</option>
			         <%
			            if (iMonth<4) {
			            	iMonthStart=10;
			            	iMonthEnd=12;
			            	iYearStart=iYearEnd=iYear-1;
			            } else if (iMonth<7) {
			            	iMonthStart=1;
			            	iMonthEnd=3;			            	
			            } else if (iMonth<10) {
			            	iMonthStart=4;
			            	iMonthEnd=6;			            	
			            } else {
			            	iMonthStart=7;
			            	iMonthEnd=9;			            	
			            }
			         %><option value="<%=(iMonthStart<10 ? "0" : "")+String.valueOf(iMonthStart)+"/"+String.valueOf(iYearStart)+"-"+(iMonthEnd<10 ? "0" : "")+String.valueOf(iMonthEnd)+"/"+String.valueOf(iYearEnd)%>">Trimestre anterior</option>
			         <%
		             iYearStart=iYearEnd=iYear;
		             iMonthStart=1;
		             iMonthEnd=12;
			         %><option value="<%=(iMonthStart<10 ? "0" : "")+String.valueOf(iMonthStart)+"/"+String.valueOf(iYearStart)+"-"+(iMonthEnd<10 ? "0" : "")+String.valueOf(iMonthEnd)+"/"+String.valueOf(iYearEnd)%>">Este a&ntilde;o</option>
			         <%
		             iYearStart=iYearEnd=iYear-1;
		             iMonthStart=1;
		             iMonthEnd=12;
			         %><option value="<%=(iMonthStart<10 ? "0" : "")+String.valueOf(iMonthStart)+"/"+String.valueOf(iYearStart)+"-"+(iMonthEnd<10 ? "0" : "")+String.valueOf(iMonthEnd)+"/"+String.valueOf(iYearEnd)%>">A&ntilde;o anterior</option>
			           <option value="custom">Elegir rango de fechas</option>
			         </stripes:select>