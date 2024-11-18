package com.picktartup.wallet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

@Configuration
public class Web3jConfig {

    @Value("${bsc.network.url}")
    private String BSC_NETWORK_URL;

    @Value("${contract.token.address}")
    private String contractAddress;

    @Value("${contract.admin.private-key}")
    private String adminPrivateKey;

    @Value("${wallet.keystore.directory}")
    private String keystoreDirectory;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(BSC_NETWORK_URL));
    }

    @Bean
    public String contractAddress() {
        return contractAddress;
    }

    @Bean
    public Credentials adminCredentials() {
        return Credentials.create(adminPrivateKey);
    }

    @Bean
    public ContractGasProvider gasProvider() {
        return new DefaultGasProvider();
    }

    @Bean
    public String keystoreDirectory() {
        return keystoreDirectory;
    }
}