package com.example.passwords;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import tools.jackson.databind.json.JsonMapper;

/**
 * A plain Java program (no web server) that shows how
 * Spring Security stores passwords and uses its helper APIs.
 */
public class PasswordStorageDemo {

    public static void main(String[] args) throws Exception {

        // ---------- 1. Password encoding ----------
        PasswordEncoder encoder =
                PasswordEncoderFactories.createDelegatingPasswordEncoder();

        String first = encoder.encode("alice123");
        String second = encoder.encode("alice123");

        System.out.println("Encoded #1 : " + first);
        System.out.println("Encoded #2 : " + second);
        System.out.println("Same text? : " + first.equals(second));
        System.out.println("matches(alice123) : "
                + encoder.matches("alice123", first));
        System.out.println("matches(wrong)    : "
                + encoder.matches("wrong", first));

        // An old password stored with a weaker encoding can still be checked
        String legacy = "{noop}alice123";
        System.out.println("legacy matches    : "
                + encoder.matches("alice123", legacy));
        System.out.println("legacy needs upgrade : "
                + encoder.upgradeEncoding(legacy));

        // BCrypt on its own (no {id} prefix), with a work factor of 12
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(12);
        System.out.println("BCrypt(12) : " + bcrypt.encode("alice123"));

        // ---------- 2. Cryptography ----------
        String salt = KeyGenerators.string().generateKey(); // random hex
        TextEncryptor encryptor =
                Encryptors.delux("my-secret-password", salt);

        String cipherText = encryptor.encrypt("4111-1111-1111-1111");
        System.out.println("Salt       : " + salt);
        System.out.println("Encrypted  : " + cipherText);
        System.out.println("Decrypted  : " + encryptor.decrypt(cipherText));

        // ---------- 3. Concurrency ----------
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        "alice", null,
                        AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(context);

        ExecutorService plainPool = Executors.newSingleThreadExecutor();
        ExecutorService securePool = new DelegatingSecurityContextExecutorService(
                Executors.newSingleThreadExecutor());

        Runnable task = () -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("  user in background thread = "
                    + (auth == null ? "null" : auth.getName()));
        };

        System.out.println("Plain executor:");
        plainPool.submit(task).get();
        System.out.println("DelegatingSecurityContextExecutorService:");
        securePool.submit(task).get();
        plainPool.shutdown();
        securePool.shutdown();

        // ---------- 4. Jackson ----------
        ClassLoader loader = PasswordStorageDemo.class.getClassLoader();
        JsonMapper mapper = JsonMapper.builder()
                .addModules(SecurityJacksonModules.getModules(loader))
                .build();

        String json = mapper.writeValueAsString(context.getAuthentication());
        System.out.println("JSON       : " + json);
        Object restored = mapper.readValue(json, Object.class);
        System.out.println("Restored   : "
                + restored.getClass().getSimpleName());

        // ---------- 5. Localization ----------
        var users = new InMemoryUserDetailsManager(
                User.withUsername("alice")
                        .password(encoder.encode("alice123"))
                        .roles("USER")
                        .build());
        var provider = new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(encoder);

        for (Locale locale : new Locale[] {
                Locale.ENGLISH, Locale.GERMAN, Locale.FRENCH }) {
            LocaleContextHolder.setLocale(locale);
            try {
                provider.authenticate(UsernamePasswordAuthenticationToken
                        .unauthenticated("alice", "wrong-password"));
            } catch (AuthenticationException ex) {
                System.out.println("Message (" + locale + ") : "
                        + ex.getMessage());
            }
        }
    }
}
