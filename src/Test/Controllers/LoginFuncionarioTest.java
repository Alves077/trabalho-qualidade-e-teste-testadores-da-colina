package Controllers;

import DAO.DaoFuncionario;
import DAO.DaoToken;
import Model.Funcionario;
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
public class LoginFuncionarioTest {

    @Mock private DaoFuncionario daoFuncionarioMock;
    @Mock private DaoToken daoTokenMock;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    private StringWriter respostaHttp;

    private class LoginFuncionarioTestavel extends loginFuncionario {
        @Override
        protected DaoFuncionario criarDaoFuncionario() { return daoFuncionarioMock; }
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
        configurarCorpoRequisicao("maria", "456");
        when(daoFuncionarioMock.login(any(Funcionario.class))).thenReturn(true);
        when(daoFuncionarioMock.pesquisaPorUsuario(any(Funcionario.class))).thenReturn(new Funcionario());

        new LoginFuncionarioTestavel().processRequest(request, response);

        verify(daoFuncionarioMock, times(1)).login(any(Funcionario.class));
    }

    @Test
    public void credenciaisInvalidasNaoDevemChamarPesquisaPorUsuario() throws Exception {
        configurarCorpoRequisicao("maria", "errada");
        when(daoFuncionarioMock.login(any(Funcionario.class))).thenReturn(false);

        new LoginFuncionarioTestavel().processRequest(request, response);

        verify(daoFuncionarioMock, never()).pesquisaPorUsuario(any(Funcionario.class));
    }

    @Test
    public void credenciaisInvalidasDevemRetornarErro() throws Exception {
        configurarCorpoRequisicao("maria", "errada");
        when(daoFuncionarioMock.login(any(Funcionario.class))).thenReturn(false);

        new LoginFuncionarioTestavel().processRequest(request, response);

        assertTrue(respostaHttp.toString().contains("erro"));
    }

    @Test
    public void credenciaisValidasNaoDevemRetornarErro() throws Exception {
        configurarCorpoRequisicao("maria", "456");
        when(daoFuncionarioMock.login(any(Funcionario.class))).thenReturn(true);
        when(daoFuncionarioMock.pesquisaPorUsuario(any(Funcionario.class))).thenReturn(new Funcionario());

        new LoginFuncionarioTestavel().processRequest(request, response);

        assertFalse(respostaHttp.toString().contains("erro"));
    }

    @Test
    public void credenciaisValidasDevemRetornarRedirecionamento() throws Exception {
        configurarCorpoRequisicao("maria", "456");
        when(daoFuncionarioMock.login(any(Funcionario.class))).thenReturn(true);
        when(daoFuncionarioMock.pesquisaPorUsuario(any(Funcionario.class))).thenReturn(new Funcionario());

        new LoginFuncionarioTestavel().processRequest(request, response);

        assertTrue(respostaHttp.toString().contains("painel.html"));
    }
}
