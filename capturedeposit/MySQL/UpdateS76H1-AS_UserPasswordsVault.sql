CREATE TABLE AS_UserPasswordsVault (
    AS_PinHash varchar(256) NOT NULL,
    fkUserId integer(25) ,
	updateTime int(11) DEFAULT NULL,		
	passwordId integer(25) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	CONSTRAINT fk_UserPasswordsVault_UserId FOREIGN KEY (fkUserId) REFERENCES AS_Users(Id) ON DELETE CASCADE,
	INDEX (fkUserId, updateTime)
)  ENGINE=InnoDB DEFAULT CHARSET=latin1;