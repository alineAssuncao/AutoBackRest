package br.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.hamcrest.xml.HasXPath;
import org.junit.Test;

import static org.hamcrest.Matchers.hasXPath;

public class HTML {
    @Test
    public void deveFazerBuscaComHTML(){
        RestAssured
                .given()
                    .log().all()
                .when()
                    .get("https://restapi.wcaquino.me/v2/users")
                .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(ContentType.HTML)
                    .body("html.body.div.table.tbody.tr.size()", Matchers.is(3))
                    .body("html.body.div.table.tbody.tr[1].td[2]", Matchers.is("25"))
                    .appendRootPath("html.body.div.table.tbody")
                    .body("tr.find{it.toString().startsWith('2')}.td[1]", Matchers.is("Maria Joaquina"))
        ;
    }

    @Test
    public void deveFazerBuscaComXpathEmHTML(){
        RestAssured
                .given()
                    .log().all()
                    .queryParam("format", "clean")
                .when()
                    .get("https://restapi.wcaquino.me/v2/users")
                .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(ContentType.HTML)
                    .body(hasXPath("count(//table/tr)", Matchers.is("4")))
                    .body(hasXPath("//td[text() = '2']/../td[2]", Matchers.is("Maria Joaquina")))
        ;
    }
}
