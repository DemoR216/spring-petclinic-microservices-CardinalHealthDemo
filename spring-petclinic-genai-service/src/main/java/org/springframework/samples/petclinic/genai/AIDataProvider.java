package org.springframework.samples.petclinic.genai;

import java.net.URI;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.samples.petclinic.genai.dto.OwnerDetails;
import org.springframework.samples.petclinic.genai.dto.PetDetails;
import org.springframework.samples.petclinic.genai.dto.PetRequest;
import org.springframework.samples.petclinic.genai.dto.Vet;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Functions that are invoked by the LLM will use this bean to query the system of record
 * for information such as listing owners and vets, or adding pets to an owner.
 *
 * @author Oded Shopen
 */
@Service
public class AIDataProvider {

	private final VectorStore vectorStore;

    private final RestTemplate restTemplate;

    private final DiscoveryClient discoveryClient;

	public AIDataProvider(VectorStore vectorStore, DiscoveryClient discoveryClient) {
        this.restTemplate = new RestTemplate();
        this.vectorStore = vectorStore;
        this.discoveryClient = discoveryClient;
    }

	public List<OwnerDetails> getAllOwners() {
        ResponseEntity<List<OwnerDetails>> response = restTemplate.exchange(
            getCustomerServiceUri() + "/owners",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<OwnerDetails>>() {}
        );
        return response.getBody();
	}

    public List<String> getVets(Vet vetRequest) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String vetAsJson = objectMapper.writeValueAsString(vetRequest);

        int topK = 20;
        if (vetRequest == null) {
            topK = 50;
        }
        SearchRequest sr = SearchRequest.builder()
            .query(vetAsJson)
            .topK(topK)
            .build();


		List<Document> topMatches = this.vectorStore.similaritySearch(sr);
		return topMatches.stream().map(Document::getFormattedContent).toList();
	}

	public PetDetails addPetToOwner(int ownerId, PetRequest petRequest) {
        return restTemplate.postForObject(
            getCustomerServiceUri() + "/owners/" + ownerId + "/pets",
            petRequest,
            PetDetails.class
        );
	}

	public OwnerDetails addOwnerToPetclinic(OwnerRequest ownerRequest) {
        return restTemplate.postForObject(
            getCustomerServiceUri() + "/owners",
            ownerRequest,
            OwnerDetails.class
        );
	}

    @NonNull
    private URI getCustomerServiceUri() {
        return discoveryClient.getInstances("customers-service").get(0).getUri();
    }

}
