package org.springframework.samples.petclinic.genai;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.samples.petclinic.genai.dto.OwnerDetails;
import org.springframework.samples.petclinic.genai.dto.PetDetails;
import org.springframework.samples.petclinic.genai.dto.PetRequest;
import org.springframework.samples.petclinic.genai.dto.Vet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.core.JsonProcessingException;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

/**
 * This class defines the tool functions that the LLM provider will invoke when it
 * requires more Information on a given topic.
 *
 * @author Oded Shopen
 * @author Antoine Rey
 */
@Configuration
class PetclinicTools {

    private static final Logger LOG = LoggerFactory.getLogger(PetclinicTools.class);

    private final AIDataProvider petclinicAiProvider;

    PetclinicTools(AIDataProvider petclinicAiProvider) {
        this.petclinicAiProvider = petclinicAiProvider;
    }

    @Bean
    public FunctionCallback listOwnersFunction() {
        return FunctionCallbackWrapper.builder(input -> {
                LOG.info("listOwners()");
                return petclinicAiProvider.getAllOwners();
            })
            .withName("listOwners")
            .withDescription("List the owners that the pet clinic has")
            .withInputType(Void.class)
            .build();
    }

    @Bean
    public FunctionCallback addOwnerFunction() {
        return FunctionCallbackWrapper.builder((OwnerRequest ownerRequest) -> {
                LOG.info("addOwnerToPetclinic() ownerRequest={}", ownerRequest);
                return petclinicAiProvider.addOwnerToPetclinic(ownerRequest);
            })
            .withName("addOwnerToPetclinic")
            .withDescription("Add a new pet owner to the pet clinic. The Owner must include a first name and a last name as two separate words, plus an address and a 10-digit phone number")
            .build();
    }

    @Bean
    public FunctionCallback listVetsFunction() {
        return FunctionCallbackWrapper.builder((Vet vetRequest) -> {
                LOG.info("listVets() vetRequest={}", vetRequest);
                try {
                    return petclinicAiProvider.getVets(vetRequest);
                } catch (JsonProcessingException e) {
                    LOG.error("Error processing JSON in the listVets function", e);
                    return List.of();
                }
            })
            .withName("listVets")
            .withDescription("List the veterinarians that the pet clinic has")
            .build();
    }

    @Bean
    public FunctionCallback addPetFunction() {
        return FunctionCallbackWrapper.builder((AddPetRequest request) -> {
                LOG.info("addPetToOwner() ownerId={} petRequest={}", request.ownerId(), request.petRequest());
                return petclinicAiProvider.addPetToOwner(request.ownerId(), request.petRequest());
            })
            .withName("addPetToOwner")
            .withDescription("Add a pet with the specified petTypeId, to an owner identified by the ownerId. The allowed Pet types IDs are only: 1 = cat, 2 = dog, 3 = lizard, 4 = snake, 5 = bird, 6 - hamster")
            .build();
    }

}

record OwnerRequest(@NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String address,
        @NotBlank String city,
        @NotBlank @Digits(fraction = 0, integer = 12) String telephone) {
}

record AddPetRequest(int ownerId, PetRequest petRequest) {
}
