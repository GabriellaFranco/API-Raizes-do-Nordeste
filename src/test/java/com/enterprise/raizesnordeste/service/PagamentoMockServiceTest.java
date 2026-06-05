package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.enuns.MeioPagamento;
import com.enterprise.raizesnordeste.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PagamentoMockServiceTest {

    @InjectMocks
    private PagamentoMockService pagamentoMockService;

    @Test
    @DisplayName("Deve aprovar pagamento com PIX")
    void deveAprovarPagamentoComPix() {
        assertDoesNotThrow(() ->
                pagamentoMockService.processarPagamento(MeioPagamento.PIX, new BigDecimal("50.00")));
    }

    @Test
    @DisplayName("Deve aprovar pagamento com cartão de crédito")
    void deveAprovarPagamentoComCartaoCredito() {
        assertDoesNotThrow(() ->
                pagamentoMockService.processarPagamento(MeioPagamento.CARTAO_CREDITO, new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Deve aprovar pagamento com cartão de débito")
    void deveAprovarPagamentoComCartaoDebito() {
        assertDoesNotThrow(() ->
                pagamentoMockService.processarPagamento(MeioPagamento.CARTAO_DEBITO, new BigDecimal("75.00")));
    }

    @Test
    @DisplayName("Deve aprovar pagamento com dinheiro")
    void deveAprovarPagamentoComDinheiro() {
        assertDoesNotThrow(() ->
                pagamentoMockService.processarPagamento(MeioPagamento.DINHEIRO, new BigDecimal("30.00")));
    }

    @Test
    @DisplayName("Deve aprovar pagamento com voucher")
    void deveAprovarPagamentoComVoucher() {
        assertDoesNotThrow(() ->
                pagamentoMockService.processarPagamento(MeioPagamento.VOUCHER, new BigDecimal("25.00")));
    }

    @Test
    @DisplayName("Deve aprovar pagamento no limite máximo permitido")
    void deveAprovarPagamentoNoLimiteMaximo() {
        assertDoesNotThrow(() ->
                pagamentoMockService.processarPagamento(MeioPagamento.PIX, new BigDecimal("10000.00")));
    }

    @Test
    @DisplayName("Deve recusar pagamento acima do limite")
    void deveRecusarPagamentoAcimaDoLimite() {
        assertThrows(BusinessException.class, () ->
                pagamentoMockService.processarPagamento(MeioPagamento.PIX, new BigDecimal("10000.01")));
    }

    @Test
    @DisplayName("Deve recusar pagamento com valor zero")
    void deveRecusarPagamentoComValorZero() {
        assertThrows(BusinessException.class, () ->
                pagamentoMockService.processarPagamento(MeioPagamento.PIX, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Deve recusar pagamento com valor negativo")
    void deveRecusarPagamentoComValorNegativo() {
        assertThrows(BusinessException.class, () ->
                pagamentoMockService.processarPagamento(MeioPagamento.PIX, new BigDecimal("-10.00")));
    }

    @Test
    @DisplayName("Deve recusar pagamento com valor nulo")
    void deveRecusarPagamentoComValorNulo() {
        assertThrows(BusinessException.class, () ->
                pagamentoMockService.processarPagamento(MeioPagamento.PIX, null));
    }

    @Test
    @DisplayName("Deve recusar pagamento com meio de pagamento nulo")
    void deveRecusarPagamentoComMeioPagamentoNulo() {
        assertThrows(BusinessException.class, () ->
                pagamentoMockService.processarPagamento(null, new BigDecimal("50.00")));
    }
}