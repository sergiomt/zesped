import es.ipsa.atril.doc.user.DataType;

captureTypes = dms.newDocumentType("CaptureTypes", dms.getRootType());
captureTypes.save();

captureType = dms.newDocumentType("CaptureType", captureTypes);
captureType_name = captureType.newAttributeType("name", DataType.STRING);
captureType_mode = captureType.newAttributeType("MultiSheetDocument", DataType.NUMBER);
captureType_mode.setDefaultValue("0");
captureType_description = captureType.newAttributeType("description", DataType.STRING);
captureType_MultiPageItem = captureType.newAttributeType("MultiPageItem", DataType.NUMBER);
captureType_MultiPageItem.setDefaultValue("0");
captureType_MimeType = captureType.newAttributeType("MimeType", DataType.STRING);
captureType_MimeType.setDefaultValue("");
captureType_SignItem = captureType.newAttributeType("Sign", DataType.NUMBER);
captureType_SignItem.setDefaultValue("0");
captureType_SignItem = captureType.newAttributeType("SignInClient", DataType.NUMBER);
captureType_SignItem.setDefaultValue("0");
captureType_SignItem = captureType.newAttributeType("SignInServer", DataType.NUMBER);
captureType_SignItem.setDefaultValue("0");
captureType_IsPDFA = captureType.newAttributeType("IsPDFA", DataType.NUMBER);
captureType_IsPDFA.setDefaultValue("0");
captureType_XMPMetadataStructure = captureType.newAttributeType("XMPMetadataStructure", DataType.STRING);
captureType_XMPMetadataStructure.setDefaultValue("");
captureType_BitsDepth = captureType.newAttributeType("bitsDepth", DataType.NUMBER);
captureType_BitsDepth.setDefaultValue("0");
captureType.save();

scan = dms.newDocumentType("Scanner", captureType);
scan.setDescription("scanner");
scan.setItem(true);
scan.expectedSize(maxFileSize);
scan_name = scan.newAttributeType("name", DataType.STRING);
scan_localizador = scan.newAttributeType("localizador", DataType.STRING);
scan.save();

fields = dms.newDocumentType("Fields", captureType);
fields.save();

field = dms.newDocumentType("Field", fields);
field_level = field.newAttributeType("level", DataType.NUMBER);
field_document_type = field.newAttributeType("document_type", DataType.STRING);
field_name = field.newAttributeType("name", DataType.STRING);
field_modifiable_by_scanner = field.newAttributeType("modifiable_by_scanner", DataType.STRING);
field_modifiable_by_operator = field.newAttributeType("modifiable_by_operator", DataType.STRING);
field_modifiable_by_ocr = field.newAttributeType("modifiable_by_ocr", DataType.STRING);
field.save();

deviceInformationType = dms.newDocumentType("DeviceInformationType", dms.getRootType());
deviceInformationType_brand = deviceInformationType.newAttributeType("brand", DataType.STRING);
deviceInformationType_model = deviceInformationType.newAttributeType("model", DataType.STRING);
deviceInformationType_serialnumber = deviceInformationType.newAttributeType("serialnumber", DataType.STRING);
deviceInformationType.save();

