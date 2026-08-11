package br.com.zup.zupayments.jwt;

import br.com.zup.zupayments.exceptions.TokenNotValidException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.security.auth.kerberos.EncryptionKey;
import java.util.Date;

@Component
public class ComponenteJWT {
    private static final Logger log = LoggerFactory.getLogger(ComponenteJWT.class);
    private final String segredo;
    private final Long milisegundos;

    public ComponenteJWT(
            @Value("${jwt.secret}")
            String segredo,
            @Value("${jwt.timeout}")
            Long milisegundos) {
        this.segredo = segredo;
        this.milisegundos = milisegundos;
    }

    public String gerarToken(String username) {
        Date vencimento = new Date(System.currentTimeMillis() + milisegundos);

        return Jwts.builder()
                .claim("sub", username)
                .expiration(vencimento)
                .signWith(new EncryptionKey(segredo.getBytes(), 1))
                .compact();
    }

    public Claims getClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .decryptWith(new EncryptionKey(segredo.getBytes(), 1))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception error) {
            log.error("Erro ao parsear token JWT", error);
            throw new TokenNotValidException(error.getMessage());
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            String username = claims.getSubject();
            Date vencimento = claims.getExpiration();
            Date agora = new Date(System.currentTimeMillis());

            return username != null && vencimento != null && agora.before(vencimento);
        } catch (TokenNotValidException tokenNotValidException) {
            log.info("Ocorreu um problema ao validar token - message: {}",
                    tokenNotValidException.getMessage());

            return false;
        }
    }
}
