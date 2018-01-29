package com.izeye.sample.bithumb.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.izeye.sample.bithumb.domain.CryptoCurrency;
import com.izeye.sample.bithumb.domain.Orderbook;

/**
 * Default {@link BithumbApiService}.
 *
 * @author Johnny Lim
 */
@Service
@EnableConfigurationProperties(BithumbApiProperties.class)
public class DefaultBithumbApiService implements BithumbApiService {

	private static final String URL_API = "https://api.bithumb.com";
	private static final String URL_PUBLIC_API = URL_API + "/public";

	private static final String URL_TICKER = URL_PUBLIC_API + "/ticker/{currency}";
	private static final String URL_ORDERBOOK = URL_PUBLIC_API + "/orderbook/{currency}";
	private static final String URL_RECENT_TRANSACTIONS = URL_PUBLIC_API + "/recent_transactions/{currency}";

	private static final String URL_ACCOUNT = URL_API + "/info/account?apiKey={apiKey}&secretKey={secretKey}&currency={currency}";

	private final RestTemplate restTemplate;

	@Autowired
	private BithumbApiProperties properties;

	public DefaultBithumbApiService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Override
	public Map<String, Object> getTicker(CryptoCurrency currency) {
		return this.restTemplate.getForObject(URL_TICKER, Map.class, currency);
	}

	@Override
	public Orderbook getOrderbook(CryptoCurrency currency) {
		return this.restTemplate.getForObject(URL_ORDERBOOK, Orderbook.class, currency);
	}

	@Override
	public Map<String, Object> getRecentTransactions(CryptoCurrency currency) {
		return this.restTemplate.getForObject(URL_RECENT_TRANSACTIONS, Map.class, currency);
	}

	@Override
	public Map<String, Object> getAccount(CryptoCurrency currency) {
		return this.restTemplate.getForObject(
				URL_ACCOUNT, Map.class, this.properties.getApiKey(), this.properties.getSecretKey(), currency);
	}

}
