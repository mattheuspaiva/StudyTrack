package br.com.studytrack;

import br.com.studytrack.dao.SessaoEstudoDAO;
import br.com.studytrack.service.SessaoEstudoService;
import br.com.studytrack.service.RelatorioService;
import br.com.studytrack.util.MemuraiUtil;
import br.com.studytrack.view.MenuView;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        MemuraiUtil.conectar();

        Scanner leitor = new Scanner(System.in);

        try {
            SessaoEstudoDAO dao = new SessaoEstudoDAO();

            SessaoEstudoService service = new SessaoEstudoService(dao);

            RelatorioService relatorioService = new RelatorioService(dao);

            MenuView menu = new MenuView(leitor, service, relatorioService
            );

            menu.exibirMenu();

        } finally {
            leitor.close();
            MemuraiUtil.desconectar();
        }
    }
}
