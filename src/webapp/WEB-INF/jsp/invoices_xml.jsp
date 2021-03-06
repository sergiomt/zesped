<%@ page import="com.zesped.Log,com.zesped.DAO,com.zesped.model.Invoice,com.zesped.model.QueryResultSet,es.ipsa.atril.sec.authentication.AtrilSession,es.ipsa.atril.doc.user.Dms,es.ipsa.atril.doc.user.Document,es.ipsa.atril.doc.user.Item" language="java" session="true" contentType="text/xml" %><?xml version="1.0" encoding="UTF-8"?>
<namespace:Facturae xmlns:namespace="http://www.facturae.es/Facturae/2007/v3.0/Facturae" xmlns:namespace2="http://uri.etsi.org/01903/v1.2.2#" xmlns:namespace3="http://www.w3.org/2000/09/xmldsig#"><FileHeader><SchemaVersion>3.0</SchemaVersion><Modality>I</Modality><InvoiceIssuerType>EM</InvoiceIssuerType><Batch><BatchIdentifier>A0952171710</BatchIdentifier><InvoicesCount>1</InvoicesCount><TotalInvoicesAmount><TotalAmount>266.80</TotalAmount></TotalInvoicesAmount><TotalOutstandingAmount><TotalAmount>266.80</TotalAmount></TotalOutstandingAmount><TotalExecutableAmount><TotalAmount>266.80</TotalAmount></TotalExecutableAmount><InvoiceCurrencyCode>EUR</InvoiceCurrencyCode></Batch></FileHeader><Parties><SellerParty><TaxIdentification><PersonTypeCode>F</PersonTypeCode><ResidenceTypeCode>R</ResidenceTypeCode><TaxIdentificationNumber>A09521717</TaxIdentificationNumber></TaxIdentification><Individual><Name>DORADO</Name><FirstSurname/><SecondSurname/><AddressInSpain><Address>Calle Atocha 22</Address><PostCode>12345</PostCode><Town>Madrid</Town><Province>12</Province><CountryCode>ESP</CountryCode></AddressInSpain></Individual></SellerParty><BuyerParty><TaxIdentification><PersonTypeCode>J</PersonTypeCode><ResidenceTypeCode>R</ResidenceTypeCode><TaxIdentificationNumber>Q2826000H</TaxIdentificationNumber></TaxIdentification><LegalEntity><CorporateName>19054</CorporateName><AddressInSpain><Address>Calle Sta Maria Magdalena</Address><PostCode>28016</PostCode><Town>Madrid</Town><Province>Madrid</Province><CountryCode>ESP</CountryCode></AddressInSpain></LegalEntity></BuyerParty></Parties><Invoices><Invoice><InvoiceHeader><InvoiceNumber>10</InvoiceNumber><InvoiceSeriesCode/><InvoiceDocumentType>FC</InvoiceDocumentType><InvoiceClass>OO</InvoiceClass></InvoiceHeader><InvoiceIssueData><IssueDate>2007-12-11</IssueDate><InvoicingPeriod><StartDate>2007-12-11</StartDate><EndDate>2007-12-11</EndDate></InvoicingPeriod><InvoiceCurrencyCode>EUR</InvoiceCurrencyCode><TaxCurrencyCode>EUR</TaxCurrencyCode></InvoiceIssueData><TaxesOutputs><Tax><TaxTypeCode>01</TaxTypeCode><TaxRate>16.00</TaxRate><TaxableBase><TotalAmount>230.00</TotalAmount></TaxableBase><TaxAmount><TotalAmount>36.80</TotalAmount></TaxAmount></Tax></TaxesOutputs><InvoiceTotals><TotalGrossAmount>230.00</TotalGrossAmount><TotalGrossAmountBeforeTaxes>230.00</TotalGrossAmountBeforeTaxes><TotalTaxOutputs>36.80</TotalTaxOutputs><TotalTaxesWithheld>0.00</TotalTaxesWithheld><InvoiceTotal>266.80</InvoiceTotal><TotalOutstandingAmount>266.80</TotalOutstandingAmount><TotalExecutableAmount>266.80</TotalExecutableAmount></InvoiceTotals><Items><InvoiceLine><IssuerContractReference> </IssuerContractReference><PurchaseOrderReference>2.28093564E8</PurchaseOrderReference><DeliveryNotesReferences><DeliveryNote><DeliveryNoteNumber> </DeliveryNoteNumber></DeliveryNote></DeliveryNotesReferences><ItemDescription>Analista</ItemDescription><Quantity>1.0</Quantity><UnitOfMeasure>01</UnitOfMeasure><UnitPriceWithoutTax>70.000000</UnitPriceWithoutTax><TotalCost>70.00</TotalCost><DiscountsAndRebates><Discount><DiscountReason>Descuento al cliente</DiscountReason><DiscountRate>0.0000</DiscountRate><DiscountAmount>0.00</DiscountAmount></Discount></DiscountsAndRebates><GrossAmount>70.00</GrossAmount><TaxesOutputs><Tax><TaxTypeCode>01</TaxTypeCode><TaxRate>16.00</TaxRate><TaxableBase><TotalAmount>70.00</TotalAmount></TaxableBase><TaxAmount><TotalAmount>11.20</TotalAmount></TaxAmount></Tax></TaxesOutputs><TransactionDate>2007-12-11</TransactionDate></InvoiceLine><InvoiceLine><IssuerContractReference> </IssuerContractReference><PurchaseOrderReference>2.28093564E8</PurchaseOrderReference><DeliveryNotesReferences><DeliveryNote><DeliveryNoteNumber> </DeliveryNoteNumber></DeliveryNote></DeliveryNotesReferences><ItemDescription>Programadores</ItemDescription><Quantity>2.0</Quantity><UnitOfMeasure>01</UnitOfMeasure><UnitPriceWithoutTax>80.000000</UnitPriceWithoutTax><TotalCost>160.00</TotalCost><DiscountsAndRebates><Discount><DiscountReason>Descuento al cliente</DiscountReason><DiscountRate>0.0000</DiscountRate><DiscountAmount>0.00</DiscountAmount></Discount></DiscountsAndRebates><GrossAmount>160.00</GrossAmount><TaxesOutputs><Tax><TaxTypeCode>01</TaxTypeCode><TaxRate>16.00</TaxRate><TaxableBase><TotalAmount>160.00</TotalAmount></TaxableBase><TaxAmount><TotalAmount>25.60</TotalAmount></TaxAmount></Tax></TaxesOutputs><TransactionDate>2007-12-11</TransactionDate></InvoiceLine></Items><PaymentDetails><Installment><InstallmentDueDate>2007-12-11</InstallmentDueDate><InstallmentAmount>266.80</InstallmentAmount><PaymentMeans>04</PaymentMeans><AccountToBeCredited><IBAN>000000150078</IBAN><BankCode>8854</BankCode><BranchCode>0000</BranchCode></AccountToBeCredited></Installment></PaymentDetails></Invoice></Invoices><ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#" xmlns:etsi="http://uri.etsi.org/01903/v1.2.2#" Id="Signature">
<ds:SignedInfo Id="Signature-SignedInfo">
<ds:CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/>
<ds:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1"/>
<ds:Reference Id="SignedPropertiesID" Type="http://uri.etsi.org/01903/v1.2.2#SignedProperties" URI="#Signature-SignedProperties">
<ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
<ds:DigestValue>E70IIZJgM5B3rTwGJ5b4hEeJ8N0=</ds:DigestValue>
</ds:Reference>
<ds:Reference URI="">
<ds:Transforms>
<ds:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
</ds:Transforms>
<ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
<ds:DigestValue>q54/ZNHSjMWKMD4A5xI9qL2tBOA=</ds:DigestValue>
</ds:Reference>
<ds:Reference URI="#Certificate1">
<ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
<ds:DigestValue>njihA04aMjUOyc0gnw6mfxjsfv8=</ds:DigestValue>
</ds:Reference>
</ds:SignedInfo>
<ds:SignatureValue Id="SignatureValue">
nfmak7CHtweDx/WkwizYHuNgL37d6QEyNkLIC99zK0Yar0fGtXzrKgKMSRQXdXX52ZtzdKKIB7+Q
dUA9zCWUQlwAofPtbFCNYr8Ju3KDekmqEE3oTN9T689jTzW9Mn9fsazBIaCVI/wgfv4PvS0Z+lNH
ZIjb2UlCaZeVfdeInNo=
</ds:SignatureValue>
<ds:KeyInfo Id="Certificate1">
<ds:X509Data>
<ds:X509Certificate>
</ds:X509Certificate>
</ds:X509Data>
<ds:KeyValue>
<ds:RSAKeyValue>
<ds:Modulus>
uJRxVtM3TvuepDYf41qgagTbuf3HYsCsB+JD7Yn47nGlfWPRLKp1Spxc0vgsrr/oYlaqP3eaQcR/
tzdIFe+rrxu99pRQFBI4hs+pQaMDiSQr2Bz9vHk12SB+plKE2zsStkVAM2GjdDa7ZcEZYF6ui/qk
5OavOMT7za9Ri8i75H0=
</ds:Modulus>
<ds:Exponent>AQAB</ds:Exponent>
</ds:RSAKeyValue>
</ds:KeyValue>
</ds:KeyInfo>
<ds:Object Id="Signature-Object"><etsi:QualifyingProperties Target="#Signature"><etsi:SignedProperties Id="Signature-SignedProperties"><etsi:SignedSignatureProperties><etsi:SigningTime>2007-12-11T19:21:28.229+01:00</etsi:SigningTime><etsi:SigningCertificate><etsi:Cert><etsi:CertDigest><ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/><ds:DigestValue>dDucu0BjFAIFCeiJpfVJOqAnsNk=</ds:DigestValue></etsi:CertDigest><etsi:IssuerSerial><ds:X509IssuerName>CN=CA usuarios,OU=MITyC DNIe Pruebas,O=MITyC,L=Madrid,ST=Madrid,C=ES</ds:X509IssuerName><ds:X509SerialNumber>58</ds:X509SerialNumber></etsi:IssuerSerial></etsi:Cert></etsi:SigningCertificate><etsi:SignaturePolicyIdentifier><etsi:SignaturePolicyId><etsi:SigPolicyId><etsi:Identifier>http://www.facturae.es/politica de firma formato facturae/politica de firma formato facturae v3_0.pdf</etsi:Identifier><etsi:Description>Política de firma electrónica para facturación electrónica con formato Facturae</etsi:Description></etsi:SigPolicyId><etsi:SigPolicyHash><ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/><ds:DigestValue>HQvPemjDslVpcNmaJPpbHzhdZ50=</ds:DigestValue></etsi:SigPolicyHash></etsi:SignaturePolicyId></etsi:SignaturePolicyIdentifier><etsi:SignerRole><etsi:ClaimedRoles><etsi:ClaimedRole>emisor</etsi:ClaimedRole></etsi:ClaimedRoles></etsi:SignerRole></etsi:SignedSignatureProperties></etsi:SignedProperties></etsi:QualifyingProperties></ds:Object></ds:Signature></namespace:Facturae>