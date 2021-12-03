package io.good.food.service;

import io.good.food.dto.request.CustomerInsertRequestDTO;
import io.good.food.dto.request.CustomerUpdateRequestDTO;
import io.good.food.dto.response.CustomerResponseDTO;
import io.good.food.entity.Customer;
import io.good.food.exception.BusinessException;
import io.good.food.repository.CustomerRepository;
import io.vavr.control.Option;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class CustomerService {

    private final MapperFacade mapperFacade;

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(final MapperFacade mapperFacade,
                           final CustomerRepository customerRepository) {
        this.mapperFacade = mapperFacade;
        this.customerRepository = customerRepository;
    }

    public Mono<CustomerResponseDTO> findById(final String id) {
        Option.of(id).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Id is required"));

        return this.customerRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException("Customer not found"))))
                .map(t -> this.mapperFacade.map(t, CustomerResponseDTO.class));
    }

    public Mono<CustomerResponseDTO> findBySuid(final String suid) {
        Option.of(suid).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Suid is required"));

        return this.customerRepository.findBySuid(suid)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException("Customer not found"))))
                .map(t -> this.mapperFacade.map(t, CustomerResponseDTO.class));
    }

    public Mono<CustomerResponseDTO> create(final CustomerInsertRequestDTO request) {
        this.validateInsert(request);

        final var entity = this.mapperFacade.map(request, Customer.class);
        entity.setCreationDate(LocalDateTime.now());

        return this.customerRepository.insert(entity)
                .map(t -> this.mapperFacade.map(t, CustomerResponseDTO.class));
    }

    public Mono<CustomerResponseDTO> update(final CustomerUpdateRequestDTO request) {
        this.validateUpdate(request);

        return this.customerRepository.findById(request.getId())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException("Customer not found"))))
                .flatMap(customer -> this.update(customer, request));
    }

    public Mono<Void> deleteBySuid(final String suid) {
        Option.of(suid).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Suid is required"));

        return this.customerRepository.findBySuid(suid)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException("Customer not found"))))
                .flatMap(t -> this.customerRepository.deleteById(t.getId()));
    }

    private Mono<CustomerResponseDTO> update(final Customer customer, final CustomerUpdateRequestDTO request) {
        customer.setName(request.getName());
        customer.setSuid(request.getSuid());
        customer.setSurname(request.getSurname());

        return this.customerRepository.save(customer)
                .map(t -> this.mapperFacade.map(t, CustomerResponseDTO.class));
    }

    private void validateInsert(final CustomerInsertRequestDTO request) {
        Option.of(request.getName()).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Name is required"));
        Option.of(request.getSuid()).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Suid is required"));
        Option.of(request.getSurname()).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Surname is required"));
    }

    private void validateUpdate(final CustomerUpdateRequestDTO request) {
        Option.of(request.getId()).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Id is required"));
        Option.of(request.getName()).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Name is required"));
        Option.of(request.getSuid()).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Suid is required"));
        Option.of(request.getSurname()).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Surname is required"));
    }
}
