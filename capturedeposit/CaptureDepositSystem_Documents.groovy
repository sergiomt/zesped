depositsTypeDocument = dms.newDocument(depositsType, dms.getRootDocument());
depositsTypeDocument.attribute("number").set("1");
depositsTypeDocument.attribute("holder").set("admin");
depositsTypeDocument.attribute("currency").set("EUR");
depositsTypeDocument.attribute("disabled").set("1");
depositsTypeDocument.save("Contenedor de depósitos");

atrilCaptureDemoVolume.addDocument(depositsTypeDocument);
atrilCaptureDemoVolume.save();

configurationsDocument = dms.newDocument(configurations, dms.getRootDocument());
configurationsDocument.save("");

endorsementsDocument = dms.newDocument(endorsements, configurationsDocument);
endorsementsDocument.save("");

endorsementDocument = dms.newDocument(endorsement, endorsementsDocument);
endorsementDocument.attribute("endorsement_id").set("01");
endorsementDocument.attribute("endorsement_text").set("Ipsa [date_dd/MM/yyyy] [Endorsement.endorsement_mask]");
endorsementDocument.attribute("endorsement_mask").set("JJJUUUSSS%03d");
endorsementDocument.save("");
