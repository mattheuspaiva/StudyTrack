package br.com.studytrack.view;

import br.com.studytrack.model.*;
import br.com.studytrack.service.SessaoEstudoService;
import br.com.studytrack.service.RelatorioService;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;

public class MenuView {

    private Scanner scanner;
    private SessaoEstudoService service;
    private RelatorioService relatorioService;

    public MenuView(Scanner scanner,
                    SessaoEstudoService service,
                    RelatorioService relatorioService) {

        this.scanner = scanner;
        this.service = service;
        this.relatorioService = relatorioService;
    }

    // Exibir o menu principal
    public void exibirMenu() {

        int opcao;

        do {
            System.out.println("\n========== STUDYTRACK ==========");
            System.out.println("1 - Cadastrar sessão de estudo");
            System.out.println("2 - Atualizar sessão");
            System.out.println("3 - Excluir sessão");

            System.out.println("\n========== RELATÓRIOS ==========");
            System.out.println("4 - Tempo estudado por disciplina");
            System.out.println("5 - Exercícios resolvidos por disciplina");
            System.out.println("6 - Disciplinas estudadas por dia");
            System.out.println("7 - Disciplina mais estudada");
            System.out.println("8 - Histórico de estudos");

            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = lerInteiro();

            switch (opcao) {

                case 1:
                    cadastrar();
                    break;

                case 2:
                    atualizar();
                    break;

                case 3:
                    excluir();
                    break;

                case 4:
                    relatorioTempoDisciplina();
                    break;

                case 5:
                    relatorioExercicios();
                    break;

                case 6:
                    relatorioDisciplinasPorDia();
                    break;

                case 7:
                    relatorioService.disciplinaMaisEstudada();
                    break;

                case 8:
                    relatorioService.exibirHistorico();
                    break;

                case 0:
                    System.out.println("Programa encerrado.");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }

        } while (opcao != 0);
    }

    // Cadastrar sessão
    private void cadastrar() {

        try {
            System.out.println("\n--- CADASTRAR SESSÃO ---");

            System.out.print("ID da sessão: ");
            int id = lerInteiro();

            System.out.print("ID do estudante: ");
            int estudanteId = lerInteiro();

            System.out.print("Nome do estudante: ");
            String estudanteNome = scanner.nextLine();

            System.out.print("ID da disciplina: ");
            int disciplinaId = lerInteiro();

            System.out.print("Nome da disciplina: ");
            String disciplinaNome = scanner.nextLine();

            System.out.print("ID do conteúdo: ");
            int conteudoId = lerInteiro();

            System.out.print("Nome do conteúdo: ");
            String conteudoNome = scanner.nextLine();

            System.out.print("Data (AAAA-MM-DD): ");
            LocalDate data = LocalDate.parse(scanner.nextLine());

            System.out.print("Duração em minutos: ");
            int duracao = lerInteiro();

            System.out.print("Exercícios resolvidos: ");
            int exercicios = lerInteiro();

            System.out.print("Status: ");
            String status = scanner.nextLine();

            Estudante estudante = new Estudante(
                    estudanteId, estudanteNome
            );

            Disciplina disciplina = new Disciplina(
                    disciplinaId, disciplinaNome
            );

            Conteudo conteudo = new Conteudo(
                    conteudoId, conteudoNome
            );

            SessaoEstudo sessao = new SessaoEstudo(
                    id,
                    estudante,
                    disciplina,
                    conteudo,
                    data,
                    duracao,
                    exercicios,
                    status
            );

            service.cadastrar(sessao);

            System.out.println("Cadastro realizado!");

        } catch (Exception e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
        }
    }

    // Atualizar sessão
    private void atualizar() {

        try {
            System.out.print("\nID da sessão que deseja atualizar: ");
            int id = lerInteiro();

            Optional<SessaoEstudo> resultado = service.buscarPorId(id);

            if (resultado.isEmpty()) {
                System.out.println("Sessão não encontrada.");
                return;
            }

            SessaoEstudo sessao = resultado.get();

            System.out.print("Nova data (AAAA-MM-DD): ");
            sessao.setData(LocalDate.parse(scanner.nextLine()));

            System.out.print("Nova duração em minutos: ");
            sessao.setDuracaoMinutos(lerInteiro());

            System.out.print("Nova quantidade de exercícios: ");
            sessao.setExerciciosResolvidos(lerInteiro());

            System.out.print("Novo status: ");
            sessao.setStatus(scanner.nextLine());

            if (service.atualizar(sessao)) {
                System.out.println("Sessão atualizada com sucesso!");
            } else {
                System.out.println("Não foi possível atualizar a sessão.");
            }

        } catch (Exception e) {
            System.out.println("Erro ao atualizar: " + e.getMessage());
        }
    }

    // Excluir sessão
    private void excluir() {

        try {
            System.out.print("\nID da sessão que deseja excluir: ");
            int id = lerInteiro();

            if (service.excluir(id)) {
                System.out.println("Sessão excluída com sucesso!");
            } else {
                System.out.println("Sessão não encontrada.");
            }

        } catch (Exception e) {
            System.out.println("Erro ao excluir: " + e.getMessage());
        }
    }

    // Relatório de tempo estudado por disciplina
    private void relatorioTempoDisciplina() {

        try {
            System.out.print("\nNome da disciplina: ");
            String disciplina = scanner.nextLine();

            System.out.print("Data inicial (AAAA-MM-DD): ");
            LocalDate inicio = LocalDate.parse(scanner.nextLine());

            System.out.print("Data final (AAAA-MM-DD): ");
            LocalDate fim = LocalDate.parse(scanner.nextLine());

            if (fim.isBefore(inicio)) {
                System.out.println("A data final não pode ser anterior à inicial.");
                return;
            }

            int minutos = relatorioService.calcularTempoPorDisciplina(
                    disciplina, inicio, fim
            );

            int horas = minutos / 60;
            int restante = minutos % 60;

            System.out.println("\nTempo estudado em " + disciplina + ": "
                    + horas + "h " + restante + "min");

        } catch (Exception e) {
            System.out.println("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    // Relatório de exercícios resolvidos
    private void relatorioExercicios() {

        try {
            System.out.print("\nNome da disciplina: ");
            String disciplina = scanner.nextLine();

            int total = relatorioService.calcularExerciciosPorDisciplina(
                    disciplina
            );

            System.out.println("Total de exercícios resolvidos: " + total);

        } catch (Exception e) {
            System.out.println("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    // Relatório de disciplinas estudadas por dia
    private void relatorioDisciplinasPorDia() {

        try {
            System.out.print("\nData (AAAA-MM-DD): ");
            LocalDate data = LocalDate.parse(scanner.nextLine());

            relatorioService.listarDisciplinasPorDia(data);

        } catch (Exception e) {
            System.out.println("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    // Ler números inteiros
    private int lerInteiro() {

        while (!scanner.hasNextInt()) {
            System.out.print("Digite um número inteiro válido: ");
            scanner.nextLine();
        }

        int numero = scanner.nextInt();
        scanner.nextLine();

        return numero;
    }
}
