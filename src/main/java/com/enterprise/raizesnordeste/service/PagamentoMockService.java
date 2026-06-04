package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.enuns.MeioPagamento;
import com.enterprise.raizesnordeste.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class PagamentoMockService {

    public String processarPagamento(MeioPagamento meioPagamento, BigDecimal valor) {
        log.info("Solicitando pagamento ao serviço externo. Meio: {}, Valor: {}", meioPagamento, valor);

        validarPagamento(meioPagamento, valor);

        var transacaoId = UUID.randomUUID().toString();
        log.info("Pagamento aprovado. TransacaoId: {}", transacaoId);

        return transacaoId;
    }

    private void validarPagamento(MeioPagamento meioPagamento, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Valor do pagamento inválido");
        }

        if (meioPagamento == null) {
            throw new BusinessException("Meio de pagamento inválido");
        }

        if (valor.compareTo(new BigDecimal("10000.00")) > 0) {
            throw new BusinessException("Pagamento recusado pelo serviço externo — valor acima do limite permitido");
        }

        log.info("Comunicando com serviço externo de pagamento");
    }
}