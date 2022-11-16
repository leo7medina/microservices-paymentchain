package com.tio.leo.paymentchain.customer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tio.leo.paymentchain.customer.entitys.Customer;
import com.tio.leo.paymentchain.customer.entitys.CustomerProduct;
import com.tio.leo.paymentchain.customer.repository.ICustomerRepository;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private ICustomerRepository customerRepository;

//    @Autowired
//    private WebClient webClient;

    private final WebClient.Builder webClientBuilder;

    /**
     * Constructor.
     */
    public CustomerController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    HttpClient httpClient = HttpClient
            .create()
            //Connection Timeout: is a period within which a connection between a client and a server must be established
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            //Response Timeout: The maximun time we wait to receive a response after sending a request
            .responseTimeout(Duration.ofSeconds(1))
            // Read and Write Timeout: A read timeout occurs when no data was read within a certain
            //period of time, while the write timeout when a write operation cannot finish at a specific time
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });

    @GetMapping("/full")
    public Customer get(@RequestParam String code) {
        Customer customer = customerRepository.findByCode(code);
        List<CustomerProduct> products = customer.getProductList();
        products.forEach(item -> {
            String productName = getNameProduct(item.getProductId());
            item.setProductName(productName);
        });
        customer.setTransactions(getTransactions(customer.getIban()));
        return customer;
    }

    private String getNameProduct(Long id){
        WebClient webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient.wiretap(true)))
                .baseUrl("http://localhost:8082/paymentProduct")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8082/paymentProduct"))
                .build();

        JsonNode product = webClient.get()
                .uri("/product/" + id)
                .retrieve()
                .bodyToMono(JsonNode.class).block();
        return product.get("name").asText();
    }

    private <T> List<T> getTransactions(String idBan) {
        WebClient webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient.wiretap(true)))
                .baseUrl("http://localhost:8083/paymentTransaction")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8083/paymentTransaction"))
                .build();

        List<Object> listTransactions = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/transaction/transactionsByIbaAccount").queryParam("ibanAccount", idBan).build())
                .retrieve()
                .bodyToFlux(Object.class).collectList().block();
        return (List<T>) listTransactions;
    }

    @GetMapping("/all")
    public List<Customer> list() {
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable long id) {
        return customerRepository.findById(id).get();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable String id, @RequestBody Customer input) {
        return null;
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Customer input) {
        //input.getProductList().forEach(x -> x.setCustomer(input));
        return ResponseEntity.ok(customerRepository.save(input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id){
        return null;
    }

}
