<%@ page import="com.zesped.DAO,com.zesped.idl.ModelManager" language="java" session="false" contentType="text/plain;charset=UTF-8" %><%

out.write("Iniciando reconstruccion del metamodelo\n");
out.flush();

// new ModelManager().createDataModel();

out.write("Reconstruccion del metamodelo completada\n");
out.flush();

out.write("Iniciando reindexacion\n");
out.flush();

DAO.reindexAll();

out.write("Reindexacion completada.\n");
%>
