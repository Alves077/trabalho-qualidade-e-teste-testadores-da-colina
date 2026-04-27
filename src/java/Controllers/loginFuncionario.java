package Controllers;

import DAO.DaoFuncionario;
import DAO.DaoToken;
import Model.Funcionario;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.Instant;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class loginFuncionario extends HttpServlet {

    protected DaoFuncionario criarDaoFuncionario() {
        return new DaoFuncionario();
    }

    protected DaoToken criarDaoToken() {
        return new DaoToken();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = "";
        boolean resultado = false;

        if (br != null) {
            json = br.readLine();
            JSONObject dados = new JSONObject(json);

            Funcionario funcionario = new Funcionario();
            funcionario.setUsuario(dados.getString("usuario"));
            funcionario.setSenha(dados.getString("senha"));

            DaoFuncionario funcionarioDAO = criarDaoFuncionario();
            DaoToken tokenDAO = criarDaoToken();
            resultado = funcionarioDAO.login(funcionario);

            if (resultado == true) {
                Funcionario funcionarioCompleto = funcionarioDAO.pesquisaPorUsuario(funcionario);

                Cookie cookie = new Cookie("tokenFuncionario", funcionarioCompleto.getId() + "-" + Instant.now().toString());
                tokenDAO.salvar(cookie.getValue());
                cookie.setMaxAge(30 * 60);
                response.addCookie(cookie);
            }
        }
        try (PrintWriter out = response.getWriter()) {
            if (resultado == true) {
                out.println("../painel/painel.html");
            } else {
                out.println("erro");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
