package com.tio.leo.paymentchain.transactions.service;

import com.tio.leo.paymentchain.transactions.entities.Transaction;

import java.util.List;
import java.util.Optional;

public interface ITransactionService {

    List<Transaction> findAll();

    Optional<Transaction> findById(Long id);

    Transaction create(Transaction input) throws Exception;

    List<Transaction> findByAccount(String idAccountBan);

    List<Transaction> findByReference(String reference);
}
