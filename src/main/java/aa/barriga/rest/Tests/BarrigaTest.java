package aa.barriga.rest.Tests;

import aa.barriga.rest.Util.DataUtil;
import aa.barriga.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import jdk.internal.org.jline.terminal.TerminalBuilder;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class BarrigaTest extends BaseTest {

    BarrigaVariaveis var = new BarrigaVariaveis();

    private static String CONTA_NAME = "CONTA " + System.nanoTime();
    private static Integer CONTA_ID;

    @BeforeClass
    public static void login(){
        Map<String, String> login = new HashMap<>();
        login.put("email", var._eMail);
        login.put("senha", var._senha);

        String TOKEN = RestAssured
                .given()
                    .body(login)
                .when()
                    .post(var._rotaSignin)
                .then()
                    .statusCode(200)
                    .extract().path("token")
                ;
        RestAssured.responseSpecification.header("Authorization", "JWT "+TOKEN);
    }

    @Test
    public void naoDeveAcessarAPISemToken(){
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");

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

        CONTA_ID = RestAssured
                .given()
                    .body(var._bodyContaNova)
                .when()
                    .post(var._rotaContas)
                .then()
                    .statusCode(201)
                    .extract().path("id")
        ;
    }

    @Test
    public void deveAlterarContaComSucesso(){
        RestAssured
                .given()
                    .body(var._bodyContaAlterada)
                    .pathParam("id", CONTA_ID)
                .when()
                    .put(var._rotaContas + CONTA_ID)
                .then()
                    .statusCode(200)
                    .body("nome", Matchers.is(var._nomeContaAlterada))
        ;
    }

    @Test
    public void naoDeveIncluirContaComMesmoNome(){
        RestAssured
                .given()
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
        mov.setData_transacao(DataUtil.getDataDiferencaDias(15));
        mov.setData_pagamento(DataUtil.getDataDiferencaDias(20));
        mov.setValor(100f);
        mov.setStatus(true);

        RestAssured
                .given()
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
        mov.setData_transacao(DataUtil.getDataDiferencaDias(0));
        mov.setData_pagamento(DataUtil.getDataDiferencaDias(0));
        mov.setValor(100f);
        mov.setStatus(true);

        RestAssured
                .given()
                .body(mov)
                .when()
                .delete(var._rotaContas)
                .then()
                .statusCode(500)
        ;
    }

    @Test
    public void deveCalcularSaldoContas(){

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
                    .body(mov)
                .when()
                    .get(var._rotaContas)
                .then()
                    .statusCode(200)
                    //Busca mais complexa, buscando unico Json dentro de um array
                    .body("find{it.conta_id == 2476387}.saldo", Matchers.is("100.00"))
        ;
    }@Test
    public void deveRemoverMovimentacao(){

        RestAssured
                .given()
                .when()
                    .delete(var._rotaTransacoes + "/115889")
                .then()
                    .statusCode(204)
        ;
    }
    
    public Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
    }
}
