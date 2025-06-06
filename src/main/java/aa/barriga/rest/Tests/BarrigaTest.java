package aa.barriga.rest.Tests;

import aa.barriga.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class BarrigaTest extends BaseTest {

    BarrigaVariaveis var = new BarrigaVariaveis();
    private String TOKEN;

    @Before
    public void login(){
        Map<String, String> login = new HashMap<>();
        login.put("email", var._eMail);
        login.put("senha", var._senha);

        TOKEN = RestAssured
                .given()
                    .body(login)
                .when()
                    .post(var._rotaSignin)
                .then()
                    .statusCode(200)
                    .extract().path("token")
                ;
    }

    @Test
    public void naoDeveAcessarAPISemToken(){
        RestAssured
                .given()
                .when()
                    .get(var._rotaContas)
                .then()
                    .statusCode(401)
        ;
    }

    @Test
    public void deveIncluirContaComSucesso(){
        RestAssured
                .given()
                    .header("Authorization", "JWT "+TOKEN) // APIs mais novas usam "bearer " + token ou ver outra solução
                    .body(var._bodyContaNova)
                .when()
                    .post(var._rotaContas)
                .then()
                    .statusCode(201)
        ;
    }

    @Test
    public void deveAlterarContaComSucesso(){
        RestAssured
                .given()
                    .header("Authorization", "JWT "+TOKEN) // APIs mais novas usam "bearer " + token ou ver outra solução
                    .body(var._bodyContaAlterada)
                .when()
                    .put(var._rotaContas + var._idConta)
                .then()
                    .statusCode(200)
                    .body("nome", Matchers.is(var._nomeContaAlterada))
        ;
    }

    @Test
    public void naoDeveIncluirContaComMesmoNome(){
        RestAssured
                .given()
                    .header("Authorization", "JWT "+TOKEN) // APIs mais novas usam "bearer " + token ou ver outra solução
                    .body(var._nomeContaAlterada)
                .when()
                    .post(var._rotaContas)
                .then()
                    .statusCode(400)
                    .body("error", Matchers.is(var._ERROR_nomeRepetido))
        ;
    }

    @Test
    public void deveIncluirMovimentacaoSucesso(){

        Movimentacao mov = new Movimentacao();
        mov.setContaId(var._idConta);
        //mov.setUsuarioId(???);
        mov.setDescricao("Descrição da movimentação");
        mov.setEnvolvido("Envolvido na movimentação");
        mov.setTipo("REC");
        mov.setDataTransacao("01/05/2010");
        mov.setDataPagamento("10/05/2010");
        mov.setValor(100f);
        mov.setStatus(true);

        RestAssured
                .given()
                    .header("Authorization", "JWT "+TOKEN) // APIs mais novas usam "bearer " + token ou ver outra solução
                    .body(mov)
                .when()
                    .post(var._rotaTransacoes)
                .then()
                    .statusCode(201)
        ;
    }
}
