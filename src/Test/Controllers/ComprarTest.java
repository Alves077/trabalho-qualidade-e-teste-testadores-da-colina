package Controllers;

import DAO.DaoBebida;
import DAO.DaoCliente;
import DAO.DaoLanche;
import DAO.DaoPedido;
import Helpers.ValidadorCookie;
import Model.Bebida;
import Model.Cliente;
import Model.Lanche;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ComprarTest {

    @Mock private ValidadorCookie validadorMock;
    @Mock private DaoCliente daoClienteMock;
    @Mock private DaoLanche daoLancheMock;
    @Mock private DaoBebida daoBebidaMock;
    @Mock private DaoPedido daoPedidoMock;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    private StringWriter respostaHttp;

    private class ComprarTestavel extends comprar {
        protected ValidadorCookie criarValidadorCookie(){ return validadorMock; }
        protected DaoCliente criarDaoCliente(){ return daoClienteMock; }
        protected DaoLanche criarDaoLanche(){ return daoLancheMock; }
        protected DaoBebida criarDaoBebida(){ return daoBebidaMock; }
        protected DaoPedido criarDaoPedido(){ return daoPedidoMock; }
    }

    @Before
    public void configurar() throws Exception {
        respostaHttp = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(respostaHttp));
    }

    @Test
    public void cookieInvalidoDeveRetornarErro() throws Exception {
        Cookie[] cookies = { new Cookie("token", "x") };

        when(request.getCookies()).thenReturn(cookies);
        when(request.getInputStream()).thenReturn(new ServletInputStreamFake(""));
        when(validadorMock.validar(cookies)).thenReturn(false);

        new ComprarTestavel().processRequest(request, response);

        assertTrue(respostaHttp.toString().contains("erro"));
    }

    @Test
    public void cookieValidoDeveSalvarPedido() throws Exception {
        Cookie[] cookies = { new Cookie("token", "ok") };

        String json = "{\"id\":1}";
        when(request.getCookies()).thenReturn(cookies);
        when(request.getInputStream()).thenReturn(new ServletInputStreamFake(json));
        when(validadorMock.validar(cookies)).thenReturn(true);

        Cliente cliente = new Cliente();
        when(daoClienteMock.pesquisaPorID("1")).thenReturn(cliente);

        new ComprarTestavel().processRequest(request, response);

        assertTrue(respostaHttp.toString().contains("Pedido Salvo"));
    }
}