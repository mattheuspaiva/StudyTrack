package br.com.studytrack.dao;

import br.com.studytrack.model.*;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;
import org.neo4j.driver.Values;
import org.neo4j.driver.Value;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessaoEstudoDAO {

    private Driver driver;

    public SessaoEstudoDAO(Driver driver) {
        this.driver = driver;
    }

    // Cadastrar sessão de estudo
    public void cadastrar(SessaoEstudo sessao) {

        String comando =
                "MERGE (e:Estudante {id: $estudanteId}) " +
                        "SET e.nome = $estudanteNome " +

                        "MERGE (d:Disciplina {id: $disciplinaId}) " +
                        "SET d.nome = $disciplinaNome " +

                        "MERGE (c:Conteudo {id: $conteudoId}) " +
                        "SET c.nome = $conteudoNome " +

                        "CREATE (s:SessaoEstudo { " +
                        "id: $id, " +
                        "data: $data, " +
                        "duracaoMinutos: $duracao, " +
                        "exerciciosResolvidos: $exercicios, " +
                        "status: $status }) " +

                        "CREATE (e)-[:REALIZOU]->(s) " +
                        "CREATE (s)-[:PERTENCE_A]->(d) " +
                        "CREATE (s)-[:ABORDOU]->(c)";

        try (Session session = driver.session(
                SessionConfig.forDatabase("studytrack"))) {

            Result resultado = session.run(
                    comando,
                    parametros(sessao)
            );

            resultado.consume();

            System.out.println("Sessão cadastrada com sucesso!");
        }
    }

    // Listar todas as sessões
    public List<SessaoEstudo> listarTodos() {

        List<SessaoEstudo> sessoes = new ArrayList<>();

        String comando =
                "MATCH (e:Estudante)-[:REALIZOU]->(s:SessaoEstudo) " +
                        "MATCH (s)-[:PERTENCE_A]->(d:Disciplina) " +
                        "MATCH (s)-[:ABORDOU]->(c:Conteudo) " +

                        "RETURN s.id AS id, " +
                        "e.id AS estudanteId, e.nome AS estudanteNome, " +
                        "d.id AS disciplinaId, d.nome AS disciplinaNome, " +
                        "c.id AS conteudoId, c.nome AS conteudoNome, " +
                        "s.data AS data, " +
                        "s.duracaoMinutos AS duracao, " +
                        "s.exerciciosResolvidos AS exercicios, " +
                        "s.status AS status " +

                        "ORDER BY s.data DESC";

        try (Session session = driver.session(
                SessionConfig.forDatabase("studytrack"))) {

            Result resultado = session.run(comando);

            while (resultado.hasNext()) {

                Record registro = resultado.next();

                SessaoEstudo sessao = converterRegistro(registro);

                sessoes.add(sessao);
            }

            resultado.consume();
        }

        return sessoes;
    }

    // Buscar sessão pelo ID
    public Optional<SessaoEstudo> buscarPorId(int id) {

        String comando =
                "MATCH (e:Estudante)-[:REALIZOU]->(s:SessaoEstudo {id: $id}) " +
                        "MATCH (s)-[:PERTENCE_A]->(d:Disciplina) " +
                        "MATCH (s)-[:ABORDOU]->(c:Conteudo) " +

                        "RETURN s.id AS id, " +
                        "e.id AS estudanteId, e.nome AS estudanteNome, " +
                        "d.id AS disciplinaId, d.nome AS disciplinaNome, " +
                        "c.id AS conteudoId, c.nome AS conteudoNome, " +
                        "s.data AS data, " +
                        "s.duracaoMinutos AS duracao, " +
                        "s.exerciciosResolvidos AS exercicios, " +
                        "s.status AS status";

        try (Session session = driver.session(
                SessionConfig.forDatabase("studytrack"))) {

            Result resultado = session.run(
                    comando,
                    Values.parameters("id", id)
            );

            if (resultado.hasNext()) {

                Record registro = resultado.next();

                SessaoEstudo sessao = converterRegistro(registro);

                resultado.consume();

                return Optional.of(sessao);
            }

            resultado.consume();
        }

        return Optional.empty();
    }

    // Atualizar sessão
    public boolean atualizar(SessaoEstudo sessao) {

        String comando =
                "MATCH (s:SessaoEstudo {id: $id}) " +
                        "SET s.data = $data, " +
                        "s.duracaoMinutos = $duracao, " +
                        "s.exerciciosResolvidos = $exercicios, " +
                        "s.status = $status " +
                        "RETURN s.id AS id";

        try (Session session = driver.session(
                SessionConfig.forDatabase("studytrack"))) {

            Result resultado = session.run(
                    comando,
                    parametros(sessao)
            );

            boolean encontrou = resultado.hasNext();

            resultado.consume();

            return encontrou;
        }
    }

    // Excluir sessão
    public boolean excluir(int id) {

        String comando =
                "MATCH (s:SessaoEstudo {id: $id}) " +
                        "DETACH DELETE s " +
                        "RETURN 1 AS resultado";

        try (Session session = driver.session(
                SessionConfig.forDatabase("studytrack"))) {

            Result resultado = session.run(
                    comando,
                    Values.parameters("id", id)
            );

            boolean encontrou = resultado.hasNext();

            resultado.consume();

            return encontrou;
        }
    }

    // Converter dados do banco para objeto Java
    private SessaoEstudo converterRegistro(Record registro) {

        Estudante estudante = new Estudante(
                registro.get("estudanteId").asInt(),
                registro.get("estudanteNome").asString()
        );

        Disciplina disciplina = new Disciplina(
                registro.get("disciplinaId").asInt(),
                registro.get("disciplinaNome").asString()
        );

        Conteudo conteudo = new Conteudo(
                registro.get("conteudoId").asInt(),
                registro.get("conteudoNome").asString()
        );

        return new SessaoEstudo(
                registro.get("id").asInt(),
                estudante,
                disciplina,
                conteudo,
                LocalDate.parse(registro.get("data").asString()),
                registro.get("duracao").asInt(),
                registro.get("exercicios").asInt(),
                registro.get("status").asString()
        );
    }

    // Preparar os parâmetros para enviar ao Neo4j
    private Value parametros(SessaoEstudo sessao) {

        return Values.parameters(
                "id", sessao.getId(),

                "estudanteId", sessao.getEstudante().getId(),
                "estudanteNome", sessao.getEstudante().getNome(),

                "disciplinaId", sessao.getDisciplina().getId(),
                "disciplinaNome", sessao.getDisciplina().getNome(),

                "conteudoId", sessao.getConteudo().getId(),
                "conteudoNome", sessao.getConteudo().getNome(),

                "data", sessao.getData().toString(),
                "duracao", sessao.getDuracaoMinutos(),
                "exercicios", sessao.getExerciciosResolvidos(),
                "status", sessao.getStatus()
        );
    }
}
