package io.dexproject.achatservice.generic.config;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaypalConfig {

	private final MyPayPalConfig myPayPalConfig;

	public PaypalConfig(MyPayPalConfig myPayPalConfig) {
		this.myPayPalConfig = myPayPalConfig;
	}

	@Bean
	public Map<String, String> paypalSdkConfig(){
		Map<String, String> sdkConfig = new HashMap<>();
		sdkConfig.put("mode", myPayPalConfig.getPaypalMode());
		return sdkConfig;
	}
	
	@Bean
	public OAuthTokenCredential authTokenCredential() {
		return new OAuthTokenCredential(myPayPalConfig.getPaypalClientId(), myPayPalConfig.getPaypalClientSecret(), paypalSdkConfig());
	}

	@Bean
	public APIContext apiContext() throws PayPalRESTException{
		@SuppressWarnings("deprecation")	
		APIContext apiContext = new APIContext(authTokenCredential().getAccessToken());
		apiContext.setConfigurationMap(paypalSdkConfig());
		return apiContext;
	}
	
}
