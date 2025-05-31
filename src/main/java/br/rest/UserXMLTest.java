package br.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.Assert;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;

public class UserXMLTest {

    public static RequestSpecification reqSpec;
    public static ResponseSpecification resSpec;

    @BeforeClass
    public static void setup(){
        RestAssured.baseURI = "https://restapi.wcaquino.me";
        RestAssured.port = 443;
        //RestAssured.basePath = "";

        RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
        reqBuilder.log(LogDetail.ALL);
        reqSpec = reqBuilder.build();

        ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
        resBuilder.expectStatusCode(200);
        resSpec = resBuilder.build();

        RestAssured.requestSpecification = reqSpec;
        RestAssured.responseSpecification = resSpec;
    }

    @Test
    public void devoTrabalharComXML(){
        RestAssured
                .given()
                .when()
                    .get("/usersXML/3")
                .then()
                    .body("user.name", is("Ana Julia"))
                    .body("user.@id", is("3"))
                    .body("user.filhos.name.size()", is(2))
                    .body("user.filhos.name[0]", is("Zezinho"))
                    .body("user.filhos.name[1]", is("Luizinho"))
                    .body("user.filhos.name", hasItem("Luizinho"))
                    .body("user.filhos.name", hasItems("Zezinho", "Luizinho"))
        ;
    }

    @Test
    public void devoTrabalharComNoRaiz(){
        RestAssured
                .given()
                .when()
                    .get("/usersXML/3")
                .then()
                    .rootPath("user")
                    .body("name", is("Ana Julia"))
                    .body("@id", is("3"))

                    .rootPath("user.filhos")
                    .body("name.size()", is(2))

                    .detachRootPath("filhos")
                    .body("filhos.name[0]", is("Zezinho"))
                    .body("filhos.name[1]", is("Luizinho"))

                    .appendRootPath("filhos")
                    .body("name", hasItem("Luizinho"))
                    .body("name", hasItems("Zezinho", "Luizinho"))
        ;
    }

    @Test
    public void devoFazerPesquisasAvancadasComXML(){
        RestAssured
                .given()
                .when()
                    .get("/usersXML")
                .then()
                    .body("users.user.size()", is(3))
                    .body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
                    .body("users.user.@id", hasItems("1", "2", "3"))
                    .body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))
                    .body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
                    .body("users.user.salary.find{it != null}", is("1234.5678"))
                    .body("users.user.salary.find{it != null}.toDouble()", is(1234.5678d))
                    .body("users.user.age.collect{it.toInteger() * 2}", hasItems(40, 50, 60))
                    .body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
        ;
    }

    @Test
    public void devoFazerPesquisasAvancadasComXMLEJava(){
        String nome = RestAssured
                .given()
                .when()
                    .get("/usersXML")
                .then()
                    //Original
                    //.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
                    .extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}")
        ;
        Assert.assertEquals("Maria Joaquina".toUpperCase(), nome.toUpperCase());
    }
/*// problemas com a biblioteca NodeImpl
    @Test
    public void devoFazerPesquisasAvancadasComXMLEJavaEArray(){

        ArrayList<NodeImpl> nomes = RestAssured
                .given()
                .when()
                    .get("/usersXML")
                .then()
                    .statusCode(200)
                    //Original
                    //.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
                    .extract().path("users.user.name.findAll{it.toString().contains('n')}")
                ;
        Assert.assertEquals(2, nomes.size());
        Assert.assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
        Assert.assertEquals("Ana Julia".toUpperCase(), nomes.get(1).toString().toUpperCase());
    }
*/
    @Test
    public void devoFazerPesquisasAvancadasComXPath(){
        RestAssured
                .given()
                .when()
                    .get("/usersXML")
                .then()
                    .body(hasXPath("count(/users/user)", is("3")))
                    .body(hasXPath("/users/user[@id = '1']"))
                    .body(hasXPath("//user[@id = '2']"))
                    .body(hasXPath("//name[text() = 'Luizinho']/../../name", is("Ana Julia")))
                    .body(hasXPath("//name[text() = 'Ana Julia']/following-sibling::filhos", allOf(containsString("Zezinho"), containsString("Luizinho"))))
                    .body(hasXPath("/users/user/name", is("João da Silva")))
                    .body(hasXPath("//name", is("João da Silva")))
                    .body(hasXPath("/users/user[2]/name", is("Maria Joaquina")))
                    .body(hasXPath("/users/user[3]/name", is("Ana Julia")))
                    .body(hasXPath("/users/user[last()]/name", is("Ana Julia")))
                    .body(hasXPath("count(/users/user/name[contains(., 'n')])", is("2")))
                    .body(hasXPath("//user[age < 24]/name", is("Ana Julia")))
                    .body(hasXPath("//user[age > 20 and age < 30]/name", is("Maria Joaquina")))
                    .body(hasXPath("//user[age > 20][age < 30]/name", is("Maria Joaquina")))
        ;
    }
}
