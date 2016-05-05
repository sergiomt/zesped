import es.ipsa.atril.sec.authentication.AuthenticationManagerFactory;
import es.ipsa.atril.sec.authentication.AuthenticationManager;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.doc.user.DataType
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.DocumentType;
import es.ipsa.atril.doc.user.AttributeType;
import es.ipsa.atril.doc.user.Item;

import java.util.Properties;
import java.io.File;

properties = new Properties();
properties.put('atril.platform', 'Atril6')
properties.put('atril.name', 'Zesped')
properties.put('atril.dialect', 'MySQL')
properties.put('atril.connectionType', 'JDBC')
properties.put('atril.connectionType.jdbc.driver', 'com.mysql.jdbc.Driver')
properties.put('atril.connectionType.jdbc.url', 'jdbc:mysql://127.0.0.1:3306/zesped?user=root&password=Zv>J5z!G1-CLQy0u')
properties.put('atril.authentication', 'LDAP')
properties.put('atril.authentication', 'Database')
properties.put('atril.authentication.ldap.protocol', 'Simple')
properties.put('atril.authentication.ldap.base_dn', 'dc=ipsa,dc=es')
properties.put('atril.authentication.ldap.host', '213.190.6.40')
properties.put('atril.authentication.ldap.port', '389')
properties.put('atril.authentication.ldap.credentials', '$0@ipsa.es')
properties.put('atril.authentication.ldap.fields_mapping', 'name=sAMAccountName,last_name=sn,first_name=givenName')
properties.put('atril.authentication.cryptography.provider', 'BC')
properties.put('atril.authentication.cryptography.algorithm', 'SHA-1')

session =  AuthenticationManagerFactory.getAuthenticationManager(properties).authenticateUser("admin", "admin")
session.open("Sesion para los tests");
session.connect();
totalSpace = 1000000000;
maxFileSize = 1;

dms = session.getDms();

atrilCaptureDemoVolume = dms.getVolumeManager().getVolumeList().get(0);

run(new File('CaptureTypes_DocumentTypes.groovy'));

run(new File('CaptureTypes_Documents.groovy'));

run(new File('CaptureDepositSystem_DocumentTypes.groovy'));

run(new File('CaptureDepositSystem_Documents.groovy'));

session.disconnect();
session.close();