package aa.barriga.rest.Tests;

import aa.barriga.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class BarrigaTest extends BaseTest {

    BarrigaVariaveis var = new BarrigaVariaveis();

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
        Map<String, String> login = new HashMap<>();
        login.put("email", var._eMail);
        login.put("senha", var._senha);

        String token = RestAssured
                .given()
                    .body(login)
                .when()
                    .post(var._rotaSignin)
                .then()
                    .statusCode(200)
                    .extract().path("token")
                ;

        RestAssured
                .given()
                    .header("Authorization", "JWT "+token) // APIs mais novas usam "bearer " + token ou ver outra solução
                    .body(var._bodyConta)
                .when()
                    .post(var._rotaContas)
                .then()
                    .statusCode(201)
        ;
    }
}
