package com.github.nwillc.funjdbc.example.database;

import com.github.nwillc.funjdbc.migrate.MigrationBase;

public class PersonTable extends MigrationBase {
    private static final String CREATE = "CREATE TABLE PERSON (\n" +
            "PERSON_ID IDENTITY,\n" +
            "GIVEN_NAME CHAR(25),\n" +
            "FAMILY_NAME CHAR(50),\n" +
            "AGE INTEGER\n" +
            ")\n";
    private static final String INSERT = "INSERT INTO PERSON (GIVEN_NAME,FAMILY_NAME, AGE) VALUES ('%s','%s', %d)";

    @Override
    public String getDescription() {
        return "Table representing a person";
    }

    @Override
    public String getIdentifier() {
        return "0";
    }

    @Override
    public void perform() throws Exception {
        dbUpdate(CREATE);
        dbUpdate(INSERT, "Wile", "Coyote", 25);
        dbUpdate(INSERT, "Elmer", "Fud", 50);
    }
}
