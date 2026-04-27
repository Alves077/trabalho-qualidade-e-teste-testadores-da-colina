package Controllers;

import DAO.DaoCliente;
import DAO.DaoToken;
import Model.Cliente;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class LoginTest {

    @Mock private DaoCliente daoClienteMock;
    @Mock private DaoToken daoTokenMock;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    private StringWriter respostaHttp;

    private class LoginTestavel extends login {
        @Override
        protected DaoCliente criarDaoCliente() { return daoClienteMock; }
        @Override
        protected DaoToken criarDaoToken() { return daoTokenMock; }
    }

    @Before
    public void configurar() throws Exception {
        respostaHttp = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(respostaHttp));
    }

    private void configurarCorpoRequisicao(String usuario, String senha) throws Exception {
        String json = "{\"usuario\":\"" + usuario + "\",\"senha\":\"" + senha + "\"}";
        when(request.getInputStream()).thenReturn(new ByteArrayInputStream(json.getBytes()) {
            public int read(byte[] b, int off, int len) { return super.read(b, off, len); }
        });
    }

    @Test
    public void credenciaisValidasDevemChamarLoginUmaVez() throws Exception {
        configurarCorpoRequisicao("joao", "123");
        when(daoClienteMock.login(any(Cliente.class))).thenReturn(true);
        when(daoClienteMock.pesquisaPorUsuario(any(Cliente.class))).thenReturn(new Cliente());

        new LoginTestavel().processRequest(request, response);

        verify(daoClienteMock, times(1)).login(any(Cliente.class));
    }

    @Test
    public void credenciaisInvalidasNaoDevemChamarPesquisaPorUsuario() throws Exception {
        configurarCorpoRequisicao("joao", "errada");
        when(daoClienteMock.login(any(Cliente.class))).thenReturn(false);

        new LoginTestavel().processRequest(request, response);

        verify(daoClienteMock, never()).pesquisaPorUsuario(any(Cliente.class));
    }

    @Test
    public void credenciaisInvalidasDevemRetornarErro() throws Exception {
        configurarCorpoRequisicao("joao", "errada");
        when(daoClienteMock.login(any(Cliente.class))).thenReturn(false);

        new LoginTestavel().processRequest(request, response);

        assertTrue(respostaHttp.toString().contains("erro"));
    }

    @Test
    public void credenciaisValidasNaoDevemRetornarErro() throws Exception {
        configurarCorpoRequisicao("joao", "123");
        when(daoClienteMock.login(any(Cliente.class))).thenReturn(true);
        when(daoClienteMock.pesquisaPorUsuario(any(Cliente.class))).thenReturn(new Cliente());

        new LoginTestavel().processRequest(request, response);

        assertFalse(respostaHttp.toString().contains("erro"));
    }

    @Test
    public void credenciaisValidasDevemRetornarRedirecionamento() throws Exception {
        configurarCorpoRequisicao("joao", "123");
        when(daoClienteMock.login(any(Cliente.class))).thenReturn(true);
        when(daoClienteMock.pesquisaPorUsuario(any(Cliente.class))).thenReturn(new Cliente());

        new LoginTestavel().processRequest(request, response);

        assertTrue(respostaHttp.toString().contains("carrinho.html"));
    }
}
