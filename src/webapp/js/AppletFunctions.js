var mapAmounts = null;
var tabIndex = 4;
var mapDocumentsId = null;

var listaImagenes = new Array();
var imagenesProcesadas = 0;
var documentosProcesados = 0;

var commited = false ;
var idCommitedDeposit ;
var idChequeToParent = "" ;
var confirmDeposit=false;

var REVERSE_CELL_INDEX_thumbnailsTable = 0;
var OBVERSE_CELL_INDEX_thumbnailsTable = 1;
var CHEQUE_INFO_CELL_INDEX_checkInfoTable = 2;

var MAXIMUM_AMOUNT_ALLOWED = 21474836.47;

var CHEQUE_WITHOUT_INCIDENCE = new Number(1);
var CHEQUE_WITH_INCIDENCE = new Number(0);

var checkBalance = 0;
var maxAllowedErrorInCodeline = -1;
var contadorCaracteresInvalidosCodeline = 0;
var contadorCaracteresValidosCodeline = 0;
var longitudCodeline = 0;
var duplicateCodelinesValidation = 0;

var rowDigitalizationTable = "";

var depositCancelled = "Deposit cancelled";
var depositCommitted = "Deposit committed";
var clientError = "Se ha producido un error: \n {0} \n<br>Solucione el problema y reinicie el escaner.";
var serverError = "Se ha producido un error en el servidor: \n{0} \n<br>Contacte con el administrador para solucionarlo.";
var scannerInitialized = "Escaner inicializado";
var deleteTheDocument = "¿Desea borrar el documento {0}?";
var errorDeleteDocument = "Ha ocurrido un error y no se ha eliminado el documento: \n{0}";
var errorTotalOfCheques = "El total de cheques es {0} pero se han digitalizado {1} correctamente. Reviselo antes de continuar";
var errorNumberOfChequesGreaterThanZero = "El total de cheques de la remesa ha de ser mayor que 0. Reviselo antes de continuar";
var errorAmountDocumentIsEmpty = "El importe del documento con indice {0} esta vacio. Reviselo antes de continuar";
var errorAmountChequeGreaterThanZero = "El importe del documento con indice {0} debe de ser mayor que cero. Reviselo antes de continuar";
var errorTotalAmount = "El Total del importe del documento esta vacio o con un valor erroneo. Reviselo antes de continuar";
var errorSumOfAmounts = "Existe un descuadre de {0} en el importe. Reviselo antes de continuar";
var descuadre = "Descuadre: ";
var operacionesPendientes = "Aun existen tareas pendientes. Espere a que finalicen para continuar.";
var rellenarCamposObligatorios = "Rellene antes el campo {0} para iniciar la captura de cheques";
var campoNumeroDeCheques = "Número de Cheques";
var campoImporteTotal = "Importe Total";
var order = "Orden:";
var amountCheque = "Importe:";
var marcaje = "Marcaje:";
var actualAmount = "Importe total actual:";
var codelineEmptyError = "Marcaje Inválido";
var maxAmountPerDayNotPostpone = "Ha superado el máximo importe por día y no podrá seguir digitalizando en el día de hoy.";
var maxAmountPerDepositer = "Ha superado el máximo importe por día, su depósito se procesará en el siguiente día. ¿Desea continuar de todas formas?";
var commitMessageExceedTimeLimit = "Se ha superado el límite de hora, el depósito se procesará en el siguiente día hábil. ¿Está seguro que desea confirmar el depósito?";
var marcajeDuplicado = "Marcaje Duplicado:";
var observations = "Observaciones:";
var currentcodeline = new String("");
var currentDocumentErrorMessage = new String("");

function enableButton(n) {
	var b = document.getElementById(n);
	b.disabled=false;
	b.className="submit";
}

function disableButton(n) {
	var b = document.getElementById(n);
	b.disabled=true;
	b.className="disabledsubmit";
}

function setCheckBalanceAmount(value) {
    checkBalance = value;
}

function setMaxAllowedErrorInCodeline(maxErrorsCodeline) {
    maxAllowedErrorInCodeline = maxErrorsCodeline;
}

function setDuplicateCodelinesValidation(duplicateCodelinesValidationValue) {
    duplicateCodelinesValidation = duplicateCodelinesValidationValue;
}

function setInitialButtonState() {
	enableButton("initButton");
	disableButton("NewDocButton");
	disableButton("scanButton");
	disableButton("stopButton");
}

function activateApplet(locale) {
	ajaxlog("Begin activateApplet("+locale+")");
    var applet = document.getElementById('atril-capture-applet');
	ajaxlog("applet.isActive()="+String(applet.isActive()));
    if (applet.isActive()) {
    	document.getElementById("error").innerHTML="";
    	document.getElementById("message").innerHTML="";
    	disableButton("initButton");
    	enableButton("NewDocButton");
    	disableButton("stopButton");
    	ajaxlog("applet.setLanguage("+locale+")");
        var language = applet.setLanguage(locale);
    	ajaxlog("applet.initScanner()");
        var opciones = applet.initScanner();
    } else {
        setTimeout(activateApplet(locale),5000);
    }
	ajaxlog("End activateApplet("+locale+")");
}

//---------------------------------------------------------------------------
//           PULSACION DE LOS BOTONES
//---------------------------------------------------------------------------

function setInitButtonClick(locale) {
	ajaxlog("Begin setInitButtonClick("+locale+")");
    try {
        activateApplet(locale);
        imagenesProcesadas = 0;
        listaImagenes.length = 0;
        documentosProcesados = 0;
        document.getElementById("initDiv").style.display="none";
        spinner.spin(document.getElementById("workDiv"));
    } catch(err) {
    	ajaxlog("Exception at setInitButtonClick "+err);
    	YUI().use("panel", function (Y) {
    	  Y.one('#alertsidebox .message').setHTML(err);
          Y.one('#alertsidebox .message').set('className', 'message dialog-stop');
          alertsidebox.show();
    	});
    }
	ajaxlog("End setInitButtonClick("+locale+")");
}

function setNewDocScanClick() {
	ajaxlog("Begin setNewDocScanClick()");
    var applet = document.getElementById('atril-capture-applet');
    try {
    	disableButton("scanButton");
        disableButton("initButton");
        disableButton("NewDocButton");
        disableButton("commitButton");
        disableButton("rollbackButton");
        disableButton("stopButton");
        // ajaxlog("applet.createNewDocument()");
        // applet.createNewDocument();
        ajaxlog("applet.scan()");
        applet.scan();
        // document.getElementById("stopDiv").style.display="block";
        document.getElementById("commitDiv").style.display="block";
        document.getElementById("rollbackDiv").style.display="block";
    } catch(err) {
    	ajaxlog("Exception at setNewDocScanClick "+err);
    	YUI().use("panel", function (Y) {
    	  Y.one('#alertsidebox .message').setHTML(err);
          Y.one('#alertsidebox .message').set('className', 'message dialog-stop');
          alertsidebox.show();
    	});
    }
	ajaxlog("End setNewDocScanClick()");
}

function setScanClick() {
	ajaxlog("Begin setScanClick()");
    var applet = document.getElementById('atril-capture-applet');
    try {
        disableButton("initButton");
        disableButton("scanButton");
        disableButton("NewDocButton");
        disableButton("commitButton");
        disableButton("rollbackButton");
    	document.getElementById("error").innerHTML="";
    	document.getElementById("message").innerHTML="";
        document.getElementById("scanDiv").style.display="none";
        document.getElementById("commitDiv").style.display="none";
        document.getElementById("rollbackDiv").style.display="none";
    	ajaxlog("applet.scan()");
        applet.scan();
        document.getElementById("scanDiv").style.display="block";
        document.getElementById("stopDiv").style.display="block";
        document.getElementById("commitDiv").style.display="block";
        document.getElementById("rollbackDiv").style.display="block";
    } catch(err) {
    	ajaxlog("Exception at setScanClick "+err);
    	YUI().use("panel", function (Y) {
          Y.one('#alertsidebox .message').setHTML(err);
          Y.one('#alertsidebox .message').set('className', 'message dialog-stop');
          alertsidebox.show();
    	});
    }
	ajaxlog("End setScanClick()");
}

function setRollbackClick(rollbackMessage) {
	ajaxlog("setRollbackClick("+rollbackMessage+","+document.getElementById('scanButton').disabled+")");
    if (!document.getElementById('scanButton').disabled) {
        var applet = document.getElementById('atril-capture-applet');
        var resp = window.confirm(rollbackMessage);
        if (resp) {
            try {
                disableButton("scanButton");
                disableButton("initButton");
                disableButton("NewDocButton");
                disableButton("stopButton");
                applet.rollback();
                document.getElementById("scanDiv").style.display="block";
                document.getElementById("commitDiv").style.display="none";
                document.getElementById("rollbackDiv").style.display="none";
                document.getElementById("stopDiv").style.display="none";
                // $("#message").html(depositCancelled);
                // deleteTable();
                imagenesProcesadas = 0;
                listaImagenes.length = 0;
                documentosProcesados = 0;
            } catch(err) {
            	YUI().use("panel", function (Y) {
            	  Y.one('#alertsidebox .message').setHTML(err);
                  Y.one('#alertsidebox .message').set('className', 'message dialog-stop');
                  alertsidebox.show();
            	});
            }
            return false;
        } else {
            return true;        	
        }
    } else {
    	YUI().use("panel", function (Y) {
    	  Y.one('#alertsidebox .message').setHTML(operacionesPendientes);
          Y.one('#alertsidebox .message').set('className', 'message dialog-stop');
          alertsidebox.show();
    	});
        return true;        	
    }
}

function setCommitClick(commitMessage,maxChequesDeposit,accountNumber,timeLimit,workingDays) {
	ajaxlog("Begin setCommitClick("+commitMessage+")");
    var applet = document.getElementById('atril-capture-applet');
        try {
            disableButton("initButton");
            disableButton("scanButton");
            disableButton("NewDocButton");
            disableButton("stopButton");
        	document.getElementById("error").innerHTML="";
        	document.getElementById("message").innerHTML="";
            applet.commit();            
            document.getElementById("scanDiv").style.display="block";
            document.getElementById("commitDiv").style.display="none";
            document.getElementById("rollbackDiv").style.display="none";
            document.getElementById("stopDiv").style.display="none";
            imagenesProcesadas = 0;
            listaImagenes.length = 0;
            documentosProcesados = 0;
        } catch(err) {
        	ajaxlog("Exception at setCommitClick "+err);
        	ajaxlog(err);
        	YUI().use("panel", function (Y) {
        	  Y.one('#alertsidebox .message').setHTML(err);
              Y.one('#alertsidebox .message').set('className', 'message dialog-stop');
              alertsidebox.show();
        	});
        }
    ajaxlog("End setCommitClick("+commitMessage+")");
}

function setStopClick() {
	ajaxlog("Begin setStopClick()");
    var applet = document.getElementById('atril-capture-applet');
    try {
        disableButton("initButton");
        enableButton("scanButton");
        enableButton("NewDocButton");
        disableButton("stopButton");
    	document.getElementById("error").innerHTML="";
    	document.getElementById("message").innerHTML="";
    	ajaxlog("applet.stopScanner()");
        applet.stopScanner();
        // comprobarCargaImagenes();
        document.getElementById("scanDiv").style.display="block";
        document.getElementById("commitDiv").style.display="block";
        document.getElementById("rollbackDiv").style.display="block";
        document.getElementById("initDiv").style.display="none";
        document.getElementById("stopDiv").style.display="none";
    } catch(err) {
    	ajaxlog("Exception at setStopClick "+err);
    	YUI().use("panel", function (Y) {
    	  Y.one('#alertsidebox .message').setHTML(err);
          Y.one('#alertsidebox .message').set('className', 'message dialog-stop');
          alertsidebox.show();
    	});
    }
	ajaxlog("End setStopClick()");
}

//---------------------------------------------------------------------------
//           CALLBACK DE LOS EVENTOS LANZADOS DESDE EL APPLET
//---------------------------------------------------------------------------


function imageReadyCallback(pathAnverso,pathReverso) {
	/*
    var applet = document.getElementById('atril-capture-applet');
    var table = document.getElementById("imageTable");
    var rowImageTable = table.insertRow(1);
    var cell = null;

    rowImageTable.insertCell(0);
    rowImageTable.insertCell(1);
    rowImageTable.insertCell(2);

    cell=rowImageTable.cells[0];
    cell.innerHTML = numImages;

    cell=rowImageTable.cells[1]; //Anverso
    cell.id = "anverso" + numImages;
    cell.width = 200;
    cell.height = 300;
    //cell.innerHTML = "<img src='data:image/jpeg;base64,"+applet.getBase64EncodedImage(pathAnverso)+"'/>";
    createNewImageItem("imageHostAnverso" + numImages);
    initTagImageItem("anverso"+numImages, "imageHostAnverso" + numImages, cell.width, cell.height, null, "default", 0, "image/jpeg");
    getImageItem("imageHostAnverso" + numImages).Paint(applet.getBase64EncodedImage(pathAnverso));

    cell=rowImageTable.cells[2]; //Reverso
    cell.id = "reverso" + numImages;
    cell.width = 200;
    cell.height = 300;
    //cell.innerHTML = "<img src='data:image/jpeg;base64,"+applet.getBase64EncodedImage(pathReverso)+"'/>";
    createNewImageItem("imageHostReverso" + numImages);
    initTagImageItem("reverso"+numImages, "imageHostReverso" + numImages, cell.width, cell.height, null, "default", 0, "image/jpeg");
    getImageItem("imageHostReverso" + numImages).Paint(applet.getBase64EncodedImage(pathReverso));
    
    //TABLA CLOUDCHECK
    cell = document.getElementById("front" + numImages);
    cell.width = 134;
    cell.height = 200;

    createNewImageItem("imageHostFront" + numImages);
    initTagImageItem("front" + numImages, "imageHostFront" + numImages, cell.width, cell.height, null, "default", 0, "image/jpeg");
    getImageItem("imageHostFront" + numImages).Paint(applet.getBase64EncodedImage(pathAnverso));

    cell = document.getElementById("back" + numImages);
    cell.width = 134;
    cell.height = 200;

    createNewImageItem("imageHostBack" + numImages);
    initTagImageItem("back" + numImages, "imageHostBack" + numImages, cell.width, cell.height, null, "default", 0, "image/jpeg");
    getImageItem("imageHostBack" + numImages).Paint(applet.getBase64EncodedImage(pathReverso));

    numImages++;
    */
}

function fatalErrorCallback(error) {
    window.alert("Reinicie el navegador");
}

function errorCallback(needInitialization, error) {
	
	ajaxlog("errorCallback "+format(clientError, error));

	spinner.stop();

    if (needInitialization) {
    	enableButton("initButton");
        disableButton("scanButton");
        disableButton("NewDocButton");
    } else {
    	disableButton("initButton");
        enableButton("NewDocButton");
        enableButton("scanButton");
    }
	disableButton("stopButton");
	YUI().use("panel", function (Y) {
      Y.one('#alertsidebox .message').setHTML(format(clientError, error));
      Y.one('#alertsidebox .message').set('className', 'message dialog-stop');
      alertsidebox.show();
	});
}

function startDocumentProcessCallback() {
	ajaxlog("Begin startDocumentProcessCallback");
    var applet = document.getElementById('atril-capture-applet');
	ajaxlog("applet.endorserOff()");
    applet.endorserOff();
	ajaxlog("End startDocumentProcessCallback");
}

function serverErrorCallback(error) {
	ajaxlog("serverErrorCallback "+format(serverError, error));
	spinner.stop();
	disableButton("initButton");
    disableButton("scanButton");
    disableButton("NewDocButton");
    disableButton("stopButton");
	YUI().use("panel", function (Y) {
      Y.one('#alertsidebox .message').setHTML(format(serverError, error));
      Y.one('#alertsidebox .message').set('className', 'message dialog-stop');
      alertsidebox.show();
	});
}

function scannerInitializedCallback() {
	ajaxlog("scannerInitializedCallback "+scannerInitialized);
	spinner.stop();
	disableButton("initButton");
    enableButton("scanButton");
    disableButton("NewDocButton");
    disableButton("stopButton");
    document.getElementById("scanDiv").style.display="block";
}

function userActionNeededCallback(error) {
    var mensaje = error;
    if (mensaje!="No documents in the feeder") {
    	ajaxlog("userActionNeededCallback "+mensaje);
    	disableButton("initButton");
    	disableButton("stopButton");
        enableButton("NewDocButton");
    	YUI().use("panel", function (Y) {
          Y.one('#alertsidebox .message').setHTML(mensaje);
          Y.one('#alertsidebox .message').set('className', 'message dialog-stop');
          alertsidebox.show();
    	});    	
    }
}

function depositConfirmedCallback(idDeposit) {
	ajaxlog("depositConfirmedCallback "+idDeposit);
	document.forms["depositData"].depositId.value = idDeposit;
}

function setExitClick() {

}

function depositRolledBackCallback(idDepositRolledBack){
	ajaxlog("depositRolledBackCallback "+idDepositRolledBack);
	document.forms["depositData"].depositId.value = "";
	enableButton("scanButton");
}

function depositServerValidations(idDeposit,applet) {
}

function viewCodelineCallback(codeline) {
	document.getElementById("message").innerHTML=codeline;
}

function documentBeginProcesCallback() {
	ajaxlog("Begin documentBeginProcesCallback");
    var applet = document.getElementById('atril-capture-applet');
	ajaxlog("applet.endorserOff()");
    applet.endorserOff();
	ajaxlog("End documentBeginProcesCallback");
}

function documentProcessedCallback(documentId, infoDocu) {
	ajaxlog("documentProcessedCallback "+documentId);

}

function pageProcessedCallback(documentId, pageId, pageNumber) {
	ajaxlog("pageProcessedCallback "+pageId);
	pagecount++;
	document.getElementById("pagecounter").innerHTML=String(pagecount);
    enableButton("scanButton");
	enableButton("stopButton");
	enableButton("commitButton");
	enableButton("rollbackButton");
}

//---------------------------------------------------------------------------
//                  GENERACION DE LA TABLA DE IMAGENES
//---------------------------------------------------------------------------
function addRowToImagesTable() {
    var table = document.getElementById("digitalizationTable");
    rowDigitalizationTable = table.insertRow(0);
    var cell = null;

    // rowDigitalizationTable.className = "thumbnailRow";

    rowDigitalizationTable.insertCell(REVERSE_CELL_INDEX_thumbnailsTable);
    cell=rowDigitalizationTable.cells[REVERSE_CELL_INDEX_thumbnailsTable];
    // cell.onMouseOut = "className='thumbnailRow'";
    // cell.onMouseOver = "className='thumbnailRowOver'";    
    // cell.className = "reverseThumbnailCell";

    rowDigitalizationTable.insertCell(OBVERSE_CELL_INDEX_thumbnailsTable);
    cell = rowDigitalizationTable.cells[OBVERSE_CELL_INDEX_thumbnailsTable];
    // cell.onMouseOut = "className='thumbnailRow'";
    // cell.onMouseOver = "className='thumbnailRowOver'";    
    // cell.className = "obverseThumbnailCell";

    documentosProcesados++;
}

//---------------------------------------------------------------------------
//         GENERACION DE LA TABLA DE INFORMACION DEL CHEQUE
//---------------------------------------------------------------------------

function addRowToChequeInfoTable(documentId, infoDocu) {
    var cellData = rowDigitalizationTable.insertCell(CHEQUE_INFO_CELL_INDEX_checkInfoTable);
    cellData.className="infoCheck_toDeposti_Cell";

    cellData.innerHTML = returnFieldSetInfo(infoDocu,documentId);
    
    //AsignarOnClickABotonDelete(cellData);
   
    asignarTabIndexElementosTabla();
    var Field = document.getElementById(documentId);
    setFieldFocus(Field);
}

function AsignarOnClickABotonDelete(cellData) {
    cellData.childNodes[0].childNodes[cellData.childNodes[0].childNodes.length-1].onclick = deleteRow;
}

function returnFieldSetInfo(infoDocu, documentId) {
    var table = document.getElementById("digitalizationTable");
    infoValidatedDocument = addAttibutesJSON(infoDocu);
    infoValidatedDocument.documentId = documentId;
    chequeAttributes = infoValidatedDocument.paragraphs;

    var paragraphs = addIdDocument(documentId) +
        addCurrentCodeline(infoValidatedDocument) +
        addOrderNumber() +
        addAmount(infoValidatedDocument) +
        chequeAttributes +
        addObservations();
    //+ addTrashButton(table);
    return "<fieldset class='chequeInformationFrame'>" + paragraphs + "</fieldset>";
}

function addIdDocument(documentId) {
    var paragraphs = "<input type='hidden' value='" + documentId + "'/>";

    return paragraphs;
}

function addOrderNumber() {
    var paragraphs = "<p><span class='fieldName'>" + order + "  " + "</span>";
    paragraphs = paragraphs + "<span>" + documentosProcesados + "</span></p>";
   
    return paragraphs;
}

function addAmount(infoValidatedDocument) {
    if((checkBalance) && (esDocumentoSinIncidencia(infoValidatedDocument))){
        var paragraphs = "<p><span class='fieldName'>" + amountCheque + "  " + "</span>";
        paragraphs = paragraphs + "<span><input id='" + infoValidatedDocument.documentId;
        paragraphs = paragraphs + "' type='textbox' class='currency' value='0.00'";
        paragraphs = paragraphs + " onblur=\"getActualAmount(); return validarImporteMaximoNoExcedido('" + infoValidatedDocument.documentId + "')\"" ;     
        paragraphs = paragraphs + " onfocus='this.select()'  onKeyPress='return validarTeclaPulsadaEsImporte(event)'/>";

    }else{
        var paragraphs = "<p><span class='fieldName' style='visibility:hidden'>" + amountCheque + "  " + "</span>";
        paragraphs = paragraphs + "<span><input id='" + infoValidatedDocument.documentId;
        paragraphs = paragraphs + "' type='textbox' class='currency' value='0.00'";
        paragraphs = paragraphs + " style='visibility:hidden' ";
    }
    
    paragraphs = paragraphs + "</span></p>";  
    return paragraphs;     
}

function addAttibutesJSON(infoDocu) {
    var obj = jQuery.parseJSON(infoDocu);
    var paragraphs = "";
    var currentcodeline = "";
    var documentDoesNotHaveIncidence = CHEQUE_WITHOUT_INCIDENCE;
    
    $.each(obj, function(key, value) {
        currentDocumentErrorMessage = "";
        var resp = verificarSiKeyEsCodeline(key);
        if(resp) {
            value = limpiarControlCharactersCodeLine(value);
            currentcodeline = value;
            if (esMarcajeValido()){
                if (esMarcajeDuplicado(value)) {
                    documentDoesNotHaveIncidence = CHEQUE_WITH_INCIDENCE;
                    currentDocumentErrorMessage = marcajeDuplicado;
                }
                paragraphs = paragraphs + "<p><span class='fieldName'>" + marcaje + " </span><span>" + value + "</span></p>";
            } else {
                paragraphs = paragraphs + "<p><span class='fieldName'>" + marcaje + " </span><span>" + value + "</span></p>";
                currentDocumentErrorMessage = codelineEmptyError;
                documentDoesNotHaveIncidence = CHEQUE_WITH_INCIDENCE;
            }   
        }
    });

    return new InfoValidatedDocument("", currentcodeline, documentDoesNotHaveIncidence, paragraphs);
}

function addObservations() {
    var paragraphs = "";
    if (currentDocumentErrorMessage.length == 0) {
        paragraphs = "<p><span class='fieldName'>" + " " + "</span>";
        paragraphs = paragraphs + "<span style='color:red;'>" + " " + "</span></p>";
    } else {
        paragraphs = "<p><span class='fieldName'>" + observations + "</span>";
        paragraphs = paragraphs + "<span style='color:red;'>" + " " + currentDocumentErrorMessage + "</span></p>";
    }
    return paragraphs;
}


function addTrashButton(table){
    var trashId = "a" + table.rows.length;
    var paragraphs = "<a class='trash' id='" + trashId + "' href='#'><span class='borrar'>borrar</span></a>";

    return paragraphs;
}

//---------------------------------------------------------------------------
//                  METODOS DE VALIDACION
//---------------------------------------------------------------------------
function validateReference() {
}

function validateTotalItems() {
    return true;
}

function validateMaxAmountPerDayPerCompanyPerDepositor(accountNumber,maxChequesDeposit,commitMessage){
}

function validateDepositTimeLimit(accountNumber,maxChequesDeposit,commitMessage,timeLimit,workingDays){
}

function validateMaxChequesDeposit(maxChequesDeposit){
}

function getNumberFromCurrency(currency) {
    return currency.asNumber({
        parseType: 'float'
    });
}

function validateValueOfTotalAmount(){
    return false;
}

function validateTotalAmount() {

}

//---------------------------------------------------------------------------
//                  METODOS DE PROPOSITO GENERAL
//---------------------------------------------------------------------------
function getRow(documentId) {
    var table = document.getElementById("digitalizationTable");
    var row = null;
    for (var i = 0; i < table.rows.length; i++) {
        row = table.rows[i];
        if (row.cells[CHEQUE_INFO_CELL_INDEX_checkInfoTable].childNodes[0].childNodes[0].value == documentId) {
            return i;
        }
    }
    return -1;
}

function insertObservation(documentId, observationText) {
    var table = document.getElementById("digitalizationTable");
    var row = null;
    for (var i = 0; i < table.rows.length; i++) {
        row = table.rows[i];
        if (row.cells[CHEQUE_INFO_CELL_INDEX_checkInfoTable].childNodes[0].childNodes[0].value == documentId) {
            row.cells[CHEQUE_INFO_CELL_INDEX_checkInfoTable].childNodes[0].childNodes[1].value = CHEQUE_WITH_INCIDENCE;
            row.cells[CHEQUE_INFO_CELL_INDEX_checkInfoTable].childNodes[0].childNodes[6].childNodes[0].innerHTML = observations;
            row.cells[CHEQUE_INFO_CELL_INDEX_checkInfoTable].childNodes[0].childNodes[6].childNodes[1].innerHTML = observationText;
            row.cells[CHEQUE_INFO_CELL_INDEX_checkInfoTable].childNodes[0].childNodes[4].childNodes[1].innerHTML = " ";
            mapAmounts["A" + documentId] = 0;
        }
    }
}

function setFieldFocus(field) {
    field.focus();
    field.select();
}

function isNumeric(sText)
{
    var validChars = "0123456789.";
    var isNumber=true;
    var charac;

    for (i = 0; i < sText.length && isNumber == true; i++){
        charac = sText.charAt(i);
        if (validChars.indexOf(charac) == -1) {
            isNumber = false;
        }
    }
    return isNumber;
}

function convertArrayToJSON() {
    return JSON.stringify(mapAmounts);
}

function addAmounts() {
    var accumulatedAmount = 0;

    for(var clave in mapAmounts) {
        accumulatedAmount += parseFloat(mapAmounts[clave]);
    }
    return accumulatedAmount;
}

function asignarTabIndexElementosTabla() {
    var idDoc;
    var textBox;

    var table = document.getElementById('digitalizationTable');
    for (var i = 0; i < table.rows.length; i++) {
        idDoc = table.rows[i].childNodes[CHEQUE_INFO_CELL_INDEX_checkInfoTable].childNodes[0].childNodes[0].value;
        textBox = document.getElementById(idDoc);
        if (textBox != null) {
            textBox.tabIndex = i + 5;
        }
    }
}

function comprobarCargaImagenes() {
    var resultado = true;
    var imageName = "";

    for (var i = 0; i < listaImagenes.length;i++) {
        imageName = listaImagenes[i];
        if (imageName != "") {
            resultado = document.images[imageName].complete;
            if (!resultado) {
                i = document.images.length;
            }
        }
    }
    if (resultado == false) {
        setTimeout("comprobarCargaImagenes()", 500);
    } else {
        imagenesProcesadas = 0;
        listaImagenes.length = 0;
        
        $("#scanButton").attr("disabled", "");
        $("#message").html("");
    }
}

function verificarSiKeyEsCodeline(keyCodeline) {

    if(keyCodeline == "marcaje" || keyCodeline == "codeline") {
        return true;
    }
    return false;
}

function esMarcajeValido() {
    return true;
}

function deleteTable() {
    var table_digitalizationTable = document.getElementById('digitalizationTable');
    var rowDigitalizationTable = null;

    for(var i = table_digitalizationTable.rows.length; i > 0; i--) {
        rowDigitalizationTable = table_digitalizationTable.rows[i-1];
        deleteRowFromTable(rowDigitalizationTable);
    }
    tabIndex = 5;
}

function deleteRow() {
    var rowCheckInfoTable = this.parentNode.parentNode.parentNode;
    var table = document.getElementById("digitalizationTable");
    var rowThumbnailsTable = table.rows[rowCheckInfoTable.rowIndex];

    if (rowThumbnailsTable.cells[OBVERSE_CELL_INDEX_thumbnailsTable].innerHTML != "" && rowThumbnailsTable.cells[REVERSE_CELL_INDEX_thumbnailsTable].innerHTML != "") {
        confirmDeleteRowWindow(rowThumbnailsTable);
    }
}



function deleteRowFromTable(rowDigitalizationTable) {
    var idRow = rowDigitalizationTable.rowIndex;
    var arrayGetValue = false;
    var arraySetValue = false;

    deleteImagesInListaImagenes(rowDigitalizationTable);
    
    var table_digitalizationTable = document.getElementById('digitalizationTable');
    table_digitalizationTable.deleteRow(idRow);
    
    if(checkBalance){
        getActualAmount();
    }
}

function deleteImagesInListaImagenes(row) {

    var idImagen = row.cells[OBVERSE_CELL_INDEX_thumbnailsTable].childNodes[0].id;
    var index = jQuery.inArray(idImagen, listaImagenes);
    if (index != -1) {
        listaImagenes[index] = "";
    }
    idImagen = row.cells[REVERSE_CELL_INDEX_thumbnailsTable].childNodes[0].id;
    index = jQuery.inArray(idImagen, listaImagenes);
    if (index != -1) {
        listaImagenes[index] = "";
    }
}

function validarTeclaPulsadaEsNumerico(event) {
    var charcode;
    
    if (navigator.appName=="Netscape") {
        charcode = event.which;
        if (charcode == 0) { //Tecla especial
            charcode = event.keyCode;
            return validarTeclaExpecial(charcode);
        }
    } else {
        charcode = event.keyCode;
    }
    
    return validarTeclaCampoNumerico(charcode);
}  

function validarTeclaPulsadaEsImporte(event) {
    var charcode;
    
    if (navigator.appName == "Netscape") {
        charcode = event.which;
        if (charcode == 0){ //Tecla especial
            charcode = event.keyCode;
            return validarTeclaExpecial(charcode);
        }
    } else {
        charcode = event.keyCode;
    }
    
    return validarTeclaCampoImporte(charcode);
} 

function validarTeclaCampoNumerico(keycode) {
    if ((keycode == 8) || (keycode == 13) || (keycode > 47 && keycode < 58)) {
        return true;
    } else {
        return false;
    }
}

function validarTeclaCampoImporte(keycode) {
    if ((keycode == 8) || (keycode == 13) || (keycode == 44) || (keycode == 46) ||
        (keycode > 47 && keycode < 58)) {
        return true;
    } else {
        return false;
    }
}

function validarTeclaExpecial(keycode) {
    if ((keycode == 9) || (keycode == 37) || (keycode == 39) || (keycode == 46)) {
        return true;
    } else {
        return false;
    }
}

function convertArrayToJSONId() {
    return JSON.stringify(mapDocumentsId);
}

function addCurrentCodeline(infoValidatedDocument) {
    var paragraphs = "<input type='hidden' value='" + infoValidatedDocument.codeline + "'/>";
    return paragraphs;
}

function limpiarControlCharactersCodeLine(codeline) {
    var cleanCodeline = "";

    longitudCodeline = codeline.length;
    for (i = 0; i < codeline.length; i++) {
        if(esCaracteresValidosCodeline(codeline.charAt(i)) || esCaracteresControlCodeline(codeline.charAt(i))){
            cleanCodeline += (codeline.charAt(i));
        }
        else {
            cleanCodeline += " ";
        }
    }

    return cleanCodeline;
}

function esMarcajeDuplicado(codeline) {
    return false;
}

function esCaracteresValidosCodeline(caracter) {
    if(caracter >= "0" && caracter <= "9"){
        contadorCaracteresValidosCodeline += 1;
        return true;
    }
    return false;
}

function esCaracteresControlCodeline(caracter) {
    if(caracter == "!" || caracter == "@" || caracter == "?"){
        contadorCaracteresInvalidosCodeline += 1;
        return true;
    }
    return false;
}

function InfoValidatedDocument(documentId,
codeline,
documentWithoutIncidence,
paragraphs) {

    this.documentId = documentId;
    this.codeline = codeline;
    this.documentWithoutIncidence = documentWithoutIncidence;
    this.paragraphs = paragraphs;
}