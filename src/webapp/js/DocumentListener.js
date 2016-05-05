function DocumentListener(){
    this.userOnDocumentProcessed = null;
    this.setOnDocumentProcessed = setOnDocumentProcessed;
    this.eventOnDocumentProcessed = eventOnDocumentProcessed;
    
    this.userOnError = null;
    this.setOnError = setOnError;
    this.eventOnError = eventOnError;

    this.userOnServerError = null;
    this.setOnServerError = setOnServerError;
    this.eventOnServerError = eventOnServerError;

    this.userOnScannerInitialized = null;
    this.setOnScannerInitialized = setOnScannerInitialized;
    this.eventOnScannerInitialized = eventOnScannerInitialized;

    this.userOnUserActionNeeded = null;
    this.setOnUserActionNeeded = setOnUserActionNeeded;
    this.eventOnUserActionNeeded = eventOnUserActionNeeded;

    this.userOnFatalError = null;
    this.setOnFatalError = setOnFatalError;
    this.eventOnFatalError = eventOnFatalError;
    
    this.userOnDepositRolledBack = null;
    this.setOnDepositRolledBack = setOnDepositRolledBack;
    this.eventOnDepositRolledBack = eventOnDepositRolledBack;

    this.userOnDepositConfirmed = null;
    this.setOnDepositConfirmed = setOnDepositConfirmed;
    this.eventOnDepositConfirmed = eventOnDepositConfirmed;


    this.userOnViewCodeline = null;
    this.setOnViewCodeline = setOnViewCodeline;
    this.eventOnViewCodeline = eventOnViewCodeline;


    this.userOnPageProcessed = null;
    this.setOnPageProcessed = setOnPageProcessed;
    this.eventOnPageProcessed = eventOnPageProcessed;
    
    this.userOnStartDocumentProcess = null;
    this.setOnStartDocumentProcess = setOnStartDocumentProcess;
    this.eventOnStartDocumentProcess = eventOnStartDocumentProcess;
    this.userOnStartDocumentProcess = null;
    this.setOnStartDocumentProcess = setOnStartDocumentProcess;
    this.eventOnStartDocumentProcess = eventOnStartDocumentProcess;

    this.userOnImageReady = null;
    this.setOnImageReady = setOnImageReady;
    this.eventOnImageReady = eventOnImageReady;
}

function eventOnPageProcessed(documentId, pageId, pageNumber) {
    if (this.userOnPageProcessed != null) {
        userOnPageProcessed(documentId, pageId, pageNumber);
    }
}

function setOnPageProcessed(pageProcessedCallback) {
    userOnPageProcessed = pageProcessedCallback;
}

function eventOnDocumentProcessed(idDocumento, infoDocu) {
    if (this.userOnDocumentProcessed != null) {
        userOnDocumentProcessed(idDocumento, infoDocu);
    }
}

function setOnDocumentProcessed(metodoExterno){
    userOnDocumentProcessed=metodoExterno;
}

function eventOnError(needInitialization, error) {
    if (this.userOnError != null) {
        userOnError(needInitialization, error);
    }
}

function setOnError(metodoExterno){
    userOnError=metodoExterno;
}

function eventOnFatalError(error) {
    if (this.userOnFatalError != null) {
        userOnFatalError(error);
    }
}

function setOnFatalError(metodoExterno){
    userOnFatalError=metodoExterno;
}

function eventOnStartDocumentProcess() {
    if (this.userOnStartDocumentProcess != null) {
        userOnStartDocumentProcess();
    }
}

function setOnStartDocumentProcess(metodoExterno) {
    userOnStartDocumentProcess = metodoExterno;
}

function eventOnServerError(error) {
    if (this.userOnServerError != null) {
        userOnServerError(error);
    }
}

function setOnServerError(metodoExterno){
    userOnServerError=metodoExterno;
}

function eventOnUserActionNeeded(error){
    if (this.userOnUserActionNeeded != null) {
        userOnUserActionNeeded(error);
    }
}

function setOnUserActionNeeded(metodoExterno){
    userOnUserActionNeeded=metodoExterno;
}

function eventOnScannerInitialized() {
    if (this.userOnScannerInitialized != null) {
        userOnScannerInitialized();
    }
}

function setOnScannerInitialized(metodoExterno){
    userOnScannerInitialized=metodoExterno;
}

function eventOnDepositConfirmed(idDepositConfirmed) {
    if (this.userOnDepositConfirmed != null) {
        userOnDepositConfirmed(idDepositConfirmed);
    }
}

function setOnDepositConfirmed(metodoExterno){
    userOnDepositConfirmed=metodoExterno;
}

function eventOnDepositRolledBack(idDepositConfirmed) {
    if (this.userOnDepositRolledBack != null) {
        userOnDepositRolledBack(idDepositConfirmed);
    }
}

function setOnDepositRolledBack(metodoExterno){
    userOnDepositRolledBack=metodoExterno;
}

function eventOnViewCodeline(codeline) {
    if (this.userOnViewCodeline != null) {
        userOnViewCodeline(codeline);
    }
}

function setOnViewCodeline(metodoExterno){
    userOnViewCodeline=metodoExterno;
}

function eventOnStartDocumentProcess(){
    if (this.userOnStartDocumentProcess != null) {
        userOnStartDocumentProcess();
    }
}

function setOnStartDocumentProcess(metodoExterno){
    userOnStartDocumentProcess=metodoExterno;
}

function eventOnImageReady(observe, reverse){
    if (this.userOnImageReady != null) {
        userOnImageReady(observe, reverse);
    }
}

function setOnImageReady(onImageReadyCallback) {
    userOnImageReady = onImageReadyCallback;
}