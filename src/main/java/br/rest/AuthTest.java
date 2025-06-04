package br.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AuthTest {
    @Test
    public void deveAcessarSWAPI(){
        RestAssured
                .given()
                    .log().all()
                .when()
                    .get("https://swapi.dev/api/people/1")
                .then()
                    .log().all()
                    .statusCode(200)
        ;
    }

    //81b14df32edc1f5b583953dba5e2bb2d
    //https://api.openweathermap.org/data/2.5/weather?q=goiania,br&appid=81b14df32edc1f5b583953dba5e2bb2d&units=metric
    @Test
    public void deveObterClima(){
        RestAssured
                .given()
                    .log().all()
                    .queryParam("q", "goiania,br")
                    .queryParam("appid", "81b14df32edc1f5b583953dba5e2bb2d")
                    .queryParam("units", "metric")
                .when()
                    .get("https://api.openweathermap.org/data/2.5/weather")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("name", Matchers.is("Goiania"))
        ;
    }

    @Test
    public void naoDeveAcessarSemSenha(){
        RestAssured
                .given()
                    .log().all()
                .when()
                    .get("https://restapi.wcaquino.me/basicauth")
                .then()
                    .log().all()
                    .statusCode(401)
        ;
    }

    @Test
    public void deveFazerAutenticacaoBasica(){
        RestAssured
                .given()
                    .log().all()
                .when()
                    .get("https://admin:senha@restapi.wcaquino.me/basicauth")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("status", Matchers.is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoBasica2(){
        RestAssured
                .given()
                    .log().all()
                    .auth().basic("admin", "senha")
                .when()
                    .get("https://restapi.wcaquino.me/basicauth")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("status", Matchers.is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoBasicaChallenge(){
        RestAssured
                .given()
                    .log().all()
                    .auth().preemptive().basic("admin", "senha")
                .when()
                    .get("https://restapi.wcaquino.me/basicauth2")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("status", Matchers.is("logado"))
        ;
    }

    // https://seubarriga.wcaquino.me/cadastrarUsuario
    // aline@assuncao   / 123456
    // https://barrigarest.wcaquino.me/



    @Test
    public void deveFazerAutenticacaoComTokenJWT(){
        Map<String, String> login = new HashMap<>();
        login.put("email", "aline@assuncao");
        login.put("senha", "123456");

        //Login na API e pegar token
        String token = RestAssured
                .given()
                    .log().all()
                    .body(login)
                    .contentType(ContentType.JSON)
                .when()
                    .post("https://barrigarest.wcaquino.me/signin")
                .then()
                    .log().all()
                    .statusCode(200)
                    .extract().path("token")
        ;

        //Obter as contas
        RestAssured
                .given()
                    .log().all()
                .header("Authorization", "JWT " + token)
                .when()
                    .get("https://barrigarest.wcaquino.me/contas")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("nome", Matchers.hasItem("conta de teste Aline"))
        ;

    }
}
