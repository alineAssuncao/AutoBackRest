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
        mov.setId(var._idConta);
        //mov.setUsuarioId(???);
        mov.setDescricao("Descrição da movimentação");
        mov.setEnvolvido("Envolvido na movimentação");
        mov.setTipo("REC");
        mov.setConta_id(var._idConta);
        mov.setData_transacao("01/05/2010");
        mov.setData_pagamento("10/05/2010");
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

    @Test
    public void deveValidarCamposObrigatoriosMovimentacao(){

        RestAssured
                .given()
                    .header("Authorization", "JWT "+TOKEN)
                    .body("{}")
                .when()
                    .post(var._rotaTransacoes)
                .then()
                    .statusCode(400)
                    .body("$", Matchers.hasSize(8))
                    .body("msg", Matchers.hasItems(
                            "Data da Movimentação é obrigatório",
                            "Data do pagamento é obrigatório",
                            "Descrição é obrigatório",
                            "Interessado é obrigatório",
                            "Valor é obrigatório",
                            "Valor deve ser um número",
                            "Conta é obrigatório",
                            "Situação é obrigatório"
                    ))
        ;
    }

    @Test
    public void naoDeveIncluirMovimentacaoFutura(){

        Movimentacao mov = new Movimentacao();
        mov.setId(var._idConta);
        //mov.setUsuarioId(???);
        mov.setDescricao("Descrição da movimentação");
        mov.setEnvolvido("Envolvido na movimentação");
        mov.setTipo("REC");
        mov.setConta_id(var._idConta);
        mov.setData_transacao("01/05/2050");
        mov.setData_pagamento("10/05/2050");
        mov.setValor(100f);
        mov.setStatus(true);

        RestAssured
                .given()
                    .header("Authorization", "JWT "+TOKEN) // APIs mais novas usam "bearer " + token ou ver outra solução
                    .body(mov)
                .when()
                    .post(var._rotaTransacoes)
                .then()
                    .statusCode(400)
                    .body("$", Matchers.hasSize(1))
                    .body("msg", Matchers.hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }

    @Test
    public void naoDeveRemoverContaComMovimentacao(){

        Movimentacao mov = new Movimentacao();
        mov.setId(var._idConta);
        //mov.setUsuarioId(???);
        mov.setDescricao("Descrição da movimentação");
        mov.setEnvolvido("Envolvido na movimentação");
        mov.setTipo("REC");
        mov.setConta_id(var._idConta);
        mov.setData_transacao("01/05/2020");
        mov.setData_pagamento("10/05/2020");
        mov.setValor(100f);
        mov.setStatus(true);

        RestAssured
                .given()
                .header("Authorization", "JWT "+TOKEN) // APIs mais novas usam "bearer " + token ou ver outra solução
                .body(mov)
                .when()
                .delete(var._rotaContas)
                .then()
                .statusCode(500)
        ;
    }
}
