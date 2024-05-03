CREATE TABLE REPOSITORY (
       Name VARCHAR2(250) NOT NULL
     , DESCRIPTION VARCHAR2(2500)
     , PRIMARY KEY (Name)
);

CREATE TABLE MESSAGE (
       ID INT NOT NULL
     , HASH_CODE INTEGER NOT NULL
     , MESSAGE_VALUE VARCHAR2(2500) NOT NULL
     , CONSTRAINT UQ_MESSAGE_VALUE UNIQUE (MESSAGE_VALUE)
     , PRIMARY KEY (ID)
);

CREATE INDEX IX_MESSAGE_HASH_CODE ON MESSAGE (HASH_CODE);

CREATE TABLE PACKAGE (
       REPOSITORY VARCHAR2(250) NOT NULL
     , NAME_ID INTEGER NOT NULL
     , PRIMARY KEY (REPOSITORY, NAME_ID)
     , CONSTRAINT FK_PACKAGE_REPOSITORY FOREIGN KEY (REPOSITORY)
                  REFERENCES REPOSITORY (Name) ON DELETE CASCADE
     , CONSTRAINT FK_PACKAGE_NAME FOREIGN KEY (NAME_ID)
                  REFERENCES MESSAGE (ID)
);

CREATE TABLE COMPILATION_UNIT (
       ID INTEGER NOT NULL PRIMARY KEY
     , REPOSITORY VARCHAR2(250) NOT NULL
     , PACKAGE INTEGER NOT NULL
     , PATH VARCHAR2(1000) NOT NULL
     , NAME VARCHAR2(200) NOT NULL
     , COMPILATION_UNIT_SIZE DOUBLE PRECISION NOT NULL
     , CHECKSUM DOUBLE PRECISION NOT NULL
     , STORE_LEVEL SMALLINT DEFAULT '0' NOT NULL
     , ROOT VARCHAR2(1000) NOT NULL
     , REVISION VARCHAR2(250)
     , CU_SOURCE BLOB
     , CONSTRAINT FK_COMPILATION_UNIT_PACKAGE FOREIGN KEY (REPOSITORY, PACKAGE)
                  REFERENCES PACKAGE (REPOSITORY, NAME_ID) ON DELETE CASCADE
);

CREATE TABLE TYPE (
       COMPILATION_UNIT_ID INTEGER NOT NULL
     , NAME INTEGER NOT NULL
     , DECLARING_TYPE INTEGER
     , SUPERCLASS VARCHAR2(2500)
     , ENCLOSING_SCOPE_SIGNATURE VARCHAR2(2500)
     , IMPLEMENTOR INTEGER NOT NULL
     , SUPERCLASS_SIGNATURE VARCHAR2(2500)
     , STORE_LEVEL SMALLINT DEFAULT 0 NOT NULL
     , PRIMARY KEY (COMPILATION_UNIT_ID, NAME)
     , CONSTRAINT FK_TYPE_COMPILATION_UNIT FOREIGN KEY (COMPILATION_UNIT_ID)
                  REFERENCES COMPILATION_UNIT (ID) ON DELETE CASCADE
     , CONSTRAINT FK_TYPE_NAME FOREIGN KEY (NAME)
                  REFERENCES MESSAGE (ID)
     , CONSTRAINT FK_TYPE_IMPLEMENTOR FOREIGN KEY (IMPLEMENTOR)
                  REFERENCES MESSAGE (ID)
     , CONSTRAINT "FK_TYPE_SQLC$DECLARING_TYPE" FOREIGN KEY (COMPILATION_UNIT_ID, DECLARING_TYPE)
                  REFERENCES TYPE (COMPILATION_UNIT_ID, NAME) ON DELETE CASCADE
);

CREATE TABLE OPERATION (
       ID INTEGER NOT NULL PRIMARY KEY
     , COMPILATION_UNIT_ID INTEGER NOT NULL
     , DECLARING_TYPE INTEGER NOT NULL
     , NAME VARCHAR2(300) NOT NULL
     , RETURN_TYPE VARCHAR2(2500)
     , PARAMETERS VARCHAR2(2500)
     , MODIFIERS VARCHAR2(100)
     , SIGNATURE VARCHAR2(2500) NOT NULL
     , CONSTRAINT "FK_OPERATION_SQLC$TYPE" FOREIGN KEY (COMPILATION_UNIT_ID, DECLARING_TYPE)
                  REFERENCES TYPE (COMPILATION_UNIT_ID, NAME) ON DELETE CASCADE
);

CREATE TABLE SCAN (
       REPOSITORY VARCHAR2(250) NOT NULL
     , ID INTEGER NOT NULL
     , SCAN_DATE DATE
     , SCAN_SIZE INTEGER
     , DESCRIPTION VARCHAR2(2500)
     , BYTE_SIZE DOUBLE PRECISION
     , CHECKSUM DOUBLE PRECISION
     , DEPENDENCY_LOADED NUMBER DEFAULT '0'
     , PRIMARY KEY (REPOSITORY, ID)
     , CONSTRAINT FK_SCAN_REPOSITORY FOREIGN KEY (REPOSITORY)
                  REFERENCES REPOSITORY (Name) ON DELETE CASCADE
);

CREATE TABLE COMPILATION_UNIT_SCAN (
       REPOSITORY VARCHAR2(250) NOT NULL
     , SCAN_ID INTEGER NOT NULL
     , COMPILATION_UNIT_ID INTEGER NOT NULL
     , PRIMARY KEY (REPOSITORY, SCAN_ID, COMPILATION_UNIT_ID)
     , CONSTRAINT FK_COMPILATION_UNIT_SCAN_SCAN FOREIGN KEY (REPOSITORY, SCAN_ID)
                  REFERENCES SCAN (REPOSITORY, ID) ON DELETE CASCADE
     , CONSTRAINT FK_COMPILATION_UNIT_SCAN FOREIGN KEY (COMPILATION_UNIT_ID)
                  REFERENCES COMPILATION_UNIT (ID) ON DELETE CASCADE
);

CREATE TABLE FIELD (
       COMPILATION_UNIT_ID INTEGER NOT NULL
     , DECLARING_TYPE INTEGER NOT NULL
     , NAME VARCHAR2(250) NOT NULL
     , TYPE VARCHAR2(2500)
     , SIGNATURE VARCHAR2(2500) NOT NULL
     , PRIMARY KEY (COMPILATION_UNIT_ID, DECLARING_TYPE, NAME)
     , CONSTRAINT "FK_FIELD_SQLC$TYPE" FOREIGN KEY (COMPILATION_UNIT_ID, DECLARING_TYPE)
                  REFERENCES TYPE (COMPILATION_UNIT_ID, NAME) ON DELETE CASCADE
);

CREATE TABLE TYPE_DEPENDENCY (
       SUPPLIER_NAME INTEGER NOT NULL
     , CLIENT_CU_ID INTEGER NOT NULL
     , CLIENT_NAME INTEGER NOT NULL
     , SUPPLIER_PACKAGE INTEGER NOT NULL
     , CLIENT_PACKAGE INTEGER NOT NULL
     , PRIMARY KEY (SUPPLIER_NAME, CLIENT_CU_ID, CLIENT_NAME)
     , CONSTRAINT FK_DEPENDENCY_CLIENT FOREIGN KEY (CLIENT_CU_ID, CLIENT_NAME)
                  REFERENCES TYPE (COMPILATION_UNIT_ID, NAME) ON DELETE CASCADE
     , CONSTRAINT FK_TYPE_DEPENDENCY_SUPP_NAME FOREIGN KEY (SUPPLIER_NAME)
                  REFERENCES MESSAGE (ID)
     , CONSTRAINT FK_TYPE_DEPENDENCY_SUPP_PKG FOREIGN KEY (SUPPLIER_PACKAGE)
                  REFERENCES MESSAGE (ID)
     , CONSTRAINT FK_TYPE_DEPENDENCY_CL_PKG FOREIGN KEY (CLIENT_NAME)
                  REFERENCES MESSAGE (ID)
);

CREATE TABLE CALL_TRACE (
       ID INTEGER NOT NULL PRIMARY KEY
     , CALLER INTEGER NOT NULL
     , CALLEE_NAME VARCHAR2(2500)
     , CALLEE INTEGER
     , CONSTRAINT FK_CALLED_OPERATION_CALLER FOREIGN KEY (CALLER)
                  REFERENCES OPERATION (ID) ON DELETE CASCADE
     , CONSTRAINT FK_CALL_TRACE_CALLEE FOREIGN KEY (CALLEE)
                  REFERENCES OPERATION (ID) ON DELETE CASCADE
);

CREATE TABLE OVERRIDE (
       ID INTEGER NOT NULL PRIMARY KEY
     , SPECIFICATION INTEGER
     , SPECIFICATION_SIGNATURE VARCHAR2(2500)
     , IMPLEMENTATION INTEGER NOT NULL
     , CONSTRAINT FK_OVERRIDE_SPECIFICATION FOREIGN KEY (SPECIFICATION)
                  REFERENCES OPERATION (ID) ON DELETE CASCADE
     , CONSTRAINT FK_OVERRIDE_IMPLEMENTATION FOREIGN KEY (IMPLEMENTATION)
                  REFERENCES OPERATION (ID) ON DELETE CASCADE
);

CREATE TABLE INTERFACE (
       COMPILATION_UNIT_ID INTEGER NOT NULL
     , DECLARING_TYPE INTEGER NOT NULL
     , INTERFACE_NAME INTEGER NOT NULL
     , SIGNATURE VARCHAR2(2500)
     , PRIMARY KEY (COMPILATION_UNIT_ID, DECLARING_TYPE, INTERFACE_NAME)
     , CONSTRAINT FK_INTERFACE_NAME FOREIGN KEY (INTERFACE_NAME)
                  REFERENCES MESSAGE (ID)
     , CONSTRAINT "FK_INTERFACE_SQLC$TYPE" FOREIGN KEY (COMPILATION_UNIT_ID, DECLARING_TYPE)
                  REFERENCES TYPE (COMPILATION_UNIT_ID, NAME) ON DELETE CASCADE
);


CREATE TABLE PRIMARY_KEY (
       KEY_NAME VARCHAR(50) NOT NULL
     , KEY_VALUE INTEGER DEFAULT '0' NOT NULL
     , PRIMARY KEY (KEY_NAME)
);

CREATE TABLE RESULT (
       ID INTEGER NOT NULL
     , TYPE VARCHAR(100) NOT NULL
     , CODEBASE NUMBER DEFAULT '0'
     , RESULT_DATE DATE
     , MAX_SEVERITY NUMBER
     , REVIEWS NUMBER DEFAULT '0'
     , VIOLATION_LEVEL NUMBER DEFAULT '0'
     , VIOLATIONS NUMBER DEFAULT '0'
     , WAIVED_VIOLATIONS NUMBER DEFAULT '0'
     , NAME VARCHAR(2500)
     , HAS_WARNINGS INTEGER DEFAULT 0
     , COMMITED NUMBER DEFAULT '0' NOT NULL
     , COMPILATION_UNIT INTEGER
     , IS_NEW NUMBER DEFAULT '0' NOT NULL
     , PRIMARY KEY (ID)
);

CREATE TABLE METRIC (
       RESULT_ID INTEGER DEFAULT '0' NOT NULL
     , NAME VARCHAR(250) NOT NULL
     , PRIMARY KEY (RESULT_ID, NAME)
     , CONSTRAINT FK_METRIC_RESULT FOREIGN KEY (RESULT_ID)
                  REFERENCES RESULT (ID) ON DELETE CASCADE
);

CREATE TABLE REPORT (
       ID INTEGER NOT NULL
     , NAME VARCHAR(250)
     , REPORT_NUMBER INTEGER
     , RESULT_ID INTEGER
     , EXECUTION_TIME NUMBER
     , HOST_NAME VARCHAR(2500)
     , HOST_ID VARCHAR(250)
     , DESCRIPTION VARCHAR(2500)
     , PRIMARY KEY (ID)
     , CONSTRAINT FK_REPORT_RESULT FOREIGN KEY (RESULT_ID)
                  REFERENCES RESULT (ID)
);

CREATE TABLE INSPECTOR (
       REPORT_ID INTEGER NOT NULL
     , NAME VARCHAR(15) NOT NULL
     , SEVERITY SMALLINT NOT NULL
     , DESCRIPTION VARCHAR(2500)
     , CONFIG_INFO VARCHAR(2500)
     , PRIMARY KEY (REPORT_ID, NAME)
     , CONSTRAINT FK_INSPECTOR_REPORT FOREIGN KEY (REPORT_ID)
                  REFERENCES REPORT (ID) ON DELETE CASCADE
);

CREATE TABLE ANNOTATION (
       ID INTEGER NOT NULL PRIMARY KEY
     , RESULT_ID INTEGER NOT NULL
     , HANDLE VARCHAR(2500)
     , CONSTRAINT FK_ANNOTATION_RESULT FOREIGN KEY (RESULT_ID)
                  REFERENCES RESULT (ID) ON DELETE CASCADE
);

CREATE TABLE MEASUREMENT (
       ID INTEGER NOT NULL PRIMARY KEY
     , RESULT_ID INTEGER DEFAULT '0' NOT NULL
     , NAME VARCHAR(250) NOT NULL
     , MEASUREMENT_VALUE NUMBER NOT NULL
     , SOURCE VARCHAR(2500)
     , LINE INTEGER DEFAULT '0' NOT NULL
     , COL INTEGER DEFAULT '0' NOT NULL
     , CONSTRAINT FK_MEASUREMENT_METRIC FOREIGN KEY (RESULT_ID, NAME)
                  REFERENCES METRIC (RESULT_ID, NAME) ON DELETE CASCADE
);

CREATE INDEX IX_MEASUREMENT_VALUE ON MEASUREMENT (MEASUREMENT_VALUE);

CREATE TABLE RESULT_NET (
       PARENT INTEGER NOT NULL
     , CHILD INTEGER NOT NULL
     , PRIMARY KEY (PARENT, CHILD)
     , CONSTRAINT FK_RESULT_NET_PARENT FOREIGN KEY (PARENT)
                  REFERENCES RESULT (ID) ON DELETE CASCADE
     , CONSTRAINT FK_RESULT_NET_CHILD FOREIGN KEY (CHILD)
                  REFERENCES RESULT (ID) ON DELETE CASCADE
);

CREATE TABLE KINDRED (
       ANCESTOR INTEGER NOT NULL
     , DESCENDANT INTEGER NOT NULL
     , PRIMARY KEY (ANCESTOR, DESCENDANT)
     , CONSTRAINT FK_KINDRED_ANCESTOR FOREIGN KEY (ANCESTOR)
                  REFERENCES RESULT (ID) ON DELETE CASCADE
     , CONSTRAINT FK_KINDRED_DESCENDANT FOREIGN KEY (DESCENDANT)
                  REFERENCES RESULT (ID) ON DELETE CASCADE
);

CREATE TABLE VIOLATION (
       ID INTEGER NOT NULL PRIMARY KEY
     , RESULT_ID INTEGER DEFAULT '0' NOT NULL
     , REPORT_ID INTEGER DEFAULT '0' NOT NULL
     , INSPECTOR VARCHAR(15)
     , MESSAGE_ID INT
     , SOURCE_ID INT
     , LINE INT DEFAULT '0' NOT NULL
     , COL INT DEFAULT '0' NOT NULL
     , SIGNATURE_POSTFIX VARCHAR(2500)
     , WAIVER_REASON INT
     , WAIVER_EXPIRES DATE
     , VIOLATION_TYPE NUMBER DEFAULT '0' NOT NULL
     , CONSTRAINT FK_VIOLATION_RESULT FOREIGN KEY (RESULT_ID)
                  REFERENCES RESULT (ID) ON DELETE CASCADE
     , CONSTRAINT FK_VIOLATION_INSPECTOR FOREIGN KEY (REPORT_ID, INSPECTOR)
                  REFERENCES INSPECTOR (REPORT_ID, NAME) ON DELETE CASCADE
     , CONSTRAINT FK_VIOLATION_MESSAGE FOREIGN KEY (MESSAGE_ID)
                  REFERENCES MESSAGE (ID)
     , CONSTRAINT FK_VIOLATION_WAIVER_REASON FOREIGN KEY (WAIVER_REASON)
                  REFERENCES MESSAGE (ID)
);

ALTER TABLE RESULT ADD CONSTRAINT FK_RESULT_COMPILATION_UNIT FOREIGN KEY (COMPILATION_UNIT)
                  REFERENCES COMPILATION_UNIT (ID) ON DELETE CASCADE;

ALTER TABLE VIOLATION ADD CONSTRAINT FK_VIOLATION_COMPILATION_UNIT FOREIGN KEY (SOURCE_ID)
                  REFERENCES COMPILATION_UNIT (ID) ON DELETE CASCADE;