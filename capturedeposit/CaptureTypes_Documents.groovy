tiposCaptura = dms.newDocument(captureTypes, dms.getRootDocument());
tiposCaptura.save("Contenedor de tipos de captura para el test");

atrilCaptureDemoVolume.addDocument(tiposCaptura);
atrilCaptureDemoVolume.save();

capture1 = dms.newDocument(captureType, tiposCaptura);
capture1.attribute("name").set("Digitalizacion_Herraduras");
capture1.attribute("description").set("Capture Type Digitalizacion Herraduras");
capture1.attribute("MimeType").set("image/jpeg");
capture1.save("Nuevo tipo de captura");

fieldsDocument = dms.newDocument(fields, capture1);
fieldsDocument.save("");

fieldDocument = dms.newDocument(field, fieldsDocument);
fieldDocument.attribute("level").set("1");
fieldDocument.attribute("document_type").set("Document");
fieldDocument.attribute("name").set("codeline");
fieldDocument.attribute("modifiable_by_scanner").set("true");
fieldDocument.attribute("modifiable_by_operator").set("false");
fieldDocument.attribute("modifiable_by_ocr").set("false");
fieldDocument.save("");

fieldDocument2 = dms.newDocument(field, fieldsDocument);
fieldDocument2.attribute("level").set("1");
fieldDocument2.attribute("document_type").set("Document");
fieldDocument2.attribute("name").set("endorsement");
fieldDocument2.attribute("modifiable_by_scanner").set("true");
fieldDocument2.attribute("modifiable_by_operator").set("false");
fieldDocument2.attribute("modifiable_by_ocr").set("false");
fieldDocument2.save("");

fieldDocument3 = dms.newDocument(field, fieldsDocument);
fieldDocument3.attribute("level").set("0");
fieldDocument3.attribute("document_type").set("Deposit");
fieldDocument3.attribute("name").set("endorsement");
fieldDocument3.attribute("modifiable_by_scanner").set("false");
fieldDocument3.attribute("modifiable_by_operator").set("false");
fieldDocument3.attribute("modifiable_by_ocr").set("false");
fieldDocument3.save("");

scannerTwain = dms.newDocument(scan, capture1);
scannerTwain.attribute("name").set("Twain");
scannerTwain.attribute("localizador").set("");
scannerTwain.save("");
scannerTwain.item().writeFromFile('./resources/TwainProperties.properties');
scannerTwain.item().mimeType("text/plain");
scannerTwain.save("Añadido fichero TwainProperties.properties");

deviceInformationTwain = dms.newDocument(deviceInformationType, dms.getRootDocument());
deviceInformationTwain.attribute("brand").set("TWAIN");
deviceInformationTwain.attribute("model").set("TWAIN");
deviceInformationTwain.attribute("serialnumber").set("");
deviceInformationTwain.save("");

