package Controllers;

import DAO.DaoIngrediente;
import DAO.DaoLanche;
import Helpers.ValidadorCookie;
import Model.Ingrediente;
import Model.Lanche;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SalvarLancheClienteTest {

    @Mock private ValidadorCookie validadorMock;
    @Mock private DaoIngrediente daoIngredienteMock;
    @Mock private DaoLanche daoLancheMock;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    private StringWriter respostaHttp;

    private class SalvarLancheTestavel extends salvarLancheCliente {
        protected ValidadorCookie criarValidadorCookie(){ return validadorMock; }
        protected DaoIngrediente criarDaoIngrediente(){ return daoIngredienteMock; }
        protected DaoLanche criarDaoLanche(){ return daoLancheMock; }
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

        new SalvarLancheTestavel().processRequest(request, response);

        assertTrue(respostaHttp.toString().contains("erro"));
    }

    @Test
    public void cookieValidoDeveRetornarCarrinho() throws Exception {
        Cookie[] cookies = { new Cookie("token", "ok") };

        String json = "{\"nome\":\"X\",\"descricao\":\"Teste\",\"ingredientes\":{\"Queijo\":1}}";

        when(request.getCookies()).thenReturn(cookies);
        when(request.getInputStream()).thenReturn(new ServletInputStreamFake(json));
        when(validadorMock.validar(cookies)).thenReturn(true);

        Ingrediente i = new Ingrediente();
        i.setValor_venda(5.0);

        when(daoIngredienteMock.pesquisaPorNome(org.mockito.ArgumentMatchers.any())).thenReturn(i);

        Lanche l = new Lanche();
        l.setNome("X");
        l.setValor_venda(5.0);

        when(daoLancheMock.pesquisaPorNome(org.mockito.ArgumentMatchers.any())).thenReturn(l);

        new SalvarLancheTestavel().processRequest(request, response);

        assertTrue(respostaHttp.toString().contains("carrinho"));
    }
}