import es.ipsa.atril.doc.user.DataType;

depositsType = dms.newDocumentType("Deposits", dms.getRootType());
depositsType_number = depositsType.newAttributeType("number", DataType.STRING);
depositsType_number.setDescription("modificado nombre");
depositsType_holder = depositsType.newAttributeType("holder", DataType.STRING);
depositsType_currency = depositsType.newAttributeType("currency", DataType.STRING);
depositsType_disabled = depositsType.newAttributeType("disabled", DataType.NUMBER);
depositsType_disabled.setDefaultValue("0");
depositsType_disabled.setDescription("Indica si la cuenta está deshabilitada. Por defecto es 0(habilitada)");
depositsType.save();

depositType = dms.newDocumentType("Deposit", depositsType);
depositType_id = depositType.newAttributeType("deposit_id", DataType.STRING);
depositType_id.setDescription("identificador del deposito");
depositType_total_amount = depositType.newAttributeType("total_amount", DataType.NUMBER);
depositType_total_amount.setDefaultValue("0");
depositType_user = depositType.newAttributeType("user", DataType.STRING);
depositType_status = depositType.newAttributeType("status", DataType.STRING);
depositType_reference = depositType.newAttributeType("reference", DataType.STRING);
depositType_endorsement = depositType.newAttributeType("endorsement", DataType.STRING);
depositType_number_of_cheques = depositType.newAttributeType("number_of_cheques", DataType.NUMBER);
depositType_date_time_commit = depositType.newAttributeType("commitdate", DataType.DATE);
depositType_date_time_commit.setDescription("Fecha y hora en que se confirma un depósito en el proceso de digitalización");
depositType.save();

documentType = dms.newDocumentType("Document", depositType);
documentType_codeline = documentType.newAttributeType("codeline", DataType.STRING);
documentType_issue = documentType.newAttributeType("issue", DataType.STRING);
documentType_amount = documentType.newAttributeType("amount", DataType.NUMBER);
documentType_amount.setDescription("amount");
documentType_amount.setDefaultValue("0");
documentType_endorsement = documentType.newAttributeType("endorsement", DataType.STRING);
documentType_endorsement.setDescription("endoso Atril 4");
documentType_account = documentType.newAttributeType("cheque_account", DataType.STRING);
documentType_account.setDescription("cuenta del cheque (aparece en el codeline)");
documentType_number = documentType.newAttributeType("cheque_number", DataType.STRING);
documentType_number.setDescription("número de cheque (en el codeline)");
documentType_transit_route = documentType.newAttributeType("transit_route", DataType.STRING);
documentType_transit_route.setDescription("ruta y tránsito del cheque (aparece en el codeline)");
documentType_backoffice_status = documentType.newAttributeType("status_bo", DataType.STRING);
documentType_backoffice_status.setDescription("estado del cheque en el backoffice");
documentType_backoffice_status.setDefaultValue("Deposited");
documentType_incident_cause = documentType.newAttributeType("incident_cause", DataType.STRING);
documentType_incident_cause.setDescription("motivo de la incidencia en el backoffice");
documentType_observations = documentType.newAttributeType("observations", DataType.STRING);
documentType_observations.setDescription("observaciones del cheque");
documentType.save();

sideType = dms.newDocumentType("Side", documentType);
sideType.setDescription("side");
sideType.setItem(true);
sideType.expectedSize(maxFileSize);
sideType_page = sideType.newAttributeType("page", DataType.NUMBER);
sideType.save();

configurations = dms.newDocumentType("Configurations", dms.getRootType());
configurations.setDescription("Configuración del Sistema de depósito de cheques");
configurations.save();

endorsements = dms.newDocumentType("Endorsements", configurations);
endorsements.setDescription("Configuración de Endosos");
endorsements.save();

endorsement = dms.newDocumentType("Endorsement", endorsements);
endorsement.setDescription("Endosos para los cheques");
endorsement_endorse_id = endorsement.newAttributeType("endorsement_id", DataType.STRING);
endorsement_endorse_id.setDescription("Identificador del endoso");
endorsement_endorse_text = endorsement.newAttributeType("endorsement_text", DataType.STRING);
endorsement_endorse_text.setDescription("Literal endoso a imprimir físicamente en los cheques");
endorsement_endorsement_mask = endorsement.newAttributeType("endorsement_mask",DataType.STRING);
endorsement_endorsement_mask.setDescription("Máscara del endoso");
endorsement.save();

