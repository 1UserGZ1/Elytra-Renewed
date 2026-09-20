package com.dyxiaojiazi.elytra_renewed;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElytraRenewed implements ModInitializer {
	public static final String MOD_ID = "elytra_renewed";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Elytra Renewed 已初始化！");
		ElytraRenewedNetworking.register();
	}
}