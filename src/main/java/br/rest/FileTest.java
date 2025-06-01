package br.rest;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class FileTest {
    @Test
    public void deveObrigarEnvioArquivo(){
        RestAssured
                .given()
                    .log().all()
                .when()
                    .post("https://restapi.wcaquino.me/upload")
                .then()
                    .log().all()
                    .statusCode(404) //Deveria ser 400
                    .body("error", Matchers.is("Arquivo não enviado"))
        ;
    }

    @Test
    public void deveFazerEnvioArquivo(){
        RestAssured
                .given()
                    .log().all()
                    .multiPart("arquivo", new File("src/main/resources/users.pdf"))
                .when()
                    .post("https://restapi.wcaquino.me/upload")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("name", Matchers.is("users.pdf"))
        ;
    }

    @Test
    public void naoDeveFazerEnvioArquivoGrande(){
        RestAssured
                .given()
                    .log().all()
                    .multiPart("arquivo", new File("src/main/resources/TesteTamanho.zip"))
                .when()
                    .post("https://restapi.wcaquino.me/upload")
                .then()
                    .log().all()
                    .time(Matchers.lessThan(1000L))
                    .statusCode(413)
        ;
    }

    @Test
    public void deveBaixarArquivo() throws IOException {
        byte[] image = RestAssured
                .given()
                    .log().all()
                .when()
                    .get("https://restapi.wcaquino.me/download")
                .then()
                    //.log().all()
                    .statusCode(200)
                    .extract().asByteArray();
        ;
        File imagem = new File("src/main/resources/file.jpg");
        OutputStream out = new FileOutputStream(imagem);
        out.write(image);
        out.close();

        Assert.assertThat(imagem.length(), Matchers.lessThan(100000L));
    }
}
