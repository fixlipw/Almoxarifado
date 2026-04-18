package com.ufc.almoxarifado.rest;

import com.ufc.almoxarifado.dto.SigaaRequest;
import com.ufc.almoxarifado.dto.SigaaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SigaaRestClient {

    private final RestClient restClient;

    public SigaaRestClient(@Value("${sigaa.base}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public SigaaResponse validarCredenciais(SigaaRequest request) {
        try {
            SigaaResponse response = restClient.post()
                    .uri("/sigaa")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(SigaaResponse.class);

            if (response == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "A resposta do SIGAA veio vazia.");
            }

            return response;
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().is4xxClientError()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais do SIGAA invalidas.");
            }

            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Nao foi possível consultar o SIGAA.");
        } catch (RestClientException _) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Nao foi possível consultar o SIGAA.");
        }
    }
}
