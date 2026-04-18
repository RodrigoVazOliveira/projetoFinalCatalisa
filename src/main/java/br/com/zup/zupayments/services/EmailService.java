package br.com.zup.zupayments.services;

import br.com.zup.zupayments.models.PedidoDeCompra;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final SpringTemplateEngine springTemplateEngine;
    private final JavaMailSender javaMailSender;

    public EmailService(SpringTemplateEngine springTemplateEngine, JavaMailSender javaMailSender) {
        this.springTemplateEngine = springTemplateEngine;
        this.javaMailSender = javaMailSender;
    }

    public void enviarEmailDePedidoPendenteDeNotaFiscal(PedidoDeCompra pedidoDeCompra) throws MessagingException {
        logger.info("Iniciando envio de email para pedido de compra: {}", pedidoDeCompra.getNumeroDePedido());

        MimeMessage mensagem = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensagem,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        String emailDestino = pedidoDeCompra.getResponsavel().getEmail();
        helper.setTo(emailDestino);
        logger.debug("Email destinado para: {}", emailDestino);

        String assunto = "URGENTE: Nota fiscal pendente - pedido " + pedidoDeCompra.getNumeroDePedido();
        helper.setSubject(assunto);
        logger.debug("Assunto do email: {}", assunto);

        helper.setFrom("staff@zup.com.br");

        Context context = new Context();
        context.setVariable("nomeResponsavel", pedidoDeCompra.getResponsavel().getNomeCompleto());
        context.setVariable("numeroDoPedido", pedidoDeCompra.getNumeroDePedido());
        context.setVariable("cnpjOucpf", pedidoDeCompra.getFornecedor().getCnpjOuCpf());
        context.setVariable("razaoSocial", pedidoDeCompra.getFornecedor().getRazaoSocial());
        logger.debug("Contexto de email preparado para o responsável: {}", pedidoDeCompra.getResponsavel().getNomeCompleto());

        String emailHtml = springTemplateEngine.process("mail", context);
        helper.setText(emailHtml, true);

        try {
            javaMailSender.send(mensagem);
            logger.info("Email enviado com sucesso para pedido de compra: {}", pedidoDeCompra.getNumeroDePedido());
        } catch (Exception e) {
            logger.error("Erro ao enviar email para pedido de compra: {}", pedidoDeCompra.getNumeroDePedido(), e);
            throw e;
        }
    }
}
