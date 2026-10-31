
package br.com.studytrack.util;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;

public class Neo4jConfig {

    private static final String URI = "neo4j://127.0.0.1:7687";
    private static final String USUARIO = "neo4j";
    private static final String SENHA = "studytrack";
    private static final String BANCO = "studytrack";

    private final Driver driver;

    public Neo4jConfig() {
        driver = GraphDatabase.driver(URI, AuthTokens.basic(USUARIO, SENHA));
    }

    public Driver getDriver() {
        return driver;
    }

    public void testarConexao() {

        try (Session session = driver.session(
                SessionConfig.forDatabase(BANCO))) {

            session.run("RETURN 1").consume();

            System.out.println(
                    "Conectado ao Neo4j com sucesso!"
            );

        }
    }

    public void fecharConexao() {
        driver.close();
    }
}
