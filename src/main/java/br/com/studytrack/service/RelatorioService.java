package br.com.studytrack.service;

import br.com.studytrack.dao.SessaoEstudoDAO;
import br.com.studytrack.model.SessaoEstudo;

import java.time.LocalDate;
import java.util.List;

public class RelatorioService {

    private SessaoEstudoDAO dao;

    public RelatorioService(SessaoEstudoDAO dao) {
        this.dao = dao;
    }


    public int calcularTempoPorDisciplina(
            String nomeDisciplina,
            LocalDate inicio,
            LocalDate fim) {

        List<SessaoEstudo> sessoes = dao.listarTodos();

        int totalMinutos = 0;

        for (SessaoEstudo sessao : sessoes) {

            boolean mesmaDisciplina = sessao.getDisciplina()
                    .getNome().equalsIgnoreCase(nomeDisciplina);

            boolean dentroDoPeriodo =
                    !sessao.getData().isBefore(inicio)
                            && !sessao.getData().isAfter(fim);

            if (mesmaDisciplina && dentroDoPeriodo) {
                totalMinutos += sessao.getDuracaoMinutos();
            }
        }

        return totalMinutos;
    }


    public int calcularExerciciosPorDisciplina(String nomeDisciplina) {

        List<SessaoEstudo> sessoes = dao.listarTodos();

        int totalExercicios = 0;

        for (SessaoEstudo sessao : sessoes) {

            boolean mesmaDisciplina = sessao.getDisciplina()
                    .getNome().equalsIgnoreCase(nomeDisciplina);

            if (mesmaDisciplina) {
                totalExercicios += sessao.getExerciciosResolvidos();
            }
        }

        return totalExercicios;
    }


    public void listarDisciplinasPorDia(LocalDate data) {

        List<SessaoEstudo> sessoes = dao.listarTodos();

        boolean encontrou = false;

        System.out.println("\nDisciplinas estudadas em " + data + ":");

        for (SessaoEstudo sessao : sessoes) {

            if (sessao.getData().equals(data)) {

                String nomeDisciplina = sessao.getDisciplina().getNome();

                System.out.println("- " + nomeDisciplina);

                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhuma sessão encontrada nessa data.");
        }
    }


    public void disciplinaMaisEstudada() {

        List<SessaoEstudo> sessoes = dao.listarTodos();

        String disciplinaMaisEstudada = "";
        int maiorTempo = 0;

        for (SessaoEstudo sessao : sessoes) {

            String nomeDisciplina = sessao.getDisciplina().getNome();

            int tempoTotal = calcularTempoPorDisciplina(
                    nomeDisciplina,
                    LocalDate.MIN,
                    LocalDate.MAX
            );

            if (tempoTotal > maiorTempo) {
                maiorTempo = tempoTotal;
                disciplinaMaisEstudada = nomeDisciplina;
            }
        }

        if (maiorTempo == 0) {
            System.out.println("Nenhuma sessão de estudo encontrada.");
        } else {

            int horas = maiorTempo / 60;
            int minutos = maiorTempo % 60;

            System.out.println("\nDisciplina mais estudada: "
                    + disciplinaMaisEstudada);

            System.out.println("Tempo total: "
                    + horas + "h " + minutos + "min");
        }
    }


    public void exibirHistorico() {

        List<SessaoEstudo> sessoes = dao.listarTodos();

        if (sessoes.isEmpty()) {
            System.out.println("\nNenhuma sessão de estudo cadastrada.");
            return;
        }

        System.out.println("\n========== HISTÓRICO DE ESTUDOS ==========");

        for (SessaoEstudo sessao : sessoes) {

            System.out.println("\nID: " + sessao.getId());

            System.out.println("Estudante: "
                    + sessao.getEstudante().getNome());

            System.out.println("Disciplina: "
                    + sessao.getDisciplina().getNome());

            System.out.println("Conteúdo: "
                    + sessao.getConteudo().getNome());

            System.out.println("Data: " + sessao.getData());

            System.out.println("Duração: "
                    + sessao.getDuracaoMinutos() + " minutos");

            System.out.println("Exercícios resolvidos: "
                    + sessao.getExerciciosResolvidos());

            System.out.println("Status: " + sessao.getStatus());

            System.out.println("------------------------------------------");
        }
    }
}
