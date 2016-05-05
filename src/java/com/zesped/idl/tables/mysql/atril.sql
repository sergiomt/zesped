CREATE TABLE AS_Users (
	AS_LastName varchar(64) NOT NULL,
	AS_PinHash text NOT NULL,
	AS_LoginName varchar(64) UNIQUE,
	AS_FirstName varchar(64) NOT NULL,
	AS_PrivateKey text NOT NULL,
	AS_PublicKey text NOT NULL,
	AS_CreationTime int(11) DEFAULT NULL,
	AS_ModificationTime  int(11) DEFAULT NULL,
	AS_DeletionTime int(11) DEFAULT NULL,
	AS_FromLDAP SMALLINT(1)  NOT NULL DEFAULT 0,
	Id integer(25) NOT NULL AUTO_INCREMENT  PRIMARY KEY
);

CREATE TABLE AS_Resources (
	AS_Name varchar(64) UNIQUE,
	AS_Description varchar(64) NOT NULL,
	AS_Type varchar(64) NOT NULL,
	AS_Data LONGTEXT NOT NULL,
	AS_CreationTime int(11) DEFAULT NULL,
	AS_ModificationTime  int(11) DEFAULT NULL,
    AS_DeletionTime int(11) DEFAULT NULL,
	Id integer(25) NOT NULL AUTO_INCREMENT  PRIMARY KEY
);

CREATE TABLE AS_Groups (
	AS_Name varchar(64) UNIQUE,
	AS_Description varchar(64) NOT NULL,
	AS_PublicKey text NOT NULL,
	AS_CreationTime int(11) DEFAULT NULL,
	AS_ModificationTime  int(11) DEFAULT NULL,
    AS_DeletionTime int(11) DEFAULT NULL,
	AS_FromLDAP SMALLINT(1)  NOT NULL DEFAULT 0,
	AS_LDAP_DN varchar(128),
	Id integer(25) NOT NULL AUTO_INCREMENT  PRIMARY KEY
);

CREATE TABLE AS_UserResourceRights (
	AS_Rights text NOT NULL,
	fkResourceId integer(25),
	fkUserId integer(25),
	AS_Key text NOT NULL, 
	AS_CreationTime int(11) DEFAULT NULL,
	AS_ModificationTime  int(11) DEFAULT NULL,
    AS_DeletionTime int(11) DEFAULT NULL,
	CONSTRAINT fk_UserResRights_ResId FOREIGN KEY (fkResourceId) REFERENCES AS_Resources(Id) ON DELETE CASCADE,
	CONSTRAINT fk_UserResRights_UserId FOREIGN KEY (fkUserId) REFERENCES AS_Users(Id) ON DELETE CASCADE,
	PRIMARY KEY(fkResourceId, fkUserId)
);

CREATE TABLE AS_GroupResourceRights (
	AS_Rights text NOT NULL,
	fkResourceId integer(25),
	fkGroupId integer(25),
	AS_Key text NOT NULL, 
	AS_CreationTime int(11) DEFAULT NULL,
	AS_ModificationTime  int(11) DEFAULT NULL,
    AS_DeletionTime int(11) DEFAULT NULL,
	CONSTRAINT fk_GroupResRights_ResId FOREIGN KEY (fkResourceId) REFERENCES AS_Resources(Id) ON DELETE CASCADE,
	CONSTRAINT fk_GroupResRights_GroupId FOREIGN KEY (fkGroupId) REFERENCES AS_Groups(Id) ON DELETE CASCADE,
	PRIMARY KEY(fkResourceId, fkGroupId)
);

CREATE TABLE AS_MemberOf (
	AS_Key text NOT NULL ,
	fkGroupId integer(25) ,
	fkUserId integer(25) ,
	AS_CreationTime int(11) DEFAULT NULL,
	AS_ModificationTime  int(11) DEFAULT NULL,
    AS_DeletionTime int(11) DEFAULT NULL,
	CONSTRAINT fk_MemberOf_GroupId FOREIGN KEY (fkGroupId) REFERENCES AS_Groups(Id) ON DELETE CASCADE,
	CONSTRAINT fk_MemberOf_UserId FOREIGN KEY (fkUserId) REFERENCES AS_Users(Id) ON DELETE CASCADE,
	PRIMARY KEY(fkGroupId, fkUserId)
);

CREATE TABLE AS_SubGroupOf (
	AS_Key text NOT NULL,
	fkParentGroupId integer(25),
	fkChildGroupId integer(25),
	AS_CreationTime int(11) DEFAULT NULL,
	AS_ModificationTime  int(11) DEFAULT NULL,
    AS_DeletionTime int(11) DEFAULT NULL,
	CONSTRAINT fk_SubGroupOf_ParentGroupId FOREIGN KEY (fkParentGroupId) REFERENCES AS_Groups(Id) ON DELETE CASCADE,
	CONSTRAINT fk_SubGroupOf_ChildGroupId FOREIGN KEY (fkChildGroupId) REFERENCES AS_Groups(Id),
	PRIMARY KEY(fkParentGroupId, fkChildGroupId)
);

CREATE TABLE AS_UserGroupRights (
	AS_Rights text NOT NULL,
	fkGroupId int(25) NOT NULL default '0',
	fkUserId int(25) NOT NULL default '0',
	AS_Key text NOT NULL,
	AS_CreationTime int(11) DEFAULT NULL,
	AS_ModificationTime  int(11) DEFAULT NULL,
    AS_DeletionTime int(11) DEFAULT NULL,
    PRIMARY KEY  (fkGroupId,fkUserId),
    KEY UGR_fkUserId (fkUserId)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE AS_GroupGroupRights (
    AS_Rights text NOT NULL,
    fkGroupId int(25) NOT NULL default '0',
    fkAdministeredGroupId int(25) NOT NULL default '0',
    AS_Key text NOT NULL,
    AS_CreationTime int(11) DEFAULT NULL,
    AS_ModificationTime  int(11) DEFAULT NULL,
    AS_DeletionTime int(11) DEFAULT NULL,
    PRIMARY KEY  (fkGroupId,fkAdministeredGroupId),
    KEY GGR_fkAdministeredGroupId (fkAdministeredGroupId)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


INSERT INTO AS_Users (AS_LastName,AS_PinHash,AS_LoginName,AS_FirstName,AS_PrivateKey,AS_PublicKey, AS_CreationTime, AS_ModificationTime,AS_FromLDAP ) VALUES ('admin','0DPiKuNIrrVmD8IUCuw1hQxNqZc=','admin','admin','','',1,1,false);

INSERT INTO AS_Groups (AS_Name,AS_Description,AS_PublicKey, AS_CreationTime, AS_ModificationTime) VALUES ('All','Default security group','',1,1);
INSERT INTO AS_Groups (AS_Name,AS_Description,AS_PublicKey, AS_CreationTime, AS_ModificationTime) VALUES ('Administrators','Default security administrators group','',1,1);

INSERT INTO AS_SubGroupOf (AS_Key, fkParentGroupId, fkChildGroupId, AS_CreationTime, AS_ModificationTime) SELECT '', t1.ID, t2.ID, 1,1 FROM(select ID from AS_Groups where AS_Name Like 'All')t1, (select ID from AS_Groups where AS_Name Like 'Administrators')t2;

INSERT INTO AS_MemberOf (AS_Key,fkGroupId,fkUserId,AS_CreationTime, AS_ModificationTime) SELECT '', t1.ID , t2.ID,1,1 FROM (select ID from AS_Groups where AS_Name LIKE 'Administrators') t1 ,(select ID from AS_Users where AS_LoginName LIKE 'admin') t2;

INSERT INTO AS_GroupGroupRights (AS_Rights, fkGroupId, fkAdministeredGroupId,AS_Key,AS_CreationTime, AS_ModificationTime) SELECT '111111111111111', t1.ID , t2.ID,'',1,1 FROM (select ID from AS_Groups where AS_Name LIKE 'Administrators' ) t1 ,(select ID from AS_Groups where AS_Name LIKE 'All' ) t2;

INSERT INTO AS_UserGroupRights (AS_Rights, fkUserId, fkGroupId, AS_Key,AS_CreationTime, AS_ModificationTime) SELECT '111111111111111', t1.ID , t2.ID,'',1,1 FROM (select ID from AS_Users where AS_LoginName LIKE 'admin' ) t1 ,(select ID from AS_Groups where AS_Name LIKE 'Administrators' ) t2;

CREATE TABLE VersionRepositorio(IdVersion varchar(255) NOT NULL,Fecha int(11) NOT NULL) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE Sesion (
	IdSesion int(11) NOT NULL AUTO_INCREMENT,
	Usuario varchar(255) NOT NULL,
	Codigo varchar(255) NOT NULL DEFAULT '',
	TInicio int(11) NOT NULL,
	TFin int(11) DEFAULT NULL,
	Modo smallint(1) unsigned NOT NULL,
	PRIMARY KEY (IdSesion)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE TipoFichero (
	IdTipoFichero int(11) NOT NULL,
	Nombre varchar(255) NOT NULL,
	Descripcion varchar(255) DEFAULT NULL,
	Eliminado smallint(1) NOT NULL DEFAULT 0,
	PRIMARY KEY (IdTipoFichero)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE TipoDocumento (
	IdTipoDoc int(11) NOT NULL,
	Nombre varchar(255) NOT NULL DEFAULT '',
	Descripcion varchar(255) DEFAULT NULL,
	Plantilla varchar(255) DEFAULT NULL,
	Item smallint(1) NOT NULL DEFAULT 0,
	TamEsperado int(11) DEFAULT NULL,
	TCreado int(11) DEFAULT NULL,
	TModificado int(11) DEFAULT NULL,
	Eliminado smallint(1) NOT NULL DEFAULT 0,
	UNIQUE Id_Externo(Nombre),
	fkIdSesionBloquea int(11) DEFAULT NULL,
	INDEX (fkIdSesionBloquea),
	CONSTRAINT fk_Sesion_IdSesionBloquea FOREIGN KEY (fkIdSesionBloquea) REFERENCES Sesion(IdSesion),
	PRIMARY KEY (IdTipoDoc)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE EsHijo (
	IdTipoDocPadre int(11),
	IdTipoDocHijo int(11) NOT NULL,
	CardinalidadMin smallint,
	CardinalidadMax smallint,
	Orden smallint,
	PRIMARY KEY (IdTipoDocPadre,IdTipoDocHijo),
	INDEX (IdTipoDocPadre),
	CONSTRAINT fk_EsHijo_IdTipoDocPadre FOREIGN KEY (IdTipoDocPadre) REFERENCES TipoDocumento(IdTipoDoc ),
	INDEX (IdTipoDocHijo),
	CONSTRAINT fk_EsHijo_IdTipoDocHijo FOREIGN KEY (IdTipoDocHijo) REFERENCES TipoDocumento(IdTipoDoc )
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE TipoVista (
	IdTipoVista int(11) NOT NULL,
	fkIdTipoDoc int(11) NOT NULL,
	Nombre varchar(255) NOT NULL,
	Descripcion varchar(255) DEFAULT NULL,
	Plantilla varchar(255) DEFAULT NULL,
	TCreado int(11) NOT NULL,
	TModificado int(11) NOT NULL,
	Orden smallint(4) unsigned NOT NULL DEFAULT 0,
	Eliminado smallint(1) NOT NULL DEFAULT 0,
	PRIMARY KEY (IdTipoVista),
	INDEX (fkIdTipoDoc),
	CONSTRAINT fk_TipoVista_fkIdTipoDoc FOREIGN KEY (fkIdTipoDoc) REFERENCES TipoDocumento(IdTipoDoc)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE Atributo (
	IdAtrib int(11) NOT NULL,
	Nombre varchar(255) NOT NULL,
	Tipo smallint(4) unsigned NOT NULL DEFAULT 0,
	fkIdSesionBloquea int(11) DEFAULT NULL,
	INDEX (fkIdSesionBloquea),
	FOREIGN KEY (fkIdSesionBloquea) REFERENCES Sesion(IdSesion),
	UNIQUE (Nombre),
	PRIMARY KEY (IdAtrib)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE Pertenece (
	fkIdAtrib int(11) NOT NULL,
	fkIdTipoDoc int(11) NOT NULL,
	Descripcion varchar(255) DEFAULT NULL,
	Orden smallint(4) unsigned NOT NULL DEFAULT 0,
	ValorPorDefecto varchar(255) DEFAULT NULL,
	Eliminado smallint(1) NOT NULL DEFAULT 0,
	PRIMARY KEY (fkIdAtrib,fkIdTipoDoc),
	INDEX (fkIdAtrib),
	CONSTRAINT fk_Pertenece_fkIdAtrib FOREIGN KEY (fkIdAtrib) REFERENCES Atributo(IdAtrib),
	INDEX (fkIdTipoDoc),
	CONSTRAINT fk_Pertenece_fkIdTipoDoc FOREIGN KEY (fkIdTipoDoc) REFERENCES TipoDocumento(IdTipoDoc)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE Cola(
	IdCola int(11) NOT NULL,
	Nombre varchar(255) NOT NULL,
	PRIMARY KEY (IdCola)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE Soporte (
	IdSoporte int(11) NOT NULL AUTO_INCREMENT,
	Nombre varchar(255) NOT NULL,
	Descripcion varchar(255) DEFAULT NULL,
	EspTotal BIGINT(20) DEFAULT NULL,
	EspOcupado BIGINT(20) DEFAULT NULL,
	Montado smallint(1) NOT NULL DEFAULT 0,
	PtoMontaje varchar(255) DEFAULT '',
	RutaRepositorio varchar(255) DEFAULT '',
	MaxNumFicheros  int(11) DEFAULT 10,
	MaxNumDirectorios  int(11) DEFAULT 10,
	MaxTamFichero  int(11) DEFAULT 1000,
	fkIdUltimoFichero int(11) DEFAULT NULL,
	Eliminado smallint(1) NOT NULL DEFAULT 0,
	Bloqueado int(11) DEFAULT NULL,
	TCreacion int(11) NOT NULL,
	UNIQUE (Nombre),
	INDEX (Bloqueado),
	CONSTRAINT fk_Soporte_Bloqueado FOREIGN KEY (Bloqueado) REFERENCES Sesion(IdSesion),
	INDEX (fkIdUltimoFichero),
	PRIMARY KEY (IdSoporte)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE Fichero (
	IdFichero int(11) NOT NULL AUTO_INCREMENT,
	fkIdSoporte int(11) NOT NULL,
	fkIdTipoFich int(11) NOT NULL,
	Nombre varchar(255) NOT NULL,
	EspacioLibre int(11),
	Eliminado smallint(1) NOT NULL DEFAULT 0,
	Bloqueado int(11) DEFAULT NULL,
	INDEX (Bloqueado),
	CONSTRAINT fk_Fichero_Bloqueado FOREIGN KEY (Bloqueado) REFERENCES Sesion(IdSesion),
	PRIMARY KEY (IdFichero),
	INDEX (fkIdSoporte),
	INDEX (fkIdTipoFich),
	INDEX (fkIdSoporte, EspacioLibre),
	CONSTRAINT fk_Fichero_fkIdTipoFich FOREIGN KEY (fkIdTipoFich) REFERENCES TipoFichero(IdTipoFichero)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

ALTER TABLE Soporte ADD CONSTRAINT fk_Soporte_fkIdUltimoFichero FOREIGN KEY (fkIdUltimoFichero) REFERENCES Fichero(IdFichero); 

CREATE TABLE Lote (
	IdLote int(11) NOT NULL AUTO_INCREMENT,
	TCreado int(11) DEFAULT NULL,
	TModificado int(11) DEFAULT NULL,
	Codigo varchar(255) NOT NULL,
	EspacioReservado int(11) DEFAULT NULL,
	Eliminado smallint(1) NOT NULL DEFAULT 0,
	Bloqueado int(11) DEFAULT NULL,
	UNIQUE (Codigo),
	INDEX (Bloqueado),
	CONSTRAINT fk_Lote_Bloqueado FOREIGN KEY (Bloqueado) REFERENCES Sesion(IdSesion),
	PRIMARY KEY (IdLote)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE Documento (
	IdDoc bigint(20),
	fkIdTipoDoc int(11) NOT NULL,
	fkIdLote int(11),
	fkIdDocPadre bigint(20) DEFAULT NULL,
	NumSecuencia int(11) NOT NULL,
	NumIdentificador bigint(20) DEFAULT NULL,
	Codigo varchar(255) DEFAULT NULL,
	EspacioLibre int(11) DEFAULT NULL,
	Version int(11) DEFAULT 0 NOT NULL,
	Prioridad smallint(4) unsigned NOT NULL DEFAULT 127,
	TCreado int(11) NOT NULL,
	TModificado int(11) NOT NULL,
	ModificacionAdministrativa smallint(1) NOT NULL DEFAULT 1,
	TEliminado int(11) DEFAULT NULL,
	fkIdSesionElimina int(11) DEFAULT NULL,
	fkIdCola int(11) DEFAULT NULL,
	fkIdSoporte int(11) DEFAULT NULL,
	fkIdUltimoFichero int(11) DEFAULT NULL,
	Clave varchar(64) DEFAULT NULL,
	Completo smallint(1) NOT NULL DEFAULT 0,
	Bloqueado int(11) DEFAULT NULL,
	CadenaDatosTemporales MEDIUMTEXT DEFAULT NULL,
	TUltimoAcceso int(11) DEFAULT NULL,
	NumAccesos int(11) DEFAULT 0,
	Mensaje varchar(255) NOT NULL DEFAULT "",
	Restricciones text NOT NULL,
	TipoFirma varchar(64) DEFAULT NULL,
	Firma longtext DEFAULT NULL,
	INDEX (Bloqueado),
	CONSTRAINT fk_Documento_Bloqueado FOREIGN KEY (Bloqueado) REFERENCES Sesion(IdSesion),
	PRIMARY KEY (IdDoc),
	INDEX (fkIdTipoDoc),
	CONSTRAINT fk_Documento_fkIdTipoDoc FOREIGN KEY (fkIdTipoDoc) REFERENCES TipoDocumento(IdTipoDoc),
	INDEX (fkIdLote),
	CONSTRAINT fk_Documento_fkIdLote FOREIGN KEY (fkIdLote) REFERENCES Lote(IdLote),
	INDEX (fkIdSoporte),
	INDEX (fkIdUltimoFichero),
	CONSTRAINT fk_Documento_fkIdUltimoFichero FOREIGN KEY (fkIdUltimoFichero) REFERENCES Fichero(IdFichero),
	INDEX (fkIdDocPadre, TEliminado),
	CONSTRAINT fk_Documento_fkIdDocPadre FOREIGN KEY (fkIdDocPadre) REFERENCES Documento(IdDoc) ON DELETE CASCADE,
	INDEX (NumIdentificador),
	INDEX (fkIdCola, TEliminado),
	CONSTRAINT fk_Documento_fkIdCola FOREIGN KEY (fkIdCola) REFERENCES Cola (IdCola),
	INDEX (fkIdSesionElimina),
	CONSTRAINT fk_Documento_fkIdSesionElimina FOREIGN KEY (fkIdSesionElimina) REFERENCES Sesion(IdSesion),
	INDEX (TCreado)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE Vista (
	fkIdTipoVista int(11) NOT NULL,
	fkIdDoc bigint(20) NOT NULL,
	Version int(11) NOT NULL,
	fkIdFich int(11) DEFAULT NULL,
	TipoMime varchar(64),
	Offset int(11) DEFAULT NULL,
	TamanoObjeto int(11) DEFAULT NULL,
	TOperacion int(11) NOT NULL,
	PRIMARY KEY (fkIdDoc, fkIdTipoVista),
	INDEX (fkIdTipoVista),
	CONSTRAINT indx_Vista_fkIdTipoVista FOREIGN KEY (fkIdTipoVista) REFERENCES TipoVista(IdTipoVista),
	INDEX (fkIdDoc),
	CONSTRAINT fk_Vista_fkIdDoc FOREIGN KEY (fkIdDoc) REFERENCES Documento(IdDoc) ON DELETE CASCADE,
	INDEX (fkIdFich),
	CONSTRAINT indx_Vista_fkIdFich FOREIGN KEY (fkIdFich) REFERENCES Fichero(IdFichero)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE VistaTraza (
	fkIdTipoVista int(11) NOT NULL,
	fkIdDoc bigint(20) NOT NULL,
	fkIdFich int(11) DEFAULT NULL,
	Version int(11) NOT NULL,
	TipoMime varchar(64),
	Offset int(11) DEFAULT NULL,
	TamanoObjeto int(11) DEFAULT NULL,
	TOperacion int(11) NOT NULL,
	PRIMARY KEY (fkIdDoc, fkIdTipoVista, Version),
	INDEX (fkIdTipoVista),
	CONSTRAINT indx_VistaTraza_fkIdTipoVista FOREIGN KEY (fkIdTipoVista) REFERENCES TipoVista(IdTipoVista),
	INDEX (fkIdDoc),
	CONSTRAINT fk_VistaTraza_fkIdDoc FOREIGN KEY (fkIdDoc) REFERENCES Documento(IdDoc) ON DELETE CASCADE,
	INDEX (fkIdFich),
	CONSTRAINT indx_VistaTraza_fkIdFich FOREIGN KEY (fkIdFich) REFERENCES Fichero(IdFichero)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE ValorEntero (
	fkIdDoc bigint(20) NOT NULL,
	fkIdAtributo int(11) NOT NULL,
	Version int(11) NOT NULL,
	Valor bigint(20) DEFAULT NULL,
	TOperacion int(11) NOT NULL,
	fkIdSesion int(11) NOT NULL,
	PRIMARY KEY (fkIdDoc,fkIdAtributo),
	INDEX (fkIdAtributo, Valor),
	INDEX (fkIdDoc),
	CONSTRAINT indx_ValorEntero_fkIdDoc FOREIGN KEY (fkIdDoc) REFERENCES Documento(IdDoc) ON DELETE CASCADE,
	INDEX (fkIdAtributo),
	CONSTRAINT indx_ValorEntero_fkIdAtributo FOREIGN KEY (fkIdAtributo) REFERENCES Atributo(IdAtrib),
	INDEX (fkIdSesion),
	CONSTRAINT indx_ValorEntero_fkIdSesion FOREIGN KEY (fkIdSesion) REFERENCES Sesion(IdSesion)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE DocumentoTraza (
	fkIdDoc bigint(20) NOT NULL,
	Version int(11) NOT NULL,
	fkIdTipoDoc int(11) DEFAULT NULL,
	fkIdDocPadre bigint(20) DEFAULT NULL,
	fkIdLote int(11),
	TOperacion int(11) NOT NULL,
	fkIdSesion int(11) NOT NULL,
	NumSecuencia int(11) DEFAULT NULL,
	NumIdentificador bigint(20) DEFAULT NULL,
	Codigo varchar(255) DEFAULT NULL,
	Mensaje varchar(255) NOT NULL DEFAULT "",
	Restricciones text NOT NULL,	
	TipoFirma varchar(64) DEFAULT NULL,
	Firma longtext DEFAULT NULL,
	PRIMARY KEY (fkIdDoc,Version),
	INDEX (fkIdDoc),
	CONSTRAINT fk_DocumentoTraza_fkIdDoc FOREIGN KEY (fkIdDoc) REFERENCES Documento(IdDoc) ON DELETE CASCADE,
	INDEX (fkIdDocPadre),
	INDEX (fkIdLote),
	CONSTRAINT fk_DocumentoTraza_fkIdLote FOREIGN KEY (fkIdLote) REFERENCES Lote(IdLote),
	INDEX (fkIdSesion),
	CONSTRAINT fk_DocumentoTraza_fkIdSesion FOREIGN KEY (fkIdSesion) REFERENCES Sesion(IdSesion)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE ValorDoble (
	fkIdDoc bigint(20) NOT NULL,
	fkIdAtributo int(11) NOT NULL,
	Version int(11) NOT NULL,
	Valor double DEFAULT NULL,
	TOperacion int(11) NOT NULL,
	fkIdSesion int(11) NOT NULL,
	PRIMARY KEY (fkIdDoc,fkIdAtributo),
	INDEX (fkIdAtributo, Valor),
	INDEX (fkIdDoc),
	CONSTRAINT indx_ValorDoble_fkIdDoc FOREIGN KEY (fkIdDoc) REFERENCES Documento(IdDoc) ON DELETE CASCADE,
	INDEX (fkIdAtributo),
	CONSTRAINT indx_ValorDoble_fkIdAtributo FOREIGN KEY (fkIdAtributo) REFERENCES Atributo(IdAtrib),
	INDEX (fkIdSesion),
	CONSTRAINT indx_ValorDoble_fkIdSesion FOREIGN KEY (fkIdSesion) REFERENCES Sesion(IdSesion)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE ValorCadena (
	fkIdDoc bigint(20) NOT NULL,
	fkIdAtributo int(11) NOT NULL,
	Version int(11) NOT NULL,
	Valor VarChar(255) DEFAULT NULL,
	RestoValor LONGTEXT DEFAULT NULL,
	TOperacion int(11) NOT NULL,
	fkIdSesion int(11) NOT NULL,
	PRIMARY KEY (fkIdDoc,fkIdAtributo),
	INDEX (fkIdAtributo, Valor),
	INDEX (fkIdDoc),
	CONSTRAINT indx_ValorCadena_fkIdDoc FOREIGN KEY (fkIdDoc) REFERENCES Documento(IdDoc) ON DELETE CASCADE,
	INDEX (fkIdAtributo),
	CONSTRAINT indx_ValorCadena_fkIdAtributo FOREIGN KEY (fkIdAtributo) REFERENCES Atributo(IdAtrib),
	INDEX (fkIdSesion),
	CONSTRAINT indx_ValorCadena_fkIdSesion FOREIGN KEY (fkIdSesion) REFERENCES Sesion(IdSesion)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE ValorEnteroTraza (
	fkIdDoc bigint(20) NOT NULL,
	fkIdAtributo int(11) NOT NULL,
	Version int(11) NOT NULL,
	Valor bigint(20) DEFAULT NULL,
	TOperacion int(11) NOT NULL,
	fkIdSesion int(11) NOT NULL,
	PRIMARY KEY (fkIdDoc,fkIdAtributo,Version),
	INDEX (fkIdAtributo, Valor),
	INDEX (fkIdDoc),
	CONSTRAINT indx_ValorEnteroT_fkIdDoc FOREIGN KEY (fkIdDoc) REFERENCES Documento(IdDoc) ON DELETE CASCADE,
	INDEX (fkIdAtributo),
	CONSTRAINT indx_ValorEnteroT_fkIdAtributo FOREIGN KEY (fkIdAtributo) REFERENCES Atributo(IdAtrib),
	INDEX (fkIdSesion),
	CONSTRAINT indx_ValorEnteroT_fkIdSesion FOREIGN KEY (fkIdSesion) REFERENCES Sesion(IdSesion)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE ValorDobleTraza (
	fkIdDoc bigint(20) NOT NULL,
	fkIdAtributo int(11) NOT NULL,
	Version int(11) NOT NULL,
	Valor double DEFAULT NULL,
	TOperacion int(11) NOT NULL,
	fkIdSesion int(11) NOT NULL,
	PRIMARY KEY (fkIdDoc,fkIdAtributo,Version),
	INDEX (fkIdAtributo, Valor),
	INDEX (fkIdDoc),
	CONSTRAINT indx_ValorDobleT_fkIdDoc FOREIGN KEY (fkIdDoc) REFERENCES Documento(IdDoc) ON DELETE CASCADE,
	INDEX (fkIdAtributo),
	CONSTRAINT indx_ValorDobleT_fkIdAtributo FOREIGN KEY (fkIdAtributo) REFERENCES Atributo(IdAtrib),
	INDEX (fkIdSesion),
	CONSTRAINT indx_ValorDobleT_fkIdSesion FOREIGN KEY (fkIdSesion) REFERENCES Sesion(IdSesion)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT; 

CREATE TABLE ValorCadenaTraza (
	fkIdDoc bigint(20) NOT NULL,
	fkIdAtributo int(11) NOT NULL,
	Version int(11) NOT NULL,
	Valor VarChar(255) DEFAULT NULL,
	RestoValor LONGTEXT DEFAULT NULL,
	TOperacion int(11) NOT NULL,
	fkIdSesion int(11) NOT NULL,
	PRIMARY KEY (fkIdDoc,fkIdAtributo,Version),
	INDEX (fkIdAtributo, Valor),
	INDEX (fkIdDoc),
	CONSTRAINT indx_ValorCadenaT_fkIdDoc FOREIGN KEY (fkIdDoc) REFERENCES Documento(IdDoc) ON DELETE CASCADE,
	INDEX (fkIdAtributo),
	CONSTRAINT indx_ValorCadenaT_fkIdAtributo FOREIGN KEY (fkIdAtributo) REFERENCES Atributo(IdAtrib),
	INDEX (fkIdSesion),
	CONSTRAINT indx_ValorCadenaT_fkIdSesion FOREIGN KEY (fkIdSesion) REFERENCES Sesion(IdSesion)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT;
	
CREATE TABLE AS_EntityDocumentRights (
    AS_Rights text NOT NULL,
	fkGroupId int(25) DEFAULT NULL,
	fkUserId int(25) DEFAULT NULL,
	fkDocumentId bigint(20) NOT NULL,
    AS_CreationTime int(11) DEFAULT NULL,
    AS_ModificationTime  int(11) DEFAULT NULL,
    AS_DeletionTime int(11) DEFAULT NULL,
	CONSTRAINT fk_EntityDocRights_DocId FOREIGN KEY (fkDocumentId) REFERENCES Documento(IdDoc) ON DELETE CASCADE,
    PRIMARY KEY  (fkGroupId,fkUserId,fkDocumentId)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT;

CREATE TABLE TipoEvento (
	IdTipoEvento int(5) NOT NULL AUTO_INCREMENT,
	Nombre VarChar(100) NOT NULL DEFAULT '',
	Descripcion VarChar(250) NOT NULL DEFAULT '',
	PRIMARY KEY(IdTipoEvento)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT;


CREATE TABLE Evento (
	IdEvento int(11) NOT NULL AUTO_INCREMENT,
	fkIdTipoEvento int(5) NOT NULL,
	Importancia int(5) NOT NULL,
	Origen VarChar(100) NOT NULL,
	Fecha int(11) NOT NULL,
	Detalles Text NOT NULL,
	fkIdSesionAtril int(11) DEFAULT NULL,
	fkIdUsuario int(25) DEFAULT NULL,
	fkIdDoc bigint(20) DEFAULT NULL,
	fkIdRecurso int(25) DEFAULT NULL,
	CONSTRAINT fk_Evento_IdTipoEvento FOREIGN KEY (fkIdTipoEvento) REFERENCES TipoEvento(IdTipoEvento),
	PRIMARY KEY(IdEvento)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT;

CREATE TABLE TipoDocLocale (
  	IdTipoDoc INT(11) NOT NULL,
	Locale VARCHAR(10) NOT NULL,
	Nombre varchar(255) NOT NULL,
	PRIMARY KEY (IdTipoDoc, Locale),
	CONSTRAINT fk_tipoDocLocale_tipoDocumento FOREIGN KEY fk_tipoDocLocale_tipoDocumento (IdTipoDoc)
	REFERENCES TipoDocumento (IdTipoDoc)
	ON DELETE CASCADE
	ON UPDATE RESTRICT
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT;

CREATE TABLE CaptionLocale (
	IdTipoDoc INT(11) NOT NULL,
	Locale VARCHAR(10) NOT NULL,
	Caption varchar(255) NOT NULL,
	PRIMARY KEY (IdTipoDoc, Locale),
	CONSTRAINT fk_CaptionLocale_tipoDocumento FOREIGN KEY fk_CaptionLocale_tipoDocumento (IdTipoDoc)
	REFERENCES TipoDocumento (IdTipoDoc)
	ON DELETE CASCADE
	ON UPDATE RESTRICT
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT;


CREATE TABLE AtributoLocale (
	IdAtributo INT(11) NOT NULL,
	Locale VARCHAR(10) NOT NULL,
	Nombre varchar(255) NOT NULL,
	PRIMARY KEY (IdAtributo, Locale),
	CONSTRAINT fk_atributoLocale_IdAtrib FOREIGN KEY fk_atributoLocale_IdAtrib (IdAtributo)
	REFERENCES Atributo (IdAtrib)
	ON DELETE CASCADE
	ON UPDATE RESTRICT
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT;

CREATE TABLE AtributoAdminLocale (
	NombreAtributo varchar(255) NOT NULL,
	Locale VARCHAR(10) NOT NULL,
	Nombre varchar(255) NOT NULL,
	PRIMARY KEY (NombreAtributo, Locale)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT;

CREATE TABLE `Usuario` (
  `Id` int(10) NOT NULL auto_increment,
  `Alias` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`Id`)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT;

CREATE TABLE `SesionContable` (
  `Id` int(10) NOT NULL auto_increment,
  `Codigo` varchar(255) NOT NULL default '',
  `TInicio` int(10) NOT NULL default '0',
  `TFin` int(10) default NULL,
  `fkIdUsuario` int(10) NOT NULL default '0',
  PRIMARY KEY  (`Id`),
  KEY `fk_Id_Usuario` (`fkIdUsuario`),
  CONSTRAINT `fk_Id_Usuario` FOREIGN KEY (`fkIdUsuario`) REFERENCES `Usuario` (`Id`)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT;

CREATE TABLE `TrazaDocumento` (
  `IdTraza` int(20) NOT NULL auto_increment,
  `fkIdDoc` bigint(20) NOT NULL,
  `fkIdTipoDoc` int(10) NOT NULL,
  `Nodo` varchar(255) NOT NULL,
  `TFechaEntrada` int(10) NOT NULL,
  `fkIdSesionEntrada` int(10) NOT NULL,
  `TFechaSalida` int(10) default NULL,
  `fkIdSesionSalida` int(10) default NULL,
  PRIMARY KEY  (`IdTraza`),
  KEY `fk_Id_SesionEntrada` (`fkIdSesionEntrada`),
  KEY `fk_Id_SesionSalida` (`fkIdSesionSalida`),
  CONSTRAINT `fk_Id_SesionEntrada` FOREIGN KEY (`fkIdSesionEntrada`) REFERENCES `SesionContable` (`Id`),
  CONSTRAINT `fk_Id_SesionSalida` FOREIGN KEY (`fkIdSesionSalida`) REFERENCES `SesionContable` (`Id`)
) ENGINE=InnoDB PACK_KEYS=DEFAULT ROW_FORMAT=DEFAULT;



INSERT INTO TipoFichero (IdTipoFichero, Nombre, Descripcion, Eliminado) VALUES (1,'cva','Contenedor de Vistas Atril',0);

INSERT INTO TipoFichero (IdTipoFichero, Nombre, Descripcion, Eliminado) VALUES (2,'img','Formato de ficheros de imagenes ATRIL',0);

INSERT INTO TipoFichero (IdTipoFichero, Nombre, Descripcion, Eliminado) VALUES (3,'tif','Tagged Image File Format',0);

INSERT INTO TipoFichero (IdTipoFichero, Nombre, Descripcion, Eliminado) VALUES (4,'jfif','JPG File Image Format',0);

INSERT INTO TipoFichero (IdTipoFichero, Nombre, Descripcion, Eliminado) VALUES (5,'bmp','Bitmap',0);

INSERT INTO TipoFichero (IdTipoFichero, Nombre, Descripcion, Eliminado) VALUES (6,'j2k','JPG 2000',0);

INSERT INTO TipoFichero (IdTipoFichero, Nombre, Descripcion, Eliminado) VALUES (7,'jbig','Joint Bi-level Image experts Group',0);

INSERT INTO TipoFichero (IdTipoFichero, Nombre, Descripcion, Eliminado) VALUES (8,'Generico','Ficheros sin extension',0);

INSERT INTO Cola (IdCola, Nombre) VALUES (-1,'Los documentos estan en esta cola antes de entrar en un workflow o despues de salir.');

INSERT INTO Cola (IdCola, Nombre) VALUES (-2,'Los documentos agregados estan en esta cola cuando todos sus hijos no estn en la misma cola.');

INSERT INTO Cola (IdCola, Nombre) VALUES (255,'Los documentos estan en esta cola antes de entrar en un workflow o despues de salir.');

INSERT INTO TipoDocumento (IdTipoDoc, Nombre, Descripcion, Item, Eliminado)
 VALUES (0, 'Root', 'Root Document Type', 0, 0);

INSERT INTO Documento (IdDoc, fkIdTipoDoc, fkIdSoporte, NumSecuencia, Version, TCreado, TModificado, Mensaje, Restricciones)
 VALUES (0, 0, NULL, 0, 0, 0, 0, 'Documento raiz, version inicial','111111111');

INSERT INTO AS_EntityDocumentRights (fkGroupId, fkDocumentId, AS_Rights) SELECT t1.ID , 0, '111111111' FROM (select ID from AS_Groups where AS_Name LIKE 'Administrators' ) t1;

INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#Codigo', 'es', 'Código');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#Id', 'es', 'Id');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#IdAgregadoSuperior', 'es', 'Id del Agregado Superior');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#IdLote', 'es', 'Id del Lote');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#IdTipoDoc', 'es', 'Id del Tipo de Documento');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#NombreCola', 'es', 'Nombre de Cola');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#NumIdentificador', 'es', 'Número de Identificador');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#NumSecuencia', 'es', 'Número de Secuencia');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#Prioridad', 'es', 'Prioridad');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#TCreado', 'es', 'Fecha de Creación');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#TEliminado', 'es', 'Fecha de Borrado');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#TModificado', 'es', 'Fecha de Modificación');

INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#Codigo', 'en', 'Code');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#Id', 'en', 'Id');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#IdAgregadoSuperior', 'en', 'Upper Level Aggregate Id');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#IdLote', 'en', 'Batch Id');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#IdTipoDoc', 'en', 'Document Type Id');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#NombreCola', 'en', 'Queue Name');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#NumIdentificador', 'en', 'Identificator Number');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#NumSecuencia', 'en', 'Sequence Number');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#Prioridad', 'en', 'Priority');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#TCreado', 'en', 'Creation Date');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#TEliminado', 'en', 'Deletion Date');
INSERT INTO AtributoAdminLocale (NombreAtributo, Locale, Nombre) VALUES ('#TModificado', 'en', 'Modification Date');

INSERT INTO VersionRepositorio(IdVersion,Fecha) VALUES ('1.9',0);

INSERT INTO  TipoDocumento  ( IdTipoDoc , Nombre , Descripcion , Plantilla , Item , TamEsperado , TCreado , TModificado , Eliminado , fkIdSesionBloquea ) VALUES (1,'System','Folder for system documents',NULL,0,0,320158917,320158917,0,NULL);
INSERT INTO  TipoDocumento  ( IdTipoDoc , Nombre , Descripcion , Plantilla , Item , TamEsperado , TCreado , TModificado , Eliminado , fkIdSesionBloquea ) VALUES (2,'Applications','Folder for application definitions',NULL,0,0,320158946,320158946,0,NULL);
INSERT INTO  TipoDocumento  ( IdTipoDoc , Nombre , Descripcion , Plantilla , Item , TamEsperado , TCreado , TModificado , Eliminado , fkIdSesionBloquea ) VALUES (3,'Application','Application definition',NULL,0,0,320158982,320158982,0,NULL);
INSERT INTO  TipoDocumento  ( IdTipoDoc , Nombre , Descripcion , Plantilla , Item , TamEsperado , TCreado , TModificado , Eliminado , fkIdSesionBloquea ) VALUES (4,'Resources','Folder for resources',NULL,0,1000,320158982,320158982,0,NULL);
INSERT INTO  TipoDocumento  ( IdTipoDoc , Nombre , Descripcion , Plantilla , Item , TamEsperado , TCreado , TModificado , Eliminado , fkIdSesionBloquea ) VALUES (5,'Resource','Resource',NULL,1,1000,320158982,320158982,0,NULL);
INSERT INTO  TipoDocumento  ( IdTipoDoc , Nombre , Descripcion , Plantilla , Item , TamEsperado , TCreado , TModificado , Eliminado , fkIdSesionBloquea ) VALUES (6,'Queries','Folder for queries saved',NULL,0,0,320158982,320158982,0,NULL);
INSERT INTO  TipoDocumento  ( IdTipoDoc , Nombre , Descripcion , Plantilla , Item , TamEsperado , TCreado , TModificado , Eliminado , fkIdSesionBloquea ) VALUES (7,'Query','Query saved',NULL,0,1000,320158982,320158982,0,NULL);

INSERT INTO  TipoDocLocale  ( IdTipoDoc , Locale , Nombre ) VALUES (1,'es','Sistema');
INSERT INTO  TipoDocLocale  ( IdTipoDoc , Locale , Nombre ) VALUES (2,'es','Aplicaciones');
INSERT INTO  TipoDocLocale  ( IdTipoDoc , Locale , Nombre ) VALUES (3,'es','Aplicación');
INSERT INTO  TipoDocLocale  ( IdTipoDoc , Locale , Nombre ) VALUES (4,'es','Recursos');
INSERT INTO  TipoDocLocale  ( IdTipoDoc , Locale , Nombre ) VALUES (5,'es','Recurso');
INSERT INTO  TipoDocLocale  ( IdTipoDoc , Locale , Nombre ) VALUES (6,'es','Consultas');

INSERT INTO  CaptionLocale  ( IdTipoDoc , Locale , Caption ) VALUES (0,'en','{#TypeName}');
INSERT INTO  CaptionLocale  ( IdTipoDoc , Locale , Caption ) VALUES (0,'es','{#TypeName}'); 
INSERT INTO  CaptionLocale  ( IdTipoDoc , Locale , Caption ) VALUES (1,'en','{#TypeName}'); 
INSERT INTO  CaptionLocale  ( IdTipoDoc , Locale , Caption ) VALUES (1,'es','{#TypeName}'); 
INSERT INTO  CaptionLocale  ( IdTipoDoc , Locale , Caption ) VALUES (2,'en','{#TypeName}');
INSERT INTO  CaptionLocale  ( IdTipoDoc , Locale , Caption ) VALUES (2,'es','{#TypeName}');
INSERT INTO  CaptionLocale  ( IdTipoDoc , Locale , Caption ) VALUES (3,'en','{name}');
INSERT INTO  CaptionLocale  ( IdTipoDoc , Locale , Caption ) VALUES (3,'es','{name}');
INSERT INTO  CaptionLocale  ( IdTipoDoc , Locale , Caption ) VALUES (4,'en','{name}');
INSERT INTO  CaptionLocale  ( IdTipoDoc , Locale , Caption ) VALUES (4,'es','{name}');
INSERT INTO  CaptionLocale  ( IdTipoDoc , Locale , Caption ) VALUES (5,'en','{name}');
INSERT INTO  CaptionLocale  ( IdTipoDoc , Locale , Caption ) VALUES (5,'es','{name}');
INSERT INTO  CaptionLocale  ( IdTipoDoc , Locale , Caption ) VALUES (6,'en','{#TypeName}');
INSERT INTO  CaptionLocale  ( IdTipoDoc , Locale , Caption ) VALUES (6,'es','{#TypeName}');


INSERT INTO  Atributo  ( IdAtrib , Nombre , Tipo , fkIdSesionBloquea ) VALUES (1,'name',2,NULL);
INSERT INTO  Atributo  ( IdAtrib , Nombre , Tipo , fkIdSesionBloquea ) VALUES (2,'description',2,NULL);
INSERT INTO  Atributo  ( IdAtrib , Nombre , Tipo , fkIdSesionBloquea ) VALUES (3,'url',2,NULL);
INSERT INTO  Atributo  ( IdAtrib , Nombre , Tipo , fkIdSesionBloquea ) VALUES (4,'icon',2,NULL);
INSERT INTO  Atributo  ( IdAtrib , Nombre , Tipo , fkIdSesionBloquea ) VALUES (5,'type',2,NULL);
INSERT INTO  Atributo  ( IdAtrib , Nombre , Tipo , fkIdSesionBloquea ) VALUES (6,'query',2,NULL);

INSERT INTO  Pertenece  ( fkIdAtrib , fkIdTipoDoc , Descripcion , Orden , ValorPorDefecto , Eliminado ) VALUES (1,3,'name of the application',1,'',0);
INSERT INTO  Pertenece  ( fkIdAtrib , fkIdTipoDoc , Descripcion , Orden , ValorPorDefecto , Eliminado ) VALUES (2,3,'Application description',1,'',0);
INSERT INTO  Pertenece  ( fkIdAtrib , fkIdTipoDoc , Descripcion , Orden , ValorPorDefecto , Eliminado ) VALUES (3,3,'Url of the application',1,'',0);
INSERT INTO  Pertenece  ( fkIdAtrib , fkIdTipoDoc , Descripcion , Orden , ValorPorDefecto , Eliminado ) VALUES (4,3,'Relative path to the image icon of the application',1,'',0);
INSERT INTO  Pertenece  ( fkIdAtrib , fkIdTipoDoc , Descripcion , Orden , ValorPorDefecto , Eliminado ) VALUES (1,4,'Resources name',1,'',0);
INSERT INTO  Pertenece  ( fkIdAtrib , fkIdTipoDoc , Descripcion , Orden , ValorPorDefecto , Eliminado ) VALUES (1,5,'Resource name',1,'',0);
INSERT INTO  Pertenece  ( fkIdAtrib , fkIdTipoDoc , Descripcion , Orden , ValorPorDefecto , Eliminado ) VALUES (5,5,'Resource type',1,'',0);
INSERT INTO  Pertenece  ( fkIdAtrib , fkIdTipoDoc , Descripcion , Orden , ValorPorDefecto , Eliminado ) VALUES (1,7,'name of the query',1,'',0);
INSERT INTO  Pertenece  ( fkIdAtrib , fkIdTipoDoc , Descripcion , Orden , ValorPorDefecto , Eliminado ) VALUES (6,7,'query string of the query',1,'',0);

INSERT INTO  Documento  ( IdDoc , fkIdTipoDoc , fkIdLote , fkIdDocPadre , NumSecuencia , NumIdentificador , Codigo , EspacioLibre , Version , Prioridad , TCreado , TModificado , ModificacionAdministrativa , TEliminado , fkIdSesionElimina , fkIdCola , fkIdSoporte , fkIdUltimoFichero , Clave , Completo , Bloqueado , CadenaDatosTemporales , TUltimoAcceso , NumAccesos , Mensaje , Restricciones ) VALUES (1,1,NULL,0,0,NULL,NULL,NULL,1,127,320160742,320160742,0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,0,'document created by the instalation script','000000000');
INSERT INTO  Documento  ( IdDoc , fkIdTipoDoc , fkIdLote , fkIdDocPadre , NumSecuencia , NumIdentificador , Codigo , EspacioLibre , Version , Prioridad , TCreado , TModificado , ModificacionAdministrativa , TEliminado , fkIdSesionElimina , fkIdCola , fkIdSoporte , fkIdUltimoFichero , Clave , Completo , Bloqueado , CadenaDatosTemporales , TUltimoAcceso , NumAccesos , Mensaje , Restricciones ) VALUES (2,2,NULL,1,0,NULL,NULL,NULL,1,127,320160745,320160745,0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,0,'document created by the instalation script','111111111');
INSERT INTO  Documento  ( IdDoc , fkIdTipoDoc , fkIdLote , fkIdDocPadre , NumSecuencia , NumIdentificador , Codigo , EspacioLibre , Version , Prioridad , TCreado , TModificado , ModificacionAdministrativa , TEliminado , fkIdSesionElimina , fkIdCola , fkIdSoporte , fkIdUltimoFichero , Clave , Completo , Bloqueado , CadenaDatosTemporales , TUltimoAcceso , NumAccesos , Mensaje , Restricciones ) VALUES (3,3,NULL,2,0,NULL,NULL,NULL,1,127,320160790,320160790,0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,0,'document created by the instalation script','111111111');
INSERT INTO  Documento  ( IdDoc , fkIdTipoDoc , fkIdLote , fkIdDocPadre , NumSecuencia , NumIdentificador , Codigo , EspacioLibre , Version , Prioridad , TCreado , TModificado , ModificacionAdministrativa , TEliminado , fkIdSesionElimina , fkIdCola , fkIdSoporte , fkIdUltimoFichero , Clave , Completo , Bloqueado , CadenaDatosTemporales , TUltimoAcceso , NumAccesos , Mensaje , Restricciones ) VALUES (4,3,NULL,2,0,NULL,NULL,NULL,1,127,320160820,320160820,0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,0,'document created by the instalation script','111111111');
INSERT INTO  Documento  ( IdDoc , fkIdTipoDoc , fkIdLote , fkIdDocPadre , NumSecuencia , NumIdentificador , Codigo , EspacioLibre , Version , Prioridad , TCreado , TModificado , ModificacionAdministrativa , TEliminado , fkIdSesionElimina , fkIdCola , fkIdSoporte , fkIdUltimoFichero , Clave , Completo , Bloqueado , CadenaDatosTemporales , TUltimoAcceso , NumAccesos , Mensaje , Restricciones ) VALUES (5,3,NULL,2,0,NULL,NULL,NULL,1,127,320160851,320160851,0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,0,'document created by the instalation script','111111111');
INSERT INTO  Documento  ( IdDoc , fkIdTipoDoc , fkIdLote , fkIdDocPadre , NumSecuencia , NumIdentificador , Codigo , EspacioLibre , Version , Prioridad , TCreado , TModificado , ModificacionAdministrativa , TEliminado , fkIdSesionElimina , fkIdCola , fkIdSoporte , fkIdUltimoFichero , Clave , Completo , Bloqueado , CadenaDatosTemporales , TUltimoAcceso , NumAccesos , Mensaje , Restricciones ) VALUES (6,3,NULL,2,0,NULL,NULL,NULL,1,127,320160871,320160871,0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,0,'document created by the instalation script','111111111');
INSERT INTO  Documento  ( IdDoc , fkIdTipoDoc , fkIdLote , fkIdDocPadre , NumSecuencia , NumIdentificador , Codigo , EspacioLibre , Version , Prioridad , TCreado , TModificado , ModificacionAdministrativa , TEliminado , fkIdSesionElimina , fkIdCola , fkIdSoporte , fkIdUltimoFichero , Clave , Completo , Bloqueado , CadenaDatosTemporales , TUltimoAcceso , NumAccesos , Mensaje , Restricciones ) VALUES (7,3,NULL,2,0,NULL,NULL,NULL,1,127,320160871,320160871,0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,0,'document created by the instalation script','111111111');
INSERT INTO  Documento  ( IdDoc , fkIdTipoDoc , fkIdLote , fkIdDocPadre , NumSecuencia , NumIdentificador , Codigo , EspacioLibre , Version , Prioridad , TCreado , TModificado , ModificacionAdministrativa , TEliminado , fkIdSesionElimina , fkIdCola , fkIdSoporte , fkIdUltimoFichero , Clave , Completo , Bloqueado , CadenaDatosTemporales , TUltimoAcceso , NumAccesos , Mensaje , Restricciones ) VALUES (8,4,NULL,1,0,NULL,NULL,NULL,1,127,320160871,320160871,0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,0,'document created by the instalation script','111111111');
INSERT INTO  Documento  ( IdDoc , fkIdTipoDoc , fkIdLote , fkIdDocPadre , NumSecuencia , NumIdentificador , Codigo , EspacioLibre , Version , Prioridad , TCreado , TModificado , ModificacionAdministrativa , TEliminado , fkIdSesionElimina , fkIdCola , fkIdSoporte , fkIdUltimoFichero , Clave , Completo , Bloqueado , CadenaDatosTemporales , TUltimoAcceso , NumAccesos , Mensaje , Restricciones ) VALUES (9,6,NULL,1,0,NULL,NULL,NULL,1,127,320160871,320160871,0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,0,'document created by the instalation script','111111111');

INSERT INTO Sesion (idSesion,Usuario,Codigo,TInicio,TFin,Modo) VALUES (1, 'admin', '', 0, 0, 0);

INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (3,1,1,'SecurityAdministration','',320160790,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (3,2,1,'Aplication for administer user groups and rights','',320160790,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (3,3,1,'url','',320160790,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (3,4,1,'icon','',320160790,1);

INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (4,1,1,'DocumentTypesAdministration','',320160820,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (4,2,1,'Aplication for manage document types','',320160820,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (4,3,1,'url','',320160820,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (4,4,1,'icon','',320160820,1);

INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (5,1,1,'VolumeAdministration','',320160851,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (5,2,1,'Aplication for manage volumes','',320160851,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (5,3,1,'url','',320160851,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (5,4,1,'icon','',320160851,1);

INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (6,1,1,'DocumentBrowser','',320160871,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (6,2,1,'Document Browser','',320160871,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (6,3,1,'url','',320160871,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (6,4,1,'icon','',320160871,1);

INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (7,1,1,'Configuration','',320160871,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (7,2,1,'Installation configuration','',320160871,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (7,3,1,'url','',320160871,1);
INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (7,4,1,'icon','',320160871,1);

INSERT INTO  ValorCadena  ( fkIdDoc , fkIdAtributo , Version , Valor , RestoValor , TOperacion , fkIdSesion ) VALUES (8,1,1,'Recursos','',320160790,1);

INSERT INTO EsHijo (iDTipoDocPadre, IdTipoDocHijo, CardinalidadMin, CardinalidadMax,Orden) VALUES (0,1,1,1,1);
INSERT INTO EsHijo (iDTipoDocPadre, IdTipoDocHijo, CardinalidadMin, CardinalidadMax,Orden) VALUES (1,2,1,1,1);
INSERT INTO EsHijo (iDTipoDocPadre, IdTipoDocHijo, CardinalidadMin, CardinalidadMax,Orden) VALUES (2,3,0,0,1);
INSERT INTO EsHijo (iDTipoDocPadre, IdTipoDocHijo, CardinalidadMin, CardinalidadMax,Orden) VALUES (1,4,1,1,1);
INSERT INTO EsHijo (iDTipoDocPadre, IdTipoDocHijo, CardinalidadMin, CardinalidadMax,Orden) VALUES (4,4,0,0,1);
INSERT INTO EsHijo (iDTipoDocPadre, IdTipoDocHijo, CardinalidadMin, CardinalidadMax,Orden) VALUES (4,5,0,0,1);
INSERT INTO EsHijo (iDTipoDocPadre, IdTipoDocHijo, CardinalidadMin, CardinalidadMax,Orden) VALUES (1,6,1,1,1);
INSERT INTO EsHijo (iDTipoDocPadre, IdTipoDocHijo, CardinalidadMin, CardinalidadMax,Orden) VALUES (6,7,0,0,1);

INSERT INTO TipoVista (IdTipoVista, fkIdTipoDoc, Nombre, Descripcion, TCreado, TModificado, Orden) VALUES (1,5,'vista','vista',0,0,1);

INSERT INTO  AS_EntityDocumentRights  ( AS_Rights , fkGroupId , fkUserId , fkDocumentId , AS_CreationTime , AS_ModificationTime , AS_DeletionTime ) VALUES ('111111111',2,0,1,320165151,320165151,NULL);