package com.picktartup.wallet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfig {

    @Value("${bsc.network.url}")
    private String BSC_NETWORK_URL;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(BSC_NETWORK_URL));
    }
}
