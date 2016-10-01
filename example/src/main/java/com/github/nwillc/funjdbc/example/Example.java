package com.github.nwillc.funjdbc.example;

import com.github.nwillc.funjdbc.DbAccessor;
import com.github.nwillc.funjdbc.UncheckedSQLException;
import com.github.nwillc.funjdbc.example.database.Database;
import com.github.nwillc.funjdbc.example.database.PersonTable;
import com.github.nwillc.funjdbc.example.model.Person;
import com.github.nwillc.funjdbc.functions.Extractor;
import com.github.nwillc.funjdbc.migrate.Manager;
import com.github.nwillc.funjdbc.utils.ExtractorFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class Example implements DbAccessor {
    private static final Logger LOGGER = Logger.getLogger(Example.class.getSimpleName());
    private final Database database;

    private Extractor<Person> personExtractor = new ExtractorFactory<Person>()
            .factory(Person::new)
            .add(Person::setPersonId, ResultSet::getLong, "PERSON_ID")
            .add(Person::setGivenName, ResultSet::getString, "GIVEN_NAME")
            .add(Person::setFamilyName, ResultSet::getString, "FAMILY_NAME")
            .getExtractor();

    public static void main(String[] args) throws Exception {
        LOGGER.info("Start");
        Example example = new Example();
        LOGGER.info("Find persion 1");
        System.out.println(example.find(1));

        Map<Long, Person> personMap;
        LOGGER.info("Get all people");
        personMap = example.query();
        personMap.values().forEach(System.out::println);
        LOGGER.info("Enrich with ages");
        example.enrich(personMap);
        personMap.values().forEach(System.out::println);
        LOGGER.info("Wait Elmer bust be like 60...");
        final Optional<Person> elmer = personMap.values().stream().filter(p -> p.getGivenName().equals("Elmer")).findFirst();
        if (!elmer.isPresent()) {
            throw new NullPointerException("Couldn't find Elmer");
        }
        example.setAge(elmer.get().getPersonId(), 60);
        example.enrich(personMap);
        personMap.values().forEach(System.out::println);
        LOGGER.info("end");
    }

    private Example() throws Exception {
        database = new Database();
        LOGGER.info("Create example schema using migration manager");
        Manager manager = Manager.getInstance();
        manager.setConnectionProvider(database);
        manager.enableMigrations();
        manager.add(new PersonTable());
        manager.doMigrations();
        LOGGER.info("Schema created");
    }

    @Override
    public Connection getConnection() throws SQLException {
        return database.getConnection();
    }

    private Optional<Person> find(int id) throws SQLException {
       return dbFind(personExtractor, "SELECT * FROM PERSON WHERE PERSON_ID = %d", id);
    }

    private Map<Long,Person> query() throws SQLException {
        return dbQuery(personExtractor, "SELECT * FROM PERSON").collect(Collectors.toMap(Person::getPersonId, person -> person));
    }

    private void enrich(Map<Long,Person> personMap) throws SQLException {
        dbEnrich(personMap, rs -> rs.getLong("PERSON_ID"), (p, rs) -> p.setAge(rs.getInt("AGE")), "SELECT PERSON_ID, AGE FROM PERSON");
    }

    private void setAge(long id, int age) throws SQLException {
        dbUpdate("UPDATE PERSON SET AGE = %d WHERE PERSON_ID = %d", age, id);
    }
}