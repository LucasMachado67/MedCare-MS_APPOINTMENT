package com.ms.appointment.service;


import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class MsPerfilService {


    @Autowired
    private WebClient perfilWebClient;

    /**
     * Valida se o ID do médico existe no ms_perfil.
     * @param medicId O ID do médico a ser verificado.
     * @return true se o médico for encontrado (Status 200).
     */
    public boolean validateMedicExists(long medicId){
        try{

            this.perfilWebClient.get()
                .uri("/medics/{id}", medicId)
                .retrieve()
                // Se o status for de erro, mapeamos para um Mono.error()
                .onStatus(HttpStatusCode::isError, response -> Mono.error(new WebClientResponseException(
                    response.statusCode().value(),
                    "Error while trying to connect to ms_perfil",
                    null,
                    null,
                    null
                )))
                .bodyToMono(Void.class) // Não precisamos do corpo, apenas do status HTTP.
                .block(); // Bloqueia a execução (síncrona) até que a resposta chegue.
            return true;
        }catch (WebClientResponseException e){
            // Captura o erro 404 (Not Found) se o médico não existir.
            return false;
        }catch (Exception e){
            // Tratar outros erros de comunicação (timeout, serviço fora do ar, etc.)
            System.err.println("COMUNICATION ERROR WITH MS_PERFIL: " + e.getMessage());
            return false;
        }

    }

}
