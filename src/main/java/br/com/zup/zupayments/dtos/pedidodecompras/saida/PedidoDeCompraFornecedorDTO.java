package br.com.zup.zupayments.dtos.pedidodecompras.saida;

import br.com.zup.zupayments.models.Fornecedor;

public record PedidoDeCompraFornecedorDTO(
        String cnpjOuCpf,
        String razaoSocial
) {
    public static PedidoDeCompraFornecedorDTO converterModeloParaDto(Fornecedor fornecedor) {
        return new PedidoDeCompraFornecedorDTO(
                fornecedor.getCnpjOuCpf(),
                fornecedor.getRazaoSocial()
        );
    }
}
