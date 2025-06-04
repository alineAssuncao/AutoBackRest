package br.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import org.hamcrest.Matchers;
import org.hamcrest.xml.HasXPath;
import org.junit.Test;

import javax.swing.plaf.synth.SynthOptionPaneUI;

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

    @Test
    public void deveAcessarAplicativoWeb(){
        //login
        String cookie = RestAssured
                .given()
                    .log().all()
                    .formParam("nome", "aline@assuncao")
                    .formParam("senha", "123456")
                    .contentType(ContentType.URLENC.withCharset("UTF-8"))
                .when()
                    .post("https://seubarriga.wcaquino.me/logar")
                .then()
                    .log().all()
                    .statusCode(200)
                    .extract().header("set-cookie")
        ;

        cookie = cookie.split("=")[1].split(";")[0];
        System.out.println(cookie);

        //obter conta
        String body = RestAssured
                .given()
                    .log().all()
                    .cookie("connect.sid", cookie)
                .when()
                    .get("https://seubarriga.wcaquino.me/contas")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("html.body.table.tbody.tr[0].td[0]", Matchers.is("conta de teste Aline"))
                    .extract().body().asString()
        ;

        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, body);
        System.out.println(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));
    }
}
