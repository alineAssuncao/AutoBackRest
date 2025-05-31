package br.rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import io.restassured.response.Response;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class olaMundoTest {

    @BeforeClass
    public static void setup(){
        RestAssured.baseURI = "https://restapi.wcaquino.me";
        RestAssured.port = 443;
        //RestAssured.basePath = "";
    }

    @Test
    public void testOlaMundo() {
        Response response = RestAssured.request(Method.GET, "/ola");
        Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
        Assert.assertTrue(response.statusCode() == 200);
        Assert.assertTrue("O Status code deve ser 200", response.statusCode() == 200);
        Assert.assertEquals(200, response.statusCode());

        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);

    }

    @Test
    public void devoConhecerOutrasFormasRestAssured(){
        Response response = RestAssured.request(Method.GET, "/ola");
        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);

        RestAssured.get("/ola").then().statusCode(200);

        //Give, then, when
        RestAssured
                .given()//Pré-condições
                .when()//Ações
                    .get("/ola")
                .then()//Verificações
                    .statusCode(200);
    }

    @Test
    public void devoConhecerMatchersHamcrest(){
        assertThat("Maria", Matchers.is("Maria"));
        assertThat(128, Matchers.is(128));
        assertThat(128, Matchers.isA(Integer.class));
        assertThat(128d, Matchers.isA(Double.class));
        assertThat(128d, Matchers.greaterThan(120d));
        assertThat(128d, Matchers.lessThan(130d));

        List<Integer> impares = Arrays.asList(1,2,3,5,8);
        assertThat(impares, hasSize(5));
        assertThat(impares, contains(1,2,3,5,8));
        assertThat(impares, containsInAnyOrder(1,5,2,3,8));
        assertThat(impares, hasItem(2));
        assertThat(impares, hasItems(2,8));

        assertThat("Maria", Matchers.is(not("Joana")));
        assertThat("Maria", Matchers.anyOf(is("Maria"), is("Ana")));
        assertThat("Joaquina", allOf(startsWith("Jo"), endsWith("na"), containsString("qui")));
    }

    @Test
    public void devoValidarBody(){
        RestAssured
                .given()
                    .log().all()
                .when()
                    .get("/ola")
                .then()
                    .statusCode(200)
                    .body(is("Ola Mundo!"))
                    .body(containsString("Mundo"))
                    .body(is(not(nullValue())));
    }
    //https://restapi.wcaquino.me/users/1

}
