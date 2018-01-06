package com.izeye.sample.bithumb.service;

import org.springframework.stereotype.Service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.izeye.sample.bithumb.Currency;

/**
 * Selenium-based Bithumb {@link TradingService}.
 *
 * @author Johnny Lim
 */
@Service
public class SeleniumBithumbTradingService implements TradingService {

	private static final String HOME_URL = "https://www.bithumb.com/";
	private static final String ORDER_URL = HOME_URL + "/trade/order/";

	private static final String ID_BUY_PRICE = "coinAmtCommaBuy";
	private static final String ID_BUY_AMOUNT = "coinQtyBuy";
	private static final String ID_BUY_BUTTON = "btnBuy";
	private static final String ID_SELL_PRICE = "coinAmtCommaSell";
	private static final String ID_SELL_AMOUNT = "coinQtySell";
	private static final String ID_SELL_BUTTON = "btnSell";
	private static final String ID_ORDER_TYPE_TAB = "tradeTypeTab";

	private static final String CLASS_NAME_YES_BUTTON = "_wModal_btn_yes";

	private static final String CSS_SELECTOR_TAB_SELL = "li[data-type=Sell]";

	private final ChromeDriver driver;

	public SeleniumBithumbTradingService() {
		System.setProperty("webdriver.chrome.driver", "./bin/chrome_driver/chromedriver");

		this.driver = new ChromeDriver();
		this.driver.get(HOME_URL);
	}

	@Override
	public void buy(Currency currency, int price, double amount) {
		this.driver.get(ORDER_URL + currency);

		WebElement priceField = driver.findElement(By.id(ID_BUY_PRICE));
		priceField.clear();
		priceField.sendKeys(String.valueOf(price));

		WebElement amountField = driver.findElement(By.id(ID_BUY_AMOUNT));
		amountField.sendKeys(String.valueOf(amount));

		WebElement buyButton = driver.findElement(By.id(ID_BUY_BUTTON));
		buyButton.click();

		WebElement confirmButton = driver.findElement(By.className(CLASS_NAME_YES_BUTTON));
		confirmButton.click();
	}

	@Override
	public void sell(Currency currency, int price, double amount) {
		this.driver.get(ORDER_URL + currency);

		WebElement sellTab = this.driver.findElement(By.id(ID_ORDER_TYPE_TAB)).findElement(By.cssSelector(CSS_SELECTOR_TAB_SELL));
		sellTab.click();

		WebElement priceField = driver.findElement(By.id(ID_SELL_PRICE));
		priceField.clear();
		priceField.sendKeys(String.valueOf(price));

		WebElement amountField = driver.findElement(By.id(ID_SELL_AMOUNT));
		amountField.sendKeys(String.valueOf(amount));

		WebElement buyButton = driver.findElement(By.id(ID_SELL_BUTTON));
		buyButton.click();

		WebElement confirmButton = driver.findElement(By.className(CLASS_NAME_YES_BUTTON));
		confirmButton.click();
	}

}
