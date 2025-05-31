package br.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class VerbosTest {

    @Test
    public void deveSalvarUsuario(){
        RestAssured
                .given()
                    .log().all()
                    .contentType("application/json")
                    .body("{\"name\": \"Jose\", \"age\": 50}")
                .when()
                    .post("https://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", is(notNullValue()))
                    .body("name", is("Jose"))
                    .body("age", is(50))
                ;
    }

    @Test
    public void deveSalvarUsuarioUsandoMAP(){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Usuário via MAP");
        params.put("age", 25);

        RestAssured
                .given()
                    .log().all()
                    .contentType("application/json")
                    .body(params)
                .when()
                    .post("https://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", is(notNullValue()))
                    .body("name", is("Usuário via MAP"))
                    .body("age", is(25))
        ;
    }

    @Test
    public void deveSalvarUsuarioUsandoObjeto(){
        User user = new User("Usuario via objeto", 35);

        RestAssured
                .given()
                    .log().all()
                    .contentType("application/json")
                    .body(user)
                .when()
                    .post("https://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", is(notNullValue()))
                    .body("name", is("Usuario via objeto"))
                    .body("age", is(35))
        ;
    }

    @Test
    public void deveDeserializarObjertoAoSalvarUsuario(){
        User user = new User("Usuario Deserializado", 45);

        User usuarioInserido = RestAssured
                .given()
                    .log().all()
                    .contentType("application/json")
                    .body(user)
                .when()
                    .post("https://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(201)
                    .extract().body().as(User.class)
        ;
        System.out.println(usuarioInserido);
        Assert.assertThat(usuarioInserido.getId(), notNullValue());
        Assert.assertEquals("Usuario Deserializado", usuarioInserido.getName());
        Assert.assertThat(usuarioInserido.getAge(), is(45));
    }


    @Test
    public void naoDeveSalvarUsuarioSemNome(){
        RestAssured
                .given()
                    .log().all()
                    .contentType("application/json")
                    .body("{\"age\": 50}")
                .when()
                    .post("https://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("id", is(nullValue()))
                    .body("error", is("Name é um atributo obrigatório"))
        ;
    }

    @Test
    public void deveSalvarUsuarioXML(){
        RestAssured
                .given()
                    .log().all()
                    .contentType(ContentType.XML)
                    .body("<user><name>Jose</name><age>50</age></user>")
                .when()
                    .post("https://restapi.wcaquino.me/usersXML")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("user.@id", is(notNullValue()))
                    .body("user.name", is("Jose"))
                    .body("user.age", is("50"))
        ;
    }

    @Test
    public void deveSalvarUsuarioXMLUsandoObjeto(){
        User user = new User("Usuario XML", 39);
        RestAssured
                .given()
                    .log().all()
                    .contentType(ContentType.XML)
                    .body(user)
                .when()
                    .post("https://restapi.wcaquino.me/usersXML")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("user.@id", is(notNullValue()))
                    .body("user.name", is("Usuario XML"))
                    .body("user.age", is("39"))
        ;
    }

    @Test
    public void deveDeserializarXMLAoSalvarUsuario(){
        User user = new User("Usuario XML", 39);
        User usuarioInserido = RestAssured
                .given()
                    .log().all()
                    .contentType(ContentType.XML)
                    .body(user)
                .when()
                    .post("https://restapi.wcaquino.me/usersXML")
                .then()
                    .log().all()
                    .statusCode(201)
                    .extract().body().as(User.class)
        ;
        Assert.assertThat(usuarioInserido.getId(), notNullValue());
        Assert.assertThat(usuarioInserido.getName(), is("Usuario XML"));
        Assert.assertThat(usuarioInserido.getAge(), is(39));
        Assert.assertThat(usuarioInserido.getSalary(), nullValue());
    }

    @Test
    public void deveAlterarUsuario(){
        RestAssured
                .given()
                    .log().all()
                    .contentType("application/json")
                    .body("{\"name\": \"Usuario Alterado\", \"age\": 80}")
                .when()
                    .put("https://restapi.wcaquino.me/users/1")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", is("Usuario Alterado"))
                    .body("age", is(80))
                    .body("salary", is(1234.5678f))
        ;
    }

    @Test
    public void deveCustomizarURLParte1(){
        RestAssured
                .given()
                    .log().all()
                    .contentType("application/json")
                    .body("{\"name\": \"Usuario Alterado\", \"age\": 80}")
                .when()
                    .put("https://restapi.wcaquino.me/{entidade}/{userID}", "users", "1")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", is("Usuario Alterado"))
                    .body("age", is(80))
                    .body("salary", is(1234.5678f))
        ;
    }

    @Test
    public void deveCustomizarURLParte2(){
        RestAssured
                .given()
                    .log().all()
                    .contentType("application/json")
                    .body("{\"name\": \"Usuario Alterado\", \"age\": 80}")
                    .pathParams("entidade", "users")
                    .pathParams("userID", "1")
                .when()
                    .put("https://restapi.wcaquino.me/{entidade}/{userID}")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", is("Usuario Alterado"))
                    .body("age", is(80))
                    .body("salary", is(1234.5678f))
        ;
    }

    @Test
    public void deveRemoverUsuario(){
        RestAssured
                .given()
                    .log().all()
                .when()
                    .delete("https://restapi.wcaquino.me/users/1")
                .then()
                    .log().all()
                    .statusCode(204)
        ;
    }

    @Test
    public void naoDeveRemoverUsuarioInexistente(){
        RestAssured
                .given()
                    .log().all()
                .when()
                    .delete("https://restapi.wcaquino.me/users/100000")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("error", is("Registro inexistente"))
        ;
    }
}

