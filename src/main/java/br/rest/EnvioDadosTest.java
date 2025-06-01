package br.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Test;

public class EnvioDadosTest {
    @Test
    public void deveEnviarValorViaQueryXML(){
        RestAssured
                .given()
                    .log().all()
                .when()
                    .get("https://restapi.wcaquino.me/v2/users?format=xml")
                .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(ContentType.XML)
                ;
    }

    @Test
    public void deveEnviarValorViaQueryJson(){
        RestAssured
                .given()
                    .log().all()
                .when()
                    .get("https://restapi.wcaquino.me/v2/users?format=json")
                .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
        ;
    }

    @Test
    public void deveEnviarValorViaParametroXML(){
        RestAssured
                .given()
                    .log().all()
                    .queryParam("format", "xml")
                    .queryParam("qqcoisa", "coisa") //parametros invalidos não são usados
                .when()
                    .get("https://restapi.wcaquino.me/v2/users")
                .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(ContentType.XML)
        ;
    }

    @Test
    public void deveEnviarValorViaParametroJson(){
        RestAssured
                .given()
                    .log().all()
                    .queryParam("format", "json")
                    .queryParam("qqcoisa", "coisa") //parametros invalidos não são usados
                .when()
                    .get("https://restapi.wcaquino.me/v2/users")
                .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .contentType(Matchers.containsString("utf-8"))
        ;
    }

    @Test
    public void deveEnviarValorViaHeader(){
        RestAssured
                .given()
                    .log().all()
                .when()
                    .get("https://restapi.wcaquino.me/v2/users")
                .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(ContentType.HTML)
        ;
    }

}
